package com.tamin.taminhamrah

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tamin.taminhamrah.ui.MainApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }

    companion object {
        const val EXTRA_INITIAL_ROUTE = "extra_initial_route"
        const val ROUTE_SEARCH = "search"
    }
}
