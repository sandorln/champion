package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.model.result.ResultData
import com.sandorln.champion.use_case.GetItemList
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getItemList: GetItemList
) : AndroidViewModel(context as Application) {
    val itemList = getItemList().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ResultData.Loading)
}