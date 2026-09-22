package com.tamin.taminhamrah.feature.developerOptions.featureFlags

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract.FeatureFlagRowUi
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract.FeatureFlagsEvent
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract.FeatureFlagsIntent
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract.FeatureFlagsUiState
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract.FeatureFlagsUiState.PartialState
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract.OverrideKind
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.repository.feature.FeatureFlagOverrideRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Backs the developer-only "Feature flags" screen: every [FeatureFlag] alongside what it currently
 * resolves to, and a long-press editor that writes a stand-in status through
 * [FeatureFlagOverrideRepository]. [FeatureManager] already reads that repository ahead of the real
 * menu, so a change here is visible everywhere in the app on its very next flag check — no restart.
 */
class FeatureFlagsViewModel(
    private val featureManager: FeatureManager,
    private val overrideRepository: FeatureFlagOverrideRepository,
) : BaseViewModel<FeatureFlagsUiState, PartialState, FeatureFlagsEvent, FeatureFlagsIntent>(
    initialState = FeatureFlagsUiState()
) {

    init {
        sendIntent(FeatureFlagsIntent.OnScreenEntered)
    }

    override fun handleIntent(intent: FeatureFlagsIntent): Flow<PartialState> = when (intent) {
        is FeatureFlagsIntent.OnScreenEntered -> observeRows()

        is FeatureFlagsIntent.OnBackClicked -> {
            sendEvent(FeatureFlagsEvent.NavigateBack)
            emptyFlow()
        }

        is FeatureFlagsIntent.OnRowLongPressed -> flow {
            emit(PartialState.EditorOpened(intent.flag))
        }

        is FeatureFlagsIntent.OnEditorDismissed -> flow {
            emit(PartialState.EditorClosed)
        }

        is FeatureFlagsIntent.OnOverrideConfirmed -> flow {
            val message = intent.message.trim().takeIf { it.isNotEmpty() }
            val status = when (intent.kind) {
                OverrideKind.ENABLED -> FeatureStatus.Enabled
                OverrideKind.DISABLED -> FeatureStatus.Disabled(message)
                OverrideKind.TEMPORARY_DISABLED -> FeatureStatus.TemporaryDisabled(message)
                OverrideKind.ENABLED_WITH_ERROR -> FeatureStatus.EnabledWithError(message)
            }
            overrideRepository.setOverride(intent.flag, status)
            emit(PartialState.EditorClosed)
        }

        is FeatureFlagsIntent.OnOverrideCleared -> flow {
            overrideRepository.clearOverride(intent.flag)
            emit(PartialState.EditorClosed)
        }

        is FeatureFlagsIntent.OnClearAllClicked -> flow {
            overrideRepository.clearAllOverrides()
        }
    }

    /**
     * The whole flag list, live: every status change (a menu refresh, or a change made right here)
     * re-derives the rows, so the screen never needs its own refresh action.
     *
     * Driven by the overrides, not `combine`d with them: [FeatureManager.observeFeatureStatuses]
     * already reads the same overrides internally to resolve its statuses, so a plain `combine` of
     * both would race two dependent views of the same state. `flatMapLatest` instead re-subscribes to
     * a fresh status read every time the overrides change, which also happens to be the only re-read
     * this screen's own writes need — the real menu still pushes its own updates through that same
     * subscription in between.
     */
    private fun observeRows(): Flow<PartialState> =
        overrideRepository.observeOverrides().flatMapLatest { overrides ->
            featureManager.observeFeatureStatuses(FeatureFlag.entries.toSet()).map { statuses ->
                val rows = FeatureFlag.entries.map { flag ->
                    FeatureFlagRowUi(
                        flag = flag,
                        status = statuses[flag] ?: FeatureStatus.Enabled,
                        isOverridden = overrides.containsKey(flag),
                    )
                }
                PartialState.RowsLoaded(rows)
            }
        }

    override fun reduceState(currentState: FeatureFlagsUiState, partialState: PartialState): FeatureFlagsUiState =
        when (partialState) {
            is PartialState.RowsLoaded -> currentState.copy(rows = partialState.rows)
            is PartialState.EditorOpened -> currentState.copy(editingFlag = partialState.flag)
            is PartialState.EditorClosed -> currentState.copy(editingFlag = null)
            is PartialState.Error -> currentState
        }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
