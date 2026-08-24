package com.tamin.taminhamrah.feature.taminServices.inspection.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionSearchCriteria
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminNavy300
import com.tamin.taminhamrah.ui.theme.TaminNavy900
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.bank_account_add
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.inspection_id
import taminx.core.core_ui.inspection_search_button
import taminx.core.core_ui.inspection_search_clear_button
import taminx.core.core_ui.inspection_search_field_optional_hint
import taminx.core.core_ui.inspection_search_sheet_title
import taminx.core.core_ui.inspection_workshop_code

private val SearchFieldHeight = 48.dp

/**
 * Local, offline search over inspections already loaded on screen — never re-hits the API.
 * The caller owns [InspectionSearchCriteria]; this sheet only edits a working copy until the
 * user taps search or clear.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionSearchSheet(
    initial: InspectionSearchCriteria,
    onDismiss: () -> Unit,
    onApply: (InspectionSearchCriteria) -> Unit,
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
                text = stringResource(Res.string.inspection_search_sheet_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            val optionalHint = stringResource(Res.string.inspection_search_field_optional_hint)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                LabeledSearchField(
                    label = stringResource(Res.string.inspection_id),
                    value = criteria.inspectionNo,
                    onValueChange = { criteria = criteria.copy(inspectionNo = it) },
                    placeholder = optionalHint,
                    modifier = Modifier.weight(1f),
                )
                LabeledSearchField(
                    label = stringResource(Res.string.inspection_workshop_code),
                    value = criteria.workshopNo,
                    onValueChange = { criteria = criteria.copy(workshopNo = it) },
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
                    text = stringResource(Res.string.inspection_search_button),
                    onClick = { onApply(criteria) },
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                    iconAtStart = true,
                    modifier = Modifier.weight(0.7f),
                )
                TaminOutlinedButton(
                    contentColor = colors.textMuted,
                    containerColor = colors.bgSurface,
                    text = stringResource(Res.string.inspection_search_clear_button),
                    onClick = {
                        criteria = InspectionSearchCriteria()
                        onClear()
                    },
                    modifier = Modifier.weight(0.3f),
                )
            }
        }
    }
}

@Composable
private fun LabeledSearchField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(CornerRadius.lg)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colors.blueText,
        unfocusedBorderColor = colors.border,
        focusedContainerColor = colors.bgSurface,
        unfocusedContainerColor = colors.bgSurface,
        focusedTextColor = colors.textPrimary,
        unfocusedTextColor = colors.textPrimary,
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(SearchFieldHeight),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary),
            singleLine = true,
            cursorBrush = SolidColor(colors.blueText),
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = false,
                    placeholder = {
                        Text(
                            text = placeholder,
                            color = colors.textMuted,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    colors = fieldColors,
                    contentPadding = PaddingValues(horizontal = Spacing.md),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = true,
                            isError = false,
                            interactionSource = interactionSource,
                            colors = fieldColors,
                            shape = shape,
                        )
                    },
                )
            },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InspectionSearchSheetPreviewLight() {
    PreviewRtlThemeContent {
        InspectionSearchSheet(
            initial = InspectionSearchCriteria(),
            onDismiss = {},
            onApply = {},
            onClear = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InspectionSearchSheetPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        InspectionSearchSheet(
            initial = InspectionSearchCriteria(
                workshopNo = "0117742260",
                inspectionNo = "01631894"
            ),
            onDismiss = {},
            onApply = {},
            onClear = {},
        )
    }
}
