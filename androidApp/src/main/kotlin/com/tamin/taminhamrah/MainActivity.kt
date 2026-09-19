package com.tamin.taminhamrah

import com.tamin.taminhamrah.deeplink.DeepLinkKey
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.deeplink.DeepLinkDispatcher
import com.tamin.taminhamrah.deeplink.DeepLinkSource
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.repository.payment.PaymentReturnNotifier
import com.tamin.taminhamrah.ui.MainApp
import com.tamin.taminhamrah.useCases.auth.HandleAuthDeepLinkUseCase
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : FragmentActivity() {

    private val handleAuthDeepLinkUseCase: HandleAuthDeepLinkUseCase by inject()
    private val tokenStoreManager: TokenStoreManager by inject()
    private val paymentReturnNotifier: PaymentReturnNotifier by inject()
    private val deepLinkDispatcher: DeepLinkDispatcher by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        setContent {
            MainApp()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val data = intent?.data ?: return
        // The gateway sends the browser back to mytamin://payment_callback when a payment ends.
        // The result screen re-checks on resume anyway, so this only makes the common case
        // immediate — it is not the only thing keeping the payment flow correct.
        if (data.host == PAYMENT_CALLBACK_HOST) {
            val ticket = data.getQueryParameter(PAYMENT_TICKET_QUERY).orEmpty()
            lifecycleScope.launch { paymentReturnNotifier.notifyReturn(ticket) }
            return
        }
        // mytamin://feature/<key>, or a native-era link naming the screen as its host.
        if (data.host == FEATURE_HOST || DeepLinkKey.fromKey(data.host.orEmpty()) != null) {
            deepLinkDispatcher.submit(data.toString(), DeepLinkSource.SYSTEM)
            return
        }
        lifecycleScope.launch {
            handleAuthDeepLinkUseCase(data.toString())
        }
    }

    private companion object {
        const val PAYMENT_CALLBACK_HOST = "payment_callback"
        const val FEATURE_HOST = "feature"
        const val PAYMENT_TICKET_QUERY = "ticket"
    }
}
