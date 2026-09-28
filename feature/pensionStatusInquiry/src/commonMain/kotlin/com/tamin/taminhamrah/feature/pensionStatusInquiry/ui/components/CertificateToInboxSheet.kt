package com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryIntent
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryUiState
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.TaminTextField
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminSearchableListSheet
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.active_relation_issue_and_send
import taminx.core.core_ui.active_relation_search_placeholder
import taminx.core.core_ui.active_relation_select_recipient_title
import taminx.core.core_ui.active_relation_send_certificate
import taminx.core.core_ui.deferred_installment_branch_label
import taminx.core.core_ui.issuance_certificate_branch_name_placeholder
import taminx.core.core_ui.issuance_certificate_error_select_recipient
import taminx.core.core_ui.no_items_found

/** Port of legacy `CertificateToInboxInquireDialogFragment`. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CertificateToInboxSheet(
    state: PensionStatusInquiryUiState,
    onIntent: (PensionStatusInquiryIntent) -> Unit,
) {
    val colors = LocalTaminColors.current

    ModalBottomSheet(
        onDismissRequest = { onIntent(PensionStatusInquiryIntent.DismissCertificateSheet) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgSurface,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.active_relation_send_certificate),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(Spacing.lg))

            PickerRow(
                text = state.selectedRecipient?.name
                    ?: stringResource(Res.string.deferred_installment_branch_label),
                isPlaceholder = state.selectedRecipient == null,
                isError = state.showRecipientError,
                showChevron = true,
                onClick = { onIntent(PensionStatusInquiryIntent.OnSelectRecipientClicked) },
            )
            if (state.showRecipientError) {
                Text(
                    text = stringResource(Res.string.issuance_certificate_error_select_recipient),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.dangerText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.xxs, start = Spacing.sm),
                )
            }
            Spacer(Modifier.height(Spacing.sm))

            TaminTextField(
                value = state.branchName,
                onValueChange = { onIntent(PensionStatusInquiryIntent.OnBranchNameChanged(it)) },
                placeholder = stringResource(Res.string.issuance_certificate_branch_name_placeholder),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.lg))

            LoadingButton(
                text = stringResource(Res.string.active_relation_issue_and_send),
                onClick = { onIntent(PensionStatusInquiryIntent.OnIssueCertificateClicked) },
                isLoading = state.isSendingCertificate,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (state.showRecipientsSheet) {
        TaminSearchableListSheet(
            title = stringResource(Res.string.active_relation_select_recipient_title),
            items = state.filteredRecipients,
            itemLabel = { it.name },
            itemKey = { it.code },
            onItemSelected = { onIntent(PensionStatusInquiryIntent.OnRecipientSelected(it)) },
            onDismiss = { onIntent(PensionStatusInquiryIntent.DismissRecipientsSheet) },
            searchQuery = state.recipientSearchQuery,
            onSearchQueryChange = { onIntent(PensionStatusInquiryIntent.OnRecipientSearchChanged(it)) },
            searchPlaceholder = stringResource(Res.string.active_relation_search_placeholder),
            isLoading = state.isLoadingRecipients,
            emptyMessage = stringResource(Res.string.no_items_found),
        )
    }
}
