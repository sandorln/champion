package com.sandorln.champion

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.sandorln.champion.navigation.championScreens
import com.sandorln.champion.navigation.moveToChampionDetail
import com.sandorln.champion.navigation.moveToChampionPatchNoteList
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.LolChampionTheme
import com.sandorln.game.navigation.gameScreens
import com.sandorln.game.navigation.moveToInitialQuiz
import com.sandorln.home.navigation.HomeScreenRoute
import com.sandorln.home.navigation.homeScreens
import com.sandorln.setting.navigation.moveToLolPatchNoteScreen
import com.sandorln.setting.navigation.settingScreens
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val moveToLicensesScreen = {
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
        }

        setContent {
            LolChampionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Colors.Blue06
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = HomeScreenRoute) {
                        homeScreens(
                            moveToChampionDetailScreen = navController::moveToChampionDetail,
                            moveToLicensesScreen = moveToLicensesScreen,
                            moveToLolPatchNoteScreen = navController::moveToLolPatchNoteScreen,
                            moveToChampionPatchNoteListScreen = navController::moveToChampionPatchNoteList,
                            moveToInitialQuizScreen = navController::moveToInitialQuiz
                        )
                        championScreens(
                            onBackStack = navController::navigateUp,
                            moveToChampionDetailScreen = navController::moveToChampionDetail
                        )
                        settingScreens(
                            onBackStack = navController::navigateUp
                        )
                        gameScreens(
                            onBackStack = navController::navigateUp
                        )
                    }
                }
            }
        }
    }
}