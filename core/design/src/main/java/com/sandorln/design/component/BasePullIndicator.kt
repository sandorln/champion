package com.sandorln.design.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.sandorln.design.R
import com.sandorln.design.theme.AnimationConfig
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.util.getDynamicHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasePullIndicator(
    modifier: Modifier = Modifier,
    pullToRefreshState: PullToRefreshState
) {
    val animationColors by rememberInfiniteTransition(label = "").animateColor(
        initialValue = Colors.Blue05,
        targetValue = Colors.BaseColor,
        animationSpec = infiniteRepeatable(
            tween(AnimationConfig.Normal),
            RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(pullToRefreshState.getDynamicHeight()),
        contentAlignment = Alignment.Center
    ) {
        val colorTint = if (pullToRefreshState.isRefreshing) animationColors else Colors.BaseColor

        Image(
            modifier = Modifier.size(Dimens.PullIndicatorSize),
            painter = painterResource(id = R.drawable.ic_lol_flat_gold),
            contentDescription = null,
            colorFilter = ColorFilter.tint(colorTint)
        )
    }
}