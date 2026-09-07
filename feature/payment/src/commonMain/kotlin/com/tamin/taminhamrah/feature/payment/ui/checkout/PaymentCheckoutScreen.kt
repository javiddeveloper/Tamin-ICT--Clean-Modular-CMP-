package com.tamin.taminhamrah.feature.payment.ui.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.payment.ui.checkout.components.PayerTypeSelector
import com.tamin.taminhamrah.feature.payment.ui.checkout.components.PaymentDetailCard
import com.tamin.taminhamrah.feature.payment.ui.checkout.components.PaymentDetailRow
import com.tamin.taminhamrah.feature.payment.ui.checkout.contract.PaymentCheckoutEvent
import com.tamin.taminhamrah.feature.payment.ui.checkout.contract.PaymentCheckoutIntent
import com.tamin.taminhamrah.feature.payment.ui.checkout.contract.PaymentCheckoutUiState
import com.tamin.taminhamrah.model.payment.PayerType
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTextField
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.rememberTaminCountdownState
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.ui.util.ExternalAppLauncher
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.payment_amount_label
import taminx.core.core_ui.payment_cancel_confirm_accept
import taminx.core.core_ui.payment_cancel_confirm_message
import taminx.core.core_ui.payment_cancel_confirm_reject
import taminx.core.core_ui.payment_cancel_confirm_title
import taminx.core.core_ui.payment_currency_unit
import taminx.core.core_ui.payment_expired_message
import taminx.core.core_ui.payment_id_label
import taminx.core.core_ui.payment_link_failed
import taminx.core.core_ui.payment_pay_action
import taminx.core.core_ui.payment_payer_foreign_id_error
import taminx.core.core_ui.payment_payer_foreign_id_label
import taminx.core.core_ui.payment_payer_legal_id_error
import taminx.core.core_ui.payment_payer_legal_id_label
import taminx.core.core_ui.payment_payer_national_code_error
import taminx.core.core_ui.payment_payer_national_code_label
import taminx.core.core_ui.payment_payer_section_description
import taminx.core.core_ui.payment_payer_section_title
import taminx.core.core_ui.payment_reason_label
import taminx.core.core_ui.payment_remaining_time_label
import taminx.core.core_ui.payment_ticket_missing
import taminx.core.core_ui.payment_title

/**
 * Step one of every payment in the app: show what is owed, ask who is paying, and hand the ticket
 * to the gateway.
 *
 * The countdown runs on the gateway's own remaining-milliseconds value. When it reaches zero the
 * ticket is released rather than left open, so the same debt can be paid again straight away —
 * an abandoned ticket used to block the next attempt until it timed out server-side.
 */
@Composable
fun PaymentCheckoutScreen(
    ticket: String,
    onGatewayOpened: () -> Unit,
    onCancelled: () -> Unit,
    viewModel: PaymentCheckoutViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // Constructed rather than injected: ExternalAppLauncher has no Koin definition — it resolves
    // the platform context itself — and every other caller in the app builds it the same way.
    val externalAppLauncher = remember { ExternalAppLauncher() }

    LaunchedEffect(ticket) { viewModel.sendIntent(PaymentCheckoutIntent.Load(ticket)) }

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            is PaymentCheckoutEvent.OpenGateway -> {
                externalAppLauncher.openUrl(event.url)
                onGatewayOpened()
            }

            PaymentCheckoutEvent.Cancelled -> onCancelled()
        }
    }

    BackHandler { viewModel.sendIntent(PaymentCheckoutIntent.BackRequested) }

    PaymentCheckoutContent(state = state, onIntent = viewModel::sendIntent)
}

@Composable
private fun PaymentCheckoutContent(
    state: PaymentCheckoutUiState,
    onIntent: (PaymentCheckoutIntent) -> Unit,
) {
    val colors = LocalTaminColors.current

    val countdown = rememberTaminCountdownState(
        onFinish = { onIntent(PaymentCheckoutIntent.TimerFinished) },
    )
    // Restarted from the preview rather than ticked down from a guess: the gateway is the only
    // thing that knows how long a ticket has left, and the device clock can be wrong by more than
    // the ticket's whole lifetime.
    LaunchedEffect(state.remainingSeconds) {
        if (state.remainingSeconds > 0) countdown.start(state.remainingSeconds)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = { TaminTopAppBar(title = stringResource(Res.string.payment_title)) },
        bottomBar = {
            TaminBottomBar {
                LoadingButton(
                    text = stringResource(Res.string.payment_pay_action),
                    onClick = { onIntent(PaymentCheckoutIntent.PayClicked) },
                    enabled = state.canSubmit,
                    isLoading = state.isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.page, vertical = Spacing.lg)
                .navigationBarsPadding()
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            PaymentDetailCard {
                PaymentDetailRow(
                    label = stringResource(Res.string.payment_amount_label),
                    value = state.preview.amount.toRialAmount(),
                    emphasize = true,
                )
                PaymentDetailRow(
                    label = stringResource(Res.string.payment_id_label),
                    value = state.preview.paymentId.orDash(),
                )
                PaymentDetailRow(
                    label = stringResource(Res.string.payment_reason_label),
                    value = state.preview.description.orDash(),
                )
                PaymentDetailRow(
                    label = stringResource(Res.string.payment_remaining_time_label),
                    value = countdown.formattedTime,
                )
            }

            if (state.isExpired) {
                TaminText(
                    text = stringResource(Res.string.payment_expired_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.dangerText,
                )
            }

            TaminText(
                text = stringResource(Res.string.payment_payer_section_title),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
            )
            TaminText(
                text = stringResource(Res.string.payment_payer_section_description),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )

            PayerTypeSelector(
                selected = state.payerType,
                onSelect = { onIntent(PaymentCheckoutIntent.PayerTypeSelected(it)) },
                enabled = !state.isSubmitting && !state.isExpired,
            )

            if (state.payerType.needsIdentifier) {
                TaminTextField(
                    value = state.payerIdentifier.toPersianDigits(),
                    onValueChange = { onIntent(PaymentCheckoutIntent.PayerIdentifierChanged(it)) },
                    label = state.payerType.identifierLabel(),
                    enabled = !state.isSubmitting && !state.isExpired,
                    keyboardType = KeyboardType.Number,
                    errorMessage = state.payerType.identifierError()
                        .takeIf { state.identifierError },
                )
            }
        }
    }

    if (state.showCancelConfirmation) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.payment_cancel_confirm_title),
            description = stringResource(Res.string.payment_cancel_confirm_message),
            onDismissRequest = { onIntent(PaymentCheckoutIntent.CancelDismissed) },
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.payment_cancel_confirm_accept),
                    onClick = { onIntent(PaymentCheckoutIntent.CancelConfirmed) },
                )
            },
            dismissButton = {
                TaminOutlinedButton(
                    text = stringResource(Res.string.payment_cancel_confirm_reject),
                    onClick = { onIntent(PaymentCheckoutIntent.CancelDismissed) },
                )
            },
        )
    }

    // The gateway does not always send a reason with a refusal, so the screen keeps its own copy
    // for that case instead of a ViewModel carrying user-facing text.
    val fallbackError = stringResource(Res.string.payment_link_failed)
    val missingTicketError = stringResource(Res.string.payment_ticket_missing)
    ErrorStateView(
        message = when {
            state.isTicketMissing -> missingTicketError
            state.error != null -> state.error.ifBlank { fallbackError }
            else -> null
        },
        onDismiss = { onIntent(PaymentCheckoutIntent.ErrorDismissed) },
    )
}

@Composable
private fun PayerType.identifierLabel(): String = when (this) {
    PayerType.CURRENT_USER -> ""
    PayerType.OTHER_PERSON -> stringResource(Res.string.payment_payer_national_code_label)
    PayerType.LEGAL_ENTITY -> stringResource(Res.string.payment_payer_legal_id_label)
    PayerType.FOREIGN_NATIONAL -> stringResource(Res.string.payment_payer_foreign_id_label)
}

@Composable
private fun PayerType.identifierError(): String = when (this) {
    PayerType.CURRENT_USER -> ""
    PayerType.OTHER_PERSON -> stringResource(Res.string.payment_payer_national_code_error)
    PayerType.LEGAL_ENTITY -> stringResource(Res.string.payment_payer_legal_id_error)
    PayerType.FOREIGN_NATIONAL -> stringResource(Res.string.payment_payer_foreign_id_error)
}

private const val DASH = "—"

@Composable
internal fun String.orDash(): String = ifBlank { DASH }.toPersianDigits()

/** Grouped Persian digits followed by the currency unit, the way every money line in the app reads. */
@Composable
internal fun Long.toRialAmount(): String =
    "${toPriceFormat()} ${stringResource(Res.string.payment_currency_unit)}"
