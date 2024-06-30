package com.sandorln.design.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sandorln.design.component.BaseButton
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.LolChampionTheme
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles

@Composable
fun BaseAlertTextDialog(
    title: String = "제목",
    body: String = "",
    submitColor: Color = Colors.BaseColor,
    submitTitleColor: Color = Colors.Gray09,
    cancelTitleColor: Color = Colors.BasicWhite,
    cancelColor: Color = Colors.Gray07,
    submitBtn: String = "네",
    cancelBtn: String = "아니요",
    onDismissRequest: () -> Unit = {},
    onClickCancel: () -> Unit = {},
    onClickSubmit: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .background(color = Colors.Gray08, shape = RoundedCornerShape(Radius.Radius06))
                .padding(
                    top = Spacings.Spacing07,
                    bottom = Spacings.Spacing05,
                    start = Spacings.Spacing05,
                    end = Spacings.Spacing05
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = TextStyles.Title02
            )

            if (body.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacings.Spacing03))
                Text(
                    text = body,
                    style = TextStyles.Body01,
                    color = Colors.Gray02
                )
            }

            Spacer(modifier = Modifier.height(Spacings.Spacing07))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
            ) {
                BaseButton(
                    modifier = Modifier
                        .height(42.dp)
                        .weight(1f),
                    title = cancelBtn,
                    isEnabled = true,
                    titleColor = cancelTitleColor,
                    pinContainerColor = cancelColor,
                    onClickBtn = onClickCancel
                )

                BaseButton(
                    modifier = Modifier
                        .height(42.dp)
                        .weight(1f),
                    title = submitBtn,
                    isEnabled = true,
                    titleColor = submitTitleColor,
                    pinContainerColor = submitColor,
                    onClickBtn = onClickSubmit
                )
            }
        }
    }
}

@Preview
@Composable
fun BaseAlertDialogPreview() {
    LolChampionTheme {
        Surface {
            BaseAlertTextDialog()
        }
    }
}