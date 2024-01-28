package com.sandorln.home.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.champion.ui.home.ChampionHomeScreen
import com.sandorln.design.component.dialog.BaseBottomSheetDialog
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionTheme
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.home.ui.intro.IntroScreen
import com.sandorln.item.ui.home.ItemHomeScreen
import kotlinx.coroutines.launch
import com.sandorln.design.R as designR

sealed class HomeScreenType(val title: String, @DrawableRes val svgId: Int) {
    data object Champion : HomeScreenType("챔피온", designR.drawable.ic_main_champion)
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
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0) { homeItems.size }
    val isInitComplete by homeViewModel.isInitComplete.collectAsState()
    val uiState by homeViewModel.homeUiState.collectAsState()
    val hasNextVersion = uiState.nextVersionName.isNotEmpty()
    val hasPreVersion = uiState.preVersionName.isNotEmpty()
    val currentVersionName = uiState.currentVersionName

    Scaffold(
        bottomBar = {
            if (isInitComplete) {
                Column {
                    HorizontalDivider(
                        color = Colors.Gray08,
                        thickness = 1.dp
                    )
                    HomeBottomNavigation(
                        selectedIndex = pagerState.currentPage,
                        onSelectedIndex = { index ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }

            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (!isInitComplete) {
                IntroScreen()
            } else {
                HomeVersionChangeBar(
                    currentVersionName = currentVersionName,
                    hasNextVersion = hasNextVersion,
                    hasPreVersion = hasPreVersion,
                    onShowVersionChangeDialog = {
                        homeViewModel.sendAction(HomeAction.ChangeVisibleVersionChangeDialog(true))
                    },
                    onChangePreVersion = {
                        homeViewModel.sendAction(HomeAction.ChangePreVersion)
                    },
                    onChangeNextVersion = {
                        homeViewModel.sendAction(HomeAction.ChangeNextVersion)
                    }
                )
                HorizontalDivider(
                    color = Colors.Gray08,
                    thickness = 1.dp
                )
                HorizontalPager(
                    state = pagerState,
                    beyondBoundsPageCount = homeItems.size
                ) { page: Int ->
                    when (homeItems[page]) {
                        HomeScreenType.Champion -> ChampionHomeScreen(moveToChampionDetailScreen = {})

                        HomeScreenType.Item -> ItemHomeScreen()

                        HomeScreenType.SummonerSpell -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Magenta)
                        ) {

                        }

                        HomeScreenType.Setting -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Green)
                        ) {

                        }
                    }
                }
            }

            if (uiState.isShowVersionChangeDialog) {
                BaseBottomSheetDialog(
                    onDismissRequest = {
                        homeViewModel.sendAction(HomeAction.ChangeVisibleVersionChangeDialog(false))
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(
                            horizontal = Spacings.Spacing03,
                            vertical = Spacings.Spacing05
                        )
                    ) {
                        items(count = uiState.versionList.size) {
                            val version = uiState.versionList[it]
                            val championNameList = uiState.newSummaryChampionMap[version.name]?.map { champion -> champion.name } ?: emptyList()
                            Row(modifier = Modifier
                                .clickable {
                                    homeViewModel.sendAction(HomeAction.ChangeVersion(version.name))
                                }
                            ) {
                                Text(
                                    text = version.name,
                                    style = TextStyles.Body03
                                )
                                Spacer(modifier = Modifier.width(Spacings.Spacing00))
                                Text(
                                    text = championNameList.toString(),
                                    style = TextStyles.Body03,
                                    color = Color.Green
                                )
                                Spacer(modifier = Modifier.width(Spacings.Spacing00))
                                Text(
                                    text = version.newItemIdList?.size?.toString() ?: "0",
                                    style = TextStyles.Body03,
                                    color = Color.Magenta
                                )
                            }
                        }
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

@Composable
fun HomeVersionChangeBar(
    currentVersionName: String = "14.2.1",
    hasNextVersion: Boolean = true,
    hasPreVersion: Boolean = true,
    onShowVersionChangeDialog: () -> Unit = {},
    onChangePreVersion: () -> Unit = {},
    onChangeNextVersion: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Spacings.Spacing05,
                vertical = Spacings.Spacing03
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing04)
    ) {
        VersionButton(
            isEnable = hasPreVersion,
            title = "Prev",
            onClickListener = onChangePreVersion
        )
        ConstraintLayout(
            modifier = Modifier
                .clickable { onShowVersionChangeDialog.invoke() }
                .weight(1f),
        ) {
            val (versionRef, iconRef) = createRefs()
            createHorizontalChain(versionRef, iconRef, chainStyle = ChainStyle.Packed)

            Text(
                modifier = Modifier.constrainAs(versionRef) {
                    start.linkTo(parent.start)
                    end.linkTo(iconRef.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.preferredWrapContent
                },
                text = "$currentVersionName ver",
                textAlign = TextAlign.Center,
                style = TextStyles.SubTitle01,
                color = Colors.Blue03,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                modifier = Modifier
                    .padding(start = Spacings.Spacing01)
                    .size(IconSize.LargeSize)
                    .constrainAs(iconRef) {
                        start.linkTo(versionRef.end)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                painter = painterResource(id = com.sandorln.design.R.drawable.ic_chevron_down),
                contentDescription = null,
                tint = Colors.Blue03
            )
        }
        VersionButton(
            isEnable = hasNextVersion,
            title = "Next",
            onClickListener = onChangeNextVersion
        )
    }
}

@Composable
internal fun VersionButton(
    isEnable: Boolean = false,
    title: String = "",
    onClickListener: () -> Unit = {}
) {
    val buttonColor by animateColorAsState(
        targetValue = if (isEnable) Colors.Blue03 else Colors.Gray05,
        label = ""
    )
    Text(
        modifier = Modifier
            .clickable { onClickListener.invoke() }
            .background(
                color = buttonColor,
                shape = RoundedCornerShape(Radius.Radius06)
            )
            .padding(
                horizontal = Spacings.Spacing04,
                vertical = 2.dp
            ),
        text = title,
        style = TextStyles.SubTitle02,
        color = Colors.BasicBlack
    )
}


@Preview
@Composable
internal fun HomeVersionChangeBarPreview() {
    LolChampionTheme {
        Surface {
            HomeVersionChangeBar()
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