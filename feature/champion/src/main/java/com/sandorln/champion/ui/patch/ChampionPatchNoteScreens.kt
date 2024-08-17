package com.sandorln.champion.ui.patch

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sandorln.champion.R
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.component.BaseToolbar
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.model.data.patchnote.PatchNoteData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionPatchNoteListScreen(
    championPatchNoteListViewModel: ChampionPatchNoteListViewModel = hiltViewModel(),
    onBackStack: () -> Unit,
) {
    val uiState by championPatchNoteListViewModel.uiState.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PULL_HEIGHT
    )

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading)
            pullToRefreshState.endRefresh()
    }

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing)
            championPatchNoteListViewModel.sendAction(ChampionPatchNoteListAction.RefreshChampionPatchNoteList)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BaseToolbar(
            onClickStartIcon = onBackStack,
            title = stringResource(id = R.string.champion_patch_note_title)
        )

        BaseLazyColumnWithPull(pullToRefreshState = pullToRefreshState) {
            item {
                Spacer(modifier = Modifier.height(Spacings.Spacing05))
            }
            items(uiState.championPatchNoteList?.size ?: 0) { index ->
                val championPatchNote = uiState.championPatchNoteList?.getOrNull(index) ?: return@items
                ChampionPatchNoteBody(championPatchNote = championPatchNote)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChampionPatchNoteListBody(
    championPatchNoteList: List<PatchNoteData>,
    moveToChampionPatchNoteListScreen: () -> Unit
) {
    val pagerState = rememberPagerState { championPatchNoteList.size }
    val coroutineScope = rememberCoroutineScope()
    val currentPosition by remember { derivedStateOf { pagerState.currentPage } }

    val moveToPage: (page: Int) -> Unit = { page ->
        coroutineScope.launch { pagerState.scrollToPage(page) }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (titleRef, listBtnRef) = createRefs()
            Text(
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                text = stringResource(id = R.string.champion_patch_note_title),
                style = TextStyles.SubTitle02,
                color = Colors.Gold02
            )

            Text(
                modifier = Modifier
                    .constrainAs(listBtnRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end, Spacings.Spacing05)
                    }
                    .clickable {
                        moveToChampionPatchNoteListScreen.invoke()
                    },
                text = stringResource(id = R.string.champion_patch_note_list),
                style = TextStyles.Body04,
                color = Colors.Gray03
            )
        }

        HorizontalPager(state = pagerState) { index ->
            val championPatchNote = runCatching { championPatchNoteList[index] }.getOrNull() ?: return@HorizontalPager
            ChampionPatchNoteBody(championPatchNote)
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = Spacings.Spacing05))

        ChampionPatchNoteViewPagerIndicator(
            patchNoteDataList = championPatchNoteList,
            selectedPosition = currentPosition,
            onClickPatchNoteItem = moveToPage
        )
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun ChampionPatchNoteBody(championPatchNote: PatchNoteData) {
    Column(
        modifier = Modifier
            .heightIn(min = Dimens.CHAMPION_PATCH_MIN_HEIGHT)
            .fillMaxWidth()
            .padding(horizontal = Spacings.Spacing05)
            .padding(bottom = Spacings.Spacing05),
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
        ) {
            GlideImage(
                modifier = Modifier.size(IconSize.XXLargeSize),
                model = championPatchNote.imageUrl,
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun ChampionPatchNoteViewPagerIndicator(
    modifier: Modifier = Modifier,
    selectedPosition: Int = 0,
    patchNoteDataList: List<PatchNoteData>,
    onClickPatchNoteItem: (position: Int) -> Unit
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Spacings.Spacing05),
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
    ) {
        items(count = patchNoteDataList.size) { index ->
            val patchNoteData = patchNoteDataList.getOrNull(index) ?: return@items
            val isSelected = selectedPosition == index
            val color by animateColorAsState(
                targetValue = if (isSelected) Colors.Gold03 else Colors.Gray05,
                label = ""
            )
            val colorFilter = if (isSelected) null else ColorFilter.tint(Colors.Gray05, BlendMode.Color)

            Column(
                modifier = Modifier.clickable {
                    onClickPatchNoteItem.invoke(index)
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlideImage(
                    modifier = modifier
                        .size(IconSize.XLargeSize)
                        .background(Colors.Blue06, CircleShape)
                        .clip(CircleShape)
                        .border(1.dp, color, CircleShape),
                    model = patchNoteData.imageUrl,
                    colorFilter = colorFilter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Text(
                    modifier = modifier.width(IconSize.XLargeSize),
                    text = patchNoteData.title,
                    style = TextStyles.Body04,
                    color = color,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
internal fun ChampionPatchNoteListPreview() {
    LolChampionThemePreview {
        ChampionPatchNoteListBody(
            moveToChampionPatchNoteListScreen = {},
            championPatchNoteList = List<PatchNoteData>(10) {
                PatchNoteData(
                    title = "title_$it",
                    summary = "summary_$it",
                    imageUrl = "image_$it"
                )
            }
        )
    }
}

@Preview
@Composable
internal fun ChampionPatchNoteViewPagerIndicatorPreview() {
    LolChampionThemePreview {
        ChampionPatchNoteViewPagerIndicator(
            patchNoteDataList = List(10) { PatchNoteData("챔피언 이름 $it", "", "이것은 패치 노트 내용") }
        ) {

        }
    }
}