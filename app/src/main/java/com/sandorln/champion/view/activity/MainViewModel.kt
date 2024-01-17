package com.sandorln.champion.view.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.data.repository.champion.ChampionRepository
import com.sandorln.data.repository.sprite.SpriteRepository
import com.sandorln.data.repository.version.VersionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val versionRepository: VersionRepository,
    private val championRepository: ChampionRepository,
    private val spriteRepository: SpriteRepository
) : ViewModel() {
    val currentSummaryChampionList = championRepository
        .currentSummaryChampionList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val currentSpriteList = spriteRepository
        .currentSpriteFileMap
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    init {
        viewModelScope.launch {
            versionRepository.refreshVersionList()
            val allVersionList = versionRepository.allVersionList.firstOrNull() ?: emptyList()
            val latestVersion = allVersionList.firstOrNull() ?: ""
            versionRepository.changeCurrentVersion(latestVersion)

            val championList = championRepository.refreshChampionList(latestVersion).getOrNull() ?: emptyList()
            val championSpriteList = championList.map { it.image.sprite }.distinct()
            spriteRepository.refreshSpriteBitmap(latestVersion, championSpriteList)
        }
    }
}