package com.sandorln.design.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.sandorln.design.R

@Composable
fun CheckBox(
    modifier: Modifier = Modifier,
    isSelect: Boolean = false
) {
    val checkBoxResId = if (isSelect) {
        R.drawable.ic_checkbox_fill
    } else {
        R.drawable.ic_checkbox_line
    }

    Icon(
        modifier = modifier,
        painter = painterResource(id = checkBoxResId),
        contentDescription = null,
        tint = Color.Unspecified
    )
}