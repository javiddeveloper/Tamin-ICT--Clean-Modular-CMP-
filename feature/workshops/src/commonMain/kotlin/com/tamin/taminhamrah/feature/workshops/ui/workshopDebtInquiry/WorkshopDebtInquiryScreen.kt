package com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListSkeleton
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_action_debt_inquiry
import taminx.core.core_ui.workshop_empty_list
import taminx.core.core_ui.workshop_inquiry_date
import taminx.core.core_ui.workshop_inquiry_definitive
import taminx.core.core_ui.workshop_inquiry_divisible
import taminx.core.core_ui.workshop_inquiry_indivisible
import taminx.core.core_ui.workshop_inquiry_result

/** استعلام بدهی کارگاه — one record, five lines. */
@Composable
fun WorkshopDebtInquiryScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    viewModel: WorkshopDebtInquiryViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(WorkshopDebtInquiryIntent.Open(workshopId, branchCode))
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_debt_inquiry),
        onBack = onBack,
    ) {
        val inquiry = state.inquiry
        when {
            state.isLoading -> WorkshopListSkeleton(rowCount = 1)

            inquiry == null -> EmptyStateMessage(
                icon = Icons.Outlined.Info,
                title = stringResource(Res.string.workshop_empty_list),
                modifier = Modifier.fillMaxSize(),
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.page)
                    .taminSurface(CornerRadius.lg)
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                DetailRow(
                    label = stringResource(Res.string.workshop_inquiry_result),
                    value = inquiry.result,
                    numeric = false,
                )
                DetailRow(stringResource(Res.string.workshop_inquiry_date), inquiry.date)
                DetailRow(
                    stringResource(Res.string.workshop_inquiry_definitive),
                    inquiry.definitiveDebt,
                )
                DetailRow(
                    stringResource(Res.string.workshop_inquiry_divisible),
                    inquiry.divisibleDebt,
                )
                DetailRow(
                    stringResource(Res.string.workshop_inquiry_indivisible),
                    inquiry.indivisibleDebt,
                )
            }
        }
    }
}
