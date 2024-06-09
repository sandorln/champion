package com.sandorln.setting.ui.home

import androidx.lifecycle.ViewModel
import com.sandorln.domain.usecase.game.GetInitialGameScore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingHomeViewModel @Inject constructor(
    private val getInitialGameScore: GetInitialGameScore
) : ViewModel() {
    suspend fun getInitialGameScore() = getInitialGameScore.invoke()
}