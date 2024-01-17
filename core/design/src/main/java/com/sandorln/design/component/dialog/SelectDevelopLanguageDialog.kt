package com.sandorln.design.component.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.GodadviceTheme
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.model.programlang.ProgramLanguage
import com.sandorln.model.programlang.ProgramLanguageType
import kotlinx.coroutines.launch

@Composable
fun SelectDevelopLanguageBottomSheetDialog(
    programLanguageListMap: Map<ProgramLanguageType, List<ProgramLanguage>> = mapOf(),
    onDismissRequest: () -> Unit = {},
    selectedLanguageType: ProgramLanguageType? = null,
    selectedLanguage: ProgramLanguage? = null,
    onSelectDevelopCategoryLanguage: (type: ProgramLanguageType?, language: ProgramLanguage?) -> Unit = { _, _ -> }
) {
    BottomSheetDialog(
        onDismissRequest = onDismissRequest,
        properties = BottomSheetDialogProperties()
    ) {
        SelectDevelopLanguageBody(
            selectedLanguageType = selectedLanguageType,
            selectedLanguage = selectedLanguage,
            developLanguageListMap = programLanguageListMap,
            onDismissRequest = onDismissRequest,
            onSelectDevelopCategoryLanguage = onSelectDevelopCategoryLanguage
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SelectDevelopLanguageBody(
    selectedLanguageType: ProgramLanguageType? = null,
    selectedLanguage: ProgramLanguage? = null,
    developLanguageListMap: Map<ProgramLanguageType, List<ProgramLanguage>> = mapOf(),
    onDismissRequest: () -> Unit = {},
    onSelectDevelopCategoryLanguage: (type: ProgramLanguageType?, language: ProgramLanguage?) -> Unit = { _, _ -> }
) {
    val coroutineScope = rememberCoroutineScope()
    val programLanguageTypeList = ProgramLanguageType.values()
    val selectColor = Colors.Green300
    val unSelectColor = Colors.Gray500
    val initialPage = programLanguageTypeList
        .indexOfFirst { selectedLanguageType == it }
        .coerceAtLeast(0)

    val pagerState = rememberPagerState(initialPage = initialPage) { programLanguageTypeList.size }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topEnd = Radius.Radius04,
            topStart = Radius.Radius04
        ),
        colors = CardDefaults.cardColors(containerColor = Colors.Gray800),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 528.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .padding(top = Spacings.Spacing02)
                    .background(Colors.Gray700, RoundedCornerShape(Radius.Radius01))
                    .width(40.dp)
                    .height(4.dp)
            )
            Text(
                modifier = Modifier
                    .padding(top = Spacings.Spacing03)
                    .fillMaxWidth()
                    .padding(
                        start = Spacings.Spacing05,
                        end = Spacings.Spacing05
                    ),
                text = "개발 언어 선택",
                style = TextStyles.Title02
            )

            TabRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacings.Spacing04),
                selectedTabIndex = pagerState.currentPage,
                containerColor = Colors.Gray800,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(
                            currentTabPosition = tabPositions[pagerState.currentPage]
                        ),
                        color = Colors.Green300
                    )
                }
            ) {
                programLanguageTypeList.forEachIndexed { index, programLanguageType ->
                    val isSelected = pagerState.currentPage == index
                    Tab(
                        modifier = Modifier.height(46.dp),
                        selected = isSelected,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        unselectedContentColor = unSelectColor,
                        selectedContentColor = selectColor,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        ) {
                            Text(
                                text = programLanguageType.title,
                                style = TextStyles.SubTitle01,
                                color = if (isSelected) selectColor else unSelectColor,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }

            HorizontalPager(state = pagerState) { index ->
                val programLanguageType = programLanguageTypeList[index]
                val programLanguageList = developLanguageListMap[programLanguageType] ?: listOf()
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(items = programLanguageList) {
                        DevelopLanguageItem(
                            selectedLanguage = selectedLanguage,
                            type = programLanguageType,
                            language = it,
                            onDismissRequest = onDismissRequest,
                            onSelectDevelopLanguage = onSelectDevelopCategoryLanguage
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun DevelopLanguageItem(
    selectedLanguage: ProgramLanguage? = null,
    type: ProgramLanguageType = ProgramLanguageType.FRONTEND,
    language: ProgramLanguage = ProgramLanguage(),
    onDismissRequest: () -> Unit = {},
    onSelectDevelopLanguage: (type: ProgramLanguageType?, language: ProgramLanguage?) -> Unit = { _, _ -> }
) {
    val isSelectedLanguage = selectedLanguage?.id == language.id

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colors.Gray800)
            .clickable {
                if (isSelectedLanguage)
                    onSelectDevelopLanguage.invoke(null, null)
                else
                    onSelectDevelopLanguage.invoke(type, language)
                onDismissRequest.invoke()
            }
            .heightIn(min = 50.dp)
            .padding(
                horizontal = Spacings.Spacing05,
                vertical = Spacings.Spacing03
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = language.name,
            style = TextStyles.SubTitle01,
            color = if (isSelectedLanguage) Colors.Green300 else Colors.BasicWhite
        )

        if (isSelectedLanguage)
            Icon(
                painter = painterResource(id = com.sandorln.design.R.drawable.ic_check),
                contentDescription = null,
                tint = Colors.Green300
            )
    }
}

@Composable
fun SelectedDevelopLanguageTag(
    modifier: Modifier = Modifier,
    selectedLanguage: ProgramLanguage? = null,
    onClickSelectDevelopLanguageDialog: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Radius.Radius05),
        border = if (selectedLanguage != null) BorderStroke(1.dp, Colors.OverlayGreen50) else null,
        colors = CardDefaults.cardColors(contentColor = Colors.Gray700, containerColor = Colors.Gray700)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = Spacings.Spacing01, horizontal = 10.dp)
                .clickable {
                    onClickSelectDevelopLanguageDialog.invoke()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 2.dp),
                text = selectedLanguage?.name ?: "개발언어",
                style = TextStyles.SubTitle03
            )
            Icon(
                modifier = Modifier.size(IconSize.MediumSize),
                painter = painterResource(id = com.sandorln.design.R.drawable.ic_chevron_down),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun DevelopLanguageItemPreview() {
    GodadviceTheme {
        Surface {
            DevelopLanguageItem(ProgramLanguage())
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun SelectDevelopLanguageBottomSheetDialogPreview() {
    GodadviceTheme {
        Surface {
            SelectDevelopLanguageBottomSheetDialog()
        }
    }
}

@Preview(showBackground = false)
@Composable
internal fun SelectDevelopLanguageBodyPreview() {
    GodadviceTheme {
        Surface {
            SelectDevelopLanguageBody()
        }
    }
}