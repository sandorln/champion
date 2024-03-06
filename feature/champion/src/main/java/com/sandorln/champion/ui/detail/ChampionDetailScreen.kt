package com.sandorln.champion.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.lerp
import androidx.constraintlayout.compose.Dimension
import com.sandorln.design.R
import com.sandorln.design.component.BaseChampionSplashImage
import com.sandorln.design.component.BaseCircleIconImage
import com.sandorln.design.component.BaseContentWithMotionToolbar
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles

private enum class MotionRefIdType {
    Splash, Icon, Name, Title, Back
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionDetailScreen() {
    BaseContentWithMotionToolbar(
        headerRatio = Dimens.ChampionSplashRatio,
        headerMinHeight = Dimens.BaseToolbarHeight,
        startConstraintSet = { headerRef, _ ->
            val splashImgRef = createRefFor(MotionRefIdType.Splash)
            val iconRef = createRefFor(MotionRefIdType.Icon)
            val nameRef = createRefFor(MotionRefIdType.Name)
            val titleRef = createRefFor(MotionRefIdType.Title)

            constrain(splashImgRef) {
                width = Dimension.matchParent
                height = Dimension.fillToConstraints
                top.linkTo(headerRef.top)
                bottom.linkTo(headerRef.bottom)
                alpha = 1f
            }
            constrain(iconRef) {
                start.linkTo(headerRef.start)
                end.linkTo(headerRef.end)
                top.linkTo(headerRef.top)
                bottom.linkTo(headerRef.bottom)
                alpha = 0f
            }
            constrain(titleRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(nameRef.top)
            }
            constrain(nameRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(headerRef.bottom)
            }
        },
        endConstraintSet = { headerRef, _ ->
            val splashImgRef = createRefFor(MotionRefIdType.Splash)
            val iconRef = createRefFor(MotionRefIdType.Icon)
            val nameRef = createRefFor(MotionRefIdType.Name)
            val titleRef = createRefFor(MotionRefIdType.Title)
            val backRef = createRefFor(MotionRefIdType.Back)

            constrain(splashImgRef) {
                width = Dimension.matchParent
                height = Dimension.fillToConstraints
                top.linkTo(headerRef.top)
                bottom.linkTo(headerRef.bottom)
                alpha = 0f
            }
            constrain(iconRef) {
                start.linkTo(backRef.end, Spacings.Spacing02)
                top.linkTo(headerRef.top)
                bottom.linkTo(headerRef.bottom)
                alpha = 1f
            }
            constrain(titleRef) {
                height = Dimension.fillToConstraints
                start.linkTo(iconRef.end, Spacings.Spacing02)
                top.linkTo(headerRef.top)
                bottom.linkTo(nameRef.top)
            }
            constrain(nameRef) {
                height = Dimension.fillToConstraints
                start.linkTo(iconRef.end, Spacings.Spacing02)
                top.linkTo(titleRef.bottom)
                bottom.linkTo(headerRef.bottom)
            }
        },
        headerContent = { progress ->
            val iconSize = lerp(IconSize.XXXLargeSize, IconSize.XLargeSize, progress)
            val nameSize = lerp(TextStyles.Title02.fontSize, TextStyles.Body03.fontSize, progress)
            val titleSize = lerp(TextStyles.Title01.fontSize, TextStyles.SubTitle01.fontSize, progress)

            Box(
                modifier = Modifier.layoutId(MotionRefIdType.Splash)
            ) {
                BaseChampionSplashImage(
                    modifier = Modifier.matchParentSize(),
                    championId = "Aatrox"
                )
            }

            Box(
                modifier = Modifier
                    .size(iconSize)
                    .background(Colors.BaseColor, CircleShape)
                    .layoutId(MotionRefIdType.Icon)
            ) {
                BaseCircleIconImage(
                    versionName = "14.5.1",
                    modifier = Modifier.matchParentSize(),
                    id = "Aatrox"
                )
            }

            Box(modifier = Modifier.layoutId(MotionRefIdType.Title)) {
                Text(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    text = "title",
                    style = TextStyles.Title01,
                    fontSize = titleSize,
                )
            }

            Box(
                modifier = Modifier.layoutId(MotionRefIdType.Name)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "name",
                    style = TextStyles.Title01,
                    fontSize = nameSize
                )
            }

            Box(
                modifier = Modifier
                    .layoutId(MotionRefIdType.Back)
                    .height(Dimens.BaseToolbarHeight)
            ) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    IconButton(
                        modifier = Modifier
                            .padding(start = Spacings.Spacing02)
                            .size(IconSize.XLargeSize)
                            .align(alignment = Alignment.Center),
                        onClick = {

                        }) {
                        Icon(
                            modifier = Modifier.size(IconSize.XLargeSize),
                            painter = painterResource(id = R.drawable.ic_chevron_left),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            for (index in 0..200) {
                Text(
                    text = "index $index",
                    style = TextStyles.SubTitle01
                )
            }
        }
    }
}

@Preview
@Composable
fun ChampionDetailScreenPreview() {
    LolChampionThemePreview {
        ChampionDetailScreen()
    }
}