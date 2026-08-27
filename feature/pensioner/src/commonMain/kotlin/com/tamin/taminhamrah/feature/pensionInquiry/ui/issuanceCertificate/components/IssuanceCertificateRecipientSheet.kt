package com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.certificate.RecipientPR
import com.tamin.taminhamrah.ui.components.CustomSearchBar
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.issuance_certificate_recipients_empty
import taminx.core.core_ui.issuance_certificate_search_placeholder
import taminx.core.core_ui.issuance_certificate_select_recipient_title

private val ListHeight = 400.dp
private val LoadingHeight = 200.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun IssuanceCertificateRecipientSheet(
    searchQuery: String,
    isLoading: Boolean,
    recipients: ImmutableList<RecipientPR>,
    onSearchQueryChange: (String) -> Unit,
    onRecipientSelected: (RecipientPR) -> Unit,
    onDismiss: () -> Unit,
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
                    .background(taminColors.border, RoundedCornerShape(50)),
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.issuance_certificate_select_recipient_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
                modifier = Modifier.padding(Spacing.lg),
            )

            CustomSearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                placeHolder = stringResource(Res.string.issuance_certificate_search_placeholder),
                modifier = Modifier.padding(horizontal = Spacing.lg),
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxWidth().height(LoadingHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = taminColors.blueText)
                }

                recipients.isEmpty() -> TaminEmptyState(
                    message = stringResource(Res.string.issuance_certificate_recipients_empty),
                )

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ListHeight)
                        .navigationBarsPadding(),
                    contentPadding = PaddingValues(bottom = Spacing.xxl),
                ) {
                    items(recipients, key = { it.code }) { recipient ->
                        RecipientRow(
                            recipient = recipient,
                            onClick = { onRecipientSelected(recipient) },
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = Spacing.lg),
                            thickness = 0.5.dp,
                            color = taminColors.border,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipientRow(
    recipient: RecipientPR,
    onClick: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = recipient.name,
            style = MaterialTheme.typography.bodyMedium,
            color = taminColors.textPrimary,
        )
    }
}
