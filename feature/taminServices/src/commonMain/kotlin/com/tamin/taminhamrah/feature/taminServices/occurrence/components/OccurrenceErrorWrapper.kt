package com.tamin.taminhamrah.feature.taminServices.occurrence.components

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
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_retry

@Composable
fun OccurrenceErrorView(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TaminText(
            text = error,
            color = taminColors.dangerText,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TaminOutlinedButton(
            text = stringResource(Res.string.action_retry),
            onClick = onRetry,
        )
    }
}

/**
 * Mirrors HealthProfileErrorWrapper's isLoading/error/content switch so occurrence step
 * screens share the same structure (Scaffold + TopAppBar + NavigationBar + ErrorWrapper).
 */
@Composable
fun OccurrenceErrorWrapper(
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    shimmerContent: @Composable () -> Unit = { LoadingStateOverlay(modifier = modifier) },
    content: @Composable () -> Unit,
) {
    if (isLoading) {
        shimmerContent()
    } else if (!error.isNullOrEmpty()) {
        OccurrenceErrorView(error = error, onRetry = onRetry, modifier = modifier)
    } else {
        content()
    }
}
