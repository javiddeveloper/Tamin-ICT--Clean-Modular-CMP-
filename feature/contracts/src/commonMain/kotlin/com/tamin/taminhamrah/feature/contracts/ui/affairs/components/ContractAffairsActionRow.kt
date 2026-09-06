package com.tamin.taminhamrah.feature.contracts.ui.affairs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminNavy300
import com.tamin.taminhamrah.ui.theme.TaminNavy900
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_affairs_count_label
import taminx.core.core_ui.contract_affairs_new_contract

@Composable
internal fun ContractAffairsActionRow(
    contractCount: Int,
    onNewContractClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier.fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TaminPrimaryButton(
            background = Brush.linearGradient(
                colors = listOf(
                    TaminNavy300,
                    TaminNavy900
                )
            ),
            text = stringResource(Res.string.contract_affairs_new_contract),
            onClick = onNewContractClicked,
            icon = Icons.Default.Add,
            iconAtStart = true,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(Spacing.sm))
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    color = colors.bgSurface,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = colors.border,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp)
        ) {
            TaminText(
                text = contractCount.toString(),
                color = colors.textPrimary
            )
            TaminText(
                text = stringResource(Res.string.contract_affairs_count_label),
                color = colors.textMuted,
                fontSize = 12.sp
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractAffairsActionRowPreviewLight() {
    PreviewRtlThemeContent {
        ContractAffairsActionRow(
            contractCount = 4,
            onNewContractClicked = {},
            modifier = Modifier.padding(vertical = Spacing.lg),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractAffairsActionRowPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractAffairsActionRow(
            contractCount = 4,
            onNewContractClicked = {},
            modifier = Modifier.padding(vertical = Spacing.lg),
        )
    }
}
