package com.tamin.taminhamrah.core.datastore

import com.russhwolf.settings.MapSettings
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

/**
 * Covers the persistence contract of dev_opt_feature_flag_* overrides: every [FeatureStatus] shape
 * round-trips (including its message/url), clearing drops it from storage rather than just from the
 * in-memory cache, and a release build never applies whatever a debug install left behind.
 */
class FeatureFlagOverrideRepositoryImplTest {

    @Test
    fun `setOverride then observeOverrides reports every shape with its message`() = runTest {
        val settings = MapSettings()
        val repository = FeatureFlagOverrideRepositoryImpl(settings, isDebug = true)

        repository.setOverride(FeatureFlag.CONTRACTS, FeatureStatus.Enabled)
        repository.setOverride(FeatureFlag.PRESCRIPTION, FeatureStatus.Disabled("غیرفعال شد"))
        repository.setOverride(FeatureFlag.PAY_ROLL, FeatureStatus.TemporaryDisabled("موقتاً غیرفعال"))
        repository.setOverride(FeatureFlag.BANK_ACCOUNT_LIST, FeatureStatus.EnabledWithError("هشدار"))
        repository.setOverride(FeatureFlag.LAWS, FeatureStatus.WebView("https://tamin.ir"))

        val overrides = repository.observeOverrides().first()
        assertEquals(FeatureStatus.Enabled, overrides[FeatureFlag.CONTRACTS])
        assertEquals(FeatureStatus.Disabled("غیرفعال شد"), overrides[FeatureFlag.PRESCRIPTION])
        assertEquals(FeatureStatus.TemporaryDisabled("موقتاً غیرفعال"), overrides[FeatureFlag.PAY_ROLL])
        assertEquals(FeatureStatus.EnabledWithError("هشدار"), overrides[FeatureFlag.BANK_ACCOUNT_LIST])
        assertEquals(FeatureStatus.WebView("https://tamin.ir"), overrides[FeatureFlag.LAWS])
    }

    @Test
    fun `clearOverride removes the flag from a freshly loaded repository`() = runTest {
        val settings = MapSettings()
        FeatureFlagOverrideRepositoryImpl(settings, isDebug = true)
            .setOverride(FeatureFlag.CONTRACTS, FeatureStatus.Disabled("x"))

        val repository = FeatureFlagOverrideRepositoryImpl(settings, isDebug = true)
        repository.clearOverride(FeatureFlag.CONTRACTS)

        assertNull(repository.observeOverrides().first()[FeatureFlag.CONTRACTS])
        assertFalse(settings.hasKey("dev_opt_feature_flag_${FeatureFlag.CONTRACTS.id}_kind"))
    }

    @Test
    fun `clearAllOverrides drops every override at once`() = runTest {
        val settings = MapSettings()
        val repository = FeatureFlagOverrideRepositoryImpl(settings, isDebug = true)
        repository.setOverride(FeatureFlag.CONTRACTS, FeatureStatus.Disabled("x"))
        repository.setOverride(FeatureFlag.PRESCRIPTION, FeatureStatus.Enabled)

        repository.clearAllOverrides()

        val overrides = repository.observeOverrides().first()
        assertNull(overrides[FeatureFlag.CONTRACTS])
        assertNull(overrides[FeatureFlag.PRESCRIPTION])
    }

    @Test
    fun `release mode never loads or applies a stored override`() = runTest {
        val settings = MapSettings()
        FeatureFlagOverrideRepositoryImpl(settings, isDebug = true)
            .setOverride(FeatureFlag.CONTRACTS, FeatureStatus.Disabled("x"))

        val repository = FeatureFlagOverrideRepositoryImpl(settings, isDebug = false)
        repository.setOverride(FeatureFlag.PRESCRIPTION, FeatureStatus.Disabled("y"))

        val overrides = repository.observeOverrides().first()
        assertNull(overrides[FeatureFlag.CONTRACTS])
        assertNull(overrides[FeatureFlag.PRESCRIPTION])
    }
}
