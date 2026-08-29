package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeHeader
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeOtpSection
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components.LegalRepresentativeWorkshopSummaryCard
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.GlassIconTile
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.legal_representative_otp_description
import taminx.core.core_ui.legal_representative_otp_heading
import taminx.core.core_ui.legal_representative_otp_request_action
import taminx.core.core_ui.legal_representative_otp_verify_action

@Composable
fun LegalRepresentativeOtpScreen(
    workshopName: String,
    workshopSubtitle: String,
    onBackClicked: () -> Unit,
    onVerified: (ticket: String) -> Unit,
    viewModel: LegalRepresentativeOtpViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.events.collectWithLifecycleAware { event ->
        if (event is LegalRepresentativeOtpEvent.VerifiedSuccessfully) {
            onVerified(event.ticket)
        }
    }

    val taminColors = LocalTaminColors.current

    Column(modifier = Modifier.fillMaxSize()) {
        LegalRepresentativeHeader(onBackClicked = onBackClicked) {
            LegalRepresentativeWorkshopSummaryCard(workshopName = workshopName, subtitle = workshopSubtitle)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(Spacing.lg))
            Box(
                modifier = Modifier
                    .size(IconSize.xxlarge)
                    .background(
                        color = taminColors.blueBg,
                        shape = RoundedCornerShape(CornerRadius.chip)
                    )
                    .border(
                        width = Thickness.border,
                        color = taminColors.blueText.copy(0.1f),
                        shape = RoundedCornerShape(CornerRadius.chip),
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ConfirmationNumber,
                    contentDescription = null,
                    tint = taminColors.blueText,
                )
            }
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = stringResource(Res.string.legal_representative_otp_heading),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = stringResource(Res.string.legal_representative_otp_description),
                style = MaterialTheme.typography.bodyMedium,
                color = taminColors.textMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Spacing.xl))

            LegalRepresentativeOtpSection(
                isTicketRequested = uiState.isTicketRequested,
                isRequestingTicket = uiState.isRequestingTicket,
                otpCode = uiState.otpCode,
                otpError = uiState.error,
                sentToLabel = null,
                requestLabel = stringResource(Res.string.legal_representative_otp_request_action),
                onRequestTicket = { viewModel.sendIntent(LegalRepresentativeOtpIntent.RequestTicket) },
                onOtpChanged = { viewModel.sendIntent(LegalRepresentativeOtpIntent.OtpChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
            )

            if (uiState.isTicketRequested) {
                Spacer(Modifier.height(Spacing.lg))
                LoadingButton(
                    text = stringResource(Res.string.legal_representative_otp_verify_action),
                    onClick = { viewModel.sendIntent(LegalRepresentativeOtpIntent.VerifyTicket) },
                    isLoading = uiState.isVerifying,
                    enabled = uiState.otpCode.isNotBlank() && !uiState.isVerifying,
                    icon = Icons.Filled.CheckCircle,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
