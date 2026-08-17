package com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsIntent
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsUiState
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.send_history_disclaimer
import taminx.core.core_ui.send_history_issue_certificate
import taminx.core.core_ui.send_history_next_step
import taminx.core.core_ui.send_history_select_type_placeholder

@Composable
internal fun SelectTypeStep(
    uiState: SendHistoryToInstitutionsUiState,
    typeLabels: List<String>,
    typeColors: List<Color>,
    typeTextColors: List<Color>,
    onIntent: (SendHistoryToInstitutionsIntent) -> Unit
) {
    val taminColors = LocalTaminColors.current
    var showSheet by remember { mutableStateOf(false) }

    val selectedIndices = buildList {
        if (uiState.isType1Selected) add(0)
        if (uiState.isType2Selected) add(1)
        if (uiState.isType3Selected) add(2)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg)
    ) {
        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = stringResource(Res.string.send_history_issue_certificate),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = taminColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        if (uiState.isLoading && uiState.userInfo == null) {
            SendToInstitutionSkeleton()
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(color = taminColors.bgSurface)
                    .border(1.dp, taminColors.border, RoundedCornerShape(18.dp))
                    .clickable { showSheet = true }
                    .padding(horizontal = Spacing.md, vertical = Spacing.smd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (selectedIndices.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.send_history_select_type_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = taminColors.textMuted
                    )
                } else {
                    FlowRow(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = Spacing.xs),
                        maxItemsInEachRow = 3,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.Start),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        selectedIndices.forEach { index ->
                            HistoryTypeChip(
                                label = typeLabels[index],
                                bgColor = typeColors[index],
                                textColor = typeTextColors[index]
                            )
                        }
                    }
                }
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                    contentDescription = null,
                    tint = taminColors.chevron,
                    modifier = Modifier.size(20.dp).rotate(270f)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(Res.string.send_history_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textSecondary,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            TaminFilledButton(
                text = stringResource(Res.string.send_history_next_step),
                icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                onClick = { onIntent(SendHistoryToInstitutionsIntent.GoToNextStep) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.hasAnyTypeSelected
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))
    }

    if (showSheet) {
        HistoryTypeBottomSheet(
            initialType1 = uiState.isType1Selected,
            initialType2 = uiState.isType2Selected,
            initialType3 = uiState.isType3Selected,
            typeLabels = typeLabels,
            typeColors = typeColors,
            typeTextColors = typeTextColors,
            onConfirm = { t1, t2, t3 ->
                onIntent(SendHistoryToInstitutionsIntent.ConfirmTypeSelection(t1, t2, t3))
                showSheet = false
            },
            onDismiss = { showSheet = false }
        )
    }
}
