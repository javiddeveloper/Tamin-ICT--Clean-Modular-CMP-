package com.tamin.taminhamrah.feature.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.profile.ui.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface ProfileRoute {
    @Serializable
    data class Main(val userId: String? = null) : ProfileRoute
}

fun NavGraphBuilder.profileGraph(
    onBack: () -> Unit
) {
    composable<ProfileRoute.Main> { backStackEntry ->
        val route: ProfileRoute.Main = backStackEntry.arguments?.let {
            ProfileRoute.Main(it.getString("userId"))
        } ?: ProfileRoute.Main()

        ProfileScreen(
            userId = route.userId,
            onBack = onBack
        )
    }
}
