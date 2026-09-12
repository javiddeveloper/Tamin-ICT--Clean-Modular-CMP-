package com.tamin.taminhamrah.useCases.auth

import com.tamin.taminhamrah.model.auth.DebugLoginResultDN

interface DebugClientCredentialsLoginUseCase {
    /**
     * @param activate whether a successful login should also become the token the app sends.
     * The login screen says yes; the token screen's re-issue button says no, so re-issuing a
     * back-to-back token never quietly moves the tester off the account they were testing with.
     */
    suspend operator fun invoke(
        clientId: String,
        clientSecret: String,
        activate: Boolean = true,
    ): DebugLoginResultDN
}
