package com.tamin.taminhamrah.feature.stories.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.stories.FakeStoryRepository
import com.tamin.taminhamrah.useCases.stories.GetStoryChannelsUseCase
import com.tamin.taminhamrah.useCases.stories.ObserveSeenStoryChannelsUseCase
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

    private fun viewModel(repository: FakeStoryRepository) = StoryRailViewModel(
        getStoryChannelsUseCase = GetStoryChannelsUseCase(repository),
        observeSeenStoryChannelsUseCase = ObserveSeenStoryChannelsUseCase(repository),
    )

    /* ---- Showing the list ---------------------------------------------------------------- */

    @Test
    fun `loads the catalogue and shows its channels`() = runTest {
        val repository = FakeStoryRepository(listOf(testChannel("a"), testChannel("b")))

        val state = viewModel(repository).uiState.value

        assertEquals(StoryRailContent.Channels, state.content)
        assertEquals(listOf("a", "b"), state.channels.map { it.key })
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `a catalogue with no channels shows the empty state rather than an empty row`() = runTest {
        val state = viewModel(FakeStoryRepository(channels = emptyList())).uiState.value

        assertEquals(StoryRailContent.Empty, state.content)
        assertTrue(state.channels.isEmpty())
    }

    /* ---- Failing, and recovering ---------------------------------------------------------- */

    @Test
    fun `a repository that throws leaves the rail in its error state`() = runTest {
        val repository = FakeStoryRepository().apply { failWith = IllegalStateException("no network") }

        val state = viewModel(repository).uiState.value

        assertEquals(StoryRailContent.Error, state.content)
        assertNotNull(state.error)
    }

    @Test
    fun `retrying after a failure loads the list and clears the error`() = runTest {
        val repository = FakeStoryRepository().apply { failWith = IllegalStateException("no network") }
        val viewModel = viewModel(repository)
        assertEquals(StoryRailContent.Error, viewModel.uiState.value.content)

        repository.failWith = null
        viewModel.sendIntent(StoryRailIntent.Retry)

        val state = viewModel.uiState.value
        assertEquals(StoryRailContent.Channels, state.content)
        assertEquals(null, state.error)
    }

    /* ---- Fetching once -------------------------------------------------------------------- */

    @Test
    fun `repeated loads do not refetch the catalogue`() = runTest {
        val repository = FakeStoryRepository()
        val viewModel = viewModel(repository)
        // One from init.
        assertEquals(1, repository.fetchCount)

        repeat(3) { viewModel.sendIntent(StoryRailIntent.Load) }

        assertEquals(1, repository.fetchCount)
    }

    @Test
    fun `two rails sharing one repository fetch the catalogue once between them`() = runTest {
        val repository = FakeStoryRepository()

        viewModel(repository)
        viewModel(repository)

        assertEquals(1, repository.fetchCount)
    }

    @Test
    fun `retry does nothing when there is no failure to retry`() = runTest {
        val repository = FakeStoryRepository()
        val viewModel = viewModel(repository)

        viewModel.sendIntent(StoryRailIntent.Retry)

        // The retry only exists on the error row; without this guard a stray one would leave a
        // second permanent collector behind.
        assertEquals(1, repository.fetchCount)
    }

    /* ---- Opening, and view tracking -------------------------------------------------------- */

    @Test
    fun `tapping a channel raises the viewer event for that position`() = runTest {
        val viewModel = viewModel(FakeStoryRepository())

        viewModel.events.test {
            viewModel.sendIntent(StoryRailIntent.OpenChannel(1))
            assertEquals(StoryRailEvent.OpenViewer(1), awaitItem())
        }
    }

    @Test
    fun `a channel marked seen on the repository greys out on the rail`() = runTest {
        val repository = FakeStoryRepository()
        val viewModel = viewModel(repository)
        val channel = viewModel.uiState.value.channels.first()
        assertEquals(false, viewModel.uiState.value.isSeen(channel))

        // What the viewer does when it closes; the rail behind it has to notice.
        repository.markChannelSeen(channel.key)

        assertTrue(viewModel.uiState.value.isSeen(channel))
    }
}
