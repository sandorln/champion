package com.sandorln.game.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val GameHomeScreenRoute = "GameHomeScreenRoute"

fun NavController.moveToGameHome() {
    navigate(route = GameHomeScreenRoute)
}

fun NavGraphBuilder.gameScreens(
    onBackStack: () -> Unit
) {
    composable(route = GameHomeScreenRoute) {

    }
}