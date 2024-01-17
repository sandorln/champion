package com.sandorln.design.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.LolChampionTheme
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings

@Composable
fun BaseBottomSheetDialog(
    onDismissRequest: () -> Unit = {},
    properties: BottomSheetDialogProperties = BottomSheetDialogProperties(),
    onBodyComposable: @Composable () -> Unit = {},
) {
    BottomSheetDialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Colors.Gray800,
                    shape = RoundedCornerShape(
                        topStart = Radius.Radius04,
                        topEnd = Radius.Radius04
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacings.Spacing02))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        color = Colors.Gray700,
                        shape = RoundedCornerShape(2.dp)
                    )
            )

            onBodyComposable.invoke()
        }
    }
}

@Preview
@Composable
fun BaseBottomSheetDialogPreview() {
    LolChampionTheme {
        Surface {
            BaseBottomSheetDialog()
        }
    }
}
