package com.tamin.taminhamrah.ui.contractFlow

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
import com.tamin.taminhamrah.model.contractFlow.FreelanceContractResultPR
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_flow_submit_contract
import taminx.core.core_ui.contract_submit_agreement
import taminx.core.core_ui.contract_submit_success

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
                Text(stringResource(Res.string.contract_flow_submit_contract))
            }
        }
        submittedContract?.let { result ->
            Text(
                text = stringResource(
                    Res.string.contract_submit_success,
                    result.contractNumber,
                    result.contractDate,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun buildAgreementText(
    registrationInfo: RegistrationInfoPR,
    premiumRateDescription: String?,
    calculatedMonthlySalary: Long?,
    agreementContractLabel: String,
): String {
    val rate = premiumRateDescription.orEmpty()
    val salary = calculatedMonthlySalary?.toPriceFormat()?.toPersianDigits().orEmpty()
    return stringResource(
        Res.string.contract_submit_agreement,
        registrationInfo.fullName,
        registrationInfo.nationalId,
        agreementContractLabel,
        rate,
        salary,
    )
}

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun SubmitContractStepContentPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        SubmitContractStepContent(
            registrationInfo = RegistrationInfoPR(
                fullName = "علی محمدی",
                nationalId = "0012345678",
                birthDateFormatted = "1375/04/15",
                insuranceId = "12345678",
                genderCode = "01",
                address = "تهران",
                zipCode = "1234567890",
                phoneNumber = "02166001234",
                mobileNumber = "09121234567",
                hasMobile = true,
            ),
            selectedPremiumRateDescription = "نرخ ۱۴ درصد (بازنشستگی و فوت قبل و بعد از بازنشستگی)",
            calculatedMonthlySalary = 25000000L,
            agreementContractLabel = "بیمه دانشجویی",
            isAgreementConfirmed = true,
            isSubmitting = false,
            submittedContract = null,
            onAgreementConfirmedChange = {},
            onSubmit = {},
        )
    }
}
