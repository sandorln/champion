package com.sandorln.game.ui.initialquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.model.data.item.ItemData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class InitialQuizViewModel @Inject constructor(

) : ViewModel() {
    private val _gameTimeMutex = Mutex()
    private val _gameTime = MutableStateFlow(60f)
    val gameTime = _gameTime.asStateFlow()

    private val _uiMutex = Mutex()
    private val _uiState = MutableStateFlow(InitialQuizUiState())
    val uiState = _uiState.asStateFlow()

    private val _action = MutableSharedFlow<InitialQuizAction>()
    fun sendAction(initialQuizAction: InitialQuizAction) {
        viewModelScope.launch {
            _action.emit(initialQuizAction)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                while (true) {
                    delay(10)
                    _gameTimeMutex.withLock {
                        _gameTime.update { max(it - 0.01f, 0f) }
                    }

                    if (_gameTime.value <= 0) break
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

                                }
                            }
                        }
                    }
            }
        }
    }
}

sealed interface InitialQuizAction {
    data object InitialQuizDone : InitialQuizAction

    data class ChangeAnswer(val text: String) : InitialQuizAction
}

data class InitialQuizUiState(
    val itemData: ItemData = dummyItem,
    val inputAnswer: String = "",
)