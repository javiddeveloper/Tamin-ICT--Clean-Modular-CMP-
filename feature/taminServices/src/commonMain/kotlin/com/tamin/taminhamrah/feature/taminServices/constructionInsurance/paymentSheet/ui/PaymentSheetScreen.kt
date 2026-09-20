package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.contract.PaymentSheetEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.contract.PaymentSheetIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.contract.PaymentSheetUiState
import com.tamin.taminhamrah.model.constructionInsurance.BuildingRequestSummaryPR
import com.tamin.taminhamrah.model.constructionInsurance.KeyValueModel
import com.tamin.taminhamrah.model.constructionInsurance.EnumTextColor
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFilePR
import com.tamin.taminhamrah.model.constructionInsurance.WorkshopIdInfoPR
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
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toFormattedDate
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.action_back
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.action_confirm
import taminx.core.core_ui.action_hide_details
import taminx.core.core_ui.action_show_details
import taminx.core.core_ui.btn_download_certificate
import taminx.core.core_ui.btn_issue_payment_sheet
import taminx.core.core_ui.deferred_installment_rial
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.issue_payment_sheet_confirm_message
import taminx.core.core_ui.issue_payment_sheet_confirm_title
import taminx.core.core_ui.label_order_number
import taminx.core.core_ui.label_payment_code
import taminx.core.core_ui.label_payment_date
import taminx.core.core_ui.label_payment_sheet_amount
import taminx.core.core_ui.payment_sheet_empty
import taminx.core.core_ui.payment_sheet_issuance_processing_notice
import taminx.core.core_ui.payment_sheet_request_summary_title
import taminx.core.core_ui.payment_sheet_status_paid
import taminx.core.core_ui.payment_sheet_status_unpaid
import taminx.core.core_ui.payment_sheet_title
import taminx.core.core_ui.objection_document_got_it
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
    var showIssueConfirm by remember { mutableStateOf(false) }

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
                        state.items.firstOrNull()?.buildingRequest?.let { buildingRequest ->
                            item {
                                RequestSummaryCard(items = buildingRequest.getRequestInfo())
                            }
                        }
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
                showCertificateAction = state.items.isNotEmpty(),
                onDownloadCertificate = { onIntent(PaymentSheetIntent.DownloadCertificate) },
                onIssueClicked = { showIssueConfirm = true },
            )
        }
    }

    state.issuanceMessage?.let {
        TaminConfirmationDialog(
            title = stringResource(CoreRes.string.issue_payment_sheet_confirm_title),
            description = stringResource(CoreRes.string.payment_sheet_issuance_processing_notice),
            icon = Icons.AutoMirrored.Outlined.ReceiptLong,
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(CoreRes.string.objection_document_got_it),
                    onClick = { onIntent(PaymentSheetIntent.DismissIssuanceNotice) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
            onDismissRequest = { onIntent(PaymentSheetIntent.DismissIssuanceNotice) },
        )
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

/**
 * The text/container/content triple a payment sheet's status pill is drawn from.
 *
 * Legacy compares the server's status as a bare code — `status == "1"` → «پرداخت شده» (green),
 * anything else → «پرداخت نشده» (red) — a strict binary, not the four-way pending/expired/void
 * split this screen used to draw from Persian substrings. This now matches that binary exactly:
 * a `"1"` code or the «پرداخت شده» text itself means paid; every other non-null value, whatever its
 * shape, is shown as unpaid. Which shape the live `getPaymentSheetConstructionInfo` service actually
 * sends (a code or free text) still couldn't be confirmed from client code alone (MR !244 review
 * item 5) — but the mapping itself no longer needs that answer, since both shapes now resolve to
 * the same two legacy states.
 */
@Composable
private fun paymentStatusDisplay(status: String?): Triple<String, Color, Color> {
    val colors = LocalTaminColors.current
    return when {
        status == null -> Triple("-", colors.chipBg, colors.textSecondary)
        status == PAID_STATUS_CODE || status.contains("پرداخت شده") -> Triple(
            stringResource(CoreRes.string.payment_sheet_status_paid),
            colors.greenBg,
            colors.greenText,
        )
        else -> Triple(
            stringResource(CoreRes.string.payment_sheet_status_unpaid),
            colors.dangerBorder,
            colors.dangerText,
        )
    }
}

private const val PAID_STATUS_CODE = "1"

@Composable
private fun PaymentSheetCard(item: PaymentSheetConstructionFilePR, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val (statusText, tileColor, accent) = paymentStatusDisplay(item.status)

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
                text = statusText,
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
    showCertificateAction: Boolean,
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
        // Legacy hides this when the file has no issued sheets yet — there is nothing to certify.
        if (showCertificateAction) {
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
        }
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

/** The expandable «خلاصه درخواست» block legacy shows above the payment-sheet list. */
@Composable
private fun RequestSummaryCard(
    items: List<KeyValueModel>,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
) {
    val colors = LocalTaminColors.current
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) -90f else 90f,
        label = "payment-sheet-request-summary-chevron",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = 20.dp,
                offsetY = 8.dp,
            )
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.lg)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(CoreRes.string.payment_sheet_request_summary_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            Icon(
                imageVector = vectorResource(CoreRes.drawable.ic_tamin_chevron_back),
                contentDescription = stringResource(
                    if (expanded) CoreRes.string.action_hide_details else CoreRes.string.action_show_details,
                ),
                tint = colors.blueText,
                modifier = Modifier
                    .size(Spacing.lg)
                    .graphicsLayer { rotationZ = rotation },
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .padding(bottom = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                items.forEach { kv ->
                    val label = kv.keyResId?.let { stringResource(it) } ?: kv.keyString.orEmpty()
                    val value = kv.valueResId?.let { stringResource(it) } ?: kv.value
                    DetailRow(
                        label = label,
                        value = value,
                        valueColor = colorForKeyValue(kv.textColor),
                        numeric = kv.numeric,
                        unit = kv.unit,
                    )
                }
            }
        }
    }
}

@Composable
private fun colorForKeyValue(textColor: EnumTextColor) = when (textColor) {
    EnumTextColor.DEFAULT -> LocalTaminColors.current.textPrimary
    EnumTextColor.AMBER -> LocalTaminColors.current.orangeText
    EnumTextColor.GREEN -> LocalTaminColors.current.greenText
    EnumTextColor.RED -> LocalTaminColors.current.dangerText
    EnumTextColor.BLUE -> LocalTaminColors.current.blueText
}


// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewBuildingRequest = BuildingRequestSummaryPR(
    debitNumber = "123456789012",
    fileNumber = 4_479_890_882L,
    requestNumber = 123_456_789L,
    requestDate = "14020901",
    totalPayment = 1_850_000L,
    paymentDeadLine = "14021001",
    workshopInfo = WorkshopIdInfoPR(
        workshopRegisterDate = "14020901",
        workshopId = "14020901",
        brhCode = "6400",
    ),
)

private val PreviewSheets = kotlinx.collections.immutable.persistentListOf(
    PaymentSheetConstructionFilePR(
        orderNumber = "1",
        paymentCode = "36001234500",
        paymentSheetAmount = 500_000L,
        status = "پرداخت شده",
        paymentDate = "14021015",
        buildingRequest = PreviewBuildingRequest,
    ),
    PaymentSheetConstructionFilePR(
        orderNumber = "2",
        paymentCode = "36001234501",
        paymentSheetAmount = 1_000_000L,
        status = "در انتظار پرداخت",
        paymentDate = null,
        buildingRequest = PreviewBuildingRequest,
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
private fun RequestSummaryCardExpandedPreview() {
    PreviewRtlThemeContent {
        RequestSummaryCard(
            items = PreviewBuildingRequest.getRequestInfo(),
            initiallyExpanded = true,
            modifier = Modifier.padding(Spacing.lg),
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
