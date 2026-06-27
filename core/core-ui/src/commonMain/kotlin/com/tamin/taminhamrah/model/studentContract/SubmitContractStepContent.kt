package com.tamin.taminhamrah.model.studentContract

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR

@Composable
fun SubmitContractStepContent(
    registrationInfo: RegistrationInfoPR,
    selectedPremiumRateDescription: String?,
    calculatedMonthlySalary: Long?,
    agreementContractLabel: String,
    isAgreementConfirmed: Boolean,
    isSubmitting: Boolean,
    submittedContract: FreelanceContractResultPR?,
    onAgreementConfirmedChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    canSubmit: Boolean = true,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Checkbox(
                checked = isAgreementConfirmed,
                onCheckedChange = onAgreementConfirmedChange,
            )
            Text(
                text = buildAgreementText(
                    registrationInfo = registrationInfo,
                    premiumRateDescription = selectedPremiumRateDescription,
                    calculatedMonthlySalary = calculatedMonthlySalary,
                    agreementContractLabel = agreementContractLabel,
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Button(
            onClick = onSubmit,
            enabled = canSubmit && isAgreementConfirmed && !isSubmitting && submittedContract == null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator()
            } else {
                Text("انعقاد قرارداد")
            }
        }
        submittedContract?.let { result ->
            Text(
                text = "قرارداد با شماره ${result.contractNumber} در تاریخ ${result.contractDate} ثبت شد.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

private fun buildAgreementText(
    registrationInfo: RegistrationInfoPR,
    premiumRateDescription: String?,
    calculatedMonthlySalary: Long?,
    agreementContractLabel: String,
): String {
    val rate = premiumRateDescription.orEmpty()
    val salary = calculatedMonthlySalary?.toString().orEmpty()
    return "اینجانب ${registrationInfo.fullName} به شماره ملی ${registrationInfo.nationalId} " +
        "درخواست انعقاد قرارداد $agreementContractLabel $rate با دستمزد مبنا $salary ریال را دارم."
}
