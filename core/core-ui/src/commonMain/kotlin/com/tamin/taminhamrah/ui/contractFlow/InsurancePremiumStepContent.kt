package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.contractFlow.SpcPremiumRateOptionPR
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_field_job
import taminx.core.core_ui.contract_premium_rate_not_found

@Composable
fun InsurancePremiumStepContent(
    premiumRates: List<SpcPremiumRateOptionPR>,
    selectedCode: String?,
    isLoading: Boolean,
    onRateSelected: (SpcPremiumRateOptionPR) -> Unit,
    isRateSelectionEnabled: Boolean = true,
    showFreeJobSelector: Boolean = false,
    freeJobs: List<FreeJobDN> = emptyList(),
    selectedFreeJobCode: String? = null,
    selectedFreeJobName: String? = null,
    isFreeJobsLoading: Boolean = false,
    onFreeJobSelected: (FreeJobDN) -> Unit = {},
) {
    when {
        isLoading && premiumRates.isEmpty() -> {
            InsurancePremiumStepShimmerSkeleton()
        }

        premiumRates.isEmpty() -> {
            Text(
                text = stringResource(Res.string.contract_premium_rate_not_found),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        else -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (showFreeJobSelector) {
                    SelectableField(
                        label = stringResource(Res.string.contract_field_job),
                        options = freeJobs,
                        selectedCode = selectedFreeJobCode.orEmpty(),
                        selectedName = selectedFreeJobName.orEmpty(),
                        optionCode = { it.jobCode.orEmpty() },
                        optionName = { it.discrioption.orEmpty() },
                        isLoading = isFreeJobsLoading,
                        onSelected = onFreeJobSelected,
                    )
                }
                premiumRates.forEach { rate ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = rate.code == selectedCode,
                                enabled = isRateSelectionEnabled,
                                onClick = {
                                    if (isRateSelectionEnabled) onRateSelected(rate)
                                },
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RadioButton(
                            selected = rate.code == selectedCode,
                            onClick = {
                                if (isRateSelectionEnabled) onRateSelected(rate)
                            },
                            enabled = isRateSelectionEnabled,
                        )
                        Text(
                            text = rate.description,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun InsurancePremiumStepContentPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        InsurancePremiumStepContent(
            premiumRates = listOf(
                SpcPremiumRateOptionPR(code = "12", description = "نرخ ۱۲ درصد (بازنشستگی و فوت بعد از بازنشستگی)", insurancePercent = "12"),
                SpcPremiumRateOptionPR(code = "14", description = "نرخ ۱۴ درصد (بازنشستگی و فوت قبل و بعد از بازنشستگی)", insurancePercent = "14"),
                SpcPremiumRateOptionPR(code = "18", description = "نرخ ۱۸ درصد (بازنشستگی، ازکارافتادگی و فوت)", insurancePercent = "18"),
            ),
            selectedCode = "14",
            isLoading = false,
            onRateSelected = {},
        )
    }
}
