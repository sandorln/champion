package com.sandorln.game.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sandorln.game.ui.initialquiz.InitialQuizScreen

const val GameHomeScreenRoute = "GameHomeScreenRoute"
const val InitialQuizScreenRoute = "InitialQuizScreenRoute"

fun NavController.moveToGameHome() {
    navigate(route = GameHomeScreenRoute)
}

fun NavController.moveToInitialQuiz() {
    navigate(route = InitialQuizScreenRoute)
}

fun NavGraphBuilder.gameScreens(
    onBackStack: () -> Unit
) {
    composable(route = InitialQuizScreenRoute) {
        InitialQuizScreen(onBackStack = onBackStack)
    }
}