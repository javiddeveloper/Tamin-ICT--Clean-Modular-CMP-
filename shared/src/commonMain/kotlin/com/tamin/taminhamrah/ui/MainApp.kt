package com.tamin.taminhamrah.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.openUrl
import com.tamin.taminhamrah.ui.navigation.TaminHamrahNavGraph
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import com.tamin.taminhamrah.ui.contract.MainEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainApp(
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            is MainEvent.OpenUrl -> openUrl(event.url)
        }
    }

    val darkTheme = when (uiState.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }

    val currentLanguage = "fa"

    TaminHamrahTheme(
        darkTheme = darkTheme,
        language = currentLanguage
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                TaminHamrahNavGraph(
                    isLoggedIn = uiState.isLoggedIn,
                    isLoading = uiState.isLoading,
                    onLoginClick = { viewModel.login() }
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
