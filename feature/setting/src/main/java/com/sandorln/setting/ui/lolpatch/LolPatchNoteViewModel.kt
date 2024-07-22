package com.sandorln.setting.ui.lolpatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandorln.domain.usecase.version.GetAllVersionList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LolPatchNoteViewModel @Inject constructor(
    getAllVersionList: GetAllVersionList
) : ViewModel() {
    val allVersionList = getAllVersionList
        .invoke()
        .map { versionList ->
            versionList
                .mapNotNull { version ->
                    val (major1, minor1, _) = version.name
                        .split('.')
                        .map { it.toInt() }

                    /* 9 이상 버전만 패치 노트 존재 */
                    if (major1 > 9) {
                        major1 to minor1
                    } else {
                        null
                    }
                }
                .distinct()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun getPatchNoteUrl(major1: Int, minor1: Int) {
        val oldUrl = "https://www.leagueoflegends.com/ko-kr/news/game-updates/patch-$major1-$minor1-notes/"
        val newUrl = "https://www.leagueoflegends.com/ko-kr/news/game-updates/lol-patch-$major1-$minor1-notes/"
    }
}