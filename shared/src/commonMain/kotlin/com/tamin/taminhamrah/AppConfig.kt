package com.tamin.taminhamrah

/**
 * Platform-agnostic application configuration and feature flags.
 * Replaces Android-specific BuildConfig in shared module.
 */
expect object AppConfig {
    val FEATURE_SIMILARITY_SEARCH: Boolean
}
