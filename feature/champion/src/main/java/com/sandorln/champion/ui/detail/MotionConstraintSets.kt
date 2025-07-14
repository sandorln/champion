package com.sandorln.champion.ui.detail

import androidx.compose.foundation.layout.PaddingValues
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import com.sandorln.design.theme.Spacings

internal enum class MotionRefIdType {
    Splash, Icon, Name, Title, Back, BottomDivider, Version, HeaderBrush
}

internal fun ConstraintSetScope.championDetailStart(
    headerRef: ConstrainedLayoutReference,
    bodyRef: ConstrainedLayoutReference,
    innerPadding: PaddingValues
) {
    val splashImgRef = createRefFor(MotionRefIdType.Splash)
    val iconRef = createRefFor(MotionRefIdType.Icon)
    val nameRef = createRefFor(MotionRefIdType.Name)
    val titleRef = createRefFor(MotionRefIdType.Title)
    val bottomDividerRef = createRefFor(MotionRefIdType.BottomDivider)
    val versionRef = createRefFor(MotionRefIdType.Version)
    val headerBrushRef = createRefFor(MotionRefIdType.HeaderBrush)

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

    constrain(versionRef) {
        width = Dimension.fillToConstraints
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        bottom.linkTo(titleRef.top)
    }

    constrain(headerBrushRef) {
        height = Dimension.fillToConstraints
        top.linkTo(headerRef.top)
        bottom.linkTo(headerRef.bottom)
    }
}

internal fun ConstraintSetScope.championDetailEnd(
    headerRef: ConstrainedLayoutReference,
    bodyRef: ConstrainedLayoutReference,
    innerPadding: PaddingValues
) {
    val splashImgRef = createRefFor(MotionRefIdType.Splash)
    val iconRef = createRefFor(MotionRefIdType.Icon)
    val nameRef = createRefFor(MotionRefIdType.Name)
    val titleRef = createRefFor(MotionRefIdType.Title)
    val backRef = createRefFor(MotionRefIdType.Back)
    val bottomDividerRef = createRefFor(MotionRefIdType.BottomDivider)
    val versionRef = createRefFor(MotionRefIdType.Version)
    val headerBrushRef = createRefFor(MotionRefIdType.HeaderBrush)

    constrain(splashImgRef) {
        width = Dimension.matchParent
        height = Dimension.fillToConstraints
        top.linkTo(headerRef.top)
        bottom.linkTo(headerRef.bottom)
        alpha = 0f
    }
    constrain(iconRef) {
        start.linkTo(backRef.end, Spacings.Spacing02)
        top.linkTo(headerRef.top, innerPadding.calculateTopPadding())
        bottom.linkTo(headerRef.bottom)
        alpha = 1f
    }
    constrain(titleRef) {
        height = Dimension.fillToConstraints
        start.linkTo(iconRef.end, Spacings.Spacing02)
        top.linkTo(headerRef.top, innerPadding.calculateTopPadding())
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

    constrain(versionRef) {
        width = Dimension.fillToConstraints
        end.linkTo(parent.end, Spacings.Spacing04)
        top.linkTo(headerRef.top, innerPadding.calculateTopPadding())
        bottom.linkTo(headerRef.bottom)
    }

    constrain(headerBrushRef) {
        height = Dimension.fillToConstraints
        top.linkTo(headerRef.top)
        bottom.linkTo(headerRef.bottom)
    }
}