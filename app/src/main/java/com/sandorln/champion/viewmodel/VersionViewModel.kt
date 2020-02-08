package com.sandorln.champion.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.sandorln.champion.repository.LolRepository

class VersionViewModel(application: Application) : AndroidViewModel(application) {
    private val lolRepository = LolRepository()

    val errorMsg: LiveData<String> = lolRepository.errorResult

    fun getVersion(onComplete: () -> Unit) {
        lolRepository.getVersion(onComplete)
    }
}