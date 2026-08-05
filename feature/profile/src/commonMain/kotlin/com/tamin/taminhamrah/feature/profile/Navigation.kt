package com.tamin.taminhamrah.feature.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.profile.ui.ProfileScreen
import com.tamin.taminhamrah.feature.profile.ui.ProfileViewModel
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.ElectronicFileRoute
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.ElectronicFileViewModel
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
    data object ElectronicFile : ProfileRoute

    @Serializable
    data object VersionHistory : ProfileRoute

}

fun NavGraphBuilder.profileGraph(
    navController: NavController,
    onNavigateToIdentity: (String?) -> Unit,
    onNavigateToElectronicFile: () -> Unit,
    onNavigateToChangeMobile: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onBack: () -> Unit
) {
    navigation<ProfileRoute.Graph>(startDestination = ProfileRoute.Main()) {
        composableWithFadeTransitions<ProfileRoute.Main> { backStackEntry ->
            val route = backStackEntry.toRoute<ProfileRoute.Main>()
            val viewModel = backStackEntry.sharedViewModel<ProfileViewModel>(navController)

            ProfileScreen(
                userId = route.userId,
                viewModel = viewModel,
                onNavigateToIdentity = { onNavigateToIdentity(route.userId) },
                onNavigateToElectronicFile = onNavigateToElectronicFile,
                onNavigateToVersionHistory = { navController.navigate(ProfileRoute.VersionHistory) },
                onNavigateToChangeMobile = onNavigateToChangeMobile,
                onOpenUrl = onOpenUrl,
                onBackClicked = onBack
            )
        }

        composableWithFadeTransitions<ProfileRoute.Identity> { backStackEntry ->
            val viewModel = koinViewModel<IdentityInViewModel>()

            IdentityInRoute(
                viewModel = viewModel,
                onBackClicked = onBack
            )
        }

        composable<ProfileRoute.ElectronicFile> {
            val viewModel = koinViewModel<ElectronicFileViewModel>()

            ElectronicFileRoute(
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
    }
}
