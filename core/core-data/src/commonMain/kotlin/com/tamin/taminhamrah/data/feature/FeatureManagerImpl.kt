package com.tamin.taminhamrah.data.feature

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.featureStatusOf
import com.tamin.taminhamrah.repository.common.CommonRepository
import com.tamin.taminhamrah.repository.feature.FeatureFlagOverrideRepository
import com.tamin.taminhamrah.repository.feature.NoOpFeatureFlagOverrideRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FeatureManagerImpl(
    private val commonRepository: CommonRepository,
    /** Debug-only stand-in set from the "Feature flags" developer screen; wins over the real menu when present. */
    private val overrideRepository: FeatureFlagOverrideRepository = NoOpFeatureFlagOverrideRepository,
) : FeatureManager {
    override fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus> =
        combine(overrideRepository.observeOverrides(), commonRepository.getMainMenu("", false)) { overrides, menu ->
            overrides[flag] ?: menu.featureStatusOf(flag)
        }

    /** One read of the menu answers every flag, instead of one read per flag. */
    override fun observeFeatureStatuses(flags: Set<FeatureFlag>): Flow<Map<FeatureFlag, FeatureStatus>> {
        if (flags.isEmpty()) return flowOf(emptyMap())
        return combine(overrideRepository.observeOverrides(), commonRepository.getMainMenu("", false)) { overrides, menu ->
            flags.associateWith { flag -> overrides[flag] ?: menu.featureStatusOf(flag) }
        }.catch { e ->
            if (e is CancellationException) throw e
            emit(flags.associateWith { FeatureStatus.Enabled })
        }
    }

    override suspend fun isFeatureEnabled(flag: FeatureFlag): Boolean {
        return try {
            getFeatureStatus(flag).first() is FeatureStatus.Enabled
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getDisabledMessage(flag: FeatureFlag): String? {
        return try {
            getFeatureStatus(flag).first().serverMessage
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getFeatureTitle(flag: FeatureFlag): String? {
        return try {
            val menu = commonRepository.getMainMenu("", false).first()
            menu.find { it.id == flag.id }?.name?.takeIf { it.isNotBlank() }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }
}
