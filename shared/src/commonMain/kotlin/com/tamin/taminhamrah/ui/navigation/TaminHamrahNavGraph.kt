package com.tamin.taminhamrah.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
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
import com.tamin.taminhamrah.feature.cartable.CartableRoute
import com.tamin.taminhamrah.feature.cartable.cartableGraph
import com.tamin.taminhamrah.feature.cartable.navigateToCartable
import androidx.compose.foundation.background
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.filled.ArrowDropDown
import com.tamin.taminhamrah.feature.history.HistoryRoute
import com.tamin.taminhamrah.feature.history.historyScreen
import com.tamin.taminhamrah.feature.contracts.contractsScreen
import com.tamin.taminhamrah.feature.contracts.navigateToContracts
import com.tamin.taminhamrah.feature.pensionInquiry.PensionInquiryRoute
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPensionInquiry
import com.tamin.taminhamrah.feature.pensionInquiry.pensionInquiryScreen
import com.tamin.taminhamrah.feature.profile.ProfileRoute
import com.tamin.taminhamrah.feature.profile.profileGraph
import com.tamin.taminhamrah.feature.workshops.WorkshopsRoute
import com.tamin.taminhamrah.feature.workshops.navigateToWorkshops
import com.tamin.taminhamrah.feature.workshops.workshopsScreen
import com.tamin.taminhamrah.feature.studentInsuranceContract.navigateToFreelanceInsuranceContract
import com.tamin.taminhamrah.feature.studentInsuranceContract.navigateToHousewifeInsuranceContract
import com.tamin.taminhamrah.feature.studentInsuranceContract.navigateToOptionalInsuranceContract
import com.tamin.taminhamrah.feature.studentInsuranceContract.navigateToStudentInsuranceContract
import com.tamin.taminhamrah.feature.studentInsuranceContract.studentInsuranceContractScreen
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.openUrl
import com.tamin.taminhamrah.feature.treatment.TreatmentRoute
import com.tamin.taminhamrah.feature.treatment.navigateToTreatment
import com.tamin.taminhamrah.feature.treatment.treatmentScreen
import androidx.compose.material.icons.filled.Favorite
import com.tamin.taminhamrah.ui.home.HomeViewModel
import com.tamin.taminhamrah.ui.home.contract.HomeEvent
import com.tamin.taminhamrah.ui.home.contract.HomeIntent
import org.koin.compose.viewmodel.koinViewModel

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

    val isBottomBarVisible = currentDestination?.hasRoute<Route.Home>() == true ||
            currentDestination?.hasRoute<ProfileRoute.Main>() == true ||
            currentDestination?.hasRoute<CartableRoute.Main>() == true

    Scaffold(
        bottomBar = {
            androidx.compose.animation.AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = androidx.compose.animation.slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
                ),
                exit = androidx.compose.animation.slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
                )
            ) {
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
                        icon = { Icon(Icons.Default.Favorite, null) },
                    label = { Text("Treatment") },
                    selected = currentDestination?.hasRoute<TreatmentRoute>() == true,
                    onClick = {
                        navController.navigateToTreatment {
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
                        icon = { Icon(Icons.Default.Inbox, null) },
                        label = { Text("Cartable") },
                        selected = currentDestination?.hasRoute<CartableRoute.Main>() == true,
                        onClick = {
                            navController.navigateToCartable {
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
                        onNavigateToContracts = {
                            navController.navigateToContracts()
                        },
                        onNavigateToWorkshops = {
                            navController.navigateToWorkshops()
                        },
                        onNavigateToStudentInsuranceContract = {
                            navController.navigateToStudentInsuranceContract()
                        },
                        onNavigateToFreelanceInsuranceContract = {
                            navController.navigateToFreelanceInsuranceContract()
                        },
                        onNavigateToOptionalInsuranceContract = {
                            navController.navigateToOptionalInsuranceContract()
                        },
                        onNavigateToHousewifeInsuranceContract = {
                            navController.navigateToHousewifeInsuranceContract()
                        },
                        onNavigateToPensionInquiry = {
                            navController.navigateToPensionInquiry()
                        },
                    )
                }

                treatmentScreen()

                profileGraph(
                    navController = navController,
                    onNavigateToIdentity = { userId ->
                        navController.navigate(ProfileRoute.Identity(userId))
                    },
                    onOpenUrl = { url -> openUrl(url) },
                    onBack = { navController.popBackStack() }
                )

                cartableGraph(
                    onNavigateToMyRequests = {
                        navController.navigate(CartableRoute.UserRequests)
                    },
                    onNavigateToPersonalInbox = {
                        navController.navigate(CartableRoute.PersonalInbox)
                    },
                    onBack = { navController.popBackStack() }
                )

                pensionInquiryScreen()

                historyScreen()

                contractsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToService = { flag ->
                        when (flag) {
                            FeatureFlag.STUDENT_INSURANCE -> navController.navigateToStudentInsuranceContract()
                            FeatureFlag.FREELANCE_INSURANCE -> navController.navigateToFreelanceInsuranceContract()
                            FeatureFlag.OPTIONAL_INSURANCE -> navController.navigateToOptionalInsuranceContract()
                            FeatureFlag.HOUSEWIFE_INSURANCE -> navController.navigateToHousewifeInsuranceContract()
                            else -> { }
                        }
                    },
                    onOpenUrl = { url -> openUrl(url) }
                )

                workshopsScreen(navController)

                studentInsuranceContractScreen(onBack = { navController.popBackStack() })
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
    onNavigateToWorkshops: () -> Unit,
    onNavigateToContracts: () -> Unit,
    onNavigateToStudentInsuranceContract: () -> Unit,
    onNavigateToFreelanceInsuranceContract: () -> Unit,
    onNavigateToOptionalInsuranceContract: () -> Unit,
    onNavigateToHousewifeInsuranceContract: () -> Unit,
    onNavigateToPensionInquiry: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Handleevents(
        viewModel = viewModel,
        onNavigateToHistory = onNavigateToHistory,
        onNavigateToWorkshops = onNavigateToWorkshops,
        onNavigateToContracts = onNavigateToContracts,
        onNavigateToStudentInsuranceContract = onNavigateToStudentInsuranceContract,
        onNavigateToFreelanceInsuranceContract = onNavigateToFreelanceInsuranceContract,
        onNavigateToOptionalInsuranceContract = onNavigateToOptionalInsuranceContract,
        onNavigateToHousewifeInsuranceContract = onNavigateToHousewifeInsuranceContract,
        onNavigateToPensionInquiry = onNavigateToPensionInquiry
    )

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "خدمات تأمین من",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                val userTypes = listOf(
                    1 to "بیمه شدگان",
                    2 to "مستمری بگیران",
                    3 to "کارفرمایان"
                )
                val availableTypes = remember(uiState.menuItems) {
                    val typesInData = uiState.menuItems.flatMap { it.showRole.filterNotNull() }.toSet()
                    userTypes.filter { it.first in typesInData }.ifEmpty { userTypes }
                }
                var selectedType by remember(availableTypes) {
                    mutableStateOf(availableTypes.firstOrNull()?.first ?: 1)
                }
                var expanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = availableTypes.find { it.first == selectedType }?.second ?: "انتخاب گروه",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(androidx.compose.material.icons.Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        availableTypes.forEach { (id, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    selectedType = id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val servicesToShow = uiState.menuItems.filter { it.showRole.contains(selectedType) }

                servicesToShow.forEach { service ->
                    val isDisabled = service.status == MenuServiceStatusDN.DISABLED ||
                                     service.status == MenuServiceStatusDN.TEMPORARY_DISABLED ||
                                     service.status == MenuServiceStatusDN.COMPLETELY_DISABLED

                    val cardAlpha = if (isDisabled) 0.5f else 1.0f

                    Card(
                        onClick = {
                            if (!isDisabled) {
                                viewModel.sendIntent(HomeIntent.OnServiceClick(service))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .alpha(cardAlpha),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                // Icon could be added here based on service.icon
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.shapes.small
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Home, // Placeholder
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Spacer(Modifier.width(16.dp))

                                Column {
                                    Text(
                                        service.name ?: "",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    if (!service.subtitle.isNullOrEmpty()) {
                                        Text(
                                            service.subtitle!!,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Show message if present and service is not just ACTIVE
                            if (!service.message.isNullOrEmpty() && service.status != com.tamin.taminhamrah.model.common.MenuServiceStatusDN.ACTIVE) {
                                val msgColor = if (service.status == com.tamin.taminhamrah.model.common.MenuServiceStatusDN.ENABLED_WITH_ERROR)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant

                                Text(
                                    text = service.message!!,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = msgColor,
                                    modifier = Modifier.padding(start = 72.dp, end = 16.dp, bottom = 12.dp)
                                )
                            }
                        }
                    }
                }

                if (uiState.menuItems.isEmpty() && !uiState.isLoading) {
                    Text(
                        "خطا در دریافت اطلاعات یا لیست خالی است",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                    Button(
                        onClick = { viewModel.sendIntent(HomeIntent.LoadMenu) },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("تلاش مجدد")
                    }
                }
            }
        }
    }
}

@Composable
private fun Handleevents(
    viewModel: HomeViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToWorkshops: () -> Unit,
    onNavigateToContracts: () -> Unit,
    onNavigateToStudentInsuranceContract: () -> Unit,
    onNavigateToFreelanceInsuranceContract: () -> Unit,
    onNavigateToOptionalInsuranceContract: () -> Unit,
    onNavigateToHousewifeInsuranceContract: () -> Unit,
    onNavigateToPensionInquiry: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.NavigateToService -> {
                    when (event.flag) {
                        FeatureFlag.MERGE_HISTORY -> onNavigateToHistory()
                        FeatureFlag.WORKSHOPS -> onNavigateToWorkshops()
                        FeatureFlag.CONTRACTS -> onNavigateToContracts()
                        FeatureFlag.STUDENT_INSURANCE -> onNavigateToStudentInsuranceContract()
                        FeatureFlag.FREELANCE_INSURANCE -> onNavigateToFreelanceInsuranceContract()
                        FeatureFlag.OPTIONAL_INSURANCE -> onNavigateToOptionalInsuranceContract()
                        FeatureFlag.HOUSEWIFE_INSURANCE -> onNavigateToHousewifeInsuranceContract()
                        FeatureFlag.PENSION_INQUIRY -> onNavigateToPensionInquiry()
                        else -> { /* Handle other flags if needed */
                        }
                    }
                }

                is HomeEvent.NavigateToWeb -> {
                    openUrl(event.url)
                }

                is HomeEvent.ShowMessage -> {
                    // In a real app, we'd use a SnackbarHostState
                }
            }
        }
    }
}


