package com.sandorln.item.ui.patch

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.item.R
import com.sandorln.model.data.patchnote.PatchNoteData
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemPatchNoteListBody(
    itemPatchNoteList: List<PatchNoteData> = emptyList()
) {
    val pagerState = rememberPagerState { itemPatchNoteList.size }
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
        Text(
            text = stringResource(id = R.string.item_patch_note_title),
            style = TextStyles.SubTitle02,
            color = Colors.Gold02
        )

        HorizontalPager(state = pagerState) { index ->
            val itemPatchNote = runCatching { itemPatchNoteList[index] }.getOrNull() ?: return@HorizontalPager
            ItemPatchNoteBody(itemPatchNote)
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = Spacings.Spacing05))

        ItemPatchNoteViewPagerIndicator(
            selectedPosition = currentPosition,
            patchNoteDataList = itemPatchNoteList,
            onClickPatchNoteItem = moveToPage
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ItemPatchNoteBody(
    itemPatchNote: PatchNoteData
) {
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
                model = itemPatchNote.imageUrl,
                contentDescription = null
            )
            Text(
                text = itemPatchNote.title,
                style = TextStyles.Title02
            )
        }

        HorizontalDivider()

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = itemPatchNote.summary,
                style = TextStyles.Body03,
                color = Colors.Gray03
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun ItemPatchNoteViewPagerIndicator(
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


@Composable
@Preview
internal fun ItemPatchNotePagerPreview() {
    LolChampionThemePreview {
        ItemPatchNoteListBody()
    }
}
