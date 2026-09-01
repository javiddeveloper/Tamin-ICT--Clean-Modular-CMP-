package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.OtpInputField
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_email
import taminx.core.core_ui.legal_representative_otp_sent_message
import kotlin.time.Duration.Companion.milliseconds

private const val OTP_LENGTH = 5
private const val COUNTDOWN_SECONDS = 5 * 60

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
    requestButtonBackground: Brush? = null,
    requestButtonContentColor: Color? = null,
    titleLabel: String? = null,
    showSentMessage: Boolean = true,
    onExpired: () -> Unit = {},
) {
    val taminColors = LocalTaminColors.current

    if (!isTicketRequested) {
        LoadingButton(
            text = requestLabel,
            onClick = onRequestTicket,
            isLoading = isRequestingTicket,
            icon = vectorResource(Res.drawable.ic_email),
            modifier = modifier.fillMaxWidth(),
            background = requestButtonBackground,
            contentColor = requestButtonContentColor,
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        titleLabel?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textMuted,
            )
        }
        if (showSentMessage) {
            Text(
                text = stringResource(Res.string.legal_representative_otp_sent_message),
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textMuted,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = sentToLabel.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textMuted,
            )
            OtpCountdown(onExpired = onExpired)
        }
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

@Composable
private fun OtpCountdown(onExpired: () -> Unit) {
    var secondsLeft by remember { mutableStateOf(COUNTDOWN_SECONDS) }
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000.milliseconds)
            secondsLeft--
        }
        onExpired()
    }
    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val label = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        modifier = Modifier
            .background(
                color = LocalTaminColors.current.blueBg,
                shape = RoundedCornerShape(CornerRadius.chip)
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.tabSelector)
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            tint = LocalTaminColors.current.blueText,
            modifier = Modifier.size(IconSize.small)
        )
        Text(
            text = label.toPersianDigits(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontFeatureSettings = "tnum",
                fontWeight = FontWeight.Bold
            ),
            color = LocalTaminColors.current.blueText
        )
    }
}
