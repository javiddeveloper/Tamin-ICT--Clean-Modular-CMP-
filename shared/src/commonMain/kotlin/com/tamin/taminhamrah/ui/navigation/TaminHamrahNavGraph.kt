package com.tamin.taminhamrah.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.history.HistoryRoute
import com.tamin.taminhamrah.feature.history.historyScreen
import com.tamin.taminhamrah.feature.pensionInquiry.PensionInquiryRoute
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPensionInquiry
import com.tamin.taminhamrah.feature.pensionInquiry.pensionInquiryScreen
import com.tamin.taminhamrah.feature.profile.ProfileRoute
import com.tamin.taminhamrah.feature.profile.profileGraph
import com.tamin.taminhamrah.feature.workshops.WorkshopsRoute
import com.tamin.taminhamrah.feature.workshops.navigateToWorkshops
import com.tamin.taminhamrah.feature.workshops.workshopsScreen
import com.tamin.taminhamrah.openUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaminHamrahNavGraph(
    isLoggedIn: Boolean,
    isLoading: Boolean,
    onLoginClick: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    val showLoginBottomSheet = !isLoggedIn && !isLoading

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") },
                    selected = currentDestination?.hasRoute<Route.Home>() == true,
                    onClick = {
                        navController.navigate(Route.Home) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") },
                    selected = currentDestination?.hasRoute<ProfileRoute.Main>() == true,
                    onClick = {
                        navController.navigate(ProfileRoute.Main(userId = "TaminUser")) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) }, // Use appropriate icon
                    label = { Text("Pension") },
                    selected = currentDestination?.hasRoute<PensionInquiryRoute>() == true,
                    onClick = {
                        navController.navigateToPensionInquiry {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = Route.Home
            ) {
                composable<Route.Home> {
                    HomeScreen(
                        onNavigateToHistory = {
                            navController.navigate(HistoryRoute)
                        },
                        onNavigateToWorkshops = {
                            navController.navigateToWorkshops()
                        }
                    )
                }

                profileGraph(
                    navController = navController,
                    onNavigateToIdentity = { userId ->
                        navController.navigate(ProfileRoute.Identity(userId))
                    },
                    onOpenUrl = { url -> openUrl(url) },
                    onBack = { navController.popBackStack() }
                )

                pensionInquiryScreen()

                historyScreen()

                workshopsScreen()
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (showLoginBottomSheet) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ورود به سامانه تأمین من")
                }
            },
            title = {
                Text(
                    "لطفاً وارد حساب کاربری خود شوید",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    "برای دسترسی به تمام امکانات اپلیکیشن، ابتدا باید وارد حساب کاربری خود شوید.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}

@Composable
fun SampleScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}

@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToWorkshops: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("خانه", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                onClick = onNavigateToHistory,
                modifier = Modifier.fillMaxWidth().height(100.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("کلیه سوابق", style = MaterialTheme.typography.titleMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                onClick = onNavigateToWorkshops,
                modifier = Modifier.fillMaxWidth().height(100.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("کارگاه ها", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
