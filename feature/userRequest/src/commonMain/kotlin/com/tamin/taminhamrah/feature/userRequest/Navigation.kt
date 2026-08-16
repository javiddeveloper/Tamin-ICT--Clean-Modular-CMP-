package com.tamin.taminhamrah.feature.userRequest

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.userRequest.ui.UserRequestsScreen
import com.tamin.taminhamrah.feature.userRequest.ui.screens.UserRequestDetailRoute
import kotlinx.serialization.Serializable

@Serializable
sealed interface UserRequestRoute {
    @Serializable
    data object List : UserRequestRoute

    @Serializable
    data class Detail(
        val requestId: Long,
        val refCode: String,
        val requestTypeId: Long,
        val title: String,
        val referenceId: String = "",
    ) : UserRequestRoute
}

fun NavController.navigateToUserRequests() {
    navigate(UserRequestRoute.List)
}

fun NavController.navigateToUserRequestDetail(
    requestId: Long,
    refCode: String,
    requestTypeId: Long,
    title: String,
    referenceId: String,
) {
    navigate(
        UserRequestRoute.Detail(
            requestId = requestId,
            refCode = refCode,
            requestTypeId = requestTypeId,
            title = title,
            referenceId = referenceId,
        )
    )
}

fun NavGraphBuilder.userRequestGraph(
    navController: NavController,
) {
    composable<UserRequestRoute.List> {
        UserRequestsScreen(
            onBackClick = { navController.popBackStack() },
            onNavigateToDetail = { requestId, refCode, requestTypeId, title, referenceId ->
                navController.navigateToUserRequestDetail(
                    requestId = requestId,
                    refCode = refCode,
                    requestTypeId = requestTypeId,
                    title = title,
                    referenceId = referenceId,
                )
            }
        )
    }

    composable<UserRequestRoute.Detail> { backStackEntry ->
        val route: UserRequestRoute.Detail = backStackEntry.toRoute()
        UserRequestDetailRoute(
            requestId = route.requestId,
            refCode = route.refCode,
            requestTypeId = route.requestTypeId,
            onBackClick = { navController.popBackStack() },
            title = route.title,
            referenceId = route.referenceId,
        )
    }
}
