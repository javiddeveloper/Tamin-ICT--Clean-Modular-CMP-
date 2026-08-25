package com.tamin.taminhamrah.feature.taminServices.inspection.ui.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionIntent
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionUiState
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionRequestStepScaffold
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.InfoBanner
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTextArea
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.inspection_request_description_info_banner
import taminx.core.core_ui.inspection_request_description_label
import taminx.core.core_ui.inspection_request_description_label_objection
import taminx.core.core_ui.inspection_request_description_placeholder
import taminx.core.core_ui.inspection_request_description_placeholder_objection
import taminx.core.core_ui.inspection_request_prev_step
import taminx.core.core_ui.inspection_request_step3_label
import taminx.core.core_ui.inspection_request_submit_button
import taminx.core.core_ui.inspection_request_submit_objection_button

private const val MAX_DESCRIPTION_LENGTH = 600

@Composable
internal fun RequestDescriptionStep(
    uiState: InspectionUiState,
    onIntent: (InspectionIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = uiState.requestDescription
    val prevStepText = stringResource(Res.string.inspection_request_prev_step)

    InspectionRequestStepScaffold(
        modifier = modifier,
        primaryText = stringResource(
            if (uiState.isObjectionRequest) Res.string.inspection_request_submit_objection_button
            else Res.string.inspection_request_submit_button
        ),
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
            onIntent(InspectionIntent.SubmitRequest(request))
        },
        secondaryText = prevStepText,
        onSecondaryClick = onBack,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg),
        ) {
            Spacer(Modifier.height(Spacing.md))

            TaminText(
                text = stringResource(Res.string.inspection_request_step3_label),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )

            Spacer(Modifier.height(Spacing.xl))

            TaminTextArea(
                value = description,
                onValueChange = { onIntent(InspectionIntent.UpdateRequestDescription(it)) },
                placeholder = stringResource(
                    if (uiState.isObjectionRequest) Res.string.inspection_request_description_placeholder_objection
                    else Res.string.inspection_request_description_placeholder
                ),
                label = stringResource(
                    if (uiState.isObjectionRequest) Res.string.inspection_request_description_label_objection
                    else Res.string.inspection_request_description_label
                ),
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

            InfoBanner(message = stringResource(Res.string.inspection_request_description_info_banner))

            Spacer(Modifier.height(Spacing.lg))
            Spacer(Modifier.height(padding.calculateBottomPadding()))
        }
    }
}

@PreviewRtlTheme
@Composable
private fun RequestDescriptionStepPreview() {
    PreviewRtlThemeContent {
        RequestDescriptionStep(
            uiState = InspectionUiState(isObjectionRequest = true),
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun RequestDescriptionStepEmptyPreview() {
    PreviewRtlThemeContent {
        RequestDescriptionStep(uiState = InspectionUiState(), onIntent = {}, onBack = {})
    }
}
