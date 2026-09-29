package com.tamin.taminhamrah.feature

import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface FeatureManager {
    fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus>
    suspend fun isFeatureEnabled(flag: FeatureFlag): Boolean
    suspend fun getDisabledMessage(flag: FeatureFlag): String?

    /** The menu's name for [flag] — the title the server gives the service — or null. */
    suspend fun getFeatureTitle(flag: FeatureFlag): String? = null

    /**
     * The state of every flag in [flags], delivered together so a screen gating several rows shows
     * them all as unresolved until the menu answers and never flips them one by one.
     *
     * A menu that cannot be read leaves every flag [FeatureStatus.Enabled]: a failed lookup must
     * not lock a person out of a service the server never said was off.
     */
    fun observeFeatureStatuses(flags: Set<FeatureFlag>): Flow<Map<FeatureFlag, FeatureStatus>> {
        if (flags.isEmpty()) return flowOf(emptyMap())
        return combine(flags.map { flag -> getFeatureStatus(flag).map { flag to it } }) { it.toMap() }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(flags.associateWith { FeatureStatus.Enabled })
            }
    }
}
