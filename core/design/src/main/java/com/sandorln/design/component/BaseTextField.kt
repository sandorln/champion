package com.sandorln.design.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.GodadviceTheme
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles

@Composable
fun BaseTextEditor(
    modifier: Modifier = Modifier,
    text: String = "",
    textStyle: TextStyle = TextStyles.Body01,
    hint: String = "",
    isShowRounderLine: Boolean = false,
    onChangeTextListener: (String) -> Unit = {}
) {
    var textFocus by remember {
        mutableStateOf(false)
    }
    val borderColor = if (textFocus) {
        Colors.Green300
    } else {
        Colors.Gray500
    }

    Box(
        modifier = modifier
            .heightIn(min = if (isShowRounderLine) 46.dp else 0.dp)
            .onFocusChanged {
                textFocus = it.isFocused
            }
            .run {
                if (isShowRounderLine) {
                    heightIn(min = 46.dp)
                        .border(
                            width = 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(Spacings.Spacing03)
                        )
                        .padding(
                            horizontal = Spacings.Spacing04,
                            vertical = Spacings.Spacing03
                        )
                } else
                    this
            }
    ) {
        BasicTextField(
            modifier = Modifier.align(Alignment.CenterStart),
            value = text,
            cursorBrush = SolidColor(Colors.Green300),
            onValueChange = onChangeTextListener,
            textStyle = textStyle,
            decorationBox = { innerTextField ->
                if (text.isEmpty())
                    Text(
                        text = hint,
                        color = Colors.Gray500,
                        style = textStyle,
                    )
                innerTextField.invoke()
            }
        )
    }
}

@Preview
@Composable
fun BaseTextEditorPreview() {
    GodadviceTheme {
        Surface {
            BaseTextEditor(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}