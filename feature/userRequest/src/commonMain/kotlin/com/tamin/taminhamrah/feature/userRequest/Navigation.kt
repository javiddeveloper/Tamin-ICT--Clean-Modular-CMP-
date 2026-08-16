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
        val title : String
    ) : UserRequestRoute
}

fun NavController.navigateToUserRequests() {
    navigate(UserRequestRoute.List)
}

fun NavController.navigateToUserRequestDetail(requestId: Long, refCode: String, requestTypeId: Long , title: String) {
    navigate(UserRequestRoute.Detail(requestId, refCode, requestTypeId ,title = title ))
}

fun NavGraphBuilder.userRequestGraph(
    navController: NavController,
) {
    composable<UserRequestRoute.List> {
        UserRequestsScreen(
            onBackClick = { navController.popBackStack() },
            onNavigateToDetail = { requestId, refCode, requestTypeId , title->
                navController.navigateToUserRequestDetail(requestId, refCode, requestTypeId , title)
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
            title = route.title
        )
    }
}
