package com.tamin.taminhamrah.useCases.auth

import com.tamin.taminhamrah.util.NetworkConstants

class GetSignOutUrlUseCase {
    operator fun invoke(): String {
        return "${NetworkConstants.BASE_URL_ACCOUNT}signout?" +
                "redirect_uri=mytamin://logout" +
                "&response_type=assertion" +
                "&client_id=${NetworkConstants.CLIENT_ID}"
    }
}
