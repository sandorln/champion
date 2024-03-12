package com.sandorln.spell.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.component.BaseBitmapImage
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellHomeScreen(
    spellHomeViewModel: SpellHomeViewModel = hiltViewModel()
) {
    val uiState by spellHomeViewModel.uiState.collectAsState()
    val spellList by spellHomeViewModel.currentSpellList.collectAsState()
    val spellSpriteBitmap by spellHomeViewModel.currentSpriteMap.collectAsState()
    val selectedSpell = uiState.selectedSpell

    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PULL_HEIGHT
    )

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading)
            pullToRefreshState.endRefresh()
    }

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing)
            spellHomeViewModel.sendAction(SpellHomeAction.RefreshSpellData)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val spanCount = floor(this.maxWidth / IconSize.XXLargeSize).toInt()
        val chunkSpellList = spellList.chunked(spanCount)
        BaseLazyColumnWithPull(
            pullToRefreshState = pullToRefreshState
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(
                            horizontal = Spacings.Spacing08,
                            vertical = Spacings.Spacing05
                        )
                        .heightIn(min = Dimens.SELECT_SPELL_HEIGHT_MIN),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
                ) {
                    Spacer(modifier = Modifier.height(Spacings.Spacing02))
                    if (selectedSpell != null) {
                        BaseBitmapImage(
                            bitmap = selectedSpell.image.getImageBitmap(spellSpriteBitmap)
                        )

                        Text(
                            text = selectedSpell.name,
                            style = TextStyles.SubTitle02,
                            color = Colors.Gold02
                        )

                        Text(
                            text = "쿨타임 ${selectedSpell.cooldownBurn} 초",
                            style = TextStyles.Body03,
                            color = Colors.Gray02
                        )

                        Text(
                            text = selectedSpell.description,
                            style = TextStyles.Body03,
                            color = Colors.Gray02
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillParentMaxHeight()
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "보고 싶은 주문을 선택해주세요",
                                style = TextStyles.SubTitle02,
                                color = Colors.Gray02
                            )
                        }
                    }
                }
            }

            items(chunkSpellList.size) { columnIndex ->
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(spanCount) { rowIndex ->
                        val spell = runCatching {
                            chunkSpellList[columnIndex][rowIndex]
                        }.getOrNull()

                        if (spell != null) {
                            val isSelectedSpell = selectedSpell == null || selectedSpell.id == spell.id

                            Box(
                                modifier = Modifier.size(IconSize.XXLargeSize)
                            ) {
                                BaseBitmapImage(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable {
                                            spellHomeViewModel.sendAction(SpellHomeAction.SelectSpell(spell))
                                        },
                                    bitmap = spell.image.getImageBitmap(spellSpriteBitmap)
                                )

                                if (!isSelectedSpell) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(Colors.Gray09.copy(alpha = 0.8f))
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.width(IconSize.XXLargeSize))
                        }
                    }
                }
            }
        }
    }
}