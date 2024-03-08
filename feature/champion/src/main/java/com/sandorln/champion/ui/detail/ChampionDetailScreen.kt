package com.sandorln.champion.ui.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.fastJoinToString
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.R
import com.sandorln.design.component.BaseChampionSplashImage
import com.sandorln.design.component.BaseCircleIconImage
import com.sandorln.design.component.BaseContentWithMotionToolbar
import com.sandorln.design.component.BaseSkillImage
import com.sandorln.design.component.html.LolHtmlTagTextView
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.design.theme.addShadow
import com.sandorln.model.data.champion.ChampionSpell

private enum class MotionRefIdType {
    Splash, Icon, Name, Title, Back, BottomDivider
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionDetailScreen(
    championDetailViewModel: ChampionDetailViewModel = hiltViewModel()
) {
    val uiState by championDetailViewModel.uiState.collectAsState()
    val championDetailData = uiState.championDetailData

    BaseContentWithMotionToolbar(
        headerRatio = Dimens.CHAMPION_SPLASH_RATIO,
        headerMinHeight = Dimens.BASE_TOOLBAR_HEIGHT,
        startConstraintSet = { headerRef, _ ->
            val splashImgRef = createRefFor(MotionRefIdType.Splash)
            val iconRef = createRefFor(MotionRefIdType.Icon)
            val nameRef = createRefFor(MotionRefIdType.Name)
            val titleRef = createRefFor(MotionRefIdType.Title)
            val bottomDividerRef = createRefFor(MotionRefIdType.BottomDivider)

            constrain(splashImgRef) {
                width = Dimension.matchParent
                height = Dimension.fillToConstraints
                top.linkTo(headerRef.top)
                bottom.linkTo(headerRef.bottom)
                alpha = 1f
            }
            constrain(iconRef) {
                start.linkTo(headerRef.start)
                end.linkTo(headerRef.end)
                top.linkTo(headerRef.top)
                bottom.linkTo(headerRef.bottom)
                alpha = 0f
            }
            constrain(titleRef) {
                width = Dimension.fillToConstraints
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(nameRef.top)
            }
            constrain(nameRef) {
                width = Dimension.fillToConstraints
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(headerRef.bottom, Spacings.Spacing01)
            }
            constrain(bottomDividerRef) {
                bottom.linkTo(headerRef.bottom)
                alpha = 0f
            }
        },
        endConstraintSet = { headerRef, _ ->
            val splashImgRef = createRefFor(MotionRefIdType.Splash)
            val iconRef = createRefFor(MotionRefIdType.Icon)
            val nameRef = createRefFor(MotionRefIdType.Name)
            val titleRef = createRefFor(MotionRefIdType.Title)
            val backRef = createRefFor(MotionRefIdType.Back)
            val bottomDividerRef = createRefFor(MotionRefIdType.BottomDivider)

            constrain(splashImgRef) {
                width = Dimension.matchParent
                height = Dimension.fillToConstraints
                top.linkTo(headerRef.top)
                bottom.linkTo(headerRef.bottom)
                alpha = 0f
            }
            constrain(iconRef) {
                start.linkTo(backRef.end, Spacings.Spacing02)
                top.linkTo(headerRef.top)
                bottom.linkTo(headerRef.bottom)
                alpha = 1f
            }
            constrain(titleRef) {
                height = Dimension.fillToConstraints
                start.linkTo(iconRef.end, Spacings.Spacing02)
                top.linkTo(headerRef.top)
                bottom.linkTo(nameRef.top)
            }
            constrain(nameRef) {
                height = Dimension.fillToConstraints
                start.linkTo(iconRef.end, Spacings.Spacing02)
                top.linkTo(titleRef.bottom)
                bottom.linkTo(headerRef.bottom)
            }
            constrain(bottomDividerRef) {
                bottom.linkTo(headerRef.bottom)
                alpha = 1f
            }
        },
        headerContent = { progress ->
            val iconSize = lerp(IconSize.XXXLargeSize, IconSize.XLargeSize, progress)
            val titleSize = lerp(TextStyles.Title01.fontSize, TextStyles.Body02.fontSize, progress)
            val nameSize = lerp(TextStyles.Title02.fontSize, TextStyles.SubTitle01.fontSize, progress)
            val titleColor = lerp(Colors.Gold02, Colors.BasicWhite, progress)
            val nameColor = lerp(Colors.BasicWhite, Colors.Gold02, progress)

            Box(
                modifier = Modifier.layoutId(MotionRefIdType.Splash)
            ) {
                BaseChampionSplashImage(
                    modifier = Modifier.matchParentSize(),
                    championId = championDetailData.id
                )
            }

            Box(
                modifier = Modifier
                    .size(iconSize)
                    .background(Colors.BaseColor, CircleShape)
                    .layoutId(MotionRefIdType.Icon)
            ) {
                BaseCircleIconImage(
                    versionName = uiState.version,
                    modifier = Modifier.matchParentSize(),
                    id = championDetailData.id
                )
            }

            Box(modifier = Modifier.layoutId(MotionRefIdType.Title)) {
                Text(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    text = championDetailData.title,
                    style = TextStyles.Title01.addShadow(),
                    fontSize = titleSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = titleColor
                )
            }

            Box(
                modifier = Modifier.layoutId(MotionRefIdType.Name)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.TopCenter),
                    text = championDetailData.name,
                    style = TextStyles.Body01.addShadow(),
                    fontSize = nameSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = nameColor
                )
            }

            Box(
                modifier = Modifier
                    .layoutId(MotionRefIdType.Back)
                    .height(Dimens.BASE_TOOLBAR_HEIGHT)
            ) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    IconButton(
                        modifier = Modifier
                            .padding(start = Spacings.Spacing02)
                            .size(IconSize.XLargeSize)
                            .align(alignment = Alignment.Center),
                        onClick = {

                        }) {
                        Icon(
                            modifier = Modifier.size(IconSize.XLargeSize),
                            painter = painterResource(id = R.drawable.ic_chevron_left),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.layoutId(MotionRefIdType.BottomDivider)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
        ) {
            Spacer(modifier = Modifier.height(Spacings.Spacing02))

            ChampionDetailInfoTitle(title = "스토리")

            LolHtmlTagTextView(
                modifier = Modifier.padding(horizontal = Spacings.Spacing04),
                lolDescription = championDetailData.lore,
                textSize = TextStyles.Body01.fontSize.value,
                textColor = Colors.Gray03
            )

            ChampionDetailInfoTitle(title = "스킬")

            ChampionSkillListBody(
                version = uiState.version,
                selectedSkill = uiState.selectedSkill,
                passiveSkill = championDetailData.passive,
                skillList = championDetailData.spells,
                onClickSkillIcon = { skill ->
                    val action = ChampionDetailAction.ChangeSelectSkill(skill)
                    championDetailViewModel.sendAction(action)
                }
            )

            Spacer(modifier = Modifier.height(Spacings.Spacing02))
        }
    }
}

@Composable
internal fun ChampionDetailInfoTitle(
    modifier: Modifier = Modifier,
    title: String = ""
) {
    Column(
        modifier = modifier
            .wrapContentWidth()
            .padding(
                horizontal = Spacings.Spacing04,
                vertical = Spacings.Spacing01
            ),
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing00)
    ) {
        Text(
            modifier = Modifier,
            text = title,
            style = TextStyles.Title03,
            color = Colors.Gold02
        )

        HorizontalDivider()
    }
}

@Composable
fun ChampionSkillListBody(
    modifier: Modifier = Modifier,
    version: String = "",
    selectedSkill: ChampionSpell = ChampionSpell(),
    onClickSkillIcon: (championSpell: ChampionSpell) -> Unit = {},
    passiveSkill: ChampionSpell = ChampionSpell(),
    skillList: List<ChampionSpell> = List(4) { ChampionSpell() }
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* TODO :: 동영상 입력 */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(
                Spacings.Spacing02,
                Alignment.CenterHorizontally
            )
        ) {
            ChampionSkillImage(
                isSelect = selectedSkill.id == passiveSkill.id,
                isPassive = true,
                version = version,
                name = passiveSkill.image.full,
                onClickSkillIcon = {
                    onClickSkillIcon.invoke(passiveSkill)
                }
            )

            VerticalDivider(
                modifier = Modifier.padding(horizontal = Spacings.Spacing02)
            )

            skillList.forEach { championSpell ->
                ChampionSkillImage(
                    isSelect = selectedSkill.id == championSpell.id,
                    version = version,
                    name = championSpell.image.full,
                    onClickSkillIcon = {
                        onClickSkillIcon.invoke(championSpell)
                    }
                )
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = Spacings.Spacing05),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = selectedSkill.name,
                style = TextStyles.SubTitle01,
                textAlign = TextAlign.Center,
                color = Colors.BaseColor
            )
            if (selectedSkill.cooldownBurn.isNotEmpty()) {
                Text(
                    text = "쿨타임 : ${selectedSkill.cooldownBurn} 초",
                    style = TextStyles.Body04,
                    textAlign = TextAlign.Center,
                    color = Colors.Gray04
                )
            }
            if (selectedSkill.costBurn.isNotEmpty() && selectedSkill.costBurn != "0") {
                Text(
                    text = "소모 : ${selectedSkill.costBurn}",
                    style = TextStyles.Body04,
                    textAlign = TextAlign.Center,
                    color = Colors.Gray04
                )
            }

            if (selectedSkill.levelTip.isNotEmpty()) {
                LolHtmlTagTextView(
                    lolDescription = "스킬 레벨업 : ${selectedSkill.levelTip.fastJoinToString(" / ")}"
                )
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = Spacings.Spacing05),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01),
        ) {
            LolHtmlTagTextView(
                textColor = Colors.Gray03,
                lolDescription = selectedSkill.description,
                textSize = TextStyles.Body02.fontSize.value
            )
            LolHtmlTagTextView(
                lolDescription = selectedSkill.tooltip,
                textSize = TextStyles.Body02.fontSize.value
            )
        }
    }
}

@Composable
fun ChampionSkillImage(
    version: String = "",
    isPassive: Boolean = false,
    isSelect: Boolean = false,
    name: String = "",
    onClickSkillIcon: () -> Unit = {}
) {
    val iconOffset by animateDpAsState(
        targetValue = if (isSelect) {
            Spacings.Spacing00
        } else {
            0.dp
        }, label = ""
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelect) {
            Colors.BaseColor
        } else {
            Colors.Gray07
        },
        label = ""
    )
    val colorFilter = if (isSelect) {
        null
    } else {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
    }

    Box(
        modifier = Modifier
            .offset(y = -iconOffset)
            .size(Dimens.CHAMPION_SKILL_SIZE)
            .border(
                width = 1.dp,
                color = borderColor
            )
            .clickable {
                onClickSkillIcon.invoke()
            }
    ) {
        BaseSkillImage(
            modifier = Modifier.matchParentSize(),
            version = version,
            isPassive = isPassive,
            fullName = name,
            colorFilter = colorFilter
        )
    }
}

@Preview
@Composable
internal fun ChampionDetailScreenPreview() {
    LolChampionThemePreview {
        ChampionDetailScreen()
    }
}

@Preview
@Composable
internal fun ChampionDetailInfoTitlePreview() {
    LolChampionThemePreview {
        ChampionDetailInfoTitle(title = "title")
    }
}

@Preview
@Composable
internal fun ChampionSkillImagePreview() {
    LolChampionThemePreview {
        ChampionSkillImage(
            isPassive = false,
            isSelect = true,
            name = ""
        )
    }
}

@Preview
@Composable
internal fun ChampionSkillBodyListPreview() {
    LolChampionThemePreview {
        ChampionSkillListBody(
            selectedSkill = ChampionSpell(
                name = "테스트",
                tooltip = "툴팁",
                description = "상세 설명",
                cooldownBurn = "10/9/8/7/6",
                costBurn = "100/90/80/60/50",
                levelTip = listOf("피해량", "만월총 추가 효과: 표식 피해량")
            )
        )
    }
}