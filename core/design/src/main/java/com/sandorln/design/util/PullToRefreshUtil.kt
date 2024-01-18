package com.sandorln.design.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.sandorln.design.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
fun PullToRefreshState.getDynamicHeight(): Dp =
    if (isRefreshing) {
        Dimens.PullHeight
    } else {
        lerp(0.dp, Dimens.PullHeight, progress)
    }