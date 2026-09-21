package com.tamin.taminhamrah.feature.developerOptions.debugLogin

/**
 * The back-to-back test client, pre-filled on the debug login screen and re-used when the token
 * screen re-issues that slot.
 *
 * Platform-specific rather than a plain `object` in this file: on Android the real values live
 * only in the `debug` build type's `BuildConfig`
 * (`feature/developerOptions/build.gradle.kts`) — the `release` build type compiles them as empty
 * strings, so the secret text itself is absent from a release APK's compiled classes, not merely
 * unreachable behind the `AppConfig.isDebug` gate on the screens that read it.
 *
 * iOS has no equivalent build-type-scoped compile step in this project yet, so the iOS `actual`
 * still carries the literal and remains readable in an extracted release IPA — a known gap, not
 * solved here.
 */
internal expect object BackToBackCredentials {
    val CLIENT_ID: String
    val CLIENT_SECRET: String
}
