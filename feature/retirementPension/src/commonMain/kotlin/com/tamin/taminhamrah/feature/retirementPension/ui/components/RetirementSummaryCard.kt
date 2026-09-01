package com.tamin.taminhamrah.feature.retirementPension.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.model.pension.retirement.RetirementHistoryPR
import com.tamin.taminhamrah.model.pension.retirement.RetirementInsuredPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.retirement_pension_history_summary_value
import taminx.core.core_ui.retirement_pension_label_address
import taminx.core.core_ui.retirement_pension_label_branch
import taminx.core.core_ui.retirement_pension_label_full_name
import taminx.core.core_ui.retirement_pension_label_history
import taminx.core.core_ui.retirement_pension_label_insurance_number
import taminx.core.core_ui.retirement_pension_label_phone
import taminx.core.core_ui.retirement_pension_label_workshop_code
import taminx.core.core_ui.retirement_pension_label_workshop_name
import taminx.core.core_ui.retirement_pension_value_placeholder

/**
 * The eight-row recap of the request.
 *
 * Shared by step 8 and the track screen, which show the same rows under different headings — the
 * design repeats it, so it is one composable rather than two that drift.
 */
@Composable
internal fun RetirementSummaryCard(
    title: String?,
    insured: RetirementInsuredPR?,
    phoneNumber: String,
    address: String,
    workshopName: String,
    workshopCode: String,
    branchName: String,
    history: RetirementHistoryPR?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val placeholder = stringResource(Res.string.retirement_pension_value_placeholder)

    RetirementCard(modifier = modifier) {
        if (title != null) {
            TaminText(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = Spacing.sm),
            )
        }
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_full_name),
            value = insured?.fullName?.ifBlank { placeholder } ?: placeholder,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_insurance_number),
            value = insured?.insuranceNumber?.ifBlank { placeholder } ?: placeholder,
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_phone),
            value = phoneNumber.ifBlank { placeholder },
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_address),
            value = address.ifBlank { placeholder },
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_workshop_name),
            value = workshopName.ifBlank { placeholder },
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_workshop_code),
            value = workshopCode.ifBlank { placeholder },
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_branch),
            value = branchName.ifBlank { placeholder },
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_history),
            value = history?.let {
                stringResource(
                    Res.string.retirement_pension_history_summary_value,
                    it.yearsValue.toString().toPersianDigits(),
                    it.monthsValue.toString().toPersianDigits(),
                )
            } ?: placeholder,
            numeric = false,
        )
    }
}
