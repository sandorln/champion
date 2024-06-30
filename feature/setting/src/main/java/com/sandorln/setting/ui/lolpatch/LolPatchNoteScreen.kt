package com.sandorln.setting.ui.lolpatch

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.component.BaseToolbar
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.setting.R

@Composable
fun LolPatchNoteScreen(
    lolPatchNoteViewModel: LolPatchNoteViewModel = hiltViewModel(),
    onBackStack: () -> Unit
) {
    val lolPatchList by lolPatchNoteViewModel.allVersionList.collectAsState()

    Column {
        BaseToolbar(
            title = stringResource(id = R.string.lol_patch_note_title),
            onClickStartIcon = onBackStack
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(lolPatchList.size) { index ->
                LolPatchNoteBody(lolPatchList[index])
            }
        }
    }
}

@Composable
fun LolPatchNoteBody(
    lolPatch: Pair<Int, Int> = 0 to 0
) {
    val context = LocalContext.current
    val urlId = if (lolPatch.first < 14 || (lolPatch.first == 14 && lolPatch.second < 13))
        R.string.lol_patch_notes_url
    else
        R.string.lol_patch_notes_url_14_12_up
    val url = stringResource(id = urlId, lolPatch.first, lolPatch.second)
    val title = stringResource(id = R.string.lol_patch_note_item_title, lolPatch.first, lolPatch.second)

    Box(
        modifier = Modifier
            .clickable {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .heightIn(min = Dimens.SETTING_MENU_HEIGHT_MIN)
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(
                Spacings.Spacing02,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = title,
                style = TextStyles.SubTitle02,
                color = Colors.Gray03
            )

            Icon(
                modifier = Modifier.size(IconSize.SmallSize),
                painter = painterResource(id = com.sandorln.design.R.drawable.ic_open_in_new),
                contentDescription = null,
                tint = Colors.Gray03
            )
        }
        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}