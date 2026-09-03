package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_treatment_commitment
import taminx.core.core_ui.contract_treatment_dependents_empty
import taminx.core.core_ui.contract_treatment_dependents_sheet_subtitle
import taminx.core.core_ui.contract_treatment_dependents_sheet_title
import taminx.core.core_ui.contract_treatment_field_birth_date
import taminx.core.core_ui.contract_treatment_field_national_id
import taminx.core.core_ui.contract_treatment_not_want
import taminx.core.core_ui.contract_treatment_view_dependents
import taminx.core.core_ui.contract_treatment_want
import taminx.core.core_ui.ic_tamin_user

@Composable
fun TreatmentSupportStepContent(
    treatmentSupportCode: String,
    isCommitmentConfirmed: Boolean,
    forceTreatmentSupport: Boolean,
    dependents: List<SubdominantItemPR>,
    isDependentsLoading: Boolean,
    dependentsError: String?,
    onSelectWithSupport: () -> Unit,
    onSelectWithoutSupport: () -> Unit,
    onCommitmentChanged: (Boolean) -> Unit,
    onViewDependents: () -> Unit,
    isLoading: Boolean = false,
    hasLoadedDependents: Boolean = false,
    withSupportCode: String = "1",
    withoutSupportCode: String = "2",
    modifier: Modifier = Modifier,
) {
    if (isLoading) {
        TreatmentSupportStepShimmerSkeleton(modifier = modifier)
        return
    }

    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.x2l)
    val wantsSupport = treatmentSupportCode == withSupportCode
    var showDependentsSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, cardShape)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        TreatmentSupportOptionRow(
            label = stringResource(Res.string.contract_treatment_want),
            selected = wantsSupport,
            enabled = true,
            onClick = onSelectWithSupport,
        )

        TreatmentSupportOptionRow(
            label = stringResource(Res.string.contract_treatment_not_want),
            selected = !wantsSupport,
            enabled = !forceTreatmentSupport,
            onClick = onSelectWithoutSupport,
        )

        AnimatedVisibility(
            visible = wantsSupport,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            TreatmentCommitmentRow(
                checked = isCommitmentConfirmed,
                onCheckedChange = onCommitmentChanged,
            )
        }

        TaminFilledButton(
            text = stringResource(Res.string.contract_treatment_view_dependents),
            onClick = {
                onViewDependents()
                showDependentsSheet = true
            },
            painter = painterResource(Res.drawable.ic_tamin_user),
            background = Brush.horizontalGradient(
                listOf(colors.greenText, colors.greenText),
            ),
            shadowColor = colors.greenText.copy(alpha = 0.28f),
            shape = RoundedCornerShape(CornerRadius.xl),
            height = ButtonDimens.height,
        )
    }

    if (showDependentsSheet) {
        ContractDependentsBottomSheet(
            dependents = dependents,
            isLoading = isDependentsLoading || !hasLoadedDependents,
            error = dependentsError,
            onDismiss = { showDependentsSheet = false },
        )
    }
}

@Composable
private fun TreatmentSupportOptionRow(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val optionShape = RoundedCornerShape(CornerRadius.lg)
    val borderColor = when {
        selected -> colors.blueText
        else -> colors.border
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(optionShape)
            .background(colors.bgSurface)
            .border(
                width = if (selected) Thickness.medium else Thickness.border,
                color = borderColor,
                shape = optionShape,
            )
            .selectable(
                selected = selected,
                enabled = enabled,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = colors.blueText,
                unselectedColor = colors.border,
                disabledSelectedColor = colors.blueText.copy(alpha = 0.5f),
                disabledUnselectedColor = colors.border,
            ),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (enabled) colors.textPrimary else colors.textMuted,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TreatmentCommitmentRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.lg)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, shape)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = colors.blueText,
                uncheckedColor = colors.border,
                checkmarkColor = Color.White,
            ),
        )
        Text(
            text = stringResource(Res.string.contract_treatment_commitment),
            style = MaterialTheme.typography.bodySmall,
            color = if (checked) colors.textPrimary else colors.textMuted,
            modifier = Modifier.weight(1f),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractDependentsBottomSheet(
    dependents: List<SubdominantItemPR>,
    isLoading: Boolean,
    error: String?,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = stringResource(Res.string.contract_treatment_dependents_sheet_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(Res.string.contract_treatment_dependents_sheet_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                modifier = Modifier.fillMaxWidth(),
            )

            when {
                isLoading -> {
                    ContractDependentsSheetShimmerSkeleton()
                }

                error != null -> {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.dangerText,
                    )
                }

                dependents.isEmpty() -> {
                    Text(
                        text = stringResource(Res.string.contract_treatment_dependents_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textMuted,
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        items(dependents, key = { it.id }) { dependent ->
                            ContractDependentCard(dependent = dependent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContractDependentCard(
    dependent: SubdominantItemPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.xl)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, cardShape)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = dependent.fullName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (dependent.relationDescription.isNotBlank()) {
                StatusPill(
                    text = shortRelationLabel(dependent.relationDescription),
                    containerColor = colors.blueBg,
                    contentColor = colors.blueText,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            DependentInfoTile(
                label = stringResource(Res.string.contract_treatment_field_national_id),
                value = dependent.nationalCode.toPersianDigits(),
                numeric = true,
                modifier = Modifier.weight(1f),
            )
            DependentInfoTile(
                label = stringResource(Res.string.contract_treatment_field_birth_date),
                value = dependent.birthDateJalali.toPersianDigits(),
                numeric = false,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DependentInfoTile(
    label: String,
    value: String,
    numeric: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.card)

    Column(
        modifier = modifier
            .clip(shape)
            .background(colors.bgPage)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
        )
        if (numeric) {
            NumericText(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
        }
    }
}

/** Prefers the short chip label; falls back to the first segment of a long coverage sentence. */
private fun shortRelationLabel(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.length <= 24) return trimmed
    return trimmed
        .substringBefore(" - ")
        .substringBefore(" – ")
        .substringBefore("-")
        .trim()
        .ifBlank { trimmed }
}

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

private val MockDependents = listOf(
    SubdominantItemPR(
        id = 1L,
        fullName = "زهره محمدی",
        nationalCode = "0061777943",
        birthDateJalali = "1357/03/16",
        relationDescription = "همسر",
    ),
    SubdominantItemPR(
        id = 2L,
        fullName = "روناک موسوی",
        nationalCode = "0065421168",
        birthDateJalali = "1385/06/12",
        relationDescription = "فرزند دختر",
    ),
    SubdominantItemPR(
        id = 3L,
        fullName = "روژان موسوی",
        nationalCode = "0065422001",
        birthDateJalali = "1388/11/03",
        relationDescription = "تبعی - تحت پوشش بیمه شده اصلی - کفالت زن توسط شوهر - عقد دائم",
    ),
)

@PreviewRtlTheme
@Composable
private fun TreatmentSupportStepWithSupportPreview() {
    PreviewRtlThemeContent {
        TreatmentSupportStepContent(
            treatmentSupportCode = "1",
            isCommitmentConfirmed = false,
            forceTreatmentSupport = false,
            dependents = emptyList(),
            isDependentsLoading = false,
            dependentsError = null,
            onSelectWithSupport = {},
            onSelectWithoutSupport = {},
            onCommitmentChanged = {},
            onViewDependents = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TreatmentSupportStepWithSupportCommittedPreview() {
    PreviewRtlThemeContent {
        TreatmentSupportStepContent(
            treatmentSupportCode = "1",
            isCommitmentConfirmed = true,
            forceTreatmentSupport = false,
            dependents = emptyList(),
            isDependentsLoading = false,
            dependentsError = null,
            onSelectWithSupport = {},
            onSelectWithoutSupport = {},
            onCommitmentChanged = {},
            onViewDependents = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TreatmentSupportStepWithoutSupportPreview() {
    PreviewRtlThemeContent {
        TreatmentSupportStepContent(
            treatmentSupportCode = "2",
            isCommitmentConfirmed = false,
            forceTreatmentSupport = false,
            dependents = emptyList(),
            isDependentsLoading = false,
            dependentsError = null,
            onSelectWithSupport = {},
            onSelectWithoutSupport = {},
            onCommitmentChanged = {},
            onViewDependents = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TreatmentSupportStepForcedPreview() {
    PreviewRtlThemeContent {
        TreatmentSupportStepContent(
            treatmentSupportCode = "1",
            isCommitmentConfirmed = true,
            forceTreatmentSupport = true,
            dependents = emptyList(),
            isDependentsLoading = false,
            dependentsError = null,
            onSelectWithSupport = {},
            onSelectWithoutSupport = {},
            onCommitmentChanged = {},
            onViewDependents = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TreatmentSupportStepShimmerPreview() {
    PreviewRtlThemeContent {
        TreatmentSupportStepContent(
            treatmentSupportCode = "1",
            isCommitmentConfirmed = false,
            forceTreatmentSupport = false,
            dependents = emptyList(),
            isDependentsLoading = false,
            dependentsError = null,
            onSelectWithSupport = {},
            onSelectWithoutSupport = {},
            onCommitmentChanged = {},
            onViewDependents = {},
            isLoading = true,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractDependentCardPreview() {
    PreviewRtlThemeContent {
        Column(
            modifier = Modifier.padding(Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            MockDependents.forEach { ContractDependentCard(dependent = it) }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractDependentsSheetLoadingPreview() {
    PreviewRtlThemeContent {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = stringResource(Res.string.contract_treatment_dependents_sheet_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LocalTaminColors.current.textPrimary,
            )
            Text(
                text = stringResource(Res.string.contract_treatment_dependents_sheet_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = LocalTaminColors.current.textMuted,
            )
            ContractDependentsSheetShimmerSkeleton()
        }
    }
}
