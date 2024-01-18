package com.sandorln.design.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseLazyColumnWithPull(
    pullToRefreshState: PullToRefreshState,
    content: LazyListScope.() -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        item {
            BasePullIndicator(pullToRefreshState = pullToRefreshState)
        }

        content.invoke(this)
    }
}