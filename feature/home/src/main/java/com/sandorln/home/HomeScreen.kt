package com.sandorln.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionTheme
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import kotlinx.coroutines.launch
import com.sandorln.design.R as designR

sealed class HomeScreenType(val title: String, @DrawableRes val svgId: Int) {
    data object Champion : HomeScreenType("챔피언", designR.drawable.ic_main_champion)
    data object Item : HomeScreenType("아이템", designR.drawable.ic_main_item)
    data object SummonerSpell : HomeScreenType("스펠", designR.drawable.ic_main_spell)
    data object Setting : HomeScreenType("설정", designR.drawable.ic_main_special)
}

private val homeItems = listOf(
    HomeScreenType.Champion,
    HomeScreenType.Item,
    HomeScreenType.SummonerSpell,
    HomeScreenType.Setting
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen() {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0) { homeItems.size }

    Scaffold(
        bottomBar = {
            HomeBottomNavigation(
                selectedIndex = pagerState.currentPage,
                onSelectedIndex = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            HorizontalPager(state = pagerState) { page: Int ->
                when (homeItems[page]) {
                    HomeScreenType.Champion -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Blue)
                    ) {

                    }

                    HomeScreenType.Item -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red)
                    ) {

                    }

                    HomeScreenType.Setting -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Green)
                    ) {

                    }

                    HomeScreenType.SummonerSpell -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Magenta)
                    ) {

                    }
                }
            }
        }
    }
}

@Composable
internal fun HomeBottomNavigation(
    selectedIndex: Int = 0,
    onSelectedIndex: (Int) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
            color = Colors.Gray08,
            thickness = 1.dp
        )

        BottomNavigation(
            backgroundColor = Colors.Blue06
        ) {
            homeItems.forEachIndexed { index, homeScreenType ->
                val isSelected = selectedIndex == index
                BottomNavigationItem(
                    selected = isSelected,
                    onClick = {
                        onSelectedIndex.invoke(index)
                    },
                    icon = {
                        Icon(
                            modifier = Modifier
                                .padding(bottom = Spacings.Spacing00)
                                .size(IconSize.LargeSize),
                            painter = painterResource(id = homeScreenType.svgId),
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(
                            text = homeScreenType.title,
                            style = TextStyles
                                .Body03
                                .copy(color = Color.Unspecified)
                        )
                    },
                    alwaysShowLabel = false,
                    selectedContentColor = Colors.BaseColor,
                    unselectedContentColor = Colors.Gray07
                )
            }
        }
    }
}


@Preview
@Composable
internal fun HomeBottomNavigationPreview() {
    LolChampionTheme {
        Surface {
            HomeBottomNavigation()
        }
    }
}


