package com.sandorln.design.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.LolChampionTheme
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.TextStyles

@Composable
fun BaseButton(
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = TextStyles.Title03,
    title: String = "",
    titleColor : Color = Colors.Gray900,
    pinContainerColor: Color? = null,
    isEnabled: Boolean = false,
    onClickBtn: () -> Unit = {}
) {
    TextButton(
        enabled = isEnabled,
        modifier = modifier.heightIn(min = 50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = pinContainerColor ?: Colors.Green300,
            disabledContainerColor = pinContainerColor ?: Colors.Gray600,
        ),
        onClick = onClickBtn,
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(Radius.Radius04)
    ) {
        Text(
            text = title,
            style = titleStyle,
            color = titleColor
        )
    }
}

@Composable
@Preview
fun BaseButtonPreview() {
    LolChampionTheme {
        BaseButton()
    }
}