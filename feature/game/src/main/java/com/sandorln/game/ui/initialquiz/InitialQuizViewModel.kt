package com.sandorln.game.ui.initialquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.design.component.toast.BaseToastType
import com.sandorln.domain.usecase.game.UpdateLocalMaxGameScore
import com.sandorln.domain.usecase.item.GetInitialQuizItemListByVersion
import com.sandorln.domain.usecase.version.GetAllVersionList
import com.sandorln.model.data.item.ItemData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Stack
import javax.inject.Inject
import kotlin.math.max
import kotlin.random.Random

@HiltViewModel
class InitialQuizViewModel @Inject constructor(
    private val getInitialQuizItemListByVersion: GetInitialQuizItemListByVersion,
    private val updateLocalMaxGameScore: UpdateLocalMaxGameScore,
    getAllVersionList: GetAllVersionList
) : ViewModel() {
    companion object {
        const val INIT_READY_TIME = 3f
        const val INIT_GAME_TIME = 60f
    }

    val totalRoundCount: Int = 10
    private val defaultPlusScore: Int = 500
    private val remainingTimePlusScore: Int = 500

    private val _previousItemStack: Stack<Triple<ChainType, ItemData, String>> = Stack()
    val previousAnswerList: List<Boolean> get() = _previousItemStack.map { it.first != ChainType.FAIL }
    val previousItemList: List<Triple<ChainType, ItemData, String>> get() = _previousItemStack.toList()
    private val _nextItemSetStack: Stack<ItemData> = Stack()

    private val _gameTimeMutex = Mutex()
    private val _gameTime = MutableStateFlow(INIT_GAME_TIME)
    val gameTime = _gameTime.asStateFlow()
    private val _readyTime = MutableStateFlow(INIT_READY_TIME)
    val readyTime = _readyTime.asStateFlow()

    private val _uiMutex = Mutex()
    private val _uiState = MutableStateFlow(InitialQuizUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<InitialQuizSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private val _action = MutableSharedFlow<InitialQuizAction>()
    fun sendAction(initialQuizAction: InitialQuizAction) {
        viewModelScope.launch {
            _action.emit(initialQuizAction)
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
                                itemData = ItemData(),
                                isGameEnd = true
                            )
                        }
                    }
                    break
                }
            }
        }
    }

    private var nextRoundJob: Job? = null
    private var chainCountDate: Long = 0
    private fun nextRound(
        isAnswer: Boolean,
        preItemData: ItemData,
        preAnswer: String
    ) {
        if (nextRoundJob?.isActive == true) return
        val nowDate = System.currentTimeMillis()

        viewModelScope.launch(Dispatchers.IO) {
            _uiMutex.withLock {
                val diffTime = nowDate - chainCountDate
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

                _previousItemStack.push(Triple(chainType, preItemData, preAnswer))

                runCatching { _nextItemSetStack.pop() }
                    .onSuccess { nextItemData ->
                        _uiState.update {
                            it.copy(
                                itemData = nextItemData
                            )
                        }
                    }.onFailure {
                        gameJob?.cancel()
                        val answerPer = previousAnswerList.count { it }.toFloat() / totalRoundCount.toFloat()
                        val remainingTime = _gameTime.value
                        val score = _uiState.value.score
                        val finalScore = (score + remainingTime * remainingTimePlusScore * answerPer).toLong()

                        updateLocalMaxGameScore.invoke(finalScore)
                        _uiState.update {
                            it.copy(
                                score = finalScore,
                                itemData = ItemData(),
                                isGameEnd = true
                            )
                        }
                    }
            }

            chainCountDate = nowDate
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                val latestVersion = getAllVersionList.invoke().firstOrNull()?.firstOrNull()?.name ?: "14.11.1"

                getInitialQuizItemListByVersion
                    .invoke(latestVersion)
                    .onSuccess { summonerItemList ->
                        val randomSeed = Random(System.currentTimeMillis())

                        while (_nextItemSetStack.count() < totalRoundCount) {
                            val randomItem = summonerItemList.random(randomSeed)
                            if (!_nextItemSetStack.contains(randomItem))
                                _nextItemSetStack.add(randomItem)
                        }

                        _uiMutex.withLock {
                            _uiState.update {
                                it.copy(itemData = _nextItemSetStack.pop())
                            }
                        }

                        chainCountDate = System.currentTimeMillis()
                        startGame()
                    }
            }

            launch {
                _action
                    .collect { action ->
                        when (action) {
                            is InitialQuizAction.InitialQuizDone -> {
                                val answer = action.answer
                                if (gameJob?.isCompleted == true || answer.isEmpty())
                                    return@collect

                                val itemData = _uiState.value.itemData
                                val isAnswer = itemData.name.replace(" ", "") == answer.replace(" ", "")
                                nextRound(isAnswer, itemData, answer)
                            }

                            InitialQuizAction.CloseGameDialog -> {
                                _uiState.update { it.copy(isGameEnd = false) }
                            }
                        }
                    }
            }
        }
    }
}

sealed interface InitialQuizSideEffect {
    @Deprecated("게임 중 TOAST 띄우면 입력메세지 밀리는 현상 발생")
    data class ShowToastMessage(val messageType: BaseToastType, val message: String) : InitialQuizSideEffect
}

sealed interface InitialQuizAction {
    data class InitialQuizDone(val answer: String) : InitialQuizAction
    data object CloseGameDialog : InitialQuizAction
}

data class InitialQuizUiState(
    val score: Long = 0,
    val itemData: ItemData = ItemData(),
    val isGameEnd: Boolean = false
)

enum class ChainType(val time: Long) {
    GREAT(3000),
    GOOD(5000),
    NICE(7000),
    NORMAL(Long.MAX_VALUE),
    FAIL(Long.MIN_VALUE);

    companion object {
        fun getChainType(diffTime: Long) = entries.firstOrNull { it.time > diffTime } ?: FAIL
    }
}