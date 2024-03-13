package com.sandorln.champion.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sandorln.champion.ui.detail.ChampionDetailScreen
import com.sandorln.model.keys.BundleKeys

private const val ChampionDetailRoute = "ChampionDetailRoute"

fun NavController.moveToChampionDetail(
    championId: String,
    version: String
) {
    navigate(route = "$ChampionDetailRoute/${championId}/${version}")
}

fun NavGraphBuilder.championScreens(
    onBackStack: () -> Unit,
    moveToChampionDetailScreen: (championId: String, version: String) -> Unit
) {
    composable(route = "$ChampionDetailRoute/{${BundleKeys.CHAMPION_ID}}/{${BundleKeys.CHAMPION_VERSION}}",
        arguments = listOf(
            navArgument(BundleKeys.CHAMPION_ID) { type = NavType.StringType },
            navArgument(BundleKeys.CHAMPION_VERSION) { type = NavType.StringType }
        ),
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(700)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(700)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(700)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(700)
            )
        }
    ) {
        ChampionDetailScreen(
            onBackStack = onBackStack,
            moveToChampionDetailScreen = moveToChampionDetailScreen
        )
    }
}