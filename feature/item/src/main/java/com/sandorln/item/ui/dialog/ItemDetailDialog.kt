package com.sandorln.item.ui.dialog

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.R
import com.sandorln.design.component.BaseBitmapImage
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.item.ui.ItemDescriptionTextView
import com.sandorln.item.ui.ItemTag
import com.sandorln.model.data.item.ItemCombination
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.data.item.SummaryItemImage
import com.sandorln.model.type.ItemTagType

@Composable
fun ItemDetailDialog(
    versionName: String,
    selectedItemId: String,
    itemDetailDialogViewModel: ItemDetailDialogViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit = {}
) {
    itemDetailDialogViewModel.initSetIdAndVersion(selectedItemId, versionName)

    val uiState by itemDetailDialogViewModel.uiState.collectAsState()
    val baseItem by remember { derivedStateOf { uiState.itemData } }
    val previousVersionItem by remember { derivedStateOf { uiState.previousVersionItem } }
    val itemCombination by remember { derivedStateOf { uiState.itemCombination } }
    val intoItemImageList by remember { derivedStateOf { uiState.intoSummaryItemList } }
    val currentSpriteMap by itemDetailDialogViewModel.currentSpriteMap.collectAsState()

    val onSelectedItem: (itemId: String) -> Unit = { itemId ->
        itemDetailDialogViewModel.sendAction(ItemDetailAction.ChangeIdAndVersion(itemId, versionName))
    }


    Dialog(onDismissRequest = {
        itemDetailDialogViewModel.sendAction(ItemDetailAction.ClearData)
        onDismissRequest.invoke()
    }) {
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
        ) {
            Spacer(modifier = Modifier.height(Spacings.Spacing03))

            ItemInfoBody(
                name = baseItem.name,
                tags = baseItem.tags,
                bitmap = baseItem.image.getImageBitmap(currentSpriteMap)
            )

            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(top = Spacings.Spacing02),
                color = Colors.Gold07
            )

            Column(
                modifier = Modifier
                    .verticalScroll(state = rememberScrollState())
                    .padding(vertical = Spacings.Spacing03)
            ) {
                PreItemCompareButton(
                    previousVersion = uiState.previousVersionName,
                    isShowPreviousBtn = uiState.isShowPreviousItem,
                    hasPreviousItem = uiState.previousVersionItem != null,
                    onToggleBtn = {
                        itemDetailDialogViewModel.sendAction(ItemDetailAction.ToggleShowPreviousItem)
                    }
                )

                Spacer(modifier = Modifier.height(Spacings.Spacing03))

                Text(
                    modifier = Modifier.padding(
                        start = Spacings.Spacing03,
                        bottom = Spacings.Spacing00
                    ),
                    text = "설명",
                    style = TextStyles.Body03,
                    color = Colors.Gold03
                )

                Row(
                    modifier = Modifier.padding(horizontal = Spacings.Spacing03),
                    horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing03)
                ) {
                    ItemStatusBody(
                        modifier = Modifier.weight(1f),
                        item = baseItem
                    )

                    if (uiState.isShowPreviousItem) {
                        ItemStatusBody(
                            modifier = Modifier.weight(1f),
                            item = previousVersionItem ?: ItemData()
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = Spacings.Spacing03),
                    color = Colors.Gold07
                )

                if (intoItemImageList.isNotEmpty()) {
                    TotalIntoItemListBody(
                        intoItemImageList = intoItemImageList,
                        spriteBitmapMap = currentSpriteMap,
                        onSelectedItem = onSelectedItem
                    )
                    Spacer(modifier = Modifier.height(Spacings.Spacing03))
                }

                if (itemCombination.fromItemList.isNotEmpty()) {
                    Text(
                        modifier = Modifier.padding(
                            start = Spacings.Spacing03,
                            bottom = Spacings.Spacing00
                        ),
                        text = "조합식",
                        style = TextStyles.Body03,
                        color = Colors.Gold03
                    )

                    TotalItemCombinationBody(
                        modifier = Modifier.padding(horizontal = Spacings.Spacing03),
                        itemCombination = itemCombination,
                        spriteBitmapMap = currentSpriteMap,
                        onSelectItem = onSelectedItem
                    )
                }
            }
        }
    }
}

@Composable
private fun TotalIntoItemListBody(
    intoItemImageList: List<SummaryItemImage>,
    spriteBitmapMap: Map<String, Bitmap?>,
    onSelectedItem: (itemId: String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(
                start = Spacings.Spacing03,
                bottom = Spacings.Spacing00
            ),
            text = "다음 아이템",
            style = TextStyles.Body03,
            color = Colors.Gold03
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.width(Spacings.Spacing03))
            intoItemImageList.forEach { itemImage ->
                BaseBitmapImage(
                    modifier = Modifier.clickable { onSelectedItem.invoke(itemImage.id) },
                    bitmap = itemImage.image.getImageBitmap(spriteBitmapMap),
                    loadingDrawableId = R.drawable.ic_main_item,
                    imageSize = IconSize.XLargeSize,
                    innerPadding = Spacings.Spacing01
                )
            }

            Spacer(modifier = Modifier.width(Spacings.Spacing03))
        }
    }
}

@Composable
private fun PreItemCompareButton(
    isShowPreviousBtn: Boolean,
    hasPreviousItem: Boolean,
    previousVersion: String,
    onToggleBtn: () -> Unit
) {
    val iconId = if (!isShowPreviousBtn) R.drawable.ic_add else R.drawable.ic_cancel
    val enableColor = if (hasPreviousItem) Colors.Gray03 else Colors.Gray06
    val clickModifier = if (hasPreviousItem) {
        Modifier.clickable {
            onToggleBtn.invoke()
        }
    } else {
        Modifier
    }

    Row(
        modifier = Modifier
            .then(clickModifier)
            .fillMaxWidth()
            .padding(end = Spacings.Spacing02),
        horizontalArrangement = Arrangement.spacedBy(
            Spacings.Spacing00,
            Alignment.End
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "이전 버전($previousVersion) 비교",
            style = TextStyles.SubTitle03,
            color = enableColor
        )
        Image(
            modifier = Modifier.size(IconSize.MediumSize),
            painter = painterResource(id = iconId),
            contentDescription = null,
            colorFilter = ColorFilter.tint(enableColor)
        )
    }
}

@Composable
fun ItemStatusBody(
    modifier: Modifier = Modifier,
    item: ItemData
) {
    Column(
        modifier = modifier
            .border(
                width = 0.5.dp,
                color = Colors.Gold05
            )
            .padding(
                top = Spacings.Spacing03,
                start = Spacings.Spacing03,
                end = Spacings.Spacing03,
                bottom = Spacings.Spacing01
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${item.gold.total} $",
                style = TextStyles.Body04,
                color = Colors.Gold04
            )

            Spacer(modifier = Modifier.width(Spacings.Spacing00))

            Text(
                text = "( 판매 ${item.gold.sell} $ )",
                style = TextStyles.Body04,
                color = Colors.Gold05
            )
        }

        HorizontalDivider()

        ItemDescriptionTextView(
            modifier = Modifier.fillMaxWidth(),
            itemDescription = item.description
        )
    }
}

@Composable
fun ItemInfoBody(
    name: String = "",
    bitmap: Bitmap? = null,
    tags: Set<ItemTagType> = emptySet()
) {
    Row(
        modifier = Modifier
            .padding(horizontal = Spacings.Spacing03)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseBitmapImage(
            bitmap = bitmap,
            loadingDrawableId = R.drawable.ic_main_item
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Spacings.Spacing00,
                    vertical = Spacings.Spacing02
                ),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing00)
        ) {
            Text(
                text = name,
                style = TextStyles.SubTitle03,
                color = Colors.Gold04
            )

            if (tags.isNotEmpty()) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing00)
                ) {
                    tags.forEach {
                        ItemTag(itemTagType = it)
                    }
                }
            }
        }
    }
}

@Composable
fun TotalItemCombinationBody(
    modifier: Modifier = Modifier,
    spriteBitmapMap: Map<String, Bitmap?> = mapOf(),
    itemCombination: ItemCombination = ItemCombination(),
    onSelectItem: (String) -> Unit = {}
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacings.Spacing00)
                .height(Dimens.ItemCombinationLineMinSize),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing00)
        ) {
            BaseBitmapImage(
                bitmap = itemCombination.image.getImageBitmap(spriteBitmapMap),
                loadingDrawableId = R.drawable.ic_main_item,
                imageSize = IconSize.LargeSize,
                innerPadding = 0.dp
            )

            Text(
                modifier = Modifier.weight(1f),
                text = "${itemCombination.name} (${itemCombination.gold.total}$)",
                style = TextStyles.Body03,
                color = Colors.Gold03,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        itemCombination.fromItemList.forEachIndexed { index, fromItemCombination ->
            ItemCombinationBody(
                hasNextItem = itemCombination.fromItemList.lastIndex > index,
                itemCombination = fromItemCombination,
                spriteBitmapMap = spriteBitmapMap,
                onSelectItem = onSelectItem
            )
        }
    }
}

@Composable
fun ItemCombinationBody(
    hasNextItem: Boolean = true,
    spriteBitmapMap: Map<String, Bitmap?> = mapOf(),
    itemCombination: ItemCombination = ItemCombination(),
    onSelectItem: (String) -> Unit
) {
    val halfMinSize = Dimens.ItemCombinationLineMinSize / 2

    Row(
        modifier = Modifier
            .clickable { onSelectItem.invoke(itemCombination.id) }
            .fillMaxWidth()
            .heightIn(min = Dimens.ItemCombinationLineMinSize)
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .width(Dimens.ItemCombinationLineMinSize)
        ) {
            VerticalDivider(
                thickness = 1.dp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .then(if (!hasNextItem) Modifier.height(halfMinSize + 1.dp) else Modifier),
                color = Colors.Blue03
            )

            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = halfMinSize)
                    .width(halfMinSize),
                color = Colors.Blue03
            )
        }

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacings.Spacing00)
                    .height(Dimens.ItemCombinationLineMinSize),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing00)
            ) {
                BaseBitmapImage(
                    bitmap = itemCombination.image.getImageBitmap(spriteBitmapMap),
                    loadingDrawableId = R.drawable.ic_main_item,
                    imageSize = IconSize.LargeSize,
                    innerPadding = 0.dp
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = "${itemCombination.name} (${itemCombination.gold.total}$)",
                    style = TextStyles.Body03,
                    color = Colors.Gray04,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            itemCombination.fromItemList.forEachIndexed { index, fromItemCombination ->
                ItemCombinationBody(
                    hasNextItem = itemCombination.fromItemList.lastIndex > index,
                    itemCombination = fromItemCombination,
                    spriteBitmapMap = spriteBitmapMap,
                    onSelectItem = onSelectItem
                )
            }
        }
    }
}

@Preview
@Composable
fun ItemDetailDialogPreView() {
    LolChampionThemePreview {
        ItemDetailDialog(
            versionName = "",
            selectedItemId = ""
        )
    }
}

@Preview
@Composable
fun ItemInfoBodyPreView() {
    LolChampionThemePreview {
        ItemInfoBody(
            "장화"
        )
    }
}

@Preview
@Composable
fun ItemStatusBodyPreView() {
    val dummyItem = ItemData(
        name = "드락사르의 암흑검",
        tags = setOf(
            ItemTagType.Mana,
            ItemTagType.Damage,
            ItemTagType.Armor,
            ItemTagType.Boots
        ),
        description = "<mainText><stats>공격력 <ornnBonus>75</ornnBonus><br>물리 관통력 <ornnBonus>26</ornnBonus><br>스킬 가속 <ornnBonus>20</ornnBonus></stats><br><br><br><li><passive>밤의 " +
                "추적자:</passive> 대상이 잃은 체력에 비례해 스킬 피해량이 최대 일정 비율까지 증가합니다. 자신이 피해를 입힌 챔피언이 3초 안에 죽으면 1.5초 동안 구조물이 아닌 대상으로부터 <keywordStealth>대상으로 지정할 수 없는 상태</keywordStealth>가 됩니다. (30(0초))<br><br><rarityMythic>신화급 기본 지속 효과:</rarityMythic> 다른 모든 <rarityLegendary>전설급</rarityLegendary> 아이템에 스킬 가속 및 이동 속도.<br></mainText>",
        gold = ItemData.Gold(total = 1000, sell = 700),
        into = listOf("1", "2", "3", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4"),
        from = listOf("1", "2", "3", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4")
    )

    LolChampionThemePreview {
        ItemStatusBody(item = dummyItem)
    }
}

@Preview
@Composable
fun TotalItemCombinationPreview() {
    LolChampionThemePreview {
        TotalItemCombinationBody(
            itemCombination = ItemCombination(
                name = "아이템 이름",
                fromItemList = listOf(
                    ItemCombination(
                        name = "다음 아이템 이름 1",
                        fromItemList = listOf(
                            ItemCombination(name = "마지막 아이템 이름 1"),
                            ItemCombination(name = "마지막 아이템 이름 2"),
                        )
                    ),
                    ItemCombination(
                        name = "다음 아이템 이름 2",
                        fromItemList = listOf(
                            ItemCombination(name = "마지막 아이템 이름")
                        )
                    )
                )
            )
        )
    }
}
