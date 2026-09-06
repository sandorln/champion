package com.sandorln.game.ui.recipequiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.item.GetItemRecipeQuizRoundList
import com.sandorln.domain.usecase.version.GetCurrentVersion
import com.sandorln.game.ui.initialquiz.ChainType
import com.sandorln.model.data.game.ItemRecipeQuizRound
import com.sandorln.model.data.item.ItemData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Stack
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class ItemRecipeQuizViewModel @Inject constructor(
    private val getItemRecipeQuizRoundList: GetItemRecipeQuizRoundList,
    currentVersion: GetCurrentVersion
) : ViewModel() {
    companion object {
        const val INIT_READY_TIME = 3f
        const val INIT_GAME_TIME = 60f
        const val INIT_VERSION_NAME = "14.13.1"
    }

    val totalRoundCount: Int = 10
    private val defaultPlusScore: Int = 500
    private val remainingTimePlusScore: Int = 500

    private val _gameTimeMutex = Mutex()
    private val _gameTime = MutableStateFlow(INIT_GAME_TIME)
    val gameTime = _gameTime.asStateFlow()

    private val _readyTime = MutableStateFlow(INIT_READY_TIME)
    val readyTime = _readyTime.asStateFlow()

    private val _uiMutex = Mutex()
    private val _uiState = MutableStateFlow(ItemRecipeQuizUiState())
    val uiState = _uiState.asStateFlow()

    private val _roundStack: Stack<ItemRecipeQuizRound> = Stack()
    private val _previousRoundList: MutableList<RecipeRoundResult> = mutableListOf()
    val previousRoundList: List<RecipeRoundResult> get() = _previousRoundList.toList()
    val previousAnswerList: List<Boolean> get() = _previousRoundList.map { it.chainType != ChainType.FAIL }

    private val _action = MutableSharedFlow<ItemRecipeQuizAction>()
    fun sendAction(action: ItemRecipeQuizAction) {
        viewModelScope.launch {
            _action.emit(action)
        }
    }

    private var gameJob: Job? = null
    private fun startGame() {
        if (gameJob?.isActive == true) return

        gameJob = viewModelScope.launch {
            _readyTime.emit(INIT_READY_TIME)
            _gameTime.emit(INIT_GAME_TIME)

            while (true) {
                val readyTime = _readyTime.value
                if (readyTime <= 0) break
                delay(10)
                _readyTime.update { readyTime - 0.01f }
            }

            while (true) {
                delay(10)
                _gameTimeMutex.withLock {
                    _gameTime.update { max(it - 0.01f, 0f) }
                }

                if (_gameTime.value <= 0) {
                    _uiMutex.withLock {
                        _uiState.update {
                            it.copy(
                                isGameEnd = true
                            )
                        }
                    }
                    break
                }
            }
        }
    }

    private var roundStartTime: Long = 0
    private fun nextRound(isAnswer: Boolean) {
        val nowDate = System.currentTimeMillis()

        viewModelScope.launch(Dispatchers.IO) {
            _uiMutex.withLock {
                val diffTime = nowDate - roundStartTime
                val chainType = if (isAnswer) ChainType.getChainType(diffTime) else ChainType.FAIL

                if (isAnswer) {
                    val plusScore = when (chainType) {
                        ChainType.GREAT -> defaultPlusScore * 5
                        ChainType.GOOD -> defaultPlusScore * 3
                        ChainType.NICE -> defaultPlusScore * 2
                        ChainType.NORMAL -> defaultPlusScore
                        ChainType.FAIL -> 0
                    }
                    _uiState.update { it.copy(score = it.score + plusScore) }
                }

                val currentRound = _uiState.value.currentRound
                _previousRoundList.add(
                    RecipeRoundResult(
                        chainType = chainType,
                        targetItem = currentRound.targetItem,
                        isCorrect = isAnswer,
                        userCart = _uiState.value.userCart,
                        answerLeaves = currentRound.requiredLeafItems
                    )
                )

                runCatching { _roundStack.pop() }
                    .onSuccess { nextRoundData ->
                        _uiState.update {
                            it.copy(
                                currentRound = nextRoundData,
                                currentRoundIndex = it.currentRoundIndex + 1,
                                userCart = emptyMap(),
                                lastFeedbackMessage = if (isAnswer) "조합 성공!" else "조합 실패!",
                                isLastAnswerCorrect = isAnswer
                            )
                        }
                    }.onFailure {
                        gameJob?.cancel()
                        val answerPer = previousAnswerList.count { it }.toFloat() / totalRoundCount.toFloat()
                        val remainingTime = _gameTime.value
                        val score = _uiState.value.score
                        val finalScore = (score + remainingTime * remainingTimePlusScore * answerPer).toLong()

                        _uiState.update {
                            it.copy(
                                score = finalScore,
                                isGameEnd = true,
                                userCart = emptyMap()
                            )
                        }
                    }
            }

            roundStartTime = nowDate
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                val latestVersion = currentVersion.invoke().firstOrNull()?.name ?: INIT_VERSION_NAME

                getItemRecipeQuizRoundList
                    .invoke(version = latestVersion, roundCount = totalRoundCount)
                    .onSuccess { rounds ->
                        if (rounds.isEmpty()) return@onSuccess

                        _roundStack.clear()
                        // Stack은 pop() 시 역순으로 나오므로 반대로 push
                        rounds.reversed().forEach { _roundStack.push(it) }

                        val firstRound = _roundStack.pop()
                        _uiMutex.withLock {
                            _uiState.update {
                                it.copy(
                                    currentRound = firstRound,
                                    currentRoundIndex = 1,
                                    totalRoundCount = rounds.size,
                                    userCart = emptyMap()
                                )
                            }
                        }

                        roundStartTime = System.currentTimeMillis()
                        startGame()
                    }
            }

            launch {
                _action.collect { action ->
                    when (action) {
                        is ItemRecipeQuizAction.AddLeafItem -> {
                            _uiState.update { state ->
                                val currentCount = state.userCart[action.item] ?: 0
                                state.copy(
                                    userCart = state.userCart + (action.item to currentCount + 1),
                                    lastFeedbackMessage = ""
                                )
                            }
                        }

                        is ItemRecipeQuizAction.RemoveLeafItem -> {
                            _uiState.update { state ->
                                val currentCount = state.userCart[action.item] ?: 0
                                val updatedMap = if (currentCount <= 1) {
                                    state.userCart - action.item
                                } else {
                                    state.userCart + (action.item to currentCount - 1)
                                }
                                state.copy(
                                    userCart = updatedMap,
                                    lastFeedbackMessage = ""
                                )
                            }
                        }

                        ItemRecipeQuizAction.ClearCart -> {
                            _uiState.update { it.copy(userCart = emptyMap(), lastFeedbackMessage = "") }
                        }

                        ItemRecipeQuizAction.SubmitCraft -> {
                            if (gameJob?.isCompleted == true) return@collect
                            val state = _uiState.value
                            val requiredMap = state.currentRound.requiredLeafItems
                            val userCart = state.userCart

                            val isAnswer = requiredMap.size == userCart.size &&
                                    requiredMap.all { (item, count) ->
                                        userCart[item] == count
                                    }

                            nextRound(isAnswer)
                        }

                        ItemRecipeQuizAction.CloseGameDialog -> {
                            _uiState.update { it.copy(isGameEnd = false) }
                        }
                    }
                }
            }
        }
    }
}

sealed interface ItemRecipeQuizAction {
    data class AddLeafItem(val item: ItemData) : ItemRecipeQuizAction
    data class RemoveLeafItem(val item: ItemData) : ItemRecipeQuizAction
    data object ClearCart : ItemRecipeQuizAction
    data object SubmitCraft : ItemRecipeQuizAction
    data object CloseGameDialog : ItemRecipeQuizAction
}

data class ItemRecipeQuizUiState(
    val score: Long = 0,
    val currentRound: ItemRecipeQuizRound = ItemRecipeQuizRound(),
    val currentRoundIndex: Int = 1,
    val totalRoundCount: Int = 10,
    val userCart: Map<ItemData, Int> = emptyMap(),
    val isGameEnd: Boolean = false,
    val lastFeedbackMessage: String = "",
    val isLastAnswerCorrect: Boolean? = null
)

data class RecipeRoundResult(
    val chainType: ChainType,
    val targetItem: ItemData,
    val isCorrect: Boolean,
    val userCart: Map<ItemData, Int>,
    val answerLeaves: Map<ItemData, Int>
)
