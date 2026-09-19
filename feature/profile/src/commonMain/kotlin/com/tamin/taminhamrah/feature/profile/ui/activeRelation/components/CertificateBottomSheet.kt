package com.tamin.taminhamrah.feature.profile.ui.activeRelation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationUiState
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.active_relation_issue_and_send
import taminx.core.core_ui.active_relation_recipient_branch_placeholder
import taminx.core.core_ui.active_relation_select_recipient
import taminx.core.core_ui.active_relation_send_certificate
import taminx.core.core_ui.ic_arrow_down

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CertificateBottomSheet(
    state: ActiveRelationUiState,
    onRecipientClick: () -> Unit,
    onBranchNameChange: (String) -> Unit,
    onIssueClick: () -> Unit,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .padding(bottom = Spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TaminText(
                text = stringResource(Res.string.active_relation_send_certificate),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            StyledTextField(
                value = state.selectedRecipient?.name?:"",
                onValueChange = {},
                trailingIcon = Icons.Default.KeyboardArrowDown,
                readOnly = true,
                onClick = { onRecipientClick() },
                label = "",
                placeholder = stringResource(Res.string.active_relation_select_recipient)
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            StyledTextField(
                value = state.branchName,
                onValueChange = onBranchNameChange,
                label = "",
                placeholder = stringResource(Res.string.active_relation_recipient_branch_placeholder),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            LoadingButton(
                text = stringResource(Res.string.active_relation_issue_and_send),
                onClick = onIssueClick,
                enabled = state.selectedRecipient != null && state.branchName.isNotBlank(),
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
