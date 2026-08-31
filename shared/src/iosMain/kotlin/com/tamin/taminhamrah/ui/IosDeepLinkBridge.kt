package com.tamin.taminhamrah.ui

import com.tamin.taminhamrah.useCases.auth.HandleAuthDeepLinkUseCase
import com.tamin.taminhamrah.useCases.workersPayment.HandleWorkersPaymentDeepLinkUseCase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Entry point for iOS `onOpenURL`. Mirrors what `MainActivity.handleIntent` does on Android:
 * try the workers-payment gateway callback first (synchronous — it just notifies the observer),
 * then fall back to the auth deep-link handler.
 */
private object IosDeepLinkEntry : KoinComponent {
    val workersPaymentHandler: HandleWorkersPaymentDeepLinkUseCase by inject()
    val authHandler: HandleAuthDeepLinkUseCase by inject()
}

fun handleExternalDeepLink(uri: String) {
    if (IosDeepLinkEntry.workersPaymentHandler(uri)) return
    MainScope().launch {
        IosDeepLinkEntry.authHandler(uri)
    }
}
