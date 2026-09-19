package com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui.steps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionRequestStepScaffold
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.InfoBanner
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionIntent
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionUiState
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTextArea
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.inspection_request_description_info_banner
import taminx.core.core_ui.inspection_request_description_label_objection
import taminx.core.core_ui.inspection_request_description_placeholder_objection
import taminx.core.core_ui.inspection_request_prev_step
import taminx.core.core_ui.inspection_request_source_chip_format
import taminx.core.core_ui.inspection_request_submit_objection_button

private const val MAX_DESCRIPTION_LENGTH = 600

@Composable
internal fun WorkshopRequestDescriptionStep(
    uiState: WorkshopInspectionUiState,
    onIntent: (WorkshopInspectionIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = uiState.requestDescription
    val prevStepText = stringResource(Res.string.inspection_request_prev_step)
    val colors = LocalTaminColors.current

    InspectionRequestStepScaffold(
        modifier = modifier,
        primaryText = stringResource(Res.string.inspection_request_submit_objection_button),
        primaryEnabled = uiState.isRequestStep3Valid && !uiState.isLoading,
        isPrimaryLoading = uiState.isLoading,
        onPrimaryClick = {
            val workshop = uiState.workshopInfo
            val request = SubmitInspectionRequestDN(
                brchCode = workshop.branchCode,
                endDate = workshop.endDateTimestamp ?: 0L,
                inspectionNumberOld = uiState.requestInspectionNo.orEmpty(),
                insuranceId = uiState.requestInsuranceNo.orEmpty(),
                insuranceJob = workshop.jobCode,
                requestDescription = description,
                startDate = workshop.startDateTimestamp ?: 0L,
                workshopAddress = workshop.workshopAddress,
                workshopManager = workshop.employerName,
                workshopName = workshop.workshopName,
                workshopNumber = workshop.workshopCode,
                workshopTel = workshop.workshopPhone,
            )
            onIntent(WorkshopInspectionIntent.SubmitRequest(request))
        },
        secondaryText = prevStepText,
        onSecondaryClick = onBack,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg),
        ) {
            Spacer(Modifier.height(Spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TaminText(
                    text = stringResource(Res.string.inspection_request_description_label_objection),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                if (!uiState.requestInspectionNo.isNullOrBlank()) {
                    CustomChip(
                        text = stringResource(
                            Res.string.inspection_request_source_chip_format,
                            uiState.requestInspectionNo.orEmpty().toPersianDigits(),
                        ),
                        containerColor = colors.blueBg,
                        textColor = colors.blueText,
                        border = BorderStroke(width = 1.dp, color = colors.blueBorder),
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            TaminTextArea(
                value = description,
                onValueChange = { onIntent(WorkshopInspectionIntent.UpdateRequestDescription(it)) },
                placeholder = stringResource(Res.string.inspection_request_description_placeholder_objection),
                label = stringResource(Res.string.inspection_request_description_label_objection),
                isRequired = true,
                maxLength = MAX_DESCRIPTION_LENGTH,
            )

            Spacer(Modifier.height(Spacing.xs))

            TaminText(
                text = "${
                    description.length.toString().toPersianDigits()
                }/${MAX_DESCRIPTION_LENGTH.toString().toPersianDigits()}",
                style = MaterialTheme.typography.labelSmall,
                color = LocalTaminColors.current.textMuted,
            )

            Spacer(Modifier.height(Spacing.md))

            BannerCard(
                type = BannerType.Info ,
                message = stringResource(Res.string.inspection_request_description_info_banner)
            )

            Spacer(Modifier.height(Spacing.lg))
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopRequestDescriptionStepPreview() {
    PreviewRtlThemeContent {
        WorkshopRequestDescriptionStep(
            uiState = WorkshopInspectionUiState(),
            onIntent = {},
            onBack = {},
        )
    }
}
