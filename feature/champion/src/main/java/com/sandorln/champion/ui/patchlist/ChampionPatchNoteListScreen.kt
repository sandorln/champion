package com.sandorln.champion.ui.patchlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sandorln.design.component.BaseToolbar

@Composable
fun ChampionPatchNoteListScreen(
    onBackStack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BaseToolbar(onClickStartIcon = onBackStack)
    }
}