package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.model.SummonerSpell
import com.sandorln.model.result.ResultData
import com.sandorln.champion.usecase.GetSummonerSpellListUseCase
import com.sandorln.champion.usecase.GetSummonerSpellVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummonerSpellViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getSummonerSpellVersionUseCase: GetSummonerSpellVersionUseCase,
    private val getSummonerSpellListUseCase: GetSummonerSpellListUseCase
) : AndroidViewModel(context as Application) {
    private val _summonerSpellList: MutableStateFlow<com.sandorln.model.result.ResultData<List<com.sandorln.model.SummonerSpell>>> = MutableStateFlow(com.sandorln.model.result.ResultData.Loading)
    val summonerSpellList = _summonerSpellList
        .onStart {
            when (val result = _summonerSpellList.firstOrNull()) {
                is com.sandorln.model.result.ResultData.Success -> {
                    result.data?.let { itemList ->
                        /* 현재 보여지고 있는 소환사 주문 버전과 설정에서 설정된 버전이 다를 시 갱신 */
                        val nowShowSummonerSpellVersion = itemList.first().version
                        val localSummonerSpellVersion = getSummonerSpellVersionUseCase().first()
                        if (nowShowSummonerSpellVersion != localSummonerSpellVersion)
                            refreshSummonerSpellList()
                    }
                }
                /* 오류 이거나 로딩 중이였을 시 곧바로 갱신 */
                else -> refreshSummonerSpellList()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), com.sandorln.model.result.ResultData.Loading)

    fun refreshSummonerSpellList() = viewModelScope.launch(Dispatchers.IO) {
        _summonerSpellList.emitAll(getSummonerSpellListUseCase())
    }
}