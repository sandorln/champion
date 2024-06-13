package com.sandorln.setting.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.LolChampionThemePreview
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.setting.R

@Composable
fun SettingHomeScreen(
    moveToLicensesScreen: () -> Unit,
    moveToLolPatchNoteScreen: () -> Unit,
    moveToInitialQuizScreen: () -> Unit,
    settingHomeViewModel: SettingHomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val packageName = context.packageName
    val versionName = context.packageManager
        .getPackageInfo(packageName, 0)
        .versionName

    val score by settingHomeViewModel.initialGameScore.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SettingMenuBody(
            title = stringResource(id = R.string.menu_lol_patch_notes),
            onClick = moveToLolPatchNoteScreen
        )

        SettingMenuBody(
            title = stringResource(id = R.string.menu_open_licenses),
            onClick = moveToLicensesScreen
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "현재 점수 : $score",
            style = TextStyles.Title03,
            textAlign = TextAlign.Center
        )

        SettingMenuBody(
            title = "초성 게임 - TEST",
            onClick = moveToInitialQuizScreen
        )

        Text(
            modifier = Modifier
                .padding(top = Spacings.Spacing03)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(
                id = R.string.app_version,
                versionName
            ),
            style = TextStyles.Body03,
            color = Colors.Gray04
        )
    }
}

@Composable
fun SettingMenuBody(
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .heightIn(min = Dimens.SETTING_MENU_HEIGHT_MIN)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            text = title,
            style = TextStyles.SubTitle02,
            color = Colors.Gray03
        )
        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
fun SettingHomeScreenPreview() {
    LolChampionThemePreview {
        SettingHomeScreen(
            moveToLicensesScreen = {},
            moveToLolPatchNoteScreen = {},
            moveToInitialQuizScreen = {}
        )
    }
}