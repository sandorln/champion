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
import kotlin.math.min
import kotlin.random.Random

@HiltViewModel
class InitialQuizViewModel @Inject constructor(
    private val getInitialQuizItemListByVersion: GetInitialQuizItemListByVersion
) : ViewModel() {
    private val _previousItemStack: Stack<ItemData> = Stack()
    private val _nextItemSetStack: Stack<ItemData> = Stack()

    private val _gameTimeMutex = Mutex()
    private val _gameTime = MutableStateFlow(60f)
    val gameTime = _gameTime.asStateFlow()

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
        gameJob?.cancel()
        gameJob = viewModelScope.launch {
            while (true) {
                delay(10)
                _gameTimeMutex.withLock {
                    _gameTime.update { max(it - 0.01f, 0f) }
                }

                if (_gameTime.value <= 0) break
            }
        }
    }

    private var nextRoundJob: Job? = null
    private fun nextRound() {
        if (nextRoundJob?.isActive == true) return

        viewModelScope.launch(Dispatchers.IO) {
            _gameTimeMutex.withLock {
                _gameTime.update { min(it + 5f, 60f) }
            }

            _uiMutex.withLock {
                _uiState.update {
                    _previousItemStack.push(it.itemData)
                    it.copy(itemData = _nextItemSetStack.pop())
                }
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                getInitialQuizItemListByVersion
                    .invoke("14.5.1")
                    .onSuccess { summonerItemList ->
                        while (_nextItemSetStack.count() < 10) {
                            val randomItem = summonerItemList.random(Random(System.currentTimeMillis()))
                            if (!_nextItemSetStack.contains(randomItem))
                                _nextItemSetStack.add(randomItem)
                        }

                        _uiMutex.withLock {
                            _uiState.update {
                                it.copy(itemData = _nextItemSetStack.pop())
                            }
                        }

                        startGame()
                    }
            }

            launch {
                _action
                    .collect { action ->
                        _uiMutex.withLock {
                            when (action) {
                                is InitialQuizAction.ChangeAnswer -> {
                                    _uiState.update { it.copy(inputAnswer = action.text) }
                                }

                                InitialQuizAction.InitialQuizDone -> {
                                    val itemData = _uiState.value.itemData
                                    val answer = _uiState.value.inputAnswer

                                    val toastMessage: InitialQuizSideEffect
                                    if (itemData.name.replace(" ", "") == answer.replace(" ", "")) {
                                        nextRound()
                                        toastMessage = InitialQuizSideEffect.ShowToastMessage(
                                            BaseToastType.OKAY,
                                            "정답입니다"
                                        )
                                    } else {
                                        toastMessage = InitialQuizSideEffect.ShowToastMessage(
                                            BaseToastType.WARNING,
                                            "틀렸습니다"
                                        )

                                    }

                                    _sideEffect.emit(toastMessage)
                                    _uiState.update { it.copy(inputAnswer = "") }
                                }
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

    data class ChangeAnswer(val text: String) : InitialQuizAction
}

data class InitialQuizUiState(
    val itemData: ItemData = ItemData(),
    val inputAnswer: String = "",
)