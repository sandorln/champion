package com.sandorln.design.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Transition
import com.sandorln.design.theme.LolChampionThemePreview

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BaseChampionSplashImage(
    modifier: Modifier = Modifier,
    skinNum: String = "0",
    championId: String,
    colorFilter: ColorFilter? = null
) {
    val url = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${championId}_$skinNum.jpg"

    GlideImage(
        modifier = modifier,
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        colorFilter = colorFilter
    )
}

@Preview
@Composable
fun BaseChampionSplashImagePreview() {
    LolChampionThemePreview {
        BaseChampionSplashImage(championId = "Aatrox")
    }
}