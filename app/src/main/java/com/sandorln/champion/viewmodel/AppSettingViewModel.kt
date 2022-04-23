package com.sandorln.champion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.sandorln.champion.usecase.GetVersionCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppSettingViewModel @Inject constructor(
    context: Context,
    getVersionCategory: GetVersionCategory
) : AndroidViewModel(context as Application) {

}