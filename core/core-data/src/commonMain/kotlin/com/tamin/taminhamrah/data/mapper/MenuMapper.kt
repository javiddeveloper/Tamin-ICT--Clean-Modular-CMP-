package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.MenuEntity
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MainServiceDto
import com.tamin.taminhamrah.model.common.MenuServiceStatus
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN

fun MainServiceDto.toDomain() = MainServiceDN(
    active = active,
    hiddenForVersions = hiddenForVersions,
    icon = icon,
    id = id,
    name = name,
    newService = newService,
    showRole = showRole,
    sorting = sorting,
    subtitle = subtitle,
    url = url,
    status = status?.toDomain(),
    message = message
)

fun MenuServiceStatus.toDomain() = when (this) {
    MenuServiceStatus.ACTIVE -> MenuServiceStatusDN.ACTIVE
    MenuServiceStatus.TEMPORARY_DISABLED -> MenuServiceStatusDN.TEMPORARY_DISABLED
    MenuServiceStatus.DISABLED -> MenuServiceStatusDN.DISABLED
    MenuServiceStatus.COMPLETELY_DISABLED -> MenuServiceStatusDN.COMPLETELY_DISABLED
    MenuServiceStatus.ENABLED_WITH_ERROR -> MenuServiceStatusDN.ENABLED_WITH_ERROR
    MenuServiceStatus.WEB_VIEW -> MenuServiceStatusDN.WEB_VIEW
}

fun MainServiceDN.toEntity() = MenuEntity(
    id = id ?: 0,
    name = name,
    subtitle = subtitle,
    icon = icon,
    active = active,
    newService = newService,
    sorting = sorting,
    url = url,
    status = status,
    message = message,
    showRole = showRole,
    hiddenForVersions = hiddenForVersions
)

fun MenuEntity.toDomain() = MainServiceDN(
    id = id,
    name = name,
    subtitle = subtitle,
    icon = icon,
    active = active,
    newService = newService,
    sorting = sorting,
    url = url,
    status = status,
    message = message,
    showRole = showRole,
    hiddenForVersions = hiddenForVersions
)
