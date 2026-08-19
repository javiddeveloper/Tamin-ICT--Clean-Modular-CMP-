package com.tamin.taminhamrah.feature.taminServices.occurrence.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.occurrence_warning_article_60_chip
import taminx.core.core_ui.occurrence_warning_article_60_desc
import taminx.core.core_ui.occurrence_warning_article_60_title
import taminx.core.core_ui.occurrence_warning_article_65_chip
import taminx.core.core_ui.occurrence_warning_article_65_desc
import taminx.core.core_ui.occurrence_warning_article_65_title
import taminx.core.core_ui.occurrence_warning_article_66_chip
import taminx.core.core_ui.occurrence_warning_article_66_desc
import taminx.core.core_ui.occurrence_warning_article_66_title
import taminx.core.core_ui.occurrence_warning_cancel
import taminx.core.core_ui.occurrence_warning_confirm
import taminx.core.core_ui.occurrence_warning_subtitle
import taminx.core.core_ui.occurrence_warning_title

/**
 * Custom (not [com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheet]) because that
 * component is shaped for selectable item/chip lists with a single description field, not a
 * scrollable stack of independent title+body cards like the three legal articles shown here.
 */
@Composable
internal fun OccurrenceWarningBottomSheet(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val taminColors = LocalTaminColors.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
        containerColor = taminColors.bgPage,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = Spacing.md)
                    .size(width = 32.dp, height = 4.dp)
                    .background(taminColors.border, RoundedCornerShape(50))
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.page),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = taminColors.orangeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.WarningAmber,
                        contentDescription = null,
                        tint = taminColors.warning,
                        modifier = Modifier.size(20.dp),
                    )
                }

                Spacer(Modifier.width(Spacing.xs))
                Column {
                    Text(
                        text = stringResource(Res.string.occurrence_warning_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = taminColors.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = stringResource(Res.string.occurrence_warning_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textMuted,
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.xs))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                LegalArticleCard(
                    chipLabel = stringResource(Res.string.occurrence_warning_article_60_chip),
                    title = stringResource(Res.string.occurrence_warning_article_60_title),
                    description = stringResource(Res.string.occurrence_warning_article_60_desc),
                )
                LegalArticleCard(
                    chipLabel = stringResource(Res.string.occurrence_warning_article_65_chip),
                    title = stringResource(Res.string.occurrence_warning_article_65_title),
                    description = stringResource(Res.string.occurrence_warning_article_65_desc),
                )
                LegalArticleCard(
                    chipLabel = stringResource(Res.string.occurrence_warning_article_66_chip),
                    title = stringResource(Res.string.occurrence_warning_article_66_title),
                    description = stringResource(Res.string.occurrence_warning_article_66_desc),
                )
            }
            Spacer(modifier = Modifier.height(Spacing.lg))
            Row(modifier = Modifier.fillMaxWidth()) {
                TaminOutlinedButton(
                    text = stringResource(Res.string.occurrence_warning_cancel),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                TaminFilledButton(
                    text = stringResource(Res.string.occurrence_warning_confirm),
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun LegalArticleCard(
    chipLabel: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.lg)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            CustomChip(
                border = BorderStroke(
                    width = 1.dp,
                    color = taminColors.blueText.copy(alpha = 0.4f)
                ),
                text = chipLabel,
                containerColor = taminColors.blueBg,
                textColor = taminColors.blueText,
            )
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
            )
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.textMuted,
        )
    }
}
