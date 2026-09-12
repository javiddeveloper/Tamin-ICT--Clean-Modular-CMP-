package com.tamin.taminhamrah.feature.contractaffair.fake

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** Reports one status for every flag; set [status] to gate a feature in a test. */
class FakeFeatureManager(
    var status: FeatureStatus = FeatureStatus.Enabled,
) : FeatureManager {

    override fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus> = flowOf(status)

    override suspend fun isFeatureEnabled(flag: FeatureFlag): Boolean =
        status is FeatureStatus.Enabled

    override suspend fun getDisabledMessage(flag: FeatureFlag): String? = null
}
