package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.VerifyPath
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.rememberTaminCountdownState
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_info_verify_edit_info
import taminx.core.core_ui.employer_info_verify_legal_submit
import taminx.core.core_ui.employer_info_verify_real_submit
import taminx.core.core_ui.employer_info_verify_subtitle
import taminx.core.core_ui.employer_info_verify_title

@Composable
fun EmployerInfoVerifySection(
    mobile: String,
    otpCode: String,
    onOtpCodeChanged: (String) -> Unit,
    verifyPath: VerifyPath?,
    errorMessage: String?,
    isLoading: Boolean,
    onEditInfo: () -> Unit,
    onSubmit: () -> Unit,
    onTimerExpired: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val countdownState = rememberTaminCountdownState(
        initialSeconds = 300,
        onFinish = onTimerExpired,
    )

    LaunchedEffect(Unit) {
        countdownState.start(300)
    }

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // Main verification card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = colors.shadowSubtle,
                    spotColor = colors.shadowSubtle,
                )
                .clip(RoundedCornerShape(20.dp))
                .background(colors.bgSurface)
                .border(1.dp, colors.border, RoundedCornerShape(20.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header with Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.blueBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = colors.blueText,
                        modifier = Modifier.size(19.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.employer_info_verify_title),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                        ),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(
                            Res.string.employer_info_verify_subtitle,
                            mobile.toPersianDigits(),
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = colors.textMuted,
                            fontSize = 10.5.sp,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Countdown Clock Badge
            val isLow = countdownState.isLowTime
            val clockFg = if (isLow) colors.dangerText else colors.blueText
            val clockBg = if (isLow) colors.dangerBg else colors.blueBg
            val clockBorder = if (isLow) colors.dangerBorder else colors.hawkesBlue

            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(clockBg)
                    .border(1.dp, clockBorder, CircleShape)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(
                        text = countdownState.formattedTime,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = clockFg,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5-digit OTP Segmented Input Field
            SegmentedInputField(
                value = otpCode,
                onValueChange = { onOtpCodeChanged(it.filter { c -> c.isDigit() }.take(5)) },
                slotCount = 5,
                error = !errorMessage.isNullOrBlank(),
                errorMessage = errorMessage,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Edit Info Button
            Row(
                modifier = Modifier
                    .weight(0.38f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.bgPage)
                    .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                    .clickable(onClick = {
                        countdownState.stop()
                        onEditInfo()
                    }),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(15.dp),
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = stringResource(Res.string.employer_info_verify_edit_info),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary,
                        fontSize = 11.5.sp,
                    ),
                )
            }

            // Submit Button
            val submitText = if (verifyPath == VerifyPath.LEGAL) {
                stringResource(Res.string.employer_info_verify_legal_submit)
            } else {
                stringResource(Res.string.employer_info_verify_real_submit)
            }

            Row(
                modifier = Modifier
                    .weight(0.62f)
                    .height(50.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(14.dp),
                        ambientColor = colors.shadowPrimary,
                        spotColor = colors.shadowPrimary,
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.buttonGradient)
                    .clickable(enabled = !isLoading, onClick = onSubmit),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = submitText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 11.5.sp,
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
