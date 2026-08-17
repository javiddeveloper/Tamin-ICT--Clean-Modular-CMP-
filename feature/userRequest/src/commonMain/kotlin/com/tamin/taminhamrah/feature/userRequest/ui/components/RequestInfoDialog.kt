package com.tamin.taminhamrah.feature.userRequest.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes
import taminx.feature.userrequest.generated.resources.user_request_action_got_it
import taminx.feature.userrequest.generated.resources.user_request_info_dialog_title

@Composable
fun RequestInfoDialog(
    message: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.x2l)),
            color = taminColors.bgSurface,
            shadowElevation = Elevation.button
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.page),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                TaminText(
                    text = stringResource(UserRequestRes.string.user_request_info_dialog_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = taminColors.textPrimary
                )

                TaminText(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                    color = taminColors.textSecondary
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                TaminFilledButton(
                    text = stringResource(UserRequestRes.string.user_request_action_got_it),
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun RequestInfoDialogPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        RequestInfoDialog(
            message = "در نسخه نهایی، این دکمه شما را به صفحه جزئیات همین درخواست با کد پیگیری ۱۰۴۸۳۸۴۰۰۲ منتقل می‌کنند.",
            onDismissRequest = {}
        )
    }
}

