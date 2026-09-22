package com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus

/**
 * The shape a status override can take from this screen. Mirrors [FeatureStatus]'s constructors
 * one for one, minus [FeatureStatus.WebView] — a dev screen is for exercising the disabled/error
 * states a real menu might not currently be serving, and a web-view redirect is not one of them.
 */
enum class OverrideKind { ENABLED, DISABLED, TEMPORARY_DISABLED, ENABLED_WITH_ERROR }

/** One row of the list: a flag, what it currently resolves to, and whether that answer is an override. */
@Immutable
data class FeatureFlagRowUi(
    val flag: FeatureFlag,
    val status: FeatureStatus,
    val isOverridden: Boolean,
)

@Immutable
data class FeatureFlagsUiState(
    val rows: List<FeatureFlagRowUi> = emptyList(),
    /** The flag a long-press opened the editor for; null when the editor is closed. */
    val editingFlag: FeatureFlag? = null,
) {
    sealed interface PartialState {
        data class RowsLoaded(val rows: List<FeatureFlagRowUi>) : PartialState
        data class EditorOpened(val flag: FeatureFlag) : PartialState
        data object EditorClosed : PartialState
        /** Nothing on this screen can actually fail — required by `BaseViewModel`, otherwise unused. */
        data class Error(val message: String) : PartialState
    }
}

sealed interface FeatureFlagsIntent {
    /** Starts the live subscription to statuses/overrides; sent once from the ViewModel's `init`. */
    data object OnScreenEntered : FeatureFlagsIntent
    data object OnBackClicked : FeatureFlagsIntent
    data class OnRowLongPressed(val flag: FeatureFlag) : FeatureFlagsIntent
    data object OnEditorDismissed : FeatureFlagsIntent
    data class OnOverrideConfirmed(val flag: FeatureFlag, val kind: OverrideKind, val message: String) : FeatureFlagsIntent
    data class OnOverrideCleared(val flag: FeatureFlag) : FeatureFlagsIntent
    data object OnClearAllClicked : FeatureFlagsIntent
}

sealed interface FeatureFlagsEvent {
    data object NavigateBack : FeatureFlagsEvent
}
