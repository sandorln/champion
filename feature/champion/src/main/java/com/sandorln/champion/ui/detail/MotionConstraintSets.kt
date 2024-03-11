package com.sandorln.champion.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayoutScope
import com.sandorln.design.R
import com.sandorln.design.component.BaseChampionSplashImage
import com.sandorln.design.component.BaseCircleIconImage
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles
import com.sandorln.design.theme.addShadow

internal enum class MotionRefIdType {
    Splash, Icon, Name, Title, Back, BottomDivider, Version, HeaderBrush
}

internal fun ConstraintSetScope.championDetailStart(
    headerRef: ConstrainedLayoutReference,
    bodyRef: ConstrainedLayoutReference
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
    bodyRef: ConstrainedLayoutReference
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
        top.linkTo(headerRef.top)
        bottom.linkTo(headerRef.bottom)
        alpha = 1f
    }
    constrain(titleRef) {
        height = Dimension.fillToConstraints
        start.linkTo(iconRef.end, Spacings.Spacing02)
        top.linkTo(headerRef.top)
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
        top.linkTo(headerRef.top)
        bottom.linkTo(headerRef.bottom)
    }

    constrain(headerBrushRef) {
        height = Dimension.fillToConstraints
        top.linkTo(headerRef.top)
        bottom.linkTo(headerRef.bottom)
    }
}