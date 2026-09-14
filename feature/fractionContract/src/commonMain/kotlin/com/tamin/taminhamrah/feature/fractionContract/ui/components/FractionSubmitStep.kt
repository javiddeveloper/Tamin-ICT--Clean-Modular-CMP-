package com.tamin.taminhamrah.feature.fractionContract.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.tamin.taminhamrah.feature.fractionContract.ui.preview.FractionContractPreviewData
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminCheckBox
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.fraction_contract_submit_commitment
import taminx.core.core_ui.fraction_contract_submit_rate_label
import taminx.core.core_ui.fraction_contract_submit_rate_value
import taminx.core.core_ui.fraction_contract_submit_section_subtitle
import taminx.core.core_ui.fraction_contract_submit_section_title
import taminx.core.core_ui.fraction_contract_submit_start_date_label
import taminx.core.core_ui.fraction_contract_title

@Composable
internal fun FractionSubmitStep(
    fullName: String,
    nationalId: String,
    startDate: String,
    isFinalConfirmed: Boolean,
    isSubmitting: Boolean,
    onFinalConfirmedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.cardCompact)
    val rate = stringResource(Res.string.fraction_contract_submit_rate_value)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.fraction_contract_submit_section_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
        )
        Text(
            text = stringResource(Res.string.fraction_contract_submit_section_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(colors.bgSurface)
                .border(Thickness.border, colors.border, cardShape)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = stringResource(Res.string.fraction_contract_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.fillMaxWidth(),
            )
            DetailRow(
                label = stringResource(Res.string.fraction_contract_submit_rate_label),
                value = rate,
                numeric = true,
            )
            DetailRow(
                label = stringResource(Res.string.fraction_contract_submit_start_date_label),
                value = startDate.toPersianDigits(),
                numeric = true,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(colors.bgSurface)
                .border(Thickness.border, colors.border, cardShape)
                .clickable(enabled = !isSubmitting) {
                    onFinalConfirmedChange(!isFinalConfirmed)
                }
                .padding(Spacing.lg),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            TaminCheckBox(
                checked = isFinalConfirmed,
                onCheckedChange = { if (!isSubmitting) onFinalConfirmedChange(it) },
                enabled = !isSubmitting,
            )
            Text(
                text = buildFinalCommitmentText(
                    fullName = fullName,
                    nationalId = nationalId.toPersianDigits(),
                    rate = rate,
                    startDate = startDate.toPersianDigits(),
                ),
                style = MaterialTheme.typography.bodySmall.copy(
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                ),
                color = colors.textSecondary,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = Spacing.xs),
            )
        }
    }
}

@Composable
private fun buildFinalCommitmentText(
    fullName: String,
    nationalId: String,
    rate: String,
    startDate: String,
) = buildAnnotatedString {
    val colors = LocalTaminColors.current
    val template = stringResource(
        Res.string.fraction_contract_submit_commitment,
        "\u0001",
        "\u0002",
        "\u0003",
        "\u0004",
    )
    val replacements = mapOf(
        "\u0001" to (fullName to colors.greenText),
        "\u0002" to (nationalId to colors.blueText),
        "\u0003" to (rate to colors.greenText),
        "\u0004" to (startDate to colors.blueText),
    )
    var index = 0
    while (index < template.length) {
        val nextMarker = replacements.keys
            .mapNotNull { marker ->
                val at = template.indexOf(marker, index)
                if (at >= 0) at to marker else null
            }
            .minByOrNull { it.first }
        if (nextMarker == null) {
            append(template.substring(index))
            break
        }
        val (at, marker) = nextMarker
        if (at > index) append(template.substring(index, at))
        val (value, color) = replacements.getValue(marker)
        withStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold)) {
            append(value)
        }
        index = at + marker.length
    }
}

@PreviewRtlTheme
@Composable
private fun FractionSubmitStepUncheckedPreview() {
    PreviewRtlThemeContent {
        FractionSubmitStep(
            fullName = FractionContractPreviewData.registrationInfo.fullName,
            nationalId = FractionContractPreviewData.registrationInfo.nationalId,
            startDate = "1405/06/22",
            isFinalConfirmed = false,
            isSubmitting = false,
            onFinalConfirmedChange = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionSubmitStepCheckedPreview() {
    PreviewRtlThemeContent {
        FractionSubmitStep(
            fullName = FractionContractPreviewData.registrationInfo.fullName,
            nationalId = FractionContractPreviewData.registrationInfo.nationalId,
            startDate = "1405/06/22",
            isFinalConfirmed = true,
            isSubmitting = false,
            onFinalConfirmedChange = {},
        )
    }
}
