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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.model.data.champion.SummaryChampion
import kotlinx.coroutines.delay
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChampionHomeScreen(
    championHomeViewModel: ChampionHomeViewModel = hiltViewModel(),
    moveToChampionDetailScreen: (championId: String) -> Unit
) {
    val currentChampionList by championHomeViewModel.currentChampionList.collectAsState()
    val currentSpriteMap by championHomeViewModel.currentSpriteMap.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PullHeight
    )

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        delay(1000)
        pullToRefreshState.endRefresh()
    }

    BoxWithConstraints {
        val spanCount = floor(this.maxWidth / IconSize.XXLargeSize).toInt()
        val chunkChampionList = currentChampionList.chunked(spanCount)
        BaseLazyColumnWithPull(
            pullToRefreshState = pullToRefreshState
        ) {
            item {
                FavoriteChampionListBody()
            }

            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Colors.BaseColor)
                ) {

                }
            }

            items(chunkChampionList.size) { columnIndex ->
                LazyRow {
                    items(spanCount) { rowIndex ->
                        val champion = runCatching {
                            chunkChampionList[columnIndex][rowIndex]
                        }.fold(
                            onFailure = { null },
                            onSuccess = { it }
                        )

                        ChampionIconBody(champion, currentSpriteMap)
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
            val imageInfo = champion.image
            val originalBitmap = currentSpriteMap[imageInfo.sprite] ?: return
            val bitmap = Bitmap.createBitmap(
                originalBitmap,
                imageInfo.x,
                imageInfo.y,
                imageInfo.w,
                imageInfo.h
            )
            Image(
                modifier = Modifier.size(IconSize.XXLargeSize),
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Text(
                modifier = Modifier
                    .background(color = Colors.BasicBlack)
                    .padding(vertical = 1.dp),
                text = champion.name,
                style = TextStyles.Body03.copy(fontSize = 8.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = Colors.Gray02
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
        backgroundColor = Colors.Blue05
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