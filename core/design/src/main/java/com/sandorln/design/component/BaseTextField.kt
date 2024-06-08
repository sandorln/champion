package com.sandorln.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
fun BaseSearchTextEditor(
    modifier: Modifier = Modifier,
    text: String = "",
    textStyle: TextStyle = TextStyles.Body01,
    hint: String = "",
    isShowRounderLine: Boolean = true,
    onChangeTextListener: (String) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var textFocus by remember {
        mutableStateOf(false)
    }
    val focusColor = if (textFocus) {
        Colors.BaseColor
    } else {
        Colors.Gray05
    }

    Row(
        modifier = modifier
            .heightIn(min = if (isShowRounderLine) 46.dp else 0.dp)
            .run {
                if (isShowRounderLine) {
                    border(
                        width = 1.dp,
                        color = focusColor,
                        shape = RoundedCornerShape(Radius.Radius02)
                    )
                        .background(
                            color = Colors.Blue06,
                            shape = RoundedCornerShape(Radius.Radius02)
                        )
                        .padding(
                            horizontal = Spacings.Spacing03,
                            vertical = Spacings.Spacing00
                        )
                } else {
                    this
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            modifier = Modifier
                .onFocusChanged { textFocus = it.isFocused }
                .weight(1f),
            value = text,
            cursorBrush = SolidColor(Colors.BaseColor),
            onValueChange = onChangeTextListener,
            maxLines = 1,
            keyboardActions = KeyboardActions { focusManager.clearFocus() },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            textStyle = textStyle,
            decorationBox = { innerTextField ->
                if (text.isEmpty())
                    Text(
                        text = hint,
                        color = Colors.Gray05,
                        style = textStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                innerTextField.invoke()
            }
        )

        Icon(
            modifier = Modifier.size(IconSize.LargeSize),
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = null,
            tint = focusColor
        )
    }
}

@Composable
fun BaseGameTextEditor(
    modifier: Modifier = Modifier,
    text: String = "",
    textStyle: TextStyle = TextStyles.Body01,
    hint: String = "정답 입력",
    onChangeTextListener: (String) -> Unit = {},
    onDoneActionListener: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var textFocus by remember {
        mutableStateOf(true)
    }
    val focusColor = if (textFocus) {
        Colors.BaseColor
    } else {
        Colors.Gray08
    }

    Row(
        modifier = modifier
            .heightIn(min = 46.dp)
            .background(
                color = Colors.Gray01,
                shape = RoundedCornerShape(Radius.Radius02)
            )
            .border(
                width = 1.dp,
                color = focusColor,
                shape = RoundedCornerShape(Radius.Radius02)
            )
            .padding(
                horizontal = Spacings.Spacing03,
                vertical = Spacings.Spacing00
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BasicTextField(
            modifier = Modifier
                .onFocusChanged { textFocus = it.isFocused }
                .weight(1f),
            value = text,
            cursorBrush = SolidColor(Colors.BaseColor),
            onValueChange = onChangeTextListener,
            maxLines = 1,
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
                onDoneActionListener.invoke()
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            textStyle = textStyle.copy(
                textAlign = TextAlign.Center,
                color = Colors.BaseColor
            ),
            decorationBox = { innerTextField ->
                if (text.isEmpty())
                    Text(
                        text = hint,
                        color = Colors.Gray05,
                        style = textStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                innerTextField.invoke()
            }
        )
    }
}

@Preview
@Composable
fun BaseSearchTextEditorPreview() {
    LolChampionThemePreview {
        BaseSearchTextEditor(
            hint = "챔피언 검색",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun BaseTextEditorPreview() {
    LolChampionThemePreview {
        BaseGameTextEditor(
            hint = "정답 입력",
            modifier = Modifier.fillMaxWidth()
        )
    }
}