package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.contractFlow.ContractEligibilityReason
import com.tamin.taminhamrah.model.contractFlow.ContractEligibilityPR
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_eligibility_eligible
import taminx.core.core_ui.contract_eligibility_ineligible

@Composable
fun ContractEligibilityPR.reasonText(): String = when (reason) {
    ContractEligibilityReason.HISTORY_AND_AGE_DYNAMIC -> stringResource(
        reason.textRes,
        historyDays.orEmpty(),
        ageFormatted.orEmpty(),
    )
    else -> stringResource(reason.textRes)
}

@Composable
fun ContractEligibilityPR.eligibilityMessage(insuranceTypeLabel: String): String =
    if (isEligible) {
        stringResource(
            Res.string.contract_eligibility_eligible,
            reasonText(),
            insuranceTypeLabel,
        )
    } else {
        stringResource(
            Res.string.contract_eligibility_ineligible,
            reasonText(),
            insuranceTypeLabel,
        )
    }
