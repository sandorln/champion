package com.sandorln.design.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideSubcomposition
import com.bumptech.glide.integration.compose.RequestState
import com.sandorln.design.R
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.IconSize
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

    GlideSubcomposition(
        modifier = modifier,
        model = url,
    ) {
        when (state) {
            RequestState.Loading -> {
                ChampionSplashPlaceholder()
            }

            RequestState.Failure -> {
                ChampionSplashPlaceholder(isError = true)
            }

            is RequestState.Success -> {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    colorFilter = colorFilter
                )
            }
        }
    }
}

@Composable
fun ChampionSplashPlaceholder(
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Colors.Gray09),
        contentAlignment = Alignment.Center
    ) {
        if (isError) {
            Icon(
                modifier = Modifier.size(IconSize.LargeSize),
                painter = painterResource(id = R.drawable.ic_main_champion),
                contentDescription = null,
                tint = Colors.Gray06
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(IconSize.LargeSize),
                color = Colors.Gold03,
                strokeWidth = 2.dp
            )
        }
    }
}


@Preview
@Composable
fun BaseChampionSplashImagePreview() {
    LolChampionThemePreview {
        BaseChampionSplashImage(championId = "Aatrox")
    }
}