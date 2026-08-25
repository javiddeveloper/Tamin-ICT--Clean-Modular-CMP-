package com.tamin.taminhamrah.feature.pensionSurvivor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.DeceasedDocumentChecklist
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.DeceasedDocumentType
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.DeceasedUploadedDocument
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoPR
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminImageViewer
import com.tamin.taminhamrah.ui.components.document.TaminDocumentSourceSheet
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadCard
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadState
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.rememberCameraPermission
import com.tamin.taminhamrah.util.toJalaliDateLabel
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.amount_unknown
import taminx.core.core_ui.btn_close
import taminx.core.core_ui.error_file_read_fallback
import taminx.core.core_ui.girl_survivor_label_birth_date
import taminx.core.core_ui.girl_survivor_label_father_name
import taminx.core.core_ui.girl_survivor_label_insurance_id
import taminx.core.core_ui.ic_number
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.identity_field_id_number
import taminx.core.core_ui.inquiry_national_id_label
import taminx.core.core_ui.occurrence_camera_permission_denied
import taminx.core.core_ui.orotez_protez_document_pick_placeholder
import taminx.core.core_ui.orotez_protez_document_status_error_tap_to_retry
import taminx.core.core_ui.orotez_protez_document_status_uploaded
import taminx.core.core_ui.pension_survivor_deceased_age_full
import taminx.core.core_ui.pension_survivor_deceased_branch_code
import taminx.core.core_ui.pension_survivor_deceased_branch_name
import taminx.core.core_ui.pension_survivor_deceased_death_certificate
import taminx.core.core_ui.pension_survivor_deceased_death_date_value
import taminx.core.core_ui.pension_survivor_deceased_documents_count
import taminx.core.core_ui.pension_survivor_deceased_documents_upload_hint
import taminx.core.core_ui.pension_survivor_deceased_documents_upload_title
import taminx.core.core_ui.pension_survivor_deceased_history_confirm_label
import taminx.core.core_ui.pension_survivor_deceased_id_children_page
import taminx.core.core_ui.pension_survivor_deceased_id_first_page
import taminx.core.core_ui.pension_survivor_deceased_more_details
import taminx.core.core_ui.pension_survivor_deceased_previous_pension
import taminx.core.core_ui.verify_issue_place_label
import taminx.core.core_ui.verify_label_age
import taminx.core.core_ui.verify_label_gender
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
fun DeceasedStep(
    state: PensionSurvivorUiState,
    onIntent: (PensionSurvivorIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        if (state.deceasedInfo == null) {
            DeceasedInquirySection(
                nationalId = state.deceasedNationalId,
                onIntent = onIntent,
            )
        } else {
            DeceasedSummaryCard(info = state.deceasedInfo)
            DeceasedHistoryConfirmCard(
                confirmed = state.isDeceasedHistoryConfirmed,
                onConfirmedChange = {
                    onIntent(PensionSurvivorIntent.DeceasedHistoryConfirmedChanged(it))
                },
            )
            DeceasedDocumentsSection(
                state = state,
                onIntent = onIntent,
            )
        }
    }
}

@Composable
private fun DeceasedInquirySection(
    nationalId: String,
    onIntent: (PensionSurvivorIntent) -> Unit,
) {
    val colors = LocalTaminColors.current

    Text(
        text = stringResource(Res.string.inquiry_national_id_label),
        style = MaterialTheme.typography.bodyMedium,
        color = colors.textMuted,
    )

    SegmentedInputField(
        value = nationalId,
        onValueChange = { onIntent(PensionSurvivorIntent.DeceasedNationalIdChanged(it)) },
        slotCount = NATIONAL_ID_LENGTH,
        leadingIcon = vectorResource(Res.drawable.ic_number),
        showClearButton = true,
    )
}

@Composable
private fun DeceasedSummaryCard(
    info: DeceasedInfoPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var expanded by remember(info.personal?.nationalId) { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(if (expanded) 180f else 0f)
    val fullName = listOfNotNull(
        info.personal?.firstName?.takeIf(String::isNotBlank),
        info.personal?.lastName?.takeIf(String::isNotBlank),
    ).joinToString(" ").ifBlank { stringResource(Res.string.amount_unknown) }
    val deathDateLabel = stringResource(
        Res.string.pension_survivor_deceased_death_date_value,
        info.deadDate?.takeIf(String::isNotBlank)?.toJalaliDateLabel()
            ?: stringResource(Res.string.amount_unknown),
    )
    val ageValue = formatDeceasedAge(info)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.card)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(IconSize.xlarge)
                    .border(Thickness.medium, colors.blueText, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_user),
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier.size(IconSize.medium),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                )
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(
                    text = deathDateLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            DeceasedFieldRow(
                left = DeceasedField(
                    label = stringResource(Res.string.girl_survivor_label_insurance_id),
                    value = info.insuranceId.orUnknown(),
                ),
                right = DeceasedField(
                    label = stringResource(Res.string.girl_survivor_label_father_name),
                    value = info.personal?.fatherName.orUnknown(),
                ),
            )

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    DeceasedFieldRow(
                        left = DeceasedField(
                            label = stringResource(Res.string.verify_label_gender),
                            value = info.personal?.gender.orUnknown(),
                        ),
                        right = DeceasedField(
                            label = stringResource(Res.string.identity_field_id_number),
                            value = info.personal?.idCardNumber.orUnknown(),
                        ),
                    )
                    DeceasedFieldRow(
                        left = DeceasedField(
                            label = stringResource(Res.string.verify_issue_place_label),
                            value = info.personal?.cityOfIssueDesc.orUnknown(),
                        ),
                        right = DeceasedField(
                            label = stringResource(Res.string.girl_survivor_label_birth_date),
                            value = PersianDateFormatter.formatTimestamp(info.personal?.dateOfBirth)
                                .ifBlank { stringResource(Res.string.amount_unknown) },
                        ),
                    )
                    DeceasedFieldRow(
                        left = DeceasedField(
                            label = stringResource(Res.string.pension_survivor_deceased_branch_name),
                            value = info.branchName.orUnknown(),
                        ),
                        right = DeceasedField(
                            label = stringResource(Res.string.pension_survivor_deceased_branch_code),
                            value = info.branchCode.orUnknown(),
                        ),
                    )
                }
            }

            DeceasedFieldTile(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(Res.string.verify_label_age),
                value = ageValue,
            )

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                DeceasedFieldTile(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(Res.string.pension_survivor_deceased_previous_pension),
                    value = info.pensionerId.orUnknown(),
                )
            }
        }

        TaminDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.md))
                .clickable { expanded = !expanded }
                .padding(vertical = Spacing.xs),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(
                    if (expanded) Res.string.btn_close
                    else Res.string.pension_survivor_deceased_more_details,
                ),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.blueText,
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier
                    .size(IconSize.medium)
                    .rotate(chevronRotation),
            )
        }
    }
}

@Composable
private fun DeceasedFieldRow(
    left: DeceasedField,
    right: DeceasedField,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        DeceasedFieldTile(
            modifier = Modifier.weight(1f),
            label = right.label,
            value = right.value,
        )
        DeceasedFieldTile(
            modifier = Modifier.weight(1f),
            label = left.label,
            value = left.value,
        )
    }
}

@Composable
private fun DeceasedFieldTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .background(colors.bgPage, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary,
        )
    }
}

private data class DeceasedField(
    val label: String,
    val value: String,
)

@Composable
private fun DeceasedHistoryConfirmCard(
    confirmed: Boolean,
    onConfirmedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.card)
            .clickable { onConfirmedChange(!confirmed) }
            .padding(Spacing.smd),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = confirmed,
            onCheckedChange = onConfirmedChange,
        )
        Text(
            text = stringResource(Res.string.pension_survivor_deceased_history_confirm_label),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DeceasedDocumentsSection(
    state: PensionSurvivorUiState,
    onIntent: (PensionSurvivorIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val toaster = LocalToaster.current
    val scope = rememberCoroutineScope()
    val cameraPermission = rememberCameraPermission()
    var previewDocument by remember { mutableStateOf<DeceasedUploadedDocument?>(null) }
    var pendingDocumentType by remember { mutableStateOf<DeceasedDocumentType?>(null) }

    fun handlePicked(file: PlatformFile?) {
        val documentType = pendingDocumentType ?: state.activeDeceasedDocument ?: return
        pendingDocumentType = null
        onIntent(PensionSurvivorIntent.DismissDeceasedDocumentSource)
        if (file == null) return

        scope.launch {
            try {
                val bytes = file.readBytes()
                onIntent(
                    PensionSurvivorIntent.DeceasedDocumentImagePicked(
                        type = documentType,
                        fileName = file.name,
                        bytes = bytes,
                    ),
                )
            } catch (e: Exception) {
                toaster.error(getString(Res.string.error_file_read_fallback))
            }
        }
    }

    val galleryLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        handlePicked(file)
    }
    val cameraLauncher = rememberCameraPickerLauncher { file ->
        handlePicked(file)
    }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.pension_survivor_deceased_documents_upload_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            StatusPill(
                text = stringResource(
                    Res.string.pension_survivor_deceased_documents_count,
                    state.deceasedDocumentsUploadedCount.toString(),
                ),
                containerColor = if (state.areDeceasedDocumentsComplete) colors.greenBg else colors.orangeBg,
                contentColor = if (state.areDeceasedDocumentsComplete) colors.greenText else colors.orangeText,
            )
        }

        Text(
            text = stringResource(Res.string.pension_survivor_deceased_documents_upload_hint),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )

        DeceasedDocumentChecklist.forEach { documentType ->
            val uploaded = state.deceasedDocuments[documentType]
            val uploadState = when {
                state.uploadingDeceasedDocument == documentType -> TaminDocumentUploadState.Uploading
                uploaded != null -> TaminDocumentUploadState.Uploaded
                state.failedDeceasedDocument == documentType -> TaminDocumentUploadState.Failed
                else -> TaminDocumentUploadState.Empty
            }
            val thumbnailBase64 = rememberBase64Thumbnail(uploaded?.bytes)
            val statusText = when (uploadState) {
                TaminDocumentUploadState.Uploaded -> uploaded?.fileName
                    ?: stringResource(Res.string.orotez_protez_document_status_uploaded)
                TaminDocumentUploadState.Failed -> state.deceasedDocumentError
                    ?: stringResource(Res.string.orotez_protez_document_status_error_tap_to_retry)
                else -> stringResource(Res.string.orotez_protez_document_pick_placeholder)
            }

            TaminDocumentUploadCard(
                title = deceasedDocumentTitle(documentType),
                state = uploadState,
                statusText = statusText,
                isRequired = true,
                thumbnailBase64 = thumbnailBase64,
                onCardClick = {
                    if (!state.isDeceasedDocumentUploading) {
                        onIntent(PensionSurvivorIntent.DeceasedDocumentClicked(documentType))
                    }
                },
                onPreviewClick = uploaded?.let { { previewDocument = it } },
                onDeleteClick = uploaded?.let {
                    { onIntent(PensionSurvivorIntent.RemoveDeceasedDocument(documentType)) }
                },
            )
        }

        state.deceasedDocumentError?.takeIf { state.failedDeceasedDocument == null }?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = colors.dangerText,
            )
        }
    }

    val preview = previewDocument
    val previewBase64 = rememberBase64Thumbnail(preview?.bytes)
    if (preview != null && previewBase64 != null) {
        TaminImageViewer(
            title = deceasedDocumentTitle(preview.type),
            url = previewBase64,
            onDismiss = { previewDocument = null },
        )
    }

    state.activeDeceasedDocument?.let { documentType ->
        TaminDocumentSourceSheet(
            title = deceasedDocumentTitle(documentType),
            showRemoveOption = state.deceasedDocuments.containsKey(documentType),
            onSelectCamera = {
                pendingDocumentType = documentType
                onIntent(PensionSurvivorIntent.DismissDeceasedDocumentSource)
                cameraPermission.request { granted ->
                    if (granted) {
                        cameraLauncher.launch()
                    } else {
                        scope.launch {
                            toaster.error(getString(Res.string.occurrence_camera_permission_denied))
                        }
                    }
                }
            },
            onSelectGallery = {
                pendingDocumentType = documentType
                onIntent(PensionSurvivorIntent.DismissDeceasedDocumentSource)
                galleryLauncher.launch()
            },
            onRemove = {
                onIntent(PensionSurvivorIntent.RemoveDeceasedDocument(documentType))
                onIntent(PensionSurvivorIntent.DismissDeceasedDocumentSource)
            },
            onDismiss = { onIntent(PensionSurvivorIntent.DismissDeceasedDocumentSource) },
        )
    }
}

@Composable
private fun deceasedDocumentTitle(type: DeceasedDocumentType): String = stringResource(
    when (type) {
        DeceasedDocumentType.DeathCertificate -> Res.string.pension_survivor_deceased_death_certificate
        DeceasedDocumentType.IdFirstPage -> Res.string.pension_survivor_deceased_id_first_page
        DeceasedDocumentType.IdChildrenPage -> Res.string.pension_survivor_deceased_id_children_page
    },
)

@Composable
private fun formatDeceasedAge(info: DeceasedInfoPR): String {
    val years = info.yearsAge?.takeIf(String::isNotBlank)
    val months = info.monthsAge?.takeIf(String::isNotBlank)
    val days = info.daysAge?.takeIf(String::isNotBlank)

    return if (years != null && months != null && days != null) {
        stringResource(
            Res.string.pension_survivor_deceased_age_full,
            years,
            months,
            days,
        )
    } else {
        years ?: stringResource(Res.string.amount_unknown)
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
private fun rememberBase64Thumbnail(bytes: ByteArray?): String? {
    val state = produceState<String?>(initialValue = null, bytes) {
        value = bytes?.let { withContext(Dispatchers.Default) { Base64.Default.encode(it) } }
    }
    return state.value
}

@Composable
private fun String?.orUnknown(): String {
    return this?.takeIf { it.isNotBlank() } ?: stringResource(Res.string.amount_unknown)
}

private const val NATIONAL_ID_LENGTH = 10
