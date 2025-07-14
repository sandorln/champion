package com.sandorln.setting.ui.lolpatch

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.component.BaseToolbar
import com.sandorln.design.component.toast.BaseToast
import com.sandorln.design.component.toast.BaseToastType
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.setting.R
import kotlinx.coroutines.launch

@Composable
fun LolPatchNoteScreen(
    lolPatchNoteViewModel: LolPatchNoteViewModel = hiltViewModel(),
    onBackStack: () -> Unit
) {
    val lolPatchList by lolPatchNoteViewModel.allVersionList.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
            BaseToolbar(
                title = stringResource(id = R.string.lol_patch_note_title),
                onClickStartIcon = onBackStack
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(lolPatchList.size) { index ->
                    LolPatchNoteBody(lolPatch = lolPatchList[index]) { major1: Int, minor1: Int ->
                        coroutineScope.launch {
                            val url = lolPatchNoteViewModel.getPatchNoteUrl(major1, minor1)
                            if (url.isNotEmpty())
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            else
                                BaseToast(context, BaseToastType.WARNING, context.getString(R.string.lol_patch_url_empty))
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
                }
            }
        }
    }
}

@Composable
fun LolPatchNoteBody(
    lolPatch: Pair<Int, Int> = 0 to 0,
    onClickAction: (major1: Int, minor1: Int) -> Unit
) {
    val title = stringResource(id = R.string.lol_patch_note_item_title, lolPatch.first, lolPatch.second)

    Box(
        modifier = Modifier
            .clickable { onClickAction.invoke(lolPatch.first, lolPatch.second) }
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