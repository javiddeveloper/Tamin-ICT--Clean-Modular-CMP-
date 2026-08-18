package com.tamin.taminhamrah.feature.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.BankAccountRoute
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.BankAccountViewModel
import com.tamin.taminhamrah.feature.profile.ui.ProfileScreen
import com.tamin.taminhamrah.feature.profile.ui.ProfileViewModel
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.ElectronicFileRoute
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.ElectronicFileViewModel
import com.tamin.taminhamrah.feature.profile.ui.identity.IdentityInRoute
import com.tamin.taminhamrah.feature.profile.ui.identity.IdentityInViewModel
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.ActiveRelationRoute
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.ActiveRelationViewModel
import com.tamin.taminhamrah.ui.sharedViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

import com.tamin.taminhamrah.feature.profile.ui.contactUs.ContactUsRoute
import com.tamin.taminhamrah.feature.profile.ui.contactUs.ContactUsViewModel
import com.tamin.taminhamrah.feature.profile.ui.dependents.DependentsListRoute
import com.tamin.taminhamrah.feature.profile.ui.dependents.DependentsListViewModel
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

    @Serializable
    data object BankAccount : ProfileRoute

    @Serializable
    data object ActiveRelation : ProfileRoute

    @Serializable
    data object ContactUs : ProfileRoute

    @Serializable
    data object DependentsList : ProfileRoute

    @Serializable
    data object UserRequests : ProfileRoute
}

fun NavGraphBuilder.profileGraph(
    navController: NavController,
    onNavigateToIdentity: (String?) -> Unit,
    onNavigateToElectronicFile: () -> Unit,
    onNavigateToMyInbox: () -> Unit,
    onNavigateToChangeMobile: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAddDependent: () -> Unit,
    onNavigateToUserRequests: () -> Unit,
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
                onNavigateToActiveRelation = { navController.navigate(ProfileRoute.ActiveRelation) },
                onNavigateToChangeMobile = onNavigateToChangeMobile,
                onNavigateToBankAccount = { navController.navigate(ProfileRoute.BankAccount) },
                onNavigateToMyInbox = onNavigateToMyInbox,
                onNavigateToContactUs = { navController.navigate(ProfileRoute.ContactUs) },
                onNavigateToSecurity = onNavigateToSecurity,
                onNavigateToDependentsList = {navController.navigate(ProfileRoute.DependentsList)},
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToUserRequests = onNavigateToUserRequests,
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

        composable<ProfileRoute.DependentsList> {
            val viewModel = koinViewModel<DependentsListViewModel>()
            DependentsListRoute(
                viewModel = viewModel,
                onNavigateToAddDependent = onNavigateToAddDependent,
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


        composableWithFadeTransitions<ProfileRoute.BankAccount> {
            val viewModel = koinViewModel<BankAccountViewModel>()

            BankAccountRoute(
                viewModel = viewModel,
                onBackClicked = { navController.popBackStack() },
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
