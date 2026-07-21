package com.tamin.taminhamrah.feature.profile

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.profile.ui.IdentityScreen
import com.tamin.taminhamrah.feature.profile.ui.ProfileScreen
import com.tamin.taminhamrah.feature.profile.ui.ProfileViewModel
import com.tamin.taminhamrah.ui.sharedViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
sealed interface ProfileRoute {
    @Serializable
    data object Graph : ProfileRoute

    @Serializable
    data class Main(val userId: String? = null) : ProfileRoute

    @Serializable
    data class Identity(val userId: String? = null) : ProfileRoute
}

fun NavGraphBuilder.profileGraph(
    navController: NavController,
    onNavigateToIdentity: (String?) -> Unit,
    onNavigateToHealthProfile: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onBack: () -> Unit
) {
    navigation<ProfileRoute.Graph>(startDestination = ProfileRoute.Main()) {
        composable<ProfileRoute.Main> { backStackEntry ->
            val route = backStackEntry.toRoute<ProfileRoute.Main>()
            val viewModel = backStackEntry.sharedViewModel<ProfileViewModel>(navController)

            ProfileScreen(
                userId = route.userId,
                viewModel = viewModel,
                onNavigateToIdentity = { onNavigateToIdentity(route.userId) },
                onNavigateToHealthProfile = onNavigateToHealthProfile,
                onOpenUrl = onOpenUrl,
                onBackClicked = onBack
            )
        }

        composable<ProfileRoute.Identity> { backStackEntry ->
            val route = backStackEntry.toRoute<ProfileRoute.Identity>()
            val viewModel = backStackEntry.sharedViewModel<ProfileViewModel>(navController)

            IdentityScreen(
                userId = route.userId,
                viewModel = viewModel,
                onBackClicked = onBack
            )
        }
    }
}
