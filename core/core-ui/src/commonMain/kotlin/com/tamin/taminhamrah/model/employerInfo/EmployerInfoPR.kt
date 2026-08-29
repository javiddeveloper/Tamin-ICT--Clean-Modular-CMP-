package com.tamin.taminhamrah.model.employerInfo

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class LegalWorkshopPR(
    val name: String? = null,
    val nationalCode: String? = null,
)

@Immutable
@Serializable
data class LegalWorkshopCeoPR(
    val firstName: String? = null,
    val lastName: String? = null,
    val fullName: String = "",
)

@Immutable
@Serializable
data class CompanyTypePR(
    val code: String,
    val title: String,
)

val COMPANY_TYPES: ImmutableList<CompanyTypePR> = persistentListOf(
    CompanyTypePR(code = "01", title = "دولتی"),
    CompanyTypePR(code = "02", title = "عمومی - غیردولتی"),
    CompanyTypePR(code = "03", title = "خصوصی"),
    CompanyTypePR(code = "04", title = "شخصیت حقوقی خارجی"),
)

@Immutable
@Serializable
data class WorkshopItemPR(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val branch: String = "",
    val bcode: String = "",
    val isLegal: Boolean = false,
    val characterDesc: String = "",
    val letDate: String = "",
    val email: String = "",
    val mobile: String = "",
    val address: String = "",
)
