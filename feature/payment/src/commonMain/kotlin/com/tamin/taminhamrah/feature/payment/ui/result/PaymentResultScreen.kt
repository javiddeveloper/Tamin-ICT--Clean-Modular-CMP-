package com.tamin.taminhamrah.feature.payment.ui.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.payment.ui.checkout.orDash
import com.tamin.taminhamrah.feature.payment.ui.result.contract.PaymentResultEvent
import com.tamin.taminhamrah.feature.payment.ui.result.contract.PaymentResultIntent
import com.tamin.taminhamrah.feature.payment.ui.result.contract.PaymentResultKind
import com.tamin.taminhamrah.feature.payment.ui.result.contract.PaymentResultUiState
import com.tamin.taminhamrah.feature.payment.ui.result.contract.messageOrNull
import com.tamin.taminhamrah.model.payment.PaymentStatus
import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.payment_currency_unit
import taminx.core.core_ui.payment_id_label
import taminx.core.core_ui.payment_reason_label
import taminx.core.core_ui.payment_reference_number_label
import taminx.core.core_ui.payment_result_check_again
import taminx.core.core_ui.payment_result_done
import taminx.core.core_ui.payment_result_expired_title
import taminx.core.core_ui.payment_result_failed_message
import taminx.core.core_ui.payment_result_failed_title
import taminx.core.core_ui.payment_result_pending
import taminx.core.core_ui.payment_result_success_title
import taminx.core.core_ui.payment_result_title
import taminx.core.core_ui.payment_result_unconfirmed_message
import taminx.core.core_ui.payment_result_unconfirmed_title
import taminx.core.core_ui.payment_result_verifying_message
import taminx.core.core_ui.payment_settled_amount_label
import taminx.core.core_ui.payment_trace_number_label

/**
 * Step two of every payment: what actually happened, and what the user should do about it.
 *
 * Re-checks whenever the screen resumes, which is what turns "the user came back from the browser"
 * into a fresh answer without relying on the deep link having arrived.
 */
@Composable
fun PaymentResultScreen(
    ticket: String,
    verifierKey: PaymentVerifierKey,
    verifierReference: String,
    onDone: () -> Unit,
    viewModel: PaymentResultViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.sendIntent(PaymentResultIntent.Check(ticket, verifierKey, verifierReference))
    }

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            PaymentResultEvent.Finished -> onDone()
        }
    }

    // Back from here must not return to the checkout screen, which is gone, nor re-open a spent
    // ticket: it means the same thing as the done button.
    BackHandler { viewModel.sendIntent(PaymentResultIntent.DoneClicked) }

    PaymentResultContent(state = state, onIntent = viewModel::sendIntent)
}

@Composable
private fun PaymentResultContent(
    state: PaymentResultUiState,
    onIntent: (PaymentResultIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val preview = state.outcome.preview

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = { TaminTopAppBar(title = stringResource(Res.string.payment_result_title)) },
        bottomBar = {
            TaminBottomBar {
                if (state.kind == PaymentResultKind.PAID_UNCONFIRMED) {
                    TaminOutlinedButton(
                        text = stringResource(Res.string.payment_result_check_again),
                        onClick = { onIntent(PaymentResultIntent.Recheck) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                LoadingButton(
                    text = stringResource(Res.string.payment_result_done),
                    onClick = { onIntent(PaymentResultIntent.DoneClicked) },
                    isLoading = state.isLoading,
                    enabled = !state.isLoading,
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
                .padding(horizontal = Spacing.page, vertical = Spacing.xl)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            TaminText(
                text = state.kind.headline(),
                style = MaterialTheme.typography.titleLarge,
                color = state.kind.headlineColor(),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            state.supportingMessage()?.let { message ->
                TaminText(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (state.kind != PaymentResultKind.CHECKING) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .taminSurface()
                        .padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    DetailRow(
                        label = stringResource(Res.string.payment_settled_amount_label),
                        // Falls back to what was asked for: a gateway that reports success without
                        // echoing the settled amount must not print a receipt reading zero.
                        value = preview.settledAmount
                            .takeIf { it > 0L }
                            .let { it ?: preview.amount }
                            .toPriceFormat(),
                        unit = stringResource(Res.string.payment_currency_unit),
                        valueStyle = MaterialTheme.typography.titleMedium,
                    )
                    DetailRow(
                        label = stringResource(Res.string.payment_id_label),
                        value = preview.paymentId.orDash(),
                    )
                    DetailRow(
                        label = stringResource(Res.string.payment_reason_label),
                        value = preview.description.orDash(),
                        numeric = false,
                    )
                    if (preview.referenceNumber.isNotBlank()) {
                        DetailRow(
                            label = stringResource(Res.string.payment_reference_number_label),
                            value = preview.referenceNumber.orDash(),
                        )
                    }
                    if (preview.traceNumber.isNotBlank()) {
                        DetailRow(
                            label = stringResource(Res.string.payment_trace_number_label),
                            value = preview.traceNumber.orDash(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentResultKind.headline(): String = when (this) {
    PaymentResultKind.CHECKING -> stringResource(Res.string.payment_result_pending)
    PaymentResultKind.SUCCESS -> stringResource(Res.string.payment_result_success_title)
    PaymentResultKind.PAID_UNCONFIRMED -> stringResource(Res.string.payment_result_unconfirmed_title)
    PaymentResultKind.EXPIRED -> stringResource(Res.string.payment_result_expired_title)
    PaymentResultKind.FAILED -> stringResource(Res.string.payment_result_failed_title)
}

@Composable
private fun PaymentResultKind.headlineColor() = with(LocalTaminColors.current) {
    when (this@headlineColor) {
        PaymentResultKind.CHECKING -> textSecondary
        PaymentResultKind.SUCCESS -> greenText
        PaymentResultKind.PAID_UNCONFIRMED -> orangeText
        PaymentResultKind.EXPIRED -> orangeText
        PaymentResultKind.FAILED -> dangerText
    }
}

/**
 * The line under the headline.
 *
 * The service's or gateway's own wording wins where there is any — it is more specific than
 * anything this screen can say — and the app's copy fills in when there is none.
 */
@Composable
private fun PaymentResultUiState.supportingMessage(): String? {
    val verifierMessage = outcome.verification.messageOrNull
    val gatewayMessage = outcome.preview.message.takeIf { it.isNotBlank() }
    return when (kind) {
        PaymentResultKind.CHECKING -> null
        PaymentResultKind.SUCCESS ->
            verifierMessage
                ?: stringResource(Res.string.payment_result_verifying_message)
                    .takeIf { outcome.preview.status == PaymentStatus.VERIFYING }

        PaymentResultKind.PAID_UNCONFIRMED ->
            verifierMessage ?: stringResource(Res.string.payment_result_unconfirmed_message)

        PaymentResultKind.EXPIRED -> gatewayMessage
        PaymentResultKind.FAILED ->
            gatewayMessage ?: stringResource(Res.string.payment_result_failed_message)
    }
}
