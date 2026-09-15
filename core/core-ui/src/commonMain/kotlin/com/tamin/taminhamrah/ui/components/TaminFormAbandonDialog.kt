package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.form_abandon_description
import taminx.core.core_ui.form_abandon_leave
import taminx.core.core_ui.form_abandon_stay
import taminx.core.core_ui.form_abandon_title
import taminx.core.core_ui.ic_tamin_alert_triangle

/**
 * Warns before leaving a multi-step form with unsaved input.
 *
 * [formName] is interpolated into the shared title/body templates
 * (e.g. «انعقاد قرارداد بیمه اختیاری»). Stay keeps the user on the form;
 * abandon runs the caller's exit navigation.
 */
@Composable
fun TaminFormAbandonDialog(
    formName: String,
    onStay: () -> Unit,
    onAbandon: () -> Unit,
    onDismissRequest: () -> Unit = onStay,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl),
            shape = RoundedCornerShape(CornerRadius.cardCompact),
            colors = CardDefaults.cardColors(containerColor = colors.bgSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = Elevation.md),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(IconSize.xxlarge)
                        .clip(RoundedCornerShape(CornerRadius.cardCompact))
                        .background(colors.orangeBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_tamin_alert_triangle),
                        contentDescription = null,
                        tint = colors.orangeText,
                        modifier = Modifier.size(IconSize.badge),
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                TaminText(
                    text = stringResource(Res.string.form_abandon_title, formName),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                TaminText(
                    text = stringResource(Res.string.form_abandon_description, formName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(Spacing.xl))

                // RTL: first child sits on the right — Stay (primary) then Leave (danger outline).
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    TaminFilledButton(
                        text = stringResource(Res.string.form_abandon_stay),
                        onClick = onStay,
                        modifier = Modifier.weight(1f),
                    )
                    TaminOutlinedButton(
                        text = stringResource(Res.string.form_abandon_leave),
                        onClick = onAbandon,
                        modifier = Modifier.weight(1f),
                        borderColor = colors.dangerBorder,
                        contentColor = colors.dangerText,
                    )
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun TaminFormAbandonDialogPreview() {
    PreviewRtlThemeContent {
        TaminFormAbandonDialog(
            formName = "انعقاد قرارداد بیمه اختیاری",
            onStay = {},
            onAbandon = {},
        )
    }
}
