package com.tamin.taminhamrah.data.remote.models.services

import com.google.gson.annotations.SerializedName

/**
 * Represents the Sentry configuration that can be fetched from the server.
 * All properties are nullable so that if the server doesn't provide a value,
 * the Sentry SDK's default will be used.
 */
data class SentryConfig(


    @SerializedName("is_enabled")
    val isEnabled: Boolean? = false,
    @SerializedName("is_send_default_pii")
    val isSendDefaultPii: Boolean? = null,

    @SerializedName("is_attach_view_hierarchy")
    val isAttachViewHierarchy: Boolean? = null,

    @SerializedName("is_start_profiler_on_app_start")
    val isStartProfilerOnAppStart: Boolean? = null,

    @SerializedName("is_attach_screenshot")
    val isAttachScreenshot: Boolean? = null,

    @SerializedName("is_enable_user_interaction_tracing")
    val isEnableUserInteractionTracing: Boolean? = null,

    @SerializedName("traces_sample_rate")
    val tracesSampleRate: Double? = null,

    @SerializedName("profiles_sample_rate")
    val profilesSampleRate: Double? = null,

    @SerializedName("sample_rate")
    val sampleRate: Double? = null,

    @SerializedName("profile_lifecycle")
    val profileLifecycle: String? = null, // Expected values: "TRACE"

    @SerializedName("session_replay")
    val sessionReplay: SentrySessionReplayConfig? = null
)

data class SentrySessionReplayConfig(
    @SerializedName("on_error_sample_rate")
    val onErrorSampleRate: Double? = null,

    @SerializedName("session_sample_rate")
    val sessionSampleRate: Double? = null
)
