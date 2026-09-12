package com.tamin.taminhamrah.feature.taminServices.inspection.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.components.TaminBottomActionBar

/** Per-step bottom-bar scaffold shared by the inspection request wizard's steps. */
@Composable
internal fun InspectionRequestStepScaffold(
    primaryText: String,
    primaryEnabled: Boolean,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
    isPrimaryLoading: Boolean? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            TaminBottomActionBar(
                primaryText = primaryText,
                primaryEnabled = primaryEnabled,
                isPrimaryLoading = isPrimaryLoading,
                onPrimaryClick = onPrimaryClick,
                secondaryText = secondaryText,
                onSecondaryClick = onSecondaryClick,
            )
        },
        contentWindowInsets = WindowInsets(0),
        content = content,
    )
}
