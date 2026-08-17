package com.tamin.taminhamrah.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.util.BiometricAuthResult
import com.tamin.taminhamrah.ui.util.BiometricAuthenticator
import com.tamin.taminhamrah.ui.util.BiometricAvailability
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.action_confirm
import taminx.core.core_ui.action_retry
import taminx.core.core_ui.biometric_enable_prompt_description
import taminx.core.core_ui.biometric_enable_prompt_title
import taminx.core.core_ui.biometric_gate_description
import taminx.core.core_ui.biometric_gate_disable_action
import taminx.core.core_ui.biometric_gate_title
import taminx.core.core_ui.biometric_gate_unavailable_description
import taminx.core.core_ui.biometric_gate_unavailable_title
import taminx.core.core_ui.biometric_prompt_subtitle
import taminx.core.core_ui.biometric_prompt_title


@Composable
fun BiometricGate(
    biometricAuthenticator: BiometricAuthenticator,
    onUnlocked: () -> Unit,
    onDisableBiometric: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    var attempt by remember { mutableStateOf(0) }
    var isAuthenticating by remember { mutableStateOf(false) }
    var isPermanentlyUnavailable by remember { mutableStateOf(false) }
    val title = stringResource(Res.string.biometric_prompt_title)
    val subtitle = stringResource(Res.string.biometric_prompt_subtitle)
    val negativeButtonText = stringResource(Res.string.action_cancel)

    LaunchedEffect(attempt) {
        val availability = biometricAuthenticator.availability()
        if (availability == BiometricAvailability.NOT_ENROLLED ||
            availability == BiometricAvailability.NO_HARDWARE
        ) {
            isPermanentlyUnavailable = true
            return@LaunchedEffect
        }
        isPermanentlyUnavailable = false

        isAuthenticating = true
        val result = biometricAuthenticator.authenticate(title, subtitle, negativeButtonText)
        isAuthenticating = false
        if (result is BiometricAuthResult.Success) {
            onUnlocked()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(taminColors.bgPage)
            .padding(Spacing.page),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            Box(
                modifier = Modifier
                    .size(IconSize.tile + Spacing.xl)
                    .clip(RoundedCornerShape(CornerRadius.card))
                    .background(taminColors.blueBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Fingerprint,
                    contentDescription = null,
                    tint = taminColors.blueText,
                    modifier = Modifier.size(IconSize.xlarge)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = stringResource(
                        if (isPermanentlyUnavailable) Res.string.biometric_gate_unavailable_title
                        else Res.string.biometric_gate_title
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = taminColors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(
                        if (isPermanentlyUnavailable) Res.string.biometric_gate_unavailable_description
                        else Res.string.biometric_gate_description
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = taminColors.textMuted,
                    textAlign = TextAlign.Center
                )
            }

            TaminFilledButton(
                text = stringResource(
                    if (isPermanentlyUnavailable) Res.string.biometric_gate_disable_action
                    else Res.string.action_retry
                ),
                onClick = {
                    if (isPermanentlyUnavailable) {
                        onDisableBiometric()
                    } else {
                        attempt++
                    }
                },
                enabled = !isAuthenticating,
                icon = Icons.Rounded.Fingerprint,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun EnableBiometricPromptDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    TaminConfirmationDialog(
        title = stringResource(Res.string.biometric_enable_prompt_title),
        description = stringResource(Res.string.biometric_enable_prompt_description),
        onDismissRequest = onDismiss,
        modifier = modifier,
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.action_confirm),
                onClick = onConfirm,
                modifier = Modifier
            )
        },
        dismissButton = {
            TaminOutlinedButton(
                text = stringResource(Res.string.action_cancel),
                onClick = onDismiss,
                modifier = Modifier
            )
        }
    )
}
