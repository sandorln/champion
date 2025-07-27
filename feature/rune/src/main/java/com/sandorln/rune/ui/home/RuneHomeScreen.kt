package com.sandorln.rune.ui.home

import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuneHomeScreen(
    runeHomeViewModel: RuneHomeViewModel = hiltViewModel()
) {
    val uiState by runeHomeViewModel.uiState.collectAsState()

    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PULL_HEIGHT
    )

    BaseLazyColumnWithPull(
        pullToRefreshState = pullToRefreshState
    ) {
        item {
            Text(uiState.runeStyleList.toString())
        }
    }
}