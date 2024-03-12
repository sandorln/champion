package com.sandorln.design.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sandorln.design.R
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles

@Composable
fun BaseFilterTag(
    isCheck: Boolean = false,
    title: String = "필터",
    onClickTag: () -> Unit = {}
) {
    val inContentColor by animateColorAsState(
        targetValue = if (isCheck) Colors.BasicBlack else Colors.Gray05,
        label = ""
    )
    val borderColor by animateColorAsState(
        targetValue = if (isCheck) Colors.Gold02 else Colors.Gray05,
        label = ""
    )
    val contentColor by animateColorAsState(
        targetValue = if (isCheck) Colors.Gold02 else Color.Transparent,
        label = ""
    )
    val shape = RoundedCornerShape(Radius.Radius04)

    Row(
        modifier = Modifier
            .border(1.dp, borderColor, shape)
            .background(contentColor, shape)
            .clickable { onClickTag.invoke() }
            .padding(vertical = Spacings.Spacing01, horizontal = Spacings.Spacing02),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing00)
    ) {
        Image(
            modifier = Modifier.size(IconSize.MediumSize),
            painter = painterResource(id = R.drawable.ic_check),
            contentDescription = null,
            colorFilter = ColorFilter.tint(inContentColor)
        )

        Text(
            text = title,
            color = inContentColor,
            style = TextStyles.Body02
        )
    }
}

@Preview
@Composable
fun BaseFilterTagPreview() {
    LolChampionThemePreview {
        BaseFilterTag(title = "필터")
    }
}