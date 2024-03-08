package com.sandorln.champion.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.lerp
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.R
import com.sandorln.design.component.BaseChampionSplashImage
import com.sandorln.design.component.BaseCircleIconImage
import com.sandorln.design.component.BaseContentWithMotionToolbar
import com.sandorln.design.component.html.LolHtmlTagTextView
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.design.theme.addShadow

private enum class MotionRefIdType {
    Splash, Icon, Name, Title, Back, BottomDivider
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionDetailScreen(
    championDetailViewModel: ChampionDetailViewModel = hiltViewModel()
) {
    val uiState by championDetailViewModel.uiState.collectAsState()
    val championDetailData = uiState.championDetailData

    BaseContentWithMotionToolbar(
        headerRatio = Dimens.ChampionSplashRatio,
        headerMinHeight = Dimens.BaseToolbarHeight,
        startConstraintSet = { headerRef, _ ->
            val splashImgRef = createRefFor(MotionRefIdType.Splash)
            val iconRef = createRefFor(MotionRefIdType.Icon)
            val nameRef = createRefFor(MotionRefIdType.Name)
            val titleRef = createRefFor(MotionRefIdType.Title)
            val bottomDividerRef = createRefFor(MotionRefIdType.BottomDivider)

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
                width = Dimension.fillToConstraints
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(nameRef.top)
            }
            constrain(nameRef) {
                width = Dimension.fillToConstraints
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(headerRef.bottom, Spacings.Spacing01)
            }
            constrain(bottomDividerRef) {
                bottom.linkTo(headerRef.bottom)
                alpha = 0f
            }
        },
        endConstraintSet = { headerRef, _ ->
            val splashImgRef = createRefFor(MotionRefIdType.Splash)
            val iconRef = createRefFor(MotionRefIdType.Icon)
            val nameRef = createRefFor(MotionRefIdType.Name)
            val titleRef = createRefFor(MotionRefIdType.Title)
            val backRef = createRefFor(MotionRefIdType.Back)
            val bottomDividerRef = createRefFor(MotionRefIdType.BottomDivider)

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
            constrain(bottomDividerRef) {
                bottom.linkTo(headerRef.bottom)
                alpha = 1f
            }
        },
        headerContent = { progress ->
            val iconSize = lerp(IconSize.XXXLargeSize, IconSize.XLargeSize, progress)
            val titleSize = lerp(TextStyles.Title01.fontSize, TextStyles.Body02.fontSize, progress)
            val nameSize = lerp(TextStyles.Title02.fontSize, TextStyles.SubTitle01.fontSize, progress)
            val titleColor = lerp(Colors.Gold02, Colors.BasicWhite, progress)
            val nameColor = lerp(Colors.BasicWhite, Colors.Gold02, progress)

            Box(
                modifier = Modifier.layoutId(MotionRefIdType.Splash)
            ) {
                BaseChampionSplashImage(
                    modifier = Modifier.matchParentSize(),
                    championId = championDetailData.id
                )
            }

            Box(
                modifier = Modifier
                    .size(iconSize)
                    .background(Colors.BaseColor, CircleShape)
                    .layoutId(MotionRefIdType.Icon)
            ) {
                BaseCircleIconImage(
                    versionName = uiState.version,
                    modifier = Modifier.matchParentSize(),
                    id = championDetailData.id
                )
            }

            Box(modifier = Modifier.layoutId(MotionRefIdType.Title)) {
                Text(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    text = championDetailData.title,
                    style = TextStyles.Title01.addShadow(),
                    fontSize = titleSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = titleColor
                )
            }

            Box(
                modifier = Modifier.layoutId(MotionRefIdType.Name)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.TopCenter),
                    text = championDetailData.name,
                    style = TextStyles.Body01.addShadow(),
                    fontSize = nameSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = nameColor
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

            HorizontalDivider(
                modifier = Modifier.layoutId(MotionRefIdType.BottomDivider)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
        ) {
            Spacer(modifier = Modifier.height(Spacings.Spacing02))

            ChampionDetailInfoTitle(title = "스토리")

            LolHtmlTagTextView(
                modifier = Modifier.padding(horizontal = Spacings.Spacing04),
                lolDescription = championDetailData.lore,
                textSize = TextStyles.Body01.fontSize.value,
                textColor = Colors.Gray03
            )

            ChampionDetailInfoTitle(title = "스킬")

            championDetailData.spells.forEach {
                LolHtmlTagTextView(
                    modifier = Modifier.padding(horizontal = Spacings.Spacing04),
                    lolDescription = it.tooltip,
                    textSize = TextStyles.Body01.fontSize.value,
                    textColor = Colors.Gray03
                )
            }


            Spacer(modifier = Modifier.height(Spacings.Spacing02))
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

@Composable
internal fun ChampionDetailInfoTitle(
    modifier: Modifier = Modifier,
    title: String = ""
) {
    Column(
        modifier = modifier
            .wrapContentWidth()
            .padding(
                horizontal = Spacings.Spacing04,
                vertical = Spacings.Spacing01
            ),
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing00)
    ) {
        Text(
            modifier = Modifier,
            text = title,
            style = TextStyles.Title03,
            color = Colors.Gold02
        )

        HorizontalDivider()
    }
}

@Preview
@Composable
fun ChampionDetailInfoTitlePreview() {
    LolChampionThemePreview {
        ChampionDetailInfoTitle(title = "title")
    }
}