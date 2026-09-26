package com.tamin.taminhamrah.ui.toparea

import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers [TopAreaState]'s drag arithmetic: how `onPreScroll`/`onPostScroll` fold and unfold the
 * header, how far each clamps at the fully collapsed/expanded edges, and the snap helpers
 * (`collapseFully` / `settleToNearestEdge` / release fling) that drive the header to an edge.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TopAreaStateTest {

    private val alwaysForward: () -> Boolean = { true }
    private val neverForward: () -> Boolean = { false }

    /**
     * Advances ~16 ms per frame so Compose [androidx.compose.animation.core.animate] can finish
     * without a real choreographer. Paired with [UnconfinedTestDispatcher] the spring runs to
     * completion inside the launching call.
     */
    private fun animatingScope(): CoroutineScope {
        val clock = object : MonotonicFrameClock {
            private var frameTimeNanos = 0L
            override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R {
                frameTimeNanos += 16_000_000L
                return onFrame(frameTimeNanos)
            }
        }
        return CoroutineScope(Job() + UnconfinedTestDispatcher() + clock)
    }

    private fun stateOf(
        maxOffsetPx: Float,
        scope: CoroutineScope = CoroutineScope(Job()),
    ): TopAreaState = TopAreaState(maxOffsetPx, initialMeasuredHeightPx = 0, scope = scope)

    private fun TopAreaState.foldBy(dy: Float, canScrollForward: () -> Boolean = alwaysForward): Offset =
        connection(canScrollForward).onPreScroll(Offset(0f, dy), NestedScrollSource.UserInput)

    private fun TopAreaState.unfoldBy(dy: Float): Offset =
        connection(alwaysForward).onPostScroll(Offset.Zero, Offset(0f, dy), NestedScrollSource.UserInput)

    private fun TopAreaState.foldBySideEffect(dy: Float): Offset =
        connection(alwaysForward).onPreScroll(Offset(0f, dy), NestedScrollSource.SideEffect)

    private fun TopAreaState.unfoldBySideEffect(dy: Float): Offset =
        connection(alwaysForward).onPostScroll(Offset.Zero, Offset(0f, dy), NestedScrollSource.SideEffect)

    @Test
    fun `folding consumes the drag 1 to 1 away from the boundaries`() {
        val state = stateOf(maxOffsetPx = 100f)

        val consumed = state.foldBy(dy = -30f)

        assertEquals(-30f, consumed.y)
        assertEquals(30f, state.rawOffsetPx)
        assertEquals(0.3f, state.progress)
    }

    @Test
    fun `folding clamps at the collapsed edge and only consumes what it actually applied`() {
        val state = stateOf(maxOffsetPx = 100f)
        state.foldBy(dy = -80f)

        val consumed = state.foldBy(dy = -30f)

        assertEquals(-20f, consumed.y)
        assertEquals(100f, state.rawOffsetPx)
        assertEquals(1f, state.progress)
    }

    @Test
    fun `a fold never starts while the content has nothing left to scroll forward`() {
        val state = stateOf(maxOffsetPx = 100f)

        val consumed = state.foldBy(dy = -40f, canScrollForward = neverForward)

        assertEquals(Offset.Zero, consumed)
        assertEquals(0f, state.rawOffsetPx)
    }

    @Test
    fun `onPreScroll never folds on a backward scroll`() {
        val state = stateOf(maxOffsetPx = 100f)

        val consumed = state.foldBy(dy = 40f)

        assertEquals(Offset.Zero, consumed)
        assertEquals(0f, state.rawOffsetPx)
    }

    @Test
    fun `unfolding restores the drag 1 to 1 away from the boundaries`() {
        val state = stateOf(maxOffsetPx = 100f)
        state.foldBy(dy = -60f)

        val consumed = state.unfoldBy(dy = 25f)

        assertEquals(25f, consumed.y)
        assertEquals(35f, state.rawOffsetPx)
    }

    @Test
    fun `unfolding clamps at the fully expanded edge and only consumes what it actually applied`() {
        val state = stateOf(maxOffsetPx = 100f)
        state.foldBy(dy = -10f)

        val consumed = state.unfoldBy(dy = 25f)

        assertEquals(10f, consumed.y)
        assertEquals(0f, state.rawOffsetPx)
    }

    @Test
    fun `onPostScroll never unfolds on a forward-scroll leftover`() {
        val state = stateOf(maxOffsetPx = 100f)
        state.foldBy(dy = -50f)

        val consumed = state.unfoldBy(dy = -10f)

        assertEquals(Offset.Zero, consumed)
        assertEquals(50f, state.rawOffsetPx)
    }

    @Test
    fun `progress tracks the raw offset linearly between the two edges`() {
        val state = stateOf(maxOffsetPx = 200f)

        state.foldBy(dy = -50f)
        assertEquals(0.25f, state.progress)

        state.foldBy(dy = -150f)
        assertEquals(1f, state.progress)

        state.unfoldBy(dy = 200f)
        assertEquals(0f, state.progress)
    }

    @Test
    fun `progress stays zero when there is no room to fold`() {
        val state = stateOf(maxOffsetPx = 0f)

        state.foldBy(dy = -50f)

        assertEquals(0f, state.rawOffsetPx)
        assertEquals(0f, state.progress)
    }

    @Test
    fun `a probe reads back exactly the progress it was frozen at`() {
        val expanded = TopAreaState.probe(progress = 0f, scope = CoroutineScope(Job()))
        val collapsed = TopAreaState.probe(progress = 1f, scope = CoroutineScope(Job()))
        val mid = TopAreaState.probe(progress = 0.4f, scope = CoroutineScope(Job()))

        assertEquals(0f, expanded.progress)
        assertEquals(1f, collapsed.progress)
        assertEquals(0.4f, mid.progress)
    }

    @Test
    fun `a probe clamps an out-of-range progress instead of drifting off the 0 to 1 scale`() {
        val underShot = TopAreaState.probe(progress = -0.5f, scope = CoroutineScope(Job()))
        val overShot = TopAreaState.probe(progress = 1.5f, scope = CoroutineScope(Job()))

        assertEquals(0f, underShot.progress)
        assertEquals(1f, overShot.progress)
    }

    @Test
    fun `programmatic SideEffect scrolls do not partially fold the header`() {
        val state = stateOf(maxOffsetPx = 100f)

        val consumed = state.foldBySideEffect(dy = -40f)

        assertEquals(Offset.Zero, consumed)
        assertEquals(0f, state.rawOffsetPx)
        assertEquals(0f, state.progress)
    }

    @Test
    fun `a SideEffect scroll does not add onto an existing mid-fold`() {
        val state = stateOf(maxOffsetPx = 100f)
        state.foldBy(dy = -30f)

        val consumed = state.foldBySideEffect(dy = -40f)

        assertEquals(Offset.Zero, consumed)
        assertEquals(30f, state.rawOffsetPx)
    }

    @Test
    fun `onPostScroll ignores SideEffect leftovers so IME cannot unfold the header`() {
        val state = stateOf(maxOffsetPx = 100f)
        state.foldBy(dy = -50f)

        val consumed = state.unfoldBySideEffect(dy = 40f)

        assertEquals(Offset.Zero, consumed)
        assertEquals(50f, state.rawOffsetPx)
    }

    @Test
    fun `collapseFully is a no-op when the header is already collapsed`() {
        val state = stateOf(maxOffsetPx = 100f, scope = animatingScope())
        state.foldBy(dy = -100f)

        state.collapseFully()

        assertEquals(100f, state.rawOffsetPx)
        assertEquals(1f, state.progress)
    }

    @Test
    fun `collapseFully springs an expanded header to the collapsed edge`() {
        val state = stateOf(maxOffsetPx = 100f, scope = animatingScope())

        state.collapseFully()

        assertEquals(100f, state.rawOffsetPx)
        assertEquals(1f, state.progress)
    }

    @Test
    fun `collapseFully springs a mid-fold header to the collapsed edge`() {
        val state = stateOf(maxOffsetPx = 100f, scope = animatingScope())
        state.foldBy(dy = -40f)

        state.collapseFully()

        assertEquals(100f, state.rawOffsetPx)
        assertEquals(1f, state.progress)
    }

    @Test
    fun `expandFully is a no-op when the header is already expanded`() {
        val state = stateOf(maxOffsetPx = 100f, scope = animatingScope())

        state.expandFully()

        assertEquals(0f, state.rawOffsetPx)
        assertEquals(0f, state.progress)
    }

    @Test
    fun `expandFully springs a collapsed header to the expanded edge`() {
        val state = stateOf(maxOffsetPx = 100f, scope = animatingScope())
        state.foldBy(dy = -100f)

        state.expandFully()

        assertEquals(0f, state.rawOffsetPx)
        assertEquals(0f, state.progress)
    }

    @Test
    fun `expandFully springs a mid-fold header to the expanded edge`() {
        val state = stateOf(maxOffsetPx = 100f, scope = animatingScope())
        state.foldBy(dy = -40f)

        state.expandFully()

        assertEquals(0f, state.rawOffsetPx)
        assertEquals(0f, state.progress)
    }

    @Test
    fun `settleToNearestEdge is a no-op when already at an edge`() {
        val state = stateOf(maxOffsetPx = 100f, scope = animatingScope())

        state.settleToNearestEdge(scrollVelocityY = -500f)

        assertEquals(0f, state.rawOffsetPx)
    }

    @Test
    fun `settleToNearestEdge expands when released below the midpoint`() {
        val state = stateOf(maxOffsetPx = 100f, scope = animatingScope())
        state.foldBy(dy = -40f)

        state.settleToNearestEdge()

        assertEquals(0f, state.rawOffsetPx)
        assertEquals(0f, state.progress)
    }

    @Test
    fun `settleToNearestEdge collapses when released at or above the midpoint`() {
        val state = stateOf(maxOffsetPx = 100f, scope = animatingScope())
        state.foldBy(dy = -60f)

        state.settleToNearestEdge()

        assertEquals(100f, state.rawOffsetPx)
        assertEquals(1f, state.progress)
    }

    @Test
    fun `settleToNearestEdge collapses when fling velocity is strongly upward`() {
        val state = stateOf(maxOffsetPx = 100f, scope = animatingScope())
        state.foldBy(dy = -20f)

        state.settleToNearestEdge(scrollVelocityY = -300f)

        assertEquals(100f, state.rawOffsetPx)
        assertEquals(1f, state.progress)
    }

    @Test
    fun `settleToNearestEdge expands when fling velocity is strongly downward`() {
        val state = stateOf(maxOffsetPx = 100f, scope = animatingScope())
        state.foldBy(dy = -80f)

        state.settleToNearestEdge(scrollVelocityY = 300f)

        assertEquals(0f, state.rawOffsetPx)
        assertEquals(0f, state.progress)
    }

    @Test
    fun `a user drag followed by an upward fling snaps the header collapsed`() = runTest {
        val state = stateOf(maxOffsetPx = 100f, scope = animatingScope())
        state.foldBy(dy = -35f)

        state.connection(alwaysForward).onPreFling(Velocity(0f, -300f))

        assertEquals(100f, state.rawOffsetPx)
        assertEquals(1f, state.progress)
    }
}
