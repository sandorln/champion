package com.sandorln.item.ui.home

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.R
import com.sandorln.design.component.BaseBitmapImage
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.component.BasePatchNoteListBodyWithLoading
import com.sandorln.design.component.BaseSearchTextEditor
import com.sandorln.design.component.toast.BaseToast
import com.sandorln.design.component.toast.BaseToastType
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.item.ui.dialog.ItemDetailDialog
import com.sandorln.item.ui.dialog.ItemFilterDialog
import com.sandorln.model.data.item.ItemData
import kotlin.math.floor
import com.sandorln.item.R as ItemR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ItemHomeScreen(
    itemHomeViewModel: ItemHomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by itemHomeViewModel.itemUiState.collectAsState()

    val bootsTitle = stringResource(id = ItemR.string.item_boots)
    val consumableTitle = stringResource(id = ItemR.string.item_consumable)
    val normalTitle = stringResource(id = ItemR.string.item_normal)
    val epicTitle = stringResource(id = ItemR.string.item_epic)
    val legendTitle = stringResource(id = ItemR.string.item_legend)
    val orrnTitle = stringResource(id = ItemR.string.item_orrn)

    val onClickItem: (ItemData) -> Unit = {
        itemHomeViewModel.sendAction(ItemHomeAction.SelectItemData(it.id))
    }

    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PULL_HEIGHT
    )

    LaunchedEffect(true) {
        itemHomeViewModel
            .sideEffect
            .collect { sideEffect ->
                when (sideEffect) {
                    is ItemHomeSideEffect.ShowErrorMessage -> {
                        val message = context.getString(R.string.default_error_message)
                        BaseToast(context, BaseToastType.WARNING, message).show()
                    }

                    is ItemHomeSideEffect.ShowMessage -> {
                        BaseToast(context, BaseToastType.OKAY, sideEffect.message).show()
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
            itemHomeViewModel.sendAction(ItemHomeAction.RefreshItemData)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val spanCount = floor(this.maxWidth / IconSize.XXLargeSize).toInt()
        itemHomeViewModel.sendAction(ItemHomeAction.ChangeSpan(span = spanCount))

        BaseLazyColumnWithPull(
            pullToRefreshState = pullToRefreshState
        ) {
            item {
                BasePatchNoteListBodyWithLoading(
                    title = stringResource(id = ItemR.string.item_patch_note_title),
                    loadingTitle = stringResource(id = ItemR.string.item_patch_note_loading_title),
                    patchNoteDataList = uiState.itemPatchList
                )
            }

            stickyHeader {
                ItemStickyHeader(
                    onKeywordChange = { search ->
                        val action = ItemHomeAction.ChangeItemSearchKeyword(search)
                        itemHomeViewModel.sendAction(action)
                    },
                    onClickFilterIcon = {
                        val action = ItemHomeAction.ChangeShowFilterDialog(true)
                        itemHomeViewModel.sendAction(action)
                    }
                )
            }

            if (uiState.bootItemList.isNotEmpty())
                baseItemList(
                    title = bootsTitle,
                    spanCount = spanCount,
                    spriteMap = uiState.currentSpriteMap,
                    itemChunkList = uiState.bootItemList,
                    onClickItem = onClickItem
                )

            if (uiState.consumableItemList.isNotEmpty())
                baseItemList(
                    title = consumableTitle,
                    spanCount = spanCount,
                    spriteMap = uiState.currentSpriteMap,
                    itemChunkList = uiState.consumableItemList,
                    onClickItem = onClickItem
                )

            if (uiState.normalItemList.isNotEmpty())
                baseItemList(
                    title = normalTitle,
                    spanCount = spanCount,
                    spriteMap = uiState.currentSpriteMap,
                    itemChunkList = uiState.normalItemList,
                    onClickItem = onClickItem
                )

            if (uiState.epicItemList.isNotEmpty())
                baseItemList(
                    title = epicTitle,
                    spanCount = spanCount,
                    spriteMap = uiState.currentSpriteMap,
                    itemChunkList = uiState.epicItemList,
                    onClickItem = onClickItem
                )

            if (uiState.legendItemList.isNotEmpty())
                baseItemList(
                    title = legendTitle,
                    spanCount = spanCount,
                    spriteMap = uiState.currentSpriteMap,
                    itemChunkList = uiState.legendItemList,
                    onClickItem = onClickItem
                )

            if (uiState.orrnItemList.isNotEmpty())
                baseItemList(
                    title = orrnTitle,
                    spanCount = spanCount,
                    spriteMap = uiState.currentSpriteMap,
                    itemChunkList = uiState.orrnItemList,
                    onClickItem = onClickItem
                )

            item {
                Spacer(modifier = Modifier.height(Spacings.Spacing02))
            }
        }

        if (uiState.selectedItemId != null) {
            ItemDetailDialog(
                versionName = uiState.currentVersionName,
                selectedItemId = uiState.selectedItemId ?: "",
                onDismissRequest = {
                    itemHomeViewModel.sendAction(ItemHomeAction.SelectItemData(null))
                },
                onChangeSelectItem = {
                    itemHomeViewModel.sendAction(ItemHomeAction.SelectItemData(it))
                }
            )
        }

        if (uiState.isShowFilterDialog) {
            ItemFilterDialog(
                isSelectNewItem = uiState.isSelectNewItem,
                selectItemTag = uiState.selectTag,
                selectMapType = uiState.selectMapType,
                onToggleNewItemFilter = {
                    itemHomeViewModel.sendAction(ItemHomeAction.ToggleSelectNewItem)
                },
                onToggleItemTagTypeFilter = { itemTagType ->
                    val action = ItemHomeAction.ToggleItemTagType(itemTagType)
                    itemHomeViewModel.sendAction(action)
                },
                onClickMapFilterTag = { mapType ->
                    val action = ItemHomeAction.ChangeMapTypeFilter(mapType)
                    itemHomeViewModel.sendAction(action)
                },
                onDismissRequest = {
                    val action = ItemHomeAction.ChangeShowFilterDialog(false)
                    itemHomeViewModel.sendAction(action)
                }
            )
        }
    }
}

@Composable
fun ItemBody(
    itemIconSize: Dp = IconSize.XXLargeSize,
    item: ItemData = ItemData(),
    currentSpriteMap: Map<String, Bitmap?> = emptyMap(),
    onClickItem: (ItemData) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .clickable { onClickItem.invoke(item) }
            .width(itemIconSize),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val bitmap = item.image.getImageBitmap(currentSpriteMap)
        BaseBitmapImage(
            bitmap = bitmap,
            loadingDrawableId = R.drawable.ic_main_item,
            imageSize = itemIconSize
        )

        Text(
            modifier = Modifier.padding(vertical = 1.dp),
            text = item.name,
            style = TextStyles.Body03.copy(fontSize = 8.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = Colors.Gold02
        )
    }
}

private fun LazyListScope.baseItemList(
    title: String = "제목",
    spanCount: Int = 5,
    spriteMap: Map<String, Bitmap?> = emptyMap(),
    itemChunkList: List<List<ItemData>> = mutableListOf(),
    onClickItem: (ItemData) -> Unit = {}
) {
    item {
        Text(
            modifier = Modifier.padding(
                start = Spacings.Spacing01,
                top = Spacings.Spacing03,
                bottom = Spacings.Spacing00
            ),
            text = title,
            style = TextStyles.Body02,
            color = Colors.Gray05
        )
    }

    items(itemChunkList.size) { columnIndex ->
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            items(spanCount) { rowIndex ->
                val item = runCatching {
                    itemChunkList[columnIndex][rowIndex]
                }.getOrNull()

                if (item != null) {
                    ItemBody(
                        item = item,
                        currentSpriteMap = spriteMap,
                        onClickItem = onClickItem
                    )
                } else {
                    Spacer(modifier = Modifier.width(IconSize.XXLargeSize))
                }
            }
        }
    }
}

@Composable
fun ItemStickyHeader(
    onKeywordChange: (String) -> Unit,
    onClickFilterIcon: () -> Unit
) {
    Row(
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
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing03),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseSearchTextEditor(
            modifier = Modifier.weight(1f),
            hint = stringResource(id = ItemR.string.search_item),
            onChangeTextListener = onKeywordChange
        )

        Icon(
            modifier = Modifier
                .size(size = IconSize.LargeSize)
                .clickable { onClickFilterIcon.invoke() },
            painter = painterResource(id = R.drawable.ic_filter_on),
            contentDescription = null,
            tint = Colors.Gray04,
        )
    }
}

@Preview
@Composable
fun ItemIconBodyPreview() {
    LolChampionThemePreview {
        ItemBody()
    }
}


@Preview
@Composable
fun ItemStickyHeaderPreview() {
    LolChampionThemePreview {
        ItemStickyHeader(onKeywordChange = {}) {

        }
    }
}