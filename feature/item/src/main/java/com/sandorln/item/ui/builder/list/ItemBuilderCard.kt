package com.sandorln.item.ui.builder.list

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sandorln.design.R as DesignR
import com.sandorln.design.component.BaseBitmapImage
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.item.R as ItemR
import com.sandorln.item.util.getIconRes
import com.sandorln.item.util.getTitleStringRes
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.type.ChampionTag

@Composable
fun ItemBuilderCard(
    modifier: Modifier = Modifier,
    uiModel: ItemBuildUiModel,
    currentSpriteMap: Map<String, Bitmap?>,
    onClickCard: () -> Unit = {},
    onClickEdit: () -> Unit = {},
    onClickDelete: () -> Unit = {}
) {
    val cardShape = RoundedCornerShape(Radius.Radius04)
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Colors.Blue07, cardShape)
            .border(1.dp, Colors.Blue05, cardShape)
            .clickable { onClickCard.invoke() }
            .padding(horizontal = Spacings.Spacing04, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
        ) {
            // Header Row: Title on Left, More Icon on Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = uiModel.itemBuild.title,
                    style = TextStyles.SubTitle01,
                    color = Colors.BasicWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Box {
                    IconButton(
                        modifier = Modifier.size(IconSize.LargeSize),
                        onClick = { isMenuExpanded = true }
                    ) {
                        Icon(
                            modifier = Modifier.size(IconSize.MediumSize),
                            painter = painterResource(id = DesignR.drawable.ic_menu_vertical),
                            contentDescription = null,
                            tint = Colors.Gray04
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(id = ItemR.string.item_builder_edit),
                                    style = TextStyles.Body02,
                                    color = Colors.Gold02
                                )
                            },
                            onClick = {
                                isMenuExpanded = false
                                onClickEdit.invoke()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(id = ItemR.string.item_builder_delete),
                                    style = TextStyles.Body02,
                                    color = Colors.Gray04
                                )
                            },
                            onClick = {
                                isMenuExpanded = false
                                onClickDelete.invoke()
                            }
                        )
                    }
                }
            }

            // Position Tags Row
            if (uiModel.itemBuild.positionList.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing01),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    uiModel.itemBuild.positionList.forEach { tag ->
                        ItemPositionBadge(tag = tag)
                    }
                }
            }

            // Items Row
            if (uiModel.itemList.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    uiModel.itemList.forEach { item ->
                        ItemBuildCardItemImage(
                            item = item,
                            currentSpriteMap = currentSpriteMap
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemPositionBadge(
    tag: ChampionTag
) {
    val shape = RoundedCornerShape(Radius.Radius02)
    Row(
        modifier = Modifier
            .background(Colors.Blue06, shape)
            .border(0.5.dp, Colors.Gold06, shape)
            .padding(horizontal = Spacings.Spacing01, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            modifier = Modifier.size(12.dp),
            painter = painterResource(id = tag.getIconRes()),
            contentDescription = null,
            tint = Colors.Gold02
        )
        Text(
            text = stringResource(id = tag.getTitleStringRes()),
            style = TextStyles.Body04.copy(fontSize = 10.sp),
            color = Colors.Gold02
        )
    }
}

@Composable
private fun ItemBuildCardItemImage(
    item: ItemData,
    currentSpriteMap: Map<String, Bitmap?>
) {
    val imageSize = 38.dp
    val shape = RoundedCornerShape(Radius.Radius02)
    val bitmap = item.image.getImageBitmap(currentSpriteMap)

    BaseBitmapImage(
        modifier = Modifier
            .size(imageSize)
            .border(1.dp, Colors.Blue05, shape),
        bitmap = bitmap,
        loadingDrawableId = DesignR.drawable.ic_main_item,
        imageSize = imageSize
    )
}
