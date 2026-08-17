package com.tamin.taminhamrah.repository

import kotlinx.coroutines.flow.StateFlow

/**
 * Tracks whether the current app process has already cleared a biometric check, independent of
 * [UserPreferencesRepository]'s persisted `isBiometricEnabled` flag. Deliberately in-memory only
 * (never persisted) — a fresh process must always re-arm the gate.
 *
 * A single shared instance so [markUnlocked] called from any screen that just required a
 * successful [com.tamin.taminhamrah.ui.util.BiometricAuthenticator.authenticate] call (the
 * post-login enrollment prompt, the Security screen toggle) is immediately visible to the
 * app-open gate, without waiting on the slower, disk-backed `UserPreferencesRepository.userData`
 * round-trip that `isBiometricEnabled` itself depends on.
 */
interface BiometricSessionState {
    val isUnlocked: StateFlow<Boolean>
    fun markUnlocked()
}
