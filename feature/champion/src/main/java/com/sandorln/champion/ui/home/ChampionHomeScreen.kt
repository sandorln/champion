package com.sandorln.champion.ui.home

import android.graphics.Bitmap
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sandorln.champion.util.getResourceId
import com.sandorln.design.R
import com.sandorln.design.component.BaseBitmapImage
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.component.BaseTextEditor
import com.sandorln.design.component.toast.BaseToast
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.model.data.champion.ChampionPatchNote
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.model.type.ChampionTag
import kotlinx.coroutines.launch
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
    val championPatchNoteList = uiState.championPatchNoteList

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

@Deprecated("필터가 불필요하다고 판단 : ver 2.01.00 삭제")
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun ChampionPatchNoteListBody(
    championPatchNoteList: List<ChampionPatchNote>
) {
    val pagerState = rememberPagerState { championPatchNoteList.size }
    val coroutineScope = rememberCoroutineScope()
    val hasNextChampionPatch by remember(key1 = pagerState.currentPage) {
        mutableStateOf(pagerState.currentPage < pagerState.pageCount - 1)
    }
    val hasPreviousChampionPatch by remember(key1 = pagerState.currentPage) {
        mutableStateOf(pagerState.currentPage > 0)
    }
    val moveToPage: (page: Int) -> Unit = { page ->
        coroutineScope.launch { pagerState.scrollToPage(page) }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
    ) {
        Text(
            text = stringResource(id = championR.string.champion_patch_note_title),
            style = TextStyles.SubTitle02,
            color = Colors.Gold02
        )

        HorizontalPager(state = pagerState) { index ->
            val championPatchNote = runCatching { championPatchNoteList[index] }.getOrNull() ?: return@HorizontalPager
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacings.Spacing05),
                verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
                ) {
                    GlideImage(
                        modifier = Modifier.size(IconSize.XXLargeSize),
                        model = championPatchNote.image,
                        contentDescription = null
                    )
                    Text(
                        text = championPatchNote.title,
                        style = TextStyles.Title02
                    )
                }

                HorizontalDivider()

                Text(
                    text = championPatchNote.summary,
                    style = TextStyles.Body03,
                    color = Colors.Gray03
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
        ) {
            Image(
                modifier = Modifier.clickable(enabled = hasPreviousChampionPatch) {
                    moveToPage.invoke(pagerState.currentPage - 1)
                },
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = null,
                colorFilter = if (hasPreviousChampionPatch) null else ColorFilter.tint(Colors.Gray05)
            )

            Text(
                modifier = Modifier
                    .padding(Spacings.Spacing00)
                    .background(
                        color = Colors.Gray07,
                        shape = RoundedCornerShape(Radius.Radius03)
                    )
                    .padding(
                        horizontal = Spacings.Spacing02,
                        vertical = Spacings.Spacing00
                    ),
                text = "${pagerState.currentPage + 1} / ${championPatchNoteList.size}",
                style = TextStyles.Body04
            )

            Image(
                modifier = Modifier.clickable(enabled = hasNextChampionPatch) {
                    moveToPage.invoke(pagerState.currentPage + 1)
                },
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                colorFilter = if (hasNextChampionPatch) null else ColorFilter.tint(Colors.Gray05)
            )
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

@Preview
@Composable
internal fun ChampionPatchNoteListPreview() {
    LolChampionThemePreview {
        ChampionPatchNoteListBody(
            championPatchNoteList = List<ChampionPatchNote>(10) {
                ChampionPatchNote(
                    title = "title_$it",
                    summary = "summary_$it",
                    image = "image_$it",
                    detailPathStory = "detailPathStory_it"
                )
            }
        )
    }
}