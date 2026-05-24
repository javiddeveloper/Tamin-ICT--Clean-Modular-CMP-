package com.tamin.taminhamrah.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.ui.navigation.TaminHamrahNavGraph
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
fun MainApp(
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val darkTheme = when (uiState.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }

    val currentLanguage = "fa" // uiState.language

    TaminHamrahTheme(
        darkTheme = darkTheme,
        language = currentLanguage
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(Spacing.xxl))

            Button(onClick = { viewModel.updateDarkThemeConfig(DarkThemeConfig.LIGHT) }) {
                Text("Light Mode")
            }
            Button(onClick = { viewModel.updateDarkThemeConfig(DarkThemeConfig.DARK) }) {
                Text("Dark Mode")
            }
            Button(onClick = { viewModel.updateDarkThemeConfig(DarkThemeConfig.FOLLOW_SYSTEM) }) {
                Text("System Default")
            }
            TaminHamrahNavGraph(
                isLoggedIn = uiState.isLoggedIn,
                isLoading = uiState.isLoading,
                onLoginClick = { viewModel.login() }
            )
        }
    }
}
