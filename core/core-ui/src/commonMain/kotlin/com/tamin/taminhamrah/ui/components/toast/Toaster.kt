package com.tamin.taminhamrah.ui.components.toast

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.map
import kotlin.math.max

/**
 * The toaster, within a popup. It displays the toasts of [state], each as a [MorphingToast]
 * capsule that grows out of its status icon and shrinks back into it.
 *
 * @param state The state of the toaster, managing the toasts to be displayed.
 * @param modifier The modifier to be applied to the container.
 * @param maxVisibleToasts Maximum number of toasts visible at the same time.
 * @param expanded Whether toasts are expanded vertically instead of stacked.
 * @param swipeable Whether toasts can be dismissed with a swipe down gesture.
 * @param dismissOnClick Whether tapping a toast dismisses it.
 * @param colors The container/content/border colors of each toast.
 * @param containerPadding Padding for the toaster container.
 * @param widthPolicy The width policy for each toast. Only [ToastWidthPolicy.max] applies: a
 * capsule always hugs its message so that it can morph from and back into its icon.
 * @param alignment Alignment of the toaster within the popup.
 * @param offset Offset of the popup.
 * @param dismissPause When the dismiss timer of a toast is paused.
 * @param iconSlot Composable slot for the toast icon, drawn in the capsule's disc.
 * @param messageSlot Composable slot for the toast message.
 * @param actionSlot Composable slot for the toast action.
 * @param toastBox A wrapper for each toast.
 */
@Composable
fun Toaster(
    state: ToasterState,
    modifier: Modifier = Modifier,
    maxVisibleToasts: Int = 3,
    expanded: Boolean = false,
    swipeable: Boolean = true,
    dismissOnClick: Boolean = true,
    colors: @Composable (toast: Toast) -> TaminToastColors = { taminToastColors(it.type) },
    containerPadding: PaddingValues = PaddingValues(Spacing.none),
    widthPolicy: @Composable (toast: Toast) -> ToastWidthPolicy = { ToastWidthPolicy() },
    alignment: Alignment = Alignment.TopCenter,
    offset: IntOffset = IntOffset.Zero,
    dismissPause: ToastDismissPause = ToastDismissPause.OnInvisible,
    iconSlot: @Composable (toast: Toast) -> Unit = { TaminToastIcon(it.type) },
    messageSlot: @Composable (toast: Toast) -> Unit = { ToasterDefaults.messageSlot(it) },
    actionSlot: @Composable (toast: Toast) -> Unit = { ToasterDefaults.actionSlot(it) },
    toastBox: @Composable (toast: Toast, toastContent: @Composable () -> Unit) -> Unit =
        { _, content -> content() }
) {
    require(maxVisibleToasts > 0) { "maxVisibleToasts should be at least 1." }

    if (state.toasts.isEmpty()) return

    ToasterPopup(alignment = alignment, offset = offset) {
        val density = LocalDensity.current

        val lazyToasterBoxState = rememberLazyToasterBoxState(
            maxVisibleToasts = maxVisibleToasts,
            itemCountProvider = { state.toasts.size },
            key = { index -> state.toasts[index].toast.id },
            indexOfKey = { key -> state.toasts.indexOfFirst { it.toast.id == key } },
            isItemDismissed = { index -> state.toasts[index].isDismissed }
        )

        val itemHeightProvider = remember { ItemHeightProvider() }

        val toastTransformHelper = remember(density, maxVisibleToasts) {
            ToastTransformHelper(density = density, maxVisibleToasts = maxVisibleToasts)
        }

        LaunchedEffect(state.toasts) {
            state.dismissingToastsFlow()
                .collect { item ->
                    val visibleItemIndices = lazyToasterBoxState.visibleItemIndices()
                    val index = state.toasts.indexOf(item)
                    if (index !in visibleItemIndices) {
                        // Item dismissed but not currently visible, mark it as
                        // dismissed state and don't render it on UI
                        state.markDismissed(item.toast.id)
                    }
                }
        }

        ApplyToastDismissPause(
            state = state,
            toastDismissPause = dismissPause,
            lazyToasterBoxState = lazyToasterBoxState,
            maxVisibleToasts = maxVisibleToasts,
        )

        LazyToasterBox(
            state = lazyToasterBoxState,
            expanded = expanded,
            itemHeightProvider = itemHeightProvider,
            toastTransformHelper = toastTransformHelper,
            contentPadding = containerPadding,
            alignment = alignment,
            modifier = modifier.testTag("Toaster"),
        ) { index ->
            val item = state.toasts[index]
            val toast = item.toast

            var invisibleItemCount by remember { mutableIntStateOf(0) }

            LaunchedEffect(state.toasts.lastIndex, index) {
                // This finds all dismissing or dismissed item count above this current item,
                // will make our toasts animated after some have dismissed.
                state.invisibleItemsInRangeFlow(
                    start = index + 1,
                    end = state.toasts.lastIndex
                )
                    .collect { invisibleItemCount = it }
            }

            val layoutIndex = state.toasts.lastIndex - index - invisibleItemCount

            val currentColors = colors(toast)

            // Still provided for slots written against the generic toaster, which read these.
            CompositionLocalProvider(
                LocalToastBorderStroke provides BorderStroke(Dp.Hairline, currentColors.border),
                LocalToastBackground provides SolidColor(currentColors.container),
                LocalToastContentColor provides currentColors.content,
            ) {
                toastBox(item.toast) {
                    ToastItem(
                        onRequestDismiss = { state.dismiss(toast.id) },
                        onInvisible = { state.markDismissed(toast.id) },
                        expanded = expanded,
                        layoutIndex = layoutIndex,
                        toast = item.toast,
                        dismissing = item.isDismissing,
                        maxVisibleToasts = maxVisibleToasts,
                        widthPolicy = widthPolicy(toast),
                        swipeable = swipeable,
                        dismissOnClick = dismissOnClick,
                        colors = currentColors,
                        alignment = alignment,
                        transformHelper = toastTransformHelper,
                        itemHeightProvider = itemHeightProvider,
                        iconSlot = iconSlot,
                        messageSlot = messageSlot,
                        actionSlot = actionSlot,
                    )
                }
            }
        }
    }
}

@Composable
private inline fun ApplyToastDismissPause(
    state: ToasterState,
    toastDismissPause: ToastDismissPause,
    lazyToasterBoxState: LazyToasterBoxState,
    maxVisibleToasts: Int,
) {
    LaunchedEffect(state, lazyToasterBoxState, toastDismissPause) {
        snapshotFlow { state.toasts.map { arrayOf(it.toast, it.state) } }
            .map { lazyToasterBoxState.visibleItemIndices() }
            .collect { visibleIndices ->
                val toasts = state.toasts
                if (toasts.isEmpty() || visibleIndices.isEmpty()) return@collect
                when (toastDismissPause) {
                    ToastDismissPause.Never -> {
                        for (toast in toasts) {
                            state.resumeDismissTimer(toast.toast.id)
                        }
                    }

                    ToastDismissPause.OnNotFront -> {
                        // Resume the dismiss timer for the front toast
                        val frontToastIndex = visibleIndices.last()
                        state.resumeDismissTimer(toasts[frontToastIndex].toast.id)
                        // Pause others
                        for (i in toasts.indices) {
                            if (i != frontToastIndex) {
                                state.pauseDismissTimer(toasts[i].toast.id)
                            }
                        }
                    }

                    ToastDismissPause.OnInvisible -> {
                        val realVisibleIndices = if (visibleIndices.size > maxVisibleToasts) {
                            // Exclude items that are marked as visible but are not,
                            // for the animation reason
                            val from = visibleIndices.size - maxVisibleToasts
                            val to = visibleIndices.size
                            visibleIndices.subList(from, to)
                        } else {
                            visibleIndices
                        }
                        // Resume dismiss timer for visible toasts
                        for (index in realVisibleIndices) {
                            state.resumeDismissTimer(toasts[index].toast.id)
                        }
                        // Pause others
                        val visibleIndexSet = realVisibleIndices.toSet()
                        for (i in toasts.indices) {
                            if (!visibleIndexSet.contains(i)) {
                                state.pauseDismissTimer(toasts[i].toast.id)
                            }
                        }
                    }
                }
            }
    }
}

/**
 * The toast item: places a [MorphingToast] in the stack and handles swiping it away.
 *
 * @param layoutIndex Starts from the most front item and starts from 0.
 */
@Composable
private fun ToastItem(
    onRequestDismiss: () -> Unit,
    onInvisible: () -> Unit,
    expanded: Boolean,
    layoutIndex: Int,
    toast: Toast,
    dismissing: Boolean,
    maxVisibleToasts: Int,
    widthPolicy: ToastWidthPolicy,
    swipeable: Boolean,
    dismissOnClick: Boolean,
    colors: TaminToastColors,
    alignment: Alignment,
    transformHelper: ToastTransformHelper,
    itemHeightProvider: ItemHeightProvider,
    iconSlot: @Composable (toast: Toast) -> Unit,
    messageSlot: @Composable (toast: Toast) -> Unit,
    actionSlot: @Composable (toast: Toast) -> Unit,
    modifier: Modifier = Modifier,
) {
    var height by remember { mutableIntStateOf(0) }

    var dragY by remember { mutableFloatStateOf(0f) }

    val isBottomAlign = alignment.isBottomAlign()

    val draggableState = rememberDraggableState(
        onDelta = { delta -> dragY = max(0f, dragY + delta) }
    )

    fun isSwipedToDismiss(velocity: Float): Boolean {
        if (velocity > 600f && dragY >= height / 5f) return true
        if (velocity > 300f && dragY >= height / 3f) return true
        if (velocity > 100f && dragY >= height / 2f) return true
        return dragY > height * 0.8f
    }

    val stackTransitionDuration = ToasterDefaults.EXIT_TRANSITION_DURATION
    val scale = animateFloatAsState(
        targetValue = if (!expanded) transformHelper.calcScale(layoutIndex) else 1f,
        animationSpec = tween(durationMillis = stackTransitionDuration),
    )
    val alpha = animateFloatAsState(
        targetValue = if (layoutIndex < maxVisibleToasts) 1f else 0f,
        animationSpec = tween(durationMillis = stackTransitionDuration),
    )
    val tranY = animateFloatAsState(
        targetValue = transformHelper.calcTranslationY(
            itemHeightProvider = itemHeightProvider,
            isBottomAlign = isBottomAlign,
            expanded = expanded,
            layoutIndex = layoutIndex,
        ),
        animationSpec = tween(durationMillis = stackTransitionDuration),
    )

    // Full width with the capsule centered: the toaster's own width then stays put while the
    // capsule morphs, so the popup never re-centers mid-animation and stacked capsules share
    // one center line — the capsule opens and closes symmetrically.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { height = it.height }
            .graphicsLayer {
                this.alpha = alpha.value
                transformOrigin = TransformOrigin(0.5f, if (isBottomAlign) 0f else 1f)
                scaleX = scale.value
                scaleY = scale.value
                translationY = tranY.value + dragY
            }
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        MorphingToast(
            toast = toast,
            dismissing = dismissing,
            onInvisible = onInvisible,
            colors = colors,
            maxWidth = widthPolicy.max,
            iconSlot = iconSlot,
            messageSlot = messageSlot,
            actionSlot = actionSlot,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { if (dismissOnClick) onRequestDismiss() },
                )
                .draggable(
                    state = draggableState,
                    enabled = swipeable,
                    orientation = Orientation.Vertical,
                    onDragStarted = { dragY = 0f },
                    onDragStopped = { velocity ->
                        if (!isSwipedToDismiss(velocity)) {
                            animate(
                                targetValue = 0f,
                                initialValue = dragY,
                                animationSpec = tween(durationMillis = stackTransitionDuration),
                            ) { value, _ ->
                                dragY = value
                            }
                        } else {
                            onRequestDismiss()
                        }
                    },
                ),
        )
    }
}
