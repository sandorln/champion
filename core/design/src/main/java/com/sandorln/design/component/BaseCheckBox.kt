package com.sandorln.design.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.LolChampionTheme
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.design.R

@Composable
fun BaseCheckBox(
    modifier: Modifier = Modifier,
    isCheck: Boolean = false,
    iconSize: Dp = IconSize.LargeSize,
    disableIconColor: Color = Colors.Gray03,
    title: String = "",
    titleStyle: TextStyle = TextStyles.Body01,
    iconAndTextPadding: Dp = Spacings.Spacing03,
    onClickCheckBox: () -> Unit = {}
) {
    val completeDrawableId by remember(isCheck) {
        val id = if (isCheck) {
            R.drawable.ic_checkbox_fill
        } else {
            R.drawable.ic_checkbox_line
        }
        mutableIntStateOf(id)
    }

    Row(
        modifier = modifier.clickable {
            onClickCheckBox.invoke()
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            painter = painterResource(id = completeDrawableId),
            contentDescription = null,
            tint = if (isCheck) Color.Unspecified else disableIconColor
        )

        if (title.isNotEmpty()) {
            Spacer(modifier = Modifier.width(iconAndTextPadding))

            Text(
                text = title,
                style = titleStyle
            )
        }
    }
}


@Preview
@Composable
fun BaseCheckBoxPreview() {
    LolChampionTheme {
        Surface {
            BaseCheckBox()
        }
    }
}