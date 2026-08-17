package com.tamin.taminhamrah.ui.util

import androidx.compose.runtime.Composable

enum class BiometricAvailability {
    AVAILABLE,
    NO_HARDWARE,
    NOT_ENROLLED,
    UNAVAILABLE
}

sealed interface BiometricAuthResult {
    data object Success : BiometricAuthResult
    data object Cancelled : BiometricAuthResult
    data class Failed(val message: String) : BiometricAuthResult
}

interface BiometricAuthenticator {
    fun availability(): BiometricAvailability
    suspend fun authenticate(
        title: String,
        subtitle: String,
        negativeButtonText: String
    ): BiometricAuthResult
}

@Composable
expect fun rememberBiometricAuthenticator(): BiometricAuthenticator
