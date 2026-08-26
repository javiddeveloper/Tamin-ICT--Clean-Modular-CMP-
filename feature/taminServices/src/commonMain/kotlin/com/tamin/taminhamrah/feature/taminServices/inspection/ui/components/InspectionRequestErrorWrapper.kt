package com.tamin.taminhamrah.feature.taminServices.inspection.ui.components

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
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_retry

/** Mirrors the occurrence wizard's isLoading/error/content switch (see `.claude/CLAUDE.md`). */
@Composable
internal fun InspectionRequestErrorWrapper(
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    shimmerContent: @Composable () -> Unit = { LoadingStateOverlay(modifier = modifier) },
    content: @Composable () -> Unit,
) {
    when {
        isLoading -> shimmerContent()
        !error.isNullOrEmpty() -> Column(
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
            TaminOutlinedButton(text = stringResource(Res.string.action_retry), onClick = onRetry)
        }
        else -> content()
    }
}
