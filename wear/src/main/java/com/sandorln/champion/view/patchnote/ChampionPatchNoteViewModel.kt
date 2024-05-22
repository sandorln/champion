package com.sandorln.champion.view.patchnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.champion.GetChampionPatchNoteList
import com.sandorln.domain.usecase.version.ChangeCurrentVersion
import com.sandorln.domain.usecase.version.GetAllVersionList
import com.sandorln.domain.usecase.version.GetCurrentVersion
import com.sandorln.domain.usecase.version.RefreshVersionList
import com.sandorln.model.data.champion.ChampionPatchNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChampionPatchNoteViewModel @Inject constructor(
    private val refreshVersionList: RefreshVersionList,
    private val getAllVersionList: GetAllVersionList,
    private val currentVersion: GetCurrentVersion,
    private val getChampionPatchNoteList: GetChampionPatchNoteList,
    private val changeCurrentVersion: ChangeCurrentVersion
) : ViewModel() {
    private val _championList = MutableStateFlow<List<ChampionPatchNote>>(emptyList())
    val championList = _championList.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                refreshVersionList.invoke()
            }
            launch {
                currentVersion
                    .invoke()
                    .map { it.name }
                    .distinctUntilChanged()
                    .filter(String::isEmpty)
                    .combine(getAllVersionList.invoke()) { _, allVersionList ->
                        if (allVersionList.isEmpty()) return@combine
                        val firstVersionName = allVersionList.firstOrNull()?.name ?: return@combine
                        changeCurrentVersion.invoke(firstVersionName)
                    }.collect()
            }
            launch {
                currentVersion
                    .invoke()
                    .map { it.name }
                    .collectLatest {
                        val result = getChampionPatchNoteList.invoke(it)
                        _championList.emit(result.getOrDefault(emptyList()))
                    }
            }
        }
    }
}