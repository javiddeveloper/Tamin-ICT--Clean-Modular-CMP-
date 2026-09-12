package com.tamin.taminhamrah.feature.profile.ui.identity.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.profile.ui.identity.model.IdentitySectionPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_check_circle
import taminx.core.core_ui.identity_verified_notice

/**
 * The identity screen's body: the registry-verified notice and the titled cards of fields.
 *
 * Each takes the resolved, immutable slice it draws rather than the screen's state, so folding the
 * header or reloading one section leaves the rest skippable.
 */

/** Confirms the record came from the civil registry rather than from the person's own entry. */
@Composable
internal fun RegistryVerifiedNotice(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
    ) {
       Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_check_circle),
            contentDescription = null,
            tint = colors.greenText,
            modifier = Modifier.size(IdentityDimens.bannerIconSize),
        )
        Text(
            text = stringResource(Res.string.identity_verified_notice),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
            textAlign = TextAlign.Center,
        )
    }
}

/** Every section of the record, in the order the design lists them. */
@Composable
internal fun IdentitySections(
    sections: ImmutableList<IdentitySectionPR>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        sections.forEach { section ->
            IdentitySectionCard(section = section)
        }
    }
}

/** One titled card: a muted caption, then the fields ruled off from each other. */
@Composable
private fun IdentitySectionCard(
    section: IdentitySectionPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(modifier = modifier.fillMaxWidth()) {
        SectionLabel(
            text = stringResource(section.title),
            color = colors.textSecondary,
            // Design: margin:20px 0 10px on the section caption.
            modifier = Modifier.padding(bottom = IdentityDimens.sectionLabelGap),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface()
                // Design rows are padding:14px 16px, and the rule between them is
                // inset by the same 16px (margin:0 16px). Spacing.md is 12dp, 4dp short.
                .padding(horizontal = IdentityDimens.rowHorizontalPadding),
        ) {
            section.fields.forEachIndexed { index, field ->
                // Names, not codes, so the value keeps the screen's reading direction.
                DetailRow(
                    label = stringResource(field.label),
                    value = field.value,
                    valueColor = if (field.isAbsent) colors.textTertiary else colors.textPrimary,
                    numeric = false,
                    verticalPadding = IdentityDimens.rowVerticalPadding,
                )
                if (index != section.fields.lastIndex) TaminDivider()
            }
        }
    }
}

/** The rows each section will hold, so the placeholder cards are the height the real ones will be. */
private val SkeletonSectionRows = listOf(2, 6, 1, 5)

/**
 * The body's outline while the record loads: the notice line, then the same titled cards with the
 * same rows, so the fields land where their placeholders were rather than after a spinner clears.
 */
@Composable
internal fun IdentitySectionsSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        ShimmerBlock(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = Spacing.sm)
                .width(ShimmerSize.subtitleWidth)
                .height(ShimmerSize.subtitleHeight),
        )
        SkeletonSectionRows.forEach { rows ->
            IdentitySectionCardSkeleton(rows = rows)
        }
    }
}

@Composable
private fun IdentitySectionCardSkeleton(rows: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        ShimmerBlock(
            modifier = Modifier
                .padding(bottom = IdentityDimens.sectionLabelGap)
                .width(ShimmerSize.sectionLabelWidth)
                .height(ShimmerSize.titleHeight),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface()
                .padding(horizontal = IdentityDimens.rowHorizontalPadding),
        ) {
            repeat(rows) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = IdentityDimens.rowVerticalPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ShimmerBlock(
                        modifier = Modifier
                            .width(ShimmerSize.labelWidth)
                            .height(ShimmerSize.subtitleHeight),
                    )
                    ShimmerBlock(
                        modifier = Modifier
                            .width(ShimmerSize.valueWidth)
                            .height(ShimmerSize.valueHeight),
                    )
                }
                if (index != rows - 1) TaminDivider()
            }
        }
    }
}
