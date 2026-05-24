package com.tamin.taminhamrah

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.ui.MainApp
import com.tamin.taminhamrah.useCases.auth.HandleAuthDeepLinkUseCase
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val handleAuthDeepLinkUseCase: HandleAuthDeepLinkUseCase by inject()
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
        intent?.data?.toString()?.let { uri ->
            lifecycleScope.launch {
                val success = handleAuthDeepLinkUseCase(uri)
                if (success) { tokenStoreManager.getToken() }
            }
        }
    }
}
