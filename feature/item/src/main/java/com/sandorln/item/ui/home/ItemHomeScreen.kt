package com.sandorln.item.ui.home

import android.graphics.Bitmap
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.R
import com.sandorln.design.component.BaseBitmapImage
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.component.BaseSearchTextEditor
import com.sandorln.design.component.html.LolHtmlTagTextView
import com.sandorln.design.component.toast.BaseToast
import com.sandorln.design.component.toast.BaseToastType
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.item.model.ItemBuildException
import com.sandorln.item.ui.dialog.ItemDetailDialog
import com.sandorln.item.ui.dialog.ItemFilterDialog
import com.sandorln.item.ui.patch.ItemPatchNoteListBody
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.type.ItemTagType
import kotlin.math.floor
import com.sandorln.item.R as ItemR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ItemHomeScreen(
    itemHomeViewModel: ItemHomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentVersion by itemHomeViewModel.currentVersion.collectAsState()
    val currentItemList by itemHomeViewModel.displayItemList.collectAsState()
    val currentSpriteMap by itemHomeViewModel.currentSpriteMap.collectAsState()
    val uiState by itemHomeViewModel.itemUiState.collectAsState()

    val (bootItemList, notBootItemList) = currentItemList.partition { it.tags.contains(ItemTagType.Boots) }
    val (consumableItemList, notConsumableItemList) = notBootItemList.partition { it.tags.contains(ItemTagType.Consumable) && it.depth < ItemHomeViewModel.ITEM_LEGEND_DEPTH }
    val (normalItemList, notNormalItemList) = notConsumableItemList.partition { it.depth < 2 }
    val (epicItemList, notEpicItemList) = notNormalItemList.partition { it.depth < ItemHomeViewModel.ITEM_LEGEND_DEPTH }
    val (orrnItemList, legendItemList) = notEpicItemList.partition { it.depth == Int.MAX_VALUE }

    val selectedItemId = uiState.selectedItemId
    val itemBuildList = uiState.itemBuildList
    val itemPatchNoteList = uiState.itemPatchList
    val totalItemBuildStatus by itemHomeViewModel.itemBuildStatus.collectAsState()
    val totalItemBuildUniqueList by itemHomeViewModel.itemBuildUniqueList.collectAsState()
    val totalItemBuildGold by itemHomeViewModel.itemBuildGold.collectAsState()

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
            .collect {
                when (it) {
                    is ItemHomeSideEffect.ShowErrorMessage -> {
                        val errorId = when (it.exception) {
                            ItemBuildException.NotAddSameLegendItem -> ItemR.string.item_build_same_legend_error
                            ItemBuildException.NotShouldAddItemSize -> ItemR.string.item_build_should_add_error
                            else -> R.string.default_error_message
                        }
                        val message = context.getString(errorId)
                        BaseToast(context, BaseToastType.WARNING, message).show()
                    }

                    is ItemHomeSideEffect.ShowMessage -> {
                        BaseToast(context, BaseToastType.OKAY, context.getString(it.stringId)).show()
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
        val epicItemChunkList = epicItemList.chunked(spanCount)
        val legendItemChunkList = legendItemList.chunked(spanCount)
        val orrnItemChunkList = orrnItemList.chunked(spanCount)

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
                        itemPatchNoteList == null -> {
                            CircularProgressIndicator(
                                color = Colors.Gold03,
                                modifier = Modifier.size(IconSize.XLargeSize),
                                strokeWidth = 2.dp
                            )

                            Text(
                                text = stringResource(id = ItemR.string.item_patch_note_loading_title),
                                style = TextStyles.SubTitle02,
                                color = Colors.Gold03
                            )
                        }

                        itemPatchNoteList.isNotEmpty() -> {
                            ItemPatchNoteListBody(itemPatchNoteList = itemPatchNoteList)
                        }
                    }
                }
            }

            stickyHeader {
                ItemStickyHeader(
                    keyword = uiState.searchKeyword,
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

            if (epicItemChunkList.isNotEmpty())
                baseItemList(
                    title = epicTitle,
                    spanCount = spanCount,
                    spriteMap = currentSpriteMap,
                    itemChunkList = epicItemChunkList,
                    onClickItem = onClickItem
                )

            if (legendItemChunkList.isNotEmpty())
                baseItemList(
                    title = legendTitle,
                    spanCount = spanCount,
                    spriteMap = currentSpriteMap,
                    itemChunkList = legendItemChunkList,
                    onClickItem = onClickItem
                )

            if (orrnItemChunkList.isNotEmpty())
                baseItemList(
                    title = orrnTitle,
                    spanCount = spanCount,
                    spriteMap = currentSpriteMap,
                    itemChunkList = orrnItemChunkList,
                    onClickItem = onClickItem
                )

            item {
                Spacer(modifier = Modifier.height(Dimens.ITEM_BUILD_PEEK_HEIGHT + Spacings.Spacing02))
            }
        }

        if (selectedItemId != null) {
            ItemDetailDialog(
                versionName = currentVersion.name,
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

        ItemBuildBottomSheet(
            itemBuildList = itemBuildList,
            currentSpriteMap = currentSpriteMap,
            totalItemBuildGold = totalItemBuildGold,
            totalItemBuildStatus = totalItemBuildStatus,
            totalItemBuildUniqueList = totalItemBuildUniqueList,
            onDeleteItemBuildIndex = {
                itemHomeViewModel.sendAction(ItemHomeAction.DeleteItemBuild(it))
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ItemBuildBottomSheet(
    itemBuildList: List<ItemData>,
    currentSpriteMap: Map<String, Bitmap>,
    totalItemBuildGold: Int,
    totalItemBuildStatus: Map<String, Pair<Int, String>>,
    totalItemBuildUniqueList: List<Pair<String, String>>,
    onDeleteItemBuildIndex: (Int) -> Unit
) {
    val hasUniqueList by remember(totalItemBuildUniqueList) {
        derivedStateOf { totalItemBuildUniqueList.any { it.second.isNotEmpty() } }
    }

    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = false
    )
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )

    LaunchedEffect(bottomSheetState.currentValue) {
        if (bottomSheetState.currentValue == SheetValue.Hidden) {
            bottomSheetState.partialExpand()
        }
    }

    BottomSheetScaffold(
        content = {},
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = Dimens.ITEM_BUILD_PEEK_HEIGHT,
        sheetDragHandle = null,
        sheetContainerColor = Colors.Blue06,
        sheetShape = RoundedCornerShape(topStart = Radius.Radius05, topEnd = Radius.Radius05),
        sheetContent = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
            ) {
                Spacer(modifier = Modifier)

                Box(
                    modifier = Modifier
                        .background(
                            Colors.Gray06,
                            RoundedCornerShape(Radius.Radius05)
                        )
                        .height(3.dp)
                        .width(25.dp)
                )

                ItemBuildListBody(
                    itemBuildList = itemBuildList,
                    currentSpriteMap = currentSpriteMap,
                    onDeleteItemBuildIndex = onDeleteItemBuildIndex
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$totalItemBuildGold G",
                        style = TextStyles.SubTitle03,
                        color = Colors.Gold02
                    )

                    HorizontalDivider()

                    ItemBuildStatusBody(totalItemBuildStatus)

                    if (hasUniqueList) {
                        HorizontalDivider()

                        ItemBuildUniqueBody(totalItemBuildUniqueList)
                    }

                    Spacer(modifier = Modifier.height(Spacings.Spacing02))
                }
            }
        })
}

@Composable
fun ItemBuildUniqueBody(
    totalItemUniqueList: List<Pair<String, String>> = emptyList()
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03)
    ) {
        totalItemUniqueList.forEachIndexed { index, unique ->
            Text(
                modifier = Modifier.padding(horizontal = Spacings.Spacing05),
                text = unique.first,
                style = TextStyles.SubTitle03,
                color = Colors.Gold02
            )

            LolHtmlTagTextView(
                modifier = Modifier.padding(horizontal = Spacings.Spacing05),
                textSize = TextStyles.Body03.fontSize.value,
                lolDescription = unique.second
            )

            if (index < totalItemUniqueList.lastIndex)
                HorizontalDivider()
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

@Composable
fun ItemBuildListBody(
    itemBuildList: List<ItemData> = emptyList(),
    currentSpriteMap: Map<String, Bitmap?> = emptyMap(),
    onDeleteItemBuildIndex: (Int) -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(state = rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(
                space = Spacings.Spacing01,
                alignment = Alignment.CenterHorizontally
            )
        ) {
            repeat(ItemHomeViewModel.ITEM_BUILD_MAX_COUNT) { index ->
                val itemData = itemBuildList.getOrNull(index) ?: ItemData()
                ItemBody(
                    itemIconSize = IconSize.XLargeSize,
                    item = itemData,
                    currentSpriteMap = currentSpriteMap,
                    onClickItem = {
                        onDeleteItemBuildIndex.invoke(index)
                    }
                )
            }
        }
    }
}

@Composable
fun ItemBuildStatusBody(
    totalItemStatus: Map<String, Pair<Int, String>> = emptyMap(),
) {
    val chunkItemStatusList = totalItemStatus.toList().chunked(2)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacings.Spacing05),
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
    ) {
        Text(
            text = stringResource(id = ItemR.string.item_build_status_title),
            style = TextStyles.SubTitle03,
            color = Colors.Gold02
        )

        chunkItemStatusList.forEach { list ->
            Row {
                list.forEach { itemStatus ->
                    val title = itemStatus.first
                    val status = itemStatus.second.first
                    val suffix = itemStatus.second.second
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "$title : ${status}${suffix}",
                        style = TextStyles.Body03,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
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
    keyword: String,
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
            text = keyword,
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
fun ItemBuildBodyPreview() {
    LolChampionThemePreview {
        ItemBuildListBody()
    }
}

@Preview
@Composable
fun ItemBuildStatusBodyPreview() {
    LolChampionThemePreview {
        ItemBuildStatusBody(
            totalItemStatus = mapOf(
                "공격력" to (20 to ""),
                "공격속도%" to (20 to "%"),
                "생명력" to (1000 to ""),
                "공격력" to (20 to ""),
            )
        )
    }
}

@Preview
@Composable
fun ItemStickyHeaderPreview() {
    LolChampionThemePreview {
        ItemStickyHeader(keyword = "", onKeywordChange = {}) {

        }
    }
}