package com.sandorln.home.ui.intro

import androidx.lifecycle.ViewModel
import com.sandorln.data.repository.version.VersionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    versionRepository: VersionRepository
) : ViewModel() {

}