package com.tamin.taminhamrah.util

object NetworkConstants {
    const val BASE_URL = "https://eservices.tamin.ir/api/"
    const val BASE_URL_VIEW = "https://eservices.tamin.ir/view/"
    const val BASE_URL_ACCOUNT = "https://account.tamin.ir/auth/"
    const val BASE_URL_HEALTH_PROFILE = "http://172.16.14.115:5700/api/"
    const val AI_BASE_URL = "https://sw.tamin.ir/api/"

    /** The payment gateway, which is its own host — a ticket is confirmed and paid for here. */
    const val BASE_URL_TFH = "https://tfh.tamin.ir/api/v1.1/payment/"

    /** Appended to [BASE_URL_TFH] with the ticket, to bind it to the signed-in user. */
    const val TFH_TICKET_PATH = "ticket/current-user/"
    const val CLIENT_ID = "1c13370e0148031d1546242f2448152e"
    const val REQUEST_TIMEOUT_60_SEC = 60_000L
    const val REQUEST_TIMEOUT_5_MIN = 300_000L
    const val REDIRECT_URI = "mytamin://login"
    const val DEFAULT_AUDIENCE = "https://es.tamin.ir,https://eservices.tamin.ir,https://profile-api.tamin.ir"
    const val EDIT_MOBILE_URL = "https://apim.tamin.ir/t/um-mobile-api.tamin.ir/change-mobile-number/request/v1"
    const val VERIFY_EDIT_MOBILE_URL = "https://apim.tamin.ir/t/um-mobile-api.tamin.ir/change-mobile-number/confirm/v1"

    const val REFERER_MOBILE = "https://profile.tamin.ir/main/change-phone-number"
}

object HeaderConstant {
    const val AUTHORIZATION = "Authorization"
    const val AUTHORIZATION_TYPE = "Bearer "
}

