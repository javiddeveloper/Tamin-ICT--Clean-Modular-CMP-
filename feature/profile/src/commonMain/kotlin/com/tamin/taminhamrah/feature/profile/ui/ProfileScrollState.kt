package com.tamin.taminhamrah.feature.profile.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp as dpLerp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.ui.theme.Spacing

@Stable
class ProfileScrollState(
    val lazyListState: LazyListState,
    private val density: Density
) {
    val maxScrollPx = with(density) { 120.dp.toPx() }

    var progress by mutableFloatStateOf(0f)
        private set

    suspend fun observeScroll() {
        snapshotFlow {
            val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
            val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset

            if (firstVisibleItemIndex > 0) {
                1f
            } else {
                (firstVisibleItemScrollOffset / maxScrollPx).coerceIn(0f, 1f)
            }
        }.collect { currentProgress ->
            progress = currentProgress
        }
    }
}

@Composable
fun rememberProfileScrollState(
    lazyListState: LazyListState = rememberLazyListState()
): ProfileScrollState {
    val density = LocalDensity.current
    val state = remember(lazyListState, density) {
        ProfileScrollState(lazyListState, density)
    }
    LaunchedEffect(state) {
        state.observeScroll()
    }
    return state
}

@Stable
class ProfileMotionState(
    private val scrollProgress: Float
) {
    val headerProgress: Float
        get() = scrollProgress.coerceIn(0f, 1f)

    val validationProgress: Float
        get() = (scrollProgress - 1f).coerceIn(0f, 1f)

    val headerTranslationY: Dp
        get() = (headerProgress * -50f).dp

    val rowTranslationY: Dp
        get() = (headerProgress * -45f).dp

    val avatarScale: Float
        get() = lerp(1f, 0.8f, headerProgress)

    val topBarBottomPadding: Dp
        get() = dpLerp(Spacing.xxxl, Spacing.sm, headerProgress)

    val extraSpacerHeight: Dp
        get() = dpLerp(Spacing.xxxl, 35.dp, headerProgress)

    val rowTopPadding: Dp
        get() = dpLerp(Spacing.xl, Spacing.none, headerProgress)

    val topBarContentSpacerHeight: Dp
        get() = dpLerp(Spacing.sm, Spacing.none, headerProgress)

    val titleAlpha: Float
        get() = (1f - (headerProgress - 0.2f) * 2f).coerceIn(0f, 1f)

    val titleTranslationY: Dp
        get() = (headerProgress * -20f).dp

    val validationAlpha: Float
        get() = 1f - validationProgress

    val validationTranslationY: Dp
        get() = (validationProgress * 24f).dp

    val validationScale: Float
        get() = lerp(1f, 0.9f, validationProgress)
}
