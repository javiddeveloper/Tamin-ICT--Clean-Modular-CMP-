package com.tamin.taminhamrah.util

object NetworkConstants {
    const val BASE_URL = "https://eservices.tamin.ir/api/"
    const val BASE_URL_VIEW = "https://eservices.tamin.ir/view/"
    const val BASE_URL_ACCOUNT = "https://account.tamin.ir/auth/"
    const val BASE_URL_HEALTH_PROFILE = "http://172.16.14.115:5700/api/"
    const val AI_BASE_URL = "https://sw.tamin.ir/api/"

    /** The payment gateway (تامین فراهم). Every payment in the app is settled through it. */
    const val TFH_BASE_URL = "https://tfh.tamin.ir/api/v1.1/payment/"

    /** Where the gateway sends the browser once it is finished, back into the app. */
    const val PAYMENT_RETURN_URI = "mytamin://payment_callback"

    /**
     * Appended to the gateway's base URL with the ticket, to bind it to the signed-in user.
     *
     * The base it is appended to comes from Developer Options ([TFH_BASE_URL] is only its default),
     * so an overridden gateway host is honoured here too.
     */
    const val TFH_TICKET_PATH = "ticket/current-user/"
    const val CLIENT_ID = "1c13370e0148031d1546242f2448152e"
    const val REQUEST_TIMEOUT_60_SEC = 60_000L
    const val REQUEST_TIMEOUT_5_MIN = 300_000L
    const val REDIRECT_URI = "mytamin://login"
    const val DEFAULT_AUDIENCE = "https://es.tamin.ir,https://eservices.tamin.ir,https://profile-api.tamin.ir,https://tfh.tamin.ir"
    const val EDIT_MOBILE_URL = "https://apim.tamin.ir/t/um-mobile-api.tamin.ir/change-mobile-number/request/v1"
    const val VERIFY_EDIT_MOBILE_URL = "https://apim.tamin.ir/t/um-mobile-api.tamin.ir/change-mobile-number/confirm/v1"

    const val REFERER_MOBILE = "https://profile.tamin.ir/main/change-phone-number"
}

object HeaderConstant {
    const val AUTHORIZATION = "Authorization"
    const val AUTHORIZATION_TYPE = "Bearer "
}

