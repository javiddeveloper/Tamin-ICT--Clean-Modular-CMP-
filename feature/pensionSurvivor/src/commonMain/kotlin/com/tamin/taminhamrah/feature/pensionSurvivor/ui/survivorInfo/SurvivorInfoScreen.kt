package com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.SurvivorContactDraft
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.SharedDeceasedDocument
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.SurvivorDocumentType
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.SurvivorRelationClassifier
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoEvent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoUiState
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorUploadedDocument
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminImageViewer
import com.tamin.taminhamrah.ui.components.TaminTextField
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.components.buttons.SquareIconButton
import com.tamin.taminhamrah.ui.components.document.TaminDocumentSourceSheet
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadCard
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadState
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.toast.success
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.rememberCameraPermission
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.amount_unknown
import taminx.core.core_ui.error_file_read_fallback
import taminx.core.core_ui.girl_survivor_address_label
import taminx.core.core_ui.girl_survivor_label_birth_date
import taminx.core.core_ui.girl_survivor_label_father_name
import taminx.core.core_ui.girl_survivor_label_full_name
import taminx.core.core_ui.girl_survivor_label_insurance_id
import taminx.core.core_ui.girl_survivor_phone_label
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.identity_field_mobile
import taminx.core.core_ui.occurrence_camera_permission_denied
import taminx.core.core_ui.orotez_protez_document_pick_placeholder
import taminx.core.core_ui.orotez_protez_document_status_error_tap_to_retry
import taminx.core.core_ui.orotez_protez_document_status_uploaded
import taminx.core.core_ui.pension_survivor_doc_husband_marriage_page
import taminx.core.core_ui.pension_survivor_doc_id_first_page
import taminx.core.core_ui.pension_survivor_doc_marriage_type_page
import taminx.core.core_ui.pension_survivor_doc_spouse_id_page
import taminx.core.core_ui.pension_survivor_doc_study_certificate
import taminx.core.core_ui.pension_survivor_doc_wife_marriage_page
import taminx.core.core_ui.pension_survivor_documents_count
import taminx.core.core_ui.pension_survivor_documents_upload_hint
import taminx.core.core_ui.pension_survivor_documents_upload_title
import taminx.core.core_ui.pension_survivor_register_survivor_info
import taminx.core.core_ui.pension_survivor_relation_survivor
import taminx.core.core_ui.pension_survivor_show_survivor_info
import taminx.core.core_ui.pension_survivor_title
import taminx.core.core_ui.verify_label_national_id
import kotlin.io.encoding.Base64

@Composable
fun SurvivorInfoScreen(
    survivor: SurvivorDependentPR,
    deceasedNationalId: String,
    branchCode: String = "",
    deceasedInsuranceId: String = "",
    sharedDeceasedDocuments: List<SharedDeceasedDocument> = emptyList(),
    address: String = "",
    phoneNumber: String = "",
    mobileNumber: String = "",
    onSaved: (String, SurvivorContactDraft) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    viewModel: SurvivorInfoViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    LaunchedEffect(
        survivor.nationalId,
        deceasedNationalId,
        branchCode,
        deceasedInsuranceId,
        address,
        phoneNumber,
        mobileNumber,
        sharedDeceasedDocuments,
    ) {
        viewModel.sendIntent(
            SurvivorInfoIntent.Init(
                survivor = survivor,
                deceasedNationalId = deceasedNationalId,
                branchCode = branchCode,
                deceasedInsuranceId = deceasedInsuranceId,
                address = address,
                phoneNumber = phoneNumber,
                mobileNumber = mobileNumber,
                sharedDeceasedDocuments = sharedDeceasedDocuments,
            ),
        )
    }

    HandleSurvivorInfoEvents(
        events = viewModel.events,
        onNavigateBack = onBack,
        onShowError = { toaster.error(it) },
        onShowSuccess = { toaster.success(it) },
        onSaved = onSaved,
    )

    SurvivorInfoContent(
        state = state,
        onBack = { viewModel.sendIntent(SurvivorInfoIntent.OnBack) },
        onIntent = viewModel::sendIntent,
    )
}

@Composable
private fun HandleSurvivorInfoEvents(
    events: Flow<SurvivorInfoEvent>,
    onNavigateBack: () -> Unit,
    onShowError: (String) -> Unit,
    onShowSuccess: (String) -> Unit,
    onSaved: (String, SurvivorContactDraft) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            SurvivorInfoEvent.NavigateBack -> onNavigateBack()
            is SurvivorInfoEvent.ShowError -> onShowError(event.message)
            is SurvivorInfoEvent.Saved -> onSaved(event.nationalId, event.draft)
            is SurvivorInfoEvent.ShowSuccess -> onShowSuccess(event.message)
        }
    }
}

@Composable
private fun SurvivorInfoContent(
    state: SurvivorInfoUiState,
    onBack: () -> Unit,
    onIntent: (SurvivorInfoIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val survivor = state.survivor

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPage),
        containerColor = colors.bgPage,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.pension_survivor_title),
                background = taminTopAppBarGradient(colors.profileGradientStops),
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.x3l,
                    bottomEnd = CornerRadius.x3l,
                ),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = onBack,
                        bordered = true,
                    )
                },
                action = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_cross),
                        contentDescription = null,
                        onClick = onBack,
                        bordered = true,
                    )
                },
            )
        },
        bottomBar = {
            TaminBottomBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SquareIconButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        onClick = onBack,
                    )
                    LoadingButton(
                        text = stringResource(Res.string.pension_survivor_register_survivor_info),
                        onClick = { onIntent(SurvivorInfoIntent.Save) },
                        isLoading = state.isLoading,
                        modifier = Modifier.weight(1f),
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                        iconPosition = LoadingButtonIconPosition.TRAILING,
                    )
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            survivor?.let {
                SurvivorSummaryCard(
                    survivor = it,
                    onShowInfo = { onIntent(SurvivorInfoIntent.ShowIdentity) },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
            ) {
                TaminTextField(
                    value = state.mobileNumber,
                    onValueChange = { onIntent(SurvivorInfoIntent.MobileNumberChanged(it)) },
                    label = stringResource(Res.string.identity_field_mobile),
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f),
                )
                TaminTextField(
                    value = state.phoneNumber,
                    onValueChange = { onIntent(SurvivorInfoIntent.PhoneNumberChanged(it)) },
                    label = stringResource(Res.string.girl_survivor_phone_label),
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f),
                )
            }

            TaminTextField(
                value = state.address,
                onValueChange = { onIntent(SurvivorInfoIntent.AddressChanged(it)) },
                label = stringResource(Res.string.girl_survivor_address_label),
            )

            state.fieldError?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.dangerText,
                )
            }

            SurvivorDocumentsSection(state = state, onIntent = onIntent)

            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }

    if (state.showIdentitySheet && survivor != null) {
        SurvivorIdentitySheet(
            survivor = survivor,
            onDismiss = { onIntent(SurvivorInfoIntent.DismissIdentity) },
        )
    }
}

@Composable
private fun SurvivorSummaryCard(
    survivor: SurvivorDependentPR,
    onShowInfo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val fullName = listOf(survivor.firstName, survivor.lastName)
        .filter(String::isNotBlank)
        .joinToString(" ")
        .ifBlank { stringResource(Res.string.amount_unknown) }
    val relationRes = SurvivorRelationClassifier.relationTitleRes(
        tendencyCode = survivor.tendencyCode,
        genderCode = survivor.genderCode,
    )
    val relationLabel = relationRes?.let { stringResource(it) }
        ?: stringResource(Res.string.pension_survivor_relation_survivor)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.blueBg, RoundedCornerShape(CornerRadius.card))
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = fullName,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
        Text(
            text = survivor.nationalId.ifBlank { stringResource(Res.string.amount_unknown) },
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textMuted,
        )
        Text(
            text = relationLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textMuted,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.bgSurface, RoundedCornerShape(CornerRadius.lg))
                .clickable(onClick = onShowInfo)
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            androidx.compose.material3.Icon(
                imageVector = vectorResource(Res.drawable.ic_info),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.medium),
            )
            Text(
                text = stringResource(Res.string.pension_survivor_show_survivor_info),
                style = MaterialTheme.typography.labelLarge,
                color = colors.blueText,
            )
        }
    }
}

@Composable
private fun SurvivorDocumentsSection(
    state: SurvivorInfoUiState,
    onIntent: (SurvivorInfoIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val toaster = LocalToaster.current
    val scope = rememberCoroutineScope()
    val cameraPermission = rememberCameraPermission()
    var previewDocument by remember { mutableStateOf<SurvivorUploadedDocument?>(null) }
    var pendingDocumentType by remember { mutableStateOf<SurvivorDocumentType?>(null) }

    fun handlePicked(file: PlatformFile?) {
        val documentType = pendingDocumentType ?: state.activeDocument ?: return
        pendingDocumentType = null
        onIntent(SurvivorInfoIntent.DismissDocumentSource)
        if (file == null) return

        scope.launch {
            try {
                val bytes = file.readBytes()
                onIntent(
                    SurvivorInfoIntent.DocumentImagePicked(
                        type = documentType,
                        fileName = file.name,
                        bytes = bytes,
                    ),
                )
            } catch (_: Exception) {
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
                text = stringResource(Res.string.pension_survivor_documents_upload_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            StatusPill(
                text = stringResource(
                    Res.string.pension_survivor_documents_count,
                    state.uploadedRequiredCount.toString(),
                    state.requiredDocuments.size.toString(),
                ),
                containerColor = if (state.areRequiredDocumentsComplete) colors.greenBg else colors.orangeBg,
                contentColor = if (state.areRequiredDocumentsComplete) colors.greenText else colors.orangeText,
            )
        }

        Text(
            text = stringResource(Res.string.pension_survivor_documents_upload_hint),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )

        state.requiredDocuments.forEach { documentType ->
            val uploaded = state.documents[documentType]
            val uploadState = when {
                state.uploadingDocument == documentType -> TaminDocumentUploadState.Uploading
                uploaded != null -> TaminDocumentUploadState.Uploaded
                state.failedDocument == documentType -> TaminDocumentUploadState.Failed
                else -> TaminDocumentUploadState.Empty
            }
            val thumbnailBase64 = rememberBase64Thumbnail(uploaded?.bytes)
            val statusText = when (uploadState) {
                TaminDocumentUploadState.Uploaded -> uploaded?.fileName
                    ?: stringResource(Res.string.orotez_protez_document_status_uploaded)
                TaminDocumentUploadState.Failed -> state.documentError
                    ?: stringResource(Res.string.orotez_protez_document_status_error_tap_to_retry)
                else -> stringResource(Res.string.orotez_protez_document_pick_placeholder)
            }

            TaminDocumentUploadCard(
                title = survivorDocumentTitle(documentType),
                state = uploadState,
                statusText = statusText,
                isRequired = true,
                thumbnailBase64 = thumbnailBase64,
                onCardClick = {
                    if (!state.isDocumentUploading) {
                        onIntent(SurvivorInfoIntent.DocumentClicked(documentType))
                    }
                },
                onPreviewClick = uploaded?.let { { previewDocument = it } },
                onDeleteClick = uploaded?.let {
                    { onIntent(SurvivorInfoIntent.RemoveDocument(documentType)) }
                },
            )
        }

        state.documentError?.takeIf { state.failedDocument == null }?.let { message ->
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
            title = survivorDocumentTitle(preview.type),
            url = previewBase64,
            onDismiss = { previewDocument = null },
        )
    }

    state.activeDocument?.let { documentType ->
        TaminDocumentSourceSheet(
            title = survivorDocumentTitle(documentType),
            showRemoveOption = state.documents.containsKey(documentType),
            onSelectCamera = {
                pendingDocumentType = documentType
                onIntent(SurvivorInfoIntent.DismissDocumentSource)
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
                onIntent(SurvivorInfoIntent.DismissDocumentSource)
                galleryLauncher.launch()
            },
            onRemove = {
                onIntent(SurvivorInfoIntent.RemoveDocument(documentType))
                onIntent(SurvivorInfoIntent.DismissDocumentSource)
            },
            onDismiss = { onIntent(SurvivorInfoIntent.DismissDocumentSource) },
        )
    }
}

@Composable
private fun SurvivorIdentitySheet(
    survivor: SurvivorDependentPR,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val rows = buildIdentityRows(survivor)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = stringResource(Res.string.pension_survivor_show_survivor_info),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            rows.forEachIndexed { index, row ->
                DetailRow(
                    label = row.label,
                    value = row.value,
                    numeric = row.numeric,
                )
                if (index < rows.lastIndex) {
                    TaminDivider()
                }
            }
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

@Composable
private fun buildIdentityRows(survivor: SurvivorDependentPR): List<IdentityRow> {
    val birthDate = survivor.dateOfBirth
        .toLongOrNull()
        ?.let(PersianDateFormatter::formatTimestamp)
        .orEmpty()

    return listOf(
        IdentityRow(
            label = stringResource(Res.string.girl_survivor_label_full_name),
            value = listOf(survivor.firstName, survivor.lastName)
                .filter(String::isNotBlank)
                .joinToString(" ")
                .ifBlank { stringResource(Res.string.amount_unknown) },
            numeric = false,
        ),
        IdentityRow(
            label = stringResource(Res.string.verify_label_national_id),
            value = survivor.nationalId.orUnknown(),
        ),
        IdentityRow(
            label = stringResource(Res.string.girl_survivor_label_father_name),
            value = survivor.fatherName.orUnknown(),
            numeric = false,
        ),
        IdentityRow(
            label = stringResource(Res.string.girl_survivor_label_insurance_id),
            value = survivor.insuranceId.orUnknown(),
        ),
        IdentityRow(
            label = stringResource(Res.string.girl_survivor_label_birth_date),
            value = birthDate.ifBlank { stringResource(Res.string.amount_unknown) },
            numeric = false,
        ),
    )
}

@Composable
private fun survivorDocumentTitle(type: SurvivorDocumentType): String = stringResource(
    when (type) {
        SurvivorDocumentType.IdFirstPage -> Res.string.pension_survivor_doc_id_first_page
        SurvivorDocumentType.SpouseIdPage -> Res.string.pension_survivor_doc_spouse_id_page
        SurvivorDocumentType.StudyCertificate -> Res.string.pension_survivor_doc_study_certificate
        SurvivorDocumentType.HusbandMarriagePage -> Res.string.pension_survivor_doc_husband_marriage_page
        SurvivorDocumentType.WifeMarriagePage -> Res.string.pension_survivor_doc_wife_marriage_page
        SurvivorDocumentType.MarriageTypePage -> Res.string.pension_survivor_doc_marriage_type_page
    },
)

@Composable
private fun rememberBase64Thumbnail(bytes: ByteArray?): String? {
    val base64 by produceState<String?>(initialValue = null, bytes) {
        value = if (bytes == null) {
            null
        } else {
            withContext(Dispatchers.Default) {
                "data:image/jpeg;base64,${Base64.encode(bytes)}"
            }
        }
    }
    return base64
}

@Composable
private fun String.orUnknown(): String {
    return if (isBlank()) stringResource(Res.string.amount_unknown) else this
}

private data class IdentityRow(
    val label: String,
    val value: String,
    val numeric: Boolean = true,
)
