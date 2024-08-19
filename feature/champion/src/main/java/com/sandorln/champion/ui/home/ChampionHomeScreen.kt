package com.sandorln.champion.ui.home

import android.graphics.Bitmap
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.champion.ui.patch.ChampionPatchNoteListBody
import com.sandorln.design.R
import com.sandorln.design.component.BaseBitmapImage
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.component.BaseSearchTextEditor
import com.sandorln.design.component.toast.BaseToast
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.model.data.champion.SummaryChampion
import kotlin.math.floor
import com.sandorln.champion.R as championR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChampionHomeScreen(
    championHomeViewModel: ChampionHomeViewModel = hiltViewModel(),
    moveToChampionDetailScreen: (championId: String, version: String) -> Unit,
    moveToChampionPatchNoteListScreen: (version: String) -> Unit
) {
    val context = LocalContext.current
    val currentVersion by championHomeViewModel.currentVersion.collectAsState()
    val currentChampionList by championHomeViewModel.displayChampionList.collectAsState()
    val currentSpriteMap by championHomeViewModel.currentSpriteMap.collectAsState()
    val uiState by championHomeViewModel.championUiState.collectAsState()
    val championPatchNoteList = uiState.championPatchNoteList

    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PULL_HEIGHT
    )

    val onClickChampionBody: (SummaryChampion) -> Unit = {
        moveToChampionDetailScreen.invoke(it.id, currentVersion)
    }

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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacings.Spacing02)
                        .animateContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03)
                ) {
                    when {
                        championPatchNoteList == null -> {
                            CircularProgressIndicator(
                                color = Colors.Gold03,
                                modifier = Modifier.size(IconSize.XLargeSize),
                                strokeWidth = 2.dp
                            )

                            Text(
                                text = stringResource(id = championR.string.champion_patch_note_loading_title),
                                style = TextStyles.SubTitle02,
                                color = Colors.Gold03
                            )
                        }

                        championPatchNoteList.isNotEmpty() -> {
                            ChampionPatchNoteListBody(
                                moveToChampionPatchNoteListScreen = { moveToChampionPatchNoteListScreen.invoke(currentVersion) },
                                championPatchNoteList = championPatchNoteList
                            )
                        }
                    }
                }
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
                        .padding(all = Spacings.Spacing03),
                ) {
                    BaseSearchTextEditor(
                        modifier = Modifier.fillMaxWidth(),
                        text = uiState.searchKeyword,
                        hint = stringResource(id = championR.string.search_champion),
                        onChangeTextListener = { search ->
                            val action = ChampionHomeAction.ChangeChampionSearchKeyword(search)
                            championHomeViewModel.sendAction(action)
                        }
                    )
                }
            }

            if (chunkChampionList.isNotEmpty())
                baseChampionList(
                    spanCount = spanCount,
                    chunckChampionList = chunkChampionList,
                    currentSpriteMap = currentSpriteMap,
                    onClickChampion = onClickChampionBody
                )
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
            loadingDrawableId = R.drawable.ic_main_champion,
            imageSize = IconSize.XXLargeSize
        )

        Text(
            modifier = Modifier.padding(vertical = 1.dp),
            text = champion.name,
            style = TextStyles.Body03.copy(fontSize = 8.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = Colors.Gold02,
        )
    }
}

private fun LazyListScope.baseChampionList(
    spanCount: Int = 5,
    chunckChampionList: List<List<SummaryChampion>> = mutableListOf(),
    currentSpriteMap: Map<String, Bitmap?>,
    onClickChampion: (SummaryChampion) -> Unit
) {
    items(chunckChampionList.size) { columnIndex ->
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            items(spanCount) { rowIndex ->
                val champion = runCatching {
                    chunckChampionList[columnIndex][rowIndex]
                }.getOrNull()

                if (champion != null) {
                    ChampionBody(
                        champion = champion,
                        currentSpriteMap = currentSpriteMap,
                        moveToChampionDetailScreen = {
                            onClickChampion.invoke(champion)
                        }
                    )
                } else {
                    Spacer(modifier = Modifier.width(IconSize.XXLargeSize))
                }
            }
        }
    }
}


@Preview
@Composable
internal fun ChampionHomeScreenPreview() {
    LolChampionThemePreview {
        ChampionHomeScreen(
            moveToChampionDetailScreen = { _, _ -> },
            moveToChampionPatchNoteListScreen = {}
        )
    }
}