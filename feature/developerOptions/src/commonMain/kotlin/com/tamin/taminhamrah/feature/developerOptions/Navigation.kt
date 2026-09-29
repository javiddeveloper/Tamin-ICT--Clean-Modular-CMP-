package com.tamin.taminhamrah.feature.developerOptions

import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.feature.developerOptions.debugLogin.DebugLoginScreen
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.FeatureFlagsScreen
import com.tamin.taminhamrah.feature.developerOptions.tokens.TokenManagerScreen
import com.tamin.taminhamrah.feature.developerOptions.ui.DeveloperOptionsScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
object DeveloperOptionsRoute

@Serializable
object DebugLoginRoute

@Serializable
object TokenManagerRoute

@Serializable
object FeatureFlagsRoute

fun NavGraphBuilder.developerOptionsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDebugLogin: () -> Unit,
    onNavigateToTokenManager: () -> Unit,
    onNavigateToFeatureFlags: () -> Unit,
    /**
     * Starts the shared payment flow on a throwaway ticket.
     *
     * Passed in as a callback rather than imported: `:feature:payment` is another feature, and
     * feature modules do not depend on each other.
     */
    onStartTestPayment: () -> Unit,
    /**
     * Opens the assistant directly, bypassing its availability-gated entry point, so the mock
     * access modes (refused, offline) can be looked at. Same reasoning as [onStartTestPayment]:
     * `:feature:agent` is another feature, so it is a callback.
     */
    onOpenAgent: () -> Unit
) {
    composableWithFadeTransitions<DeveloperOptionsRoute> {
        DeveloperOptionsScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToDebugLogin = onNavigateToDebugLogin,
            onNavigateToTokenManager = onNavigateToTokenManager,
            onNavigateToFeatureFlags = onNavigateToFeatureFlags,
            onStartTestPayment = onStartTestPayment,
            onOpenAgent = onOpenAgent
        )
    }
}

fun NavGraphBuilder.debugLoginScreen(
    onNavigateBack: () -> Unit
) {
    composableWithFadeTransitions<DebugLoginRoute> {
        DebugLoginScreen(
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavGraphBuilder.tokenManagerScreen(
    onNavigateBack: () -> Unit
) {
    composableWithFadeTransitions<TokenManagerRoute> {
        TokenManagerScreen(
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavGraphBuilder.featureFlagsScreen(
    onNavigateBack: () -> Unit
) {
    composableWithFadeTransitions<FeatureFlagsRoute> {
        FeatureFlagsScreen(
            onNavigateBack = onNavigateBack
        )
    }
}
