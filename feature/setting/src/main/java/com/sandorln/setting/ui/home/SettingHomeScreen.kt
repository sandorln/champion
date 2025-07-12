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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.setting.R

@Composable
fun SettingHomeScreen(
    moveToLicensesScreen: () -> Unit,
    moveToLolPatchNoteScreen: () -> Unit,
) {
    val context = LocalContext.current
    val packageName = context.packageName
    val versionName = context.packageManager
        .getPackageInfo(packageName, 0)
        .versionName ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
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