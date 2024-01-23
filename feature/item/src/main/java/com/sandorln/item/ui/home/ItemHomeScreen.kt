package com.sandorln.item.ui.home

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.component.BaseTextEditor
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.model.data.item.ItemData
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ItemHomeScreen(
    itemHomeViewModel: ItemHomeViewModel = hiltViewModel()
) {
    val currentItemList by itemHomeViewModel.displayItemList.collectAsState()
    val currentSpriteMap by itemHomeViewModel.currentSpriteMap.collectAsState()
    val uiState by itemHomeViewModel.itemUiState.collectAsState()

    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PullHeight
    )

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading)
            pullToRefreshState.endRefresh()
    }

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing)
            itemHomeViewModel.sendAction(ItemHomeAction.RefreshItemData)
    }

    BoxWithConstraints {
        val spanCount = floor(this.maxWidth / IconSize.XXLargeSize).toInt()
        val chunkItemList = currentItemList.chunked(spanCount)
        BaseLazyColumnWithPull(
            pullToRefreshState = pullToRefreshState
        ) {
            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Colors.Blue06.copy(alpha = 1f),
                                    Colors.Blue06.copy(alpha = 0.0f)
                                ),
                                startY = Spacings.Spacing08.value
                            )
                        )
                ) {
                    BaseTextEditor(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = Spacings.Spacing05,
                                vertical = Spacings.Spacing03
                            ),
                        text = uiState.searchKeyword,
                        hint = "아이템 검색",
                        onChangeTextListener = { search ->
                            val action = ItemHomeAction.ChangeItemSearchKeyword(search)
                            itemHomeViewModel.sendAction(action)
                        }
                    )
                }
            }

            items(chunkItemList.size) { columnIndex ->
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(spanCount) { rowIndex ->
                        val item = runCatching {
                            chunkItemList[columnIndex][rowIndex]
                        }.fold(
                            onFailure = { null },
                            onSuccess = { it }
                        )

                        ItemIconBody(
                            item = item,
                            currentSpriteMap = currentSpriteMap
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemIconBody(
    item: ItemData? = null,
    currentSpriteMap: Map<String, Bitmap?> = emptyMap()
) {
    Column(
        modifier = Modifier.width(IconSize.XXLargeSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (item != null) {
            val bitmap = item.image.getImageBitmap(currentSpriteMap)
            if (bitmap != null) {
                Image(
                    modifier = Modifier.size(IconSize.XXLargeSize),
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(IconSize.XXLargeSize)
                )
            }

            Text(
                modifier = Modifier.padding(vertical = 1.dp),
                text = item.name,
                style = TextStyles.Body03.copy(fontSize = 8.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = Colors.Gold02
            )
        }
    }
}

@Preview
@Composable
fun ItemIconBodyPreview() {
    LolChampionThemePreview {
        ItemIconBody()
    }
}