package com.tamin.taminhamrah.repository.feature

import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * A developer-only stand-in for the server's answer on a flag — set from the "Feature flags" screen
 * in Developer Options so a build without server-side control over a given state can still be
 * tested end to end (a temporarily-disabled service with its message, an error banner, …).
 *
 * [FeatureManagerImpl][com.tamin.taminhamrah.data.feature.FeatureManagerImpl] reads an override
 * ahead of the real menu — present, it wins outright; absent, the menu answers as always. Never
 * consulted outside a debug build: an implementation is expected to behave exactly like
 * [NoOpFeatureFlagOverrideRepository] in release, whatever is left over in storage from a prior
 * debug install.
 */
interface FeatureFlagOverrideRepository {
    /** Every flag currently overridden, keyed by flag — empty when nothing is overridden. */
    fun observeOverrides(): Flow<Map<FeatureFlag, FeatureStatus>>

    fun setOverride(flag: FeatureFlag, status: FeatureStatus)
    fun clearOverride(flag: FeatureFlag)
    fun clearAllOverrides()
}

/** The default for [com.tamin.taminhamrah.data.feature.FeatureManagerImpl] outside of DI — no flag is ever overridden. */
object NoOpFeatureFlagOverrideRepository : FeatureFlagOverrideRepository {
    override fun observeOverrides(): Flow<Map<FeatureFlag, FeatureStatus>> = flowOf(emptyMap())
    override fun setOverride(flag: FeatureFlag, status: FeatureStatus) = Unit
    override fun clearOverride(flag: FeatureFlag) = Unit
    override fun clearAllOverrides() = Unit
}
