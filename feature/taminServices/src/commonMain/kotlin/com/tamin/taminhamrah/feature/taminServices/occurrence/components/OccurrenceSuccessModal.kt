package com.tamin.taminhamrah.feature.taminServices.occurrence.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.occurrence_success_action
import taminx.core.core_ui.occurrence_success_desc
import taminx.core.core_ui.occurrence_success_title
import taminx.core.core_ui.occurrence_success_tracking_label

@Composable
internal fun OccurrenceSuccessModal(
    trackingCode: String,
    onDismiss: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    TaminConfirmationDialog(
        title = stringResource(Res.string.occurrence_success_title),
        description = stringResource(Res.string.occurrence_success_desc),
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = taminColors.chipBg)
                        .border(
                            width = 1.dp,
                            color = taminColors.blueBg,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TaminText(
                        text = stringResource(Res.string.occurrence_success_tracking_label),
                        color = taminColors.blueText
                    )
                    TaminText(text = trackingCode, color = taminColors.textPrimary)
                }
                Spacer(Modifier.height(Spacing.md))
                TaminFilledButton(
                    text = stringResource(Res.string.occurrence_success_action),
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
        icon = Icons.Default.Check,
        iconTint = taminColors.greenText,
        iconBackground = taminColors.greenBg,
    )
}

@PreviewRtlTheme
@Preview
@Composable
private fun Step6DocumentSubmitStepPreview() {
    PreviewRtlThemeContent {
        OccurrenceSuccessModal(
            trackingCode = "121213224234",
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun BannerCardDarkPreview() {
    TaminHamrahTheme(darkTheme = true) {
        OccurrenceSuccessModal(
            trackingCode = "121213224234",
            onDismiss = {}
        )
    }
}
