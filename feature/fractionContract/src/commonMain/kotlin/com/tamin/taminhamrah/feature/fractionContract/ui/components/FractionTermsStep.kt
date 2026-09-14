package com.tamin.taminhamrah.feature.fractionContract.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.fractionContract.ui.preview.FractionContractPreviewData
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.contractFlow.ContractTermsStepContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.fraction_contract_terms_section_subtitle
import taminx.core.core_ui.fraction_contract_terms_section_title

@Composable
internal fun FractionTermsStep(
    info: RegistrationInfoPR,
    isRulesConfirmed: Boolean,
    onRulesConfirmedChange: (Boolean) -> Unit,
    onShowRules: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.fraction_contract_terms_section_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
        )
        Text(
            text = stringResource(Res.string.fraction_contract_terms_section_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
        ContractTermsStepContent(
            info = info,
            isRulesConfirmed = isRulesConfirmed,
            onRulesConfirmedChange = onRulesConfirmedChange,
            onShowRules = onShowRules,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionTermsStepUncheckedPreview() {
    PreviewRtlThemeContent {
        FractionTermsStep(
            info = FractionContractPreviewData.registrationInfo,
            isRulesConfirmed = false,
            onRulesConfirmedChange = {},
            onShowRules = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionTermsStepCheckedPreview() {
    PreviewRtlThemeContent {
        FractionTermsStep(
            info = FractionContractPreviewData.registrationInfo,
            isRulesConfirmed = true,
            onRulesConfirmedChange = {},
            onShowRules = {},
        )
    }
}
