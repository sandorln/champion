package com.sandorln.champion.tile

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.wear.compose.material.Colors

object MessagingTileTheme {
    val colors = composeColors.toTileColors()
}

/**
 * A Compose-based Colors object.
 *
 * This would typically be used in your Wear app too (and include more color overrides). Since it's
 * being used only for Tiles here, only the primary/surface colors are defined.
 */
private val composeColors = Colors(
    primary = ColorPalette.purple,
    onPrimary = ColorPalette.darkBlue,
    surface = ColorPalette.darkBlue,
    onSurface = ColorPalette.purple
)

private object ColorPalette {
    val purple = Color(0xFFC58AF9)
    val darkBlue = Color(0xFF202124)
}

private fun Colors.toTileColors() = androidx.wear.protolayout.material.Colors(
    /* primary = */
    primary.toArgb(),
    /* onPrimary = */
    onPrimary.toArgb(),
    /* surface = */
    surface.toArgb(),
    /* onSurface = */
    onSurface.toArgb()
)
