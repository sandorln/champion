package com.sandorln.home.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.champion.ui.home.ChampionHomeScreen
import com.sandorln.design.component.BaseCircleIconImage
import com.sandorln.design.component.CircleIconType
import com.sandorln.design.component.dialog.BaseBottomSheetDialog
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionTheme
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.home.ui.intro.IntroScreen
import com.sandorln.item.ui.home.ItemHomeScreen
import com.sandorln.model.data.version.Version
import com.sandorln.setting.ui.SettingHomeScreen
import com.sandorln.spell.ui.SpellHomeScreen
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
    homeViewModel: HomeViewModel = hiltViewModel(),
    moveToChampionDetailScreen: (championId: String, version: String) -> Unit,
    moveToLicensesScreen: () -> Unit
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
                        HomeScreenType.Champion -> ChampionHomeScreen(
                            moveToChampionDetailScreen = moveToChampionDetailScreen
                        )

                        HomeScreenType.Item -> ItemHomeScreen()

                        HomeScreenType.SummonerSpell -> SpellHomeScreen()

                        HomeScreenType.Setting -> SettingHomeScreen(
                            moveToLicensesScreen = moveToLicensesScreen
                        )
                    }
                }
            }

            if (uiState.isShowVersionChangeDialog) {
                BaseBottomSheetDialog(
                    onDismissRequest = {
                        homeViewModel.sendAction(HomeAction.ChangeVisibleVersionChangeDialog(false))
                    }
                ) {
                    LazyColumn {
                        items(count = uiState.versionList.size) {
                            val version = uiState.versionList[it]
                            val versionName = version.name

                            VersionItemBody(
                                version = version,
                                isSelectedVersion = versionName == uiState.currentVersionName,
                                onClickListener = {
                                    homeViewModel.sendAction(HomeAction.ChangeVersion(versionName))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VersionItemBody(
    version: Version,
    isSelectedVersion: Boolean = false,
    onClickListener: () -> Unit = {}
) {
    val newChampionIdList = version.newChampionIdList ?: emptyList()
    val newItemIdList = version.newItemIdList ?: emptyList()
    val backgroundColor = if (isSelectedVersion) Colors.Blue05 else Colors.Gray08
    Row(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .clickable { onClickListener.invoke() }
            .padding(
                horizontal = Spacings.Spacing05,
                vertical = Spacings.Spacing03
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
    ) {

        Text(
            modifier = Modifier.weight(1f),
            text = version.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyles.SubTitle01,
            color = if (isSelectedVersion) Colors.BaseColor else Colors.Gray04
        )

        if (newChampionIdList.isNotEmpty() || newItemIdList.isNotEmpty())
            Text(
                modifier = Modifier
                    .background(
                        color = Colors.Gold04,
                        shape = RoundedCornerShape(Radius.Radius04)
                    )
                    .padding(
                        horizontal = Spacings.Spacing00,
                        vertical = 1.dp
                    ),
                text = "NEW",
                style = TextStyles.Body04,
                color = Colors.Gray07
            )

        if (newChampionIdList.isNotEmpty())
            NewContentListBody(
                circleIconType = CircleIconType.CHAMPION,
                newIdList = newChampionIdList,
                versionName = version.name
            )

        if (newItemIdList.isNotEmpty())
            NewContentListBody(
                circleIconType = CircleIconType.ITEM,
                newIdList = newItemIdList,
                versionName = version.name
            )
    }
}

private const val MAX_COUNT = 3

@Composable
private fun NewContentListBody(
    circleIconType: CircleIconType = CircleIconType.CHAMPION,
    newIdList: List<String> = emptyList(),
    versionName: String = ""
) {
    val circleTypeIcon = when (circleIconType) {
        CircleIconType.ITEM -> com.sandorln.design.R.drawable.ic_main_item
        CircleIconType.CHAMPION -> com.sandorln.design.R.drawable.ic_main_champion
    }

    Box {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .background(Colors.Gold05, CircleShape)
                .size(IconSize.MediumSize)
                .padding(2.5.dp)
                .clip(CircleShape),
            painter = painterResource(id = circleTypeIcon),
            contentDescription = null,
            tint = Colors.Gray03
        )

        newIdList
            .take(MAX_COUNT)
            .forEachIndexed { index, id ->
                val startPadding = ((IconSize.LargeSize / 2) * index) + IconSize.MediumSize
                BaseCircleIconImage(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = startPadding)
                        .zIndex(index.toFloat() + 1)
                        .size(IconSize.LargeSize),
                    versionName = versionName,
                    id = id,
                    circleIconType = circleIconType
                )
            }

        if (newIdList.size > MAX_COUNT) {
            val startPadding = ((IconSize.LargeSize / 2) * (MAX_COUNT + 1)) + IconSize.MediumSize / 2
            val backgroundShape = RoundedCornerShape(
                topEnd = Radius.Radius04,
                bottomEnd = Radius.Radius04
            )
            Text(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(start = startPadding)
                    .background(
                        color = Colors.Blue06,
                        shape = backgroundShape
                    )
                    .border(
                        width = 1.dp,
                        color = Colors.Gold05,
                        shape = backgroundShape
                    )
                    .padding(
                        start = 10.dp,
                        end = Spacings.Spacing01
                    )
                    .zIndex(0f),
                text = "+${(newIdList.size - MAX_COUNT).coerceAtMost(99)}",
                style = TextStyles.Body04,
                color = Colors.Gold04
            )
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
                        style = TextStyles.Body03.copy(color = Color.Unspecified)
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
    currentVersionName: String = "",
    hasNextVersion: Boolean = true,
    hasPreVersion: Boolean = true,
    onShowVersionChangeDialog: () -> Unit = {},
    onChangePreVersion: () -> Unit = {},
    onChangeNextVersion: () -> Unit = {}
) {
    val versionColor by animateColorAsState(
        targetValue = if (hasNextVersion) Colors.Gray02 else Colors.BaseColor,
        label = ""
    )

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
                color = versionColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                modifier = Modifier
                    .padding(start = Spacings.Spacing01)
                    .size(IconSize.MediumSize)
                    .constrainAs(iconRef) {
                        start.linkTo(versionRef.end)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                painter = painterResource(id = com.sandorln.design.R.drawable.ic_chevron_down),
                contentDescription = null,
                tint = versionColor
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
        targetValue = if (isEnable) Colors.BaseColor else Colors.Gray05,
        label = ""
    )

    Text(
        modifier = Modifier
            .clickable { onClickListener.invoke() }
            .border(
                width = 1.dp,
                color = buttonColor,
                shape = RoundedCornerShape(Radius.Radius06)
            )
            .padding(
                horizontal = Spacings.Spacing04,
                vertical = 2.dp
            ),
        text = title,
        style = TextStyles.SubTitle02,
        color = buttonColor
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

@Preview
@Composable
fun VersionItemBodyPreview() {
    LolChampionThemePreview {
        VersionItemBody(
            version = Version()
        )
    }
}