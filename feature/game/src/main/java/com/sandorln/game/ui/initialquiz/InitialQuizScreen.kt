package com.sandorln.game.ui.initialquiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.component.BaseGameTextEditor
import com.sandorln.design.component.BaseRectangleIconImage
import com.sandorln.design.component.BaseToolbar
import com.sandorln.design.component.html.LolHtmlTagTextView
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.type.ItemTagType
import java.text.DecimalFormat

@Composable
fun InitialQuizScreen(
    initialQuizViewModel: InitialQuizViewModel = hiltViewModel(),
    onBackStack: () -> Unit
) {
    val uiState by initialQuizViewModel.uiState.collectAsState()
    val gameTime by initialQuizViewModel.gameTime.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        BaseToolbar(onClickStartIcon = onBackStack)

        InitialQuizGameBody(
            time = gameTime,
            item = uiState.itemData
        )

        Column {
            HorizontalDivider()

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
                    text = uiState.inputAnswer,
                    onChangeTextListener = {
                        initialQuizViewModel.sendAction(InitialQuizAction.ChangeAnswer(it))
                    },
                    onDoneActionListener = {
                        initialQuizViewModel.sendAction(InitialQuizAction.InitialQuizDone)
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(min = Dimens.GAME_DONE_BUTTON_WIDTH_MIN),
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "제출",
                        style = TextStyles.SubTitle01,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun InitialQuizGameBody(
    time: Float,
    item: ItemData
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Spacings.Spacing05,
                vertical = Spacings.Spacing03
            )
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03)
    ) {
        GameTimeBody(
            time = time
        )

        HorizontalDivider()

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
                    )
            }
        }

        HorizontalDivider()

        BaseRectangleIconImage(modifier = Modifier.size(IconSize.XXLargeSize))

        InitialItemStatusBody(
            modifier = Modifier
                .widthIn(
                    max = Dimens.INITIAL_ITEM_STATUS_BODY_WIDTH_MAX
                ),
            item = dummyItem
        )
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
                text = "totalGoldText",
                style = TextStyles.Body04,
                color = Colors.Gold04
            )

            Spacer(modifier = Modifier.width(Spacings.Spacing00))

            Text(
                text = "sellGoldText",
                style = TextStyles.Body04,
                color = Colors.Gold05
            )
        }

        HorizontalDivider()

        LolHtmlTagTextView(
            modifier = Modifier.fillMaxWidth(),
            lolDescription = item.description
        )
    }
}

@Composable
fun GameTimeBody(time: Float) {
    var textColor: Color by remember { mutableStateOf(Colors.Blue01) }
    val timeDecimalFormat = DecimalFormat("00.00")
    LaunchedEffect(time) {
        textColor = when {
            time >= 60 -> Colors.Gold01
            time >= 40 -> Colors.Gold02
            time >= 20 -> Colors.Gold03
            time >= 10 -> Colors.Gold04
            time > 0 -> Colors.Gold05
            else -> Colors.Gray07
        }
    }

    Text(
        text = timeDecimalFormat.format(time),
        textAlign = TextAlign.Center,
        color = textColor,
        style = TextStyles.Title01.copy(letterSpacing = 12.sp),
    )
}

@Preview(device = Devices.PIXEL_2)
@Composable
internal fun InitialQuizScreenPreview() {
    LolChampionThemePreview {
        InitialQuizGameBody(
            60f,
            dummyItem
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

val dummyItem = ItemData(
    name = "드락사르의 암흑검",
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
