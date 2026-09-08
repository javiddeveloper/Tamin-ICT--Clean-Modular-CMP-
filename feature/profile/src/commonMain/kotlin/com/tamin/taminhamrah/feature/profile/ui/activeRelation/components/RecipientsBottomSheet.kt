package com.tamin.taminhamrah.feature.profile.ui.activeRelation.components

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationUiState
import com.tamin.taminhamrah.model.certificate.RecipientPR
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminSearchableListSheet
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.active_relation_search_placeholder
import taminx.core.core_ui.active_relation_select_recipient_title
import taminx.core.core_ui.no_items_found

@Composable
internal fun RecipientsBottomSheet(
    state: ActiveRelationUiState,
    onSearchQueryChange: (String) -> Unit,
    onRecipientSelected: (RecipientPR) -> Unit,
    onDismiss: () -> Unit
) {
    TaminSearchableListSheet(
        title = stringResource(Res.string.active_relation_select_recipient_title),
        items = state.filteredRecipients,
        itemLabel = { it.name },
        itemKey = { it.code },
        onItemSelected = onRecipientSelected,
        onDismiss = onDismiss,
        searchQuery = state.searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        searchPlaceholder = stringResource(Res.string.active_relation_search_placeholder),
        isLoading = state.isLoadingRecipients,
        emptyMessage = stringResource(Res.string.no_items_found),
    )
}

