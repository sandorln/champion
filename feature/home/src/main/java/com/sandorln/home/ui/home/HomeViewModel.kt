package com.sandorln.home.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.RefreshAppStartData
import com.sandorln.domain.usecase.champion.GetAllVersionNewSummaryChampionMap
import com.sandorln.domain.usecase.version.ChangeCurrentVersion
import com.sandorln.domain.usecase.version.GetAllVersionList
import com.sandorln.domain.usecase.version.GetCurrentVersion
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.model.data.version.Version
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
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
    getCurrentVersion: GetCurrentVersion,
    getAllVersionList: GetAllVersionList,
    refreshAppStartData: RefreshAppStartData,
    changeCurrentVersion: ChangeCurrentVersion,
    getAllVersionNewSummaryChampionMap: GetAllVersionNewSummaryChampionMap,
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
    private val _allVersionList = getAllVersionList.invoke()

    init {
        viewModelScope.launch {
            launch {
                _allVersionList
                    .filter { it.isNotEmpty() }
                    .collectLatest { versionList ->
                        _uiStateMutex.withLock {
                            _homeUiState.update {
                                it.copy(
                                    versionList = versionList
                                )
                            }
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
                                    changeCurrentVersion.invoke(tempUiState.nextVersionName)
                            }

                            HomeAction.ChangePreVersion -> {
                                if (tempUiState.preVersionName.isNotEmpty())
                                    changeCurrentVersion.invoke(tempUiState.preVersionName)
                            }

                            is HomeAction.ChangeVisibleVersionChangeDialog -> {
                                _homeUiState.update { it.copy(isShowVersionChangeDialog = action.isVisible) }
                            }

                            is HomeAction.ChangeVersion -> {
                                changeCurrentVersion.invoke(action.versionName)
                                _homeUiState.update { it.copy(isShowVersionChangeDialog = false) }
                            }
                        }
                    }
                }
            }

            launch(Dispatchers.IO) {
                refreshAppStartData.invoke()
                _isInitComplete.emit(true)
            }

            launch {
                getAllVersionList
                    .invoke()
                    .first { versionList ->
                        val currentVersion = getCurrentVersion.invoke().firstOrNull() ?: Version()
                        if (currentVersion.name.isEmpty()) {
                            val latestVersion = versionList.firstOrNull() ?: return@first false
                            changeCurrentVersion.invoke(latestVersion.name)
                        }

                        currentVersion.name.isNotEmpty()
                    }
            }

            launch {
                getAllVersionNewSummaryChampionMap
                    .invoke()
                    .collectLatest { allVersionNewSummaryChampionMap ->
                        _uiStateMutex.withLock {
                            _homeUiState.update { it.copy(newSummaryChampionMap = allVersionNewSummaryChampionMap) }
                        }
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

    val versionList: List<Version> = listOf(),
    val newSummaryChampionMap: Map<String, List<SummaryChampion>> = mapOf()
)

sealed interface HomeAction {
    data object ChangeNextVersion : HomeAction
    data object ChangePreVersion : HomeAction

    data class ChangeVersion(val versionName: String) : HomeAction
    data class ChangeVisibleVersionChangeDialog(val isVisible: Boolean) : HomeAction
}