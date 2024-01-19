package com.sandorln.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sandorln.home.ui.home.HomeScreen

const val HomeScreenRoute = "HomeScreenRoute"

fun NavGraphBuilder.homeScreens() {
    composable(
        route = HomeScreenRoute
    ) {
        HomeScreen()
    }
}