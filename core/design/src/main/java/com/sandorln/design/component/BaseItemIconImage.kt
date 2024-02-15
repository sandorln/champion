package com.sandorln.design.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideSubcomposition
import com.bumptech.glide.integration.compose.RequestState
import com.sandorln.design.R
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.Spacings

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BaseItemIconImage(
    versionName: String = "",
    itemId: String = "",
    iconSize: Dp = IconSize.LargeSize,
    onClickIcon: () -> Unit
) {
    val url = "https://ddragon.leagueoflegends.com/cdn/${versionName}/img/item/${itemId}.png"

    GlideSubcomposition(
        modifier = Modifier
            .clickable { onClickIcon.invoke() }
            .size(iconSize)
            .border(
                width = 0.5.dp,
                color = Colors.Gold06
            ),
        model = url,
    ) {
        when (state) {
            RequestState.Failure -> {
                Icon(
                    modifier = Modifier
                        .size(iconSize)
                        .padding(Spacings.Spacing02),
                    painter = painterResource(id = R.drawable.ic_main_item),
                    contentDescription = null,
                    tint = Colors.BaseColor
                )
            }

            is RequestState.Success -> {
                Image(
                    modifier = Modifier.size(iconSize),
                    painter = painter,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }

            else -> return@GlideSubcomposition
        }
    }
}