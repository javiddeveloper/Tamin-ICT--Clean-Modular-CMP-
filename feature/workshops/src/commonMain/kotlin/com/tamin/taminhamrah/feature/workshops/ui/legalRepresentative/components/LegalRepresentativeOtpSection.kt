package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.OtpInputField
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.legal_representative_otp_sent_message

private const val OTP_LENGTH = 5
private const val COUNTDOWN_SECONDS = 5 * 60

/**
 * The request → countdown → code-entry widget shared by the workshop-level OTP screen and the
 * agent-level OTP embedded in the add/edit form. [sentToLabel] is `null` before a code is
 * requested; once non-null the request button is replaced by the countdown and input field.
 */
@Composable
internal fun LegalRepresentativeOtpSection(
    isTicketRequested: Boolean,
    isRequestingTicket: Boolean,
    otpCode: String,
    otpError: String?,
    sentToLabel: String?,
    requestLabel: String,
    onRequestTicket: () -> Unit,
    onOtpChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    if (!isTicketRequested) {
        LoadingButton(
            text = requestLabel,
            onClick = onRequestTicket,
            isLoading = isRequestingTicket,
            icon = Icons.AutoMirrored.Filled.Send,
            modifier = modifier.fillMaxWidth(),
        )
        return
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = sentToLabel.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textMuted,
            )
            OtpCountdown()
        }
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = stringResource(Res.string.legal_representative_otp_sent_message),
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.textMuted,
        )
        Spacer(Modifier.height(Spacing.sm))
        OtpInputField(
            value = otpCode,
            onValueChange = onOtpChanged,
            length = OTP_LENGTH,
            error = otpError != null,
            errorMessage = otpError,
            leadingIcon = Icons.Outlined.Lock,
        )
    }
}

/** Local, cosmetic-only countdown — the request button re-enabling is server-driven, not this. */
@Composable
private fun OtpCountdown() {
    var secondsLeft by remember { mutableStateOf(COUNTDOWN_SECONDS) }
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }
    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val label = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    Text(
        text = label.toPersianDigits(),
        style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum", fontWeight = FontWeight.Bold),
        color = LocalTaminColors.current.blueText,
    )
}
