package com.sandorln.item.ui.builder.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.R as DesignR
import com.sandorln.design.component.BaseButton
import com.sandorln.design.component.dialog.BaseAlertTextDialog
import com.sandorln.design.component.toast.BaseToast
import com.sandorln.design.component.toast.BaseToastType
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.item.R as ItemR

@Composable
fun ItemBuilderListScreen(
    viewModel: ItemBuilderListViewModel = hiltViewModel(),
    onBackStack: () -> Unit = {},
    moveToItemBuilderEdit: (buildId: Long) -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(true) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is ItemBuilderListSideEffect.ShowToast -> {
                    BaseToast(context, BaseToastType.WARNING, sideEffect.message).show()
                }
                is ItemBuilderListSideEffect.ShowError -> {
                    BaseToast(context, BaseToastType.WARNING, context.getString(sideEffect.messageResId)).show()
                }
                ItemBuilderListSideEffect.BuildDeleted -> {
                    BaseToast(context, BaseToastType.OKAY, context.getString(ItemR.string.item_builder_deleted)).show()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Colors.Blue06)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.BASE_TOOLBAR_HEIGHT)
                        .padding(horizontal = Spacings.Spacing02),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        modifier = Modifier.size(IconSize.XLargeSize),
                        onClick = onBackStack
                    ) {
                        Icon(
                            modifier = Modifier.size(IconSize.XLargeSize),
                            painter = painterResource(id = DesignR.drawable.ic_chevron_left),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }

                    Text(
                        text = "${stringResource(id = ItemR.string.item_builder_title)} ${uiState.countText}",
                        style = TextStyles.SubTitle01,
                        color = Colors.BasicWhite
                    )

                    IconButton(
                        modifier = Modifier.size(IconSize.XLargeSize),
                        onClick = {
                            if (uiState.isMaxBuildCountReached) {
                                BaseToast(
                                    context,
                                    BaseToastType.WARNING,
                                    context.getString(ItemR.string.item_builder_max_count_reached)
                                ).show()
                            } else {
                                moveToItemBuilderEdit.invoke(0L)
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(IconSize.LargeSize),
                            painter = painterResource(id = DesignR.drawable.ic_add),
                            contentDescription = null,
                            tint = if (uiState.isMaxBuildCountReached) Colors.Gray06 else Colors.Gold02
                        )
                    }
                }
                HorizontalDivider(color = Colors.Blue05)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.buildList.isEmpty() && !uiState.isLoading) {
                // Empty State View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacings.Spacing05),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(64.dp),
                        painter = painterResource(id = DesignR.drawable.ic_pencil),
                        contentDescription = null,
                        tint = Colors.Gray06
                    )

                    Spacer(modifier = Modifier.height(Spacings.Spacing03))

                    Text(
                        text = stringResource(id = ItemR.string.item_builder_empty),
                        style = TextStyles.Body01,
                        color = Colors.Gray05,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(Spacings.Spacing05))

                    BaseButton(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(44.dp),
                        title = stringResource(id = ItemR.string.item_builder_new),
                        isEnabled = true,
                        onClickBtn = { moveToItemBuilderEdit.invoke(0L) }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = Spacings.Spacing03,
                        vertical = Spacings.Spacing03
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacings.Spacing03)
                ) {
                    items(
                        items = uiState.buildList,
                        key = { it.itemBuild.id }
                    ) { uiModel ->
                        ItemBuilderCard(
                            uiModel = uiModel,
                            currentSpriteMap = uiState.currentSpriteMap,
                            onClickCard = {
                                moveToItemBuilderEdit.invoke(uiModel.itemBuild.id)
                            },
                            onClickEdit = {
                                moveToItemBuilderEdit.invoke(uiModel.itemBuild.id)
                            },
                            onClickDelete = {
                                viewModel.sendAction(ItemBuilderListAction.ShowDeleteDialog(uiModel.itemBuild))
                            }
                        )
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (uiState.deleteTargetBuild != null) {
            BaseAlertTextDialog(
                title = stringResource(id = ItemR.string.item_builder_delete),
                body = stringResource(id = ItemR.string.item_builder_delete_confirm),
                submitBtn = stringResource(id = ItemR.string.item_builder_delete),
                cancelBtn = stringResource(id = ItemR.string.cancel),
                onDismissRequest = {
                    viewModel.sendAction(ItemBuilderListAction.ShowDeleteDialog(null))
                },
                onClickCancel = {
                    viewModel.sendAction(ItemBuilderListAction.ShowDeleteDialog(null))
                },
                onClickSubmit = {
                    val targetId = uiState.deleteTargetBuild?.id ?: 0L
                    viewModel.sendAction(ItemBuilderListAction.DeleteItemBuild(targetId))
                }
            )
        }
    }
}
