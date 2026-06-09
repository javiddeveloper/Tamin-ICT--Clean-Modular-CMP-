package com.tamin.taminhamrah.util

object NetworkConstants {
    const val BASE_URL = "https://eservices.tamin.ir/api/"
    const val BASE_URL_ACCOUNT = "https://account.tamin.ir/auth/"
    const val CLIENT_ID = "1c13370e0148031d1546242f2448152e"
    const val REQUEST_TIMEOUT_60_SEC = 60_000L
    const val REQUEST_TIMEOUT_5_MIN = 300_000L
    const val REDIRECT_URI = "mytamin://login"
    const val DEFAULT_AUDIENCE = "https://es.tamin.ir,https://eservices.tamin.ir"
    const val EDIT_MOBILE_URL = "https://profile.tamin.ir/api/v2.0/users/data/request-otp"
    const val VERIFY_EDIT_MOBILE_URL = "https://profile.tamin.ir/api/v2.0/users/data/user-mobile"
}

object HeaderConstant {
    const val AUTHORIZATION = "Authorization"
    const val AUTHORIZATION_TYPE = "Bearer "
}

