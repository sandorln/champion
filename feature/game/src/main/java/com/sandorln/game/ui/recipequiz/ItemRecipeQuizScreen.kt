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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import com.sandorln.design.component.BaseBitmapImage
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
    val previousRound = remember(recipeQuizViewModel.previousAnswerList.size) {
        recipeQuizViewModel.previousAnswerList
    }

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
                        previousRound = previousRound,
                        onAddLeaf = { recipeQuizViewModel.sendAction(ItemRecipeQuizAction.AddLeafItem(it)) },
                        onRemoveLeaf = { recipeQuizViewModel.sendAction(ItemRecipeQuizAction.RemoveLeafItem(it)) },
                        onRemoveLeafAtIndex = { recipeQuizViewModel.sendAction(ItemRecipeQuizAction.RemoveLeafItemAtIndex(it)) },
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
    previousRound: List<Boolean>,
    onAddLeaf: (ItemData) -> Unit,
    onRemoveLeaf: (ItemData) -> Unit,
    onRemoveLeafAtIndex: (Int) -> Unit,
    onClearCart: () -> Unit,
    onSubmitCraft: () -> Unit
) {
    val round = uiState.currentRound
    val targetItem = round.targetItem
    val totalRequiredCount = round.totalRequiredCount
    val currentSelectedCount = uiState.userCartList.size

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Section (Fixed): Target Item Card & Full-Width Selected Leaf Items Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = Spacings.Spacing01),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
        ) {

            // Target Item Card (with standard horizontal padding)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacings.Spacing03)
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
                    }
                    val isExceeded = currentSelectedCount > totalRequiredCount
                    val isComplete = currentSelectedCount == totalRequiredCount
                    val warningRed = Color(0xFFE84057)

                    Box(
                        modifier = Modifier
                            .background(
                                if (isExceeded) warningRed.copy(alpha = 0.2f) else Colors.Blue06,
                                RoundedCornerShape(Radius.Radius02)
                            )
                            .then(
                                if (isExceeded) {
                                    Modifier.border(1.dp, warningRed, RoundedCornerShape(Radius.Radius02))
                                } else Modifier
                            )
                            .padding(horizontal = Spacings.Spacing02, vertical = Spacings.Spacing01)
                    ) {
                        Text(
                            text = "$currentSelectedCount / $totalRequiredCount",
                            style = TextStyles.SubTitle02,
                            color = when {
                                isExceeded -> warningRed
                                isComplete -> Colors.Gold02
                                else -> Colors.Gray03
                            }
                        )
                    }
                }
            }

            // Selected Leaf Items Row (No border, full width scrollable, preserving addition order)
            val selectedLeafList = uiState.userCartList
            val emptySlotCount = maxOf(0, totalRequiredCount - selectedLeafList.size)
            val selectedListState = rememberLazyListState()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Full-width horizontal scroll with edge fading (alpha gradient for scrollable indication)
                LazyRow(
                    state = selectedListState,
                    modifier = Modifier
                        .weight(1f)
                        .horizontalFadingEdge(
                            startFade = selectedListState.canScrollBackward,
                            endFade = selectedListState.canScrollForward,
                            fadeWidth = 24.dp
                        ),
                    contentPadding = PaddingValues(start = Spacings.Spacing03, end = Spacings.Spacing02, top = 6.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filled slots (selected items in chronological addition order)
                    itemsIndexed(selectedLeafList) { index, item ->
                        Box(
                            modifier = Modifier
                                .size(width = 42.dp, height = 42.dp)
                                .clickable { onRemoveLeafAtIndex(index) }
                        ) {
                            BaseRectangleIconImage(
                                modifier = Modifier
                                    .size(36.dp)
                                    .align(Alignment.BottomStart),
                                serverIconType = ServerIconType.ITEM,
                                versionName = item.version,
                                id = item.id
                            )
                            // Clear 'x' badge at top-right within safe bounds (never clipped)
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .align(Alignment.TopEnd)
                                    .background(Colors.Gray09, CircleShape)
                                    .border(1.dp, Colors.Gold03, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    modifier = Modifier.size(8.dp),
                                    painter = painterResource(id = DesignR.drawable.ic_clear),
                                    contentDescription = "제거",
                                    tint = Colors.Gold02
                                )
                            }
                        }
                    }

                    // Empty skeleton slots
                    items(emptySlotCount) {
                        Box(
                            modifier = Modifier.size(width = 42.dp, height = 42.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .align(Alignment.BottomStart)
                                    .background(Colors.Blue07.copy(alpha = 0.5f), RoundedCornerShape(Radius.Radius01))
                                    .border(1.dp, Colors.Gray06.copy(alpha = 0.35f), RoundedCornerShape(Radius.Radius01)),
                                contentAlignment = Alignment.Center
                            ) {
                                BaseBitmapImage(
                                    bitmap = null,
                                    loadingDrawableId = DesignR.drawable.ic_main_item,
                                    imageSize = 28.dp,
                                    innerPadding = Spacings.Spacing01
                                )
                            }
                        }
                    }
                }

                // Circle Icon Button for Reset (Fixed on the right with Spacings.Spacing03 margin)
                Box(
                    modifier = Modifier
                        .padding(end = Spacings.Spacing03, start = Spacings.Spacing01)
                        .size(32.dp)
                        .background(
                            if (currentSelectedCount > 0) Colors.Blue07 else Colors.Blue07.copy(alpha = 0.4f),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            if (currentSelectedCount > 0) Colors.Gold03 else Colors.Gray06,
                            CircleShape
                        )
                        .clickable(enabled = currentSelectedCount > 0, onClick = onClearCart),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        painter = painterResource(id = DesignR.drawable.ic_refresh),
                        contentDescription = "전체 초기화",
                        tint = if (currentSelectedCount > 0) Colors.Gold02 else Colors.Gray05
                    )
                }
            }
        }

        // 2. Middle Section (Scrollable): 3-Column LazyVerticalGrid with Fading Edge
        val candidates = round.candidateLeafItems
        val gridState = rememberLazyGridState()

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = gridState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = Spacings.Spacing03)
                .verticalFadingEdge(
                    topFade = gridState.canScrollBackward,
                    bottomFade = gridState.canScrollForward
                ),
            contentPadding = PaddingValues(
                top = Spacings.Spacing03,
                bottom = Spacings.Spacing04
            ),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing02),
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
        ) {
            items(candidates, key = { it.id }) { item ->
                val count = uiState.userCart[item] ?: 0
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (count > 0) Colors.Blue05 else Colors.Blue06,
                            RoundedCornerShape(Radius.Radius02)
                        )
                        .border(
                            1.dp,
                            if (count > 0) Colors.Gold03 else Colors.Gray07,
                            RoundedCornerShape(Radius.Radius02)
                        )
                        .padding(vertical = Spacings.Spacing02, horizontal = Spacings.Spacing01),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier.clickable { onAddLeaf(item) }
                        ) {
                            BaseRectangleIconImage(
                                modifier = Modifier.size(40.dp),
                                serverIconType = ServerIconType.ITEM,
                                versionName = item.version,
                                id = item.id
                            )
                        }
                        Text(
                            text = item.name,
                            style = TextStyles.Body04,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
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

                        // [-] count [+] enlarged buttons for easy clicking
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        if (count > 0) Colors.Blue07 else Colors.Blue07.copy(alpha = 0.4f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        0.5.dp,
                                        if (count > 0) Colors.Gray05 else Colors.Gray07,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable(enabled = count > 0) { onRemoveLeaf(item) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "-",
                                    style = TextStyles.SubTitle02,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (count > 0) Colors.Gold02 else Colors.Gray06
                                )
                            }

                            Text(
                                text = "$count",
                                style = TextStyles.SubTitle02,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (count > 0) Colors.Gold02 else Colors.Gray04
                            )

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Colors.Blue07, RoundedCornerShape(6.dp))
                                    .border(0.5.dp, Colors.Gray05, RoundedCornerShape(6.dp))
                                    .clickable { onAddLeaf(item) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+",
                                    style = TextStyles.SubTitle02,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Colors.Gold02
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Bottom Section (Fixed): Round status (InitialQuiz style) + "조합 완성" Action Button
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider()

            RecipeRoundBody(
                previousRound = previousRound,
                totalRoundCount = uiState.totalRoundCount
            )

            val canCraft = currentSelectedCount > 0 && currentSelectedCount <= totalRequiredCount
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
}

@Composable
fun RecipeRoundBody(
    previousRound: List<Boolean>,
    totalRoundCount: Int
) {
    val horizontalState = rememberScrollState()
    Row(
        modifier = Modifier
            .padding(vertical = Spacings.Spacing01)
            .fillMaxWidth()
            .horizontalScroll(state = horizontalState),
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02, Alignment.CenterHorizontally)
    ) {
        Spacer(modifier = Modifier.width(Spacings.Spacing02))
        repeat(totalRoundCount) { index ->
            val isSubmit = previousRound.getOrNull(index)
            val iconId: Int
            val tintColor: Color

            when (isSubmit) {
                true -> {
                    iconId = DesignR.drawable.ic_done
                    tintColor = Colors.Green00
                }

                false -> {
                    iconId = DesignR.drawable.ic_clear
                    tintColor = Colors.Orange00
                }

                else -> {
                    iconId = DesignR.drawable.ic_question
                    tintColor = Colors.Gray06
                }
            }
            Icon(
                modifier = Modifier.size(IconSize.MediumSize),
                painter = painterResource(id = iconId),
                tint = tintColor,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(Spacings.Spacing02))
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
                    .heightIn(max = 260.dp)
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
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = TextStyles.SubTitle03,
                                color = Colors.Gold04,
                                fontWeight = FontWeight.Bold
                            )
                            BaseRectangleIconImage(
                                modifier = Modifier.size(28.dp),
                                serverIconType = ServerIconType.ITEM,
                                versionName = round.targetItem.version,
                                id = round.targetItem.id
                            )
                            Text(
                                text = round.targetItem.name,
                                style = TextStyles.SubTitle03,
                                color = Colors.BasicWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(Spacings.Spacing02))
                        Text(
                            text = if (round.isCorrect) "정답 (${round.chainType.name})" else "실패",
                            style = TextStyles.Body03,
                            color = if (round.isCorrect) Colors.Gold02 else Color(0xFFE84057).copy(alpha = 0.9f),
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

fun Modifier.verticalFadingEdge(
    topFade: Boolean,
    bottomFade: Boolean,
    fadeHeight: Dp = 20.dp
): Modifier = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        val fadeHeightPx = fadeHeight.toPx()
        if (topFade && size.height > fadeHeightPx) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = 0f,
                    endY = fadeHeightPx
                ),
                blendMode = BlendMode.DstIn
            )
        }
        if (bottomFade && size.height > fadeHeightPx) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    startY = size.height - fadeHeightPx,
                    endY = size.height
                ),
                blendMode = BlendMode.DstIn
            )
        }
    }

fun Modifier.horizontalFadingEdge(
    startFade: Boolean,
    endFade: Boolean,
    fadeWidth: Dp = 24.dp
): Modifier = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        val fadeWidthPx = fadeWidth.toPx()
        if (startFade && size.width > fadeWidthPx) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startX = 0f,
                    endX = fadeWidthPx
                ),
                blendMode = BlendMode.DstIn
            )
        }
        if (endFade && size.width > fadeWidthPx) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    startX = size.width - fadeWidthPx,
                    endX = size.width
                ),
                blendMode = BlendMode.DstIn
            )
        }
    }
