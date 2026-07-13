package com.tamin.taminhamrah.data.feature

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FeatureManagerImpl(
    private val commonRepository: CommonRepository
) : FeatureManager {
    override fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus> {
        return commonRepository.getMainMenu("", false).map { menu ->
            val item = menu.find { it.id == flag.id }
            mapToFeatureStatus(item)
        }
    }

    override suspend fun isFeatureEnabled(flag: FeatureFlag): Boolean {
        return try {
            getFeatureStatus(flag).first() is FeatureStatus.Enabled
        } catch (e: Exception) {
            false
        }
    }

    private fun mapToFeatureStatus(item: MainServiceDN?): FeatureStatus {
        if (item == null) return FeatureStatus.Disabled(null)
        if (item.active == false) return FeatureStatus.Disabled(item.message)

        return when (item.status) {
            MenuServiceStatusDN.ACTIVE -> FeatureStatus.Enabled
            MenuServiceStatusDN.TEMPORARY_DISABLED -> FeatureStatus.TemporaryDisabled(item.message)
            MenuServiceStatusDN.DISABLED -> FeatureStatus.Disabled(item.message)
            MenuServiceStatusDN.COMPLETELY_DISABLED -> FeatureStatus.Disabled(item.message)
            MenuServiceStatusDN.ENABLED_WITH_ERROR -> FeatureStatus.EnabledWithError(item.message)
            MenuServiceStatusDN.WEB_VIEW -> item.url?.let { FeatureStatus.WebView(it) } ?: FeatureStatus.Enabled
            null -> FeatureStatus.Enabled
        }
    }
}
