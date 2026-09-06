package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityDocumentChecklist
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityDocumentState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoPR
import com.tamin.taminhamrah.model.personal.DisabilityPersonalPR
import com.tamin.taminhamrah.model.personal.DisabilityWorkPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.TaminCheckbox
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.disability_pension_summary_commission_row_title
import taminx.core.core_ui.disability_pension_summary_commission_value_no_objection
import taminx.core.core_ui.disability_pension_summary_commission_value_objection
import taminx.core.core_ui.disability_pension_summary_confirm_error
import taminx.core.core_ui.disability_pension_summary_confirm_label
import taminx.core.core_ui.disability_pension_summary_dependents_row_title
import taminx.core.core_ui.disability_pension_summary_dependents_value
import taminx.core.core_ui.disability_pension_summary_documents_row_title
import taminx.core.core_ui.disability_pension_summary_documents_value
import taminx.core.core_ui.disability_pension_summary_edit_content_description
import taminx.core.core_ui.disability_pension_summary_identity_row_title
import taminx.core.core_ui.disability_pension_summary_section_title
import taminx.core.core_ui.disability_pension_summary_terms_row_title
import taminx.core.core_ui.disability_pension_summary_terms_value
import taminx.core.core_ui.disability_pension_summary_workshop_row_title
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_edit

@Composable
fun DisabilityPensionSummaryStep(
    state: DisabilityPensionUiState,
    onIntent: (DisabilityPensionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val uploadedDocumentsCount = state.documents.values.count { it is DisabilityDocumentState.Uploaded }
    val hasObjection = state.hasCommissionObjection == true

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.smd),
    ) {
        SectionLabel(text = stringResource(Res.string.disability_pension_summary_section_title))

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            SummaryReviewRow(
                title = stringResource(Res.string.disability_pension_summary_terms_row_title),
                value = stringResource(Res.string.disability_pension_summary_terms_value),
                onClick = { onIntent(DisabilityPensionIntent.EditSummarySectionClicked(DisabilityPensionStep.Terms)) },
            )
            SummaryReviewRow(
                title = stringResource(Res.string.disability_pension_summary_dependents_row_title),
                value = stringResource(
                    Res.string.disability_pension_summary_dependents_value,
                    state.dependents.size.toString().toPersianDigits(),
                ),
                onClick = { onIntent(DisabilityPensionIntent.EditSummarySectionClicked(DisabilityPensionStep.Dependents)) },
            )
            SummaryReviewRow(
                title = stringResource(Res.string.disability_pension_summary_identity_row_title),
                value = state.landlinePhone,
                numericValue = true,
                onClick = {
                    onIntent(DisabilityPensionIntent.EditSummarySectionClicked(DisabilityPensionStep.IdentityContact))
                },
            )
            SummaryReviewRow(
                title = stringResource(Res.string.disability_pension_summary_workshop_row_title),
                value = state.workshopName.ifBlank { state.employerName }.ifBlank { "-" },
                onClick = { onIntent(DisabilityPensionIntent.EditSummarySectionClicked(DisabilityPensionStep.Workshop)) },
            )
            SummaryReviewRow(
                title = stringResource(Res.string.disability_pension_summary_commission_row_title),
                value = stringResource(
                    if (hasObjection) {
                        Res.string.disability_pension_summary_commission_value_objection
                    } else {
                        Res.string.disability_pension_summary_commission_value_no_objection
                    },
                ),
                onClick = {
                    onIntent(DisabilityPensionIntent.EditSummarySectionClicked(DisabilityPensionStep.CommissionRecord))
                },
            )
            SummaryReviewRow(
                title = stringResource(Res.string.disability_pension_summary_documents_row_title),
                value = stringResource(
                    Res.string.disability_pension_summary_documents_value,
                    uploadedDocumentsCount.toString().toPersianDigits(),
                    DisabilityDocumentChecklist.size.toString().toPersianDigits(),
                ),
                onClick = { onIntent(DisabilityPensionIntent.EditSummarySectionClicked(DisabilityPensionStep.Documents)) },
            )
        }

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.xs)
                    .clickable { onIntent(DisabilityPensionIntent.FinalConfirmedChanged(!state.isFinalConfirmed)) },
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminCheckbox(checked = state.isFinalConfirmed)
                Text(
                    text = stringResource(Res.string.disability_pension_summary_confirm_label, state.applicantFullName),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
            }

            if (state.showFinalConfirmationError) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = colors.dangerText,
                        modifier = Modifier.size(IconSize.small),
                    )
                    Text(
                        text = stringResource(Res.string.disability_pension_summary_confirm_error),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.dangerText,
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryReviewRow(
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    numericValue: Boolean = false,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgPage)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.smd, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {

        Box(
            modifier = Modifier
                .size(IconSize.medium - Spacing.xs)
                .clip(RoundedCornerShape(CornerRadius.md))
                .background(colors.greenText),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IconSize.statIcon + Spacing.xxs),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        val valueStyle = MaterialTheme.typography.labelSmall
        if (numericValue) {
            NumericText(
                text = value.ifBlank { "-" },
                style = valueStyle,
                color = colors.textMuted,
            )
        } else {
            Text(
                text = value,
                style = valueStyle,
                color = colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 130.dp),
            )
        }
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_edit),
            contentDescription = stringResource(Res.string.disability_pension_summary_edit_content_description),
            tint = colors.blueText,
            modifier = Modifier.size(IconSize.small),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun DisabilityPensionSummaryStepPreview() {
    PreviewRtlThemeContent {
        DisabilityPensionSummaryStep(
            state = DisabilityPensionUiState(
                applicantFullName = "رضا دریکوند",
                landlinePhone = "05832245678",
                employerName = "شرکت صنایع دما بخار مشهد",
                workshopName = "شرکت صنایع دما بخار مشهد",
                hasCommissionObjection = false,
                identityInfo = DisabilityPersonalInfoPR(
                    branch = "5802",
                    branchName = "یک مشهد",
                    confirmed = true,
                    insuranceId = "12345678",
                    mobileNumber = "09123456789",
                    personal = DisabilityPersonalPR(
                        firstName = "رضا",
                        lastName = "دریکوند",
                        nationalId = "0012345678",
                        fatherName = "محمد",
                        idCardNumber = "123",
                        cityOfIssue = "تهران",
                        dateOfBirth = "1370/01/01",
                        genderDesc = "مرد",
                    ),
                    provinceName = "خراسان رضوی",
                    work = DisabilityWorkPR(jobDescription = "", workshopId = "0081631829"),
                    yearsAge = "33",
                    monthsAge = "5",
                    daysAge = "10",
                    strAge = "33 سال",
                ),
                documents = persistentMapOf<String, DisabilityDocumentState>().toPersistentMap(),
            ),
            onIntent = {},
        )
    }
}
