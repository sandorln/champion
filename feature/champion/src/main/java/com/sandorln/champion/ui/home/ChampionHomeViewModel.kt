package com.sandorln.champion.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.data.repository.champion.ChampionRepository
import com.sandorln.data.repository.sprite.SpriteRepository
import com.sandorln.data.repository.version.VersionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ChampionHomeViewModel @Inject constructor(
    versionRepository: VersionRepository,
    championRepository: ChampionRepository,
    spriteRepository: SpriteRepository
) : ViewModel() {
    val currentChampionList = championRepository.currentSummaryChampionList.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val currentSpriteMap = spriteRepository.currentSpriteFileMap.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())
    private val _spriteMutex = Mutex()

    init {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
                combine(
                    versionRepository.currentVersion,
                    currentChampionList,
                    currentSpriteMap
                ) { version, championList, spriteMap ->
                    _spriteMutex.withLock {
                        if (version.isDownLoadChampionIconSprite || championList.isEmpty() || spriteMap.isNotEmpty())
                            return@combine

                        val spriteFileList = championList.map { it.image.sprite }.distinct()
                        val spriteResult = spriteRepository.refreshSpriteBitmap(version.name, spriteFileList).isSuccess

                        val tempVersion = version.copy(isDownLoadChampionIconSprite = spriteResult)
                        versionRepository.updateVersionData(tempVersion)
                    }
                }
//                    .collect()
            }
        }
    }
}