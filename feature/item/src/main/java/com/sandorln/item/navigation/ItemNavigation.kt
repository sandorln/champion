package com.sandorln.item.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sandorln.item.ui.builder.edit.ItemBuilderEditScreen
import com.sandorln.item.ui.builder.list.ItemBuilderListScreen

private const val ItemBuilderListRoute = "ItemBuilderListRoute"
private const val ItemBuilderEditRoute = "ItemBuilderEditRoute"

fun NavController.moveToItemBuilderList() {
    navigate(route = ItemBuilderListRoute)
}

fun NavController.moveToItemBuilderEdit(buildId: Long = 0L) {
    navigate(route = "$ItemBuilderEditRoute?buildId=$buildId")
}

fun NavGraphBuilder.itemScreens(
    onBackStack: () -> Unit,
    moveToItemBuilderEdit: (buildId: Long) -> Unit
) {
    composable(
        route = ItemBuilderListRoute,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        }
    ) {
        ItemBuilderListScreen(
            onBackStack = onBackStack,
            moveToItemBuilderEdit = moveToItemBuilderEdit
        )
    }

    composable(
        route = "$ItemBuilderEditRoute?buildId={buildId}",
        arguments = listOf(
            navArgument("buildId") {
                type = NavType.LongType
                defaultValue = 0L
            }
        ),
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(500)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(500)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(500)
            )
        }
    ) {
        ItemBuilderEditScreen(
            onBackStack = onBackStack
        )
    }
}
