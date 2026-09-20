package com.tamin.taminhamrah.feature.treatment.fake

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Reports one status for every flag; set [status] to gate a feature in a test.
 *
 * Set [error] to fail the lookup instead — a `CancellationException` there stands in for the
 * screen going away while the flag is being read.
 */
class FakeFeatureManager(
    var status: FeatureStatus = FeatureStatus.Enabled,
    var error: Throwable? = null,
) : FeatureManager {

    override fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus> = flow {
        error?.let { throw it }
        emit(status)
    }

    override suspend fun isFeatureEnabled(flag: FeatureFlag): Boolean =
        status is FeatureStatus.Enabled

    override suspend fun getDisabledMessage(flag: FeatureFlag): String? = null
}
