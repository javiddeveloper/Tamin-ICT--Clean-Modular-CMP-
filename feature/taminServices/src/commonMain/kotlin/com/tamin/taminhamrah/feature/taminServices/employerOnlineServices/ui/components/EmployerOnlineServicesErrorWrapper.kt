package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_online_services_error_retry

/**
 * The centred message + retry error view every خدمات غیرحضوری کارفرمایان screen shows for a fatal,
 * retryable load failure — the same shape [com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionRequestErrorWrapper]
 * and the occurrence wizard use, so the three flows read the same. A retry re-issues only the call
 * behind the given [EmployerOnlineServicesErrorSource]. Genuine "nothing here yet" states keep using
 * `EmptyStateMessage` — this is errors only.
 */
@Composable
internal fun EmployerOnlineServicesErrorView(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(Spacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TaminText(
            text = error,
            color = LocalTaminColors.current.dangerText,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.md))
        TaminOutlinedButton(
            text = stringResource(Res.string.employer_online_services_error_retry),
            onClick = onRetry,
        )
    }
}

/**
 * `isLoading` / `error` / `content` switch — mirrors `InspectionRequestErrorWrapper` for screens that
 * gate their whole body on one call.
 */
@Composable
internal fun EmployerOnlineServicesErrorWrapper(
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    shimmerContent: @Composable () -> Unit = { LoadingStateOverlay(modifier = modifier) },
    content: @Composable () -> Unit,
) {
    when {
        isLoading -> shimmerContent()
        !error.isNullOrEmpty() -> EmployerOnlineServicesErrorView(
            error = error,
            onRetry = onRetry,
            modifier = modifier,
        )
        else -> content()
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerOnlineServicesErrorViewPreviewLight() {
    PreviewRtlThemeContent {
        EmployerOnlineServicesErrorView(
            error = "دریافت اطلاعات با خطا روبه‌رو شد. اتصال اینترنت خود را بررسی کنید.",
            onRetry = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerOnlineServicesErrorViewPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        EmployerOnlineServicesErrorView(
            error = "دریافت اطلاعات با خطا روبه‌رو شد. اتصال اینترنت خود را بررسی کنید.",
            onRetry = {},
        )
    }
}
