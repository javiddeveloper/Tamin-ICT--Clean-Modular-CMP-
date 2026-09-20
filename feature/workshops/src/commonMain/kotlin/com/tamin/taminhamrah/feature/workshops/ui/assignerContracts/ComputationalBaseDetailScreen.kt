package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsIntent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.DocumentFailure
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.DocumentPreview
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.BaseDocumentCategory
import com.tamin.taminhamrah.model.workshop.BaseDocumentKind
import com.tamin.taminhamrah.model.workshop.BaseDocumentPR
import com.tamin.taminhamrah.model.workshop.ComputationalBasePR
import com.tamin.taminhamrah.model.workshop.ComputationalBaseStatus
import com.tamin.taminhamrah.model.workshop.label
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemBadge
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminImageViewer
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_base_detail_header
import taminx.core.core_ui.assigner_base_detail_title
import taminx.core.core_ui.assigner_base_document_badge
import taminx.core.core_ui.assigner_base_period
import taminx.core.core_ui.assigner_bases_empty_body
import taminx.core.core_ui.assigner_bases_empty_title
import taminx.core.core_ui.assigner_document_open_failed
import taminx.core.core_ui.assigner_document_unavailable
import taminx.core.core_ui.assigner_documents_empty
import taminx.core.core_ui.assigner_documents_title
import taminx.core.core_ui.assigner_field_amount
import taminx.core.core_ui.assigner_field_estimated_order
import taminx.core.core_ui.assigner_field_final_order
import taminx.core.core_ui.assigner_field_letter_number
import taminx.core.core_ui.assigner_field_period
import taminx.core.core_ui.assigner_field_status
import taminx.core.core_ui.ic_tamin_computational_base
import taminx.core.core_ui.ic_tamin_document_image
import taminx.core.core_ui.ic_tamin_workshop_contract_rows
import taminx.core.core_ui.settlement_value_missing

/**
 * جزئیات مبنا — one مبنای محاسباتی's figures, then the documents filed with it.
 *
 * The documents arrived with the base row, so the section is populated without a request; only
 * *opening* one fetches, and each kind goes to a different endpoint and a different viewer.
 */
@Composable
fun ComputationalBaseDetailScreen(
    viewModel: AssignerContractsViewModel,
    letterNumber: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Found in the list this screen was opened from, by the number its route carries. Null only
    // after process death, when that list was never fetched in this process.
    val bases = state.bases.items
    val base = remember(bases, letterNumber) {
        bases.firstOrNull { it.letterNumber == letterNumber }
    }

    ComputationalBaseDetailContent(
        base = base,
        openingDocumentId = state.openingDocumentId,
        failure = state.documentFailure,
        preview = state.preview,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun ComputationalBaseDetailContent(
    base: ComputationalBasePR?,
    openingDocumentId: String?,
    failure: DocumentFailure?,
    preview: DocumentPreview?,
    onIntent: (AssignerContractsIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // «سند N · دوره», or «سند N» alone when the service sent no period.
    val subtitle = if (base != null) {
        val badge = stringResource(Res.string.assigner_base_document_badge, base.letterNumber)
        val period = stringResource(Res.string.assigner_base_period, base.periodStart, base.periodEnd)
        val withPeriod = stringResource(Res.string.assigner_base_detail_header, base.letterNumber, period)
        if (base.periodStart.isNotBlank() && base.periodEnd.isNotBlank()) withPeriod else badge
    } else {
        null
    }
    WorkshopScreenShell(
        title = stringResource(Res.string.assigner_base_detail_title),
        onBack = onBack,
        subtitle = subtitle,
        modifier = modifier,
    ) {
        if (base == null) {
            EmptyStateMessage(
                icon = vectorResource(Res.drawable.ic_tamin_computational_base),
                title = stringResource(Res.string.assigner_bases_empty_title),
                subtitle = stringResource(Res.string.assigner_bases_empty_body),
                showIconTile = true,
            )
            return@WorkshopScreenShell
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(WorkshopDimens.listContentPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.smd),
        ) {
            BaseFiguresCard(
                status = base.status,
                letterNumber = base.letterNumber,
                periodStart = base.periodStart,
                periodEnd = base.periodEnd,
                amount = base.amount,
                finalOrderNumber = base.finalOrderNumber,
                estimatedOrderNumber = base.estimatedOrderNumber,
            )

            Text(
                text = stringResource(Res.string.assigner_documents_title),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = LocalTaminColors.current.textSecondary,
                modifier = Modifier.padding(top = Spacing.sm),
            )
            if (base.documents.isEmpty()) {
                Text(
                    text = stringResource(Res.string.assigner_documents_empty),
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalTaminColors.current.textMuted,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                DocumentList(
                    documents = base.documents,
                    openingDocumentId = openingDocumentId,
                    failure = failure,
                    onOpen = { onIntent(AssignerContractsIntent.DocumentTapped(it)) },
                )
            }
        }
    }

    if (preview != null) {
        DocumentViewer(
            preview = preview,
            onRetry = { onIntent(AssignerContractsIntent.RetryDocument) },
            onDismiss = { onIntent(AssignerContractsIntent.PreviewDismissed) },
        )
    }
}

/**
 * The base's وضعیت, then شمارهٔ سند, دورهٔ کارکرد and مبلغ کارکرد as the design's card lists them, and
 * the two debt orders the old app lists under them.
 *
 * وضعیت leads and is the one emphasized value, as the old app sets it in bold: it is what the user
 * opens a base to find out — whether it was rejected, is waiting on a debt order, or has been settled.
 * The period row is left out when the service sent no period, rather than printing a dash under a
 * label that promises one.
 */
@Composable
private fun BaseFiguresCard(
    status: ComputationalBaseStatus?,
    letterNumber: String,
    periodStart: String,
    periodEnd: String,
    amount: String,
    finalOrderNumber: String,
    estimatedOrderNumber: String,
    modifier: Modifier = Modifier,
) {
    val hasPeriod = periodStart.isNotBlank() && periodEnd.isNotBlank()
    WorkshopRecordCard(modifier = modifier) {
        DetailRow(
            label = stringResource(Res.string.assigner_field_status),
            value = status?.let { stringResource(it.title) } ?: stringResource(Res.string.settlement_value_missing),
            valueStyle = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.assigner_field_letter_number),
            value = letterNumber,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        if (hasPeriod) {
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.assigner_field_period),
                value = stringResource(Res.string.assigner_base_period, periodStart, periodEnd),
                numeric = false,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
        }
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.assigner_field_amount),
            value = amount,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.assigner_field_final_order),
            value = finalOrderNumber,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.assigner_field_estimated_order),
            value = estimatedOrderNumber,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
    }
}

/**
 * The attached documents, one row each.
 *
 * Drawn through the shared [ListGroupView]: the design's row is an icon tile, a name, a kind under
 * it and a chevron — a list row, not a new component. A row whose fetch is in flight is disabled,
 * which is what stops a second tap and says the first one landed.
 *
 * A document that is not there at all is badged «در دسترس نیست» and stops responding, the same
 * way پروفایل badges its own rows. That is only for the permanent case — see [DocumentFailure].
 */
@Composable
private fun DocumentList(
    documents: ImmutableList<BaseDocumentPR>,
    openingDocumentId: String?,
    failure: DocumentFailure?,
    onOpen: (BaseDocumentPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val genericFailure = stringResource(Res.string.assigner_document_open_failed)
    val unavailableLabel = stringResource(Res.string.assigner_document_unavailable)
    val imageIcon = painterResource(Res.drawable.ic_tamin_document_image)
    val pdfIcon = painterResource(Res.drawable.ic_tamin_workshop_contract_rows)
    // Resolved outside the loop: `stringResource` in a `forEach` would be a composable call whose
    // count changes with the list, and these two are the only kind labels there are.
    val imageLabel = stringResource(BaseDocumentKind.IMAGE.label)
    val pdfLabel = stringResource(BaseDocumentKind.PDF.label)

    // Every heading resolved once, by category rather than by row: `stringResource` inside a loop
    // over the documents would be a composable call whose count follows the data, and the list it
    // built would be a fresh instance on every recomposition.
    val letter = stringResource(BaseDocumentCategory.LETTER.title)
    val subcontractor = stringResource(BaseDocumentCategory.SUBCONTRACTOR.title)
    val supplement = stringResource(BaseDocumentCategory.SUPPLEMENT.title)
    val finalStatus = stringResource(BaseDocumentCategory.FINAL_STATUS.title)
    val other = stringResource(BaseDocumentCategory.OTHER.title)
    val titles = remember(letter, subcontractor, supplement, finalStatus, other) {
        mapOf(
            BaseDocumentCategory.LETTER to letter,
            BaseDocumentCategory.SUBCONTRACTOR to subcontractor,
            BaseDocumentCategory.SUPPLEMENT to supplement,
            BaseDocumentCategory.FINAL_STATUS to finalStatus,
            BaseDocumentCategory.OTHER to other,
        )
    }

    val items = remember(
        documents, titles, openingDocumentId, failure, genericFailure, unavailableLabel,
        imageIcon, pdfIcon, imageLabel, pdfLabel, colors, onOpen,
    ) {
        documents.map { document ->
            val isImage = document.kind == BaseDocumentKind.IMAGE
            val didFail = failure?.documentId == document.documentId
            // Two ways to be unavailable, and both are permanent: the base named a document with
            // no id at all — nothing to ask for, known before any tap — or the service answered
            // that it does not hold it. Either way another tap can only fail the same way.
            val isUnavailable =
                document.documentId.isBlank() || (didFail && failure.isMissing)
            ListItemData(
                title = titles.getValue(document.category),
                // The kind normally; the reason the fetch failed once it has. The service's own
                // words when it gave any — `upload-image` names the id it could not find — and the
                // generic line only when it failed without saying why. An unavailable row says it
                // in the badge instead, so the line under the name is not saying it twice.
                subtitle = when {
                    isUnavailable -> null
                    didFail -> failure.message?.takeIf { it.isNotBlank() } ?: genericFailure
                    // «فایل PDF · شناسه», as the design names each attachment.
                    else -> (if (isImage) imageLabel else pdfLabel) + DOCUMENT_ID_SEPARATOR + document.documentId
                },
                // The same badge the profile hangs off a row: what the row *is*, without making
                // the user tap it to find out.
                badge = if (isUnavailable) {
                    ListItemBadge(
                        text = unavailableLabel,
                        backgroundColor = colors.dangerBorder,
                        textColor = colors.dangerText,
                    )
                } else {
                    null
                },
                leadingIconPainter = if (isImage) imageIcon else pdfIcon,
                leadingIconShape = DocumentIconShape,
                // Nothing to open, so nothing to point at.
                showArrow = !isUnavailable,
                // Disabled while its own fetch is in flight, and while another one is: two
                // downloads at once is not a state this screen has anything to say about. A row
                // that merely failed stays live, because tapping it again is the retry; one that
                // is *unavailable* does not, because it cannot succeed.
                enabled = openingDocumentId == null && !isUnavailable,
                onClick = { onOpen(document) },
                // An image in blue, a PDF in red — the design's two tiles. The PDF tile takes
                // dangerBorder: the theme's dangerBg is the plain surface in light mode, and would leave
                // the glyph with no tile at all; dangerBorder carries the design's own #FDECEC.
                colors = ListItemColors(
                    subtitleColor = if (didFail) colors.dangerText else Color.Unspecified,
                    leadingIconBackgroundColor = if (isImage) colors.blueBg else colors.dangerBorder,
                    leadingIconTintColor = if (isImage) colors.blueText else colors.dangerText,
                ),
            )
        }.toImmutableList()
    }

    ListGroupView(
        items = items,
        showDividers = false,
        itemContentPadding = DocumentRowPadding,
        modifier = modifier.fillMaxWidth(),
    )
}

/** Whichever of the two viewers the open document's kind names. */
@Composable
private fun DocumentViewer(
    preview: DocumentPreview,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    val title = stringResource(preview.title)
    when (preview.kind) {
        // Blank while the bytes are still coming, which is what makes the viewer wait rather than
        // draw a broken-image placeholder — the same three states the PDF viewer has.
        BaseDocumentKind.IMAGE -> TaminImageViewer(
            title = title,
            // Base64 straight from `upload-image`; the async loader decodes it.
            url = preview.imageData,
            downloadFailed = preview.didFail,
            isLoading = preview.imageData.isBlank() && !preview.didFail,
            onDismiss = onDismiss,
        )

        BaseDocumentKind.PDF -> TaminPdfViewer(
            fileName = "$PdfFilePrefix${preview.documentId}$PdfFileSuffix",
            // Null while the bytes are still coming, which is what makes the viewer wait rather
            // than declare the document empty.
            pdf = preview.pdf,
            downloadFailed = preview.didFail,
            onRequestDownload = onRetry,
            onDismiss = onDismiss,
            title = title,
            showEmptyStateTile = true,
        )
    }
}

/** Joins a document's kind to its id — the same separator the rest of the feature's lines use. */
private const val DOCUMENT_ID_SEPARATOR = " · "

private const val PdfFilePrefix = "computational_base_"
private const val PdfFileSuffix = ".pdf"

/** `border-radius:11px` on the design's 34px document glyph tile. */
private val DocumentIconShape = RoundedCornerShape(CornerRadius.md)

/** `padding:9px 11px` inside the design's document row. */
private val DocumentRowPadding = PaddingValues(horizontal = Spacing.smd, vertical = Spacing.sm)

// ------------------------------------------------------------------------------- previews

private val PreviewBase = ComputationalBasePR(
    letterNumber = "۱۲۰۴۴",
    sendDate = "۱۴۰۰/۱۲/۱۵",
    amount = "۸۴,۰۰۰,۰۰۰ ریال",
    amountRials = 84_000_000L,
    periodStart = "۱۴۰۰/۰۷/۰۱",
    periodEnd = "۱۴۰۰/۰۹/۳۰",
    documentCount = "۲",
    status = ComputationalBaseStatus.FINAL_ORDER_ISSUED,
    finalOrderNumber = "۴۵۱۲۰۰۹",
    estimatedOrderNumber = "—",
    documents = persistentListOf(
        BaseDocumentPR(
            documentId = "img-1",
            kind = BaseDocumentKind.IMAGE,
            category = BaseDocumentCategory.LETTER,
        ),
        BaseDocumentPR(
            documentId = "pdf-1",
            kind = BaseDocumentKind.PDF,
            category = BaseDocumentCategory.FINAL_STATUS,
        ),
    ),
)

@PreviewRtlTheme
@Composable
private fun ComputationalBaseDetailPreview() = PreviewRtlThemeContent {
    ComputationalBaseDetailContent(
        base = PreviewBase,
        openingDocumentId = null,
        failure = null,
        preview = null,
        onIntent = {},
        onBack = {},
    )
}

/** A مبنا filed with nothing attached, and no period sent — its own sentence, and no period row. */
@PreviewRtlTheme
@Composable
private fun ComputationalBaseDetailNoDocumentsPreview() = PreviewRtlThemeContent {
    ComputationalBaseDetailContent(
        base = PreviewBase.copy(
            documents = persistentListOf(),
            documentCount = "۰",
            periodStart = "",
            periodEnd = "",
        ),
        openingDocumentId = null,
        failure = null,
        preview = null,
        onIntent = {},
        onBack = {},
    )
}

/** A document being fetched — every row refuses a second tap while one is in flight. */
@PreviewRtlTheme
@Composable
private fun ComputationalBaseDetailOpeningPreview() = PreviewRtlThemeContent {
    ComputationalBaseDetailContent(
        base = PreviewBase,
        openingDocumentId = "img-1",
        failure = null,
        preview = null,
        onIntent = {},
        onBack = {},
    )
}
