package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminDivider
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
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toFormattedDate
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.action_back
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.action_confirm
import taminx.core.core_ui.btn_download_certificate
import taminx.core.core_ui.btn_issue_payment_sheet
import taminx.core.core_ui.deferred_installment_rial
import taminx.core.core_ui.issue_payment_sheet_confirm_message
import taminx.core.core_ui.issue_payment_sheet_confirm_title
import taminx.core.core_ui.label_order_number
import taminx.core.core_ui.label_payment_code
import taminx.core.core_ui.label_payment_date
import taminx.core.core_ui.label_payment_sheet_amount
import taminx.core.core_ui.payment_sheet_empty
import taminx.core.core_ui.payment_sheet_title
import taminx.core.core_ui.Res as CoreRes


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
        LaunchedEffect(message) {
            toaster.success(message)
            onBackClicked()
        }
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
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when {
                    state.isLoading && state.items.isEmpty() -> PaymentSheetSkeleton(
                        modifier = Modifier.fillMaxSize(),
                    )

                    state.items.isEmpty() && !state.isLoading -> EmptyStateMessage(
                        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                        title = stringResource(CoreRes.string.payment_sheet_empty),
                        showIconTile = true,
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = Spacing.page,
                            vertical = Spacing.md
                        ),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        items(
                            items = state.items,
                            key = { it.paymentCode ?: it.hashCode() }) { sheet ->
                            PaymentSheetCard(item = sheet)
                        }
                    }
                }

                if (state.items.isNotEmpty() && (state.isLoading || state.isIssuing)) {
                    LoadingStateOverlay()
                }
            }

            PaymentSheetActionBar(
                isIssuing = state.isIssuing,
                onDownloadCertificate = { onIntent(PaymentSheetIntent.DownloadCertificate) },
                onIssueClicked = { showIssueConfirm = true },
            )
        }
    }

    if (showIssueConfirm) {
        TaminConfirmationDialog(
            title = stringResource(CoreRes.string.issue_payment_sheet_confirm_title),
            description = stringResource(CoreRes.string.issue_payment_sheet_confirm_message),
            icon = Icons.AutoMirrored.Outlined.ReceiptLong,
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(CoreRes.string.action_confirm),
                    onClick = {
                        showIssueConfirm = false
                        onIntent(PaymentSheetIntent.IssuePaymentSheet)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {
                TaminOutlinedButton(
                    text = stringResource(CoreRes.string.action_cancel),
                    onClick = { showIssueConfirm = false },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            onDismissRequest = { showIssueConfirm = false },
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

/** The container/content pair a payment sheet's status pill is drawn in, keyed off the server's own status text. */
@Composable
private fun paymentStatusTint(status: String?): Pair<Color, Color> {
    val colors = LocalTaminColors.current
    return when {
        status == null -> colors.chipBg to colors.textSecondary
        status.contains("پرداخت شده") -> colors.greenBg to colors.greenText
        status.contains("انتظار") -> colors.orangeBg to colors.orangeText
        status.contains("منقضی") || status.contains("باطل") -> colors.dangerBorder to colors.dangerText
        else -> colors.chipBg to colors.textSecondary
    }
}

@Composable
private fun PaymentSheetCard(item: PaymentSheetConstructionFilePR, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val (tileColor, accent) = paymentStatusTint(item.status)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = 20.dp,
                offsetY = 8.dp
            )
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Text(
                    text = stringResource(CoreRes.string.label_order_number),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
                NumericText(
                    text = item.orderNumber ?: "-",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                )
            }
            StatusPill(
                text = item.status ?: "-",
                containerColor = tileColor,
                contentColor = accent,
            )
        }

        TaminDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(
                    text = stringResource(CoreRes.string.label_payment_sheet_amount),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                ) {
                    NumericText(
                        text = (item.paymentSheetAmount ?: 0L).toPriceFormat(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = accent,
                    )
                    Text(
                        text = stringResource(CoreRes.string.deferred_installment_rial),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textMuted
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
            ) {
                Text(
                    text = stringResource(CoreRes.string.label_payment_date),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
                NumericText(
                    text = item.paymentDate?.toFormattedDate().orDash(),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textPrimary,
                )
            }
        }

        DetailRow(
            label = stringResource(CoreRes.string.label_payment_code),
            value = item.paymentCode ?: "-",
        )
    }
}

/** Fixed footer — never scrolls with [PaymentSheetCard]s, which run under it instead. */
@Composable
private fun PaymentSheetActionBar(
    isIssuing: Boolean,
    onDownloadCertificate: () -> Unit,
    onIssueClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.glassSolid)
            .padding(horizontal = Spacing.page, vertical = Spacing.md)
            .padding(bottom = bottomInset),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        TaminOutlinedButton(
            containerColor = colors.chipBg,
            textStyle = MaterialTheme.typography.titleSmall,
            text = stringResource(CoreRes.string.btn_download_certificate),
            onClick = onDownloadCertificate,
            icon = Icons.Filled.Description,
            shape = RoundedCornerShape(CornerRadius.lg),
            modifier = Modifier.weight(1f),
            borderWidth = 1.dp,
            borderColor = colors.blueBorder
        )
        LoadingButton(
            textStyle = MaterialTheme.typography.titleSmall,
            text = stringResource(CoreRes.string.btn_issue_payment_sheet),
            onClick = onIssueClicked,
            icon = Icons.Filled.Add,
            modifier = Modifier.weight(1f),
            enabled = !isIssuing,
            isLoading = isIssuing
        )
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

@PreviewRtlTheme
@Composable
private fun PaymentSheetScreenLoadingPreview() {
    PreviewRtlThemeContent {
        PaymentSheetScreen(
            state = PaymentSheetUiState(debitNumber = "123456789012", isLoading = true),
            onIntent = {},
            onBackClicked = {},
        )
    }
}
