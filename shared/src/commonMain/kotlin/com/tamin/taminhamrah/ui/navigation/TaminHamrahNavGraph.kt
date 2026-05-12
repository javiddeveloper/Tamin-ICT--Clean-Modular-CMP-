package com.tamin.taminhamrah.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun TaminHamrahNavGraph() {
    var selectedRoute by remember { mutableStateOf<Route>(Route.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    Triple(Route.Home, "Home", Icons.Default.Home),
                    Triple(Route.Search, "Search", Icons.Default.Search),
                    Triple(Route.Notifications, "Alerts", Icons.Default.Notifications),
                    Triple(Route.Profile, "Profile", Icons.Default.Person),
                    Triple(Route.Settings, "Settings", Icons.Default.Settings),
                )
                items.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = selectedRoute == route,
                        onClick = { selectedRoute = route }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AnimatedContent(targetState = selectedRoute) { route ->
                when (route) {
                    Route.Home -> SampleScreen("Home Screen")
                    Route.Search -> SampleScreen("Search Screen")
                    Route.Notifications -> SampleScreen("Notifications Screen")
                    Route.Profile -> SampleScreen("Profile Screen")
                    Route.Settings -> SampleScreen("Settings Screen")
                }
            }
        }
    }
}

@Composable
fun SampleScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
