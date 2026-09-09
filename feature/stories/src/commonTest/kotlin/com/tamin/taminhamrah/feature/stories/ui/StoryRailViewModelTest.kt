package com.tamin.taminhamrah.feature.stories.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.stories.FakeStorySource
import com.tamin.taminhamrah.feature.stories.data.StoryCatalog
import com.tamin.taminhamrah.feature.stories.testChannel
import com.tamin.taminhamrah.feature.stories.ui.rail.StoryRailViewModel
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailContent
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailEvent
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class StoryRailViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(source: FakeStorySource) = StoryRailViewModel(StoryCatalog(source))

    /* ---- Showing the list ---------------------------------------------------------------- */

    @Test
    fun `loads the catalogue and shows its channels`() = runTest {
        val source = FakeStorySource(listOf(testChannel("a"), testChannel("b")))

        val state = viewModel(source).uiState.value

        assertEquals(StoryRailContent.Channels, state.content)
        assertEquals(listOf("a", "b"), state.channels.map { it.key })
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `a catalogue with no channels shows the empty state rather than an empty row`() = runTest {
        val state = viewModel(FakeStorySource(channels = emptyList())).uiState.value

        assertEquals(StoryRailContent.Empty, state.content)
        assertTrue(state.channels.isEmpty())
    }

    /* ---- Failing, and recovering ---------------------------------------------------------- */

    @Test
    fun `a source that throws leaves the rail in its error state`() = runTest {
        val source = FakeStorySource().apply { failWith = IllegalStateException("no network") }

        val state = viewModel(source).uiState.value

        assertEquals(StoryRailContent.Error, state.content)
        assertNotNull(state.error)
    }

    @Test
    fun `retrying after a failure loads the list and clears the error`() = runTest {
        val source = FakeStorySource().apply { failWith = IllegalStateException("no network") }
        val viewModel = viewModel(source)
        assertEquals(StoryRailContent.Error, viewModel.uiState.value.content)

        source.failWith = null
        viewModel.sendIntent(StoryRailIntent.Retry)

        val state = viewModel.uiState.value
        assertEquals(StoryRailContent.Channels, state.content)
        assertEquals(null, state.error)
    }

    /* ---- Fetching once -------------------------------------------------------------------- */

    @Test
    fun `repeated loads do not refetch the catalogue`() = runTest {
        val source = FakeStorySource()
        val viewModel = viewModel(source)
        // One from init.
        assertEquals(1, source.callCount)

        repeat(3) { viewModel.sendIntent(StoryRailIntent.Load) }

        assertEquals(1, source.callCount)
    }

    @Test
    fun `two rails sharing one catalogue fetch it once between them`() = runTest {
        val source = FakeStorySource()
        val catalog = StoryCatalog(source)

        StoryRailViewModel(catalog)
        StoryRailViewModel(catalog)

        assertEquals(1, source.callCount)
    }

    @Test
    fun `retry is the one path allowed to fetch again`() = runTest {
        val source = FakeStorySource()
        val viewModel = viewModel(source)

        viewModel.sendIntent(StoryRailIntent.Retry)

        assertEquals(2, source.callCount)
    }

    /* ---- Opening, and view tracking -------------------------------------------------------- */

    @Test
    fun `tapping a channel raises the viewer event for that position`() = runTest {
        val viewModel = viewModel(FakeStorySource())

        viewModel.events.test {
            viewModel.sendIntent(StoryRailIntent.OpenChannel(1))
            assertEquals(StoryRailEvent.OpenViewer(1), awaitItem())
        }
    }

    @Test
    fun `a channel marked seen on the catalogue greys out on the rail`() = runTest {
        val source = FakeStorySource()
        val catalog = StoryCatalog(source)
        val viewModel = StoryRailViewModel(catalog)
        val channel = viewModel.uiState.value.channels.first()
        assertEquals(false, viewModel.uiState.value.isSeen(channel))

        catalog.markChannelSeen(channel.key)

        assertTrue(viewModel.uiState.value.isSeen(channel))
    }
}
