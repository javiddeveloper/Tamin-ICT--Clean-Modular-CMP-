package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.tamin.taminhamrah.feature.treatment.ui.components.CoverageBadge
import com.tamin.taminhamrah.feature.treatment.ui.components.InsuranceCard
import com.tamin.taminhamrah.feature.treatment.ui.components.InsuranceCardCarousel
import com.tamin.taminhamrah.feature.treatment.ui.components.insuranceCardGradient
import com.tamin.taminhamrah.feature.treatment.ui.components.quickAccessGradient
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.CoverageStatus
import com.tamin.taminhamrah.feature.treatment.ui.model.PatientItem
import com.tamin.taminhamrah.feature.treatment.ui.model.coverageStatusOf
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemBadge
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.icons.TaminIcons
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminCoverageBadgeBg
import com.tamin.taminhamrah.ui.theme.TaminCoverageBadgeFg
import com.tamin.taminhamrah.ui.theme.TaminRed
import com.tamin.taminhamrah.ui.theme.TaminRedDark
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf

/**
 * The stacked sections of the treatment hub, kept out of [TreatmentScreen] so that file
 * stays a readable description of the screen's shape rather than its every detail.
 */

private val CARD_LOADING_HEIGHT = 160.dp

/** Shown in place of an amount that has not loaded, so a blank never reads as zero. */
private const val UNKNOWN_AMOUNT = "—"

/**
 * The insured-person carousel, or the loading and empty states that stand in for it.
 */
@Composable
internal fun PatientCarousel(
    state: TreatmentUiState,
    patients: List<PatientItem>,
    pagerState: PagerState,
    onShowEntitlementReason: (String) -> Unit,
    onRetry: () -> Unit = {},
) {
    when {
        state.isLoading && patients.isEmpty() -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(CARD_LOADING_HEIGHT),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = LocalTaminColors.current.teal)
        }

        // A failure or an empty result still renders a card, so the carousel slot never
        // collapses into a bare line of text.
        patients.isEmpty() -> PatientPlaceholderCard(
            message = state.error ?: "بیمه‌شده‌ای برای نمایش وجود ندارد",
            isError = state.error != null,
            onRetry = onRetry,
        )

        else -> InsuranceCardCarousel(
            pageCount = patients.size,
            pagerState = pagerState,
        ) { page ->
            PatientCard(
                patient = patients[page],
                status = coverageStatusOf(patients[page], state.deservedList),
                // Position among dependants only, so each keeps its own color whether
                //  a main insured person is present.
                dependantOrdinal = patients.take(page).count { it.isDependent },
                onShowEntitlementReason = onShowEntitlementReason,
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
            .height(CARD_LOADING_HEIGHT),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = Spacing.page),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Icon(
                imageVector = if (isError) TaminIcons.Cross else TaminIcons.HealthProfile,
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
                    Text(text = "تلاش دوباره", color = colors.teal)
                }
            }
        }
    }
}

@Composable
private fun PatientCard(
    patient: PatientItem,
    status: CoverageStatus,
    dependantOrdinal: Int,
    onShowEntitlementReason: (String) -> Unit,
) {
    val style = status.cardStyle(
        isDependent = patient.isDependent,
        dependantOrdinal = dependantOrdinal,
    )
    InsuranceCard(
        holderName = patient.fullName,
        nationalId = patient.nationalId,
        coverageLabel = style.label,
        background = style.background,
        coverageBadge = style.badge,
        footerAction = (status as? CoverageStatus.Rejected)?.let { rejected ->
            { EntitlementReasonChip(onClick = { onShowEntitlementReason(rejected.reason) }) }
        },
    )
}

/** How a [CoverageStatus] is dressed on the card: wording, gradient and badge. */
private data class CoverageCardStyle(
    val label: String,
    val background: Brush,
    val badge: (@Composable () -> Unit)?,
)

/**
 * Covered is the only state that shows the person's own card identity — pending and
 * rejected deliberately override it so status stays readable at a glance.
 */
@Composable
private fun CoverageStatus.cardStyle(
    isDependent: Boolean,
    dependantOrdinal: Int,
): CoverageCardStyle {
    val colors = LocalTaminColors.current
    return when (this) {
        CoverageStatus.Pending -> CoverageCardStyle(
            label = "در حال استعلام وضعیت استحقاق…",
            background = Brush.verticalGradient(listOf(colors.textMuted, colors.chevron)),
            badge = null,
        )

        is CoverageStatus.Rejected -> CoverageCardStyle(
            label = "فاقد استحقاق درمان",
            background = Brush.verticalGradient(listOf(TaminRedDark, TaminRed)),
            badge = {
                CoverageBadge(
                    icon = TaminIcons.Cross,
                    containerColor = Color.White,
                    contentColor = TaminRedDark,
                )
            },
        )

        CoverageStatus.Covered -> CoverageCardStyle(
            label = "وضعیت حمایت‌های درمانی: برخوردار هستید",
            background = insuranceCardGradient(isDependent, dependantOrdinal),
            badge = {
                CoverageBadge(
                    icon = TaminIcons.Verified,
                    containerColor = TaminCoverageBadgeBg,
                    contentColor = TaminCoverageBadgeFg,
                )
            },
        )
    }
}

@Composable
private fun EntitlementReasonChip(onClick: () -> Unit) {
    Text(
        text = "علت",
        style = MaterialTheme.typography.labelMedium,
        color = Color.White,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.22f), CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
    )
}

/** The prominent destinations above the service grid. */
@Composable
internal fun TreatmentQuickAccess(
    healthProfileCompleted: Boolean?,
    onOpenMedicalRecords: () -> Unit,
    onOpenHealthProfile: () -> Unit,
    onOpenCenters: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier.padding(horizontal = Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        SectionLabel(text = "دسترسی سریع")

        // One ListGroupView per row rather than a single grouped list: the design keeps the
        // three as separate cards with gaps, and only the first carries the gradient.
        ListGroupView(
            modifier = Modifier.background(quickAccessGradient(), RoundedCornerShape(CornerRadius.card)),
            containerShape = RoundedCornerShape(CornerRadius.card),
            containerBackgroundColor = Color.Transparent,
            items = persistentListOf(
                ListItemData(
                    title = "سوابق درمانی من",
                    subtitle = "تاریخچهٔ نسخه، ویزیت، پاراکلینیک و آزمایش",
                    leadingIconPainter = rememberVectorPainter(TaminIcons.MedicalRecords),
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
            containerShape = RoundedCornerShape(CornerRadius.card),
            items = persistentListOf(
                ListItemData(
                    title = "پروندهٔ سلامت من",
                    subtitle = "خوداظهاری سلامت و اطلاعات پزشکی",
                    leadingIconPainter = rememberVectorPainter(TaminIcons.HealthProfile),
                    colors = ListItemColors(
                        leadingIconBackgroundColor = colors.blueBg,
                        leadingIconTintColor = colors.blueText,
                    ),
                    // No pill until the status is known, so the card never guesses either way.
                    badge = healthProfileCompleted?.let { completed ->
                        ListItemBadge(
                            text = if (completed) "تکمیل شده" else "تکمیل نشده",
                            backgroundColor = if (completed) colors.greenBg else colors.orangeBg,
                            textColor = if (completed) colors.greenText else colors.orangeText,
                        )
                    },
                    onClick = onOpenHealthProfile,
                ),
            ),
        )

        ListGroupView(
            containerShape = RoundedCornerShape(CornerRadius.card),
            items = persistentListOf(
                ListItemData(
                    title = "مراکز درمانی طرف قرارداد",
                    subtitle = "جست‌وجوی بیمارستان و داروخانه",
                    leadingIconPainter = rememberVectorPainter(TaminIcons.MedicalCenters),
                    colors = ListItemColors(
                        leadingIconBackgroundColor = colors.greenBg,
                        leadingIconTintColor = colors.teal,
                    ),
                    onClick = onOpenCenters,
                ),
            ),
        )
    }
}

/** The three-up grid of treatment services. */
@Composable
internal fun TreatmentCategories(
    onOpenPrescriptions: () -> Unit,
    onOpenMedicalApprovals: () -> Unit,
    onOpenMiscClaims: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.padding(horizontal = Spacing.page),
        horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        CategoryTile(
            label = "نسخه‌های الکترونیک",
            icon = TaminIcons.Prescriptions,
            iconTint = colors.blueText,
            iconBackground = Brush.linearGradient(listOf(colors.blueBg, colors.blueBg)),
            onClick = onOpenPrescriptions,
            modifier = Modifier.weight(1f),
        )
        CategoryTile(
            label = "تاییدیه‌های پزشکی",
            icon = TaminIcons.MedicalApprovals,
            iconTint = colors.teal,
            iconBackground = Brush.linearGradient(listOf(colors.greenBg, colors.greenBg)),
            onClick = onOpenMedicalApprovals,
            modifier = Modifier.weight(1f),
        )
        CategoryTile(
            label = "خسارت متفرقه",
            icon = TaminIcons.MiscClaims,
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
) {
    CostSummaryCard(
        title = "هزینه‌های سال ${PersianDateFormatter.currentJalaliYear()} (سال جاری)"
            .toPersianDigits(),
        insuredShareLabel = "سهم بیمه‌شده",
        insuredShareAmount = insuredShare?.toPriceFormat() ?: UNKNOWN_AMOUNT,
        organizationShareLabel = "سهم سازمان",
        organizationShareAmount = organizationShare?.toPriceFormat() ?: UNKNOWN_AMOUNT,
        modifier = Modifier.padding(horizontal = Spacing.page),
    )
}
