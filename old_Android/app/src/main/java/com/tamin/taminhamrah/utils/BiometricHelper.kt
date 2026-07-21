package com.tamin.taminhamrah.utils.biometric

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.utils.isBiometricAvailable
import javax.inject.Inject

class BiometricHelper @Inject constructor() {

    fun authenticate(
        fragment: Fragment,
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: ((errorCode: Int, errString: CharSequence) -> Unit)? = null,
        onFailed: (() -> Unit)? = null
    ) {
        val context = fragment.requireContext()

        if (!context.isBiometricAvailable()) {
            onError?.invoke(0, "Biometric not available")
            return
        }

        val executor = ContextCompat.getMainExecutor(context)

        val biometricPrompt = BiometricPrompt(fragment, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess(result)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError?.invoke(errorCode, errString)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed?.invoke()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(fragment.getString(R.string.authentication))
            .setSubtitle(fragment.getString(R.string.Please_authenticate_using_your_fingerprint))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText(fragment.getString(R.string.cancel_))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}