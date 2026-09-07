package com.sandorln.game.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import kotlinx.coroutines.delay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import com.sandorln.design.R
import com.sandorln.design.component.BaseRectangleIconImage
import com.sandorln.design.component.ServerIconType
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
    moveToItemRecipeQuizScreen: () -> Unit = {},
    gameHomeViewModel: GameHomeViewModel = hiltViewModel()
) {
    val score by gameHomeViewModel.initialGameScore.collectAsState()
    val rank by gameHomeViewModel.initialGameRank.collectAsState()
    val refreshRemainingTime by gameHomeViewModel.remainingRankRefreshTime.collectAsState()
    val versionName by gameHomeViewModel.currentVersionName.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(state = rememberScrollState())
            .padding(vertical = Spacings.Spacing04),
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing05)
    ) {
        // Game 1: Initial Quiz
        InitialGameRankingBody(
            score = score,
            rank = rank,
            refreshRemainingTime = refreshRemainingTime,
            onClickInitialRankRefreshBtn = gameHomeViewModel::refreshGameRank,
            onClickInitialGameStart = moveToInitialQuizScreen
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = Spacings.Spacing04),
            color = Colors.Gray08,
            thickness = 1.dp
        )

        // Game 2: Item Recipe Quiz (신규)
        RecipeGameHubBody(
            versionName = versionName,
            onClickRecipeGameStart = moveToItemRecipeQuizScreen
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
            .padding(vertical = Spacings.Spacing03),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
        ) {
            InitialGameTitleBody(INITIAL_GAME_TITLE)
        }

        Text(
            text = "초성으로 아이템 이름을 빠르게 맞추세요!",
            style = TextStyles.Body03,
            color = Colors.Gray04
        )

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
                text = "초성 게임 시작",
                style = TextStyles.SubTitle01,
                color = Colors.Gold02
            )
        }
    }
}

@Composable
fun RecipeGameHubBody(
    versionName: String = "14.13.1",
    onClickRecipeGameStart: () -> Unit
) {
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
            .padding(vertical = Spacings.Spacing03),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03)
    ) {
        RecipeGameTitleBody(versionName = versionName)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
        ) {
            Box(
                modifier = Modifier
                    .background(Colors.Gold02, RoundedCornerShape(Radius.Radius01))
                    .padding(horizontal = Spacings.Spacing01, vertical = 2.dp)
            ) {
                Text(
                    text = "NEW ✨",
                    style = TextStyles.Body04,
                    color = Colors.Blue06,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "아이템 조합식 퀴즈",
                style = TextStyles.SubTitle01,
                color = Colors.BasicWhite
            )
        }

        Text(
            text = "완성 아이템에 필요한 최소 기초 재료와 수량을 맞추세요!",
            style = TextStyles.Body03,
            color = Colors.Gray04
        )

        Spacer(modifier = Modifier.height(Spacings.Spacing01))

        TextButton(
            modifier = Modifier.offset(y = btnOffset.dp),
            shape = RoundedCornerShape(Radius.Radius09),
            border = BorderStroke(1.dp, Colors.Gold04),
            onClick = onClickRecipeGameStart,
        ) {
            Text(
                text = "조합 게임 시작",
                style = TextStyles.SubTitle01,
                color = Colors.Gold02
            )
        }
    }
}

data class RecipeSlotItem(
    val leaf1Id: String,
    val leaf2Id: String,
    val resultId: String
)

private val RECIPE_SLOT_ITEMS = listOf(
    RecipeSlotItem("1036", "1036", "3134"), // 롱소드 + 롱소드 = 톱날단검
    RecipeSlotItem("1052", "1052", "3108"), // 증폭의 고서 + 증폭의 고서 = 악마의 마법서
    RecipeSlotItem("1028", "1036", "3044"), // 루비 수정 + 롱소드 = 탐식의 망치
    RecipeSlotItem("1029", "1029", "3082"), // 천 갑옷 + 천 갑옷 = 파수꾼의 갑옷
    RecipeSlotItem("1027", "1052", "3802"), // 사파이어 수정 + 증폭의 고서 = 사라진 양피지
    RecipeSlotItem("1042", "1036", "3133"), // 단검 + 롱소드 = 콜필드의 전투망치
    RecipeSlotItem("1038", "1037", "3031")  // B.F. 대검 + 곡괭이 = 무한의 대검
)

@Composable
fun RecipeGameTitleBody(versionName: String = "14.13.1") {
    val infiniteTransition = rememberInfiniteTransition(label = "fadeQuestion")
    val questionAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "questionAlpha"
    )

    var currentSlotIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            currentSlotIndex = (currentSlotIndex + 1) % RECIPE_SLOT_ITEMS.size
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
    ) {
        // Leaf 1 Slot (Fixed frame, inner image scrolls)
        RecipeSlotBox {
            AnimatedContent(
                targetState = currentSlotIndex,
                transitionSpec = {
                    (slideInVertically(animationSpec = tween(400)) { height -> -height } + fadeIn(tween(400)))
                        .togetherWith(slideOutVertically(animationSpec = tween(400)) { height -> height } + fadeOut(tween(400)))
                },
                label = "leaf1SlotMachine"
            ) { index ->
                val item = RECIPE_SLOT_ITEMS[index]
                RecipeLeafSlotContent(
                    itemId = item.leaf1Id,
                    versionName = versionName,
                    questionAlpha = questionAlpha
                )
            }
        }

        Text(
            text = "+",
            style = TextStyles.Title02,
            fontWeight = FontWeight.Bold,
            color = Colors.Gold02
        )

        // Leaf 2 Slot (Fixed frame, inner image scrolls)
        RecipeSlotBox {
            AnimatedContent(
                targetState = currentSlotIndex,
                transitionSpec = {
                    (slideInVertically(animationSpec = tween(400)) { height -> -height } + fadeIn(tween(400)))
                        .togetherWith(slideOutVertically(animationSpec = tween(400)) { height -> height } + fadeOut(tween(400)))
                },
                label = "leaf2SlotMachine"
            ) { index ->
                val item = RECIPE_SLOT_ITEMS[index]
                RecipeLeafSlotContent(
                    itemId = item.leaf2Id,
                    versionName = versionName,
                    questionAlpha = questionAlpha
                )
            }
        }

        Text(
            text = "=",
            style = TextStyles.Title02,
            fontWeight = FontWeight.Bold,
            color = Colors.Gold02
        )

        // Result Item Slot (Fixed frame, inner image scrolls)
        RecipeSlotBox {
            AnimatedContent(
                targetState = currentSlotIndex,
                transitionSpec = {
                    (slideInVertically(animationSpec = tween(400)) { height -> -height } + fadeIn(tween(400)))
                        .togetherWith(slideOutVertically(animationSpec = tween(400)) { height -> height } + fadeOut(tween(400)))
                },
                label = "resultSlotMachine"
            ) { index ->
                val item = RECIPE_SLOT_ITEMS[index]
                Box(
                    modifier = Modifier.size(Dimens.INITIAL_GAME_TITLE_SIZE - 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BaseRectangleIconImage(
                        modifier = Modifier.fillMaxSize(),
                        serverIconType = ServerIconType.ITEM,
                        versionName = versionName,
                        id = item.resultId
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeSlotBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(Dimens.INITIAL_GAME_TITLE_SIZE)
            .background(
                color = Colors.Gray07,
                shape = RoundedCornerShape(Radius.Radius03)
            )
            .border(
                width = 1.dp,
                color = Colors.Gold04,
                shape = RoundedCornerShape(Radius.Radius03)
            )
            .clip(RoundedCornerShape(Radius.Radius03)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun RecipeLeafSlotContent(
    itemId: String,
    versionName: String,
    questionAlpha: Float
) {
    Box(
        modifier = Modifier.size(Dimens.INITIAL_GAME_TITLE_SIZE - 4.dp),
        contentAlignment = Alignment.Center
    ) {
        BaseRectangleIconImage(
            modifier = Modifier.fillMaxSize(),
            serverIconType = ServerIconType.ITEM,
            versionName = versionName,
            id = itemId
        )
        // Gray background with white '?' fade in/out
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.Gray08.copy(alpha = questionAlpha * 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "?",
                style = TextStyles.Title02,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = questionAlpha)
            )
        }
    }
}


@Composable
fun InitialGameTitleBody(titles: List<String> = INITIAL_GAME_TITLE) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
    ) {
        titles.forEach {
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