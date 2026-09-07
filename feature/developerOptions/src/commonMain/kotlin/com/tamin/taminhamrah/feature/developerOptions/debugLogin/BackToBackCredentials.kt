package com.tamin.taminhamrah.feature.developerOptions.debugLogin

/**
 * The back-to-back test client, pre-filled on the debug login screen and re-used when the token
 * screen re-issues that slot.
 *
 * Hardcoded at explicit request and accepted risk: `feature:developerOptions` ships unminified in
 * every build, so both values are readable in the release APK/IPA by anyone who extracts it.
 * Reaching the screens that use them is gated on `AppConfig.isDebug`, but that gate does not strip
 * the string literals themselves.
 */
internal object BackToBackCredentials {
    const val CLIENT_ID = "442e832b206822656b5f816f6a630383"
    const val CLIENT_SECRET = "04136b343832526014771644295c43636e4323051a805f4e1c3b192589820373"
}
