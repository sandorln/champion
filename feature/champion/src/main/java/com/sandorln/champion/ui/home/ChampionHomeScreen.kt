package com.sandorln.champion.ui.home

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.champion.util.getResourceId
import com.sandorln.design.component.BaseBitmapImage
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.component.BaseTextEditor
import com.sandorln.design.component.toast.BaseToast
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.model.type.ChampionTag
import kotlin.math.floor
import com.sandorln.champion.R as championR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChampionHomeScreen(
    championHomeViewModel: ChampionHomeViewModel = hiltViewModel(),
    moveToChampionDetailScreen: (championId: String, version: String) -> Unit
) {
    val context = LocalContext.current
    val currentVersion by championHomeViewModel.currentVersion.collectAsState()
    val currentChampionList by championHomeViewModel.displayChampionList.collectAsState()
    val currentSpriteMap by championHomeViewModel.currentSpriteMap.collectAsState()
    val uiState by championHomeViewModel.championUiState.collectAsState()
    val selectedTagSet by remember { derivedStateOf { uiState.selectChampionTagSet } }

    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PULL_HEIGHT
    )

    LaunchedEffect(true) {
        championHomeViewModel
            .sideEffect
            .collect {
                when (it) {
                    is ChampionHomeSideEffect.ShowErrorMessage -> {
                        BaseToast
                            .createDefaultErrorToast(context)
                            .show()
                    }
                }
            }
    }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading)
            pullToRefreshState.endRefresh()
    }

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing)
            championHomeViewModel.sendAction(ChampionHomeAction.RefreshChampionData)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val spanCount = floor(this.maxWidth / IconSize.XXLargeSize).toInt()
        val chunkChampionList = currentChampionList.chunked(spanCount)
        BaseLazyColumnWithPull(
            pullToRefreshState = pullToRefreshState
        ) {
            item {
                ChampionTagFilterBody(
                    modifier = Modifier
                        .padding(top = Spacings.Spacing05)
                        .fillMaxWidth(),
                    selectedTagSet = selectedTagSet,
                    onClickAction = {
                        championHomeViewModel.sendAction(ChampionHomeAction.ToggleChampionTag(it))
                    })
            }

            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Colors.Blue06.copy(alpha = 1f),
                                    Colors.Blue06.copy(alpha = 0.0f)
                                ),
                                startY = Spacings.Spacing08.value
                            )
                        )
                ) {
                    BaseTextEditor(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = Spacings.Spacing05,
                                vertical = Spacings.Spacing03
                            ),
                        text = uiState.searchKeyword,
                        hint = stringResource(id = championR.string.search_champion),
                        onChangeTextListener = { search ->
                            val action = ChampionHomeAction.ChangeChampionSearchKeyword(search)
                            championHomeViewModel.sendAction(action)
                        }
                    )
                }
            }

            items(chunkChampionList.size) { columnIndex ->
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(spanCount) { rowIndex ->
                        val champion = runCatching {
                            chunkChampionList[columnIndex][rowIndex]
                        }.getOrNull()

                        if (champion != null) {
                            ChampionBody(
                                champion = champion,
                                currentSpriteMap = currentSpriteMap,
                                moveToChampionDetailScreen = {
                                    moveToChampionDetailScreen.invoke(champion.id, currentVersion)
                                }
                            )
                        } else {
                            Spacer(modifier = Modifier.width(IconSize.XXLargeSize))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChampionBody(
    modifier: Modifier = Modifier,
    champion: SummaryChampion = SummaryChampion(),
    currentSpriteMap: Map<String, Bitmap?>,
    moveToChampionDetailScreen: () -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { moveToChampionDetailScreen.invoke() }
            .fillMaxHeight()
            .width(IconSize.XXLargeSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val bitmap = champion.image.getImageBitmap(currentSpriteMap)
        BaseBitmapImage(
            bitmap = bitmap,
            loadingDrawableId = com.sandorln.design.R.drawable.ic_main_champion,
            imageSize = IconSize.XXLargeSize
        )

        Text(
            modifier = Modifier.padding(vertical = 1.dp),
            text = champion.name,
            style = TextStyles.Body03.copy(fontSize = 8.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = Colors.Gold02
        )
    }
}

@Composable
fun ChampionTagFilterBody(
    modifier: Modifier = Modifier,
    selectedTagSet: Set<ChampionTag> = setOf(),
    onClickAction: (ChampionTag) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            Spacings.Spacing05,
            Alignment.CenterHorizontally
        )
    ) {
        ChampionTag.entries.forEach { championTag ->
            val iconId = championTag.getResourceId()
            val isSelect = selectedTagSet.contains(championTag)
            val contentColor = when {
                isSelect -> Colors.Gold02
                else -> Colors.Gray07
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacings.Spacing00)
            ) {
                Icon(
                    modifier = Modifier
                        .clickable {
                            onClickAction.invoke(championTag)
                        }
                        .size(IconSize.LargeSize),
                    painter = painterResource(id = iconId),
                    contentDescription = null,
                    tint = contentColor
                )

                Text(
                    text = championTag.name,
                    style = TextStyles.Body04,
                    color = contentColor
                )
            }
        }
    }
}

@Preview
@Composable
internal fun ChampionTagBodyPreview() {
    LolChampionThemePreview {
        ChampionTagFilterBody(
            selectedTagSet = setOf(*ChampionTag.entries.toTypedArray())
        ) {

        }
    }
}

@Preview
@Composable
internal fun ChampionHomeScreenPreview() {
    LolChampionThemePreview {
        ChampionHomeScreen { _, _ -> }
    }
}