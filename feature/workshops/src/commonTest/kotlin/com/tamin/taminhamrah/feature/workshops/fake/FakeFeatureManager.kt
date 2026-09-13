package com.tamin.taminhamrah.feature.workshops.fake

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * A feature manager a test can gate.
 *
 * [disabled] names the flags to report as off, so a test can switch off one service and leave the
 * rest reachable — which is the case the workshop action menu has to get right.
 */
class FakeFeatureManager(
    var disabled: Set<FeatureFlag> = emptySet(),
    var error: Throwable? = null,
) : FeatureManager {

    override fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus> =
        flowOf(
            if (flag in disabled) FeatureStatus.Disabled(null) else FeatureStatus.Enabled,
        )

    override suspend fun isFeatureEnabled(flag: FeatureFlag): Boolean {
        error?.let { throw it }
        return flag !in disabled
    }

    override suspend fun getDisabledMessage(flag: FeatureFlag): String? = null
}
