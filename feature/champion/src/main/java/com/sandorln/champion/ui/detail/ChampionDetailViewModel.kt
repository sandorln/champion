package com.sandorln.champion.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.champion.GetChampionDetail
import com.sandorln.domain.usecase.champion.GetSummaryChampion
import com.sandorln.domain.usecase.champion.HasChampionDetail
import com.sandorln.domain.usecase.version.GetAllVersionList
import com.sandorln.domain.usecase.version.GetPreviousVersion
import com.sandorln.model.data.champion.ChampionDetailData
import com.sandorln.model.data.champion.ChampionSpell
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.model.keys.BundleKeys
import com.sandorln.model.type.SpellType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ChampionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSummaryChampion: GetSummaryChampion,
    private val getChampionDetail: GetChampionDetail,
    private val hasChampionDetail: HasChampionDetail,
    private val getAllVersionList: GetAllVersionList,
    private val getPreviousVersion: GetPreviousVersion
) : ViewModel() {
    private val _championId = savedStateHandle.get<String>(BundleKeys.CHAMPION_ID) ?: ""
    private val _version = savedStateHandle.getStateFlow(BundleKeys.CHAMPION_VERSION, "")

    private val _uiMutex = Mutex()
    private val _uiState = MutableStateFlow(ChampionDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _action = MutableSharedFlow<ChampionDetailAction>()
    fun sendAction(action: ChampionDetailAction) = viewModelScope.launch {
        _action.emit(action)
    }

    private val _sideEffect = MutableSharedFlow<ChampionDetailSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            launch {
                getAllVersionList
                    .invoke()
                    .map { versionList ->
                        versionList.map { version ->
                            version.name
                        }
                    }
                    .distinctUntilChanged()
                    .collectLatest { versionList ->
                        _uiMutex.withLock {
                            _uiState.update {
                                val latestVersion = versionList.firstOrNull() ?: ""
                                it.copy(
                                    isLatestVersion = latestVersion == it.selectedVersion,
                                    versionNameList = versionList
                                )
                            }
                        }
                    }
            }
            launch {
                _version.collectLatest { version ->
                    val preSelectedSkillType = _uiState.value.selectedSkill.spellType

                    _uiMutex.withLock {
                        _uiState.update {
                            val latestVersion = it.versionNameList.firstOrNull() ?: ""
                            it.copy(
                                isLatestVersion = latestVersion == version,
                                isShowVersionListDialog = false,
                                selectedVersion = version,
                                selectedSkillUrl = ""
                            )
                        }
                    }

                    val previousVersion = getPreviousVersion.invoke(version)
                    val preChampionData = if (previousVersion == null) {
                        null
                    } else {
                        getSummaryChampion
                            .invoke(_championId, previousVersion.name)
                            .getOrNull()
                    }

                    getChampionDetail
                        .invoke(_championId, version)
                        .onSuccess { championDetailData ->
                            _uiMutex.withLock {
                                _uiState.update {
                                    it.copy(
                                        championDetailData = championDetailData,
                                        preVersionName = previousVersion?.name ?: "",
                                        preChampion = preChampionData
                                    )
                                }
                            }

                            val selectedSkill = when (preSelectedSkillType) {
                                SpellType.P -> championDetailData.passive
                                else -> championDetailData.spells.first { it.spellType == preSelectedSkillType }
                            }

                            sendAction(ChampionDetailAction.ChangeSelectSkill(selectedSkill))
                        }.onFailure {
                            _sideEffect.emit(ChampionDetailSideEffect.ShowToastMessage(it.message ?: "Error"))
                        }
                }
            }

            launch {
                _action.collect { action ->
                    when (action) {
                        is ChampionDetailAction.ChangeSelectSkill -> {
                            _uiMutex.withLock {
                                _uiState.update {
                                    val championKey = String.format("%04d", it.championDetailData.key)
                                    val selectedSkill = action.skill
                                    val spellKeyName = selectedSkill.spellType.name
                                    val suffix = if (selectedSkill.spellType == SpellType.P) "mp4" else "webm"
                                    val url = "https://d28xe8vt774jo5.cloudfront.net/champion-abilities" +
                                            "/${championKey}/ability_${championKey}_${spellKeyName}1.$suffix"

                                    it.copy(
                                        selectedSkill = selectedSkill,
                                        selectedSkillUrl = url
                                    )
                                }
                            }
                        }

                        is ChampionDetailAction.ChangeVersion -> {
                            hasChampionDetail.invoke(
                                championId = _championId,
                                version = action.versionName
                            ).onSuccess { hasData ->
                                if (hasData) {
                                    savedStateHandle[BundleKeys.CHAMPION_VERSION] = action.versionName
                                    _uiMutex.withLock {
                                        _uiState.update {
                                            it.copy(isShowVersionListDialog = false)
                                        }
                                    }
                                } else {
                                    _sideEffect.emit(ChampionDetailSideEffect.ShowToastMessage("해당 버전 때 챔피언이 없었습니다"))
                                }
                            }.onFailure {
                                _sideEffect.emit(ChampionDetailSideEffect.ShowToastMessage(it.message ?: "Error"))
                            }
                        }

                        is ChampionDetailAction.ChangeVersionListDialog -> {
                            _uiMutex.withLock {
                                _uiState.update {
                                    it.copy(
                                        isShowVersionListDialog = action.visible
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ChampionDetailUiState(
    val championDetailData: ChampionDetailData = ChampionDetailData(),
    val selectedVersion: String = "",
    val versionNameList: List<String> = listOf(),
    val selectedSkill: ChampionSpell = ChampionSpell(),
    val selectedSkillUrl: String = "",
    val isLatestVersion: Boolean = false,
    val isShowVersionListDialog: Boolean = false,
    val preVersionName: String = "",
    val preChampion: SummaryChampion? = null
)

sealed interface ChampionDetailAction {
    data class ChangeVersion(val versionName: String) : ChampionDetailAction
    data class ChangeSelectSkill(val skill: ChampionSpell) : ChampionDetailAction
    data class ChangeVersionListDialog(val visible: Boolean) : ChampionDetailAction
}

sealed interface ChampionDetailSideEffect {
    data class ShowToastMessage(val message: String) : ChampionDetailSideEffect
}
