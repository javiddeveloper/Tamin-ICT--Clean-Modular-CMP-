package com.tamin.taminhamrah.feature.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.profile.ui.ProfileScreen
import com.tamin.taminhamrah.feature.profile.ui.ProfileViewModel
import com.tamin.taminhamrah.feature.profile.ui.identity.IdentityInRoute
import com.tamin.taminhamrah.feature.profile.ui.identity.IdentityInViewModel
import com.tamin.taminhamrah.ui.sharedViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

import com.tamin.taminhamrah.feature.profile.ui.versionHistory.VersionHistoryRoute
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.VersionHistoryViewModel

@Serializable
sealed interface ProfileRoute {
    @Serializable
    data object Graph : ProfileRoute

    @Serializable
    data class Main(val userId: String? = null) : ProfileRoute

    @Serializable
    data class Identity(val userId: String? = null) : ProfileRoute

    @Serializable
    data object VersionHistory : ProfileRoute

    @Serializable
    data object AddDependent : ProfileRoute

    @Serializable
    data object DependentsList : ProfileRoute
}

fun NavGraphBuilder.profileGraph(
    navController: NavController,
    onNavigateToIdentity: (String?) -> Unit,
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
                onNavigateToVersionHistory = { navController.navigate(ProfileRoute.VersionHistory) },
                onOpenUrl = onOpenUrl,
                onBackClicked = onBack
            )
        }

        composable<ProfileRoute.Identity> {
            val viewModel = koinViewModel<IdentityInViewModel>()

            IdentityInRoute(
                viewModel = viewModel,
                onBackClicked = onBack
            )
        }

        composable<ProfileRoute.VersionHistory> {
            val viewModel = koinViewModel<VersionHistoryViewModel>()

            VersionHistoryRoute(
                viewModel = viewModel,
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable<ProfileRoute.DependentsList> {
            val viewModel = koinViewModel<com.tamin.taminhamrah.feature.profile.ui.dependents.DependentsListViewModel>()
            com.tamin.taminhamrah.feature.profile.ui.dependents.DependentsListRoute(
                viewModel = viewModel,
                onNavigateToAddDependent = { navController.navigate(ProfileRoute.AddDependent) },
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable<ProfileRoute.AddDependent> {
            val viewModel = koinViewModel<com.tamin.taminhamrah.feature.profile.ui.addDependent.AddDependentViewModel>()
            com.tamin.taminhamrah.feature.profile.ui.addDependent.AddDependentRoute(
                viewModel = viewModel,
                onBackClicked = { navController.popBackStack() }
            )
        }
    }
}

