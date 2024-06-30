package com.sandorln.game.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.R
import com.sandorln.design.theme.AnimationConfig
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.design.util.thousandDotDecimalFormat

@Composable
fun GameHomeScreen(
    moveToInitialQuizScreen: () -> Unit,
    gameHomeViewModel: GameHomeViewModel = hiltViewModel()
) {

    val score by gameHomeViewModel.initialGameScore.collectAsState()
    val rank by gameHomeViewModel.initialGameRank.collectAsState()
    val refreshRemainingTime by gameHomeViewModel.remainingRankRefreshTime.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(state = rememberScrollState())
    ) {
        InitialGameRankingBody(
            score = score,
            rank = rank,
            refreshRemainingTime = refreshRemainingTime,
            onClickInitialRankRefreshBtn = gameHomeViewModel::refreshGameRank,
            onClickInitialGameStart = moveToInitialQuizScreen
        )
    }
}

private val INITIAL_GAME_TITLE = listOf("ㅊ", "ㅅ", "ㄱ", "ㅇ")

@Composable
fun InitialGameRankingBody(
    score: Long,
    rank: Int?,
    refreshRemainingTime: Long,
    onClickInitialRankRefreshBtn: () -> Unit,
    onClickInitialGameStart: () -> Unit
) {
    val rankText = when {
        rank == null -> "?? 위"
        rank > 30 -> "+30 위"
        else -> "$rank 위"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val btnOffset by infiniteTransition.animateFloat(
        initialValue = 0.dp.value,
        targetValue = -Spacings.Spacing00.value,
        animationSpec = infiniteRepeatable(
            animation = tween(AnimationConfig.FAST, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacings.Spacing07),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
        ) {
            InitialGameTitleBody()
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "점수",
                color = Colors.Gray04,
                style = TextStyles.SubTitle03
            )
            Text(
                text = thousandDotDecimalFormat.format(score),
                style = TextStyles.Title02,
                color = Colors.Gray01
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.padding(horizontal = Spacings.Spacing05),
                horizontalArrangement = Arrangement.spacedBy(
                    Spacings.Spacing02,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = rankText,
                    style = TextStyles.Title01
                )
                Icon(
                    modifier = Modifier
                        .size(IconSize.LargeSize)
                        .background(
                            Colors.Gray06,
                            CircleShape
                        )
                        .clickable(onClick = onClickInitialRankRefreshBtn),
                    painter = painterResource(id = R.drawable.ic_refresh),
                    contentDescription = null,
                    tint = Colors.BasicWhite
                )
            }

            Text(
                text = if (refreshRemainingTime > 0) "${refreshRemainingTime}초 후 갱신이 가능합니다" else "",
                style = TextStyles.Body04,
                color = Colors.Gray05
            )
        }


        TextButton(
            modifier = Modifier.offset(y = btnOffset.dp),
            shape = RoundedCornerShape(Radius.Radius09),
            border = BorderStroke(1.dp, Colors.BaseColor),
            onClick = onClickInitialGameStart,
        ) {
            Text(
                text = "GAME START",
                style = TextStyles.SubTitle01,
                color = Colors.Gold02
            )
        }
    }
}

@Composable
fun InitialGameTitleBody() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
    ) {
        INITIAL_GAME_TITLE.forEach {
            Box(
                modifier = Modifier
                    .background(
                        color = Colors.Gray07,
                        shape = RoundedCornerShape(Radius.Radius03)
                    )
                    .border(
                        width = 1.dp,
                        color = Colors.Gold04,
                        shape = RoundedCornerShape(Radius.Radius03)
                    )
                    .size(Dimens.INITIAL_GAME_TITLE_SIZE)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = it,
                    style = TextStyles.Title02
                )
            }
        }
    }
}

@Preview
@Composable
fun InitialGameRankingBodyPreview() {
    LolChampionThemePreview {
        InitialGameRankingBody(
            score = 10000,
            rank = null,
            refreshRemainingTime = 0,
            onClickInitialRankRefreshBtn = {},
            onClickInitialGameStart = {}
        )
    }
}