package com.sandorln.item.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.item.util.getTitleStringId
import com.sandorln.model.type.ItemTagType

@Composable
fun ItemTag(
    itemTagType: ItemTagType
) {
    val colorTint = when (itemTagType.getStyleType()) {
        ItemTagType.StyleType.ATTACK -> Colors.Orange00
        ItemTagType.StyleType.MANA -> Colors.Blue03
        ItemTagType.StyleType.ARMOR -> Colors.Green00
        else -> Colors.Gray04
    }

    Box(
        modifier = Modifier
            .border(
                width = 0.5.dp,
                color = colorTint,
                shape = RoundedCornerShape(Radius.Radius05)
            )
            .padding(
                horizontal = Spacings.Spacing00,
                vertical = 2.dp,
            )
    ) {
        Text(
            text = stringResource(id = itemTagType.getTitleStringId()),
            style = TextStyles.Body04.copy(fontSize = 7.sp),
            color = colorTint
        )
    }
}

@Preview
@Composable
fun ItemTagPreview() {
    LolChampionThemePreview {
        ItemTag(itemTagType = ItemTagType.NonbootsMovement)
    }
}