package com.sandorln.item.ui.builder.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandorln.design.R as DesignR
import com.sandorln.design.component.BaseBitmapImage
import com.sandorln.design.component.BaseContentWithMotionToolbar
import com.sandorln.design.component.BaseSearchTextEditor
import com.sandorln.design.component.toast.BaseToast
import com.sandorln.design.component.toast.BaseToastType
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.item.R as ItemR
import com.sandorln.item.ui.dialog.ItemDetailDialog
import com.sandorln.item.ui.home.baseItemList
import com.sandorln.item.util.getIconRes
import com.sandorln.item.util.getTitleStringRes
import com.sandorln.model.data.item.ItemData
import com.sandorln.model.type.ChampionTag
import kotlin.math.floor
import kotlin.math.max

@Composable
fun ItemBuilderEditScreen(
    viewModel: ItemBuilderEditViewModel = hiltViewModel(),
    onBackStack: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(true) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is ItemBuilderEditSideEffect.ShowToast -> {
                    BaseToast(context, BaseToastType.WARNING, sideEffect.message).show()
                }
                is ItemBuilderEditSideEffect.ShowError -> {
                    BaseToast(context, BaseToastType.WARNING, context.getString(sideEffect.messageResId)).show()
                }
                ItemBuilderEditSideEffect.SaveSuccess -> {
                    BaseToast(context, BaseToastType.OKAY, context.getString(ItemR.string.item_builder_saved)).show()
                    onBackStack.invoke()
                }
            }
        }
    }

    val availableTags = remember {
        listOf(
            ChampionTag.Tank,
            ChampionTag.Fighter,
            ChampionTag.Mage,
            ChampionTag.Assassin,
            ChampionTag.Marksman,
            ChampionTag.Support
        )
    }

    val bootsTitle = stringResource(id = ItemR.string.item_boots)
    val consumableTitle = stringResource(id = ItemR.string.item_consumable)
    val normalTitle = stringResource(id = ItemR.string.item_normal)
    val epicTitle = stringResource(id = ItemR.string.item_epic)
    val legendTitle = stringResource(id = ItemR.string.item_legend)
    val orrnTitle = stringResource(id = ItemR.string.item_orrn)

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        BaseContentWithMotionToolbar(
            headerMaxHeight = 220.dp,
            headerMinHeight = Dimens.BASE_TOOLBAR_HEIGHT,
            innerPadding = innerPadding,
            startConstraintSet = ConstraintSetScope::itemBuilderEditStart,
            endConstraintSet = ConstraintSetScope::itemBuilderEditEnd,
            headerContent = { progress ->
                // Dismiss keyboard when collapsing
                LaunchedEffect(progress) {
                    if (progress > 0.05f) {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                }

                IconButton(
                    modifier = Modifier
                        .layoutId(ItemBuilderEditMotionRefId.BACK)
                        .padding(start = Spacings.Spacing02)
                        .size(IconSize.XLargeSize),
                    onClick = onBackStack
                ) {
                    Icon(
                        modifier = Modifier.size(IconSize.XLargeSize),
                        painter = painterResource(id = DesignR.drawable.ic_chevron_left),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }

                TextButton(
                    modifier = Modifier
                        .layoutId(ItemBuilderEditMotionRefId.SAVE)
                        .padding(end = Spacings.Spacing02),
                    enabled = uiState.canSave,
                    onClick = { viewModel.sendAction(ItemBuilderEditAction.Save) }
                ) {
                    Text(
                        text = stringResource(id = ItemR.string.item_builder_save),
                        style = TextStyles.SubTitle01,
                        color = if (uiState.canSave) Colors.Gold02 else Colors.Gray05
                    )
                }

                Text(
                    modifier = Modifier
                        .layoutId(ItemBuilderEditMotionRefId.COLLAPSED_TITLE)
                        .padding(horizontal = Spacings.Spacing02),
                    text = uiState.title.ifBlank { stringResource(id = ItemR.string.item_builder_title) },
                    style = TextStyles.SubTitle01,
                    color = Colors.BasicWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(
                    modifier = Modifier.layoutId(ItemBuilderEditMotionRefId.DIVIDER),
                    color = Colors.Blue05
                )

                Column(
                    modifier = Modifier
                        .layoutId(ItemBuilderEditMotionRefId.EXPANDED_AREA)
                        .padding(horizontal = Spacings.Spacing03, vertical = Spacings.Spacing01),
                    verticalArrangement = Arrangement.spacedBy(Spacings.Spacing02)
                ) {
                    // Title Input Field
                    ItemBuildTitleInput(
                        title = uiState.title,
                        onTitleChange = { viewModel.sendAction(ItemBuilderEditAction.ChangeTitle(it)) }
                    )

                    // Position Chips Section
                    Text(
                        text = stringResource(id = ItemR.string.item_builder_position_title),
                        style = TextStyles.Body03,
                        color = Colors.Gold02
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
                    ) {
                        availableTags.forEach { tag ->
                            val isSelected = uiState.selectedPositions.contains(tag)
                            ItemPositionChip(
                                tag = tag,
                                isSelected = isSelected,
                                onClick = { viewModel.sendAction(ItemBuilderEditAction.TogglePosition(tag)) }
                            )
                        }
                    }
                }
            },
            bodyContent = {
                val lazyListState = rememberLazyListState()

                // Keyboard hide on scroll
                LaunchedEffect(lazyListState.isScrollInProgress) {
                    if (lazyListState.isScrollInProgress) {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            })
                        }
                ) {
                    val spanCount = max(1, floor(this.maxWidth / IconSize.XXLargeSize).toInt())
                    LaunchedEffect(spanCount) {
                        viewModel.sendAction(ItemBuilderEditAction.ChangeSpan(spanCount))
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        // 1. Fixed Slots Area directly below TopBar
                        ItemBuildFixedSlots(
                            selectedItems = uiState.selectedItems,
                            currentSpriteMap = uiState.currentSpriteMap,
                            onRemoveSlot = { index ->
                                viewModel.sendAction(ItemBuilderEditAction.RemoveItemByIndex(index))
                            }
                        )

                        HorizontalDivider(color = Colors.Blue05)

                        // 2. Lower Catalog Search & Item List
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacings.Spacing03, vertical = Spacings.Spacing02)
                        ) {
                            BaseSearchTextEditor(
                                modifier = Modifier.fillMaxWidth(),
                                hint = stringResource(id = ItemR.string.search_item),
                                onChangeTextListener = { keyword ->
                                    viewModel.sendAction(ItemBuilderEditAction.ChangeSearchKeyword(keyword))
                                }
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            state = lazyListState
                        ) {
                            if (uiState.bootItemList.isNotEmpty())
                                baseItemList(
                                    title = bootsTitle,
                                    spanCount = spanCount,
                                    spriteMap = uiState.currentSpriteMap,
                                    itemChunkList = uiState.bootItemList,
                                    onClickItem = { item ->
                                        viewModel.sendAction(ItemBuilderEditAction.SelectItemId(item.id))
                                    }
                                )

                            if (uiState.consumableItemList.isNotEmpty())
                                baseItemList(
                                    title = consumableTitle,
                                    spanCount = spanCount,
                                    spriteMap = uiState.currentSpriteMap,
                                    itemChunkList = uiState.consumableItemList,
                                    onClickItem = { item ->
                                        viewModel.sendAction(ItemBuilderEditAction.SelectItemId(item.id))
                                    }
                                )

                            if (uiState.normalItemList.isNotEmpty())
                                baseItemList(
                                    title = normalTitle,
                                    spanCount = spanCount,
                                    spriteMap = uiState.currentSpriteMap,
                                    itemChunkList = uiState.normalItemList,
                                    onClickItem = { item ->
                                        viewModel.sendAction(ItemBuilderEditAction.SelectItemId(item.id))
                                    }
                                )

                            if (uiState.epicItemList.isNotEmpty())
                                baseItemList(
                                    title = epicTitle,
                                    spanCount = spanCount,
                                    spriteMap = uiState.currentSpriteMap,
                                    itemChunkList = uiState.epicItemList,
                                    onClickItem = { item ->
                                        viewModel.sendAction(ItemBuilderEditAction.SelectItemId(item.id))
                                    }
                                )

                            if (uiState.legendItemList.isNotEmpty())
                                baseItemList(
                                    title = legendTitle,
                                    spanCount = spanCount,
                                    spriteMap = uiState.currentSpriteMap,
                                    itemChunkList = uiState.legendItemList,
                                    onClickItem = { item ->
                                        viewModel.sendAction(ItemBuilderEditAction.SelectItemId(item.id))
                                    }
                                )

                            if (uiState.orrnItemList.isNotEmpty())
                                baseItemList(
                                    title = orrnTitle,
                                    spanCount = spanCount,
                                    spriteMap = uiState.currentSpriteMap,
                                    itemChunkList = uiState.orrnItemList,
                                    onClickItem = { item ->
                                        viewModel.sendAction(ItemBuilderEditAction.SelectItemId(item.id))
                                    }
                                )

                            item {
                                Spacer(modifier = Modifier.height(Spacings.Spacing05))
                            }
                        }
                    }
                }
            }
        )

        // Item Detail Dialog in Builder Mode
        if (uiState.selectedItemId != null) {
            ItemDetailDialog(
                versionName = uiState.currentVersionName,
                selectedItemId = uiState.selectedItemId ?: "",
                isBuilderMode = true,
                onDismissRequest = {
                    viewModel.sendAction(ItemBuilderEditAction.SelectItemId(null))
                },
                onChangeSelectItem = {
                    viewModel.sendAction(ItemBuilderEditAction.SelectItemId(it))
                },
                onAddItemBuildData = { itemData ->
                    viewModel.sendAction(ItemBuilderEditAction.AddItem(itemData))
                }
            )
        }
    }
}

@Composable
private fun ItemBuildTitleInput(
    title: String,
    onTitleChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .background(Colors.Blue07, RoundedCornerShape(Radius.Radius04))
            .border(
                width = 1.dp,
                color = if (isFocused) Colors.Gold02 else Colors.Blue05,
                shape = RoundedCornerShape(Radius.Radius04)
            )
            .padding(horizontal = Spacings.Spacing03, vertical = Spacings.Spacing01),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { isFocused = it.isFocused },
            value = title,
            onValueChange = onTitleChange,
            cursorBrush = SolidColor(Colors.Gold02),
            maxLines = 1,
            textStyle = TextStyles.SubTitle01.copy(color = Colors.BasicWhite),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            decorationBox = { innerTextField ->
                if (title.isEmpty()) {
                    Text(
                        text = stringResource(id = ItemR.string.item_builder_name_hint),
                        style = TextStyles.SubTitle01,
                        color = Colors.Gray05
                    )
                }
                innerTextField.invoke()
            }
        )

        if (title.isNotEmpty()) {
            Icon(
                modifier = Modifier
                    .size(IconSize.SmallSize)
                    .clickable { onTitleChange("") },
                painter = painterResource(id = DesignR.drawable.ic_clear),
                contentDescription = null,
                tint = Colors.Gray05
            )
        }
    }
}

@Composable
private fun ItemPositionChip(
    tag: ChampionTag,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(Radius.Radius04)
    val backgroundColor = if (isSelected) Colors.Gold02 else Colors.Blue07
    val contentColor = if (isSelected) Colors.BasicBlack else Colors.Gray04
    val borderColor = if (isSelected) Colors.Gold02 else Colors.Gray06

    Row(
        modifier = Modifier
            .border(1.dp, borderColor, shape)
            .background(backgroundColor, shape)
            .clickable { onClick.invoke() }
            .padding(horizontal = Spacings.Spacing02, vertical = Spacings.Spacing01),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
    ) {
        Icon(
            modifier = Modifier.size(IconSize.SmallSize),
            painter = painterResource(id = tag.getIconRes()),
            contentDescription = null,
            tint = contentColor
        )
        Text(
            text = stringResource(id = tag.getTitleStringRes()),
            style = TextStyles.Body03,
            color = contentColor
        )
    }
}

@Composable
private fun ItemBuildFixedSlots(
    selectedItems: List<ItemData>,
    currentSpriteMap: Map<String, android.graphics.Bitmap?>,
    onRemoveSlot: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colors.Blue06)
            .padding(vertical = Spacings.Spacing02),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacings.Spacing01)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacings.Spacing02, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (index in 0 until 6) {
                val item = selectedItems.getOrNull(index)
                ItemBuildSlotItem(
                    index = index,
                    item = item,
                    currentSpriteMap = currentSpriteMap,
                    onClickRemove = { onRemoveSlot(index) }
                )
            }
        }
    }
}

@Composable
private fun ItemBuildSlotItem(
    index: Int,
    item: ItemData?,
    currentSpriteMap: Map<String, android.graphics.Bitmap?>,
    onClickRemove: () -> Unit
) {
    val slotSize = 48.dp
    val shape = RoundedCornerShape(Radius.Radius03)

    if (item != null) {
        Box(
            modifier = Modifier
                .size(slotSize)
                .clickable { onClickRemove.invoke() }
        ) {
            val bitmap = item.image.getImageBitmap(currentSpriteMap)
            BaseBitmapImage(
                modifier = Modifier
                    .matchParentSize()
                    .border(1.dp, Colors.Gold02, shape),
                bitmap = bitmap,
                loadingDrawableId = DesignR.drawable.ic_main_item,
                imageSize = slotSize
            )

            // Remove badge
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
                    .background(Colors.BasicBlack.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(
                    modifier = Modifier
                        .size(10.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = DesignR.drawable.ic_clear),
                    contentDescription = null,
                    tint = Colors.BasicWhite
                )
            }
        }
    } else {
        // Placeholder empty slot
        Box(
            modifier = Modifier
                .size(slotSize)
                .background(Colors.Blue07, shape)
                .border(1.dp, Colors.Blue05, shape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${index + 1}",
                style = TextStyles.Body03.copy(fontSize = 10.sp),
                color = Colors.Gray06
            )
        }
    }
}
