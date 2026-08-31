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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
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

    Column(modifier = Modifier.fillMaxSize()) {
        LegalRepresentativeHeader(
            onBackClicked = onBackClicked,
            heroCardOverlap = HeroCardOverlap
        ) {
            LegalRepresentativeHeroSubtitle(stringResource(Res.string.legal_representative_hub_subtitle))
        }

        // The card rides up into the header's reserved extra space; wrapping it together with
        // everything below in a single offset keeps their normal spacing intact instead of
        // opening a gap where the card used to sit.
        Column(modifier = Modifier.fillMaxSize().offset(y = -HeroCardOverlap)) {
            LegalRepresentativeIdentitySummaryCard(
                fullName = uiState.fullName,
                workshopCount = uiState.workshops.size,
                modifier = Modifier.padding(horizontal = Spacing.lg),
            )

            Box(modifier = Modifier.fillMaxSize()) {
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
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = Spacing.lg,
                            end = Spacing.lg,
                            top = Spacing.lg,
                            bottom = Spacing.lg + HeroCardOverlap,
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
            }
        }
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
