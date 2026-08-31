package com.tamin.taminhamrah

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.ui.MainApp
import com.tamin.taminhamrah.useCases.auth.HandleAuthDeepLinkUseCase
import com.tamin.taminhamrah.useCases.workersPayment.HandleWorkersPaymentDeepLinkUseCase
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : FragmentActivity() {

    private val handleAuthDeepLinkUseCase: HandleAuthDeepLinkUseCase by inject()
    private val handleWorkersPaymentDeepLinkUseCase: HandleWorkersPaymentDeepLinkUseCase by inject()
    private val tokenStoreManager: TokenStoreManager by inject()

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
        logDeepLinkProbe(intent) // TEMP: inspect what the payment gateway sends back
        val uri = intent?.data?.toString() ?: return
        if (handleWorkersPaymentDeepLinkUseCase(uri)) return
        lifecycleScope.launch {
            handleAuthDeepLinkUseCase(uri)
        }
    }

    // TEMP diagnostic — dumps every part of an incoming deep-link Intent so we can see whether the
    // payment gateway appends any result data (query string, path, fragment, extras) to
    // mytamin://workers_payment_callback. Remove once confirmed. Filter logcat by tag "DeepLinkProbe".
    private fun logDeepLinkProbe(intent: Intent?) {
        val data = intent?.data
        val sb = StringBuilder()
        sb.appendLine("--- deep link ---")
        sb.appendLine("action       = ${intent?.action}")
        sb.appendLine("dataString   = ${intent?.dataString}")
        sb.appendLine("uri          = $data")
        sb.appendLine("isOpaque     = ${data?.isOpaque}")
        sb.appendLine("scheme       = ${data?.scheme}")
        sb.appendLine("host         = ${runCatching { data?.host }.getOrNull()}")
        sb.appendLine("path         = ${runCatching { data?.path }.getOrNull()}")
        sb.appendLine("pathSegments = ${runCatching { data?.pathSegments }.getOrNull()}")
        sb.appendLine("sspart       = ${data?.schemeSpecificPart}")
        sb.appendLine("encodedQuery = ${runCatching { data?.encodedQuery }.getOrNull()}")
        sb.appendLine("fragment     = ${data?.fragment}")
        runCatching {
            data?.queryParameterNames?.forEach { name ->
                sb.appendLine("  query[$name] = ${data.getQueryParameter(name)}")
            }
        }
        intent?.extras?.let { ex ->
            ex.keySet().forEach { k ->
                sb.appendLine("  extra[$k] = ${runCatching { @Suppress("DEPRECATION") ex.get(k) }.getOrNull()}")
            }
        }
        android.util.Log.i("DeepLinkProbe", sb.toString())
    }
}
