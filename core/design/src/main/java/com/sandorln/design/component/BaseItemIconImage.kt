package com.sandorln.design.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BaseItemIconImage(
    modifier: Modifier = Modifier,
    versionName: String = "",
    itemId: String = ""
) {
    val url = "https://ddragon.leagueoflegends.com/cdn/${versionName}/img/item/${itemId}.png"

    GlideImage(
        modifier = modifier,
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}