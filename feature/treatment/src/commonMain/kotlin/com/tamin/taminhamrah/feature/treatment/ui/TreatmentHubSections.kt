package com.tamin.taminhamrah.feature.treatment.ui

import com.tamin.taminhamrah.feature.treatment.ui.components.raisedCard
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.treatment.ui.components.CategoryTile
import com.tamin.taminhamrah.feature.treatment.ui.components.CostSummaryCard
import com.tamin.taminhamrah.feature.treatment.ui.components.InsuranceCardCarousel
import com.tamin.taminhamrah.feature.treatment.ui.components.PatientCard
import com.tamin.taminhamrah.feature.treatment.ui.components.quickAccessGradient
import com.tamin.taminhamrah.feature.treatment.ui.components.raisedShadow
import com.tamin.taminhamrah.feature.treatment.ui.model.PatientCardItemPR
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemBadge
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.util.ExternalAppLauncher
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_retry
import taminx.core.core_ui.amount_unknown
import taminx.core.core_ui.category_approvals
import taminx.core.core_ui.category_misc_claims
import taminx.core.core_ui.category_prescriptions
import taminx.core.core_ui.hub_centers_subtitle
import taminx.core.core_ui.hub_centers_title
import taminx.core.core_ui.hub_costs_title
import taminx.core.core_ui.hub_empty_patients
import taminx.core.core_ui.hub_health_profile_completed
import taminx.core.core_ui.hub_health_profile_incomplete
import taminx.core.core_ui.hub_health_profile_subtitle
import taminx.core.core_ui.hub_health_profile_title
import taminx.core.core_ui.hub_quick_access
import taminx.core.core_ui.hub_records_subtitle
import taminx.core.core_ui.hub_records_title
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_health_profile
import taminx.core.core_ui.ic_tamin_medical_approvals
import taminx.core.core_ui.ic_tamin_medical_centers
import taminx.core.core_ui.ic_tamin_medical_records
import taminx.core.core_ui.ic_tamin_misc_claims
import taminx.core.core_ui.ic_tamin_prescriptions
import taminx.core.core_ui.share_insured
import taminx.core.core_ui.share_organization

/**
 * The stacked sections of the treatment hub, kept out of [TreatmentScreen] so that file
 * stays a readable description of the screen's shape rather than its every detail.
 */

/**
 * The insured-person carousel, or the loading and empty states that stand in for it.
 *
 * Takes the resolved [cards] and the two flags it actually draws rather than the hub's state, so
 * that loading a total or reporting a selection leaves it untouched.
 */
@Composable
internal fun PatientCarousel(
    cards: ImmutableList<PatientCardItemPR>,
    isLoading: Boolean,
    error: String?,
    pagerState: PagerState,
    onRetry: () -> Unit = {},
    collapseProgress: () -> Float = { 0f },
) {
    when {
        isLoading && cards.isEmpty() -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TreatmentDimens.cardLoadingHeight)
                .raisedCard(CornerRadius.card)
                .shimmer(),
        )

        // A failure or an empty result still renders a card, so the carousel slot never
        // collapses into a bare line of text.
        cards.isEmpty() -> PatientPlaceholderCard(
            message = error ?: stringResource(Res.string.hub_empty_patients),
            isError = error != null,
            onRetry = onRetry,
        )

        else -> {
            val cardLambda: @Composable (Int) -> Unit = remember(cards, collapseProgress) {
                { page ->
                    cards.getOrNull(page)?.let { card ->
                        PatientCard(
                            patient = card.patient,
                            status = card.coverage,
                            dependantOrdinal = card.dependantOrdinal,
                            collapseProgress = collapseProgress,
                        )
                    }
                }
            }
            InsuranceCardCarousel(
                pageCount = cards.size,
                pagerState = pagerState,
                card = cardLambda,
            )
        }
    }
}

/**
 * Stands in for the insurance card when there is nobody to show.
 *
 * Keeps the carousel's footprint so the hub does not jump between states, and offers a retry when
 * the cause was a failure rather than a genuinely empty result.
 */
@Composable
private fun PatientPlaceholderCard(
    message: String,
    isError: Boolean,
    onRetry: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val accent = if (isError) colors.dangerText else colors.textSecondary
    val container = if (isError) colors.dangerBg else colors.bgSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page)
            .clip(RoundedCornerShape(CornerRadius.card))
            .background(container)
            .border(
                width = 1.dp,
                color = if (isError) colors.dangerBorder else colors.border,
                shape = RoundedCornerShape(CornerRadius.card),
            )
            .height(TreatmentDimens.cardLoadingHeight),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = Spacing.page),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Icon(
                imageVector = if (isError) vectorResource(Res.drawable.ic_tamin_cross) else vectorResource(Res.drawable.ic_tamin_health_profile),
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(IconSize.medium),
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = accent,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            if (isError) {
                TextButton(onClick = onRetry) {
                    Text(text = stringResource(Res.string.action_retry), color = colors.teal)
                }
            }
        }
    }
}

/** The prominent destinations above the service grid. */
@Composable
internal fun TreatmentQuickAccess(
    healthProfileCompleted: Boolean?,
    onOpenMedicalRecords: () -> Unit,
    onOpenHealthProfile: () -> Unit,
) {
    val colors = LocalTaminColors.current
    // The contracted-centers directory is a web page the organization maintains, not a screen of
    // ours, so it opens in the browser on both platforms. Remembered so the item's onClick stays
    // the same instance across recompositions and the list item keeps skipping.
    val launcher = remember { ExternalAppLauncher() }
    val openCenters = remember(launcher) { { launcher.openUrl(CONTRACTED_CENTERS_URL) } }
    Column(
        modifier = Modifier.padding(horizontal = Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        SectionLabel(text = stringResource(Res.string.hub_quick_access))

        // One ListGroupView per row rather than a single grouped list: the design keeps the
        // three as separate cards with gaps, and only the first carries the gradient.
        ListGroupView(
            modifier = Modifier
                .raisedShadow(CornerRadius.card)
                .background(quickAccessGradient(), RoundedCornerShape(CornerRadius.card)),
            containerShape = RoundedCornerShape(CornerRadius.card),
            containerBackgroundColor = Color.Transparent,
            items = persistentListOf(
                ListItemData(
                    title = stringResource(Res.string.hub_records_title),
                    subtitle = stringResource(Res.string.hub_records_subtitle),
                    leadingIconPainter = rememberVectorPainter(vectorResource(Res.drawable.ic_tamin_medical_records)),
                    titleStyle = MaterialTheme.typography.titleMedium,
                    colors = ListItemColors(
                        titleColor = Color.White,
                        subtitleColor = Color.White.copy(alpha = 0.8f),
                        leadingIconBackgroundColor = Color.White.copy(alpha = 0.16f),
                        leadingIconTintColor = Color.White,
                    ),
                    onClick = onOpenMedicalRecords,
                ),
            ),
        )

        ListGroupView(
            modifier = Modifier.raisedShadow(CornerRadius.card),
            containerShape = RoundedCornerShape(CornerRadius.card),
            items = persistentListOf(
                ListItemData(
                    title = stringResource(Res.string.hub_health_profile_title),
                    subtitle = stringResource(Res.string.hub_health_profile_subtitle),
                    leadingIconPainter = rememberVectorPainter(vectorResource(Res.drawable.ic_tamin_health_profile)),
                    colors = ListItemColors(
                        leadingIconBackgroundColor = colors.blueBg,
                        leadingIconTintColor = colors.blueText,
                    ),
                    // No pill until the status is known, so the card never guesses either way.
                    badge = healthProfileCompleted?.let { completed ->
                        ListItemBadge(
                            text = stringResource(
            if (completed) {
                Res.string.hub_health_profile_completed
            } else {
                Res.string.hub_health_profile_incomplete
            },
        ),
                            backgroundColor = if (completed) colors.greenBg else colors.orangeBg,
                            textColor = if (completed) colors.greenText else colors.orangeText,
                        )
                    },
                    onClick = onOpenHealthProfile,
                ),
            ),
        )

        ListGroupView(
            modifier = Modifier.raisedShadow(CornerRadius.card),
            containerShape = RoundedCornerShape(CornerRadius.card),
            items = persistentListOf(
                ListItemData(
                    title = stringResource(Res.string.hub_centers_title),
                    subtitle = stringResource(Res.string.hub_centers_subtitle),
                    leadingIconPainter = rememberVectorPainter(vectorResource(Res.drawable.ic_tamin_medical_centers)),
                    colors = ListItemColors(
                        leadingIconBackgroundColor = colors.greenBg,
                        leadingIconTintColor = colors.teal,
                    ),
                    onClick = openCenters,
                ),
            ),
        )
    }
}

/** The three-up grid of treatment services. */
@Composable
internal fun TreatmentCategories(
    onOpenPrescriptions: () -> Unit,
    onOpenMiscClaims: () -> Unit,
    onOpenApprovals: () -> Unit = {},
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.page),
        horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        CategoryTile(
            label = stringResource(Res.string.category_prescriptions),
            icon = vectorResource(Res.drawable.ic_tamin_prescriptions),
            iconTint = colors.blueText,
            iconBackground = Brush.linearGradient(listOf(colors.blueBg, colors.blueBg)),
            onClick = onOpenPrescriptions,
            modifier = Modifier.weight(1f),
        )
        CategoryTile(
            label = stringResource(Res.string.category_approvals),
            icon = vectorResource(Res.drawable.ic_tamin_medical_approvals),
            iconTint = colors.teal,
            iconBackground = Brush.linearGradient(listOf(colors.greenBg, colors.greenBg)),
            onClick = onOpenApprovals,
            modifier = Modifier.weight(1f),
        )
        CategoryTile(
            label = stringResource(Res.string.category_misc_claims),
            icon = vectorResource(Res.drawable.ic_tamin_misc_claims),
            iconTint = colors.orangeText,
            iconBackground = Brush.linearGradient(listOf(colors.orangeBg, colors.orangeBg)),
            onClick = onOpenMiscClaims,
            modifier = Modifier.weight(1f),
        )
    }
}


/**
 * Current-year spend, split between the insured person and the organization. Amounts show
 * a dash until the totals load, so an unloaded card never reads as zero spend.
 */
@Composable
internal fun TreatmentCostSummary(
    insuredShare: Long?,
    organizationShare: Long?,
    isLoading: Boolean,
) {
    CostSummaryCard(
        title = stringResource(
            Res.string.hub_costs_title,
            PersianDateFormatter.currentJalaliYear(),
        )
            .toPersianDigits(),
        insuredShareLabel = stringResource(Res.string.share_insured),
        // Shimmers while the request is out; once it is back, a still-missing total is a
        // genuine absence and reads as one.
        insuredShareAmount = insuredShare?.toPriceFormat()
            ?: stringResource(Res.string.amount_unknown).takeIf { !isLoading },
        organizationShareLabel = stringResource(Res.string.share_organization),
        organizationShareAmount = organizationShare?.toPriceFormat()
            ?: stringResource(Res.string.amount_unknown).takeIf { !isLoading },
        modifier = Modifier.padding(horizontal = Spacing.page),
    )
}

/**
 * The organization's directory of contracted treatment centres.
 *
 * A page on tamin.ir rather than an endpoint: there is no centers API, and the published list is
 * what the branches actually keep current.
 */
private const val CONTRACTED_CENTERS_URL = "https://tamin.ir/html/item/4474"
