package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.contractFlow.FreelancePremiumRangePR
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_premium_calc_unavailable
import taminx.core.core_ui.contract_premium_calculate_monthly
import taminx.core.core_ui.contract_premium_monthly_salary
import taminx.core.core_ui.contract_premium_range_min_max
import taminx.core.core_ui.contract_premium_range_unavailable
import taminx.core.core_ui.contract_premium_selected_amount

@Composable
fun PremiumSalaryStepContent(
    premiumRange: FreelancePremiumRangePR?,
    selectedPremium: Long?,
    calculatedMonthlySalary: Long?,
    isLoading: Boolean,
    isCalculating: Boolean,
    onPremiumChange: (Long) -> Unit,
    onCalculate: () -> Unit,
    showPremiumSlider: Boolean = true,
) {
    when {
        showPremiumSlider && isLoading && premiumRange == null -> {
            CircularProgressIndicator()
        }

        !showPremiumSlider && calculatedMonthlySalary == null && isCalculating -> {
            CircularProgressIndicator()
        }

        showPremiumSlider && premiumRange == null -> {
            Text(
                text = stringResource(Res.string.contract_premium_range_unavailable),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        !showPremiumSlider && calculatedMonthlySalary == null && !isCalculating -> {
            Text(
                text = stringResource(Res.string.contract_premium_calc_unavailable),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        else -> {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (showPremiumSlider && premiumRange != null) {
                    val low = premiumRange.lowPremium.toFloat()
                    val high = premiumRange.highPremium.toFloat()
                    val current = (selectedPremium ?: premiumRange.lowPremium).toFloat().coerceIn(low, high)
                    val range = high - low

                    Text(
                        text = stringResource(
                            Res.string.contract_premium_range_min_max,
                            premiumRange.lowPremium.toString(),
                            premiumRange.highPremium.toString(),
                        ),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = stringResource(
                            Res.string.contract_premium_selected_amount,
                            current.toLong().toString(),
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    if (range > 0f) {
                        Slider(
                            value = current,
                            onValueChange = { onPremiumChange(it.toLong()) },
                            valueRange = low..high,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Button(
                        onClick = onCalculate,
                        enabled = !isCalculating,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (isCalculating) {
                            CircularProgressIndicator()
                        } else {
                            Text(stringResource(Res.string.contract_premium_calculate_monthly))
                        }
                    }
                }
                calculatedMonthlySalary?.let { salary ->
                    Text(
                        text = stringResource(
                            Res.string.contract_premium_monthly_salary,
                            salary.toString(),
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
