package com.tamin.taminhamrah.feature

import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import kotlinx.coroutines.flow.Flow

interface FeatureManager {
    fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus>
    suspend fun isFeatureEnabled(flag: FeatureFlag): Boolean
}
