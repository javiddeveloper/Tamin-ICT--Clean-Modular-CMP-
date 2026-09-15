package com.tamin.taminhamrah.ui

import com.tamin.taminhamrah.deeplink.DeepLinkDispatcher
import com.tamin.taminhamrah.deeplink.DeepLinkSource
import com.tamin.taminhamrah.repository.payment.PaymentReturnNotifier
import com.tamin.taminhamrah.useCases.auth.HandleAuthDeepLinkUseCase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

private val incomingUrlScope = MainScope()

/**
 * Entry point for every URL iOS opens the app with (`onOpenURL` in `iOSApp.swift`).
 * Mirrors `MainActivity.handleIntent` on Android so both platforms route links the same way.
 */
fun handleIncomingUrl(url: String) {
    val koin = KoinPlatform.getKoin()
    val host = url.substringAfter("://", "").substringBefore('/').substringBefore('?').lowercase()
    when (host) {
        PAYMENT_CALLBACK_HOST -> {
            val ticket = url.substringAfter("$PAYMENT_TICKET_QUERY=", "").substringBefore('&')
            incomingUrlScope.launch { koin.get<PaymentReturnNotifier>().notifyReturn(ticket) }
        }
        FEATURE_HOST -> koin.get<DeepLinkDispatcher>().submit(url, DeepLinkSource.SYSTEM)
        else -> incomingUrlScope.launch { koin.get<HandleAuthDeepLinkUseCase>()(url) }
    }
}

private const val PAYMENT_CALLBACK_HOST = "payment_callback"
private const val PAYMENT_TICKET_QUERY = "ticket"
private const val FEATURE_HOST = "feature"
