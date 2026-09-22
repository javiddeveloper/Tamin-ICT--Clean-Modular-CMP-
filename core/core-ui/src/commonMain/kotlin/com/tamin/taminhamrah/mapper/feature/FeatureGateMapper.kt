package com.tamin.taminhamrah.mapper.feature

import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.ui.components.ListItemData

/**
 * A list row as its feature flag says it should read — the same states the services page shows.
 *
 * - `null` — the menu has not answered yet, so the row shimmers rather than guess either way.
 * - disabled or temporarily disabled — dimmed, not tappable, the server's reason under the title.
 * - enabled with error — opens as usual, with the server's warning under the title in [warningColor].
 * - enabled or web view — untouched; a web view is opened by whoever handles the tap.
 */
fun ListItemData.gatedBy(status: FeatureStatus?, warningColor: Color): ListItemData = when (status) {
    null -> copy(isLoading = true)
    FeatureStatus.Enabled, is FeatureStatus.WebView -> this
    is FeatureStatus.EnabledWithError -> copy(
        subtitle = status.message ?: subtitle,
        colors = colors.copy(subtitleColor = warningColor),
    )
    is FeatureStatus.Disabled, is FeatureStatus.TemporaryDisabled -> copy(
        enabled = false,
        subtitle = status.serverMessage ?: subtitle,
    )
}
