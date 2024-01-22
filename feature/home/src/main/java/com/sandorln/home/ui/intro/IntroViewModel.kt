package com.sandorln.home.ui.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.data.repository.champion.ChampionRepository
import com.sandorln.data.repository.item.ItemRepository
import com.sandorln.data.repository.spell.SummonerSpellRepository
import com.sandorln.data.repository.sprite.SpriteRepository
import com.sandorln.data.repository.version.VersionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    versionRepository: VersionRepository,
    championRepository: ChampionRepository,
    itemRepository: ItemRepository,
    summonerSpellRepository: SummonerSpellRepository,
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
                            val currentVersion = versionRepository.currentVersion.firstOrNull()?.name ?: ""
                            if (currentVersion.isEmpty()) versionRepository.changeCurrentVersion(versionName)

                            val championResult = if (version.isCompleteChampions) {
                                true
                            } else {
                                championRepository.refreshChampionList(versionName).isSuccess
                            }

                            val itemResult = if (version.isCompleteItems) {
                                true
                            } else {
                                itemRepository.refreshItemList(versionName).isSuccess
                            }

                            val summonerSpellResult = if (version.isCompleteSummonerSpell) {
                                true
                            } else {
                                summonerSpellRepository.refreshSummonerSpellList(versionName).isSuccess
                            }

                            val resultVersion = version.copy(
                                isCompleteChampions = championResult,
                                isCompleteItems = itemResult,
                                isCompleteSummonerSpell = summonerSpellResult,
                            )

                            versionRepository.updateVersionData(resultVersion)
                        }
                }

                _isInitComplete.emit(true)
            }
        }
    }
}