package com.tamin.taminhamrah.core.datastore

import com.tamin.taminhamrah.repository.BiometricSessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryBiometricSessionState : BiometricSessionState {

    private val _isUnlocked = MutableStateFlow(false)

    override val isUnlocked: StateFlow<Boolean>
        get() = _isUnlocked.asStateFlow()

    override fun markUnlocked() {
        _isUnlocked.value = true
    }
}
