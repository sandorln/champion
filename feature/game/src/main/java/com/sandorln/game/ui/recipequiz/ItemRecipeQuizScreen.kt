package com.sandorln.game.ui.recipequiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.delay
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

    LaunchedEffect(uiState.craftAnimation) {
        if (uiState.craftAnimation != null) {
            delay(600)
            recipeQuizViewModel.sendAction(ItemRecipeQuizAction.DismissCraftAnimation)
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
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
            }

            // Central Craft Animation Overlay (0.5s fade in / out)
            AnimatedVisibility(
                visible = uiState.craftAnimation != null,
                enter = fadeIn(tween(150)) + scaleIn(tween(150), initialScale = 0.85f),
                exit = fadeOut(tween(200)) + scaleOut(tween(200), targetScale = 1.05f),
                modifier = Modifier.align(Alignment.Center)
            ) {
                CraftResultOverlay(craftAnimation = uiState.craftAnimation)
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
private fun CraftResultOverlay(craftAnimation: CraftAnimationType?) {
    if (craftAnimation == null) return
    val isSuccess = craftAnimation == CraftAnimationType.SUCCESS

    val failColor = Color(0xFFE84057)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Colors.Blue06.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(Radius.Radius04),
        border = BorderStroke(
            2.dp,
            if (isSuccess) Colors.Gold02 else failColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 48.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(
                    if (isSuccess) DesignR.drawable.ic_craft_success else DesignR.drawable.ic_craft_fail
                ),
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(Spacings.Spacing03))

            Text(
                text = if (isSuccess) "SUCCESS" else "FAILED",
                style = TextStyles.Title01.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = if (isSuccess) Colors.Gold02 else failColor
            )

            Spacer(modifier = Modifier.height(Spacings.Spacing01))

            Text(
                text = if (isSuccess) "조합 성공!" else "조합 실패...",
                style = TextStyles.Body03,
                fontWeight = FontWeight.SemiBold,
                color = Colors.BasicWhite.copy(alpha = 0.85f)
            )
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
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Scrollable Top-Aligned Content (Round Header, Target Item, Candidate Header & Grid)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacings.Spacing03, vertical = Spacings.Spacing01),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
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

            // Candidate Leaf Items Header (Larger text + [전체 초기화] button on far right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "최소 기초 재료 후보",
                    style = TextStyles.SubTitle01,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Colors.BasicWhite
                )
                Row(
                    modifier = Modifier
                        .background(Colors.Blue06, RoundedCornerShape(Radius.Radius01))
                        .border(1.dp, Colors.Gray06, RoundedCornerShape(Radius.Radius01))
                        .clickable(onClick = onClearCart)
                        .padding(horizontal = Spacings.Spacing02, vertical = Spacings.Spacing01),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        painter = painterResource(id = DesignR.drawable.ic_refresh),
                        contentDescription = null,
                        tint = Colors.Gold03
                    )
                    Text(
                        text = "전체 초기화",
                        style = TextStyles.Body04,
                        fontWeight = FontWeight.SemiBold,
                        color = Colors.Gold03
                    )
                }
            }

            // Candidate Leaf Items (4 rows x 4 items = 16 slots, padded with blank slots if < 16)
            val candidates = round.candidateLeafItems
            val paddedSlots: List<ItemData?> = (0 until 16).map { candidates.getOrNull(it) }
            val chunked = paddedSlots.chunked(4)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
            ) {
                chunked.forEach { rowSlots ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
                    ) {
                        rowSlots.forEach { item ->
                            if (item != null) {
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
                                        .padding(vertical = 4.dp, horizontal = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.clickable { onAddLeaf(item) }
                                        ) {
                                            BaseRectangleIconImage(
                                                modifier = Modifier.size(34.dp),
                                                serverIconType = ServerIconType.ITEM,
                                                versionName = item.version,
                                                id = item.id
                                            )
                                        }
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
                                            fontSize = 9.sp,
                                            color = Colors.Gold04
                                        )

                                        // [-] count [+] direct controls with wide spacing
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 4.dp, vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .background(
                                                        if (count > 0) Colors.Blue07 else Colors.Blue07.copy(alpha = 0.4f),
                                                        RoundedCornerShape(4.dp)
                                                    )
                                                    .border(
                                                        0.5.dp,
                                                        if (count > 0) Colors.Gray05 else Colors.Gray07,
                                                        RoundedCornerShape(4.dp)
                                                    )
                                                    .clickable(enabled = count > 0) { onRemoveLeaf(item) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "-",
                                                    style = TextStyles.SubTitle03,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (count > 0) Colors.Gold02 else Colors.Gray06
                                                )
                                            }

                                            Text(
                                                text = "$count",
                                                style = TextStyles.Body04,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (count > 0) Colors.Gold02 else Colors.Gray04
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .background(Colors.Blue07, RoundedCornerShape(4.dp))
                                                    .border(0.5.dp, Colors.Gray05, RoundedCornerShape(4.dp))
                                                    .clickable { onAddLeaf(item) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "+",
                                                    style = TextStyles.SubTitle03,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Colors.Gold02
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                // 빈칸 슬롯 (Placeholder when fewer than 16 items)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(84.dp)
                                        .background(Colors.Blue07.copy(alpha = 0.2f), RoundedCornerShape(Radius.Radius02))
                                        .border(1.dp, Colors.Gray07.copy(alpha = 0.2f), RoundedCornerShape(Radius.Radius02))
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Button: "조합 완성" (Fixed at bottom)
        val canCraft = currentSelectedCount > 0
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacings.Spacing03, vertical = Spacings.Spacing02)
                .background(
                    if (canCraft) Colors.Gold02 else Colors.Gray07,
                    RoundedCornerShape(Radius.Radius03)
                )
                .clickable(enabled = canCraft, onClick = onSubmitCraft)
                .padding(vertical = Spacings.Spacing03),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "조합 완성",
                style = TextStyles.SubTitle01,
                color = if (canCraft) Colors.Blue06 else Colors.Gray05,
                fontWeight = FontWeight.Bold
            )
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
