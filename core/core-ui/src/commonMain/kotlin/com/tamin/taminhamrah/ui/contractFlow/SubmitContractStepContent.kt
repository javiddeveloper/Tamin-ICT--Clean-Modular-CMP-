package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.contractFlow.ContractStep
import com.tamin.taminhamrah.model.contractFlow.FreelanceContractResultPR
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminCheckBox
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_edit_step
import taminx.core.core_ui.contract_submit_agreement
import taminx.core.core_ui.contract_submit_success
import taminx.core.core_ui.contract_summary_title
import taminx.core.core_ui.ic_tamin_check_circle
import taminx.core.core_ui.ic_tamin_edit

@Immutable
data class ContractSummaryRowPR(
    val step: ContractStep,
    val title: String,
    val value: String,
    val isEditable: Boolean = true,
)

@Composable
fun SubmitContractStepContent(
    summaryRows: List<ContractSummaryRowPR>,
    registrationInfo: RegistrationInfoPR,
    selectedPremiumRateDescription: String?,
    calculatedMonthlySalary: Long?,
    agreementContractLabel: String,
    isAgreementConfirmed: Boolean,
    isSubmitting: Boolean,
    submittedContract: FreelanceContractResultPR?,
    onAgreementConfirmedChange: (Boolean) -> Unit,
    onEditStep: (ContractStep) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.x2l)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, cardShape)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        TaminText(
            text = stringResource(Res.string.contract_summary_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            summaryRows.forEachIndexed { index, row ->
                ContractSummaryRow(
                    title = row.title,
                    value = row.value,
                    onEdit = if (row.isEditable) {
                        { onEditStep(row.step) }
                    } else {
                        null
                    },
                )
                if (index < summaryRows.lastIndex) {
                    HorizontalDivider(
                        thickness = Thickness.border,
                        color = colors.border,
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            TaminCheckBox(
                checked = isAgreementConfirmed,
                onCheckedChange = { if (!isSubmitting && submittedContract == null) onAgreementConfirmedChange(it) },
                enabled = !isSubmitting && submittedContract == null,
            )
            TaminText(
                text = buildAgreementText(
                    registrationInfo = registrationInfo,
                    premiumRateDescription = selectedPremiumRateDescription,
                    calculatedMonthlySalary = calculatedMonthlySalary,
                    agreementContractLabel = agreementContractLabel,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                modifier = Modifier.weight(1f),
            )
        }

        submittedContract?.let { result ->
            TaminText(
                text = stringResource(
                    Res.string.contract_submit_success,
                    result.contractNumber,
                    result.contractDate,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.greenText,
            )
        }
    }
}

@Composable
private fun ContractSummaryRow(
    title: String,
    value: String,
    onEdit: (() -> Unit)?,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_check_circle),
            contentDescription = null,
            tint = colors.greenText,
            modifier = Modifier.size(IconSize.medium),
        )
        TaminText(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        TaminText(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f),
        )
        if (onEdit != null) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_edit),
                contentDescription = stringResource(Res.string.contract_edit_step),
                tint = colors.blueText,
                modifier = Modifier
                    .size(IconSize.medium)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onEdit),
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

@PreviewRtlTheme
@Composable
private fun SubmitContractStepContentPreview() {
    PreviewRtlThemeContent {
        SubmitContractStepContent(
            summaryRows = listOf(
                ContractSummaryRowPR(
                    step = ContractStep.STEP_USER_INFO,
                    title = "اطلاعات کاربر",
                    value = "مشهد",
                ),
                ContractSummaryRowPR(
                    step = ContractStep.STEP_CONTRACT_APPLICANT,
                    title = "متقاضی قرارداد",
                    value = "شخص متقاضی",
                ),
                ContractSummaryRowPR(
                    step = ContractStep.STEP_SELECT_BRANCH,
                    title = "شعبه انتخابی",
                    value = "شعبه چناران",
                ),
                ContractSummaryRowPR(
                    step = ContractStep.STEP_UPLOAD_IMAGE,
                    title = "مدارک",
                    value = "مدرک",
                ),
                ContractSummaryRowPR(
                    step = ContractStep.STEP_TREATMENT_SUPPORT,
                    title = "حمایت درمانی",
                    value = "با حمایت درمان",
                ),
                ContractSummaryRowPR(
                    step = ContractStep.STEP_INSURANCE_PREMIUM,
                    title = "حق بیمه ماهانه",
                    value = "۲۲,۵۹۶,۰۰۰ ریال",
                ),
            ),
            registrationInfo = RegistrationInfoPR(
                fullName = "رضا دریکوند",
                nationalId = "۰۹۲۷۰۶۳۶۷۱",
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
            calculatedMonthlySalary = 71_661_840L,
            agreementContractLabel = "بیمه دانشجویی",
            isAgreementConfirmed = false,
            isSubmitting = false,
            submittedContract = null,
            onAgreementConfirmedChange = {},
            onEditStep = {},
        )
    }
}
