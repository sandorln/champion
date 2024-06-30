package com.sandorln.design.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val darkColorScheme = darkColorScheme(
    onPrimary = Colors.BaseColor,
    background = Colors.Blue06,
    onBackground = Colors.Blue06,
    surface = Colors.Blue06,
    onSurface = Colors.Blue06,
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
            window.statusBarColor = Colors.Blue07.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme,
        content = content
    )
}

@Composable
fun LolChampionThemePreview(
    content: @Composable () -> Unit
) {
    LolChampionTheme {
        Surface(contentColor = Colors.Blue06) {
            content.invoke()
        }
    }
}