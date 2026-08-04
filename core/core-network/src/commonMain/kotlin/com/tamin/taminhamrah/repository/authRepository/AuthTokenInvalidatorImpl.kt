package com.tamin.taminhamrah.repository.authRepository

import com.tamin.taminhamrah.repository.AuthTokenInvalidator

class AuthTokenInvalidatorImpl : AuthTokenInvalidator {
    private val clearActions = mutableListOf<() -> Unit>()

    override fun registerClearAction(action: () -> Unit) {
        clearActions.add(action)
    }

    override fun invalidateAll() {
        clearActions.forEach { it() }
    }
}
