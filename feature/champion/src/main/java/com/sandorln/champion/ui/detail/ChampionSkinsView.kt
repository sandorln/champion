package com.sandorln.champion.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.sandorln.design.component.BaseChampionSplashImage
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.design.theme.addShadow
import com.sandorln.model.data.champion.ChampionSkin

@Composable
fun ChampionSkinsView(
    modifier: Modifier = Modifier,
    championId: String,
    championSkinList: List<ChampionSkin> = emptyList()
) {
    var isShowSkinInfo by remember { mutableStateOf(true) }
    var selectedPosition by remember { mutableIntStateOf(0) }
    val lazyListState = rememberLazyListState()
    val selectedChampionSkin = championSkinList.getOrElse(selectedPosition) { championSkinList.firstOrNull() }
    val selectedChampionSkinNum = selectedChampionSkin?.num?.takeIf(String::isNotEmpty) ?: selectedPosition.toString()
    val selectedChampionSkinName = selectedChampionSkin?.name?.takeIf { selectedPosition > 0 } ?: "기본 스킨"
    val disableColorFilter = ColorFilter.tint(Colors.Gray07, BlendMode.Color)

    LaunchedEffect(key1 = championSkinList.size) {
        selectedPosition = if (championSkinList.lastIndex < selectedPosition) 0 else selectedPosition
    }

    Box(modifier = modifier) {
        BaseChampionSplashImage(
            modifier = Modifier
                .matchParentSize()
                .clickable { isShowSkinInfo = !isShowSkinInfo },
            championId = championId,
            skinNum = selectedChampionSkinNum
        )

        AnimatedVisibility(
            modifier = Modifier.matchParentSize(),
            visible = isShowSkinInfo,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ConstraintLayout(
                modifier = Modifier.fillMaxSize()
            ) {
                val (skinListRef, skinNameRef) = createRefs()

                LazyColumn(
                    modifier = Modifier
                        .constrainAs(skinListRef) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .fillMaxHeight()
                        .aspectRatio(0.35f)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Colors.BasicBlack,
                                    Colors.BasicBlack.copy(alpha = 0f)
                                ),
                                startX = 0.9f
                            )
                        )
                        .padding(horizontal = Spacings.Spacing02),
                    verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01),
                    contentPadding = PaddingValues(vertical = Spacings.Spacing05),
                    state = lazyListState
                ) {
                    items(championSkinList.size) { count ->
                        val isSelected = count == selectedPosition
                        val skinNum = championSkinList[count].num?.takeIf(String::isNotEmpty) ?: count.toString()
                        BaseChampionSplashImage(
                            modifier = Modifier
                                .clickable {
                                    selectedPosition = count
                                }
                                .fillMaxWidth()
                                .aspectRatio(Dimens.CHAMPION_SPLASH_RATIO),
                            championId = championId,
                            skinNum = skinNum,
                            colorFilter = if (isSelected) null else disableColorFilter
                        )
                    }
                }

                Text(
                    modifier = Modifier
                        .padding(
                            horizontal = Spacings.Spacing04,
                            vertical = Spacings.Spacing02
                        )
                        .constrainAs(skinNameRef) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(skinListRef.end)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                    text = selectedChampionSkinName,
                    style = TextStyles.Title03.addShadow(),
                    color = Colors.Gold01,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun ChampionSkinsViewPreview() {
    LolChampionThemePreview {
        ChampionSkinsView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(Dimens.CHAMPION_SPLASH_RATIO),
            championId = "",
            championSkinList = List(10) { ChampionSkin() }
        )
    }
}