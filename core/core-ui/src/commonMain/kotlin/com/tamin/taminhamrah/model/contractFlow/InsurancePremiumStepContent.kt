package com.tamin.taminhamrah.model.contractFlow

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
import com.tamin.taminhamrah.model.contracts.FreeJobDN

@Composable
fun InsurancePremiumStepContent(
    premiumRates: List<SpcPremiumRateOptionPR>,
    selectedCode: String?,
    isLoading: Boolean,
    onRateSelected: (SpcPremiumRateOptionPR) -> Unit,
    showFreeJobSelector: Boolean = false,
    freeJobs: List<FreeJobDN> = emptyList(),
    selectedFreeJobCode: String? = null,
    selectedFreeJobName: String? = null,
    isFreeJobsLoading: Boolean = false,
    onFreeJobSelected: (FreeJobDN) -> Unit = {},
) {
    when {
        isLoading && premiumRates.isEmpty() -> {
            CircularProgressIndicator()
        }

        premiumRates.isEmpty() -> {
            Text(
                text = "نرخ حق بیمه‌ای یافت نشد.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        else -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (showFreeJobSelector) {
                    SelectableField(
                        label = "شغل",
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
                                onClick = { onRateSelected(rate) },
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RadioButton(
                            selected = rate.code == selectedCode,
                            onClick = { onRateSelected(rate) },
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
