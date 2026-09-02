package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.document

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.components.ObjectionSummaryHeader
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.components.label
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.drainBytesOrNull
import com.tamin.taminhamrah.ui.components.rememberPdfSaver
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_download
import taminx.core.core_ui.objection_document_debit_number
import taminx.core.core_ui.objection_document_download_button
import taminx.core.core_ui.objection_document_downloaded_desc
import taminx.core.core_ui.objection_document_downloaded_title
import taminx.core.core_ui.objection_document_file_size
import taminx.core.core_ui.objection_document_got_it
import taminx.core.core_ui.objection_document_no_document_desc
import taminx.core.core_ui.objection_document_no_document_title
import taminx.core.core_ui.objection_document_objection_date
import taminx.core.core_ui.objection_document_objection_number
import taminx.core.core_ui.objection_document_subtitle_article16
import taminx.core.core_ui.objection_document_subtitle_default
import taminx.core.core_ui.objection_document_title
import taminx.core.core_ui.objection_document_title_article16
import taminx.core.core_ui.objection_status_action_sms
import taminx.core.core_ui.workshop_code

@Composable
fun ObjectionDocumentScreen(
    seqNo: Long,
    debitNumber: String,
    workshopId: String,
    objectionDate: String,
    objectionType: WorkShopObjectionType,
    objectionStatus: WorkShopObjectionStatus,
    onBack: () -> Unit,
    onOpenSms: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ObjectionDocumentViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(seqNo) {
        viewModel.sendIntent(
            ObjectionDocumentIntent.Open(
                seqNo = seqNo,
                debitNumber = debitNumber,
                workshopId = workshopId,
                objectionDate = objectionDate,
                objectionType = objectionType,
                objectionStatus = objectionStatus,
            )
        )
    }

    ObjectionDocumentContent(
        state = state,
        onBack = onBack,
        onOpenSms = onOpenSms,
        onDownload = { viewModel.sendIntent(ObjectionDocumentIntent.DownloadFile) },
        modifier = modifier,
    )
}

@Composable
fun ObjectionDocumentContent(
    state: ObjectionDocumentUiState,
    onBack: () -> Unit,
    onOpenSms: () -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val saver = rememberPdfSaver()
    var downloadedBytes by remember(state.seqNo) { mutableStateOf<Int?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showNoDocumentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.pdf, state.downloadFailed) {
        val pdf = state.pdf ?: run {
            if (state.downloadFailed) showNoDocumentDialog = true
            return@LaunchedEffect
        }
        val usable = pdf.drainBytesOrNull()
        if (usable == null) {
            showNoDocumentDialog = true
        } else {
            saver.save("objection_${state.seqNo}.pdf", usable)
            downloadedBytes = usable.size
            showSuccessDialog = true
        }
    }

    val isArticleSixteen = state.objectionType == WorkShopObjectionType.ARTICLE_SIXTEEN
    Column(modifier = modifier.fillMaxWidth().background(colors.bgPage)) {
        TaminTopAppBar(
            title = if (isArticleSixteen) {
                stringResource(Res.string.objection_document_title_article16)
            } else {
                stringResource(Res.string.objection_document_title)
            },
            background = Brush.horizontalGradient(colors.profileGradientStops),
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBack,
                    bordered = true,
                )
            },
        ) {
            ObjectionSummaryHeader(
                status = state.objectionStatus,
                objectionType = state.objectionType,
                objectionNumber = state.seqNo.toString().toPersianDigits(),
                onNavigateToSibling = onOpenSms,
                siblingIcon = Icons.Default.Description,
                siblingContentDescription = stringResource(Res.string.objection_status_action_sms),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .taminSurface(CornerRadius.card)
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.smPlus),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                        Text(
                            text = if (isArticleSixteen) {
                                stringResource(Res.string.objection_document_title_article16)
                            } else {
                                stringResource(Res.string.objection_document_title)
                            },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = downloadedBytes?.let { bytes ->
                                stringResource(Res.string.objection_document_file_size, bytes / 1024)
                            } ?: if (isArticleSixteen) {
                                stringResource(Res.string.objection_document_subtitle_article16)
                            } else {
                                stringResource(Res.string.objection_document_subtitle_default)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textMuted,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(colors.dangerBg, RoundedCornerShape(CornerRadius.lg)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = colors.dangerText,
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.bgPage, RoundedCornerShape(CornerRadius.lg))
                        .padding(Spacing.smPlus),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    DetailRow(
                        label = stringResource(Res.string.objection_document_objection_number),
                        value = state.seqNo.toString().toPersianDigits(),
                    )
                    DetailRow(
                        label = stringResource(Res.string.objection_document_debit_number),
                        value = state.debitNumber,
                    )
                    DetailRow(
                        label = stringResource(Res.string.workshop_code),
                        value = state.workshopId,
                    )
                    DetailRow(
                        label = stringResource(Res.string.objection_document_objection_date),
                        value = state.objectionDate,
                    )
                }

                if (state.isDownloading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .taminSurface(CornerRadius.lg)
                            .padding(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        repeat(4) {
                            ShimmerBlock(
                                modifier = Modifier.fillMaxWidth().height(8.dp),
                                cornerRadius = CornerRadius.chip,
                            )
                        }
                    }
                }
            }

            LoadingButton(
                text = stringResource(Res.string.objection_document_download_button),
                onClick = onDownload,
                isLoading = state.isDownloading,
                icon = vectorResource(Res.drawable.ic_tamin_download),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (showSuccessDialog) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.objection_document_downloaded_title),
            description = stringResource(Res.string.objection_document_downloaded_desc),
            icon = Icons.Default.Check,
            confirmButton = {
                LoadingButton(
                    text = stringResource(Res.string.objection_document_got_it),
                    onClick = { showSuccessDialog = false },
                    isLoading = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
            onDismissRequest = { showSuccessDialog = false },
            iconTint = colors.greenText,
            iconBackground = colors.greenBg,
        )
    }

    if (showNoDocumentDialog) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.objection_document_no_document_title),
            description = stringResource(Res.string.objection_document_no_document_desc),
            icon = Icons.Default.Warning,
            confirmButton = {
                LoadingButton(
                    text = stringResource(Res.string.objection_document_got_it),
                    onClick = { showNoDocumentDialog = false },
                    isLoading = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
            onDismissRequest = { showNoDocumentDialog = false },
            iconTint = colors.dangerText,
            iconBackground = colors.dangerBg,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ObjectionDocumentScreenPreview() {
    PreviewRtlThemeContent {
        ObjectionDocumentContent(
            state = ObjectionDocumentUiState(
                seqNo = 1403008720,
                debitNumber = "۱۴۰۲/۴۴۱۹۰",
                workshopId = "۲۳۶۱۸۴۷",
                objectionDate = "۱۴۰۳/۰۹/۱۲",
                objectionType = WorkShopObjectionType.ESTIMATE,
                objectionStatus = WorkShopObjectionStatus.BOARD_REVIEW,
            ),
            onBack = {},
            onOpenSms = {},
            onDownload = {},
        )
    }
}
