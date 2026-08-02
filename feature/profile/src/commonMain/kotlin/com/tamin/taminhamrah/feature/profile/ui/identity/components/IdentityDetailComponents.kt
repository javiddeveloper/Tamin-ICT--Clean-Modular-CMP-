package com.tamin.taminhamrah.feature.profile.ui.identity.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
        Text(
            text = stringResource(Res.string.identity_verified_notice),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
            textAlign = TextAlign.Center,
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_check_circle),
            contentDescription = null,
            tint = colors.greenText,
            modifier = Modifier.size(IdentityDimens.bannerIconSize),
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
            modifier = Modifier.padding(bottom = Spacing.sm),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface()
                .padding(horizontal = Spacing.md),
        ) {
            section.fields.forEachIndexed { index, field ->
                // Names, not codes, so the value keeps the screen's reading direction.
                DetailRow(
                    label = stringResource(field.label),
                    value = field.value,
                    valueColor = if (field.isAbsent) colors.textTertiary else colors.textPrimary,
                    numeric = false,
                    verticalPadding = Spacing.sm,
                )
                if (index != section.fields.lastIndex) TaminDivider()
            }
        }
    }
}
