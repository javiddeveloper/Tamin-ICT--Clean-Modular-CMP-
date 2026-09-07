package com.tamin.taminhamrah.feature.developerOptions

import androidx.navigation.NavGraphBuilder
import com.tamin.taminhamrah.feature.developerOptions.debugLogin.DebugLoginScreen
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

fun NavGraphBuilder.developerOptionsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDebugLogin: () -> Unit,
    onNavigateToTokenManager: () -> Unit
) {
    composableWithFadeTransitions<DeveloperOptionsRoute> {
        DeveloperOptionsScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToDebugLogin = onNavigateToDebugLogin,
            onNavigateToTokenManager = onNavigateToTokenManager
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
