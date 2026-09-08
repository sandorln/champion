package com.sandorln.game.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sandorln.game.ui.initialquiz.InitialQuizScreen
import com.sandorln.game.ui.recipequiz.ItemRecipeQuizScreen

const val GameHomeScreenRoute = "GameHomeScreenRoute"
const val InitialQuizScreenRoute = "InitialQuizScreenRoute"
const val ItemRecipeQuizScreenRoute = "ItemRecipeQuizScreenRoute"

fun NavController.moveToGameHome() {
    navigate(route = GameHomeScreenRoute)
}

fun NavController.moveToInitialQuiz() {
    navigate(route = InitialQuizScreenRoute)
}

fun NavController.moveToItemRecipeQuiz() {
    navigate(route = ItemRecipeQuizScreenRoute)
}

fun NavGraphBuilder.gameScreens(
    onBackStack: () -> Unit
) {
    composable(route = InitialQuizScreenRoute) {
        InitialQuizScreen(onBackStack = onBackStack)
    }
    composable(route = ItemRecipeQuizScreenRoute) {
        ItemRecipeQuizScreen(onBackStack = onBackStack)
    }
}