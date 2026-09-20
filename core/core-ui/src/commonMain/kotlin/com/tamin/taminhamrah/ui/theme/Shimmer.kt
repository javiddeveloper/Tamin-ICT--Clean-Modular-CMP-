package com.tamin.taminhamrah.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val SHIMMER_DURATION_MS = 1200

/**
 * Opts the shimmers beneath it into one band shared across the window.
 *
 * False — the default — keeps the per-block sweep every existing skeleton was built against, so
 * nothing changes for a screen that does not provide it. A screen provides true when its skeletons
 * are built from many small blocks: each block otherwise runs its own sweep, started whenever it
 * composed and scaled to its own width, and a dozen of them read as flicker rather than as loading.
 * With it on, every block paints the slice of a single band that falls inside its bounds, so the
 * skeleton shimmers as one surface.
 */
val LocalSynchronizedShimmer = staticCompositionLocalOf { false }

fun Modifier.shimmer(
    colorBase: Color = Color.Unspecified,
    colorHighlight: Color = Color.Unspecified,
): Modifier = composed {
    val base = if (colorBase != Color.Unspecified) colorBase else MaterialTheme.colorScheme.surfaceVariant
    val highlight = if (colorHighlight != Color.Unspecified) colorHighlight else MaterialTheme.colorScheme.surface

    if (LocalSynchronizedShimmer.current) {
        return@composed SynchronizedShimmerElement(base = base, highlight = highlight)
    }

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(SHIMMER_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslateX",
    )

    drawBehind {
        val brush = Brush.linearGradient(
            colors = listOf(base, highlight, base),
            start = Offset(translateX * size.width, 0f),
            end = Offset(translateX * size.width + size.width, 0f),
        )
        drawRect(brush = brush)
    }
}

/**
 * How far the shared band travels in one cycle, in window widths: one width before the left edge to
 * one past the right — the `-1 → 2` the per-block sweep covers, so a full-width block looks the same
 * either way.
 */
private const val SYNCHRONIZED_SWEEP_WIDTHS = 3f

private data class SynchronizedShimmerElement(
    val base: Color,
    val highlight: Color,
) : ModifierNodeElement<SynchronizedShimmerNode>() {
    override fun create(): SynchronizedShimmerNode = SynchronizedShimmerNode(base, highlight)

    override fun update(node: SynchronizedShimmerNode) = node.update(base, highlight)
}

/**
 * The shared band for one block.
 *
 * Its phase comes from the frame clock every node in the window reads, and its position from where
 * the block sits in the window. A frame of the sweep costs no recomposition: the node invalidates only
 * its own draw, and the brush is built once per window width and then translated.
 */
private class SynchronizedShimmerNode(
    private var base: Color,
    private var highlight: Color,
) : Modifier.Node(), DrawModifierNode, GlobalPositionAwareModifierNode {

    private var leftInWindow = 0f
    private var windowWidth = 0f

    /** 0 → 1 across one sweep. */
    private var progress = 0f

    private var band: Brush? = null
    private var bandSpan = 0f

    override fun onAttach() {
        coroutineScope.launch {
            while (isActive) {
                withFrameMillis { frameTimeMillis ->
                    progress = (frameTimeMillis % SHIMMER_DURATION_MS) / SHIMMER_DURATION_MS.toFloat()
                }
                invalidateDraw()
            }
        }
    }

    fun update(base: Color, highlight: Color) {
        if (base == this.base && highlight == this.highlight) return
        this.base = base
        this.highlight = highlight
        band = null
        invalidateDraw()
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        leftInWindow = coordinates.positionInRoot().x
        windowWidth = coordinates.findRootCoordinates().size.width.toFloat()
    }

    override fun ContentDrawScope.draw() {
        // Until the first layout pass reports the window, the block stands in for it.
        val span = if (windowWidth > 0f) windowWidth else size.width
        val brush = band?.takeIf { bandSpan == span } ?: Brush.horizontalGradient(
            colors = listOf(base, highlight, base),
            startX = 0f,
            endX = span,
        ).also {
            band = it
            bandSpan = span
        }
        // The band's start in this block's own coordinates. Outside its span the gradient clamps to
        // [base], so a block the band has not reached reads as plain placeholder.
        val bandStart = (progress * SYNCHRONIZED_SWEEP_WIDTHS - 1f) * span - leftInWindow
        translate(left = bandStart) {
            drawRect(brush = brush, topLeft = Offset(-bandStart, 0f), size = size)
        }
        drawContent()
    }
}

/**
 * A shimmering block standing in for something that has not arrived yet.
 *
 * Sized by the caller, so the same thing stands in for a line of digits or for a whole card, and
 * a skeleton is a handful of these rather than a bespoke box each time.
 */
@Composable
fun ShimmerBlock(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CornerRadius.sm,
    colorBase: Color = Color.Unspecified,
    colorHighlight: Color = Color.Unspecified,
) {
    Box(modifier.clip(RoundedCornerShape(cornerRadius)).shimmer(colorBase, colorHighlight))
}

/**
 * A page of cards that have not arrived yet, at the rhythm the real list will have — so the
 * content lands where the placeholders already were instead of the page jumping when it does.
 *
 * A plain [Column]: a screen shows a handful of these while it loads, never a scrollable list of
 * them.
 */
@Composable
fun ShimmerCardList(
    modifier: Modifier = Modifier,
    count: Int = 3,
    cardHeight: Dp = ShimmerSize.cardHeight,
    cornerRadius: Dp = CornerRadius.cardCompact,
    spacing: Dp = Spacing.lg,
    contentPadding: PaddingValues = PaddingValues(Spacing.page),
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        repeat(count) {
            ShimmerBlock(
                modifier = Modifier.fillMaxWidth().height(cardHeight),
                cornerRadius = cornerRadius,
            )
        }
    }
}
