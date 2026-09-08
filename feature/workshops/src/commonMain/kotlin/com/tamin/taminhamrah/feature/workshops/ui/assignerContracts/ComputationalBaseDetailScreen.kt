package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsIntent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.DocumentFailure
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.DocumentPreview
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.BaseDocumentCategory
import com.tamin.taminhamrah.model.workshop.BaseDocumentKind
import com.tamin.taminhamrah.model.workshop.BaseDocumentPR
import com.tamin.taminhamrah.model.workshop.ComputationalBasePR
import com.tamin.taminhamrah.model.workshop.label
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminImageViewer
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Duration
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_base_detail_subtitle
import taminx.core.core_ui.assigner_base_detail_title
import taminx.core.core_ui.assigner_bases_empty_body
import taminx.core.core_ui.assigner_bases_empty_title
import taminx.core.core_ui.assigner_documents_empty
import taminx.core.core_ui.assigner_document_open_failed
import taminx.core.core_ui.assigner_documents_title
import taminx.core.core_ui.ic_tamin_chevron_down
import taminx.core.core_ui.ic_tamin_computational_base
import taminx.core.core_ui.ic_tamin_document_image
import taminx.core.core_ui.ic_tamin_workshop_contract_rows

/**
 * جزئیات مبنا — one مبنای محاسباتی, its amount, and the documents filed with it.
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
    WorkshopScreenShell(
        title = stringResource(Res.string.assigner_base_detail_title),
        onBack = onBack,
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
        ) {
            BaseSummaryCard(
                letterNumber = base.letterNumber,
                sendDate = base.sendDate,
                amount = base.amount,
                documents = base.documents,
                openingDocumentId = openingDocumentId,
                failure = failure,
                onOpenDocument = { onIntent(AssignerContractsIntent.DocumentTapped(it)) },
            )
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
 * The one card جزئیات مبنا is built around: the سند line, the amount, then the documents.
 *
 * Takes the base's fields rather than the base, so the collapsible section below does not redraw
 * because an unrelated field of the state changed.
 */
@Composable
private fun BaseSummaryCard(
    letterNumber: String,
    sendDate: String,
    amount: String,
    documents: ImmutableList<BaseDocumentPR>,
    openingDocumentId: String?,
    failure: DocumentFailure?,
    onOpenDocument: (BaseDocumentPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    // Open on arrival: the documents are the reason this screen exists, and they arrived with
    // the row, so collapsing them behind a tap hides the whole point of the page.
    var isExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(WorkshopDimens.cardCorner)
            .padding(
                horizontal = WorkshopDimens.cardHorizontalPadding,
                vertical = WorkshopDimens.cardTopPadding,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(
                    Res.string.assigner_base_detail_subtitle,
                    letterNumber,
                    sendDate,
                ),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            NumericText(
                text = amount,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = colors.blueText,
            )
        }

        DocumentsToggle(
            isExpanded = isExpanded,
            onToggle = { isExpanded = !isExpanded },
            modifier = Modifier.padding(top = Spacing.smd),
        )

        if (isExpanded) {
            if (documents.isEmpty()) {
                Text(
                    text = stringResource(Res.string.assigner_documents_empty),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.smd),
                )
            } else {
                DocumentList(
                    documents = documents,
                    openingDocumentId = openingDocumentId,
                    failure = failure,
                    onOpen = onOpenDocument,
                    modifier = Modifier.padding(top = Spacing.smd),
                )
            }
        }
    }
}

/** «مستندات پیوست‌شده» and the chevron that turns over when the section opens. */
@Composable
private fun DocumentsToggle(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) ChevronOpenDegrees else 0f,
        animationSpec = tween(Duration.fast),
        label = "documentsChevron",
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            // `border-top:1px dashed` in the design — the one rule that separates the amount from
            // its attachments.
            .drawBehind {
                drawLine(
                    color = colors.divider,
                    start = Offset.Zero,
                    end = Offset(size.width, 0f),
                    strokeWidth = DashedRuleWidth.toPx(),
                    pathEffect = DashedRule,
                )
            }
            .padding(top = Spacing.smd),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.assigner_documents_title),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_down),
            contentDescription = null,
            tint = colors.chevron,
            // Read inside graphicsLayer, so a frame of the turn costs no recomposition.
            modifier = Modifier
                .size(IconSize.small)
                .graphicsLayer { rotationZ = rotation },
        )
    }
}

/**
 * The attached documents, one row each.
 *
 * Drawn through the shared [ListGroupView]: the design's row is an icon tile, a name, a kind under
 * it and a chevron — a list row, not a new component. A row whose fetch is in flight is disabled,
 * which is what stops a second tap and says the first one landed.
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
        documents, titles, openingDocumentId, failure, genericFailure,
        imageIcon, pdfIcon, imageLabel, pdfLabel, colors, onOpen,
    ) {
        documents.map { document ->
            val isImage = document.kind == BaseDocumentKind.IMAGE
            val didFail = failure?.documentId == document.documentId
            ListItemData(
                title = titles.getValue(document.category),
                // The kind normally; the reason the fetch failed once it has. The service's own
                // words when it gave any — `upload-image` names the id it could not find — and the
                // generic line only when it failed without saying why.
                subtitle = when {
                    !didFail -> if (isImage) imageLabel else pdfLabel
                    else -> failure.message?.takeIf { it.isNotBlank() } ?: genericFailure
                },
                leadingIconPainter = if (isImage) imageIcon else pdfIcon,
                leadingIconShape = DocumentIconShape,
                // Disabled while its own fetch is in flight, and while another one is: two
                // downloads at once is not a state this screen has anything to say about. A row
                // that failed stays live, because tapping it again is the retry.
                enabled = openingDocumentId == null,
                onClick = { onOpen(document) },
                colors = if (didFail) {
                    ListItemColors(subtitleColor = colors.dangerText)
                } else {
                    ListItemColors()
                },
            )
        }.toImmutableList()
    }

    ListGroupView(
        items = items,
        containerBackgroundColor = colors.bgPage,
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
        )
    }
}

/** `transform:rotate(180deg)` once the documents section is open. */
private const val ChevronOpenDegrees = 180f

/** Hoisted: a path effect allocated per frame is a path effect allocated for nothing. */
private val DashedRule = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
private val DashedRuleWidth = 1.dp

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
    documentCount = "۲",
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

/** A مبنا filed with nothing attached — the section opens onto its own sentence, not a gap. */
@PreviewRtlTheme
@Composable
private fun ComputationalBaseDetailNoDocumentsPreview() = PreviewRtlThemeContent {
    ComputationalBaseDetailContent(
        base = PreviewBase.copy(documents = persistentListOf(), documentCount = "۰"),
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
