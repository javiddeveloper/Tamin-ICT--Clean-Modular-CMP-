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
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_action_debt_inquiry
import taminx.core.core_ui.workshop_empty_list
import taminx.core.core_ui.workshop_inquiry_date
import taminx.core.core_ui.workshop_inquiry_definitive
import taminx.core.core_ui.workshop_inquiry_divisible
import taminx.core.core_ui.workshop_inquiry_heading
import taminx.core.core_ui.workshop_inquiry_indivisible
import taminx.core.core_ui.workshop_inquiry_result

/**
 * استعلام بدهی کارگاه — one record, five lines, nothing to unfold.
 *
 * The design heads this one «وضعیت بدهی کارگاه» rather than repeating the screen's own title, and
 * drops the count beside it: there is only ever one answer.
 */
@Composable
fun WorkshopDebtInquiryScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    workshopName: String = "",
    viewModel: WorkshopDebtInquiryViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(WorkshopDebtInquiryIntent.Open(workshopId, branchCode))
    }

    WorkshopDebtInquiryContent(
        state = state,
        workshopName = workshopName,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun WorkshopDebtInquiryContent(
    state: WorkshopDebtInquiryUiState,
    workshopName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_debt_inquiry),
        onBack = onBack,
        workshopName = workshopName.takeIf { it.isNotBlank() },
        workshopCode = state.workshopId.takeIf { it.isNotBlank() }?.toPersianDigits(),
        modifier = modifier,
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
                    .padding(horizontal = Spacing.page)
                    .padding(top = Spacing.smd, bottom = Spacing.page),
                verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
            ) {
                WorkshopSectionHeader(title = stringResource(Res.string.workshop_inquiry_heading))
                DebtInquiryCard(inquiry = inquiry)
            }
        }
    }
}

@Composable
private fun DebtInquiryCard(
    inquiry: WorkshopDebtInquiryPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    WorkshopRecordCard(modifier = modifier) {
        DetailRow(
            label = stringResource(Res.string.workshop_inquiry_result),
            value = inquiry.result,
            // The verdict is the one line on this card the design colors, because it is the one
            // line that is either good news or bad.
            valueColor = colors.dangerText,
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_inquiry_date),
            value = inquiry.date,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_inquiry_definitive),
            value = inquiry.definitiveDebt,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_inquiry_divisible),
            value = inquiry.divisibleDebt,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_inquiry_indivisible),
            value = inquiry.indivisibleDebt,
            valueColor = colors.orangeText,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopDebtInquiryScreenPreview() {
    PreviewRtlThemeContent {
        WorkshopDebtInquiryContent(
            state = WorkshopDebtInquiryUiState(
                workshopId = "0968210170",
                inquiry = WorkshopDebtInquiryPR(
                    result = "کارگاه دارای بدهی قطعی",
                    date = "۱۴۰۵/۰۵/۲۶",
                    definitiveDebt = "۲۹٬۴۱۰٬۵۰۰",
                    divisibleDebt = "۰",
                    indivisibleDebt = "۲۹٬۴۱۰٬۵۰۰",
                ),
            ),
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            onBack = {},
        )
    }
}
