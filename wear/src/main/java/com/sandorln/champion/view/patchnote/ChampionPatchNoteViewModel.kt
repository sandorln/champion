package com.sandorln.champion.view.patchnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.data.repository.champion.ChampionRepository
import com.sandorln.model.data.champion.SummaryChampion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChampionPatchNoteViewModel @Inject constructor(
    private val championRepository: ChampionRepository
): ViewModel() {
    private val _championList = MutableStateFlow<List<SummaryChampion>>(emptyList())

    init {
        viewModelScope.launch {
            championRepository.getSummaryChampionListByVersion("14.8.1")
        }
    }
}