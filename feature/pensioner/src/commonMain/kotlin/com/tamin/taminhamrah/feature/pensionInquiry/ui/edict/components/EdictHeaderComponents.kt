package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import com.tamin.taminhamrah.ui.components.rideUpIntoHeader
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.EdictIntent
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.EdictUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminColors
import com.tamin.taminhamrah.ui.theme.TaminIdentityCardShadow
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.edict_title
import taminx.core.core_ui.edict_search_title
import taminx.core.core_ui.edict_year_filter
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_down
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.jalali_months

private val HEADER_OVERLAP = 24.dp

/**
 * Floating header (IdentityInScreen pattern): a Column containing TaminTopAppBar and
 * EdictMainCard (or EdictEmptyCard). The card rides HEADER_OVERLAP pixels up into
 * the gradient via rideUpIntoHeader, so the gradient's bottom blends into the card top.
 *
 * This composable is placed at Alignment.TopCenter of a Box and measured via
 * onSizeChanged; the scrollable content underneath starts with a Spacer(reservedHeight)
 * whose size matches this header's measured height.
 */
@Composable
fun EdictHeader(
    state: EdictUiState,
    onBack: () -> Unit,
    onIntent: (EdictIntent) -> Unit,
    collapseProgress: () -> Float = { 0f },
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val edict = state.edictPensioner

    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.edict_title),
            background = profileGradientBrush,
            bottomPadding = HEADER_OVERLAP,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = stringResource(Res.string.action_back),
                    onClick = onBack,
                    bordered = true
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                    contentDescription = stringResource(Res.string.edict_search_title),
                    onClick = { onIntent(EdictIntent.ShowSearchSheet) },
                    bordered = true
                )
            },
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                DecorativeBackgroundCircle(
                    size = 190.dp,
                    xOffset = 450.dp,
                    yOffset = (-150).dp,
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.md, bottom = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    EdictPensionerChip(
                        state = state,
                        onIntent = onIntent,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                    if (state.isDateFilteredBySearch && state.startDate.isNotEmpty()) {
                        EdictYearFilterChip(
                            startDate = state.startDate,
                            onClear = { onIntent(EdictIntent.ClearDateFilter) },
                        )
                    } else {
                        EdictDateChipsRow(state = state, onIntent = onIntent)
                    }
                }
            }
        }

        val cardModifier = Modifier
            .fillMaxWidth()
            .rideUpIntoHeader(
                progress = collapseProgress,
                expandedOverlap = HEADER_OVERLAP,
                collapsedOverlap = HEADER_OVERLAP,
            )
            .padding(horizontal = Spacing.lg)

        // Loading is checked first, and on every load rather than only the first: a refetch
        // after a date or pensioner change replaces the card with its skeleton, where it used
        // to leave the previous edict on screen under a scrim -- numbers from the old query
        // that read as the answer to the new one.
        if (state.isLoading) {
            EdictSkeletonMainCard(modifier = cardModifier)
        } else if (edict != null) {
            EdictMainCard(
                edict = edict,
                collapseProgress = collapseProgress,
                selectedDate = state.startDate,
                modifier = cardModifier.shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(CornerRadius.card),
                    ambientColor = TaminIdentityCardShadow,
                    spotColor = TaminIdentityCardShadow,
                ),
            )
        } else if (state.hasLoadedOnce) {
            EdictEmptyCard(
                onShowAll = { onIntent(EdictIntent.ClearDateFilter) },
                modifier = cardModifier,
            )
        }
    }
}

@Composable
private fun EdictPensionerChip(
    state: EdictUiState,
    onIntent: (EdictIntent) -> Unit,
    modifier: Modifier
) {
    val taminColors = LocalTaminColors.current
    val isLoading = state.selectedPensionerId == null
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.card))
            .background(Color.White.copy(alpha = 0.12f))
            .border(
                width = 1.dp,
                color = taminColors.border,
                shape = RoundedCornerShape(CornerRadius.card)
            )
            .clickable(enabled = !isLoading) { onIntent(EdictIntent.ShowPensionerSheet) }
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(CornerRadius.sm))
                        .shimmer(
                            colorBase = Color.White.copy(alpha = 0.15f),
                            colorHighlight = Color.White.copy(alpha = 0.45f),
                        )
                )
            } else {
                TaminText(
                    text = "${state.selectedPensionerId} | مستمری بگیر",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.width(Spacing.sm))
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_chevron_down),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun EdictYearFilterChip(
    startDate: String,
    onClear: () -> Unit,
) {
    val year = startDate.take(4)
    val month = startDate.drop(4).take(2)
    val months = stringArrayResource(Res.array.jalali_months)
    val monthName = month.toIntOrNull()?.minus(1)?.let { idx ->
        months.getOrNull(idx)
    }
    val label = buildString {
        append(stringResource(Res.string.edict_year_filter, year))
        if (monthName != null) append(" · $monthName")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.chip))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(16.dp)
                    .clickable(onClick = onClear),
            )
            Spacer(Modifier.width(Spacing.xs))
            TaminText(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun EdictDateChipsRow(
    state: EdictUiState,
    onIntent: (EdictIntent) -> Unit,
) {
    // Only فروردین (01) edicts are shown. Generate the last 5 years dynamically.
    // In a future iteration these will come from an API list-of-edicts call.
    val currentYear = remember { PersianDateFormatter.currentJalaliYear() }
    val dates = remember(currentYear) {
        (currentYear downTo (currentYear - 4)).map { "${it}01" }
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        items(dates) { date ->
            val isSelected = date == state.startDate
            val bgColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.12f)
            val textColor = if (isSelected) Color.Black else Color.White

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(bgColor)
                    .clickable {
                        onIntent(EdictIntent.ChangeStartDate(date))
                        onIntent(EdictIntent.LoadEdict)
                    }
                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                TaminText(
                    text = formatEdictDateLabel(date),
                    color = textColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
internal fun formatEdictDateLabel(date: String): String {
    if (date.length < 5) return ""
    val year = date.substring(0, 4)
    val monthStr = date.substring(4)
    val monthIndex = monthStr.toIntOrNull()?.minus(1) ?: return date
    val months = stringArrayResource(Res.array.jalali_months)
    return if (monthIndex in months.indices) "${months[monthIndex]} ${year.toPersianDigits()}" else date.toPersianDigits()
}

@PreviewRtlTheme
@Composable
private fun EdictHeaderPreview() {
    PreviewRtlThemeContent {
        EdictHeader(
            state = EdictUiState(
                selectedPensionerId = "1003406938",
                startDate = "139905",
            ),
            onBack = {},
            onIntent = {},
        )
    }
}
