package com.tamin.taminhamrah.feature.settings.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.settings.fake.FakeUserPreferencesRepository
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsEvent
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsIntent
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.model.FontSizeOption
import com.tamin.taminhamrah.useCases.common.SetFontSizeUseCase
import com.tamin.taminhamrah.useCases.common.SetThemeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var userPreferencesRepository: FakeUserPreferencesRepository
    private lateinit var viewModel: SettingsViewModel

    @BeforeTest
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        userPreferencesRepository = FakeUserPreferencesRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): SettingsViewModel = SettingsViewModel(
        setThemeUseCase = SetThemeUseCase(userPreferencesRepository),
        setFontSizeUseCase = SetFontSizeUseCase(userPreferencesRepository),
        userPreferencesRepository = userPreferencesRepository,
    )

    @Test
    fun `initial state picks up MEDIUM font size and FOLLOW_SYSTEM from the repository default`() = runTest(testDispatcher) {
        // UserData.DEFAULT ships with darkThemeConfig = FOLLOW_SYSTEM, so the ViewModel's
        // init collectors flip isFollowSystem to true before any intent is sent.
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(FontSizeOption.MEDIUM, state.fontSize)
        assertTrue(state.isFollowSystem)
    }

    @Test
    fun `state picks up a font size already persisted before the ViewModel was created`() = runTest(testDispatcher) {
        userPreferencesRepository.setFontSize(FontSizeOption.LARGE)
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(FontSizeOption.LARGE, viewModel.uiState.value.fontSize)
    }

    @Test
    fun `SelectFontSize intent persists the font size and updates state`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(SettingsIntent.SelectFontSize(FontSizeOption.SMALL))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(FontSizeOption.SMALL, viewModel.uiState.value.fontSize)
        assertEquals(FontSizeOption.SMALL, userPreferencesRepository.userData.value.fontSize)
    }

    @Test
    fun `ToggleNightMode(true) sets DARK theme and clears follow-system`() = runTest(testDispatcher) {
        userPreferencesRepository.setDarkThemeConfig(DarkThemeConfig.FOLLOW_SYSTEM)
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isFollowSystem)

        viewModel.sendIntent(SettingsIntent.ToggleNightMode(isDark = true))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DarkThemeConfig.DARK, userPreferencesRepository.userData.value.darkThemeConfig)
        assertFalse(viewModel.uiState.value.isFollowSystem)
    }

    @Test
    fun `ToggleNightMode(false) sets LIGHT theme`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(SettingsIntent.ToggleNightMode(isDark = false))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DarkThemeConfig.LIGHT, userPreferencesRepository.userData.value.darkThemeConfig)
        assertFalse(viewModel.uiState.value.isFollowSystem)
    }

    @Test
    fun `ToggleFollowSystem(true) sets FOLLOW_SYSTEM theme and updates state`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(SettingsIntent.ToggleFollowSystem(enabled = true))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DarkThemeConfig.FOLLOW_SYSTEM, userPreferencesRepository.userData.value.darkThemeConfig)
        assertTrue(viewModel.uiState.value.isFollowSystem)
    }

    @Test
    fun `ToggleFollowSystem(false) falls back to LIGHT theme`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(SettingsIntent.ToggleFollowSystem(enabled = false))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DarkThemeConfig.LIGHT, userPreferencesRepository.userData.value.darkThemeConfig)
        assertFalse(viewModel.uiState.value.isFollowSystem)
    }

    @Test
    fun `OnBackClicked sends a NavigateBack event`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(SettingsIntent.OnBackClicked)
            assertEquals(SettingsEvent.NavigateBack, awaitItem())
        }
    }
}
