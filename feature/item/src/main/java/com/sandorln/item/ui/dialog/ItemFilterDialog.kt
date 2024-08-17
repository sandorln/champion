package com.sandorln.item.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sandorln.design.component.BaseFilterTag
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.item.R
import com.sandorln.item.util.getTitleStringId
import com.sandorln.model.data.map.MapType
import com.sandorln.model.type.ItemTagType

@Composable
fun ItemFilterDialog(
    isSelectNewItem: Boolean = false,
    selectItemTag: Set<ItemTagType> = emptySet(),
    selectMapType: MapType = MapType.ALL,
    onToggleNewItemFilter: () -> Unit = {},
    onToggleItemTagTypeFilter: (itemTagType: ItemTagType) -> Unit = {},
    onClickMapFilterTag: (mapType: MapType) -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        ItemFilterBody(
            isSelectNewItem = isSelectNewItem,
            selectItemTag = selectItemTag,
            selectMapType = selectMapType,
            onToggleNewItemFilter = onToggleNewItemFilter,
            onToggleItemTagTypeFilter = onToggleItemTagTypeFilter,
            onClickMapFilterTag = onClickMapFilterTag
        )
    }
}

@Composable
fun ItemFilterBody(
    isSelectNewItem: Boolean = false,
    selectItemTag: Set<ItemTagType> = emptySet(),
    selectMapType: MapType = MapType.ALL,
    onToggleNewItemFilter: () -> Unit = {},
    onToggleItemTagTypeFilter: (itemTagType: ItemTagType) -> Unit = {},
    onClickMapFilterTag: (mapType: MapType) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .background(
                color = Colors.Blue07,
                shape = RoundedCornerShape(Radius.Radius04)
            )
            .border(
                width = 1.dp,
                color = Colors.Gold06,
                shape = RoundedCornerShape(Radius.Radius04)
            )
            .padding(all = Spacings.Spacing05)
    ) {
        Column(
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
            text = stringResource(id = R.string.item_list_title),
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
                title = stringResource(id = R.string.item_filter_all),
                onClickTag = onToggleNewItemFilter
            )
            BaseFilterTag(
                isCheck = isNewItemSelect,
                title = stringResource(id = R.string.item_filter_new),
                onClickTag = onToggleNewItemFilter
            )
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
            text = stringResource(id = R.string.item_filter_stats_title),
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemMapFilerList(
    selectMapType: MapType = MapType.ALL,
    onClickMapFilterTag: (mapType: MapType) -> Unit = {}
) {
    Column {
        Text(
            text = stringResource(id = R.string.item_filter_map_title),
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

@Preview
@Composable
internal fun ItemFilterDialogBodyPreview() {
    LolChampionThemePreview {
        ItemFilterBody()
    }
}