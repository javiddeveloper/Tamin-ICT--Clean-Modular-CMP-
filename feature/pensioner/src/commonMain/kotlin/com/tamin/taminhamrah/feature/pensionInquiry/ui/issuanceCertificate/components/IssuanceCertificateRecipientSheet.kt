package com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.components

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.model.certificate.RecipientPR
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminSearchableListSheet
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.issuance_certificate_recipients_empty
import taminx.core.core_ui.issuance_certificate_search_placeholder
import taminx.core.core_ui.issuance_certificate_select_recipient_title

@Composable
internal fun IssuanceCertificateRecipientSheet(
    searchQuery: String,
    isLoading: Boolean,
    recipients: ImmutableList<RecipientPR>,
    onSearchQueryChange: (String) -> Unit,
    onRecipientSelected: (RecipientPR) -> Unit,
    onDismiss: () -> Unit,
) {
    TaminSearchableListSheet(
        title = stringResource(Res.string.issuance_certificate_select_recipient_title),
        items = recipients,
        itemLabel = { it.name },
        itemKey = { it.code },
        onItemSelected = onRecipientSelected,
        onDismiss = onDismiss,
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        searchPlaceholder = stringResource(Res.string.issuance_certificate_search_placeholder),
        isLoading = isLoading,
        emptyMessage = stringResource(Res.string.issuance_certificate_recipients_empty),
    )
}

