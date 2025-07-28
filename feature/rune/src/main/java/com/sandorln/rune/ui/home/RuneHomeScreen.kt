package com.sandorln.rune.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.sandorln.design.component.BaseLazyColumnWithPull
import com.sandorln.design.component.BasePatchNoteListBodyWithLoading
import com.sandorln.design.component.html.LolHtmlTagTextView
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.model.data.rune.RuneData
import com.sandorln.model.data.rune.RuneSlot
import com.sandorln.model.data.rune.RuneStyle
import com.sandorln.rune.R
import com.sandorln.design.R as DesignR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuneHomeScreen(
    runeHomeViewModel: RuneHomeViewModel = hiltViewModel()
) {
    val uiState by runeHomeViewModel.uiState.collectAsState()
    val runeSlots = uiState.selectedRuneStyle?.slots ?: emptyList()

    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = Dimens.PULL_HEIGHT
    )

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading)
            pullToRefreshState.endRefresh()
    }

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing)
            runeHomeViewModel.sendAction(RuneHomeAction.RefreshRuneData)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.notRuneSystem) {
            NotRuneSystemScreen()
            return@Box
        }

        BaseLazyColumnWithPull(
            pullToRefreshState = pullToRefreshState
        ) {
            item {
                BasePatchNoteListBodyWithLoading(
                    title = stringResource(R.string.rune_patch_note_title),
                    loadingTitle = stringResource(R.string.rune_patch_note_loading_title),
                    patchNoteDataList = uiState.runePatchNoteList
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacings.Spacing05))
            }

            item {
                RuneStyleListBody(
                    modifier = Modifier.fillMaxWidth(),
                    selectedRuneStyle = uiState.selectedRuneStyle,
                    runeStyleList = uiState.runeStyleList,
                ) { runeStyle ->
                    runeHomeViewModel.sendAction(RuneHomeAction.SelectedRuneStyle(runeStyle))
                }
            }

            items(count = runeSlots.size) { index ->
                val runeSlot = runeSlots.getOrNull(index) ?: return@items
                val isCoreRune = index == 0
                RuneSlotListBody(
                    modifier = Modifier.fillMaxWidth(),
                    runeSlot = runeSlot,
                    isCoreRune = isCoreRune,
                    selectedRuneData = uiState.selectedRuneDataList[index],
                    onClickRuneData = { runeData ->
                        runeHomeViewModel.sendAction(
                            RuneHomeAction.SelectedRuneDataId(
                                runeSlotIndex = index,
                                runeData = runeData
                            )
                        )
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacings.Spacing07))
            }
        }
    }
}

@Composable
fun RuneStyleListBody(
    modifier: Modifier = Modifier,
    selectedRuneStyle: RuneStyle?,
    runeStyleList: List<RuneStyle>,
    onClickRuneStyle: (RuneStyle) -> Unit,
) {
    LazyRow(
        modifier = modifier.heightIn(min = Dimens.RUNE_STYLE_BAR_HEIGHT),
        contentPadding = PaddingValues(
            horizontal = Spacings.Spacing05,
            vertical = Spacings.Spacing01
        ),
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing06, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            count = runeStyleList.size,
            key = { index -> runeStyleList[index].key }) { index ->

            val runeStyle = runeStyleList.getOrNull(index) ?: return@items
            RuneStyleBody(
                isSelect = selectedRuneStyle?.key == runeStyle.key,
                runeStyle = runeStyle,
                onClickRuneStyle = onClickRuneStyle
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RuneStyleBody(
    isSelect: Boolean = false,
    runeStyle: RuneStyle,
    onClickRuneStyle: (RuneStyle) -> Unit
) {
    val size by animateDpAsState(if (isSelect) IconSize.XXLargeSize else IconSize.XLargeSize)
    val color by animateColorAsState(if (isSelect) Colors.Gold03 else Colors.Gray05, label = "")
    val colorFilter = if (isSelect) null else ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })

    Column(
        modifier = Modifier.clickable { onClickRuneStyle.invoke(runeStyle) },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GlideImage(
            modifier = Modifier
                .size(size)
                .background(Colors.Blue06, CircleShape)
                .clip(CircleShape)
                .border(1.dp, color, CircleShape),
            model = runeStyle.iconUrl,
            colorFilter = colorFilter,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(Spacings.Spacing00))

        Text(
            modifier = Modifier.width(size),
            text = runeStyle.name,
            style = TextStyles.SubTitle02,
            color = color,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RuneSlotListBody(
    modifier: Modifier = Modifier,
    runeSlot: RuneSlot,
    isCoreRune: Boolean,
    selectedRuneData: RuneData?,
    onClickRuneData: (runeData: RuneData) -> Unit
) {
    val minHeight = if (isCoreRune) Dimens.RUNE_DATA_CORE_HEIGHT else Dimens.RUNE_DATA_DEFAULT_HEIGHT

    Column(modifier = modifier) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight),
            contentPadding = PaddingValues(
                horizontal = Spacings.Spacing05,
                vertical = Spacings.Spacing01
            ),
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing06, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(
                count = runeSlot.runes.size,
                key = { index -> runeSlot.runes[index].key }) { index ->
                val runeData = runeSlot.runes.getOrNull(index) ?: return@items
                val isSelected = selectedRuneData?.id == runeData.id
                RuneDataBody(
                    isSelect = isSelected,
                    selectedSize = IconSize.XXLargeSize.takeIf { isCoreRune },
                    unselectedSize = IconSize.XLargeSize.takeIf { isCoreRune },
                    runeData = runeData,
                    onClickRuneData = onClickRuneData,
                )
            }
        }

        AnimatedVisibility(visible = selectedRuneData != null) {
            SelectedRuneDataBody(runeData = selectedRuneData)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RuneDataBody(
    isSelect: Boolean = false,
    selectedSize: Dp? = IconSize.XLargeSize,
    unselectedSize: Dp? = IconSize.LargeSize,
    runeData: RuneData,
    onClickRuneData: (RuneData) -> Unit
) {
    val size by animateDpAsState(
        targetValue = if (isSelect)
            selectedSize ?: IconSize.XLargeSize
        else
            unselectedSize ?: IconSize.LargeSize
    )
    val color by animateColorAsState(if (isSelect) Colors.Gold03 else Colors.Gray05, label = "")
    val colorFilter = if (isSelect) null else ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })

    GlideImage(
        modifier = Modifier
            .clickable { onClickRuneData.invoke(runeData) }
            .size(size)
            .background(Colors.Blue06, CircleShape)
            .clip(CircleShape)
            .border(1.dp, color, CircleShape),
        model = runeData.iconUrl,
        colorFilter = colorFilter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun SelectedRuneDataBody(runeData: RuneData?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = Spacings.Spacing02,
                start = Spacings.Spacing05,
                end = Spacings.Spacing05,
            )
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = runeData?.name ?: "",
            style = TextStyles.SubTitle02,
            color = Colors.Gold02,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacings.Spacing01))

        LolHtmlTagTextView(
            lolDescription = runeData?.shortDesc ?: "",
            textSize = TextStyles.Body03.fontSize.value,
            textColor = Colors.Gold04
        )

        Spacer(modifier = Modifier.height(Spacings.Spacing00))

        LolHtmlTagTextView(
            lolDescription = runeData?.longDesc ?: "",
            textSize = TextStyles.Body04.fontSize.value
        )

        Spacer(modifier = Modifier.height(Spacings.Spacing05))
        HorizontalDivider()
    }
}

@Composable
fun NotRuneSystemScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(IconSize.XXXXLargeSize),
            contentScale = ContentScale.Fit,
            painter = painterResource(DesignR.drawable.img_error),
            contentDescription = null,
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.not_rune_system_message),
            style = TextStyles.SubTitle01,
            color = Colors.BasicWhite,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun RuneStyleListBodyPreview() {
    LolChampionThemePreview {
        RuneStyleListBody(
            modifier = Modifier.fillMaxWidth(),
            runeStyleList = listOf(
                RuneStyle(
                    id = 8100,
                    key = "RuneStyle1",
                    icon = "",
                    name = "RuneStyle1",
                    slots = emptyList()
                ),
                RuneStyle(
                    id = 8100,
                    key = "RuneStyle2",
                    icon = "",
                    name = "RuneStyle2",
                    slots = emptyList()
                ),
                RuneStyle(
                    id = 8100,
                    key = "RuneStyle3",
                    icon = "",
                    name = "RuneStyle3",
                    slots = emptyList()
                ),
            ),
            selectedRuneStyle = RuneStyle(0, "RuneStyle1", "", "", emptyList()),
            onClickRuneStyle = {},
        )
    }
}

@Preview
@Composable
fun RuneSlotListBodyPreview() {
    LolChampionThemePreview {
        RuneSlotListBody(
            modifier = Modifier.fillMaxWidth(),
            isCoreRune = true,
            selectedRuneData = null,
            runeSlot = RuneSlot(
                runes = List(4) { index ->
                    RuneData(
                        id = index,
                        key = index.toString(),
                        icon = "",
                        name = "감전",
                        shortDesc = "@WindowDuration@초 동안 같은 챔피언에게 <b>개별</b> 공격 또는 스킬을 3회 적중시키면 추가 <lol-uikit-tooltipped-keyword key=\\\"LinkTooltip_Description_AdaptiveDmg\\\">적응형 피해</lol-uikit-tooltipped-keyword> 적용",
                        longDesc = "@WindowDuration@초 동안 같은 챔피언에게 <b>개별</b> 공격 또는 스킬을 3회 적중시키면 추가 <lol-uikit-tooltipped-keyword key='LinkTooltip_Description_AdaptiveDmg'><font color='#48C4B7'>적응형 피해</font></lol-uikit-tooltipped-keyword>를 입힙니다.<br><br>피해량: @DamageBase@ ~ @DamageMax@ (+추가 공격력의 @BonusADRatio.-1@, +주문력의 " +
                                "@APRatio.-1@)<br><br>재사용 대기시간: @Cooldown@ ~ @CooldownMin@초<br><br><hr></hr><i>'우리는 그들을 천둥군주라고 부른다. 그들의 번개를 입에 올리는 것은 재앙을 부르는 길이기 때문이다.'</i>"
                    )
                }
            ),
            onClickRuneData = { _ -> }
        )
    }
}

@Preview
@Composable
fun SelectedRuneDataBodyPreview() {
    LolChampionThemePreview {
        SelectedRuneDataBody(
            runeData = RuneData(
                id = 1,
                key = "key",
                icon = "",
                name = "감전",
                shortDesc = "@WindowDuration@초 동안 같은 챔피언에게 <b>개별</b> 공격 또는 스킬을 3회 적중시키면 추가 <lol-uikit-tooltipped-keyword key=\\\"LinkTooltip_Description_AdaptiveDmg\\\">적응형 피해</lol-uikit-tooltipped-keyword> 적용",
                longDesc = "@WindowDuration@초 동안 같은 챔피언에게 <b>개별</b> 공격 또는 스킬을 3회 적중시키면 추가 <lol-uikit-tooltipped-keyword key='LinkTooltip_Description_AdaptiveDmg'><font color='#48C4B7'>적응형 피해</font></lol-uikit-tooltipped-keyword>를 입힙니다.<br><br>피해량: @DamageBase@ ~ @DamageMax@ (+추가 공격력의 @BonusADRatio.-1@, +주문력의 " +
                        "@APRatio.-1@)<br><br>재사용 대기시간: @Cooldown@ ~ @CooldownMin@초<br><br><hr></hr><i>'우리는 그들을 천둥군주라고 부른다. 그들의 번개를 입에 올리는 것은 재앙을 부르는 길이기 때문이다.'</i>"
            )
        )
    }
}

@Preview
@Composable
fun NotRuneSystemScreenPreview() {
    LolChampionThemePreview {
        NotRuneSystemScreen()
    }
}
