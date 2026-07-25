package com.tamin.taminhamrah.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.openUrl
import com.tamin.taminhamrah.ui.contract.MainEvent
import com.tamin.taminhamrah.ui.navigation.TaminHamrahNavGraph
import com.tamin.taminhamrah.ui.system.StatusBarIcons
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
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

    val revealController = rememberThemeRevealController()
    TaminHamrahTheme(
        darkTheme = darkTheme
    ) {
        AppToastHost {
        CompositionLocalProvider(
            LocalThemeRevealController provides revealController
        ) {
            ThemeRevealHost(
                controller = revealController
            ) {
                StatusBarIcons(darkIcons = !darkTheme)
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
            }

    }
}
