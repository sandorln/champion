package com.sandorln.champion.navigation

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

fun NavGraphBuilder.championScreens() {
    composable(route = "$ChampionDetailRoute/{${BundleKeys.CHAMPION_ID}}/{${BundleKeys.CHAMPION_VERSION}}",
        arguments = listOf(
            navArgument(BundleKeys.CHAMPION_ID) { type = NavType.StringType },
            navArgument(BundleKeys.CHAMPION_VERSION) { type = NavType.StringType }
        )
    ) {
        ChampionDetailScreen()
    }
}