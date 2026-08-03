package com.tamin.taminhamrah.feature.changemobile.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.changemobile.ui.contract.ChangeMobileIntent
import com.tamin.taminhamrah.feature.changemobile.ui.contract.ChangeMobileUiState
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.OtpInputField
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun VerifyOtpStep(
    uiStateState: State<ChangeMobileUiState>,
    onIntent: (ChangeMobileIntent) -> Unit
) {
    val taminColors = LocalTaminColors.current
    val uiState = uiStateState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val newMobile = uiState.newMobile
        Text(
            text = "کد پیامک ‌شده به شمارهٔ زیر را وارد کنید",
            style = MaterialTheme.typography.bodyMedium,
            color = taminColors.textMuted
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = newMobile,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Row(
                modifier = Modifier
                    .clickable { onIntent(ChangeMobileIntent.BackToPreviousStep) }
                    .padding(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "ویرایش",
                    tint = taminColors.blueText,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "ویرایش",
                    style = MaterialTheme.typography.labelLarge,
                    color = taminColors.blueText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(Spacing.xl))

        val otpCode = uiState.otpCode
        val error = uiState.error
        OtpInputField(
            value = otpCode,
            onValueChange = {
                onIntent(ChangeMobileIntent.OtpChanged(it))
            },
            error = error != null,
            errorMessage = error,
            showClearButton = true,
            leadingIcon = Icons.Outlined.Lock
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        ResendTimer(
            onResendClick = { onIntent(ChangeMobileIntent.GetOtpCode) }
        )
        Spacer(modifier = Modifier.height(Spacing.xl))

        LoadingButton(
            text = "تأیید و ادامه",
            onClick = {
                onIntent(ChangeMobileIntent.VerifyOtp)
            },
            enabled = !uiState.isLoading && uiState.otpCode.length == 5,
            isLoading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ResendTimer(
    onResendClick: () -> Unit
) {
    var timerTrigger by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(120) }

    LaunchedEffect(timerTrigger) {
        timeLeft = 120
        while (timeLeft > 0) {
            delay(1000.milliseconds)
            timeLeft--
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeString = "${minutes.toString().padStart(2, '0')}:${
        seconds.toString().padStart(2, '0')
    }".toPersianDigits()
    val isResendEnabled = timeLeft == 0
    val taminColors = LocalTaminColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ارسال مجدد کد ",
            style = MaterialTheme.typography.labelLarge,
            color = if (isResendEnabled) taminColors.blueText else taminColors.textMuted,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(enabled = isResendEnabled) {
                onResendClick()
                timerTrigger++
            }
        )
        if (!isResendEnabled) {
            Row {
                timeString.reversed().forEachIndexed { index, char ->
                    AnimatedContent(
                        modifier = if (index == 2) Modifier else Modifier.width(12.dp),
                        targetState = char,
                        contentAlignment = Alignment.Center,
                        transitionSpec = {
                            slideInVertically { height -> -height } + fadeIn() togetherWith
                                slideOutVertically { height -> height } + fadeOut()
                        },
                        label = "CharAnimation$index"
                    ) { targetChar ->
                        Text(
                            text = targetChar.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFeatureSettings = "tnum"
                            ),
                            color = taminColors.textMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
