package com.tamin.taminhamrah.feature.retirementPension.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementDocumentPR
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementDocumentType
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadCard
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadState
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_alert_circle
import taminx.core.core_ui.retirement_pension_document_id_description_page
import taminx.core.core_ui.retirement_pension_document_id_description_page_hint
import taminx.core.core_ui.retirement_pension_document_id_first_page
import taminx.core.core_ui.retirement_pension_document_id_first_page_hint
import taminx.core.core_ui.retirement_pension_document_missing
import taminx.core.core_ui.retirement_pension_document_quit_letter
import taminx.core.core_ui.retirement_pension_document_quit_letter_hint
import taminx.core.core_ui.retirement_pension_document_quit_letter_missing
import taminx.core.core_ui.retirement_pension_document_uploaded
import taminx.core.core_ui.retirement_pension_document_uploading
import taminx.core.core_ui.retirement_pension_identity_documents_note
import taminx.core.core_ui.retirement_pension_quit_letter_note

/** The label the design gives each document, and the sentence under it before anything is picked. */
@Composable
internal fun RetirementDocumentType.label(): String = stringResource(
    when (this) {
        RetirementDocumentType.IdFirstPage -> Res.string.retirement_pension_document_id_first_page
        RetirementDocumentType.IdDescriptionPage ->
            Res.string.retirement_pension_document_id_description_page
        RetirementDocumentType.QuitLetter -> Res.string.retirement_pension_document_quit_letter
    },
)

@Composable
private fun RetirementDocumentType.hint(): StringResource = when (this) {
    RetirementDocumentType.IdFirstPage -> Res.string.retirement_pension_document_id_first_page_hint
    RetirementDocumentType.IdDescriptionPage ->
        Res.string.retirement_pension_document_id_description_page_hint
    RetirementDocumentType.QuitLetter -> Res.string.retirement_pension_document_quit_letter_hint
}

/**
 * Steps 6 and 7 — one composable, because the two steps differ only in which documents they ask for
 * and the sentence above them.
 *
 * The complaint for a missing document is drawn under its own card, never in the shared line at the
 * bottom: the design points at the card, and saying it twice reads as two problems.
 */
@Composable
internal fun RetirementDocumentsStep(
    types: ImmutableList<RetirementDocumentType>,
    documents: ImmutableMap<RetirementDocumentType, RetirementDocumentPR>,
    missing: (RetirementDocumentType) -> Boolean,
    isIdentityStep: Boolean,
    onDocumentClick: (RetirementDocumentType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    RetirementStepColumn(modifier = modifier) {
        TaminText(
            text = stringResource(
                if (isIdentityStep) {
                    Res.string.retirement_pension_identity_documents_note
                } else {
                    Res.string.retirement_pension_quit_letter_note
                },
            ),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textTertiary,
        )

        types.forEach { type ->
            RetirementDocumentRow(
                type = type,
                document = documents[type],
                isMissing = missing(type),
                onClick = { onDocumentClick(type) },
            )
        }
    }
}

@Composable
private fun RetirementDocumentRow(
    type: RetirementDocumentType,
    document: RetirementDocumentPR?,
    isMissing: Boolean,
    onClick: () -> Unit,
) {
    val label = type.label()
    val state = when {
        document?.isUploading == true -> TaminDocumentUploadState.Uploading
        document?.isUploaded == true -> TaminDocumentUploadState.Uploaded
        document?.hasFailed == true || isMissing -> TaminDocumentUploadState.Failed
        else -> TaminDocumentUploadState.Empty
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        TaminDocumentUploadCard(
            title = label,
            state = state,
            statusText = when (state) {
                TaminDocumentUploadState.Uploaded ->
                    stringResource(Res.string.retirement_pension_document_uploaded)
                TaminDocumentUploadState.Uploading ->
                    stringResource(Res.string.retirement_pension_document_uploading)
                else -> stringResource(type.hint())
            },
            onCardClick = onClick,
        )

        if (isMissing) {
            RetirementDocumentError(
                message = if (type == RetirementDocumentType.QuitLetter) {
                    stringResource(Res.string.retirement_pension_document_quit_letter_missing)
                } else {
                    stringResource(Res.string.retirement_pension_document_missing, label)
                },
            )
        }
    }
}

@Composable
private fun RetirementDocumentError(message: String) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.xs, start = Spacing.xs, end = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_alert_circle),
            contentDescription = null,
            tint = colors.dangerText,
            modifier = Modifier.size(IconSize.statIcon + Spacing.xs),
        )
        TaminText(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = colors.dangerText,
            modifier = Modifier.weight(1f),
        )
    }
}
