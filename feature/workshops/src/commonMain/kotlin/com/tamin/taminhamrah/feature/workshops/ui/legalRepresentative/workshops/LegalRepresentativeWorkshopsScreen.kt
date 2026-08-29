package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.workshops

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeHeader
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeHeroSubtitle
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeIdentitySummaryCard
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.legal_representative_branch_and_count
import taminx.core.core_ui.legal_representative_branch_code_label
import taminx.core.core_ui.legal_representative_empty_title
import taminx.core.core_ui.legal_representative_hub_subtitle
import taminx.core.core_ui.legal_representative_info_banner
import taminx.core.core_ui.legal_representative_open_action
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
        LegalRepresentativeHeader(onBackClicked = onBackClicked, heroCardOverlap = HeroCardOverlap) {
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
                            Text(
                                text = stringResource(Res.string.legal_representative_info_banner),
                                style = MaterialTheme.typography.bodySmall,
                                color = taminColors.textMuted,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .taminSurface()
                                    .padding(Spacing.md),
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
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = workshop.workshopName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = taminColors.textPrimary,
        )
        DetailRow(
            label = stringResource(Res.string.legal_representative_workshop_code_label),
            value = workshop.workshopId,
            copyValue = workshop.workshopId,
        )
        DetailRow(
            label = stringResource(Res.string.legal_representative_branch_code_label),
            value = workshop.branchCode,
            copyValue = workshop.branchCode,
        )
        Text(
            text = stringResource(
                Res.string.legal_representative_branch_and_count,
                workshop.branchName ?: workshop.branchCode,
                workshop.representativeCount ?: 0,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.textMuted,
        )
        TaminOutlinedButton(
            text = stringResource(Res.string.legal_representative_open_action),
            onClick = { onOpenWorkshop(workshop) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
