package com.tamin.taminhamrah.ui.util

expect class ExternalAppLauncher() {
    fun openEmail(email: String, cc: String? = null)
    fun openPhone(phone: String)
    fun openWhatsApp(phone: String)

    /**
     * Hands [text] to the platform's own share sheet, so the person picks the destination.
     *
     * Text rather than a file: the exports on this screen are fetched into memory, and putting a
     * PDF on the sheet would mean a FileProvider and a cache file on Android and a temporary URL on
     * iOS -- a lot of moving parts for something every target app can already accept as text.
     *
     * [title] is the chooser's heading on Android; iOS's sheet has no equivalent and ignores it.
     */
    fun shareText(text: String, title: String? = null)
}
