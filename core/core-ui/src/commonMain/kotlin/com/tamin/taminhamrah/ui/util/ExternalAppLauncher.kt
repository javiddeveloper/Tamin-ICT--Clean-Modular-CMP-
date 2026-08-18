package com.tamin.taminhamrah.ui.util

expect class ExternalAppLauncher() {
    fun openEmail(email: String, cc: String? = null)
    fun openPhone(phone: String)
    fun openWhatsApp(phone: String)

    /**
     * Opens [url] in whatever the device uses for the web — the browser on Android, Safari or a
     * registered app on iOS.
     *
     * Does nothing when the device cannot open it (no browser, or a URL it will not parse) rather
     * than throwing: this is always a secondary action on a screen that still works without it.
     */
    fun openUrl(url: String)

    /**
     * Hands [text] to the platform's own share sheet, so the person picks the destination.
     *
     * Text rather than a file: the exports on this screen are fetched into memory, and putting a
     * PDF on the sheet would mean a FileProvider and a cache file on Android and a temporary URL on
     * iOS -- a lot of moving parts for something every target app can already accept as text.
     *
     */

    fun shareText(text: String)
}
