package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_terms_commitment_body_full
import taminx.core.core_ui.contract_terms_commitment_header
import taminx.core.core_ui.contract_terms_info_banner
import taminx.core.core_ui.contract_terms_view_rules_btn
import taminx.core.core_ui.ic_tamin_print

@Composable
fun ContractTermsStepContent(
    info: RegistrationInfoPR,
    isRulesConfirmed: Boolean,
    onRulesConfirmedChange: (Boolean) -> Unit,
    onShowRules: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.x2l)
    val bannerShape = RoundedCornerShape(CornerRadius.card)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, cardShape)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // Info Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(bannerShape)
                .background(colors.blueBg)
                .border(Thickness.border, colors.blueText.copy(alpha = 0.20f), bannerShape)
                .padding(horizontal = Spacing.md, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = buildInfoBannerText(),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.medium),
            )
        }

        // View Rules Outlined Button
        TaminOutlinedButton(
            text = stringResource(Res.string.contract_terms_view_rules_btn),
            onClick = onShowRules,
            icon = vectorResource(Res.drawable.ic_tamin_print),
            iconPosition = IconPosition.Start,
            shape = RoundedCornerShape(CornerRadius.lg),
            height = 50.dp,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Commitment Header
        Text(
            text = stringResource(Res.string.contract_terms_commitment_header),
            style = MaterialTheme.typography.labelLarge,
            color = colors.textMuted,
        )

        // Commitment Checkbox and Text
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRulesConfirmedChange(!isRulesConfirmed) },
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = buildCommitmentText(fullName = info.fullName),
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = MaterialTheme.typography.bodyMedium.lineHeight),
                color = colors.textSecondary,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = Spacing.xs),
            )
            Checkbox(
                checked = isRulesConfirmed,
                onCheckedChange = onRulesConfirmedChange,
            )
        }
    }
}

@Composable
private fun buildInfoBannerText() = buildAnnotatedString {
    val colors = LocalTaminColors.current
    val template = stringResource(Res.string.contract_terms_info_banner)
    val highlightWord = "مقررات و ضوابط انعقاد قرارداد"
    val index = template.indexOf(highlightWord)

    if (index == -1) {
        append(template)
    } else {
        append(template.substring(0, index))
        withStyle(
            SpanStyle(
                color = colors.blueText,
                fontWeight = FontWeight.Bold,
            ),
        ) {
            append(highlightWord)
        }
        append(template.substring(index + highlightWord.length))
    }
}

@Composable
private fun buildCommitmentText(fullName: String) = buildAnnotatedString {
    val colors = LocalTaminColors.current
    val template = stringResource(Res.string.contract_terms_commitment_body_full, "%1\$s")
    val marker = "%1\$s"
    val markerIndex = template.indexOf(marker)

    if (markerIndex == -1) {
        append(template)
    } else {
        append(template.substring(0, markerIndex))
        withStyle(
            SpanStyle(
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
            ),
        ) {
            append(fullName)
        }
        append(template.substring(markerIndex + marker.length))
    }
}

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun ContractTermsStepContentUncheckedPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ContractTermsStepContent(
            info = RegistrationInfoPR(
                fullName = "علی محمدی",
                nationalId = "0012345678",
                birthDateFormatted = "1375/04/15",
                insuranceId = "12345678",
                genderCode = "01",
                address = "تهران",
                zipCode = "1234567890",
                phoneNumber = "02166001234",
                mobileNumber = "09121234567",
                hasMobile = true,
            ),
            isRulesConfirmed = false,
            onRulesConfirmedChange = {},
            onShowRules = {},
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun ContractTermsStepContentCheckedPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ContractTermsStepContent(
            info = RegistrationInfoPR(
                fullName = "علی محمدی",
                nationalId = "0012345678",
                birthDateFormatted = "1375/04/15",
                insuranceId = "12345678",
                genderCode = "01",
                address = "تهران",
                zipCode = "1234567890",
                phoneNumber = "02166001234",
                mobileNumber = "09121234567",
                hasMobile = true,
            ),
            isRulesConfirmed = true,
            onRulesConfirmedChange = {},
            onShowRules = {},
        )
    }
}
