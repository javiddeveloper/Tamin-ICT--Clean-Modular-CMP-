package com.tamin.taminhamrah.feature.developerOptions.debugLogin

import com.tamin.taminhamrah.feature.developerOptions.BuildConfig

internal actual object BackToBackCredentials {
    actual val CLIENT_ID: String = BuildConfig.BACK_TO_BACK_CLIENT_ID
    actual val CLIENT_SECRET: String = BuildConfig.BACK_TO_BACK_CLIENT_SECRET
}
