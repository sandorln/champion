package com.sandorln.home.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.data.repository.champion.ChampionRepository
import com.sandorln.data.repository.item.ItemRepository
import com.sandorln.data.repository.spell.SummonerSpellRepository
import com.sandorln.data.repository.version.VersionRepository
import com.sandorln.domain.usecase.version.GetAllVersionList
import com.sandorln.domain.usecase.version.GetCurrentVersion
import com.sandorln.domain.usecase.version.GetVersionNewCount
import com.sandorln.model.data.version.Version
import com.sandorln.model.data.version.VersionNewCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getVersionNewCount: GetVersionNewCount,
    getCurrentVersion: GetCurrentVersion,
    getAllVersionList: GetAllVersionList,
    versionRepository: VersionRepository,
    championRepository: ChampionRepository,
    itemRepository: ItemRepository,
    summonerSpellRepository: SummonerSpellRepository
) : ViewModel() {
    private val _isInitComplete: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isInitComplete = _isInitComplete.asStateFlow()

    private val _uiStateMutex = Mutex()
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    private val _homeAction = MutableSharedFlow<HomeAction>()
    fun sendAction(homeAction: HomeAction) = viewModelScope.launch {
        _homeAction.emit(homeAction)
    }

    private val _currentVersionName = getCurrentVersion
        .invoke()
        .map { it.name }
        .distinctUntilChanged()
    private val _allVersionList = getAllVersionList
        .invoke()
        .distinctUntilChangedBy { it.size }

    init {
        viewModelScope.launch {
            launch {
                _allVersionList
                    .filter { it.isNotEmpty() }
                    .map { it.map { version -> version.name } }
                    .collectLatest { versionNameList ->
                        val versionNewCountList = versionNameList.mapIndexed { index, versionName ->
                            async {
                                val preVersionName = versionNameList.getOrNull(index + 1) ?: ""
                                getVersionNewCount.invoke(versionName, preVersionName)
                            }
                        }.awaitAll()

                        _uiStateMutex.withLock {
                            _homeUiState.update { it.copy(versionNewCountList = versionNewCountList) }
                        }
                    }
            }
            launch {
                combine(_currentVersionName, _allVersionList) { currentVersionName, allVersionList ->
                    val currentVersionIndex = allVersionList.indexOfFirst { it.name == currentVersionName }
                    val nextVersionName = allVersionList.getOrNull(currentVersionIndex - 1)?.name ?: ""
                    val preVersionName = allVersionList.getOrNull(currentVersionIndex + 1)?.name ?: ""

                    _uiStateMutex.withLock {
                        _homeUiState.update {
                            it.copy(
                                currentVersionName = currentVersionName,
                                nextVersionName = nextVersionName,
                                preVersionName = preVersionName
                            )
                        }
                    }
                }.collect()
            }
            launch(Dispatchers.IO) {
                _homeAction.collect { action ->
                    _uiStateMutex.withLock {
                        val tempUiState = _homeUiState.value

                        when (action) {
                            HomeAction.ChangeNextVersion -> {
                                if (tempUiState.nextVersionName.isNotEmpty())
                                    versionRepository.changeCurrentVersion(tempUiState.nextVersionName)
                            }

                            HomeAction.ChangePreVersion -> {
                                if (tempUiState.preVersionName.isNotEmpty())
                                    versionRepository.changeCurrentVersion(tempUiState.preVersionName)
                            }

                            is HomeAction.ChangeVisibleVersionChangeDialog -> {
                                _homeUiState.update { it.copy(isShowVersionChangeDialog = action.isVisible) }
                            }

                            is HomeAction.ChangeVersion -> {
                                versionRepository.changeCurrentVersion(action.versionName)
                            }
                        }
                    }
                }
            }

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

            launch {
                versionRepository
                    .allVersionList
                    .first { versionList ->
                        val currentVersion = versionRepository.currentVersion.firstOrNull() ?: Version()
                        if (currentVersion.name.isEmpty()) {
                            val latestVersion = versionList.firstOrNull() ?: return@first false
                            versionRepository.changeCurrentVersion(latestVersion.name)
                        }

                        currentVersion.name.isNotEmpty()
                    }
            }
        }
    }
}

data class HomeUiState(
    val currentVersionName: String = "",
    val isShowVersionChangeDialog: Boolean = false,
    val preVersionName: String = "",
    val nextVersionName: String = "",
    val versionNewCountList: List<VersionNewCount> = mutableListOf()
)

sealed interface HomeAction {
    data object ChangeNextVersion : HomeAction
    data object ChangePreVersion : HomeAction

    data class ChangeVersion(val versionName: String) : HomeAction
    data class ChangeVisibleVersionChangeDialog(val isVisible: Boolean) : HomeAction
}