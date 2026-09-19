package com.tamin.taminhamrah.ui.toparea

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers [TopAreaState]'s drag arithmetic: how `onPreScroll`/`onPostScroll` fold and unfold the
 * header, and how far each clamps at the fully collapsed/expanded edges. `onPreFling`'s
 * release-to-edge spring is left untested here since it's a suspend/animation concern, not a
 * geometry one.
 */
class TopAreaStateTest {

    private val alwaysForward: () -> Boolean = { true }
    private val neverForward: () -> Boolean = { false }

    private fun stateOf(maxOffsetPx: Float): TopAreaState =
        TopAreaState(maxOffsetPx, initialMeasuredHeightPx = 0, scope = CoroutineScope(Job()))

    private fun TopAreaState.foldBy(dy: Float, canScrollForward: () -> Boolean = alwaysForward): Offset =
        connection(canScrollForward).onPreScroll(Offset(0f, dy), NestedScrollSource.UserInput)

    private fun TopAreaState.unfoldBy(dy: Float): Offset =
        connection(alwaysForward).onPostScroll(Offset.Zero, Offset(0f, dy), NestedScrollSource.UserInput)

    private fun TopAreaState.foldBySideEffect(dy: Float): Offset =
        connection(alwaysForward).onPreScroll(Offset(0f, dy), NestedScrollSource.SideEffect)

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
}
