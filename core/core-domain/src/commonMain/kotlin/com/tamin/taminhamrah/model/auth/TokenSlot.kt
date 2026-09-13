package com.tamin.taminhamrah.model.auth

/**
 * Where a token is kept, so two logins can be held at once instead of overwriting each other.
 *
 * The app reads through [com.tamin.taminhamrah.repository.TokenStoreManager]'s no-argument
 * accessors, which resolve to whichever slot is active — but only in debug builds. A release build
 * always reads [USER], so slots are invisible outside Developer Options and a shipped app behaves
 * exactly as it did before they existed.
 */
enum class TokenSlot {
    /**
     * The real login: PKCE authorization_code. Its keys are the ones the app has always used, so
     * an existing install keeps its session and nothing needs migrating.
     */
    USER,

    /**
     * The debug-only back-to-back login (client_credentials). Written only by Developer Options,
     * so signing in there no longer costs the tester the account session they were using.
     */
    BACK_TO_BACK,

    /**
     * The AI assistant's chat token. Not an app-wide bearer — it travels in the agent request body
     * — so it is never "active"; it is held here to be inspected and refreshed by hand.
     */
    AGENT,
}
