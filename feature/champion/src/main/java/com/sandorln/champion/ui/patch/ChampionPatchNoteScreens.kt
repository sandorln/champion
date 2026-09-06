package com.sandorln.champion.ui.patch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.champion.R
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.component.BaseToolbar
import com.sandorln.design.component.PatchNoteBody
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionPatchNoteListScreen(
    championPatchNoteListViewModel: ChampionPatchNoteListViewModel = hiltViewModel(),
    onBackStack: () -> Unit,
) {
    val uiState by championPatchNoteListViewModel.uiState.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PULL_HEIGHT
    )

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading)
            pullToRefreshState.endRefresh()
    }

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing)
            championPatchNoteListViewModel.sendAction(ChampionPatchNoteListAction.RefreshChampionPatchNoteList)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            BaseToolbar(
                onClickStartIcon = onBackStack,
                title = stringResource(id = R.string.champion_patch_note_title)
            )

            val currentTabIndex = PatchNoteTab.entries.indexOf(uiState.selectedTab)
            TabRow(
                selectedTabIndex = currentTabIndex,
                containerColor = Colors.Blue06,
                contentColor = Colors.Gold03,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[currentTabIndex]),
                        color = Colors.Gold03
                    )
                },
                divider = {
                    HorizontalDivider(color = Colors.Gray07)
                }
            ) {
                PatchNoteTab.entries.forEach { tab ->
                    val isSelected = uiState.selectedTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = {
                            championPatchNoteListViewModel.sendAction(ChampionPatchNoteListAction.ChangeTab(tab))
                        },
                        text = {
                            Text(
                                text = tab.title,
                                style = TextStyles.Body03,
                                color = if (isSelected) Colors.Gold03 else Colors.Gray04
                            )
                        }
                    )
                }
            }

            val currentList = uiState.currentPatchNoteList
            BaseLazyColumnWithPull(pullToRefreshState = pullToRefreshState) {
                item {
                    Spacer(modifier = Modifier.height(Spacings.Spacing05))
                }

                if (currentList != null && currentList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacings.Spacing08),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${uiState.selectedTab.title}에 대한 패치노트가 없습니다.",
                                style = TextStyles.Body03,
                                color = Colors.Gray04
                            )
                        }
                    }
                } else {
                    items(currentList?.size ?: 0) { index ->
                        val patchNote = currentList?.getOrNull(index) ?: return@items
                        PatchNoteBody(championPatchNote = patchNote)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
                }
            }
        }
    }
}