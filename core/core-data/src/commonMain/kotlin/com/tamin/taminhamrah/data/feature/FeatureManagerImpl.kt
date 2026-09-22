package com.tamin.taminhamrah.data.feature

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.featureStatusOf
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FeatureManagerImpl(
    private val commonRepository: CommonRepository
) : FeatureManager {
    override fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus> {
        return commonRepository.getMainMenu("", false).map { menu -> menu.featureStatusOf(flag) }
    }

    /** One read of the menu answers every flag, instead of one read per flag. */
    override fun observeFeatureStatuses(flags: Set<FeatureFlag>): Flow<Map<FeatureFlag, FeatureStatus>> {
        if (flags.isEmpty()) return flowOf(emptyMap())
        return commonRepository.getMainMenu("", false)
            .map { menu -> flags.associateWith { flag -> menu.featureStatusOf(flag) } }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(flags.associateWith { FeatureStatus.Enabled })
            }
    }

    override suspend fun isFeatureEnabled(flag: FeatureFlag): Boolean {
        return try {
            getFeatureStatus(flag).first() is FeatureStatus.Enabled
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getDisabledMessage(flag: FeatureFlag): String? {
        return try {
            val menu = commonRepository.getMainMenu("", false).first()
            menu.find { it.id == flag.id }?.message
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getFeatureTitle(flag: FeatureFlag): String? {
        return try {
            val menu = commonRepository.getMainMenu("", false).first()
            menu.find { it.id == flag.id }?.name?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }
}
