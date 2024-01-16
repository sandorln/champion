package com.sandorln.champion.view.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.data.repo.champion.ChampionRepository
import com.sandorln.data.repo.version.VersionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val versionRepository: VersionRepository,
    private val championRepository: ChampionRepository
) : ViewModel() {
    val currentSummaryChampionList = championRepository
        .currentSummaryChampionList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            versionRepository.refreshVersionList()
            val allVersionList = versionRepository.allVersionList.firstOrNull() ?: emptyList()
            val latestVersion = allVersionList.firstOrNull() ?: ""
            versionRepository.changeCurrentVersion(latestVersion)

            championRepository.refreshChampionList(latestVersion)
        }
    }
}