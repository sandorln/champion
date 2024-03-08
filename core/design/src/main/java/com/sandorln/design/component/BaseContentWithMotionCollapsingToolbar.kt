package com.sandorln.design.component

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutScope
import com.sandorln.design.theme.Dimens

private enum class MotionRefIds {
    HEADER_AREA, BODY
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BaseContentWithMotionToolbar(
    headerRatio: String = "",
    headerMaxHeight: Dp = 300.dp,
    headerMinHeight: Dp = Dimens.BASE_TOOLBAR_HEIGHT,
    startConstraintSet: ConstraintSetScope.(
        headerRef: ConstrainedLayoutReference,
        bodyRef: ConstrainedLayoutReference
    ) -> Unit,
    endConstraintSet: ConstraintSetScope.(
        headerRef: ConstrainedLayoutReference,
        bodyRef: ConstrainedLayoutReference
    ) -> Unit,
    headerContent: @Composable MotionLayoutScope.(progress: Float) -> Unit = {},
    bodyContent: @Composable BoxScope.(progress: Float) -> Unit = {}
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val heightInPx = with(LocalDensity.current) { maxHeight.toPx() }

        val swipingState = rememberSwipeableState(initialValue = SwipingStates.EXPANDED)
        val nestedScrollConnection = createNestScrollConnection(swipingState)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .swipeable(
                    state = swipingState,
                    thresholds = { _, _ -> FractionalThreshold(0.05f) },
                    orientation = Orientation.Vertical,
                    anchors = mapOf(
                        0f to SwipingStates.COLLAPSED,
                        heightInPx to SwipingStates.EXPANDED,
                    )
                )
                .nestedScroll(nestedScrollConnection)
        ) {
            val computedProgress by remember {
                derivedStateOf {
                    if (swipingState.progress.to == SwipingStates.COLLAPSED)
                        swipingState.progress.fraction
                    else
                        1f - swipingState.progress.fraction
                }
            }

            MotionLayout(
                modifier = Modifier.fillMaxSize(),
                progress = computedProgress,
                start = ConstraintSet {
                    val headerAreaRef = createRefFor(MotionRefIds.HEADER_AREA)
                    val bodyRef = createRefFor(MotionRefIds.BODY)
                    constrain(headerAreaRef) {
                        this.width = Dimension.matchParent
                        this.height = if (headerRatio.isNotEmpty())
                            Dimension.ratio(headerRatio)
                        else
                            Dimension.value(headerMaxHeight)
                    }
                    constrain(bodyRef) {
                        this.width = Dimension.matchParent
                        this.height = Dimension.fillToConstraints
                        this.top.linkTo(headerAreaRef.bottom, 0.dp)
                        this.bottom.linkTo(parent.bottom, 0.dp)
                    }
                    startConstraintSet.invoke(this, headerAreaRef, bodyRef)
                },
                end = ConstraintSet {
                    val headerAreaRef = createRefFor(MotionRefIds.HEADER_AREA)
                    val bodyRef = createRefFor(MotionRefIds.BODY)
                    constrain(headerAreaRef) {
                        this.height = Dimension.value(headerMinHeight)
                    }
                    constrain(bodyRef) {
                        this.width = Dimension.matchParent
                        this.height = Dimension.fillToConstraints
                        this.top.linkTo(headerAreaRef.bottom, 0.dp)
                        this.bottom.linkTo(parent.bottom, 0.dp)
                    }
                    endConstraintSet.invoke(this, headerAreaRef, bodyRef)
                }
            ) {
                Box(modifier = Modifier.layoutId(MotionRefIds.HEADER_AREA))
                headerContent.invoke(this, computedProgress)

                Box(modifier = Modifier.layoutId(MotionRefIds.BODY)) {
                    bodyContent.invoke(this, computedProgress)
                }
            }
        }
    }
}

enum class SwipingStates {
    EXPANDED,
    COLLAPSED
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun createNestScrollConnection(
    swipingState: SwipeableState<SwipingStates>
) = remember {
    object : NestedScrollConnection {
        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            val delta = available.y
            return if (delta < 0) {
                swipingState.performDrag(delta).toOffset()
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            val delta = available.y
            return swipingState.performDrag(delta).toOffset()
        }

        override suspend fun onPostFling(
            consumed: Velocity,
            available: Velocity
        ): Velocity {
            swipingState.performFling(velocity = available.y)
            return super.onPostFling(consumed, available)
        }

        private fun Float.toOffset() = Offset(0f, this)
    }
}