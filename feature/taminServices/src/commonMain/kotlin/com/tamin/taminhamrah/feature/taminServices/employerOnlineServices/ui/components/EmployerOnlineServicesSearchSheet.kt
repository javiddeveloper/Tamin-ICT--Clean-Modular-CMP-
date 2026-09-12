package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.EmployerAgreementSearch
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminNavy300
import com.tamin.taminhamrah.ui.theme.TaminNavy900
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_online_services_search_branch_label
import taminx.core.core_ui.employer_online_services_search_button
import taminx.core.core_ui.employer_online_services_search_clear_button
import taminx.core.core_ui.employer_online_services_search_optional_hint
import taminx.core.core_ui.employer_online_services_search_sheet_title
import taminx.core.core_ui.employer_online_services_search_workshop_label
import taminx.core.core_ui.ic_tamin_search

/**
 * The "جستجو در تعهدنامه‌ها" sheet — a straight copy of `inspection`'s `InspectionSearchSheet`, with
 * the two fields re-labelled to کد شعبه / کد کارگاه. Edits stay local to the sheet until جستجو is
 * tapped, so dismissing without applying leaves the current filter untouched.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EmployerOnlineServicesSearchSheet(
    initial: EmployerAgreementSearch,
    onDismiss: () -> Unit,
    onApply: (EmployerAgreementSearch) -> Unit,
    onClear: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var criteria by remember { mutableStateOf(initial) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgSurface,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .navigationBarsPadding()
                .padding(bottom = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Text(
                text = stringResource(Res.string.employer_online_services_search_sheet_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            val optionalHint = stringResource(Res.string.employer_online_services_search_optional_hint)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                TaminStyledTextField(
                    value = criteria.branchCode,
                    onValueChange = { criteria = criteria.copy(branchCode = it) },
                    label = stringResource(Res.string.employer_online_services_search_branch_label),
                    placeholder = optionalHint,
                    modifier = Modifier.weight(1f),
                )
                TaminStyledTextField(
                    value = criteria.workshopCode,
                    onValueChange = { criteria = criteria.copy(workshopCode = it) },
                    label = stringResource(Res.string.employer_online_services_search_workshop_label),
                    placeholder = optionalHint,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                TaminPrimaryButton(
                    background = Brush.linearGradient(listOf(TaminNavy300, TaminNavy900)),
                    text = stringResource(Res.string.employer_online_services_search_button),
                    onClick = { onApply(criteria) },
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                    iconAtStart = true,
                    modifier = Modifier.weight(0.7f),
                )
                TaminOutlinedButton(
                    contentColor = colors.textMuted,
                    containerColor = colors.bgSurface,
                    text = stringResource(Res.string.employer_online_services_search_clear_button),
                    onClick = {
                        criteria = EmployerAgreementSearch()
                        onClear()
                    },
                    modifier = Modifier.weight(0.3f),
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerOnlineServicesSearchSheetPreviewLight() {
    PreviewRtlThemeContent {
        EmployerOnlineServicesSearchSheet(
            initial = EmployerAgreementSearch(),
            onDismiss = {},
            onApply = {},
            onClear = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerOnlineServicesSearchSheetPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        EmployerOnlineServicesSearchSheet(
            initial = EmployerAgreementSearch(branchCode = "1202", workshopCode = "0081631829"),
            onDismiss = {},
            onApply = {},
            onClear = {},
        )
    }
}
