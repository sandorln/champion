package com.sandorln.design.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val darkColorScheme = darkColorScheme(
    onPrimary = Colors.BasicWhite,
    background = Colors.Gray09,
    onBackground = Colors.Gray09,
    surface = Colors.Gray09,
    onSurface = Colors.Gray09,
    outlineVariant = Colors.Gray08
)

@Composable
fun LolChampionTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Colors.Gray09.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme,
        content = content
    )
}