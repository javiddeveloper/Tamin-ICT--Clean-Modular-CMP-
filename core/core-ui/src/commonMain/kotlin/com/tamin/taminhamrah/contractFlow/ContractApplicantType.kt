package com.tamin.taminhamrah.contractFlow

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_applicant_guardian
import taminx.core.core_ui.contract_applicant_personal

enum class ContractApplicantType(val labelRes: StringResource) {
    PERSONAL(Res.string.contract_applicant_personal),
    GUARDIAN(Res.string.contract_applicant_guardian),
}
