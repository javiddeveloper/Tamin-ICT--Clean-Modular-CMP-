package com.tamin.taminhamrah.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Home : Route
    @Serializable
    data object Search : Route
    @Serializable
    data object Settings : Route
    @Serializable
    data object Notifications : Route
}
