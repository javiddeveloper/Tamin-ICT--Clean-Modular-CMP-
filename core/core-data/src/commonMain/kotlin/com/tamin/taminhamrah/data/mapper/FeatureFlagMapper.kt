package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.common.FeatureDN
import com.tamin.taminhamrah.model.common.FeaturePlatform
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.FeatureType
import com.tamin.taminhamrah.model.common.MainServiceDto
import com.tamin.taminhamrah.model.common.UserRole

/**
 * Maps the raw [MainServiceDto] to the rich [FeatureDN] domain model (EM-2381).
 *
 * Returns null when the entry cannot be identified ([MainServiceDto.id] / name missing), so the
 * repository can safely drop malformed rows.
 */
fun MainServiceDto.toDomainOrNull(): FeatureDN? {
    val safeId = id ?: return null
    val safeName = name ?: return null
    return FeatureDN(
        id = safeId,
        title = safeName,
        subtitle = subtitle?.takeIf { it.isNotBlank() },
        icon = icon,
        sorting = sorting ?: Int.MAX_VALUE,
        isNew = newService == true,
        type = FeatureType.fromRaw(type),
        roles = showRole.mapNotNull { UserRole.fromRaw(it).takeIf { role -> role != UserRole.Unknown } },
        hiddenForVersions = hiddenForVersions.filterNotNull(),
        status = resolveStatus(),
        platform = resolvePlatform(),
    )
}

/**
 * Decodes the feature-flag status. Prefers the explicit `statusType` when the backend sends it,
 * otherwise falls back to the legacy [MainServiceDto.active] boolean.
 */
private fun MainServiceDto.resolveStatus(): FeatureStatus = when (statusType) {
    STATUS_ACTIVE -> FeatureStatus.Active
    STATUS_INACTIVE -> FeatureStatus.Inactive
    STATUS_ERROR -> FeatureStatus.Error(statusMessage ?: DEFAULT_ERROR_MESSAGE)
    STATUS_TEMP_DISABLED -> FeatureStatus.TemporarilyDisabled(statusMessage ?: DEFAULT_TEMP_DISABLED_MESSAGE)
    else -> if (active == true) FeatureStatus.Active else FeatureStatus.Inactive
}

private fun MainServiceDto.resolvePlatform(): FeaturePlatform {
    val url = webUrl
    return if (isWeb == true && !url.isNullOrBlank()) FeaturePlatform.Web(url) else FeaturePlatform.Native
}

private const val STATUS_ACTIVE = 0
private const val STATUS_INACTIVE = 1
private const val STATUS_ERROR = 2
private const val STATUS_TEMP_DISABLED = 3

private const val DEFAULT_ERROR_MESSAGE = "این سرویس با خطا مواجه شده است."
private const val DEFAULT_TEMP_DISABLED_MESSAGE = "این سرویس موقتاً غیرفعال است."
