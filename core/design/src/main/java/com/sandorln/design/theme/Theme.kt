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
    background = Colors.Gray900,
    onBackground = Colors.Gray900,
    surface = Colors.Gray900,
    onSurface = Colors.Gray900,
    outlineVariant = Colors.Gray800
)

@Composable
fun LolChampionTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Colors.Gray900.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme,
        content = content
    )
}