package com.tamin.taminhamrah.feature.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.profile.ui.ProfileScreen
import com.tamin.taminhamrah.feature.profile.ui.ProfileViewModel
import com.tamin.taminhamrah.feature.profile.ui.identity.IdentityInRoute
import com.tamin.taminhamrah.feature.profile.ui.identity.IdentityInViewModel
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.ActiveRelationRoute
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.ActiveRelationViewModel
import com.tamin.taminhamrah.ui.sharedViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

import com.tamin.taminhamrah.feature.profile.ui.contactUs.ContactUsRoute
import com.tamin.taminhamrah.feature.profile.ui.contactUs.ContactUsViewModel
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
    data object ActiveRelation : ProfileRoute

    @Serializable
    data object ContactUs : ProfileRoute
    @Serializable
    data object DependentsList : ProfileRoute
}

fun NavGraphBuilder.profileGraph(
    navController: NavController,
    onNavigateToIdentity: (String?) -> Unit,
    onNavigateToChangeMobile: () -> Unit,
    onNavigateToAddDependent: () -> Unit,
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
                onNavigateToVersionHistory = { navController.navigate(ProfileRoute.VersionHistory) },
                onNavigateToActiveRelation = { navController.navigate(ProfileRoute.ActiveRelation) },
                onNavigateToChangeMobile = onNavigateToChangeMobile,
                onNavigateToDependentsList = {navController.navigate(ProfileRoute.DependentsList)},
                onNavigateToContactUs = { navController.navigate(ProfileRoute.ContactUs) },
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
                onNavigateToAddDependent = onNavigateToAddDependent,
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable<ProfileRoute.ActiveRelation> {
            val viewModel = koinViewModel<ActiveRelationViewModel>()

            ActiveRelationRoute(
                viewModel = viewModel,
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable<ProfileRoute.ContactUs> {
            val viewModel = koinViewModel<ContactUsViewModel>()

            ContactUsRoute(
                viewModel = viewModel,
                onOpenUrl = onOpenUrl,
                onBackClicked = { navController.popBackStack() }
            )
        }
    }
}

