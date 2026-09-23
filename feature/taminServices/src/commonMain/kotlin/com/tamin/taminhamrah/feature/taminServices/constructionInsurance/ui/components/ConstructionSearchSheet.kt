package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import com.tamin.taminhamrah.ui.theme.TaminNavy300
import com.tamin.taminhamrah.ui.theme.TaminNavy900
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.all_items
import taminx.core.core_ui.branch_code
import taminx.core.core_ui.btn_search
import taminx.core.core_ui.construction_insurance_search_sheet_title
import taminx.core.core_ui.contract_affairs_search_type_optional
import taminx.core.core_ui.file_number
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.label_request_number
import taminx.core.core_ui.workshop_number

@Composable
internal fun ConstructionSearchSheet(
    fileNoQuery: String,
    reqNoQuery: String,
    workshopIdQuery: String,
    branchCodeQuery: String,
    onFileNoChanged: (String) -> Unit,
    onReqNoChanged: (String) -> Unit,
    onWorkshopIdChanged: (String) -> Unit,
    onBranchCodeChanged: (String) -> Unit,
    onExecuteSearch: () -> Unit,
    onResetSearch: () -> Unit,
    onDismiss: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val optionalHint = stringResource(Res.string.contract_affairs_search_type_optional)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = taminColors.bgSurface,
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
                text = stringResource(Res.string.construction_insurance_search_sheet_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                TaminStyledTextField(
                    value = reqNoQuery,
                    onValueChange = onReqNoChanged,
                    label = stringResource(Res.string.label_request_number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = optionalHint,
                )
                TaminStyledTextField(
                    value = fileNoQuery,
                    onValueChange = onFileNoChanged,
                    label = stringResource(Res.string.file_number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = optionalHint,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                TaminStyledTextField(
                    value = branchCodeQuery,
                    onValueChange = onBranchCodeChanged,
                    label = stringResource(Res.string.branch_code),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = optionalHint,
                )
                TaminStyledTextField(
                    value = workshopIdQuery,
                    onValueChange = onWorkshopIdChanged,
                    label = stringResource(Res.string.workshop_number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = optionalHint,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                TaminOutlinedButton(
                    text = stringResource(Res.string.all_items),
                    onClick = onResetSearch,
                    borderColor = taminColors.border,
                    containerColor = taminColors.chipBg,
                    contentColor = taminColors.textSecondary,
                    textStyle = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(0.5f),
                )
                TaminFilledButton(
                    background = Brush.linearGradient(colors = listOf(TaminNavy300, TaminNavy900)),
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.btn_search),
                    onClick = onExecuteSearch,
                    painter = painterResource(Res.drawable.ic_tamin_search),
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionSearchSheetPreview() {
    PreviewRtlThemeContent {
        ConstructionSearchSheet(
            fileNoQuery = "۱۲۳۴۵۶",
            reqNoQuery = "۹۸۷۶۵۴",
            workshopIdQuery = "۱۰۲۳۴",
            branchCodeQuery = "۱۲۰۱",
            onFileNoChanged = {},
            onReqNoChanged = {},
            onWorkshopIdChanged = {},
            onBranchCodeChanged = {},
            onExecuteSearch = {},
            onResetSearch = {},
            onDismiss = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionSearchSheetPreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        ConstructionSearchSheet(
            fileNoQuery = "",
            reqNoQuery = "",
            workshopIdQuery = "",
            branchCodeQuery = "",
            onFileNoChanged = {},
            onReqNoChanged = {},
            onWorkshopIdChanged = {},
            onBranchCodeChanged = {},
            onExecuteSearch = {},
            onResetSearch = {},
            onDismiss = {},
        )
    }
}
