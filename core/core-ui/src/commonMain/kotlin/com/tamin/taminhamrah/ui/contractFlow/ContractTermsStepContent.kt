package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.mapper.contracts.genderHonorific
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_terms_commitment_prefix
import taminx.core.core_ui.contract_terms_commitment_suffix
import taminx.core.core_ui.contract_terms_commitment_title
import taminx.core.core_ui.contract_terms_confirm_hint
import taminx.core.core_ui.contract_terms_view_rules

@Composable
fun ContractTermsStepContent(
    info: RegistrationInfoPR,
    isRulesConfirmed: Boolean,
    onRulesConfirmedChange: (Boolean) -> Unit,
    onShowRules: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = colors.orangeText,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    text = stringResource(Res.string.contract_terms_confirm_hint),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Button(
            onClick = onShowRules,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(Res.string.contract_terms_view_rules))
        }

        Text(
            text = stringResource(Res.string.contract_terms_commitment_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Checkbox(
                checked = isRulesConfirmed,
                onCheckedChange = onRulesConfirmedChange,
            )
            Text(
                text = buildCommitmentText(
                    genderHonorific = info.genderHonorific(),
                    fullName = info.fullName,
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun buildCommitmentText(
    genderHonorific: String,
    fullName: String,
) = buildAnnotatedString {
    append(stringResource(Res.string.contract_terms_commitment_prefix, genderHonorific))
    withStyle(SpanStyle(color = LocalTaminColors.current.greenText, fontWeight = FontWeight.Bold)) {
        append(fullName)
    }
    append(stringResource(Res.string.contract_terms_commitment_suffix))
}
