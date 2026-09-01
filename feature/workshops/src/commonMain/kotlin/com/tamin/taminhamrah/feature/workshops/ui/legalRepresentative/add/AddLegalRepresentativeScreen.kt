package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeContractPickerSheet
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeHeader
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeOtpSection
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeWorkshopSummaryCard
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_workshop_badge
import taminx.core.core_ui.legal_representative_access_code_label
import taminx.core.core_ui.legal_representative_access_services_hint
import taminx.core.core_ui.legal_representative_add_heading
import taminx.core.core_ui.legal_representative_add_success_message
import taminx.core.core_ui.legal_representative_contracts_label
import taminx.core.core_ui.legal_representative_credit_code_label
import taminx.core.core_ui.legal_representative_edit_heading
import taminx.core.core_ui.legal_representative_edit_success_message
import taminx.core.core_ui.legal_representative_electronic_notification
import taminx.core.core_ui.legal_representative_insured_registration
import taminx.core.core_ui.legal_representative_internet_list
import taminx.core.core_ui.legal_representative_national_code_label
import taminx.core.core_ui.legal_representative_national_code_placeholder
import taminx.core.core_ui.legal_representative_otp_description_agent
import taminx.core.core_ui.legal_representative_otp_expired_message
import taminx.core.core_ui.legal_representative_otp_expired_title
import taminx.core.core_ui.legal_representative_otp_request_action
import taminx.core.core_ui.legal_representative_otp_retry_action
import taminx.core.core_ui.legal_representative_otp_sent_to_national_code
import taminx.core.core_ui.legal_representative_submit_action
import taminx.core.core_ui.legal_representative_success_confirm
import taminx.core.core_ui.legal_representative_success_title

@Composable
fun AddLegalRepresentativeScreen(
    workshopId: String,
    branchCode: String,
    workshopName: String,
    workshopSubtitle: String,
    isEditMode: Boolean,
    nationalCode: String,
    hasElectronicNotification: Boolean,
    hasInternetList: Boolean,
    hasInsuredRegistration: Boolean,
    special: Boolean,
    onBackClicked: () -> Unit,
    onSubmitted: () -> Unit,
    viewModel: AddLegalRepresentativeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode, isEditMode, nationalCode) {
        viewModel.sendIntent(
            AddLegalRepresentativeIntent.Init(
                workshopId = workshopId,
                branchCode = branchCode,
                isEditMode = isEditMode,
                nationalCode = nationalCode,
                hasElectronicNotification = hasElectronicNotification,
                hasInternetList = hasInternetList,
                hasInsuredRegistration = hasInsuredRegistration,
                special = special,
            )
        )
    }

    val taminColors = LocalTaminColors.current

    Column(modifier = Modifier.fillMaxSize()) {
        LegalRepresentativeHeader(onBackClicked = onBackClicked) {
            LegalRepresentativeWorkshopSummaryCard(workshopName = workshopName, subtitle = workshopSubtitle)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = stringResource(
                    if (uiState.isEditMode) Res.string.legal_representative_edit_heading
                    else Res.string.legal_representative_add_heading
                ),
                style = MaterialTheme.typography.titleMedium,
                color = taminColors.textPrimary,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .taminSurface()
                    .padding(Spacing.md),
            ) {
                TaminStyledTextField(
                    value = uiState.nationalCode,
                    onValueChange = {
                        viewModel.sendIntent(
                            AddLegalRepresentativeIntent.NationalCodeChanged(
                                it
                            )
                        )
                    },
                    label = stringResource(Res.string.legal_representative_national_code_label),
                    placeholder = stringResource(Res.string.legal_representative_national_code_placeholder),
                    leadingIcon = if (uiState.isEditMode) Icons.Outlined.Lock else null,
                    readOnly = uiState.isEditMode,
                    errorText = uiState.nationalCodeError,
                    isValid = uiState.nationalCodeError?.let { false },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    maxLength = 10,
                    textFieldBg = taminColors.blueBg
                )
            }

            Text(
                text = stringResource(Res.string.legal_representative_access_services_hint),
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textMuted,
            )

            LegalRepresentativeCheckboxRow(
                label = stringResource(Res.string.legal_representative_electronic_notification),
                checked = uiState.hasElectronicNotification,
                onCheckedChange = { viewModel.sendIntent(AddLegalRepresentativeIntent.ElectronicNotificationChanged(it)) },
            )
            LegalRepresentativeCheckboxRow(
                label = stringResource(Res.string.legal_representative_internet_list),
                checked = uiState.hasInternetList,
                onCheckedChange = { viewModel.sendIntent(AddLegalRepresentativeIntent.InternetListChanged(it)) },
            )
            LegalRepresentativeCheckboxRow(
                label = stringResource(Res.string.legal_representative_insured_registration),
                checked = uiState.hasInsuredRegistration,
                onCheckedChange = { viewModel.sendIntent(AddLegalRepresentativeIntent.InsuredRegistrationChanged(it)) },
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(Res.string.legal_representative_access_code_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = taminColors.textMuted,
                )
                NumericText(
                    text = uiState.accessCodePreview,
                    style = MaterialTheme.typography.labelSmall,
                    color = taminColors.textMuted,
                )
            }

            if (uiState.isSpecialWorkshop) {
                LegalRepresentativeContractsSummaryRow(
                    selectedContractRows = uiState.selectedContractRows,
                    onClick = { viewModel.sendIntent(AddLegalRepresentativeIntent.OpenContractPicker) },
                )
            }

            if (uiState.isTicketRequested) {
                LegalRepresentativeOtpSection(
                    isTicketRequested = true,
                    isRequestingTicket = uiState.isRequestingTicket,
                    otpCode = uiState.otpCode,
                    otpError = uiState.otpError,
                    sentToLabel = stringResource(Res.string.legal_representative_otp_sent_to_national_code, uiState.nationalCode),
                    requestLabel = stringResource(Res.string.legal_representative_otp_request_action),
                    onRequestTicket = { viewModel.sendIntent(AddLegalRepresentativeIntent.RequestTicket) },
                    onOtpChanged = { viewModel.sendIntent(AddLegalRepresentativeIntent.OtpChanged(it)) },
                    onExpired = { viewModel.sendIntent(AddLegalRepresentativeIntent.OtpExpired) },
                    titleLabel = stringResource(Res.string.legal_representative_credit_code_label),
                    showSentMessage = false,
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().taminSurface().padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Text(
                        text = stringResource(Res.string.legal_representative_otp_description_agent),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textMuted,
                    )
                    LegalRepresentativeOtpSection(
                        isTicketRequested = false,
                        isRequestingTicket = uiState.isRequestingTicket,
                        otpCode = uiState.otpCode,
                        otpError = uiState.otpError,
                        sentToLabel = null,
                        requestLabel = stringResource(Res.string.legal_representative_otp_request_action),
                        onRequestTicket = { viewModel.sendIntent(AddLegalRepresentativeIntent.RequestTicket) },
                        onOtpChanged = { viewModel.sendIntent(AddLegalRepresentativeIntent.OtpChanged(it)) },
                        requestButtonBackground = SolidColor(taminColors.blueBg),
                        requestButtonContentColor = taminColors.blueText,
                    )
                }
            }
        }

        TaminDivider()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(taminColors.bgPage)
                .padding(Spacing.lg),
        ) {
            LoadingButton(
                text = stringResource(Res.string.legal_representative_submit_action),
                onClick = { viewModel.sendIntent(AddLegalRepresentativeIntent.Submit) },
                isLoading = uiState.isSubmitting,
                enabled = uiState.canSubmit,
                icon = Icons.Filled.Check,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (uiState.isSuccess) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.legal_representative_success_title),
            description = stringResource(
                if (uiState.isEditMode) Res.string.legal_representative_edit_success_message
                else Res.string.legal_representative_add_success_message
            ),
            icon = vectorResource(Res.drawable.ic_tamin_check),
            iconTint = taminColors.teal,
            iconBackground = taminColors.greenBg,
            onDismissRequest = onSubmitted,
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.legal_representative_success_confirm),
                    onClick = onSubmitted,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
        )
    }

    if (uiState.isOtpExpired) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.legal_representative_otp_expired_title),
            description = stringResource(Res.string.legal_representative_otp_expired_message),
            icon = Icons.Default.AccessTime,
            iconTint = taminColors.dangerText,
            iconBackground = taminColors.dangerBorder,
            onDismissRequest = { viewModel.sendIntent(AddLegalRepresentativeIntent.DismissOtpExpiredDialog) },
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.legal_representative_otp_retry_action),
                    onClick = { viewModel.sendIntent(AddLegalRepresentativeIntent.DismissOtpExpiredDialog) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
        )
    }

    if (uiState.isContractPickerOpen) {
        LegalRepresentativeContractPickerSheet(
            contracts = uiState.availableContracts,
            selectedContractRows = uiState.selectedContractRows,
            isLoading = uiState.isLoadingContracts,
            onToggleContractRow = { viewModel.sendIntent(AddLegalRepresentativeIntent.ToggleContractRow(it)) },
            onConfirm = { viewModel.sendIntent(AddLegalRepresentativeIntent.DismissContractPicker) },
        )
    }
}

@Composable
private fun LegalRepresentativeContractsSummaryRow(
    selectedContractRows: ImmutableList<String>,
    onClick: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface(cornerRadius = CornerRadius.lg)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_workshop_badge),
                contentDescription = null,
                tint = taminColors.orangeText,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(CornerRadius.md))
                    .background(taminColors.orangeBg)
                    .padding(Spacing.xs),
            )
                Text(
                    text = stringResource(Res.string.legal_representative_contracts_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = taminColors.textMuted,
                )
                    NumericText(
                        text = selectedContractRows.joinToString(separator = " ، "),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary,
                    )
        }
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            contentDescription = null,
            tint = taminColors.textMuted,
            modifier = Modifier.size(IconSize.small),
        )
    }
}

@Composable
private fun LegalRepresentativeCheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.chip))
            .background(if (checked) taminColors.blueBg else taminColors.bgPage)
            .border(
                BorderStroke(1.dp, if (checked) taminColors.blueText.copy(0.3f) else taminColors.border),
                RoundedCornerShape(CornerRadius.chip)
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = taminColors.blueText,
                uncheckedColor = taminColors.border,
            ),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = taminColors.textPrimary,
        )
    }
}
