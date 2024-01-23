package com.sandorln.champion.ui.home

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.sandorln.design.component.BaseChampionSplashImage
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.component.BaseTextEditor
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.model.data.champion.SummaryChampion
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChampionHomeScreen(
    championHomeViewModel: ChampionHomeViewModel = hiltViewModel(),
    moveToChampionDetailScreen: (championId: String) -> Unit
) {
    val currentChampionList by championHomeViewModel.displayChampionList.collectAsState()
    val currentSpriteMap by championHomeViewModel.currentSpriteMap.collectAsState()
    val uiState by championHomeViewModel.championUiState.collectAsState()

    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PullHeight
    )

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading)
            pullToRefreshState.endRefresh()
    }

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing)
            championHomeViewModel.sendAction(ChampionHomeAction.RefreshChampionData)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val spanCount = floor(this.maxWidth / IconSize.XXLargeSize).toInt()
        val chunkChampionList = currentChampionList.chunked(spanCount)
        BaseLazyColumnWithPull(
            pullToRefreshState = pullToRefreshState
        ) {
            item {
                FavoriteChampionListBody()
            }

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
                        hint = "챔피언 검색",
                        onChangeTextListener = { search ->
                            val action = ChampionHomeAction.ChangeChampionSearchKeyword(search)
                            championHomeViewModel.sendAction(action)
                        }
                    )
                }
            }

            items(chunkChampionList.size) { columnIndex ->
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(spanCount) { rowIndex ->
                        val champion = runCatching {
                            chunkChampionList[columnIndex][rowIndex]
                        }.getOrNull()

                        ChampionIconBody(
                            champion = champion,
                            currentSpriteMap = currentSpriteMap
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChampionIconBody(
    champion: SummaryChampion? = null,
    currentSpriteMap: Map<String, Bitmap?>
) {
    Column(
        modifier = Modifier.width(IconSize.XXLargeSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (champion != null) {
            val bitmap = champion.image.getImageBitmap(currentSpriteMap)
            if (bitmap != null) {
                Image(
                    modifier = Modifier.size(IconSize.XXLargeSize),
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            } else {
                CircularProgressIndicator(
                    color = Colors.BaseColor,
                    strokeWidth = 3.dp
                )
            }

            Text(
                modifier = Modifier.padding(vertical = 1.dp),
                text = champion.name,
                style = TextStyles.Body03.copy(fontSize = 8.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = Colors.Gold02
            )
        }
    }
}

@Composable
private fun FavoriteChampionListBody() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(
            Spacings.Spacing00,
            Alignment.CenterHorizontally
        )
    ) {
        FavoriteChampionBody()
        FavoriteChampionBody()
    }
}

@Composable
private fun FavoriteChampionBody(
    championId: String = "Aatrox",
) {
    Card(
        modifier = Modifier
            .widthIn(max = 80.dp)
            .aspectRatio(0.55f),
        shape = RoundedCornerShape(0),
        colors = CardDefaults.cardColors(containerColor = Colors.Blue05)
    ) {
        Box {
            BaseChampionSplashImage(championId = championId)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(Spacings.Spacing01)
            ) {
                Text(
                    text = "아트록스",
                    style = TextStyles.SubTitle03,
                    color = Colors.Gold03,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Preview
@Composable
internal fun ChampionHomeScreenPreview() {
    LolChampionThemePreview {
        ChampionHomeScreen {}
    }
}