package com.tamin.taminhamrah.feature.stories.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.stories.FakeStoryRepository
import com.tamin.taminhamrah.useCases.stories.GetStoryChannelsUseCase
import com.tamin.taminhamrah.useCases.stories.MarkStoryChannelSeenUseCase
import com.tamin.taminhamrah.useCases.stories.ObserveStoryEngagementUseCase
import com.tamin.taminhamrah.useCases.stories.ToggleStoryLikeUseCase
import com.tamin.taminhamrah.useCases.stories.ToggleStorySaveUseCase
import com.tamin.taminhamrah.feature.stories.testChannel
import com.tamin.taminhamrah.feature.stories.ui.theme.STORY_DEFAULT_DURATION_MS
import com.tamin.taminhamrah.feature.stories.ui.viewer.StoryViewerViewModel
import com.tamin.taminhamrah.feature.stories.ui.viewer.contract.StoryViewerEvent
import com.tamin.taminhamrah.feature.stories.ui.viewer.contract.StoryViewerIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.flow.first
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The viewer's whole timing story is testable because the segment clock counts in virtual time and
 * never reads a wall clock: every wait below is `advanceTimeBy`, and nothing here sleeps.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StoryViewerViewModelTest {

    // Shares its scheduler with every `runTest` below, so advancing time in the test advances the
    // ViewModel's own clock rather than a separate one.
    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun test(body: suspend TestScope.() -> Unit) = runTest(dispatcher) { body() }

    private fun repository(
        vararg channels: com.tamin.taminhamrah.model.stories.StoryChannelDN,
    ) = FakeStoryRepository(channels.toList())

    private fun FakeStoryRepository.viewer(channelIndex: Int = 0): StoryViewerViewModel =
        StoryViewerViewModel(
            getStoryChannelsUseCase = GetStoryChannelsUseCase(this),
            markStoryChannelSeenUseCase = MarkStoryChannelSeenUseCase(this),
            observeStoryEngagementUseCase = ObserveStoryEngagementUseCase(this),
            toggleStoryLikeUseCase = ToggleStoryLikeUseCase(this),
            toggleStorySaveUseCase = ToggleStorySaveUseCase(this),
        ).also { it.sendIntent(StoryViewerIntent.Open(channelIndex)) }

    /** Runs the clock out and lets whatever it scheduled at that instant actually run. */
    private fun TestScope.elapse(millis: Long) {
        advanceTimeBy(millis)
        runCurrent()
    }

    /* ---- Opening ------------------------------------------------------------------------- */

    @Test
    fun `opens on the first slide of the channel it was given`() = test {
        val viewModel = repository(testChannel("a"), testChannel("b")).viewer(channelIndex = 1)

        val state = viewModel.uiState.value
        assertEquals(1, state.channelIndex)
        assertEquals(0, state.itemIndex)
        assertEquals(3, state.itemCount)
        assertFalse(state.isLoading)
    }

    @Test
    fun `an empty catalogue closes the viewer rather than showing an empty screen`() = test {
        val repository = FakeStoryRepository(channels = emptyList())
        val viewModel = StoryViewerViewModel(
            getStoryChannelsUseCase = GetStoryChannelsUseCase(repository),
            markStoryChannelSeenUseCase = MarkStoryChannelSeenUseCase(repository),
            observeStoryEngagementUseCase = ObserveStoryEngagementUseCase(repository),
            toggleStoryLikeUseCase = ToggleStoryLikeUseCase(repository),
            toggleStorySaveUseCase = ToggleStorySaveUseCase(repository),
        )

        viewModel.events.test {
            viewModel.sendIntent(StoryViewerIntent.Open(0))
            assertEquals(StoryViewerEvent.Close, awaitItem())
        }
    }

    @Test
    fun `opening does not refetch a catalogue the rail already loaded`() = test {
        val repository = repository(testChannel("a"))
        // Stand in for the rail: it loaded the catalogue before the viewer was ever opened.
        GetStoryChannelsUseCase(repository)().first()
        assertEquals(1, repository.fetchCount)

        repository.viewer()

        assertEquals(1, repository.fetchCount)
    }

    /* ---- Moving forward ------------------------------------------------------------------- */

    @Test
    fun `next moves to the following slide`() = test {
        val viewModel = repository(testChannel("a")).viewer()

        viewModel.sendIntent(StoryViewerIntent.Next)

        assertEquals(0, viewModel.uiState.value.channelIndex)
        assertEquals(1, viewModel.uiState.value.itemIndex)
    }

    @Test
    fun `next past a channel's last slide opens the next channel and marks the finished one seen`() =
        test {
            val repository = repository(testChannel("a", itemCount = 2), testChannel("b"))
            val viewModel = repository.viewer()

            repeat(2) { viewModel.sendIntent(StoryViewerIntent.Next) }

            val state = viewModel.uiState.value
            assertEquals(1, state.channelIndex)
            assertEquals(0, state.itemIndex)
            assertTrue("a" in repository.seenChannels)
        }

    @Test
    fun `next past the last slide of the last channel closes the viewer`() = test {
        val repository = repository(testChannel("a", itemCount = 1))
        val viewModel = repository.viewer()

        viewModel.events.test {
            viewModel.sendIntent(StoryViewerIntent.Next)
            assertEquals(StoryViewerEvent.Close, awaitItem())
        }
        assertTrue("a" in repository.seenChannels)
    }

    /* ---- Moving back ---------------------------------------------------------------------- */

    @Test
    fun `previous steps back a slide`() = test {
        val viewModel = repository(testChannel("a")).viewer()
        viewModel.sendIntent(StoryViewerIntent.Next)

        viewModel.sendIntent(StoryViewerIntent.Previous)

        assertEquals(0, viewModel.uiState.value.itemIndex)
    }

    @Test
    fun `previous on the very first slide replays it instead of doing nothing`() = test {
        val viewModel = repository(testChannel("a")).viewer()
        elapse(2_000)
        val tokenBefore = viewModel.uiState.value.segmentToken

        viewModel.sendIntent(StoryViewerIntent.Previous)

        val state = viewModel.uiState.value
        assertEquals(0, state.channelIndex)
        assertEquals(0, state.itemIndex)
        // The segment started over, which is what "replays" means to the progress bar.
        assertTrue(state.segmentToken > tokenBefore)
        assertEquals(0L, state.segmentElapsedMs)
    }

    @Test
    fun `previous on a later channel's first slide lands on the previous channel's last slide`() =
        test {
            val viewModel = repository(testChannel("a", itemCount = 3), testChannel("b"))
                .viewer(channelIndex = 1)

            viewModel.sendIntent(StoryViewerIntent.Previous)

            val state = viewModel.uiState.value
            assertEquals(0, state.channelIndex)
            assertEquals(2, state.itemIndex)
        }

    /* ---- The clock ------------------------------------------------------------------------ */

    @Test
    fun `a slide advances on its own once its time is up`() = test {
        val viewModel = repository(testChannel("a")).viewer()

        elapse(STORY_DEFAULT_DURATION_MS - 100)
        assertEquals(0, viewModel.uiState.value.itemIndex)

        elapse(100)
        assertEquals(1, viewModel.uiState.value.itemIndex)
    }

    @Test
    fun `a paused slide stays put however long it is held`() = test {
        val viewModel = repository(testChannel("a")).viewer()
        elapse(1_000)

        viewModel.sendIntent(StoryViewerIntent.Pause)
        elapse(STORY_DEFAULT_DURATION_MS * 3)

        assertTrue(viewModel.uiState.value.isPaused)
        assertEquals(0, viewModel.uiState.value.itemIndex)
    }

    @Test
    fun `pausing reports how far the slide had got`() = test {
        val viewModel = repository(testChannel("a")).viewer()
        elapse(2_000)

        viewModel.sendIntent(StoryViewerIntent.Pause)

        assertEquals(2_000L, viewModel.uiState.value.segmentElapsedMs)
        assertFalse(viewModel.uiState.value.isPlaying)
    }

    @Test
    fun `resuming finishes the slide in the time it had left, not a whole one`() = test {
        val viewModel = repository(testChannel("a")).viewer()
        elapse(2_000)
        viewModel.sendIntent(StoryViewerIntent.Pause)
        elapse(60_000)

        viewModel.sendIntent(StoryViewerIntent.Resume)
        assertFalse(viewModel.uiState.value.isPaused)

        // 4_200 remained of the 6_200.
        elapse(4_000)
        assertEquals(0, viewModel.uiState.value.itemIndex)
        elapse(200)
        assertEquals(1, viewModel.uiState.value.itemIndex)
    }

    @Test
    fun `a tap that lands while the clock is running does not advance the story twice`() = test {
        val viewModel = repository(testChannel("a", itemCount = 3)).viewer()

        viewModel.sendIntent(StoryViewerIntent.Next)
        // Whatever was left of the first slide's clock must not fire against the second.
        elapse(STORY_DEFAULT_DURATION_MS - 100)

        assertEquals(1, viewModel.uiState.value.itemIndex)
    }

    /* ---- The comment field ----------------------------------------------------------------- */

    @Test
    fun `the comment keyboard holds the story while it is up`() = test {
        val viewModel = repository(testChannel("a")).viewer()
        elapse(1_000)

        viewModel.sendIntent(StoryViewerIntent.CommentFocusChanged(focused = true))
        elapse(STORY_DEFAULT_DURATION_MS * 3)

        assertTrue(viewModel.uiState.value.isComposingComment)
        assertFalse(viewModel.uiState.value.isPlaying)
        assertEquals(0, viewModel.uiState.value.itemIndex)
    }

    @Test
    fun `closing the keyboard finishes the slide in the time it had left`() = test {
        val viewModel = repository(testChannel("a")).viewer()
        elapse(2_000)
        viewModel.sendIntent(StoryViewerIntent.CommentFocusChanged(focused = true))
        elapse(60_000)

        viewModel.sendIntent(StoryViewerIntent.CommentFocusChanged(focused = false))

        elapse(4_000)
        assertEquals(0, viewModel.uiState.value.itemIndex)
        elapse(200)
        assertEquals(1, viewModel.uiState.value.itemIndex)
    }

    @Test
    fun `a finger lifting does not restart a story the keyboard is still holding`() = test {
        val viewModel = repository(testChannel("a")).viewer()
        viewModel.sendIntent(StoryViewerIntent.CommentFocusChanged(focused = true))
        viewModel.sendIntent(StoryViewerIntent.Pause)

        viewModel.sendIntent(StoryViewerIntent.Resume)

        val state = viewModel.uiState.value
        assertFalse(state.isTouchHeld)
        assertTrue(state.isComposingComment)
        assertFalse(state.isPlaying)

        elapse(STORY_DEFAULT_DURATION_MS * 2)
        assertEquals(0, viewModel.uiState.value.itemIndex)
    }

    @Test
    fun `closing the keyboard does not restart a story a finger is still on`() = test {
        val viewModel = repository(testChannel("a")).viewer()
        viewModel.sendIntent(StoryViewerIntent.Pause)
        viewModel.sendIntent(StoryViewerIntent.CommentFocusChanged(focused = true))

        viewModel.sendIntent(StoryViewerIntent.CommentFocusChanged(focused = false))

        assertTrue(viewModel.uiState.value.isTouchHeld)
        assertFalse(viewModel.uiState.value.isPlaying)

        elapse(STORY_DEFAULT_DURATION_MS * 2)
        assertEquals(0, viewModel.uiState.value.itemIndex)
    }

    @Test
    fun `typing is kept and sending clears it and lets the story run on`() = test {
        val viewModel = repository(testChannel("a")).viewer()
        viewModel.sendIntent(StoryViewerIntent.CommentFocusChanged(focused = true))

        viewModel.sendIntent(StoryViewerIntent.CommentChanged("سلام"))
        assertEquals("سلام", viewModel.uiState.value.commentDraft)

        viewModel.sendIntent(StoryViewerIntent.CommentSubmitted)

        val state = viewModel.uiState.value
        assertEquals("", state.commentDraft)
        assertFalse(state.isComposingComment)
        assertTrue(state.isPlaying)
    }

    @Test
    fun `a draft belongs to its own slide`() = test {
        val viewModel = repository(testChannel("a")).viewer()
        viewModel.sendIntent(StoryViewerIntent.CommentChanged("نیمه‌کاره"))

        viewModel.sendIntent(StoryViewerIntent.Next)

        assertEquals("", viewModel.uiState.value.commentDraft)
    }

    /* ---- Video ---------------------------------------------------------------------------- */

    @Test
    fun `a clip's slide waits for the player instead of running the default time`() = test {
        val viewModel = repository(testChannel("a", videoIndices = setOf(0))).viewer()

        assertTrue(viewModel.uiState.value.isBuffering)
        assertFalse(viewModel.uiState.value.isPlaying)

        elapse(STORY_DEFAULT_DURATION_MS)
        assertEquals(0, viewModel.uiState.value.itemIndex)
    }

    @Test
    fun `a clip's slide runs for exactly as long as the player says it is`() = test {
        val viewModel = repository(testChannel("a", videoIndices = setOf(0))).viewer()

        viewModel.sendIntent(StoryViewerIntent.MediaReady(durationMs = 3_000))
        val state = viewModel.uiState.value
        assertFalse(state.isBuffering)
        assertEquals(3_000L, state.segmentDurationMs)

        elapse(2_900)
        assertEquals(0, viewModel.uiState.value.itemIndex)
        elapse(100)
        assertEquals(1, viewModel.uiState.value.itemIndex)
    }

    @Test
    fun `a player that reports no length gets the ordinary slide time`() = test {
        val viewModel = repository(testChannel("a", videoIndices = setOf(0))).viewer()

        viewModel.sendIntent(StoryViewerIntent.MediaReady(durationMs = 0))

        assertEquals(STORY_DEFAULT_DURATION_MS, viewModel.uiState.value.segmentDurationMs)
    }

    @Test
    fun `a clip that plays out moves the story on without waiting for the clock`() = test {
        val viewModel = repository(testChannel("a", videoIndices = setOf(0))).viewer()
        viewModel.sendIntent(StoryViewerIntent.MediaReady(durationMs = 30_000))

        viewModel.sendIntent(StoryViewerIntent.MediaEnded)

        assertEquals(1, viewModel.uiState.value.itemIndex)
    }

    /* ---- Media failure --------------------------------------------------------------------- */

    @Test
    fun `media that fails falls back to the ordinary slide time and says so`() = test {
        val viewModel = repository(testChannel("a")).viewer()

        viewModel.sendIntent(StoryViewerIntent.MediaFailed)

        assertTrue(viewModel.uiState.value.mediaFailed)
        assertEquals(STORY_DEFAULT_DURATION_MS, viewModel.uiState.value.segmentDurationMs)

        elapse(STORY_DEFAULT_DURATION_MS)
        assertEquals(1, viewModel.uiState.value.itemIndex)
    }

    @Test
    fun `a clip that never reports anything is given up on rather than stalling the viewer`() =
        test {
            val viewModel = repository(testChannel("a", videoIndices = setOf(0))).viewer()

            // Long enough for the watchdog, short of the slide time that follows it.
            elapse(8_000)

            assertTrue(viewModel.uiState.value.mediaFailed)
            assertFalse(viewModel.uiState.value.isBuffering)
            assertEquals(0, viewModel.uiState.value.itemIndex)

            elapse(STORY_DEFAULT_DURATION_MS)
            assertEquals(1, viewModel.uiState.value.itemIndex)
        }

    /* ---- Leaving -------------------------------------------------------------------------- */

    @Test
    fun `closing marks the channel seen and leaves`() = test {
        val repository = repository(testChannel("a"))
        val viewModel = repository.viewer()

        viewModel.events.test {
            viewModel.sendIntent(StoryViewerIntent.Close)
            assertEquals(StoryViewerEvent.Close, awaitItem())
        }
        assertTrue("a" in repository.seenChannels)
    }

    @Test
    fun `a call to action with an internal deep link raises it for the host to open`() = test {
        val viewModel = repository(testChannel("a", ctaIndices = setOf(0))).viewer()

        viewModel.events.test {
            viewModel.sendIntent(StoryViewerIntent.CtaClicked)
            assertEquals(StoryViewerEvent.OpenDeepLink("tamin://feature/AGENT"), awaitItem())
        }
    }

    @Test
    fun `a call to action with an external deep link raises it for the host to openUrl`() = test {
        val channel = testChannel("a", itemCount = 1)
        val modifiedItems = channel.items.toMutableList()
        modifiedItems[0] = modifiedItems[0].copy(cta = com.tamin.taminhamrah.model.stories.StoryCtaDN("سایت", "https://tamin.ir"))
        val modifiedChannel = channel.copy(items = modifiedItems)
        val viewModel = repository(modifiedChannel).viewer()

        viewModel.events.test {
            viewModel.sendIntent(StoryViewerIntent.CtaClicked)
            assertEquals(StoryViewerEvent.OpenDeepLink("https://tamin.ir"), awaitItem())
        }
    }

    @Test
    fun `a call to action without a deep link does not navigate`() = test {
        val channel = testChannel("a", itemCount = 1)
        val modifiedItems = channel.items.toMutableList()
        modifiedItems[0] = modifiedItems[0].copy(cta = com.tamin.taminhamrah.model.stories.StoryCtaDN("label", null))
        val modifiedChannel = channel.copy(items = modifiedItems)
        val viewModel = repository(modifiedChannel).viewer()

        viewModel.events.test {
            viewModel.sendIntent(StoryViewerIntent.CtaClicked)
            expectNoEvents()
        }
    }

    @Test
    fun `a closed viewer's clock stops with it`() = test {
        val viewModel = repository(testChannel("a")).viewer()

        viewModel.sendIntent(StoryViewerIntent.Close)
        elapse(STORY_DEFAULT_DURATION_MS * 2)

        assertEquals(0, viewModel.uiState.value.itemIndex)
    }

    /* ---- Liking and saving ------------------------------------------------------------------ */

    @Test
    fun `liking a slide counts it and shows on the slide it was made on`() = test {
        val viewModel = repository(testChannel("a")).viewer()
        val before = viewModel.uiState.value.likeCount

        viewModel.sendIntent(StoryViewerIntent.ToggleLike)
        assertTrue(viewModel.uiState.value.isLiked)
        assertEquals(before + 1, viewModel.uiState.value.likeCount)

        viewModel.sendIntent(StoryViewerIntent.ToggleLike)
        assertFalse(viewModel.uiState.value.isLiked)
        assertEquals(before, viewModel.uiState.value.likeCount)
    }

    @Test
    fun `a like stays with its own slide when the story moves on`() = test {
        val viewModel = repository(testChannel("a")).viewer()
        viewModel.sendIntent(StoryViewerIntent.ToggleLike)

        viewModel.sendIntent(StoryViewerIntent.Next)

        assertFalse(viewModel.uiState.value.isLiked)
    }

    @Test
    fun `bookmarking a slide sticks`() = test {
        val viewModel = repository(testChannel("a")).viewer()

        viewModel.sendIntent(StoryViewerIntent.ToggleSave)

        assertTrue(viewModel.uiState.value.isSaved)
    }
}
