package com.tamin.taminhamrah.feature.profile.ui.activeRelation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.active_relation_success_button
import taminx.core.core_ui.active_relation_success_description
import taminx.core.core_ui.active_relation_success_title
import taminx.core.core_ui.ic_tamin_check_circle

@Composable
internal fun CertificateSuccessDialog(
    onDismiss: () -> Unit
) {
    val taminColors = LocalTaminColors.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xxl),
            shape = RoundedCornerShape(CornerRadius.x2l),
            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(taminColors.greenBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_tamin_check_circle),
                        contentDescription = null,
                        tint = taminColors.greenText,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                TaminText(
                    text = stringResource(Res.string.active_relation_success_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = taminColors.springGreenText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                TaminText(
                    text = stringResource(Res.string.active_relation_success_description),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 24.sp
                    ),
                    color = taminColors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.xxl))

                TaminFilledButton(
                    text = stringResource(Res.string.active_relation_success_button),
                    onClick = onDismiss,
                    background = androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(taminColors.greenText, taminColors.greenText)
                    ),
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(CornerRadius.full)
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewCertificateSuccessDialogLight() {
    PreviewRtlThemeContent {
        CertificateSuccessDialog(onDismiss = {})
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewCertificateSuccessDialogDark() {
    TaminHamrahTheme(darkTheme = true) {
        CertificateSuccessDialog(onDismiss = {})
    }
}
