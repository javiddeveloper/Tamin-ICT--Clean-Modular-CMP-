package com.tamin.taminhamrah.feature.retirementPension.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementFormError
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_document_lines
import taminx.core.core_ui.retirement_pension_consent_label
import taminx.core.core_ui.retirement_pension_rules_body
import taminx.core.core_ui.retirement_pension_rules_link

private val RulesLinkHeight = 44.dp

/** Step 1 — read the rules, then undertake to follow them. */
@Composable
internal fun RetirementRulesStep(
    applicantName: String,
    consentAccepted: Boolean,
    error: RetirementFormError?,
    onViewRules: () -> Unit,
    onConsentChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    RetirementStepColumn(modifier = modifier) {
        RetirementCard {
            TaminText(
                text = stringResource(Res.string.retirement_pension_rules_body),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
            RulesLink(onClick = onViewRules, modifier = Modifier.padding(top = Spacing.smPlus))
        }

        RetirementConsentRow(
            text = stringResource(Res.string.retirement_pension_consent_label, applicantName),
            checked = consentAccepted,
            isError = error != null,
            onCheckedChange = onConsentChange,
        )
        if (error != null) RetirementErrorLine(error)
    }
}

@Composable
private fun RulesLink(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.listRow)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(RulesLinkHeight)
            .clip(shape)
            .background(colors.blueBg)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_document_lines),
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(IconSize.small),
        )
        TaminText(
            text = stringResource(Res.string.retirement_pension_rules_link),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.blueText,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(IconSize.small),
        )
    }
}
