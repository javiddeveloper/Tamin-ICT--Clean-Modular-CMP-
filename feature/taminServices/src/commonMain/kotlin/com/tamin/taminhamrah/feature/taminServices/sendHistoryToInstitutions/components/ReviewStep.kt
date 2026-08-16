package com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsIntent
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsUiState
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.send_history_destination_value
import taminx.core.core_ui.send_history_disclaimer
import taminx.core.core_ui.send_history_label_destination
import taminx.core.core_ui.send_history_label_full_name
import taminx.core.core_ui.send_history_label_insurance_number
import taminx.core.core_ui.send_history_label_selected_type
import taminx.core.core_ui.send_history_review_title
import taminx.core.core_ui.send_history_submit_button

@Composable
internal fun ReviewStep(
    uiState: SendHistoryToInstitutionsUiState,
    typeLabels: List<String>,
    typeColors: List<Color>,
    typeTextColors: List<Color>,
    onIntent: (SendHistoryToInstitutionsIntent) -> Unit
) {
    val taminColors = LocalTaminColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg)
    ) {
        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = stringResource(Res.string.send_history_review_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = taminColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(color = taminColors.bgSurface)
                .border(1.dp, taminColors.border, RoundedCornerShape(18.dp))
                .padding(Spacing.md)
        ) {
            if (uiState.isLoading && uiState.userInfo == null) {
                SendToInstitutionSkeleton()
            } else {
                uiState.userInfo?.let { user ->
                    DetailRow(
                        verticalPadding = 12.dp,
                        label = stringResource(Res.string.send_history_label_full_name),
                        value = "${user.firstName} ${user.lastName}",
                        numeric = false
                    )
                    TaminDivider()
                    DetailRow(
                        verticalPadding = 12.dp,
                        label = stringResource(Res.string.send_history_label_insurance_number),
                        value = user.insuranceNumber
                    )
                    TaminDivider()
                }
                DetailRow(
                    verticalPadding = 12.dp,
                    label = stringResource(Res.string.send_history_label_destination),
                    value = stringResource(Res.string.send_history_destination_value),
                    numeric = false
                )
                TaminDivider()

                Text(
                    text = stringResource(Res.string.send_history_label_selected_type),
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textMuted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xs),
                    textAlign = TextAlign.Start
                )

                val selectedIndices = buildList {
                    if (uiState.isType1Selected) add(0)
                    if (uiState.isType2Selected) add(1)
                    if (uiState.isType3Selected) add(2)
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    selectedIndices.forEach { index ->
                        HistoryTypeChip(
                            label = typeLabels[index],
                            bgColor = typeColors[index],
                            textColor = typeTextColors[index],
                            modifier = Modifier.padding(start = Spacing.xs)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = stringResource(Res.string.send_history_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.textSecondary,
            textAlign = TextAlign.Justify
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isSending = uiState.isLoading && uiState.userInfo != null
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(CornerRadius.iconTile))
                    .border(1.dp, taminColors.border, RoundedCornerShape(CornerRadius.iconTile))
                    .clickable(enabled = !isSending) {
                        onIntent(SendHistoryToInstitutionsIntent.GoToPreviousStep)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    tint = if (isSending) taminColors.textMuted else taminColors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            LoadingButton(
                text = stringResource(Res.string.send_history_submit_button),
                onClick = { onIntent(SendHistoryToInstitutionsIntent.SendToInstitution) },
                modifier = Modifier.weight(1f),
                enabled = uiState.userInfo != null,
                isLoading = isSending,
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))
    }
}
