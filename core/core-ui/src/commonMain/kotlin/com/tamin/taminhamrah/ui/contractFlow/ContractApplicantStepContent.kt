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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.contractFlow.ContractApplicantType
import com.tamin.taminhamrah.model.contractFlow.GuardianFormPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_applicant_guardian
import taminx.core.core_ui.contract_applicant_personal
import taminx.core.core_ui.contract_guardian_full_name_label
import taminx.core.core_ui.contract_guardian_full_name_placeholder
import taminx.core.core_ui.contract_guardian_letter_date_label
import taminx.core.core_ui.contract_guardian_letter_date_placeholder
import taminx.core.core_ui.contract_guardian_letter_no_label
import taminx.core.core_ui.contract_guardian_letter_no_placeholder
import taminx.core.core_ui.contract_guardian_national_id_label
import taminx.core.core_ui.contract_guardian_national_id_placeholder
import taminx.core.core_ui.contract_guardian_upload_button
import taminx.core.core_ui.contract_guardian_upload_hint
import taminx.core.core_ui.contract_guardian_upload_success

@Composable
fun ContractApplicantStepContent(
    selectedType: ContractApplicantType,
    onTypeSelected: (ContractApplicantType) -> Unit,
    guardianForm: GuardianFormPR = GuardianFormPR(),
    onGuardianNationalIdChange: (String) -> Unit = {},
    onGuardianLetterNumberChange: (String) -> Unit = {},
    onGuardianFullNameChange: (String) -> Unit = {},
    onGuardianLetterDateChange: (formatted: String, epoch: Long) -> Unit = { _, _ -> },
    onGuardianImagePicked: (fileName: String, bytes: ByteArray) -> Unit = { _, _ -> },
    onClearGuardianDocument: () -> Unit = {},
    availableTypes: List<ContractApplicantType> = listOf(
        ContractApplicantType.PERSONAL,
        ContractApplicantType.GUARDIAN,
    ),
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    if (isLoading) {
        ContractApplicantStepShimmerSkeleton(modifier = modifier)
    } else {
        val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.x2l)
    val optionShape = RoundedCornerShape(CornerRadius.lg)
    val scope = rememberCoroutineScope()
    var showDatePicker by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { file: PlatformFile? ->
        if (file == null) return@rememberFilePickerLauncher
        scope.launch {
            try {
                val bytes = file.readBytes()
                onGuardianImagePicked(file.name, bytes)
            } catch (_: Exception) {
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, cardShape)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // Radio Options Cards
        availableTypes.forEach { type ->
            val isSelected = selectedType == type
            val borderColor = if (isSelected) colors.blueText else colors.border

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(optionShape)
                    .background(colors.bgSurface)
                    .border(
                        width = if (isSelected) Thickness.medium else Thickness.border,
                        color = borderColor,
                        shape = optionShape,
                    )
                    .selectable(
                        selected = isSelected,
                        onClick = { onTypeSelected(type) },
                        role = Role.RadioButton,
                    )
                    .padding(horizontal = Spacing.md, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = colors.blueText,
                        unselectedColor = colors.border,
                    ),
                )
                Text(
                    text = stringResource(type.labelRes),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = colors.textPrimary,
                )
            }
        }

        // Sub-form for Guardian (قیم‌نامه)
        AnimatedVisibility(
            visible = selectedType == ContractApplicantType.GUARDIAN,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // Row 1: Guardian National ID & Guardian Letter Number
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    OutlinedTextField(
                        value = guardianForm.nationalId,
                        onValueChange = onGuardianNationalIdChange,
                        label = { Text(stringResource(Res.string.contract_guardian_national_id_label)) },
                        placeholder = { Text(stringResource(Res.string.contract_guardian_national_id_placeholder)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(CornerRadius.lg),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.blueText,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            cursorColor = colors.blueText,
                            focusedContainerColor = colors.bgSurface,
                            unfocusedContainerColor = colors.bgSurface,
                        ),
                        modifier = Modifier.weight(1f),
                    )

                    OutlinedTextField(
                        value = guardianForm.letterNumber,
                        onValueChange = onGuardianLetterNumberChange,
                        label = { Text(stringResource(Res.string.contract_guardian_letter_no_label)) },
                        placeholder = { Text(stringResource(Res.string.contract_guardian_letter_no_placeholder)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(CornerRadius.lg),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.blueText,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary,
                            cursorColor = colors.blueText,
                            focusedContainerColor = colors.bgSurface,
                            unfocusedContainerColor = colors.bgSurface,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }

                // Row 2: Guardian Full Name
                OutlinedTextField(
                    value = guardianForm.fullName,
                    onValueChange = onGuardianFullNameChange,
                    label = { Text(stringResource(Res.string.contract_guardian_full_name_label)) },
                    placeholder = { Text(stringResource(Res.string.contract_guardian_full_name_placeholder)) },
                    singleLine = true,
                    shape = RoundedCornerShape(CornerRadius.lg),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.blueText,
                        unfocusedBorderColor = colors.border,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        cursorColor = colors.blueText,
                        focusedContainerColor = colors.bgSurface,
                        unfocusedContainerColor = colors.bgSurface,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                // Row 3: Guardian Letter Date & Document Upload Tile
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Date field
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showDatePicker = true },
                    ) {
                        OutlinedTextField(
                            value = guardianForm.letterDateFormatted.toPersianDigits(),
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text(stringResource(Res.string.contract_guardian_letter_date_label)) },
                            placeholder = { Text(stringResource(Res.string.contract_guardian_letter_date_placeholder)) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    tint = colors.textMuted,
                                    modifier = Modifier.size(IconSize.medium),
                                )
                            },
                            shape = RoundedCornerShape(CornerRadius.lg),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = colors.border,
                                disabledTextColor = colors.textPrimary,
                                disabledLabelColor = colors.textSecondary,
                                disabledPlaceholderColor = colors.textMuted,
                                disabledContainerColor = colors.bgSurface,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    // Document Upload Tile
                    val isDocumentUploaded = guardianForm.documentGuid != null || guardianForm.documentPreviewBytes != null
                    val uploadShape = RoundedCornerShape(CornerRadius.lg)

                    if (isDocumentUploaded) {
                        // Green uploaded tile
                        Row(
                            modifier = Modifier
                                .width(120.dp)
                                .height(56.dp)
                                .clip(uploadShape)
                                .background(colors.greenBg)
                                .border(Thickness.border, colors.greenText.copy(alpha = 0.35f), uploadShape)
                                .clickable { filePickerLauncher.launch() }
                                .padding(horizontal = Spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = colors.greenText,
                                modifier = Modifier.size(IconSize.small),
                            )
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Text(
                                text = stringResource(Res.string.contract_guardian_upload_success),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.greenText,
                            )
                        }
                    } else {
                        // Dashed upload button
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(56.dp)
                                .clip(uploadShape)
                                .background(colors.blueBg.copy(alpha = 0.35f))
                                .drawBehind {
                                    drawRoundRect(
                                        color = colors.blueText.copy(alpha = 0.40f),
                                        style = Stroke(
                                            width = Thickness.border.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                                        ),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(CornerRadius.lg.toPx()),
                                    )
                                }
                                .clickable(enabled = !guardianForm.isUploadingDocument) {
                                    filePickerLauncher.launch()
                                }
                                .padding(horizontal = Spacing.sm),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (guardianForm.isUploadingDocument) {
                                CircularProgressIndicator(
                                    color = colors.blueText,
                                    modifier = Modifier.size(IconSize.small),
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Add,
                                        contentDescription = null,
                                        tint = colors.blueText,
                                        modifier = Modifier.size(IconSize.small),
                                    )
                                    Text(
                                        text = stringResource(Res.string.contract_guardian_upload_button),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colors.blueText,
                                    )
                                }
                            }
                        }
                    }
                }

                // Helper Text
                Text(
                    text = stringResource(Res.string.contract_guardian_upload_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                    modifier = Modifier.padding(horizontal = Spacing.xs),
                )
            }
        }
    }

        if (showDatePicker) {
            TaminJalaliDatePicker(
                title = stringResource(Res.string.contract_guardian_letter_date_label),
                onDismiss = { showDatePicker = false },
                onConfirm = { year, month, day ->
                    val formatted = "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
                    val epoch = PersianDateFormatter.toEpochMillis(year, month, day)
                    onGuardianLetterDateChange(formatted, epoch)
                    showDatePicker = false
                },
            )
        }
    }
}

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

@PreviewRtlTheme
@Composable
private fun ContractApplicantStepContentPersonalPreview() {
    PreviewRtlThemeContent {
        ContractApplicantStepContent(
            selectedType = ContractApplicantType.PERSONAL,
            onTypeSelected = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractApplicantStepContentGuardianEmptyPreview() {
    PreviewRtlThemeContent {
        ContractApplicantStepContent(
            selectedType = ContractApplicantType.GUARDIAN,
            onTypeSelected = {},
            guardianForm = GuardianFormPR(),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractApplicantStepContentGuardianFilledPreview() {
    PreviewRtlThemeContent {
        ContractApplicantStepContent(
            selectedType = ContractApplicantType.GUARDIAN,
            onTypeSelected = {},
            guardianForm = GuardianFormPR(
                nationalId = "0012345678",
                letterNumber = "1234567890",
                fullName = "رضا نادری",
                letterDateFormatted = "1405/07/11",
                documentGuid = "sample-guid",
            ),
        )
    }
}
