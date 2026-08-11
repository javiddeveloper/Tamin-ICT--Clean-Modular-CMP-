package com.tamin.taminhamrah.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.RequestNotificationPermissionOnLogin
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.openUrl
import com.tamin.taminhamrah.ui.contract.MainEvent
import com.tamin.taminhamrah.ui.navigation.TaminHamrahNavGraph
import com.tamin.taminhamrah.ui.system.StatusBarIcons
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import com.tamin.taminhamrah.ui.util.BiometricAuthResult
import com.tamin.taminhamrah.ui.util.BiometricAvailability
import com.tamin.taminhamrah.ui.util.rememberBiometricAuthenticator
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.biometric_prompt_subtitle
import taminx.core.core_ui.biometric_prompt_title

@Composable
fun MainApp(
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val biometricAuthenticator = rememberBiometricAuthenticator()
    val scope = rememberCoroutineScope()
    var showEnableBiometricPrompt by remember { mutableStateOf(false) }
    val showBiometricGate = uiState.isLoggedIn && uiState.isBiometricEnabled && !uiState.isBiometricUnlocked
    val biometricPromptTitle = stringResource(Res.string.biometric_prompt_title)
    val biometricPromptSubtitle = stringResource(Res.string.biometric_prompt_subtitle)
    val biometricPromptNegativeButton = stringResource(Res.string.action_cancel)

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            is MainEvent.OpenUrl -> openUrl(event.url)
            is MainEvent.PromptEnableBiometric -> {
                if (biometricAuthenticator.availability() == BiometricAvailability.AVAILABLE) {
                    showEnableBiometricPrompt = true
                }
            }
        }
    }

    val darkTheme = when (uiState.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }

    TaminHamrahTheme(
        darkTheme = darkTheme
    ) {
        AppToastHost {
            StatusBarIcons(darkIcons = !darkTheme)
            RequestNotificationPermissionOnLogin(isLoggedIn = uiState.isLoggedIn)
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

                if (showBiometricGate) {
                    BiometricGate(
                        biometricAuthenticator = biometricAuthenticator,
                        onUnlocked = { viewModel.onBiometricUnlockSucceeded() },
                        onDisableBiometric = { viewModel.disableBiometricAndContinue() }
                    )
                }
            }

            if (showEnableBiometricPrompt) {
                EnableBiometricPromptDialog(
                    onConfirm = {
                        scope.launch {
                            val result = biometricAuthenticator.authenticate(
                                title = biometricPromptTitle,
                                subtitle = biometricPromptSubtitle,
                                negativeButtonText = biometricPromptNegativeButton
                            )
                            viewModel.resolveBiometricEnrollmentPrompt(enable = result is BiometricAuthResult.Success)
                            showEnableBiometricPrompt = false
                        }
                    },
                    onDismiss = {
                        viewModel.resolveBiometricEnrollmentPrompt(enable = false)
                        showEnableBiometricPrompt = false
                    }
                )
            }
        }
    }
}
