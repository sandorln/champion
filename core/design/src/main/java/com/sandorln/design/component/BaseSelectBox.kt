package com.sandorln.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sandorln.design.R
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.LolChampionTheme
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles

@Composable
fun BaseSelectBox(
    modifier: Modifier = Modifier,
    title: String = "",
    hint: String = "개발 언어",
    isEnable: Boolean? = null,
    onClickSelectBox: () -> Unit = {}
) {
    val textColor = when (isEnable) {
        null -> Colors.Gray03
        true -> Colors.BasicWhite
        false -> Colors.Gray06
    }
    val iconColor = if (isEnable == false) Colors.Gray06 else Colors.Gray05
    Row(
        modifier = modifier
            .background(
                color = Colors.Gray08,
                shape = RoundedCornerShape(Radius.Radius03)
            )
            .clickable {
                if (isEnable != false)
                    onClickSelectBox.invoke()
            }
            .heightIn(min = 56.dp)
            .padding(Spacings.Spacing04),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title.ifEmpty { hint },
            style = TextStyles.SubTitle01,
            color = textColor
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_down),
            contentDescription = null,
            tint = iconColor
        )
    }
}

@Preview
@Composable
fun BaseSelectBoxPreview() {
    LolChampionTheme {
        Surface {
            BaseSelectBox(
                modifier = Modifier
                    .padding(horizontal = Spacings.Spacing05)
                    .fillMaxWidth()
            )
        }
    }
}