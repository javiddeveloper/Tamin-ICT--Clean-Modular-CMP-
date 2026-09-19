package com.tamin.taminhamrah.feature

import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import kotlinx.coroutines.flow.Flow

interface FeatureManager {
    fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus>
    suspend fun isFeatureEnabled(flag: FeatureFlag): Boolean
    suspend fun getDisabledMessage(flag: FeatureFlag): String?

    /** The menu's name for [flag] — the title the server gives the service — or null. */
    suspend fun getFeatureTitle(flag: FeatureFlag): String? = null
}
