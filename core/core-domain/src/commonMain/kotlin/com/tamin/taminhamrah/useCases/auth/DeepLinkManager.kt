package com.tamin.taminhamrah.useCases.auth

interface DeepLinkManager {
    fun isAuthLogin(uriString: String): Boolean
    fun extractAuthCode(uriString: String): String?
}
