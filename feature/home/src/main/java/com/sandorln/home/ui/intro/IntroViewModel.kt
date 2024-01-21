package com.sandorln.home.ui.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.data.repository.champion.ChampionRepository
import com.sandorln.data.repository.sprite.SpriteRepository
import com.sandorln.data.repository.version.VersionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    versionRepository: VersionRepository,
    championRepository: ChampionRepository,
    spriteRepository: SpriteRepository
) : ViewModel() {
    private val _isInitComplete: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isInitComplete = _isInitComplete.asStateFlow()

    init {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
                runCatching {
                    versionRepository
                        .getNotInitCompleteVersionList()
                        .map { version ->
                            val versionName = version.name
                            val currentVersion = versionRepository.currentVersion.firstOrNull() ?: ""
                            if (currentVersion.isEmpty()) versionRepository.changeCurrentVersion(versionName)

                            async {
                                val championResult = if (version.isCompleteChampions) {
                                    true
                                } else {
                                    championRepository.refreshChampionList(versionName).isSuccess
                                }

                                val championSpriteResult = when {
                                    championResult && version.isDownLoadChampionIconSprite -> true
                                    championResult && !version.isDownLoadChampionIconSprite -> {
                                        val championSpriteFileList = championRepository
                                            .getSummaryChampionListByVersion(versionName)
                                            .map { it.image.sprite }
                                            .distinct()

                                        spriteRepository.refreshSpriteBitmap(versionName, championSpriteFileList).isSuccess
                                    }

                                    else -> false
                                }

                                val resultVersion = version.copy(
                                    isCompleteChampions = championResult,
                                    isDownLoadChampionIconSprite = championSpriteResult
                                )
                                versionRepository.updateVersionData(resultVersion)
                            }
                        }.awaitAll()
                }

                _isInitComplete.emit(true)
            }
        }
    }
}