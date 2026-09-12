package com.sandorln.item.ui.builder.edit

import androidx.compose.foundation.layout.PaddingValues
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import com.sandorln.design.theme.Spacings

internal enum class ItemBuilderEditMotionRefId {
    BACK, SAVE, COLLAPSED_TITLE, EXPANDED_AREA, DIVIDER
}

internal fun ConstraintSetScope.itemBuilderEditStart(
    headerRef: ConstrainedLayoutReference,
    bodyRef: ConstrainedLayoutReference,
    innerPadding: PaddingValues
) {
    val backRef = createRefFor(ItemBuilderEditMotionRefId.BACK)
    val saveRef = createRefFor(ItemBuilderEditMotionRefId.SAVE)
    val collapsedTitleRef = createRefFor(ItemBuilderEditMotionRefId.COLLAPSED_TITLE)
    val expandedAreaRef = createRefFor(ItemBuilderEditMotionRefId.EXPANDED_AREA)
    val dividerRef = createRefFor(ItemBuilderEditMotionRefId.DIVIDER)

    constrain(backRef) {
        start.linkTo(parent.start)
        top.linkTo(headerRef.top, innerPadding.calculateTopPadding())
    }
    constrain(saveRef) {
        end.linkTo(parent.end)
        top.linkTo(headerRef.top, innerPadding.calculateTopPadding())
    }
    constrain(collapsedTitleRef) {
        start.linkTo(backRef.end)
        end.linkTo(saveRef.start)
        top.linkTo(backRef.top)
        bottom.linkTo(backRef.bottom)
        width = Dimension.fillToConstraints
        alpha = 0f
    }
    constrain(expandedAreaRef) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(backRef.bottom, Spacings.Spacing00)
        bottom.linkTo(headerRef.bottom, Spacings.Spacing01)
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
        alpha = 1f
    }
    constrain(dividerRef) {
        bottom.linkTo(headerRef.bottom)
        width = Dimension.matchParent
        alpha = 0f
    }
}

internal fun ConstraintSetScope.itemBuilderEditEnd(
    headerRef: ConstrainedLayoutReference,
    bodyRef: ConstrainedLayoutReference,
    innerPadding: PaddingValues
) {
    val backRef = createRefFor(ItemBuilderEditMotionRefId.BACK)
    val saveRef = createRefFor(ItemBuilderEditMotionRefId.SAVE)
    val collapsedTitleRef = createRefFor(ItemBuilderEditMotionRefId.COLLAPSED_TITLE)
    val expandedAreaRef = createRefFor(ItemBuilderEditMotionRefId.EXPANDED_AREA)
    val dividerRef = createRefFor(ItemBuilderEditMotionRefId.DIVIDER)

    constrain(backRef) {
        start.linkTo(parent.start)
        top.linkTo(headerRef.top, innerPadding.calculateTopPadding())
        bottom.linkTo(headerRef.bottom)
    }
    constrain(saveRef) {
        end.linkTo(parent.end)
        top.linkTo(headerRef.top, innerPadding.calculateTopPadding())
        bottom.linkTo(headerRef.bottom)
    }
    constrain(collapsedTitleRef) {
        start.linkTo(backRef.end, Spacings.Spacing02)
        end.linkTo(saveRef.start, Spacings.Spacing02)
        top.linkTo(headerRef.top, innerPadding.calculateTopPadding())
        bottom.linkTo(headerRef.bottom)
        width = Dimension.fillToConstraints
        alpha = 1f
    }
    constrain(expandedAreaRef) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(backRef.bottom)
        width = Dimension.fillToConstraints
        alpha = 0f
    }
    constrain(dividerRef) {
        bottom.linkTo(headerRef.bottom)
        width = Dimension.matchParent
        alpha = 1f
    }
}
