package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoPR
import com.tamin.taminhamrah.model.personal.DisabilityPersonalPR
import com.tamin.taminhamrah.model.personal.DisabilityWorkPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminCheckbox1
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.disability_pension_identity_field_placeholder
import taminx.core.core_ui.disability_pension_workshop_activity_label
import taminx.core.core_ui.disability_pension_workshop_address_error
import taminx.core.core_ui.disability_pension_workshop_address_label
import taminx.core.core_ui.disability_pension_workshop_branch_code
import taminx.core.core_ui.disability_pension_workshop_branch_name
import taminx.core.core_ui.disability_pension_workshop_confirm_error
import taminx.core.core_ui.disability_pension_workshop_confirm_label
import taminx.core.core_ui.disability_pension_workshop_employer_label
import taminx.core.core_ui.disability_pension_workshop_name_error
import taminx.core.core_ui.disability_pension_workshop_name_label
import taminx.core.core_ui.disability_pension_workshop_number
import taminx.core.core_ui.disability_pension_workshop_province

@Composable
fun DisabilityPensionWorkshopStep(
    state: DisabilityPensionUiState,
    onIntent: (DisabilityPensionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val info = state.identityInfo

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        if (info == null) {
            DisabilityPensionInfoGridSkeleton()
        } else {
            WorkshopInfoGrid(
                tiles = listOf(
                    stringResource(Res.string.disability_pension_workshop_province) to
                        info.provinceName.ifBlank { "-" },
                    stringResource(Res.string.disability_pension_workshop_branch_code) to
                        info.branch.ifBlank { "-" },
                    stringResource(Res.string.disability_pension_workshop_branch_name) to
                        info.branchName.ifBlank { "-" },
                    stringResource(Res.string.disability_pension_workshop_number) to
                        (info.work?.workshopId?.ifBlank { "-" } ?: "-"),
                ),
            )
        }

        val fieldPlaceholder = stringResource(Res.string.disability_pension_identity_field_placeholder)

        TaminStyledTextField(
            value = state.workshopName,
            onValueChange = { onIntent(DisabilityPensionIntent.WorkshopNameChanged(it)) },
            label = stringResource(Res.string.disability_pension_workshop_name_label),
            placeholder = fieldPlaceholder,
            isRequired = true,
            isValid = if (state.workshopNameError) false else null,
            errorText = stringResource(Res.string.disability_pension_workshop_name_error),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
        ) {
            TaminStyledTextField(
                value = state.employerName,
                onValueChange = { onIntent(DisabilityPensionIntent.EmployerNameChanged(it)) },
                label = stringResource(Res.string.disability_pension_workshop_employer_label),
                placeholder = fieldPlaceholder,
                modifier = Modifier.weight(1f),
            )
            TaminStyledTextField(
                value = state.activityType,
                onValueChange = { onIntent(DisabilityPensionIntent.ActivityTypeChanged(it)) },
                label = stringResource(Res.string.disability_pension_workshop_activity_label),
                placeholder = fieldPlaceholder,
                modifier = Modifier.weight(1f),
            )
        }

        TaminStyledTextField(
            value = state.workshopAddress,
            onValueChange = { onIntent(DisabilityPensionIntent.WorkshopAddressChanged(it)) },
            label = stringResource(Res.string.disability_pension_workshop_address_label),
            placeholder = fieldPlaceholder,
            isRequired = true,
            isValid = if (state.workshopAddressError) false else null,
            errorText = stringResource(Res.string.disability_pension_workshop_address_error),
        )

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onIntent(DisabilityPensionIntent.WorkshopConfirmedChanged(!state.isWorkshopConfirmed)) },
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminCheckbox1(checked = state.isWorkshopConfirmed)
                Text(
                    text = stringResource(Res.string.disability_pension_workshop_confirm_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
            }

            if (state.showWorkshopConfirmationError) {
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
                        text = stringResource(Res.string.disability_pension_workshop_confirm_error),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.dangerText,
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkshopInfoGrid(
    tiles: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        tiles.chunked(2).forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                rowTiles.forEach { (label, value) ->
                    WorkshopInfoTile(label = label, value = value, modifier = Modifier.weight(1f))
                }
                if (rowTiles.size == 1) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun WorkshopInfoTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .background(colors.bgPage, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = Spacing.smd, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun DisabilityPensionWorkshopStepPreview() {
    PreviewRtlThemeContent {
        DisabilityPensionWorkshopStep(
            state = DisabilityPensionUiState(
                identityInfo = DisabilityPersonalInfoPR(
                    branch = "5802",
                    branchName = "یک مشهد",
                    confirmed = true,
                    insuranceId = "12345678",
                    mobileNumber = "09123456789",
                    personal = DisabilityPersonalPR(
                        firstName = "علی",
                        lastName = "علوی",
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
                workshopName = "شرکت صنایع دما بخار مشهد",
                workshopAddress = "مشهد، شهرک صنعتی توس",
            ),
            onIntent = {},
        )
    }
}
