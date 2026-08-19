package com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceErrorWrapper
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceNavigationBar
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSelectionBottomSheet
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSheetOption
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceTopAppBar
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.AccidentStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.DocumentSubmitStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceIntent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceUiState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.WorkHoursStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.WorkshopStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.OccurrenceDocTypePR
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.WorkshopItemPR
import com.tamin.taminhamrah.model.occurrence.OccurrenceUploadedDocDN
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.error_file_read_fallback
import taminx.core.core_ui.occurrence_add_document
import taminx.core.core_ui.occurrence_doc_format_hint
import taminx.core.core_ui.occurrence_doc_required_hint
import taminx.core.core_ui.occurrence_documents_min_hint
import taminx.core.core_ui.occurrence_documents_section_title
import taminx.core.core_ui.occurrence_prev_step
import taminx.core.core_ui.occurrence_sheet_doc_type_title
import taminx.core.core_ui.occurrence_step6_title
import taminx.core.core_ui.occurrence_submit
import taminx.core.core_ui.occurrence_submit_disclaimer
import taminx.core.core_ui.occurrence_summary_accident_date
import taminx.core.core_ui.occurrence_summary_accident_time
import taminx.core.core_ui.occurrence_summary_employer
import taminx.core.core_ui.occurrence_summary_outcome
import taminx.core.core_ui.occurrence_summary_section
import taminx.core.core_ui.occurrence_summary_transport
import taminx.core.core_ui.occurrence_summary_workshop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Step6DocumentSubmitStep(
    uiState: OccurrenceUiState,
    onIntent: (OccurrenceIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val toaster = LocalToaster.current
    val scope = rememberCoroutineScope()
    val step = uiState.documentSubmit
    var pendingDocType by remember { mutableStateOf<OccurrenceDocTypePR?>(null) }
    var showDocTypeSheet by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file: PlatformFile? ->
        val docType = pendingDocType ?: return@rememberFilePickerLauncher
        if (file == null) { pendingDocType = null; return@rememberFilePickerLauncher }
        scope.launch {
            try {
                val bytes = file.readBytes()
                onIntent(OccurrenceIntent.UploadDocument(typeId = docType.id, typeName = docType.title, fileName = file.name, fileBytes = bytes))
            } catch (e: Exception) {
                val msg = try { getString(Res.string.error_file_read_fallback) } catch (_: Exception) { e.message.orEmpty() }
                toaster.error(msg)
            } finally {
                pendingDocType = null
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            OccurrenceTopAppBar(
                title = stringResource(Res.string.occurrence_step6_title),
                onBackClicked = onBack,
                currentStep = uiState.stepNumber,
                totalSteps = OccurrenceStep.entries.size,
            )
        },
        bottomBar = {
            OccurrenceNavigationBar(
                primaryText = stringResource(Res.string.occurrence_submit),
                primaryEnabled = uiState.isStep6Valid && !uiState.isLoading && !uiState.isSubmitting,
                showChevron = false,
                onPrimaryClick = { onIntent(OccurrenceIntent.SubmitOccurrence) },
                secondaryText = stringResource(Res.string.occurrence_prev_step),
                onSecondaryClick = onBack,
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        OccurrenceErrorWrapper(
            isLoading = uiState.isLoading,
            error = null,
            onRetry = { onIntent(OccurrenceIntent.LoadInitialData) },
            modifier = Modifier.padding(padding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.lg),
            ) {
                Spacer(modifier = Modifier.height(Spacing.md))

                SectionLabel(text = stringResource(Res.string.occurrence_documents_section_title))
                Spacer(modifier = Modifier.height(Spacing.xs))

                Text(
                    text = stringResource(Res.string.occurrence_documents_min_hint, step.uploadedDocuments.size.toString()),
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textMuted,
                )
                Text(
                    text = stringResource(Res.string.occurrence_doc_format_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textMuted,
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                if (step.uploadedDocuments.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.occurrence_doc_required_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.dangerText,
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        step.uploadedDocuments.forEach { doc ->
                            UploadedDocRow(doc = doc, onDelete = { onIntent(OccurrenceIntent.RemoveDocument(doc.guid)) })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                TaminOutlinedButton(
                    text = stringResource(Res.string.occurrence_add_document),
                    onClick = { showDocTypeSheet = true },
                    enabled = !step.isUploadingDoc,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(Spacing.lg))
                TaminDivider()
                Spacer(modifier = Modifier.height(Spacing.md))

                SectionLabel(text = stringResource(Res.string.occurrence_summary_section))
                Spacer(modifier = Modifier.height(Spacing.xs))

                DetailRow(label = stringResource(Res.string.occurrence_summary_workshop), value = uiState.workshop.selectedWorkshop?.displayCode.orEmpty(), numeric = true)
                TaminDivider()
                DetailRow(label = stringResource(Res.string.occurrence_summary_employer), value = uiState.workshop.employerName, numeric = false)
                TaminDivider()
                DetailRow(label = stringResource(Res.string.occurrence_summary_accident_date), value = uiState.accident.accidentDate, numeric = true)
                TaminDivider()
                DetailRow(label = stringResource(Res.string.occurrence_summary_accident_time), value = uiState.accident.accidentTime, numeric = true)
                TaminDivider()
                DetailRow(label = stringResource(Res.string.occurrence_summary_outcome), value = uiState.accident.accidentOutcomeTitle, numeric = false)
                TaminDivider()
                DetailRow(label = stringResource(Res.string.occurrence_summary_transport), value = uiState.workHours.transportation, numeric = false)

                Spacer(modifier = Modifier.height(Spacing.md))

                Text(
                    text = stringResource(Res.string.occurrence_submit_disclaimer),
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textMuted,
                )

                Spacer(modifier = Modifier.height(Spacing.lg))
                Spacer(modifier = Modifier.height(padding.calculateBottomPadding()))
            }
        }
    }

    if (showDocTypeSheet) {
        OccurrenceSelectionBottomSheet(
            title = stringResource(Res.string.occurrence_sheet_doc_type_title),
            options = step.docTypes.map { OccurrenceSheetOption(id = it.id.toString(), title = it.title) },
            selectedId = null,
            onSelect = { option ->
                val docType = step.docTypes.first { it.id.toString() == option.id }
                pendingDocType = docType
                showDocTypeSheet = false
                filePickerLauncher.launch()
            },
            onDismiss = { showDocTypeSheet = false },
        )
    }
}

@Composable
private fun UploadedDocRow(
    doc: OccurrenceUploadedDocDN,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = doc.typeName, style = MaterialTheme.typography.bodyMedium, color = taminColors.textPrimary)
            Text(text = doc.fileName, style = MaterialTheme.typography.bodySmall, color = taminColors.textMuted)
        }
        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = taminColors.dangerText)
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
private fun Step6DocumentSubmitStepPreview() {
    PreviewRtlThemeContent {
        Step6DocumentSubmitStep(
            uiState = OccurrenceUiState(
                workshop = WorkshopStepState(
                    selectedWorkshop = WorkshopItemPR(id = "1", workshopCode = "1412345", branchCode = "014", name = "کارگاه تولیدی الف", employerName = "شرکت الف", employerPhone = "02112345678", address = "تهران، خیابان ولیعصر", postalCode = "1234567890", phone = "02112345678"),
                    employerName = "شرکت الف",
                ),
                accident = AccidentStepState(
                    accidentDate = "1402/06/15",
                    accidentTime = "۱۴:۳۰",
                    accidentOutcomeTitle = "استراحت پزشکی",
                ),
                workHours = WorkHoursStepState(
                    transportation = "وسیله نقلیه شخصی",
                ),
                documentSubmit = DocumentSubmitStepState(
                    docTypes = listOf(OccurrenceDocTypePR(id = 1, title = "گزارش حادثه"), OccurrenceDocTypePR(id = 2, title = "مدارک پزشکی")),
                    uploadedDocuments = listOf(
                        OccurrenceUploadedDocDN(guid = "abc-1", typeId = 1, typeName = "گزارش حادثه", fileName = "report.jpg"),
                        OccurrenceUploadedDocDN(guid = "abc-2", typeId = 2, typeName = "مدارک پزشکی", fileName = "medical.jpg"),
                    ),
                ),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}
