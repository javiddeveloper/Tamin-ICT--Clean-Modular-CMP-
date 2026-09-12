package com.tamin.taminhamrah.feature.retirementPension.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementFormError
import com.tamin.taminhamrah.model.pension.retirement.RetirementHistoryPR
import com.tamin.taminhamrah.model.pension.retirement.RetirementInsuredPR
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.retirement_pension_final_confirm
import taminx.core.core_ui.retirement_pension_final_note

/** Step 8 — the recap, what happens next, and the undertaking that submits the request. */
@Composable
internal fun RetirementFinalStep(
    insured: RetirementInsuredPR?,
    phoneNumber: String,
    address: String,
    workshopName: String,
    workshopCode: String,
    branchName: String,
    history: RetirementHistoryPR?,
    isConfirmed: Boolean,
    error: RetirementFormError?,
    onConfirmedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    RetirementStepColumn(modifier = modifier) {
        RetirementSummaryCard(
            title = null,
            insured = insured,
            phoneNumber = phoneNumber,
            address = address,
            workshopName = workshopName,
            workshopCode = workshopCode,
            branchName = branchName,
            history = history,
        )

        BannerCard(
            message = stringResource(Res.string.retirement_pension_final_note),
            type = BannerType.Info,
            // The design reads its informational prose in slate; only the glyph is blue.
            textColor = LocalTaminColors.current.textSecondary,
        )

        RetirementConsentRow(
            text = stringResource(Res.string.retirement_pension_final_confirm),
            checked = isConfirmed,
            isError = error != null,
            onCheckedChange = onConfirmedChange,
        )
        if (error != null) RetirementErrorLine(error)
    }
}
