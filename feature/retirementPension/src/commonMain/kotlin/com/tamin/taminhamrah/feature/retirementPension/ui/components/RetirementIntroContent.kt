package com.tamin.taminhamrah.feature.retirementPension.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.pension.retirement.RetirementInsuredPR
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_clock
import taminx.core.core_ui.retirement_pension_age_value
import taminx.core.core_ui.retirement_pension_age_gate_passed
import taminx.core.core_ui.retirement_pension_insured_card_title
import taminx.core.core_ui.retirement_pension_intro_steps_note
import taminx.core.core_ui.retirement_pension_label_age
import taminx.core.core_ui.retirement_pension_label_branch
import taminx.core.core_ui.retirement_pension_label_full_name
import taminx.core.core_ui.retirement_pension_label_insurance_number
import taminx.core.core_ui.retirement_pension_label_national_code
import taminx.core.core_ui.retirement_pension_track_request
import taminx.core.core_ui.retirement_pension_value_placeholder

private val TrackRowHeight = 52.dp
private val ShimmerRowHeight = 18.dp

/**
 * The service's front door: who the request would be for, whether the age gate is open, what the
 * eight steps are, and the way in.
 *
 * Takes [insured] rather than the whole state so typing anywhere in the wizard cannot recompose it.
 */
@Composable
internal fun RetirementIntroContent(
    insured: RetirementInsuredPR?,
    isLoading: Boolean,
    isAgeConfirmed: Boolean,
    hasExistingRequest: Boolean,
    onOpenTrack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RetirementStepColumn(modifier = modifier) {
        AnimatedContent(
            targetState = isLoading || insured == null,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "RetirementIntroCard",
        ) { loading ->
            if (loading) RetirementInsuredShimmer() else RetirementInsuredCard(insured!!)
        }

        if (isAgeConfirmed) {
            BannerCard(
                message = stringResource(Res.string.retirement_pension_age_gate_passed),
                type = BannerType.Success,
            )
        }

        BannerCard(
            message = stringResource(Res.string.retirement_pension_intro_steps_note),
            type = BannerType.Info,
            // The design reads its informational prose in slate; only the glyph is blue.
            textColor = LocalTaminColors.current.textSecondary,
        )

        if (hasExistingRequest) {
            RetirementTrackRow(onClick = onOpenTrack)
        }
    }
}

@Composable
private fun RetirementInsuredCard(insured: RetirementInsuredPR) {
    val colors = LocalTaminColors.current
    val placeholder = stringResource(Res.string.retirement_pension_value_placeholder)

    RetirementCard {
        TaminText(
            text = stringResource(Res.string.retirement_pension_insured_card_title),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = Spacing.sm),
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_full_name),
            value = insured.fullName.ifBlank { placeholder },
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_insurance_number),
            value = insured.insuranceNumber.ifBlank { placeholder },
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_national_code),
            value = insured.nationalCode.ifBlank { placeholder },
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_age),
            value = if (insured.ageYears.isBlank()) {
                placeholder
            } else {
                stringResource(
                    Res.string.retirement_pension_age_value,
                    insured.ageYears,
                    insured.ageMonths,
                )
            },
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_branch),
            value = insured.branchName.ifBlank { placeholder },
            numeric = false,
        )
    }
}

/** Five rows of shimmer, sized like the card they stand in for so nothing jumps when it arrives. */
@Composable
private fun RetirementInsuredShimmer() {
    RetirementCard {
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.4f)
                .height(ShimmerRowHeight),
            cornerRadius = CornerRadius.sm,
        )
        repeat(INSURED_ROW_COUNT) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.28f)
                        .height(ShimmerRowHeight),
                    cornerRadius = CornerRadius.sm,
                )
                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.5f)
                        .height(ShimmerRowHeight),
                    cornerRadius = CornerRadius.sm,
                )
            }
        }
    }
}

@Composable
private fun RetirementTrackRow(onClick: () -> Unit) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.cardCompact)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TrackRowHeight)
            .clip(shape)
            .background(colors.bgSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.smPlus),
    ) {
        Box(
            modifier = Modifier
                .size(IconSize.badge)
                .background(colors.orangeBg, RoundedCornerShape(CornerRadius.lg)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_clock),
                contentDescription = null,
                tint = colors.orangeText,
                modifier = Modifier.size(IconSize.small),
            )
        }
        TaminText(
            text = stringResource(Res.string.retirement_pension_track_request),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            contentDescription = null,
            tint = colors.chevron,
            modifier = Modifier.size(IconSize.small),
        )
    }
}

private const val INSURED_ROW_COUNT = 5
