package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sandorln.champion.repository.LolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VersionViewModel @Inject constructor(@ApplicationContext context: Context) : AndroidViewModel(context as Application) {
    private val lolRepository = LolRepository()

    val errorMsg: LiveData<String> = lolRepository.errorResult

    fun getVersion(onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            lolRepository.getVersion(onComplete)
        }
    }
}