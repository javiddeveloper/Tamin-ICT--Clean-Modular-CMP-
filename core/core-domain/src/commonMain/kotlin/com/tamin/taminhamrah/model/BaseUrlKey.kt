package com.tamin.taminhamrah.model

import com.tamin.taminhamrah.util.NetworkConstants

enum class BaseUrlKey(val defaultValue: String) {
    MAIN(NetworkConstants.BASE_URL),
    ACCOUNT(NetworkConstants.BASE_URL_ACCOUNT),
    HEALTH_PROFILE(NetworkConstants.BASE_URL_HEALTH_PROFILE),
    AI(NetworkConstants.AI_BASE_URL),
    TFH(NetworkConstants.TFH_BASE_URL)
}
