package com.sandorln.champion.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sandorln.champion.viewmodel.ChampViewModel
import com.sandorln.champion.viewmodel.VersionViewModel

class ViewModelFactory(var application: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ChampViewModel::class.java) -> ChampViewModel(application) as T
            modelClass.isAssignableFrom(VersionViewModel::class.java) -> VersionViewModel(application) as T
            else -> super.create(modelClass)
        }
}