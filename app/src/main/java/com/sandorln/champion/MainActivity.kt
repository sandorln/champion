package com.sandorln.champion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.LolChampionTheme
import com.sandorln.home.navigation.HomeScreenRoute
import com.sandorln.home.navigation.homeScreens
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LolChampionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Colors.Gray900
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = HomeScreenRoute) {
                        homeScreens()
                    }
                }
            }
        }
    }
}