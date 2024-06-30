package com.sandorln.item.ui.home

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.R
import com.sandorln.design.component.BaseBitmapImage
import com.sandorln.design.component.BaseFilterTag
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.component.BaseSearchTextEditor
import com.sandorln.design.component.toast.BaseToast
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.item.ui.dialog.ItemDetailDialog
import com.sandorln.item.util.getTitleStringId
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.map.MapType
import com.sandorln.model.type.ItemTagType
import kotlin.math.floor
import com.sandorln.item.R as itemR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ItemHomeScreen(
    itemHomeViewModel: ItemHomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentItemList by itemHomeViewModel.displayItemList.collectAsState()
    val currentSpriteMap by itemHomeViewModel.currentSpriteMap.collectAsState()
    val uiState by itemHomeViewModel.itemUiState.collectAsState()

    val (bootItemList, notBootItemList) = currentItemList.partition { it.tags.contains(ItemTagType.Boots) }
    val (consumableItemList, normalItemList) = notBootItemList.partition { it.tags.contains(ItemTagType.Consumable) }
    val selectedItemId = uiState.selectedItemId
    val currentVersion = uiState.currentVersionName

    val onClickItem: (ItemData) -> Unit = {
        itemHomeViewModel.sendAction(ItemHomeAction.SelectItemData(it.id))
    }

    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PULL_HEIGHT
    )

    LaunchedEffect(true) {
        itemHomeViewModel
            .sideEffect
            .collect {
                when (it) {
                    is ItemHomeSideEffect.ShowErrorMessage -> {
                        BaseToast.createDefaultErrorToast(context).show()
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
        val bootsItemListChunkList = bootItemList.chunked(spanCount)
        val consumableItemChunkList = consumableItemList.chunked(spanCount)
        val normalItemChunkList = normalItemList.chunked(spanCount)

        val bootsTitle = stringResource(id = itemR.string.item_boots)
        val consumableTitle = stringResource(id = itemR.string.item_consumable)
        val normalTitle = stringResource(id = itemR.string.item_normal)

        BaseLazyColumnWithPull(
            pullToRefreshState = pullToRefreshState
        ) {
            item {
                ItemHomeMenuBody(
                    title = "아이템 빌더",
                    isShowDetailBody = uiState.isShowBuildBody,
                    onToggleVisibleDetailBody = {
                        itemHomeViewModel.sendAction(ItemHomeAction.ToggleVisibleBuildBody)
                    }
                )
            }

            item {
                ItemHomeMenuBody(
                    title = "아이템 필터",
                    isShowDetailBody = uiState.isShowFilterBody,
                    onToggleVisibleDetailBody = {
                        itemHomeViewModel.sendAction(ItemHomeAction.ToggleVisibleFilterBody)
                    }
                )

                AnimatedVisibility(
                    visible = uiState.isShowFilterBody,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    FilterSelectBody(
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
                        }
                    )
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
                    BaseSearchTextEditor(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = Spacings.Spacing05,
                                vertical = Spacings.Spacing03
                            ),
                        text = uiState.searchKeyword,
                        hint = stringResource(id = itemR.string.search_item),
                        onChangeTextListener = { search ->
                            val action = ItemHomeAction.ChangeItemSearchKeyword(search)
                            itemHomeViewModel.sendAction(action)
                        }
                    )
                }
            }

            if (bootsItemListChunkList.isNotEmpty())
                baseItemList(
                    title = bootsTitle,
                    spanCount = spanCount,
                    spriteMap = currentSpriteMap,
                    itemChunkList = bootsItemListChunkList,
                    onClickItem = onClickItem
                )

            if (consumableItemChunkList.isNotEmpty())
                baseItemList(
                    title = consumableTitle,
                    spanCount = spanCount,
                    spriteMap = currentSpriteMap,
                    itemChunkList = consumableItemChunkList,
                    onClickItem = onClickItem
                )

            if (normalItemChunkList.isNotEmpty())
                baseItemList(
                    title = normalTitle,
                    spanCount = spanCount,
                    spriteMap = currentSpriteMap,
                    itemChunkList = normalItemChunkList,
                    onClickItem = onClickItem
                )
        }

        if (selectedItemId != null) {
            ItemDetailDialog(
                versionName = currentVersion,
                selectedItemId = selectedItemId,
                onDismissRequest = {
                    itemHomeViewModel.sendAction(ItemHomeAction.SelectItemData(null))
                },
                onAddItemBuildData = {
                    itemHomeViewModel.sendAction(ItemHomeAction.AddItemBuild(it))
                },
                onChangeSelectItem = {
                    itemHomeViewModel.sendAction(ItemHomeAction.SelectItemData(it))
                }
            )
        }
    }
}

@Composable
fun ItemBody(
    item: ItemData = ItemData(),
    currentSpriteMap: Map<String, Bitmap?> = emptyMap(),
    onClickItem: (ItemData) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .clickable { onClickItem.invoke(item) }
            .width(IconSize.XXLargeSize),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val bitmap = item.image.getImageBitmap(currentSpriteMap)
        BaseBitmapImage(
            bitmap = bitmap,
            loadingDrawableId = com.sandorln.design.R.drawable.ic_main_item,
            imageSize = IconSize.XXLargeSize
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemNewFilerList(
    isNewItemSelect: Boolean = false,
    onToggleNewItemFilter: () -> Unit = {}
) {
    Column {
        Text(
            text = stringResource(id = itemR.string.item_list_title),
            style = TextStyles.SubTitle02,
            color = Colors.Gold02
        )
        Spacer(modifier = Modifier.height(Spacings.Spacing00))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
        ) {
            BaseFilterTag(
                isCheck = !isNewItemSelect,
                title = stringResource(id = itemR.string.item_filter_all),
                onClickTag = onToggleNewItemFilter
            )
            BaseFilterTag(
                isCheck = isNewItemSelect,
                title = stringResource(id = itemR.string.item_filter_new),
                onClickTag = onToggleNewItemFilter
            )
        }
    }
}

@Composable
fun FilterSelectBody(
    isSelectNewItem: Boolean = false,
    selectItemTag: Set<ItemTagType> = emptySet(),
    selectMapType: MapType = MapType.ALL,
    onToggleNewItemFilter: () -> Unit = {},
    onToggleItemTagTypeFilter: (itemTagType: ItemTagType) -> Unit = {},
    onClickMapFilterTag: (mapType: MapType) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = Spacings.Spacing03,
                    horizontal = Spacings.Spacing05
                ),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03)
        ) {
            ItemNewFilerList(
                isNewItemSelect = isSelectNewItem,
                onToggleNewItemFilter = onToggleNewItemFilter
            )

            ItemTagTypeFilerList(
                selectItemTag = selectItemTag,
                onToggleItemTagTypeFilter = onToggleItemTagTypeFilter
            )

            ItemMapFilerList(
                selectMapType = selectMapType,
                onClickMapFilterTag = onClickMapFilterTag
            )
        }

        Spacer(modifier = Modifier.height(Spacings.Spacing02))

        HorizontalDivider()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemMapFilerList(
    selectMapType: MapType = MapType.ALL,
    onClickMapFilterTag: (mapType: MapType) -> Unit = {}
) {
    Column {
        Text(
            text = stringResource(id = itemR.string.item_filter_map_title),
            style = TextStyles.SubTitle02,
            color = Colors.Gold02
        )
        Spacer(modifier = Modifier.height(Spacings.Spacing00))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
        ) {
            MapType.entries.filter { it != MapType.ALL }.forEach { mapType ->
                BaseFilterTag(
                    isCheck = selectMapType == mapType,
                    title = stringResource(id = mapType.getTitleStringId()),
                    onClickTag = {
                        onClickMapFilterTag.invoke(mapType)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemTagTypeFilerList(
    selectItemTag: Set<ItemTagType> = emptySet(),
    onToggleItemTagTypeFilter: (itemTagType: ItemTagType) -> Unit = {}
) {
    Column {
        Text(
            text = stringResource(id = itemR.string.item_filter_stats_title),
            style = TextStyles.SubTitle02,
            color = Colors.Gold02
        )
        Spacer(modifier = Modifier.height(Spacings.Spacing00))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
        ) {
            ItemTagType.entries.forEach { itemTagType ->
                if (itemTagType == ItemTagType.Boots || itemTagType == ItemTagType.Consumable) return@forEach
                BaseFilterTag(
                    isCheck = selectItemTag.contains(itemTagType),
                    title = stringResource(id = itemTagType.getTitleStringId()),
                    onClickTag = {
                        onToggleItemTagTypeFilter.invoke(itemTagType)
                    }
                )
            }
        }
    }
}

@Composable
fun ItemHomeMenuBody(
    title: String = "",
    isShowDetailBody: Boolean = false,
    onToggleVisibleDetailBody: () -> Unit = {}
) {
    val iconId = if (isShowDetailBody) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down
    val titleColor by animateColorAsState(
        targetValue = if (isShowDetailBody) Colors.Gold05 else Colors.Gold02,
        label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .heightIn(45.dp)
            .clickable(onClick = onToggleVisibleDetailBody)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = Spacings.Spacing05),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = TextStyles.Title03,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                tint = titleColor
            )
        }

        if (!isShowDetailBody)
            HorizontalDivider()
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

@Preview
@Composable
fun ItemFilerListPreview() {
    LolChampionThemePreview {
        ItemMapFilerList()
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
fun FilterSelectBodyPreview() {
    LolChampionThemePreview {
        FilterSelectBody()
    }
}

@Preview
@Composable
fun FilterTitleBodyPreview() {
    LolChampionThemePreview {
        ItemHomeMenuBody(
            title = "아이템 필터",
            isShowDetailBody = true
        )
    }
}