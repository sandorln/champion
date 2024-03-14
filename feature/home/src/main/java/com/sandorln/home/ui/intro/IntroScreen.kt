package com.sandorln.home.ui.intro

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.R as designR

@Composable
fun IntroScreen() {
    val logoColor by rememberInfiniteTransition(label = "")
        .animateColor(
            label = "",
            initialValue = Colors.BaseColor,
            targetValue = Colors.Gold07,
            animationSpec = infiniteRepeatable(
                tween(1000),
                RepeatMode.Reverse
            )
        )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier
                .heightIn(max = Dimens.INTRO_SPLASH_HEIGHT_MAX)
                .scale(1f)
                .align(Alignment.Center),
            painter = painterResource(id = designR.drawable.ic_logo_black),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .heightIn(max = Dimens.INTRO_SPLASH_HEIGHT_MAX)
                .align(Alignment.Center)
                .offset(y = -Spacings.Spacing04),
            painter = painterResource(id = designR.drawable.ic_logo_black),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = logoColor)
        )
    }
}


@Preview
@Composable
fun IntroScreenPreview() {
    LolChampionThemePreview {
        IntroScreen()
    }
}