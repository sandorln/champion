package com.sandorln.game.ui.initialquiz

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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.component.BaseCircleIconImage
import com.sandorln.design.component.BaseGameTextEditor
import com.sandorln.design.component.BaseRectangleIconImage
import com.sandorln.design.component.BaseToolbar
import com.sandorln.design.component.ServerIconType
import com.sandorln.design.component.html.LolHtmlTagTextView
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.design.theme.addShadow
import com.sandorln.design.util.thousandDotDecimalFormat
import com.sandorln.game.util.getInitialHangul
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.type.ItemTagType
import java.util.Stack
import kotlin.math.ceil
import com.sandorln.design.R as DesignR

@Composable
fun InitialQuizScreen(
    initialQuizViewModel: InitialQuizViewModel = hiltViewModel(),
    onBackStack: () -> Unit
) {
    val uiState by initialQuizViewModel.uiState.collectAsState()
    val gameTime by initialQuizViewModel.gameTime.collectAsState()
    val readyTime by initialQuizViewModel.readyTime.collectAsState()
    val previousRound = remember(initialQuizViewModel.previousAnswerList.size) {
        initialQuizViewModel.previousAnswerList
    }

    val onGameDialogDismissListener: () -> Unit = {
        initialQuizViewModel.sendAction(InitialQuizAction.CloseGameDialog)
        onBackStack.invoke()
    }

    val timeProgressColor by remember(gameTime) {
        val progress = (gameTime / InitialQuizViewModel.INIT_GAME_TIME).coerceIn(0f, 1f)
        val color = lerp(Color.Red, Color.Green, progress)
        mutableStateOf(color)
    }

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
                progress = { gameTime / InitialQuizViewModel.INIT_GAME_TIME },
                color = timeProgressColor
            )
        }

        if (readyTime <= 0)
            InitialQuizGameBody(
                modifier = Modifier.weight(1f),
                item = uiState.itemData
            )

        InitialInputBody(
            previousRound = previousRound,
            totalRoundCount = initialQuizViewModel.totalRoundCount,
            onDoneAnswer = {
                initialQuizViewModel.sendAction(InitialQuizAction.InitialQuizDone(it))
            }
        )

        if (uiState.isGameEnd) {
            Dialog(onDismissRequest = onGameDialogDismissListener) {
                GameEndDialogBody(
                    score = uiState.score,
                    previousItemList = initialQuizViewModel.previousItemList,
                    onDismissListener = onGameDialogDismissListener
                )
            }
        }

        if (readyTime > 0) {
            Dialog(
                onDismissRequest = { },
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

@Composable
private fun ReadyTimeDialogBody(
    readyTime: Float
) {
    val progress = (readyTime / InitialQuizViewModel.INIT_READY_TIME).coerceIn(0f, 1f)
    val progressColor by remember(readyTime) {
        val color = lerp(Color.Red, Color.Green, progress)
        mutableStateOf(color)
    }

    Box {
        CircularProgressIndicator(
            modifier = Modifier
                .size(Dimens.GAME_READY_TIME_PROGRESS)
                .align(Alignment.Center),
            progress = { progress },
            color = progressColor,
            trackColor = Colors.Gray05,
            strokeWidth = 2.dp
        )
        Text(
            modifier = Modifier
                .offset(y = 6.dp)
                .align(Alignment.Center),
            text = ceil(readyTime).toInt().toString(),
            style = TextStyles
                .Title01
                .copy(fontSize = 124.sp)
                .addShadow(),
            color = Colors.Gray05
        )
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = ceil(readyTime).toInt().toString(),
            style = TextStyles
                .Title01
                .copy(fontSize = 124.sp)
                .addShadow()
        )
    }
}

@Composable
private fun GameEndDialogBody(
    score: Long = 0,
    previousItemList: List<Triple<ChainType, ItemData, String>> = emptyList(),
    onDismissListener: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = Colors.Blue06,
                    shape = RoundedCornerShape(Radius.Radius02)
                )
                .padding(vertical = Spacings.Spacing03),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03)
        ) {
            Text(
                text = "점수표",
                style = TextStyles.Title03
            )

            HorizontalDivider()

            Text(
                text = thousandDotDecimalFormat.format(score.coerceIn(0, 999999)),
                style = TextStyles.Title01,
                fontSize = 32.sp,
                color = Colors.Gold02
            )
        }

        if (previousItemList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(Spacings.Spacing00))

            GameEndDialogPreviousItemBody(
                previousItemList = previousItemList
            )
        }

        Spacer(modifier = Modifier.height(Spacings.Spacing00))

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
            Text(
                text = "닫기",
                style = TextStyles.Title04,
                color = Colors.Gray03
            )
        }
    }
}

@Composable
private fun GameEndDialogPreviousItemBody(
    previousItemList: List<Triple<ChainType, ItemData, String>> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Colors.Blue06,
                shape = RoundedCornerShape(Radius.Radius02)
            )
            .padding(vertical = Spacings.Spacing01)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(space = Spacings.Spacing00)
    ) {
        previousItemList.forEachIndexed { index, (chainType, item, answer) ->
            if (index > 0)
                HorizontalDivider()

            Row(
                modifier = Modifier.padding(horizontal = Spacings.Spacing02),
                horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconId: Int
                val tintColor: Color
                val isSubmit = chainType != ChainType.FAIL

                if (isSubmit) {
                    iconId = DesignR.drawable.ic_done
                    tintColor = Colors.Green00
                } else {
                    iconId = DesignR.drawable.ic_clear
                    tintColor = Colors.Orange00
                }
                Box(modifier = Modifier.size(IconSize.XLargeSize)) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(IconSize.MediumSize),
                        painter = painterResource(id = iconId),
                        contentDescription = null,
                        tint = tintColor
                    )
                    Text(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        text = chainType.name,
                        style = TextStyles.Body04,
                        fontSize = 4.sp,
                        color = tintColor
                    )
                }
                BaseCircleIconImage(
                    modifier = Modifier.size(IconSize.LargeSize),
                    serverIconType = ServerIconType.ITEM,
                    versionName = item.version,
                    id = item.id
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.name,
                    style = TextStyles.Body03,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = answer,
                    style = TextStyles.Body03,
                    color = if (isSubmit) Colors.Green00 else Colors.Gray05,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun InitialInputBody(
    previousRound: List<Boolean>,
    totalRoundCount: Int,
    onDoneAnswer: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    val isEnableSubmit: Boolean by remember(text) { derivedStateOf { text.isNotEmpty() } }
    val onDoneActionListener = {
        onDoneAnswer.invoke(text)
        text = ""
    }

    val submitColor by animateColorAsState(
        targetValue = if (isEnableSubmit) Colors.Gold02 else Colors.Gray05,
        label = ""
    )

    Column {
        HorizontalDivider()

        InitialRoundBody(
            previousRound = previousRound,
            totalRoundCount = totalRoundCount
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(
                    horizontal = Spacings.Spacing04,
                    vertical = Spacings.Spacing02
                )
        ) {
            BaseGameTextEditor(
                modifier = Modifier.weight(1f),
                text = text,
                onChangeTextListener = { text = it },
                onDoneActionListener = onDoneActionListener
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable(
                        enabled = isEnableSubmit,
                        onClick = onDoneActionListener
                    )
                    .widthIn(min = Dimens.GAME_DONE_BUTTON_WIDTH_MIN),
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "제출",
                    color = submitColor,
                    style = TextStyles.SubTitle01,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun InitialRoundBody(
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
@OptIn(ExperimentalLayoutApi::class)
private fun InitialQuizGameBody(
    modifier: Modifier = Modifier,
    item: ItemData
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacings.Spacing05)
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03, Alignment.CenterVertically)
    ) {
        Spacer(modifier = Modifier.height(Spacings.Spacing02))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing00, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing00)
        ) {
            item.name.forEach {
                if (it == ' ')
                    Spacer(modifier = Modifier.width(IconSize.LargeSize))
                else
                    Box(
                        modifier = Modifier
                            .size(IconSize.LargeSize)
                            .background(Colors.Gray07, CircleShape)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = it.getInitialHangul(),
                            style = TextStyles.SubTitle01
                        )
                    }
            }
        }

        BaseRectangleIconImage(
            modifier = Modifier.size(IconSize.XXLargeSize),
            serverIconType = ServerIconType.ITEM,
            versionName = item.version,
            id = item.id
        )

        InitialItemStatusBody(
            modifier = Modifier
                .widthIn(max = Dimens.INITIAL_ITEM_STATUS_BODY_WIDTH_MAX),
            item = item
        )

        Spacer(modifier = Modifier.height(Spacings.Spacing02))
    }
}

@Composable
fun InitialItemStatusBody(
    modifier: Modifier = Modifier,
    item: ItemData
) {
    Column(
        modifier = modifier
            .border(
                width = 0.5.dp,
                color = Colors.Gold05
            )
            .padding(
                top = Spacings.Spacing03,
                start = Spacings.Spacing03,
                end = Spacings.Spacing03,
                bottom = Spacings.Spacing01
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.version,
                style = TextStyles.Body04,
                color = Colors.Gold04
            )
        }

        HorizontalDivider()

        LolHtmlTagTextView(
            modifier = Modifier.fillMaxWidth(),
            lolDescription = item.description
        )
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
internal fun InitialQuizScreenPreview() {
    LolChampionThemePreview {
        InitialQuizGameBody(
            item = dummyItem
        )
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
internal fun InitialItemStatusBodyPreview() {
    LolChampionThemePreview {
        InitialItemStatusBody(item = dummyItem)
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
internal fun InitialInputBodyPreview() {
    LolChampionThemePreview {
        InitialInputBody(
            previousRound = pre,
            totalRoundCount = 10,
            onDoneAnswer = {}
        )
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
internal fun InitialRoundBodyPreview() {
    LolChampionThemePreview {
        InitialRoundBody(
            previousRound = pre,
            totalRoundCount = 10,
        )
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
internal fun GameEndDialogBodyPreview() {
    LolChampionThemePreview {
        GameEndDialogPreviousItemBody(
            previousItemList = listOf(
                Triple(ChainType.FAIL, dummyItem, "a"),
                Triple(ChainType.NORMAL, dummyItem, "a"),
                Triple(ChainType.NICE, dummyItem, "a"),
                Triple(ChainType.GREAT, dummyItem, "a"),
                Triple(ChainType.GOOD, dummyItem, "a")
            )
        )
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
internal fun ReadyTimeDialogBodyPreview() {
    LolChampionThemePreview {
        ReadyTimeDialogBody(
            readyTime = 2.5f
        )
    }
}


private val dummyItem = ItemData(
    name = "드락사르의 암흑검",
    version = "14.11.01",
    tags = setOf(
        ItemTagType.Mana,
        ItemTagType.Damage,
        ItemTagType.Armor,
        ItemTagType.Boots
    ),
    description = "<mainText><stats>공격력 <ornnBonus>75</ornnBonus><br>물리 관통력 <ornnBonus>26</ornnBonus><br>스킬 가속 <ornnBonus>20</ornnBonus></stats><br><br><br><li><passive>밤의 " +
            "추적자:</passive> 대상이 잃은 체력에 비례해 스킬 피해량이 최대 일정 비율까지 증가합니다. 자신이 피해를 입힌 챔피언이 3초 안에 죽으면 1.5초 동안 구조물이 아닌 대상으로부터 <keywordStealth>대상으로 지정할 수 없는 상태</keywordStealth>가 됩니다. (30(0초))<br><br><rarityMythic>신화급 기본 지속 효과:</rarityMythic> 다른 모든 <rarityLegendary>전설급</rarityLegendary> 아이템에 스킬 가속 및 이동 속도.<br></mainText>",
    gold = ItemData.Gold(total = 1000, sell = 700),
    into = listOf("1", "2", "3", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4"),
    from = listOf("1", "2", "3", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4")
)

private val pre: List<Boolean> = Stack<Boolean>().apply {
    push(true)
    push(false)
    push(true)
    push(false)
    push(true)
}
private val next: Stack<ItemData> = Stack<ItemData>().apply {
    push(dummyItem)
    push(dummyItem)
    push(dummyItem)
    push(dummyItem)
    push(dummyItem)
}