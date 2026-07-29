package com.tamin.taminhamrah.ui.motion

import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.ExperimentalFoundationApi
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberMotionSnapFlingBehavior(
    lazyListState: LazyListState,
    motionState: ScrollMotionState,
    decayAnimationSpec: DecayAnimationSpec<Float>,
    velocityThreshold: Float = 500f
): FlingBehavior {
    return remember(lazyListState, motionState, decayAnimationSpec, velocityThreshold) {
        snapFlingBehavior(
            snapLayoutInfoProvider = object : SnapLayoutInfoProvider {
                override fun calculateSnapOffset(velocity: Float): Float {
                    if (lazyListState.firstVisibleItemIndex == 0) {
                        val currentOffset = lazyListState.firstVisibleItemScrollOffset.toFloat()
                        val maxScrollPx = motionState.maxMotionDistancePx
                        if (currentOffset > 0 && currentOffset < maxScrollPx) {
                            val targetOffset = if (abs(velocity) > velocityThreshold) {
                                if (velocity > 0) maxScrollPx else 0f
                            } else {
                                if (currentOffset < maxScrollPx / 2f) 0f else maxScrollPx
                            }
                            return targetOffset - currentOffset
                        }
                    }
                    return 0f
                }
            },
            decayAnimationSpec = decayAnimationSpec,
            snapAnimationSpec = spring(stiffness = Spring.StiffnessLow)
        )
    }
}
