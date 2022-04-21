package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.use_case.GetSummonerSpellList
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SummonerSpellViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getSummonerSpellList: GetSummonerSpellList
) : AndroidViewModel(context as Application) {
    val getSummonerSpellList = getSummonerSpellList().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ResultData.Loading)
}