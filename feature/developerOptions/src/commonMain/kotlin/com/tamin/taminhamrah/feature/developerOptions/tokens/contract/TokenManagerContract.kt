package com.tamin.taminhamrah.feature.developerOptions.tokens.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.developerOptions.tokens.JwtExpiry
import com.tamin.taminhamrah.model.auth.TokenSlot
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class TokenCardUi(
    val slot: TokenSlot,
    val token: String?,
    val expiry: JwtExpiry,
    /** The app is sending this slot's token right now. Never true for [TokenSlot.AGENT]. */
    val isActive: Boolean,
    /**
     * Whether «رفرش» can trade a refresh token in. False on a client_credentials slot, where the
     * button re-issues the login instead — and on the agent slot, which re-asks `chat-allowed`.
     */
    val canRefreshWithRefreshToken: Boolean,
)

@Immutable
data class TokenManagerUiState(
    val cards: ImmutableList<TokenCardUi> = persistentListOf(),
    /** Which card is mid-request, so only that card's buttons are disabled. */
    val busySlot: TokenSlot? = null,
    val statusText: String? = null,
) {
    sealed interface PartialState {
        data class CardsLoaded(val cards: ImmutableList<TokenCardUi>) : PartialState
        data class BusyChanged(val slot: TokenSlot?) : PartialState
        data class StatusChanged(val statusText: String?) : PartialState
    }
}

sealed interface TokenManagerIntent {
    data object Refresh : TokenManagerIntent
    data class OnActivateClicked(val slot: TokenSlot) : TokenManagerIntent
    data class OnRefreshClicked(val slot: TokenSlot) : TokenManagerIntent
    data class OnClearClicked(val slot: TokenSlot) : TokenManagerIntent
    data object OnBackClicked : TokenManagerIntent
}

sealed interface TokenManagerEvent {
    data object NavigateBack : TokenManagerEvent
}
