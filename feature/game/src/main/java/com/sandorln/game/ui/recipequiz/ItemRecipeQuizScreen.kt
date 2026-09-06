package com.sandorln.game.ui.recipequiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.R as DesignR
import com.sandorln.design.component.BaseRectangleIconImage
import com.sandorln.design.component.BaseToolbar
import com.sandorln.design.component.ServerIconType
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.design.theme.addShadow
import com.sandorln.design.util.thousandDotDecimalFormat
import com.sandorln.game.ui.initialquiz.ChainType
import com.sandorln.model.data.item.ItemData
import kotlin.math.ceil

@Composable
fun ItemRecipeQuizScreen(
    recipeQuizViewModel: ItemRecipeQuizViewModel = hiltViewModel(),
    onBackStack: () -> Unit
) {
    val uiState by recipeQuizViewModel.uiState.collectAsState()
    val gameTime by recipeQuizViewModel.gameTime.collectAsState()
    val readyTime by recipeQuizViewModel.readyTime.collectAsState()

    val onGameDialogDismissListener: () -> Unit = {
        recipeQuizViewModel.sendAction(ItemRecipeQuizAction.CloseGameDialog)
        onBackStack.invoke()
    }

    val timeProgressColor by remember(gameTime) {
        val progress = (gameTime / ItemRecipeQuizViewModel.INIT_GAME_TIME).coerceIn(0f, 1f)
        val color = lerp(Color.Red, Color.Green, progress)
        mutableStateOf(color)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                BaseToolbar(onClickStartIcon = onBackStack)
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    progress = { (gameTime / ItemRecipeQuizViewModel.INIT_GAME_TIME).coerceIn(0f, 1f) },
                    color = timeProgressColor
                )
            }

            if (readyTime <= 0) {
                ItemRecipeQuizBody(
                    modifier = Modifier.weight(1f),
                    uiState = uiState,
                    onAddLeaf = { recipeQuizViewModel.sendAction(ItemRecipeQuizAction.AddLeafItem(it)) },
                    onRemoveLeaf = { recipeQuizViewModel.sendAction(ItemRecipeQuizAction.RemoveLeafItem(it)) },
                    onClearCart = { recipeQuizViewModel.sendAction(ItemRecipeQuizAction.ClearCart) },
                    onSubmitCraft = { recipeQuizViewModel.sendAction(ItemRecipeQuizAction.SubmitCraft) }
                )
            }

            if (uiState.isGameEnd) {
                Dialog(onDismissRequest = onGameDialogDismissListener) {
                    RecipeGameEndDialogBody(
                        score = uiState.score,
                        previousRoundList = recipeQuizViewModel.previousRoundList,
                        onDismissListener = onGameDialogDismissListener
                    )
                }
            }

            if (readyTime > 0) {
                Dialog(
                    onDismissRequest = {},
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    )
                ) {
                    ReadyTimeDialogBody(readyTime)
                }
            }
        }
    }
}

@Composable
private fun ItemRecipeQuizBody(
    modifier: Modifier = Modifier,
    uiState: ItemRecipeQuizUiState,
    onAddLeaf: (ItemData) -> Unit,
    onRemoveLeaf: (ItemData) -> Unit,
    onClearCart: () -> Unit,
    onSubmitCraft: () -> Unit
) {
    val round = uiState.currentRound
    val targetItem = round.targetItem
    val totalRequiredCount = round.totalRequiredCount
    val currentSelectedCount = uiState.userCart.values.sum()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacings.Spacing03, vertical = Spacings.Spacing01),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Round header & Score
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ROUND ${uiState.currentRoundIndex} / ${uiState.totalRoundCount}",
                style = TextStyles.SubTitle02,
                color = Colors.Gold02
            )
            Text(
                text = "${thousandDotDecimalFormat.format(uiState.score)}점",
                style = TextStyles.Title02,
                color = Colors.Gray01
            )
        }

        // Target Item Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Colors.Blue05, RoundedCornerShape(Radius.Radius03))
                .border(1.dp, Colors.Gold04, RoundedCornerShape(Radius.Radius03))
                .padding(Spacings.Spacing03)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing03)
            ) {
                BaseRectangleIconImage(
                    modifier = Modifier.size(54.dp),
                    serverIconType = ServerIconType.ITEM,
                    versionName = targetItem.version,
                    id = targetItem.id
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = targetItem.name,
                        style = TextStyles.SubTitle01,
                        color = Colors.BasicWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "가격: ${targetItem.gold.total}G",
                        style = TextStyles.Body03,
                        color = Colors.Gold03
                    )
                    Text(
                        text = "필요 최소 재료: ${totalRequiredCount}개",
                        style = TextStyles.Body04,
                        color = Colors.Gray03
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Colors.Blue06, RoundedCornerShape(Radius.Radius02))
                        .padding(horizontal = Spacings.Spacing02, vertical = Spacings.Spacing01)
                ) {
                    Text(
                        text = "$currentSelectedCount / $totalRequiredCount",
                        style = TextStyles.SubTitle02,
                        color = if (currentSelectedCount == totalRequiredCount) Colors.Gold02 else Colors.Gray03
                    )
                }
            }
        }

        // Cart / Selected items area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Colors.Blue07, RoundedCornerShape(Radius.Radius02))
                .border(1.dp, Colors.Gray07, RoundedCornerShape(Radius.Radius02))
                .padding(Spacings.Spacing02)
        ) {
            Text(
                text = "내 조합 장바구니 (클릭 시 수량 차감)",
                style = TextStyles.Body04,
                color = Colors.Gray04
            )
            Spacer(modifier = Modifier.height(Spacings.Spacing01))

            if (uiState.userCart.isEmpty()) {
                Text(
                    modifier = Modifier.padding(vertical = Spacings.Spacing02),
                    text = "아래에서 기초 재료를 눌러 필요한 수량을 담으세요.",
                    style = TextStyles.Body04,
                    color = Colors.Gray05
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
                ) {
                    uiState.userCart.forEach { (item, count) ->
                        Row(
                            modifier = Modifier
                                .background(Colors.Blue05, RoundedCornerShape(Radius.Radius02))
                                .border(1.dp, Colors.Gold05, RoundedCornerShape(Radius.Radius02))
                                .clickable { onRemoveLeaf(item) }
                                .padding(horizontal = Spacings.Spacing02, vertical = Spacings.Spacing01),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
                        ) {
                            BaseRectangleIconImage(
                                modifier = Modifier.size(24.dp),
                                serverIconType = ServerIconType.ITEM,
                                versionName = item.version,
                                id = item.id
                            )
                            Text(
                                text = item.name,
                                style = TextStyles.Body04,
                                color = Colors.BasicWhite,
                                maxLines = 1
                            )
                            Text(
                                text = "x$count",
                                style = TextStyles.SubTitle03,
                                color = Colors.Gold02,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                modifier = Modifier.size(IconSize.SmallSize),
                                painter = painterResource(id = DesignR.drawable.ic_clear),
                                contentDescription = null,
                                tint = Colors.Gray04
                            )
                        }
                    }
                }
            }
        }

        // Candidate Leaf Items (2 rows x 4 items)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
        ) {
            Text(
                text = "최소 기초 재료 후보",
                style = TextStyles.Body04,
                color = Colors.Gray04
            )

            val candidates = round.candidateLeafItems
            val chunked = candidates.chunked(4)

            chunked.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
                ) {
                    rowItems.forEach { item ->
                        val count = uiState.userCart[item] ?: 0
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (count > 0) Colors.Blue05 else Colors.Blue06,
                                    RoundedCornerShape(Radius.Radius02)
                                )
                                .border(
                                    1.dp,
                                    if (count > 0) Colors.Gold03 else Colors.Gray07,
                                    RoundedCornerShape(Radius.Radius02)
                                )
                                .clickable { onAddLeaf(item) }
                                .padding(vertical = Spacings.Spacing01),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                BaseRectangleIconImage(
                                    modifier = Modifier.size(36.dp),
                                    serverIconType = ServerIconType.ITEM,
                                    versionName = item.version,
                                    id = item.id
                                )
                                Text(
                                    text = item.name,
                                    style = TextStyles.Body04,
                                    fontSize = 11.sp,
                                    color = Colors.BasicWhite,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${item.gold.total}G",
                                    style = TextStyles.Body04,
                                    fontSize = 10.sp,
                                    color = Colors.Gold04
                                )
                            }

                            if (count > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(2.dp)
                                        .size(18.dp)
                                        .background(Colors.Gold02, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$count",
                                        style = TextStyles.Body04,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Colors.Blue06
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacings.Spacing01),
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Colors.Gray07, RoundedCornerShape(Radius.Radius03))
                    .clickable(onClick = onClearCart)
                    .padding(vertical = Spacings.Spacing03),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "초기화",
                    style = TextStyles.SubTitle02,
                    color = Colors.Gray03
                )
            }

            val canCraft = currentSelectedCount > 0
            Box(
                modifier = Modifier
                    .weight(2f)
                    .background(
                        if (canCraft) Colors.Gold02 else Colors.Gray07,
                        RoundedCornerShape(Radius.Radius03)
                    )
                    .clickable(enabled = canCraft, onClick = onSubmitCraft)
                    .padding(vertical = Spacings.Spacing03),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✨ 조합 완성",
                    style = TextStyles.SubTitle02,
                    color = if (canCraft) Colors.Blue06 else Colors.Gray05,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ReadyTimeDialogBody(readyTime: Float) {
    val progress = (readyTime / ItemRecipeQuizViewModel.INIT_READY_TIME).coerceIn(0f, 1f)
    val progressColor by remember(readyTime) {
        val color = lerp(Color.Red, Color.Green, progress)
        mutableStateOf(color)
    }

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(Dimens.GAME_READY_TIME_PROGRESS),
            progress = { progress },
            color = progressColor,
            trackColor = Colors.Gray05,
            strokeWidth = 2.dp
        )
        Text(
            text = ceil(readyTime).toInt().toString(),
            style = TextStyles.Title01.copy(fontSize = 124.sp).addShadow(),
            color = Colors.BasicWhite
        )
    }
}

@Composable
private fun RecipeGameEndDialogBody(
    score: Long = 0,
    previousRoundList: List<RecipeRoundResult> = emptyList(),
    onDismissListener: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colors.Blue06, RoundedCornerShape(Radius.Radius03))
            .padding(Spacings.Spacing03),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03)
    ) {
        Text(
            text = "조합 퀴즈 결과",
            style = TextStyles.Title03,
            color = Colors.BasicWhite
        )

        HorizontalDivider(color = Colors.Gray07)

        Text(
            text = thousandDotDecimalFormat.format(score.coerceIn(0, 999999)),
            style = TextStyles.Title01,
            fontSize = 32.sp,
            color = Colors.Gold02
        )

        if (previousRoundList.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
            ) {
                previousRoundList.forEachIndexed { index, round ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Colors.Blue05, RoundedCornerShape(Radius.Radius02))
                            .padding(Spacings.Spacing02),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
                        ) {
                            BaseRectangleIconImage(
                                modifier = Modifier.size(28.dp),
                                serverIconType = ServerIconType.ITEM,
                                versionName = round.targetItem.version,
                                id = round.targetItem.id
                            )
                            Text(
                                text = round.targetItem.name,
                                style = TextStyles.SubTitle03,
                                color = Colors.BasicWhite
                            )
                        }
                        Text(
                            text = if (round.isCorrect) "정답 (${round.chainType.name})" else "실패",
                            style = TextStyles.Body03,
                            color = if (round.isCorrect) Colors.Gold02 else Colors.Gray04,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.clickable(onClick = onDismissListener),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(IconSize.MediumSize),
                painter = painterResource(id = DesignR.drawable.ic_clear),
                contentDescription = null,
                tint = Colors.Gray03
            )
            Spacer(modifier = Modifier.width(Spacings.Spacing01))
            Text(
                text = "닫기",
                style = TextStyles.Title04,
                color = Colors.Gray03
            )
        }
    }
}
