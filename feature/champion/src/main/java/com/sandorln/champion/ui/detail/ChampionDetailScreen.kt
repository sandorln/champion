package com.sandorln.champion.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.fastJoinToString
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.champion.util.getResourceId
import com.sandorln.champion.util.statusCompareColor
import com.sandorln.design.R
import com.sandorln.design.component.BaseChampionSplashImage
import com.sandorln.design.component.BaseCircleIconImage
import com.sandorln.design.component.BaseContentWithMotionToolbar
import com.sandorln.design.component.BaseSkillImage
import com.sandorln.design.component.ExoPlayerView
import com.sandorln.design.component.dialog.BaseBottomSheetDialog
import com.sandorln.design.component.html.LolHtmlTagTextView
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.design.theme.addShadow
import com.sandorln.model.data.champion.ChampionDetailData
import com.sandorln.model.data.champion.ChampionSkin
import com.sandorln.model.data.champion.ChampionSpell
import com.sandorln.model.data.champion.ChampionStats
import com.sandorln.model.data.champion.SummaryChampion
import com.sandorln.model.type.ChampionTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionDetailScreen(
    championDetailViewModel: ChampionDetailViewModel = hiltViewModel(),
    onBackStack: () -> Unit = {},
    moveToChampionDetailScreen: (championId: String, version: String) -> Unit
) {
    val uiState by championDetailViewModel.uiState.collectAsState()
    val championDetailData = uiState.championDetailData
    val changedStatsVersion = uiState.changedStatsVersion

    BaseContentWithMotionToolbar(
        headerRatio = Dimens.CHAMPION_SPLASH_RATIO_STRING,
        headerMinHeight = Dimens.BASE_TOOLBAR_HEIGHT,
        startConstraintSet = ConstraintSetScope::championDetailStart,
        endConstraintSet = ConstraintSetScope::championDetailEnd,
        headerContent = { progress ->
            val iconSize = lerp(IconSize.XXXLargeSize, IconSize.XLargeSize, progress)
            val titleSize = lerp(TextStyles.Title01.fontSize, TextStyles.Body02.fontSize, progress)
            val nameSize = lerp(TextStyles.Title02.fontSize, TextStyles.SubTitle01.fontSize, progress)
            val titleColor = lerp(Colors.Gold02, Colors.BasicWhite, progress)
            val nameColor = lerp(Colors.BasicWhite, Colors.Gold02, progress)
            val versionBorderWidth = lerp((-0.1).dp, 1.dp, progress)

            Box(
                modifier = Modifier.layoutId(MotionRefIdType.Splash)
            ) {
                BaseChampionSplashImage(
                    modifier = Modifier.matchParentSize(),
                    championId = championDetailData.id
                )
            }

            Box(
                modifier = Modifier.layoutId(MotionRefIdType.HeaderBrush)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .height(Spacings.Spacing05)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Colors.Blue06.copy(alpha = 1.0f),
                                    Colors.Blue06.copy(alpha = 0.0f)
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(Spacings.Spacing05)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Colors.Blue06.copy(alpha = 0.0f),
                                    Colors.Blue06.copy(alpha = 1.0f)
                                )
                            )
                        )
                )
            }

            Box(
                modifier = Modifier
                    .size(iconSize)
                    .background(Colors.BaseColor, CircleShape)
                    .layoutId(MotionRefIdType.Icon)
            ) {
                BaseCircleIconImage(
                    versionName = uiState.selectedVersion,
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
                            onBackStack.invoke()
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

            Box(
                modifier = Modifier.layoutId(MotionRefIdType.Version)
            ) {
                Row(
                    modifier = Modifier
                        .width(80.dp)
                        .background(
                            color = Colors.Blue06,
                            shape = RoundedCornerShape(Radius.Radius04)
                        )
                        .border(
                            width = versionBorderWidth,
                            color = Colors.BaseColor,
                            shape = RoundedCornerShape(Radius.Radius04)
                        )
                        .padding(
                            horizontal = Spacings.Spacing01,
                            vertical = Spacings.Spacing00
                        )
                        .align(Alignment.Center)
                        .clickable {
                            championDetailViewModel.sendAction(ChampionDetailAction.ChangeVersionListDialog(true))
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = uiState.selectedVersion,
                        style = TextStyles.SubTitle03,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    Icon(
                        modifier = Modifier.size(IconSize.SmallSize),
                        painter = painterResource(id = R.drawable.ic_chevron_down),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03)
        ) {
            Spacer(modifier = Modifier.height(Spacings.Spacing00))

            ChampionDetailInfoTitle(title = "능력치")

            ChampionStatusBody(
                currentVersion = uiState.selectedVersion,
                preVersion = uiState.preVersionName,
                championStats = championDetailData.stats,
                preChampionStats = uiState.preChampion?.stats
            )

            ChampionDetailInfoTitle(title = "스킬")

            ChampionSkillListBody(
                isLatestVersion = uiState.isLatestVersion,
                version = uiState.selectedVersion,
                selectedSkill = uiState.selectedSkill,
                selectedSkillUrl = uiState.selectedSkillUrl,
                passiveSkill = championDetailData.passive,
                skillList = championDetailData.spells,
                onClickSkillIcon = { skill ->
                    val action = ChampionDetailAction.ChangeSelectSkill(skill)
                    championDetailViewModel.sendAction(action)
                }
            )

            if (uiState.similarChampionList.isNotEmpty()) {
                ChampionDetailInfoTitle(title = "비슷한 역할 챔피언")

                SimilarChampionListBody(
                    version = uiState.selectedVersion,
                    tags = championDetailData.tags,
                    similarChampionList = uiState.similarChampionList,
                    moveToChampionDetailScreen = moveToChampionDetailScreen
                )
            }

            ChampionDetailInfoTitle(title = "스킨")

            ChampionSkins(
                championId = championDetailData.id,
                skinList = championDetailData.skins
            )

            ChampionDetailInfoTitle(title = "스토리")

            LolHtmlTagTextView(
                modifier = Modifier.padding(horizontal = Spacings.Spacing04),
                lolDescription = championDetailData.lore,
                textSize = TextStyles.Body01.fontSize.value,
                textColor = Colors.Gray03
            )

            Spacer(modifier = Modifier.height(Spacings.Spacing05))

            ChampionLinkListBody(championDetailData = championDetailData)

            Spacer(modifier = Modifier.height(Spacings.Spacing02))
        }

        if (uiState.isShowVersionListDialog) {
            BaseBottomSheetDialog(
                onDismissRequest = {
                    championDetailViewModel.sendAction(ChampionDetailAction.ChangeVersionListDialog(false))
                }
            ) {
                LazyColumn {
                    items(count = uiState.versionNameList.size) {
                        val versionName = uiState.versionNameList[it]

                        VersionItemBody(
                            versionName = versionName,
                            isSelectedVersion = versionName == uiState.selectedVersion,
                            isChangedStatsDiff = changedStatsVersion[versionName] == true,
                            onClickListener = {
                                championDetailViewModel.sendAction(ChampionDetailAction.ChangeVersion(versionName))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChampionLinkListBody(
    championDetailData: ChampionDetailData = ChampionDetailData()
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing00)
    ) {
        Text(
            text = "공략 사이트로 이동",
            style = TextStyles.Body03,
            color = Colors.Gray05,
            textDecoration = TextDecoration.Underline
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(
                Spacings.Spacing02,
                Alignment.CenterHorizontally
            )
        ) {
            ChampionLinkBody(
                title = "OP.GG",
                onClickListener = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.op.gg/champions/${championDetailData.id}/build")))
                }
            )
            ChampionLinkBody(
                title = "LOL.PS",
                onClickListener = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://lol.ps/champ/${championDetailData.key}")))
                }
            )
        }
    }
}

@Composable
fun VersionItemBody(
    versionName: String,
    isSelectedVersion: Boolean = false,
    isChangedStatsDiff: Boolean = false,
    onClickListener: () -> Unit = {}
) {
    val backgroundColor = if (isSelectedVersion) Colors.Blue05 else Colors.Gray08
    Row(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .clickable { onClickListener.invoke() }
            .padding(
                horizontal = Spacings.Spacing05,
                vertical = Spacings.Spacing03
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = versionName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyles.SubTitle01,
            color = if (isSelectedVersion) Colors.BaseColor else Colors.Gray04
        )

        if (isChangedStatsDiff) {
            Text(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Colors.Gold02,
                        shape = RoundedCornerShape(Radius.Radius03)
                    )
                    .padding(
                        horizontal = Spacings.Spacing01,
                        vertical = Spacings.Spacing00
                    ),
                text = "능력치 변경",
                style = TextStyles.Body04,
                color = Colors.Gold02
            )
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
    isLatestVersion: Boolean = false,
    selectedSkill: ChampionSpell = ChampionSpell(),
    selectedSkillUrl: String = "",
    onClickSkillIcon: (championSpell: ChampionSpell) -> Unit = {},
    passiveSkill: ChampionSpell = ChampionSpell(),
    skillList: List<ChampionSpell> = List(4) { ChampionSpell() }
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLatestVersion)
            ExoPlayerView(
                modifier = Modifier
                    .padding(horizontal = Spacings.Spacing04)
                    .fillMaxWidth()
                    .aspectRatio(Dimens.CHAMPION_SKILL_VIDEO_RATIO)
                    .border(
                        width = 0.5.dp,
                        color = Colors.Gold02
                    ),
                url = selectedSkillUrl
            )

        if (!isLatestVersion)
            Text(
                text = "※ 동영상은 최신 버전일 때 재생 됩니다 ※",
                modifier = Modifier.padding(vertical = Spacings.Spacing00),
                style = TextStyles.Body04.addShadow(),
                color = Colors.Gray03
            )

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
                isSelect = selectedSkill.spellType == passiveSkill.spellType,
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
                    isSelect = selectedSkill.spellType == championSpell.spellType,
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
            Spacings.Spacing01
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
            .background(Colors.Gray09)
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

@Composable
fun ChampionLinkBody(
    title: String = "OP.GG",
    onClickListener: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clickable { onClickListener.invoke() }
            .border(1.dp, Colors.Blue03, RoundedCornerShape(Radius.Radius04))
            .padding(vertical = Spacings.Spacing00, horizontal = Spacings.Spacing02),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
    ) {
        Text(
            text = title,
            color = Colors.Blue03,
            style = TextStyles.Body04,
            textDecoration = TextDecoration.Underline
        )

        Icon(
            modifier = Modifier.size(IconSize.SmallSize),
            painter = painterResource(id = R.drawable.ic_open_in_new),
            contentDescription = null,
            tint = Colors.Blue03
        )
    }
}

@Composable
fun ChampionStatusBody(
    championStats: ChampionStats = ChampionStats(),
    preChampionStats: ChampionStats? = null,
    currentVersion: String = "",
    preVersion: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
    ) {
        Row {
            Box(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.weight(1f),
                text = currentVersion,
                style = TextStyles.SubTitle03,
                textAlign = TextAlign.Center,
                color = Colors.Gray04
            )

            if (preChampionStats != null) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = preVersion,
                    style = TextStyles.SubTitle03,
                    textAlign = TextAlign.Center,
                    color = Colors.Gray04
                )
            }
        }

        StatusCompareBody(
            title = "공격력",
            originalValue = championStats.attackdamage,
            originalLvValue = championStats.attackdamageperlevel,
            compareValue = preChampionStats?.attackdamage,
            compareLvValue = preChampionStats?.attackdamageperlevel
        )

        StatusCompareBody(
            title = "공격속도",
            originalValue = championStats.attackspeed,
            originalLvValue = championStats.attackspeedperlevel,
            compareValue = preChampionStats?.attackspeed,
            compareLvValue = preChampionStats?.attackspeedperlevel
        )

        StatusCompareBody(
            title = "사정거리",
            originalValue = championStats.attackrange,
            compareValue = preChampionStats?.attackrange,
        )

        StatusCompareBody(
            title = "체력",
            originalValue = championStats.hp,
            originalLvValue = championStats.hpperlevel,
            compareValue = preChampionStats?.hp,
            compareLvValue = preChampionStats?.hpperlevel
        )

        StatusCompareBody(
            title = "체력재생",
            originalValue = championStats.hpregen,
            originalLvValue = championStats.hpregenperlevel,
            compareValue = preChampionStats?.hpregen,
            compareLvValue = preChampionStats?.hpregenperlevel
        )

        StatusCompareBody(
            title = "마나",
            originalValue = championStats.mp,
            originalLvValue = championStats.mpperlevel,
            compareValue = preChampionStats?.mp,
            compareLvValue = preChampionStats?.mpperlevel
        )

        StatusCompareBody(
            title = "마나재생",
            originalValue = championStats.mpregen,
            originalLvValue = championStats.mpregenperlevel,
            compareValue = preChampionStats?.mpregen,
            compareLvValue = preChampionStats?.mpregenperlevel
        )

        StatusCompareBody(
            title = "방어력",
            originalValue = championStats.armor,
            originalLvValue = championStats.armorperlevel,
            compareValue = preChampionStats?.armor,
            compareLvValue = preChampionStats?.armorperlevel
        )

        StatusCompareBody(
            title = "마법방어력",
            originalValue = championStats.spellblock,
            originalLvValue = championStats.spellblockperlevel,
            compareValue = preChampionStats?.spellblock,
            compareLvValue = preChampionStats?.spellblockperlevel
        )

        StatusCompareBody(
            title = "이동속도",
            originalValue = championStats.movespeed,
            compareValue = preChampionStats?.movespeed,
        )
    }
}

@Composable
fun StatusCompareBody(
    title: String = "공격력",
    originalValue: Double = 0.0,
    originalLvValue: Double? = null,
    compareValue: Double? = null,
    compareLvValue: Double? = null,
) {
    val titleSuffix = if (originalLvValue != null) " (+Lv)" else ""
    val originalValueColor: Color = originalValue.statusCompareColor(compareValue)
    val originalLvValueColor: Color = originalLvValue.statusCompareColor(compareLvValue)
    val compareValueColor: Color = compareValue.statusCompareColor(originalValue)
    val compareLvValueColor: Color = compareLvValue.statusCompareColor(originalLvValue)

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "$title$titleSuffix",
            style = TextStyles.Body04,
            color = Colors.Gray04,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis
        )

        StatusBody(
            modifier = Modifier.weight(1f),
            value = originalValue,
            valueColor = originalValueColor,
            lvValue = originalLvValue,
            lvValueColor = originalLvValueColor
        )

        if (compareValue != null) {
            StatusBody(
                modifier = Modifier.weight(1f),
                value = compareValue,
                valueColor = compareValueColor,
                lvValue = compareLvValue,
                lvValueColor = compareLvValueColor
            )
        }
    }
}

@Composable
fun StatusBody(
    modifier: Modifier = Modifier,
    value: Double = 0.0,
    valueColor: Color = Colors.BasicWhite,
    lvValue: Double? = null,
    lvValueColor: Color = Colors.BasicWhite,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            Spacings.Spacing00,
            Alignment.CenterHorizontally
        )
    ) {
        Text(
            modifier = Modifier,
            text = value.toString(),
            style = TextStyles.Body04,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            color = valueColor
        )
        if (lvValue != null) {
            Text(
                modifier = Modifier,
                text = "(+$lvValue)",
                style = TextStyles.Body04,
                maxLines = 1,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                color = lvValueColor
            )
        }
    }
}

@Composable
fun SimilarChampionListBody(
    modifier: Modifier = Modifier,
    version: String = "",
    tags: List<ChampionTag> = listOf(),
    similarChampionList: List<SummaryChampion> = listOf(),
    moveToChampionDetailScreen: (championId: String, version: String) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacings.Spacing04),
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02),
        ) {
            tags.forEach {
                Icon(
                    modifier = Modifier.size(IconSize.MediumSize),
                    painter = painterResource(id = it.getResourceId()),
                    contentDescription = null,
                    tint = Colors.Gold02
                )
            }
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
        ) {
            Spacer(modifier = Modifier.width(Spacings.Spacing02))
            similarChampionList.forEach {
                Column(
                    modifier = Modifier.clickable {
                        moveToChampionDetailScreen.invoke(it.id, version)
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BaseCircleIconImage(
                        versionName = version,
                        modifier = Modifier.size(IconSize.XLargeSize),
                        id = it.id
                    )
                    Text(
                        modifier = modifier.width(IconSize.XLargeSize),
                        text = it.name,
                        style = TextStyles.Body04,
                        color = Colors.Gold02,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.width(Spacings.Spacing02))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChampionSkins(
    championId: String,
    skinList: List<ChampionSkin>
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0) {
        skinList.size
    }

    val skinName = remember(pagerState.currentPage) {
        val currentPage = pagerState.currentPage
        if (currentPage == 0) {
            "기본 스킨"
        } else {
            skinList[currentPage].name
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(Dimens.CHAMPION_SPLASH_RATIO)
        ) {
            HorizontalPager(
                modifier = Modifier.matchParentSize(),
                state = pagerState,
            ) { count ->
                val skinNum = skinList[count].num.takeIf {
                    !it.isNullOrEmpty()
                } ?: count.toString()

                BaseChampionSplashImage(
                    modifier = Modifier.matchParentSize(),
                    championId = championId,
                    skinNum = skinNum
                )
            }
        }

        Text(
            text = skinName,
            style = TextStyles.SubTitle03,
            color = Colors.Gold04
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = Spacings.Spacing01,
                alignment = Alignment.CenterHorizontally
            ),
        ) {
            repeat(skinList.size) { iteration ->
                val color = if (pagerState.currentPage == iteration)
                    Colors.Gold04
                else
                    Colors.Gray07

                Box(
                    modifier = Modifier
                        .background(color, CircleShape)
                        .size(IconSize.TinySize)
                )
            }
        }
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

@Preview
@Composable
internal fun ChampionLinkBodyPreview() {
    LolChampionThemePreview {
        ChampionLinkListBody()
    }
}

@Preview
@Composable
internal fun ChampionStatusBodyPreview() {
    LolChampionThemePreview {
        ChampionStatusBody()
    }
}

@Preview
@Composable
internal fun SimilarChampionListBodyPreview() {
    LolChampionThemePreview {
        SimilarChampionListBody(
            tags = listOf(ChampionTag.Assassin, ChampionTag.Fighter),
            similarChampionList = List(5) { SummaryChampion(name = "$it") },
            moveToChampionDetailScreen = { _, _ ->

            }
        )
    }
}

@Preview
@Composable
internal fun VersionBodyPreview() {
    LolChampionThemePreview {
        VersionItemBody(
            "14.5.1",
            isChangedStatsDiff = true
        )
    }
}

@Preview
@Composable
internal fun ChampionSkinsPreview() {
    LolChampionThemePreview {
        ChampionSkins(
            "14.5.1",
            List(5) { ChampionSkin(name = it.toString()) }
        )
    }
}