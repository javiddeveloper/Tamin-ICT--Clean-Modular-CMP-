package com.tamin.taminhamrah.feature.inquiryEducation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationIntent
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationUiState
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemPR
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.inquiry_education_code_counter
import taminx.core.core_ui.inquiry_education_code_helper
import taminx.core.core_ui.inquiry_education_code_label
import taminx.core.core_ui.inquiry_education_copy_address
import taminx.core.core_ui.inquiry_education_empty_sons
import taminx.core.core_ui.inquiry_education_info_body
import taminx.core.core_ui.inquiry_education_msrt_url_copy
import taminx.core.core_ui.inquiry_education_msrt_url_display
import taminx.core.core_ui.inquiry_education_national_id_label
import taminx.core.core_ui.inquiry_education_select_one
import taminx.core.core_ui.inquiry_education_son_section
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_number

private const val EDUCATION_CODE_LENGTH = 10

@Composable
fun InquiryEducationFormStep(
    state: InquiryEducationUiState,
    onIntent: (InquiryEducationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        InquiryEducationInfoSurface()

        when {
            state.sons.isNotEmpty() -> {
                SonSelectionSection(
                    sons = state.sons,
                    selectedNationalId = state.selectedNationalId,
                    sonSelectionError = state.sonSelectionError,
                    onSelectSon = { onIntent(InquiryEducationIntent.SelectSon(it)) },
                )
            }
            !state.isLoading -> {
                TaminEmptyState(stringResource(Res.string.inquiry_education_empty_sons))
            }
        }

        EducationCodeSection(
            educationCode = state.educationCode,
            educationCodeError = state.educationCodeError,
            onEducationCodeChanged = { onIntent(InquiryEducationIntent.EducationCodeChanged(it)) },
        )
    }
}

@Composable
private fun InquiryEducationInfoSurface(
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.listRow)
    val copyValue = stringResource(Res.string.inquiry_education_msrt_url_copy)
    val copy = rememberCopyAction(copyValue)
    val copyLabel = stringResource(Res.string.inquiry_education_copy_address)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.blueBg, shape)
            .border(Thickness.border, colors.blueText.copy(alpha = 0.2f), shape)
            .padding(Spacing.smd),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_info),
                contentDescription = null,
                tint = colors.blueText,
            )
            Text(
                text = stringResource(Res.string.inquiry_education_info_body),
                style = MaterialTheme.typography.bodySmall,
                color = colors.blueText,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.md))
                .clickable(onClick = copy)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = stringResource(Res.string.inquiry_education_msrt_url_display),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colors.blueText,
                modifier = Modifier.weight(1f),
            )
            CopyIconButton(
                value = copyValue,
                label = copyLabel,
                tint = colors.blueText,
                interactive = false,
            )
            Text(
                text = copyLabel,
                style = MaterialTheme.typography.labelMedium,
                color = colors.blueText,
            )
        }
    }
}

@Composable
private fun SonSelectionSection(
    sons: List<EducationDependentItemPR>,
    selectedNationalId: String?,
    sonSelectionError: String?,
    onSelectSon: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionLabel(text = stringResource(Res.string.inquiry_education_son_section))
            Text(
                text = stringResource(Res.string.inquiry_education_select_one),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
        }

        sons.forEach { son ->
            SonSelectionRow(
                son = son,
                selected = son.nationalId == selectedNationalId,
                onClick = { onSelectSon(son.nationalId) },
            )
        }

        sonSelectionError?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = colors.dangerText,
            )
        }
    }
}

@Composable
private fun SonSelectionRow(
    son: EducationDependentItemPR,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val borderColor = if (selected) colors.blueText else colors.border
    val shape = RoundedCornerShape(CornerRadius.lg)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color = colors.bgSurface, shape = shape)
            .border(
                BorderStroke(
                    width = if (selected) Thickness.medium else Thickness.border,
                    color = borderColor,
                ),
                shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = colors.blueText,
                unselectedColor = colors.border,
            ),
        )
        Spacer(modifier = Modifier.width(Spacing.xs))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminText(
                    text = son.fullName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                if (son.relationDescription.isNotBlank()) {
                    StatusPill(
                        text = son.relationDescription,
                        containerColor = colors.blueBg,
                        contentColor = colors.blueText,
                    )
                }
            }
            Text(
                text = stringResource(
                    Res.string.inquiry_education_national_id_label,
                    son.nationalId.toPersianDigits(),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
        }
    }
}

@Composable
private fun EducationCodeSection(
    educationCode: String,
    educationCodeError: String?,
    onEducationCodeChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = stringResource(Res.string.inquiry_education_code_label),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textMuted,
        )
        SegmentedInputField(
            value = educationCode,
            onValueChange = onEducationCodeChanged,
            slotCount = EDUCATION_CODE_LENGTH,
            leadingIcon = vectorResource(Res.drawable.ic_number),
            keyboardType = KeyboardType.Ascii,
            error = educationCodeError != null,
            errorMessage = educationCodeError,
            valueFilter = { raw ->
                raw.filter { char -> char.isLetterOrDigit() && char.code < 128 }
                    .take(EDUCATION_CODE_LENGTH)
            },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = stringResource(Res.string.inquiry_education_code_helper),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(
                    Res.string.inquiry_education_code_counter,
                    educationCode.length.toString().toPersianDigits(),
                ),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
        }
    }
}
