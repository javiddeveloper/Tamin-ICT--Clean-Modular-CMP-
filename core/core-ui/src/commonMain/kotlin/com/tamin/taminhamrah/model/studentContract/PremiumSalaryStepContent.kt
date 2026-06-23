package com.tamin.taminhamrah.model.studentContract

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

@Composable
fun PremiumSalaryStepContent(
    premiumRange: FreelancePremiumRangePR?,
    selectedPremium: Long?,
    calculatedMonthlySalary: Long?,
    isLoading: Boolean,
    isCalculating: Boolean,
    onPremiumChange: (Long) -> Unit,
    onCalculate: () -> Unit,
) {
    when {
        isLoading && premiumRange == null -> {
            CircularProgressIndicator()
        }

        premiumRange == null -> {
            Text(
                text = "محدوده حق بیمه در دسترس نیست.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        else -> {
            val low = premiumRange.lowPremium.toFloat()
            val high = premiumRange.highPremium.toFloat()
            val current = (selectedPremium ?: premiumRange.lowPremium).toFloat().coerceIn(low, high)
            val range = high - low

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "حداقل: ${premiumRange.lowPremium} — حداکثر: ${premiumRange.highPremium}",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = "مبلغ انتخابی: ${current.toLong()} ریال",
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
                        Text("محاسبه حق بیمه ماهانه")
                    }
                }
                calculatedMonthlySalary?.let { salary ->
                    Text(
                        text = "دستمزد ماهانه شما بر اساس حق بیمه انتخابی $salary ریال می‌باشد",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
