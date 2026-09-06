package com.tamin.taminhamrah.contractFlow

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_eligibility_reason_age_under_50
import taminx.core.core_ui.contract_eligibility_reason_history_age
import taminx.core.core_ui.contract_eligibility_reason_max_two_contracts
import taminx.core.core_ui.contract_eligibility_reason_min_history
import taminx.core.core_ui.contract_eligibility_reason_not_met
import taminx.core.core_ui.contract_eligibility_reason_zero_history

enum class ContractEligibilityReason(val textRes: StringResource) {
    MIN_TEN_YEARS_HISTORY(Res.string.contract_eligibility_reason_min_history),
    AGE_UNDER_FIFTY(Res.string.contract_eligibility_reason_age_under_50),
    ZERO_HISTORY_PLACEHOLDER(Res.string.contract_eligibility_reason_zero_history),
    MAX_TWO_FREELANCE_CONTRACTS(Res.string.contract_eligibility_reason_max_two_contracts),
    AGE_HISTORY_NOT_MET(Res.string.contract_eligibility_reason_not_met),
    HISTORY_AND_AGE_DYNAMIC(Res.string.contract_eligibility_reason_history_age),
}
