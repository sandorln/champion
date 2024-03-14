package com.sandorln.design.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BaseSkillImage(
    modifier: Modifier = Modifier,
    version: String,
    fullName: String,
    isPassive: Boolean,
    colorFilter: ColorFilter? = null
) {
    val middlePath = if (isPassive) "passive" else "spell"
    val url = "https://ddragon.leagueoflegends.com/cdn/$version/img/$middlePath/$fullName"

    GlideImage(
        modifier = modifier,
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        colorFilter = colorFilter
    )
}