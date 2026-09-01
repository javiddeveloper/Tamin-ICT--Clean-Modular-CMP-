package com.tamin.taminhamrah.feature.retirementPension.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementFormError
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.OtpInputField
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_envelope
import taminx.core.core_ui.retirement_pension_otp_intro
import taminx.core.core_ui.retirement_pension_otp_invalid
import taminx.core.core_ui.retirement_pension_otp_request
import taminx.core.core_ui.retirement_pension_otp_resend_in
import taminx.core.core_ui.retirement_pension_otp_sent
import taminx.core.core_ui.retirement_pension_otp_verified
import kotlin.time.Duration.Companion.milliseconds

/** Digits in the code the service sends. */
private const val OTP_LENGTH = 6

/** How long the code stays valid — the legacy app's `timerStart(900)`. */
private const val OTP_WINDOW_SECONDS = 900

private const val SECOND_MILLIS = 1_000L
private const val SECONDS_PER_MINUTE = 60
private val CountdownDigitWidth = 12.dp

/**
 * Step 2 — the SMS code. Entering the sixth digit *is* the verification: the code is the ticket the
 * service checks, so there is no separate confirm button.
 */
@Composable
internal fun RetirementAuthStep(
    mobileNumber: String,
    otpValue: String,
    otpSent: Boolean,
    otpVerified: Boolean,
    otpInvalid: Boolean,
    isSending: Boolean,
    error: RetirementFormError?,
    onRequestOtp: () -> Unit,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    RetirementStepColumn(modifier = modifier) {
        TaminText(
            text = stringResource(Res.string.retirement_pension_otp_intro),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )

        AnimatedContent(
            targetState = when {
                otpVerified -> OtpPhase.Verified
                otpSent -> OtpPhase.Entering
                else -> OtpPhase.NotSent
            },
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "RetirementOtpPhase",
        ) { phase ->
            when (phase) {
                OtpPhase.NotSent -> Column {
                    LoadingButton(
                        text = stringResource(Res.string.retirement_pension_otp_request),
                        onClick = onRequestOtp,
                        isLoading = isSending,
                        icon = vectorResource(Res.drawable.ic_tamin_envelope),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    // Pressing «مرحلهٔ بعدی» before asking for a code complains here, at the button
                    // that would have fixed it.
                    if (error != null) {
                        RetirementErrorLine(error, modifier = Modifier.padding(top = Spacing.sm))
                    }
                }

                OtpPhase.Entering -> OtpEntryCard(
                    mobileNumber = mobileNumber,
                    otpValue = otpValue,
                    otpInvalid = otpInvalid,
                    error = error,
                    onOtpChange = onOtpChange,
                    onResend = onRequestOtp,
                )

                OtpPhase.Verified -> BannerCard(
                    message = stringResource(Res.string.retirement_pension_otp_verified),
                    type = BannerType.Success,
                )
            }
        }
    }
}

private enum class OtpPhase { NotSent, Entering, Verified }

@Composable
private fun OtpEntryCard(
    mobileNumber: String,
    otpValue: String,
    otpInvalid: Boolean,
    error: RetirementFormError?,
    onOtpChange: (String) -> Unit,
    onResend: () -> Unit,
) {
    val colors = LocalTaminColors.current

    RetirementCard {
        TaminText(
            text = stringResource(
                Res.string.retirement_pension_otp_sent,
                mobileNumber.toPersianDigits(),
            ),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        OtpInputField(
            value = otpValue,
            onValueChange = onOtpChange,
            length = OTP_LENGTH,
            // A rejected code names itself; an incomplete one borrows the step's complaint. Either
            // way the message and the animated border land on the field, not at the page foot.
            error = otpInvalid || error != null,
            errorMessage = when {
                otpInvalid -> stringResource(Res.string.retirement_pension_otp_invalid)
                error != null -> error.text()
                else -> null
            },
            leadingIcon = Icons.Outlined.Lock,
            modifier = Modifier.padding(top = Spacing.smPlus),
        )

        ResendCountdown(
            onResend = onResend,
            modifier = Modifier.padding(top = Spacing.smPlus),
        )
    }
}

/**
 * The resend window.
 *
 * Owned by this composable rather than the ViewModel: a per-second value in the UI state would
 * re-emit the whole state — and re-run the wizard's validation — sixty times a minute for a number
 * only this row draws. Restarting is keyed on [onResend] being taken, so stepping away and back
 * begins a fresh window, which is also what the service does with a fresh ticket.
 */
@Composable
private fun ResendCountdown(
    onResend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var restarts by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(OTP_WINDOW_SECONDS) }

    LaunchedEffect(restarts) {
        secondsLeft = OTP_WINDOW_SECONDS
        while (secondsLeft > 0) {
            delay(SECOND_MILLIS.milliseconds)
            secondsLeft--
        }
    }

    val canResend = secondsLeft == 0
    val minutes = secondsLeft / SECONDS_PER_MINUTE
    val seconds = secondsLeft % SECONDS_PER_MINUTE
    val clock = "${minutes.padded()}:${seconds.padded()}".toPersianDigits()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.blueBg, RoundedCornerShape(CornerRadius.full))
            .clickable(enabled = canResend) {
                onResend()
                restarts++
            }
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TaminText(
            text = stringResource(Res.string.retirement_pension_otp_resend_in),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (canResend) colors.blueText else colors.textMuted,
        )
        // A clock reads left-to-right even on a right-to-left page; without this the row renders
        // "۱۴:۵۹" back to front. Digit-by-digit so only the digit that changed animates.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row {
                clock.forEachIndexed { index, char ->
                    AnimatedContent(
                        modifier = if (char == ':') Modifier else Modifier.width(CountdownDigitWidth),
                        targetState = char,
                        contentAlignment = Alignment.Center,
                        transitionSpec = {
                            slideInVertically { height -> -height } + fadeIn() togetherWith
                                slideOutVertically { height -> height } + fadeOut()
                        },
                        label = "RetirementCountdownDigit$index",
                    ) { digit ->
                        TaminText(
                            text = digit.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.blueText,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

private fun Int.padded(): String = toString().padStart(2, '0')
