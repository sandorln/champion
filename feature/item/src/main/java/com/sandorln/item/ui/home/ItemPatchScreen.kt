package com.sandorln.item.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sandorln.design.R
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.model.data.item.ItemPatchNote
import kotlinx.coroutines.launch
import com.sandorln.item.R as ItemR

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemPatchNoteListBody(
    itemPatchNoteList: List<ItemPatchNote> = emptyList()
) {
    val pagerState = rememberPagerState { itemPatchNoteList.size }
    val coroutineScope = rememberCoroutineScope()
    val hasNextItemPatch by remember(key1 = pagerState.currentPage) {
        mutableStateOf(pagerState.currentPage < pagerState.pageCount - 1)
    }
    val hasPreviousItemPatch by remember(key1 = pagerState.currentPage) {
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
            text = stringResource(id = ItemR.string.item_patch_note_title),
            style = TextStyles.SubTitle02,
            color = Colors.Gold02
        )

        HorizontalPager(state = pagerState) { index ->
            val itemPatchNote = runCatching { itemPatchNoteList[index] }.getOrNull() ?: return@HorizontalPager
            ItemPatchNoteBody(itemPatchNote)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
        ) {
            Image(
                modifier = Modifier.clickable(enabled = hasPreviousItemPatch) {
                    moveToPage.invoke(pagerState.currentPage - 1)
                },
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = null,
                colorFilter = if (hasPreviousItemPatch) null else ColorFilter.tint(Colors.Gray05)
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
                text = "${pagerState.currentPage + 1} / ${itemPatchNoteList.size}",
                style = TextStyles.Body04
            )

            Image(
                modifier = Modifier.clickable(enabled = hasNextItemPatch) {
                    moveToPage.invoke(pagerState.currentPage + 1)
                },
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                colorFilter = if (hasNextItemPatch) null else ColorFilter.tint(Colors.Gray05)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ItemPatchNoteBody(
    itemPatchNote: ItemPatchNote
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
                model = itemPatchNote.image,
                contentDescription = null
            )
            Text(
                text = itemPatchNote.itemName,
                style = TextStyles.Title02
            )
        }

        HorizontalDivider()

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemPatchNote.detailPathList.forEach { patchNote ->
                Text(
                    text = patchNote,
                    style = TextStyles.Body03,
                    color = Colors.Gray03
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