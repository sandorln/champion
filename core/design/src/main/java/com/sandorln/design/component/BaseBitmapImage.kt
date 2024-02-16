package com.sandorln.design.component

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sandorln.design.R
import com.sandorln.design.theme.AnimationConfig
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.Spacings

@Composable
fun BaseBitmapImage(
    bitmap: Bitmap? = null,
    @DrawableRes loadingDrawableId: Int = R.drawable.ic_main_special,
    imageSize: Dp = IconSize.XXLargeSize,
    innerPadding: Dp = Spacings.Spacing02
) {
    val itemIconAlpha by animateFloatAsState(
        targetValue = if (bitmap != null) 1f else 0f,
        label = "",
        animationSpec = tween(
            durationMillis = AnimationConfig.Normal
        )
    )

    Box {
        if (bitmap == null) {
            val loadingIconColor by rememberInfiniteTransition(label = "")
                .animateColor(
                    initialValue = Colors.Gray07,
                    targetValue = Colors.Gray09,
                    animationSpec = infiniteRepeatable(
                        tween(AnimationConfig.Normal),
                        RepeatMode.Reverse
                    ),
                    label = ""
                )

            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(imageSize)
                    .padding(innerPadding),
                painter = painterResource(id = loadingDrawableId),
                contentDescription = null,
                tint = loadingIconColor
            )
        } else {
            Image(
                modifier = Modifier
                    .alpha(itemIconAlpha)
                    .align(Alignment.Center)
                    .border(
                        width = 0.5.dp,
                        color = Colors.Gold06
                    )
                    .size(imageSize),
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}