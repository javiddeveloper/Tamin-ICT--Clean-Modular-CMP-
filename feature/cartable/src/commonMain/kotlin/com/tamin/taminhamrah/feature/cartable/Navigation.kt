package com.tamin.taminhamrah.feature.cartable

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navigation
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.cartable.ui.CartableScreen
import com.tamin.taminhamrah.feature.cartable.ui.UserRequestsScreen
import com.tamin.taminhamrah.feature.cartable.ui.PersonalInboxScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface CartableRoute {
    @Serializable
    data object Graph : CartableRoute

    @Serializable
    data object Main : CartableRoute

    @Serializable
    data object UserRequests : CartableRoute

    @Serializable
    data object PersonalInbox : CartableRoute
}

fun NavController.navigateToCartable(navOptions: NavOptions? = null) {
    navigate(CartableRoute.Main, navOptions)
}

fun NavController.navigateToCartable(builder: NavOptionsBuilder.() -> Unit) {
    navigate(CartableRoute.Main, builder)
}

fun NavGraphBuilder.cartableGraph(
    onNavigateToMyRequests: () -> Unit,
    onNavigateToPersonalInbox: () -> Unit,
    onBack: () -> Unit,
) {
    navigation<CartableRoute.Graph>(startDestination = CartableRoute.Main) {
        composableWithFadeTransitions<CartableRoute.Main> {
            CartableScreen(
                onNavigateToMyRequests = onNavigateToMyRequests,
                onNavigateToPersonalInbox = onNavigateToPersonalInbox,
                onBackClicked = onBack,
            )
        }

        composableWithFadeTransitions<CartableRoute.UserRequests> {
            UserRequestsScreen(onBackClicked = onBack)
        }

        composableWithFadeTransitions<CartableRoute.PersonalInbox> {
            PersonalInboxScreen(onBackClicked = onBack)
        }
    }
}
