package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.workshops

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeHeader
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeHeroSubtitle
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeIdentitySummaryCard
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.list.AddRepresentativeChip
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopPR
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.dashedOutline
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.driveTopArea
import com.tamin.taminhamrah.ui.toparea.rememberMeasuredTopAreaState
import com.tamin.taminhamrah.ui.toparea.reportTopAreaHeight
import com.tamin.taminhamrah.ui.toparea.topAreaContentPadding
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_copy
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_copy
import taminx.core.core_ui.legal_representative_branch_and_count
import taminx.core.core_ui.legal_representative_branch_code_label
import taminx.core.core_ui.legal_representative_empty_title
import taminx.core.core_ui.legal_representative_hub_subtitle
import taminx.core.core_ui.legal_representative_info_banner
import taminx.core.core_ui.legal_representative_open_action
import taminx.core.core_ui.legal_representative_special_workshop_badge
import taminx.core.core_ui.legal_representative_workshop_code_label

/** How far the identity card rides up into the header's gradient, straddling the seam. */
private val HeroCardOverlap = Spacing.xxl

@Composable
fun LegalRepresentativeWorkshopsScreen(
    onBackClicked: () -> Unit,
    onOpenWorkshop: (LegalRepresentativeWorkshopPR) -> Unit,
    viewModel: LegalRepresentativeWorkshopsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(LegalRepresentativeWorkshopsIntent.Load)
    }

    LegalRepresentativeWorkshopsContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onOpenWorkshop = onOpenWorkshop,
        onRetry = { viewModel.sendIntent(LegalRepresentativeWorkshopsIntent.Load) },
    )
}

@Composable
private fun LegalRepresentativeWorkshopsContent(
    uiState: LegalRepresentativeWorkshopsUiState,
    onBackClicked: () -> Unit,
    onOpenWorkshop: (LegalRepresentativeWorkshopPR) -> Unit,
    onRetry: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    // Folds the header's icon/subtitle from the list's own drag, snapping on release. The
    // identity card below it is never wrapped in a topArea behavior, so it stays fully shown and
    // pinned above the list -- only the header's own content folds and fades away. The drag
    // budget is measured from this exact header+card block, so it can't drift out of sync with a
    // copy or font change to either. See docs/vault/TopArea-System.md.
    val topArea = rememberMeasuredTopAreaState { state ->
        LegalRepresentativeWorkshopsTopArea(
            fullName = uiState.fullName,
            workshopCount = uiState.workshops.size,
            onBackClicked = onBackClicked,
            topAreaState = state,
        )
    }
    val listState = rememberLazyListState()

    // Overlaid rather than a plain Column so the header keeps drawing edge-to-edge behind the
    // status bar while the list passes underneath it as it scrolls.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator(
                color = taminColors.blueText,
                modifier = Modifier.align(Alignment.Center),
            )

            uiState.error != null -> ErrorStateView(
                message = uiState.error,
                onDismiss = {},
                onRetry = onRetry,
                modifier = Modifier.align(Alignment.Center),
            )

            uiState.workshops.isEmpty() -> EmptyStateMessage(
                icon = Icons.Filled.Groups,
                title = stringResource(Res.string.legal_representative_empty_title),
                modifier = Modifier.align(Alignment.Center),
            )

            else -> LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .driveTopArea(topArea, listState),
                contentPadding = topAreaContentPadding(
                    state = topArea,
                    rest = PaddingValues(start = Spacing.lg, end = Spacing.lg, top = Spacing.lg, bottom = Spacing.lg),
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                item {
                    BannerCard(
                        message = stringResource(Res.string.legal_representative_info_banner),
                        type = BannerType.Info,
                    )
                }
                items(uiState.workshops) { workshop ->
                    LegalRepresentativeWorkshopCard(
                        workshop = workshop,
                        onOpenWorkshop = onOpenWorkshop,
                    )
                }
            }
        }

        LegalRepresentativeWorkshopsTopArea(
            fullName = uiState.fullName,
            workshopCount = uiState.workshops.size,
            onBackClicked = onBackClicked,
            topAreaState = topArea,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .reportTopAreaHeight(topArea),
        )
    }
}

/**
 * The hub's floating top area: the folding gradient hero (back button, icon, subtitle) plus the
 * identity card, which stays fully visible and pinned beneath it regardless of scroll -- riding
 * up by [HeroCardOverlap] to straddle the header's seam, same as before the header could fold.
 */
@Composable
private fun LegalRepresentativeWorkshopsTopArea(
    fullName: String?,
    workshopCount: Int,
    onBackClicked: () -> Unit,
    topAreaState: TopAreaState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LegalRepresentativeHeader(
            onBackClicked = onBackClicked,
            heroCardOverlap = HeroCardOverlap,
        ) {
            LegalRepresentativeHeroSubtitle(
                text = stringResource(Res.string.legal_representative_hub_subtitle),
                topAreaState = topAreaState,
            )
        }
        LegalRepresentativeIdentitySummaryCard(
            fullName = fullName,
            workshopCount = workshopCount,
            // Rides up into the header's reserved bottom space, rather than sitting right after
            // it, so the card visually straddles the header's seam. Reports a height reduced by
            // the same overlap so reportTopAreaHeight sees the true visual footprint of this
            // whole block, not the overlap counted twice as reserved list space.
            modifier = Modifier
                .straddlePreviousSibling(HeroCardOverlap)
                .padding(horizontal = Spacing.lg),
        )
    }
}

/**
 * Shifts this child up by [overlap] to overlap the previous sibling's bottom edge, while
 * reporting a height reduced by that same amount -- so a parent measuring total column height
 * (here, [reportTopAreaHeight]) sees the true visual footprint instead of double-counting the
 * overlap as reserved space.
 */
private fun Modifier.straddlePreviousSibling(overlap: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val overlapPx = overlap.roundToPx()
    val reportedHeight = (placeable.height - overlapPx).coerceAtLeast(0)
    layout(placeable.width, reportedHeight) {
        placeable.placeRelative(0, -overlapPx)
    }
}

@Composable
private fun LegalRepresentativeWorkshopCard(
    workshop: LegalRepresentativeWorkshopPR,
    onOpenWorkshop: (LegalRepresentativeWorkshopPR) -> Unit,
) {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = workshop.workshopName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
            )
            if (workshop.special) {
                StatusPill(
                    text = stringResource(Res.string.legal_representative_special_workshop_badge),
                    containerColor = taminColors.orangeBg,
                    contentColor = taminColors.orangeText,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            WorkshopCodeChip(
                label = stringResource(Res.string.legal_representative_workshop_code_label),
                value = workshop.workshopId,
                modifier = Modifier.weight(1f),
            )
            WorkshopCodeChip(
                label = stringResource(Res.string.legal_representative_branch_code_label),
                value = workshop.branchCode,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(
                    Res.string.legal_representative_branch_and_count,
                    workshop.branchName ?: workshop.branchCode,
                    workshop.representativeCount ?: 0,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textMuted,
            )
            AddRepresentativeChip(
                text = stringResource(Res.string.legal_representative_open_action),
                onClick = { onOpenWorkshop(workshop) },
                icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                iconPosition = IconPosition.End,
            )
        }
    }
}

/**
 * A dashed, tinted chip for a copyable code — the workshop or branch code, label stacked over the
 * value. Reuses [dashedOutline] and the blue tint pair `TrackingCodeRow` (core-ui's
 * `RecordCardParts.kt`) already established for exactly this "code you might copy" look, just
 * arranged in two lines instead of one so a pair of them sit side by side without crowding.
 */
@Composable
private fun WorkshopCodeChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val copy = rememberCopyAction(value)
    val shape = RoundedCornerShape(CornerRadius.md)

    Column(
        modifier = modifier
            .clip(shape)
            .background(taminColors.blueBg)
            .dashedOutline(taminColors.blueText.copy(0.1f), CornerRadius.md, Thickness.border)
            .clickable(onClick = copy)
            .padding(horizontal = Spacing.sm, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textMuted,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_copy),
                    contentDescription = null,
                    tint = taminColors.blueText,
                    modifier = Modifier.size(IconSize.small),
                )
                Text(
                    text = stringResource(Res.string.action_copy),
                    style = MaterialTheme.typography.labelSmall,
                    color = taminColors.blueText,
                )
            }
        }
        NumericText(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = taminColors.blueText,
        )


    }
}
