package com.sandorln.champion.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.champion.GetChampionDetail
import com.sandorln.domain.usecase.champion.GetChampionDiffStatusVersion
import com.sandorln.domain.usecase.champion.GetChampionVersionList
import com.sandorln.domain.usecase.champion.GetSimilarChampionList
import com.sandorln.domain.usecase.champion.GetSummaryChampion
import com.sandorln.domain.usecase.champion.HasChampionDetail
import com.sandorln.domain.usecase.version.GetPreviousVersion
import com.sandorln.model.data.champion.ChampionDetailData
import com.sandorln.model.data.champion.ChampionSpell
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.model.keys.BundleKeys
import com.sandorln.model.type.SpellType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChampionDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getSummaryChampion: GetSummaryChampion,
    private val getChampionDetail: GetChampionDetail,
    private val hasChampionDetail: HasChampionDetail,
    private val getPreviousVersion: GetPreviousVersion,
    private val getSimilarChampionList: GetSimilarChampionList,
    private val getChampionVersionList: GetChampionVersionList,
    private val getChampionDiffStatusVersion: GetChampionDiffStatusVersion,
) : ViewModel() {
    private val _championId = savedStateHandle.get<String>(BundleKeys.CHAMPION_ID) ?: ""
    private val _version = savedStateHandle.getStateFlow(BundleKeys.CHAMPION_VERSION, "")

    private val _uiState = MutableStateFlow(ChampionDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ChampionDetailSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun sendAction(action: ChampionDetailAction) {
        when (action) {
            is ChampionDetailAction.ChangeSelectSkill -> _uiState.update {
                it.copy(
                    selectedSkill = action.skill,
                    selectedSkillUrl = it.championDetailData.getVideoUrl(action.skill)
                )
            }

            is ChampionDetailAction.ChangeVersion -> changeVersion(action.versionName)
            is ChampionDetailAction.ChangeVersionListDialog -> _uiState.update {
                it.copy(isShowVersionListDialog = action.visible)
            }
        }
    }

    private var _changeVersionJob: Job? = null
    private fun changeVersion(versionName: String) {
        _changeVersionJob?.cancel()

        _changeVersionJob = viewModelScope.launch {
            hasChampionDetail.invoke(
                championId = _championId,
                version = versionName
            ).onSuccess { hasData ->
                if (hasData) {
                    savedStateHandle[BundleKeys.CHAMPION_VERSION] = versionName
                    _uiState.update { it.copy(isShowVersionListDialog = false) }
                } else {
                    _sideEffect.emit(ChampionDetailSideEffect.NotFoundChampionInVersion)
                }
            }.onFailure {
                _sideEffect.emit(ChampionDetailSideEffect.ShowErrorMessage(it.message))
            }
        }
    }

    init {
        viewModelScope.launch {
            launch {
                val championVersionList = getChampionVersionList.invoke(_championId)
                val changedStatsVersion = getChampionDiffStatusVersion.invoke(_championId)

                _uiState.update {
                    val latestVersion = championVersionList.firstOrNull() ?: ""
                    it.copy(
                        isLatestVersion = latestVersion == it.selectedVersion,
                        versionNameList = championVersionList,
                        changedStatsVersion = changedStatsVersion
                    )
                }
            }

            launch {
                _version.collectLatest { version ->
                    val preSelectedSkillType = _uiState.value.selectedSkill.spellType
                    val previousVersion = getPreviousVersion.invoke(version)
                    val preChampionData = if (previousVersion == null) {
                        null
                    } else {
                        getSummaryChampion
                            .invoke(_championId, previousVersion.name)
                            .getOrNull()
                    }

                    _uiState.update {
                        val latestVersion = it.versionNameList.firstOrNull() ?: ""
                        it.copy(
                            isLatestVersion = latestVersion == version,
                            selectedVersion = version,
                            preVersionName = previousVersion?.name ?: "",
                            isShowVersionListDialog = false,
                            selectedSkillUrl = "",
                            preChampion = preChampionData
                        )
                    }

                    getChampionDetail
                        .invoke(_championId, version)
                        .onSuccess { championDetailData ->
                            val similarChampionList = getSimilarChampionList.invoke(
                                version,
                                championDetailData.tags
                            ).filterNot { it.id == _championId }

                            _uiState.update {
                                it.copy(
                                    championDetailData = championDetailData,
                                    similarChampionList = similarChampionList
                                )
                            }

                            val selectedSkill = when (preSelectedSkillType) {
                                SpellType.P -> {
                                    championDetailData.passive
                                }

                                else -> {
                                    championDetailData
                                        .spells
                                        .firstOrNull {
                                            it.spellType == preSelectedSkillType
                                        } ?: ChampionSpell()
                                }
                            }

                            sendAction(ChampionDetailAction.ChangeSelectSkill(selectedSkill))
                        }.onFailure {
                            _sideEffect.emit(ChampionDetailSideEffect.ShowErrorMessage(it.message))
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
    val isShowRatingEditorDialog: Boolean = false,
    val preVersionName: String = "",
    val preChampion: SummaryChampion? = null,
    val similarChampionList: List<SummaryChampion> = listOf(),
    val changedStatsVersion: Map<String, Boolean> = mapOf()
)

sealed interface ChampionDetailAction {
    data class ChangeVersion(val versionName: String) : ChampionDetailAction
    data class ChangeSelectSkill(val skill: ChampionSpell) : ChampionDetailAction
    data class ChangeVersionListDialog(val visible: Boolean) : ChampionDetailAction
}

sealed interface ChampionDetailSideEffect {
    data object NotFoundChampionInVersion : ChampionDetailSideEffect
    data class ShowErrorMessage(val errorMessage: String? = null) : ChampionDetailSideEffect
}
