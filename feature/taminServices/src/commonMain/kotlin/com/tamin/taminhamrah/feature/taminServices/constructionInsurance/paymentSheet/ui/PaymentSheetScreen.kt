package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.contract.PaymentSheetEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.contract.PaymentSheetIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.contract.PaymentSheetUiState
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFilePR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.ToasterState
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.toast.success
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res as CoreRes
import taminx.core.core_ui.action_back
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.action_confirm
import taminx.core.core_ui.action_retry
import taminx.core.core_ui.btn_download_certificate
import taminx.core.core_ui.btn_issue_payment_sheet
import taminx.core.core_ui.issue_payment_sheet_confirm_message
import taminx.core.core_ui.issue_payment_sheet_confirm_title
import taminx.core.core_ui.label_order_number
import taminx.core.core_ui.label_payment_code
import taminx.core.core_ui.label_payment_date
import taminx.core.core_ui.label_payment_sheet_amount
import taminx.core.core_ui.label_payment_status
import taminx.core.core_ui.payment_sheet_empty
import taminx.core.core_ui.payment_sheet_title

private const val RIAL_UNIT = "ریال"

@Composable
fun PaymentSheetRoute(
    viewModel: PaymentSheetViewModel,
    debitNumber: String,
    branchCode: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    LaunchedEffect(Unit) {
        viewModel.sendIntent(PaymentSheetIntent.Load(debitNumber, branchCode))
    }

    PaymentSheetEvents(events = viewModel.events, toaster = toaster, onBackClicked = onBackClicked)

    PaymentSheetScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
        modifier = modifier,
    )
}

@Composable
private fun PaymentSheetEvents(
    events: Flow<PaymentSheetEvent>,
    toaster: ToasterState,
    onBackClicked: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            PaymentSheetEvent.NavigateBack -> onBackClicked()
            is PaymentSheetEvent.ShowError -> toaster.error(event.message)
        }
    }
}

@Composable
fun PaymentSheetScreen(
    state: PaymentSheetUiState,
    onIntent: (PaymentSheetIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val toaster = LocalToaster.current
    var showIssueConfirm by remember { mutableStateOf(false) }

    state.issuanceMessage?.let { message ->
        LaunchedEffect(message) { toaster.success(message) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(CoreRes.string.payment_sheet_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(CoreRes.string.action_back),
                        onClick = onBackClicked,
                        bordered = true,
                    )
                },
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.page, vertical = Spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    TaminOutlinedButton(
                        text = stringResource(CoreRes.string.btn_download_certificate),
                        onClick = { onIntent(PaymentSheetIntent.DownloadCertificate) },
                        icon = Icons.Filled.Description,
                        modifier = Modifier.weight(1f),
                    )
                    TaminFilledButton(
                        text = stringResource(CoreRes.string.btn_issue_payment_sheet),
                        onClick = { showIssueConfirm = true },
                        icon = Icons.Filled.Add,
                        modifier = Modifier.weight(1f),
                        enabled = !state.isIssuing,
                    )
                }

                when {
                    state.error != null && state.items.isEmpty() -> PaymentSheetErrorState(
                        message = state.error,
                        onRetry = { onIntent(PaymentSheetIntent.Retry) },
                    )

                    state.items.isEmpty() && !state.isLoading -> TaminEmptyState(
                        message = stringResource(CoreRes.string.payment_sheet_empty),
                        modifier = Modifier.padding(top = Spacing.xl),
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = Spacing.page, vertical = Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        items(items = state.items, key = { it.paymentCode ?: it.hashCode() }) { sheet ->
                            PaymentSheetCard(item = sheet)
                        }
                    }
                }
            }

            if (state.isLoading || state.isIssuing) {
                LoadingStateOverlay()
            }
        }
    }

    if (showIssueConfirm) {
        AlertDialog(
            onDismissRequest = { showIssueConfirm = false },
            title = { Text(stringResource(CoreRes.string.issue_payment_sheet_confirm_title)) },
            text = { Text(stringResource(CoreRes.string.issue_payment_sheet_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showIssueConfirm = false
                    onIntent(PaymentSheetIntent.IssuePaymentSheet)
                }) { Text(stringResource(CoreRes.string.action_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showIssueConfirm = false }) {
                    Text(stringResource(CoreRes.string.action_cancel))
                }
            },
        )
    }

    if (state.showPdfViewer) {
        TaminPdfViewer(
            fileName = "payment_certificate_${state.debitNumber}.pdf",
            pdf = state.pdfDownload,
            downloadFailed = state.pdfDownloadFailed,
            title = stringResource(CoreRes.string.btn_download_certificate),
            onRequestDownload = { onIntent(PaymentSheetIntent.DownloadCertificate) },
            onDismiss = { onIntent(PaymentSheetIntent.DismissPdfViewer) },
        )
    }
}

@Composable
private fun PaymentSheetCard(item: PaymentSheetConstructionFilePR, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(color = colors.shadowSubtle, borderRadius = CornerRadius.card, blurRadius = 20.dp, offsetY = 8.dp)
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        DetailRow(
            label = stringResource(CoreRes.string.label_order_number),
            value = item.orderNumber ?: "-",
            valueColor = colors.textPrimary,
            valueStyle = MaterialTheme.typography.titleSmall,
        )
        DetailRow(label = stringResource(CoreRes.string.label_payment_code), value = item.paymentCode ?: "-")
        DetailRow(
            label = stringResource(CoreRes.string.label_payment_status),
            value = item.status ?: "-",
            numeric = false,
        )
        DetailRow(label = stringResource(CoreRes.string.label_payment_date), value = item.paymentDate ?: "-")
        DetailRow(
            label = stringResource(CoreRes.string.label_payment_sheet_amount),
            value = (item.paymentSheetAmount ?: 0L).toPriceFormat(),
            unit = RIAL_UNIT,
            valueColor = colors.blueText,
        )
    }
}

@Composable
private fun PaymentSheetErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(text = message, color = LocalTaminColors.current.dangerText)
        TaminOutlinedButton(text = stringResource(CoreRes.string.action_retry), onClick = onRetry)
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewSheets = kotlinx.collections.immutable.persistentListOf(
    PaymentSheetConstructionFilePR(
        orderNumber = "1",
        paymentCode = "36001234500",
        paymentSheetAmount = 500_000L,
        status = "پرداخت شده",
        paymentDate = "14021015",
    ),
    PaymentSheetConstructionFilePR(
        orderNumber = "2",
        paymentCode = "36001234501",
        paymentSheetAmount = 1_000_000L,
        status = "در انتظار پرداخت",
        paymentDate = null,
    ),
)

@PreviewRtlTheme
@Composable
private fun PaymentSheetScreenPreview() {
    PreviewRtlThemeContent {
        PaymentSheetScreen(
            state = PaymentSheetUiState(debitNumber = "123456789012", items = PreviewSheets),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PaymentSheetScreenEmptyPreview() {
    PreviewRtlThemeContent {
        PaymentSheetScreen(
            state = PaymentSheetUiState(debitNumber = "123456789012"),
            onIntent = {},
            onBackClicked = {},
        )
    }
}
