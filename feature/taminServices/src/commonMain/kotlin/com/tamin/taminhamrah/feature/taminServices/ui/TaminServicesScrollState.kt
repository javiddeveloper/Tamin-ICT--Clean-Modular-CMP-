package com.tamin.taminhamrah.feature.taminServices.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp as dpLerp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.ui.theme.Spacing

@Stable
class TaminServicesScrollState(
    val lazyListState: LazyListState
) {
    var collapseDistancePx by mutableFloatStateOf(Float.MAX_VALUE)

    val progress: Float by derivedStateOf {
        val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
        val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset

        if (firstVisibleItemIndex > 0) {
            1f
        } else {
            (firstVisibleItemScrollOffset / collapseDistancePx).coerceIn(0f, 1f)
        }
    }
}

@Composable
fun rememberTaminServicesScrollState(
    lazyListState: LazyListState = rememberLazyListState()
): TaminServicesScrollState {
    return remember(lazyListState) {
        TaminServicesScrollState(lazyListState)
    }
}

@Stable
class TaminServicesMotionState(
    private val scrollProgressProvider: () -> Float
) {
    private val scrollProgress: Float
        get() = scrollProgressProvider()
    val headerProgress: Float
        get() = scrollProgress.coerceIn(0f, 1f)
    val headerTopPadding: Dp
        get() = dpLerp(Spacing.xxl, Spacing.xxxl, headerProgress)
}

@Composable
fun rememberTaminServicesMotionState(
    scrollState: TaminServicesScrollState
): TaminServicesMotionState {
    return remember(scrollState) {
        TaminServicesMotionState(scrollProgressProvider = { scrollState.progress })
    }
}
