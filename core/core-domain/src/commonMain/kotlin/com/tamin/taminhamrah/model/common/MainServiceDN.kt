package com.tamin.taminhamrah.model.common

data class MainServiceDN(
    val active: Boolean? = null,
    val hiddenForVersions: List<Int?> = emptyList(),
    val icon: String? = null,
    val id: Int? = null,
    val name: String? = null,
    val newService: Boolean? = null,
    val showRole: List<Int?> = emptyList(),
    val sorting: Int? = null,
    val subtitle: String? = null,
    val url: String? = null,
    val status: MenuServiceStatusDN? = MenuServiceStatusDN.ACTIVE,
    val message: String? = null
)

enum class MenuServiceStatusDN {
    ACTIVE,
    TEMPORARY_DISABLED,
    DISABLED,
    COMPLETELY_DISABLED,
    ENABLED_WITH_ERROR,
    WEB_VIEW
}
