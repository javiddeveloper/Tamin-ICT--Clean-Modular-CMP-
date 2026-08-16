package com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.send_history_confirm_sheet
import taminx.core.core_ui.send_history_select_type_title

@Composable
internal fun HistoryTypeBottomSheet(
    initialType1: Boolean,
    initialType2: Boolean,
    initialType3: Boolean,
    typeLabels: List<String>,
    typeColors: List<Color>,
    typeTextColors: List<Color>,
    onConfirm: (Boolean, Boolean, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var draft1 by remember { mutableStateOf(initialType1) }
    var draft2 by remember { mutableStateOf(initialType2) }
    var draft3 by remember { mutableStateOf(initialType3) }
    val drafts = listOf(draft1, draft2, draft3)
    val hasAnyDraft = draft1 || draft2 || draft3

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = taminColors.bgSurface,
        shape = RoundedCornerShape(topStart = CornerRadius.x2l, topEnd = CornerRadius.x2l),
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
                .navigationBarsPadding()
        ) {
            Text(
                text = stringResource(Res.string.send_history_select_type_title),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.md),
            )

            typeLabels.forEachIndexed { index, label ->
                HistoryTypeRow(
                    label = label,
                    isSelected = drafts[index],
                    bgColor = typeColors[index],
                    borderAccentColor = typeTextColors[index],
                    onToggle = {
                        when (index) {
                            0 -> draft1 = !draft1
                            1 -> draft2 = !draft2
                            2 -> draft3 = !draft3
                        }
                    }
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            TaminFilledButton(
                text = stringResource(Res.string.send_history_confirm_sheet),
                onClick = { onConfirm(draft1, draft2, draft3) },
                modifier = Modifier.fillMaxWidth(),
                enabled = hasAnyDraft
            )

            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }
}
