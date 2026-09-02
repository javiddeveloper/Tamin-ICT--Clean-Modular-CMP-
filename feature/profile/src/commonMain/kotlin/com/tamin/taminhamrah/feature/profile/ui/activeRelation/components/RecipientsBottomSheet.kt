package com.tamin.taminhamrah.feature.profile.ui.activeRelation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationUiState
import com.tamin.taminhamrah.model.certificate.RecipientPR
import com.tamin.taminhamrah.ui.components.CustomSearchBar
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerCardList
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.active_relation_search_placeholder
import taminx.core.core_ui.active_relation_select_recipient_title

/** Rows the picker stands in with while the recipients load. */
private const val LoadingPlaceholderRows = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecipientsBottomSheet(
    state: ActiveRelationUiState,
    onSearchQueryChange: (String) -> Unit,
    onRecipientSelected: (RecipientPR) -> Unit,
    onDismiss: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = taminColors.bgSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = Spacing.md)
                    .size(width = 32.dp, height = 4.dp)
                    .background(taminColors.border, RoundedCornerShape(50))
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TaminText(
                text = stringResource(Res.string.active_relation_select_recipient_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
                modifier = Modifier.padding(Spacing.lg)
            )

            CustomSearchBar(
                query = state.searchQuery,
                onQueryChange = onSearchQueryChange,
                placeHolder = stringResource(Res.string.active_relation_search_placeholder),
                modifier = Modifier.padding(horizontal = Spacing.lg)
            )

            if (state.isLoadingRecipients) {
                ShimmerCardList(
                    count = LoadingPlaceholderRows,
                    cardHeight = ShimmerSize.rowHeight,
                    cornerRadius = CornerRadius.md,
                    spacing = Spacing.sm,
                    contentPadding = PaddingValues(Spacing.lg)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentPadding = PaddingValues(bottom = Spacing.xxl)
                ) {
                    items(state.filteredRecipients) { recipient ->
                        RecipientItem(
                            recipient = recipient,
                            onClick = { onRecipientSelected(recipient) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = Spacing.lg),
                            thickness = 0.5.dp,
                            color = taminColors.border
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipientItem(
    recipient: RecipientPR,
    onClick: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(Spacing.lg),
        contentAlignment = Alignment.CenterStart
    ) {
        TaminText(
            text = recipient.name,
            style = MaterialTheme.typography.bodyMedium,
            color = taminColors.textPrimary
        )
    }
}
