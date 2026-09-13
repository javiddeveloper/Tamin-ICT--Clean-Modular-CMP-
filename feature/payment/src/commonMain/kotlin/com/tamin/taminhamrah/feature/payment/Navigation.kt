package com.tamin.taminhamrah.feature.payment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.payment.ui.checkout.PaymentCheckoutScreen
import com.tamin.taminhamrah.feature.payment.ui.result.PaymentResultScreen
import com.tamin.taminhamrah.feature.payment.ui.sandbox.PaymentSandboxScreen
import com.tamin.taminhamrah.model.payment.PaymentRequestDN
import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlin.random.Random
import kotlinx.serialization.Serializable

/**
 * The shared payment flow, entered from any feature that has just been handed a gateway ticket.
 *
 * The route carries the ticket and the name of the post-payment check rather than a callback,
 * because the user leaves the app to pay: the process can be killed while the browser is in front,
 * and everything the flow needs on the way back has to survive in the back stack.
 */
@Serializable
sealed interface PaymentRoute {

    /** Preview, payer selection, and the hand-off to the gateway. */
    @Serializable
    data class Checkout(
        val ticket: String,
        val verifierKey: PaymentVerifierKey = PaymentVerifierKey.NONE,
        val verifierReference: String = "",
    ) : PaymentRoute

    /** Debug-only catalogue of every payment the app makes, run against the mock gateway. */
    @Serializable
    data object Sandbox : PaymentRoute

    /** What happened, once the user is back from the gateway. */
    @Serializable
    data class Result(
        val ticket: String,
        val verifierKey: PaymentVerifierKey = PaymentVerifierKey.NONE,
        val verifierReference: String = "",
    ) : PaymentRoute
}

/**
 * Attaches the payment flow to the app graph.
 *
 * [onFinished] is called when the user is done — the payment was cancelled, or the result screen
 * was dismissed. The feature that started the payment is normally still on the back stack
 * underneath, so popping back to it is the usual answer.
 */
fun NavGraphBuilder.paymentGraph(
    navController: NavController,
    onFinished: () -> Unit,
) {
    composableWithFadeTransitions<PaymentRoute.Checkout> { backStackEntry ->
        val route = backStackEntry.toRoute<PaymentRoute.Checkout>()
        PaymentCheckoutScreen(
            ticket = route.ticket,
            onGatewayOpened = {
                navController.navigate(
                    PaymentRoute.Result(
                        ticket = route.ticket,
                        verifierKey = route.verifierKey,
                        verifierReference = route.verifierReference,
                    )
                ) {
                    // Checkout has served its purpose the moment the gateway is open: coming back
                    // must land on the result, and back from the result must not offer to pay a
                    // ticket that is already spent.
                    popUpTo<PaymentRoute.Checkout> { inclusive = true }
                }
            },
            onCancelled = onFinished,
        )
    }

    composableWithFadeTransitions<PaymentRoute.Result> { backStackEntry ->
        val route = backStackEntry.toRoute<PaymentRoute.Result>()
        PaymentResultScreen(
            ticket = route.ticket,
            verifierKey = route.verifierKey,
            verifierReference = route.verifierReference,
            onDone = onFinished,
        )
    }
}

/**
 * Starts a payment — the one call a feature makes once its own `pay*` endpoint returned a ticket.
 */
/**
 * Attaches the debug-only payment sandbox. Register it only under `AppConfig.isDebug`.
 *
 * Each run gets a random ticket suffix: a gateway treats a ticket as spent once it has been
 * through it, so a fixed one previews as already-paid the second time round. Randomness is enough
 * — the mock gateway only needs the tickets to differ, not to be unguessable.
 */
fun NavGraphBuilder.paymentSandboxScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
) {
    composableWithFadeTransitions<PaymentRoute.Sandbox> {
        PaymentSandboxScreen(
            onStartPayment = { scenario ->
                navController.navigateToPayment(
                    scenario.toPaymentRequest(nonce = Random.nextLong().toString())
                )
            },
            onNavigateBack = onNavigateBack,
        )
    }
}

fun NavController.navigateToPayment(
    request: PaymentRequestDN,
    builder: (NavOptionsBuilder.() -> Unit)? = null,
) {
    navigate(
        PaymentRoute.Checkout(
            ticket = request.ticket,
            verifierKey = request.verifierKey,
            verifierReference = request.verifierReference,
        )
    ) {
        builder?.invoke(this)
    }
}
