@file:OptIn(ExperimentalForeignApi::class)

package com.tamin.taminhamrah.ui.util

import androidx.compose.runtime.Composable
import kotlin.coroutines.resume
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAErrorBiometryNotAvailable
import platform.LocalAuthentication.LAErrorBiometryNotEnrolled
import platform.LocalAuthentication.LAErrorUserCancel
import platform.LocalAuthentication.LAErrorUserFallback
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics

private class IosBiometricAuthenticator : BiometricAuthenticator {

    override fun availability(): BiometricAvailability = memScoped {
        val errorVar = alloc<ObjCObjectVar<NSError?>>()
        val canEvaluate = LAContext().canEvaluatePolicy(
            LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            errorVar.ptr
        )
        if (canEvaluate) {
            return@memScoped BiometricAvailability.AVAILABLE
        }
        when (errorVar.value?.code) {
            LAErrorBiometryNotEnrolled -> BiometricAvailability.NOT_ENROLLED
            LAErrorBiometryNotAvailable -> BiometricAvailability.NO_HARDWARE
            else -> BiometricAvailability.UNAVAILABLE
        }
    }

    override suspend fun authenticate(
        title: String,
        subtitle: String,
        negativeButtonText: String
    ): BiometricAuthResult = suspendCancellableCoroutine { continuation ->
        val context = LAContext()
        context.localizedCancelTitle = negativeButtonText
        context.evaluatePolicy(
            LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            subtitle
        ) { success, error ->
            val result = when {
                success -> BiometricAuthResult.Success
                error?.code == LAErrorUserCancel || error?.code == LAErrorUserFallback ->
                    BiometricAuthResult.Cancelled
                else -> BiometricAuthResult.Failed(error?.localizedDescription ?: "Unknown error")
            }
            if (continuation.isActive) continuation.resume(result)
        }

        continuation.invokeOnCancellation {
            context.invalidate()
        }
    }
}

@Composable
actual fun rememberBiometricAuthenticator(): BiometricAuthenticator = IosBiometricAuthenticator()
