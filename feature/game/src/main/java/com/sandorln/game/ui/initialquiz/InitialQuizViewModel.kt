package com.sandorln.game.ui.initialquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.design.component.toast.BaseToastType
import com.sandorln.domain.usecase.item.GetInitialQuizItemListByVersion
import com.sandorln.model.data.item.ItemData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val getInitialQuizItemListByVersion: GetInitialQuizItemListByVersion
) : ViewModel() {
    val totalRoundCount: Int = 5
    private val defaultPlusScore: Int = 500
    private val _previousItemStack: Stack<Triple<Boolean, ItemData, String>> = Stack()
    val previousAnswerList: List<Boolean> get() = _previousItemStack.map { it.first }
    val previousItemList: List<Triple<Boolean, ItemData, String>> get() = _previousItemStack.toList()
    private val _nextItemSetStack: Stack<ItemData> = Stack()

    private val _gameTimeMutex = Mutex()
    private val _gameTime = MutableStateFlow(60f)
    val gameTime = _gameTime.asStateFlow()

    private val _uiMutex = Mutex()
    private val _uiState = MutableStateFlow(InitialQuizUiState())
    val uiState = _uiState.asStateFlow()

    private val _inputAnswer = MutableStateFlow("")
    val inputAnswer = _inputAnswer.asStateFlow()

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
        gameJob?.cancel()
        gameJob = viewModelScope.launch {
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
            _inputAnswer.update { "" }

            _uiMutex.withLock {
                if (isAnswer) {
                    val diffTime = nowDate - chainCountDate
                    val chainType = ChainType.getChainType(diffTime)
                    val plusScore = when (chainType) {
                        ChainType.GREAT -> defaultPlusScore * 5
                        ChainType.EXCELLENT -> defaultPlusScore * 3
                        ChainType.NICE -> defaultPlusScore * 2
                        ChainType.NORMAL -> defaultPlusScore
                    }

                    _uiState.update { it.copy(score = it.score + plusScore) }
                }

                _previousItemStack.push(Triple(isAnswer, preItemData, preAnswer))

                runCatching { _nextItemSetStack.pop() }
                    .onSuccess { nextItemData ->
                        _uiState.update {
                            it.copy(
                                itemData = nextItemData
                            )
                        }
                    }.onFailure {
                        gameJob?.cancel()
                        _uiState.update {
                            it.copy(
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
                getInitialQuizItemListByVersion
                    .invoke("14.11.1")
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
                            is InitialQuizAction.ChangeAnswer -> {
                                _inputAnswer.update { action.text }
                            }

                            InitialQuizAction.InitialQuizDone -> {
                                if (gameJob?.isCompleted == true) return@collect

                                val itemData = _uiState.value.itemData
                                val answer = _inputAnswer.value

                                val toastMessage: InitialQuizSideEffect
                                val isAnswer = itemData.name.replace(" ", "") == answer.replace(" ", "")

                                nextRound(isAnswer, itemData, answer)

                                toastMessage = if (isAnswer) {
                                    InitialQuizSideEffect.ShowToastMessage(BaseToastType.OKAY, "정답입니다")
                                } else {
                                    InitialQuizSideEffect.ShowToastMessage(BaseToastType.WARNING, "틀렸습니다")
                                }

                                _sideEffect.emit(toastMessage)
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
    data class ShowToastMessage(
        val messageType: BaseToastType,
        val message: String
    ) : InitialQuizSideEffect
}

sealed interface InitialQuizAction {
    data object InitialQuizDone : InitialQuizAction
    data object CloseGameDialog : InitialQuizAction

    data class ChangeAnswer(val text: String) : InitialQuizAction
}

data class InitialQuizUiState(
    val score: Int = 0,
    val itemData: ItemData = ItemData(),
    val isGameEnd: Boolean = false
)

enum class ChainType(val time: Long) {
    GREAT(3000),
    EXCELLENT(5000),
    NICE(7000),
    NORMAL(Long.MAX_VALUE);

    companion object {
        fun getChainType(diffTime: Long) = entries.firstOrNull { it.time > diffTime } ?: NORMAL
    }
}