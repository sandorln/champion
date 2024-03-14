package com.sandorln.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sandorln.home.ui.home.HomeScreen

const val HomeScreenRoute = "HomeScreenRoute"

fun NavGraphBuilder.homeScreens(
    moveToChampionDetailScreen: (championId: String, version: String) -> Unit,
    moveToLicensesScreen: () -> Unit,
    moveToLolPatchNoteScreen: () -> Unit
) {
    composable(
        route = HomeScreenRoute
    ) {
        HomeScreen(
            moveToChampionDetailScreen = moveToChampionDetailScreen,
            moveToLicensesScreen = moveToLicensesScreen,
            moveToLolPatchNoteScreen = moveToLolPatchNoteScreen
        )
    }
}