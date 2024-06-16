package com.sandorln.setting.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.game.GetGameRank
import com.sandorln.domain.usecase.game.GetInitialGameScore
import com.sandorln.domain.usecase.game.GetRefreshRankGameNextTime
import com.sandorln.domain.usecase.game.RefreshGameRank
import com.sandorln.model.type.GameType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class SettingHomeViewModel @Inject constructor(
    getInitialGameScore: GetInitialGameScore,
    getRefreshRankGameNextTime: GetRefreshRankGameNextTime,
    getGameRank: GetGameRank,
    private val refreshGameRank: RefreshGameRank
) : ViewModel() {
    val initialGameRank = getGameRank.invoke()
        .map { it[GameType.Initial] ?: 31 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 31)

    private val _remainingRankRefreshTime: MutableStateFlow<Long> = MutableStateFlow(0)
    val remainingRankRefreshTime = _remainingRankRefreshTime.asStateFlow()

    private var refreshJob: Job? = null
    fun refreshGameRank() {
        val remainingTime = _remainingRankRefreshTime.value
        if (refreshJob?.isActive == true || remainingTime > 0) return

        refreshJob = viewModelScope.launch(Dispatchers.IO) {
            refreshGameRank.invoke()
        }
    }

    val initialGameScore = getInitialGameScore.invoke().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                getRefreshRankGameNextTime
                    .invoke()
                    .collect { refreshNextTime ->
                        var currentTime = Calendar
                            .getInstance(TimeZone.getTimeZone("UTC"))
                            .timeInMillis

                        _remainingRankRefreshTime.emit((refreshNextTime - currentTime) / 1000)

                        while (true) {
                            currentTime = Calendar
                                .getInstance(TimeZone.getTimeZone("UTC"))
                                .timeInMillis

                            val diffTime = refreshNextTime - currentTime

                            _remainingRankRefreshTime.emit(diffTime / 1000)
                            if (currentTime > refreshNextTime) return@collect

                            delay(1000)
                        }
                    }
            }
        }
    }
}