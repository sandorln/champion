package com.sandorln.champion.ui.home

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
class ChampionHomeViewModel @Inject constructor(
    championRepository: ChampionRepository,
    spriteRepository: SpriteRepository,
    versionRepository: VersionRepository
) : ViewModel() {
    val currentChampionList = championRepository.currentSummaryChampionList.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val currentSpriteMap = spriteRepository.currentSpriteFileMap.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    init {
        viewModelScope.launch {
            versionRepository.refreshVersionList()
            val latestVersion = versionRepository.allVersionList.firstOrNull()?.firstOrNull() ?: return@launch
            versionRepository.changeCurrentVersion(latestVersion)
            val spriteList = championRepository.refreshChampionList(latestVersion).getOrNull()?.map { it.image.sprite }?.distinct() ?: return@launch
            spriteRepository.refreshSpriteBitmap(latestVersion, spriteList)
        }
    }
}