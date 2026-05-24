package com.tamin.taminhamrah.useCases.auth

class DeepLinkManagerImpl : DeepLinkManager {
    override fun isAuthLogin(uriString: String): Boolean {
        return uriString.contains("code=") && uriString.contains("mytamin://login")
    }

    override fun extractAuthCode(uriString: String): String? {
        val regex = Regex("code=([^&]+)")
        return regex.find(uriString)?.groupValues?.get(1)
    }
}
