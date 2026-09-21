package com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.inspection_id
import taminx.core.core_ui.inspection_search_button
import taminx.core.core_ui.inspection_search_clear_button
import taminx.core.core_ui.inspection_search_field_optional_hint
import taminx.core.core_ui.inspection_search_sheet_title
import taminx.core.core_ui.inspection_workshop_code

private const val MAX_WORKSHOP_CODE_LENGTH = 10
private const val MAX_INSPECTION_ID_LENGTH = 13

/**
 * Two optional numeric filters (workshop code, inspection id) applied client-side over the
 * already-loaded list — see [com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionFilter].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WorkshopInspectionSearchSheet(
    workshopCodeQuery: String,
    onWorkshopCodeQueryChange: (String) -> Unit,
    inspectionIdQuery: String,
    onInspectionIdQueryChange: (String) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val optionalHint = stringResource(Res.string.inspection_search_field_optional_hint)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = taminColors.bgPage,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            TaminText(
                text = stringResource(Res.string.inspection_search_sheet_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                TaminStyledTextField(
                    value = workshopCodeQuery,
                    onValueChange = onWorkshopCodeQueryChange,
                    label = stringResource(Res.string.inspection_workshop_code),
                    placeholder = optionalHint,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    inputRestriction = InputRestriction.DigitsOnly,
                    maxLength = MAX_WORKSHOP_CODE_LENGTH,
                    modifier = Modifier.weight(1f),
                )

                TaminStyledTextField(
                    value = inspectionIdQuery,
                    onValueChange = onInspectionIdQueryChange,
                    label = stringResource(Res.string.inspection_id),
                    placeholder = optionalHint,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    inputRestriction = InputRestriction.DigitsOnly,
                    maxLength = MAX_INSPECTION_ID_LENGTH,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm, bottom = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                TaminFilledButton(
                    text = stringResource(Res.string.inspection_search_button),
                    onClick = onApply,
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                    iconPosition = IconPosition.End,
                    modifier = Modifier.weight(1f),
                )
                TaminOutlinedButton(
                    text = stringResource(Res.string.inspection_search_clear_button),
                    onClick = onClear,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
