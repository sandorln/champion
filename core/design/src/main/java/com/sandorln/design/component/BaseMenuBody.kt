package com.sandorln.design.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.GodadviceTheme
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles

@Composable
fun BaseMenuBody(
    title: String = "메뉴",
    endTitle: String = "",
    isOnlyTitle: Boolean = false,
    onClickMenu: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clickable { onClickMenu.invoke() }
            .fillMaxWidth()
            .padding(
                start = Spacings.Spacing05,
                top = Spacings.Spacing03,
                bottom = Spacings.Spacing03,
                end = Spacings.Spacing03
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = Spacings.Spacing03),
            text = title,
            style = TextStyles.SubTitle01,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (isOnlyTitle) return@Row

        if (endTitle.isEmpty()) {
            Icon(
                painter = painterResource(id = com.sandorln.design.R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Colors.Gray400
            )
        } else {
            Text(
                modifier = Modifier.padding(end = Spacings.Spacing02),
                text = endTitle,
                style = TextStyles.Body01,
                color = Colors.Gray400
            )
        }
    }
}

@Preview
@Composable
fun BaseMenuBodyPreview() {
    GodadviceTheme {
        Surface {
            BaseMenuBody()
        }
    }
}
