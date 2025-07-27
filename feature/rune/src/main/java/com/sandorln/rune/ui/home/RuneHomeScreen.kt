package com.sandorln.rune.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.model.data.rune.RuneData
import com.sandorln.model.data.rune.RuneSlot
import com.sandorln.model.data.rune.RuneStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuneHomeScreen(
    runeHomeViewModel: RuneHomeViewModel = hiltViewModel()
) {
    val uiState by runeHomeViewModel.uiState.collectAsState()

    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PULL_HEIGHT
    )

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading)
            pullToRefreshState.endRefresh()
    }

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing)
            runeHomeViewModel.sendAction(RuneHomeAction.RefreshRuneData)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BaseLazyColumnWithPull(
            pullToRefreshState = pullToRefreshState
        ) {
            item {
                Text(
                    modifier = Modifier.padding(
                        start = Spacings.Spacing01,
                        top = Spacings.Spacing03,
                        bottom = Spacings.Spacing00
                    ),
                    text = "룬 정하기",
                    style = TextStyles.Body02,
                    color = Colors.Gray05
                )
            }
            item {
                RuneStyleListBody(
                    modifier = Modifier.fillMaxWidth(),
                    selectedRuneStyle = uiState.selectedRuneStyle,
                    runeStyleList = uiState.runeStyleList,
                ) { runeStyle ->
                    runeHomeViewModel.sendAction(RuneHomeAction.SelectedRuneStyle(runeStyle))
                }
            }
        }
    }
}

@Composable
fun RuneStyleListBody(
    modifier: Modifier = Modifier,
    selectedRuneStyle: RuneStyle?,
    runeStyleList: List<RuneStyle>,
    onClickRuneStyle: (RuneStyle) -> Unit,
) {
    LazyRow(
        modifier = modifier.heightIn(min = Dimens.RUNE_STYLE_BAR_HEIGHT),
        contentPadding = PaddingValues(
            horizontal = Spacings.Spacing05,
            vertical = Spacings.Spacing01
        ),
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing06, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            count = runeStyleList.size,
            key = { index -> runeStyleList[index].key }) { index ->

            val runeStyle = runeStyleList.getOrNull(index) ?: return@items
            RuneStyleBody(
                isSelect = selectedRuneStyle?.key == runeStyle.key,
                runeStyle = runeStyle,
                onClickRuneStyle = onClickRuneStyle
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RuneStyleBody(
    isSelect: Boolean = false,
    runeStyle: RuneStyle,
    onClickRuneStyle: (RuneStyle) -> Unit
) {
    val size by animateDpAsState(if (isSelect) IconSize.XXLargeSize else IconSize.XLargeSize)
    val color by animateColorAsState(if (isSelect) Colors.Gold03 else Colors.Gray05, label = "")
    val colorFilter = if (isSelect) null else ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })

    Column(
        modifier = Modifier.clickable { onClickRuneStyle.invoke(runeStyle) },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GlideImage(
            modifier = Modifier
                .size(size)
                .background(Colors.Blue06, CircleShape)
                .clip(CircleShape)
                .border(1.dp, color, CircleShape),
            model = runeStyle.iconUrl,
            colorFilter = colorFilter,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(Spacings.Spacing00))

        Text(
            modifier = Modifier.width(size),
            text = runeStyle.name,
            style = TextStyles.Body02,
            color = color,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RuneSlotListBody(runeSlot: RuneSlot) {

}

@Preview
@Composable
fun RuneStyleListBodyPreview() {
    LolChampionThemePreview {
        RuneStyleListBody(
            modifier = Modifier.fillMaxWidth(),
            runeStyleList = listOf(
                RuneStyle(
                    id = 8100,
                    key = "RuneStyle1",
                    icon = "",
                    name = "RuneStyle1",
                    slots = emptyList()
                ),
                RuneStyle(
                    id = 8100,
                    key = "RuneStyle2",
                    icon = "",
                    name = "RuneStyle2",
                    slots = emptyList()
                ),
                RuneStyle(
                    id = 8100,
                    key = "RuneStyle3",
                    icon = "",
                    name = "RuneStyle3",
                    slots = emptyList()
                ),
            ),
            selectedRuneStyle = RuneStyle(0, "RuneStyle1", "", "", emptyList()),
            onClickRuneStyle = {},
        )
    }
}

@Preview
@Composable
fun RuneSlotListBodyPreview() {
    LolChampionThemePreview {
        RuneSlotListBody(
            RuneSlot(
                listOf(
                    RuneData(0, "", "", "", "", "")
                )
            )
        )
    }
}