package com.tamin.taminhamrah.model.employerInfo

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_info_company_type_foreign
import taminx.core.core_ui.employer_info_company_type_government
import taminx.core.core_ui.employer_info_company_type_private
import taminx.core.core_ui.employer_info_company_type_public

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

/** [code] is what the service stores; [titleRes] is what the picker shows. */
@Immutable
data class CompanyTypePR(
    val code: String,
    val titleRes: StringResource,
)

/**
 * The four company types, in the order the design lists them — which is by code, and is neither
 * alphabetical nor the order the old app used. The order is data: it lives beside the table.
 */
val COMPANY_TYPES: ImmutableList<CompanyTypePR> = persistentListOf(
    CompanyTypePR(code = "01", titleRes = Res.string.employer_info_company_type_government),
    CompanyTypePR(code = "02", titleRes = Res.string.employer_info_company_type_public),
    CompanyTypePR(code = "03", titleRes = Res.string.employer_info_company_type_private),
    CompanyTypePR(code = "04", titleRes = Res.string.employer_info_company_type_foreign),
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
    /** "شعبهٔ ۲ مشهد · ۱۲۰۲" — joined by the mapper so the row does no work per recomposition. */
    val branchLabel: String = "",
    val letDate: String = "",
    val email: String = "",
    val mobile: String = "",
    val address: String = "",
)
