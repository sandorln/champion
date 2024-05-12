package com.sandorln.champion.ui.patchlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.champion.ui.home.ChampionPatchNoteBody
import com.sandorln.design.R
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.component.BaseToolbar
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionPatchNoteListScreen(
    championPatchNoteListViewModel: ChampionPatchNoteListViewModel = hiltViewModel(),
    onBackStack: () -> Unit,
) {
    val uiState by championPatchNoteListViewModel.uiState.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PULL_HEIGHT
    )

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading)
            pullToRefreshState.endRefresh()
    }

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing)
            championPatchNoteListViewModel.sendAction(ChampionPatchNoteListAction.RefreshChampionPatchNoteList)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BaseToolbar(
            onClickStartIcon = onBackStack,
            title = stringResource(id = com.sandorln.champion.R.string.champion_patch_note_title)
        )

        BaseLazyColumnWithPull(pullToRefreshState = pullToRefreshState) {
            item {
                Spacer(modifier = Modifier.height(Spacings.Spacing05))
            }
            items(uiState.championPatchNoteList?.size ?: 0) { index ->
                val championPatchNote = uiState.championPatchNoteList?.getOrNull(index) ?: return@items
                ChampionPatchNoteBody(championPatchNote = championPatchNote)
            }
        }
    }
}