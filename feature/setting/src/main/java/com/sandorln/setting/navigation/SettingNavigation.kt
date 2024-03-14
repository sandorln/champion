package com.sandorln.setting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sandorln.setting.ui.lolpatch.LolPatchNoteScreen

private const val LolPatchNoteRoute = "LolPatchNoteRoute"

fun NavController.moveToLolPatchNoteScreen() {
    navigate(
        route = LolPatchNoteRoute,
        navOptions = NavOptions
            .Builder()
            .setLaunchSingleTop(true)
            .build()
    )
}

fun NavGraphBuilder.settingScreens(
    onBackStack: () -> Unit,
) {
    composable(LolPatchNoteRoute) {
        LolPatchNoteScreen(
            onBackStack = onBackStack
        )
    }
}