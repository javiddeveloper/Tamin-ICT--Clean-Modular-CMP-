package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.disability_pension_registered_request_item_title
import taminx.core.core_ui.disability_pension_registered_request_verdict_prefix
import taminx.core.core_ui.disability_pension_registered_requests_empty
import taminx.core.core_ui.disability_pension_registered_requests_sheet_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisabilityPensionRegisteredRequestsSheet(
    isLoading: Boolean,
    requests: ImmutableList<RegisteredMedicalCommissionPR>,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = Spacing.md)
                    .size(width = 32.dp, height = 4.dp)
                    .background(colors.border, RoundedCornerShape(50)),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .navigationBarsPadding()
                .padding(bottom = Spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(Spacing.smd),
        ) {
            Text(
                text = stringResource(Res.string.disability_pension_registered_requests_sheet_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )

            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xxl),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
                requests.isEmpty() -> TaminEmptyState(
                    message = stringResource(Res.string.disability_pension_registered_requests_empty),
                )
                else -> requests.forEach { request ->
                    RegisteredRequestItem(request = request)
                }
            }
        }
    }
}

@Composable
private fun RegisteredRequestItem(request: RegisteredMedicalCommissionPR) {
    val colors = LocalTaminColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface(
                cornerRadius = Spacing.lg
            )
            .padding(Spacing.smd),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = stringResource(Res.string.disability_pension_registered_request_item_title),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            NumericText(
                text = PersianDateFormatter.formatTimestamp(request.demandSaveDate),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
        }
        Text(
            text = stringResource(Res.string.disability_pension_registered_request_verdict_prefix, request.verdictDescription),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun DisabilityPensionRegisteredRequestsSheetPreview() {
    PreviewRtlThemeContent {
        DisabilityPensionRegisteredRequestsSheet(
            isLoading = false,
            requests = persistentListOf(
                RegisteredMedicalCommissionPR(
                    demandInfoId = "1",
                    demandSaveDate = 1720000000000L,
                    verdictDescription = "ازکارافتادگی کلی ناشی از کار",
                ),
            ),
            onDismiss = {},
        )
    }
}
