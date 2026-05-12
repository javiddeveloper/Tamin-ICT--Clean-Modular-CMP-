package com.tamin.taminhamrah.ui.settings

import com.tamin.taminhamrah.domain.model.AccentColor
import com.tamin.taminhamrah.feature.settings.presentation.SettingsUiState
import com.tamin.taminhamrah.domain.model.CacheInfo
import com.tamin.taminhamrah.domain.model.NsfwFilterLevel
import com.tamin.taminhamrah.domain.model.PollingInterval
import com.tamin.taminhamrah.domain.model.SortOrder
import com.tamin.taminhamrah.domain.model.TimePeriod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SettingsUiStateTest {

    @Test
    fun default_state_has_expected_values() {
        val state = SettingsUiState()

        assertEquals(NsfwFilterLevel.Off, state.nsfwFilterLevel)
        assertEquals(SortOrder.MostDownloaded, state.defaultSortOrder)
        assertEquals(TimePeriod.AllTime, state.defaultTimePeriod)
        assertEquals(2, state.gridColumns)
        assertTrue(state.hiddenModels.isEmpty())
        assertTrue(state.excludedTags.isEmpty())
        assertNull(state.apiKey)
        assertNull(state.connectedUsername)
        assertFalse(state.isValidatingApiKey)
        assertNull(state.apiKeyError)
        assertFalse(state.powerUserMode)
        assertFalse(state.notificationsEnabled)
        assertEquals(PollingInterval.Off, state.pollingInterval)
        assertEquals(AccentColor.Blue, state.accentColor)
        assertFalse(state.amoledDarkMode)
        assertTrue(state.isOnline)
        assertTrue(state.offlineCacheEnabled)
        assertEquals(200, state.cacheSizeLimitMb)
        assertEquals(CacheInfo(0, 0), state.cacheInfo)
    }

    @Test
    fun copy_updates_only_specified_fields() {
        val original = SettingsUiState()
        val updated = original.copy(isOnline = false, gridColumns = 3)

        assertFalse(updated.isOnline)
        assertEquals(3, updated.gridColumns)
        // Other fields remain unchanged
        assertEquals(NsfwFilterLevel.Off, updated.nsfwFilterLevel)
        assertEquals(200, updated.cacheSizeLimitMb)
    }
}
