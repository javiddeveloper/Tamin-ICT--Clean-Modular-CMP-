package com.tamin.taminhamrah.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.*
import androidx.compose.animation.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tamin.taminhamrah.feature.cartable.CartableRoute
import com.tamin.taminhamrah.feature.cartable.cartableGraph
import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.feature.contracts.contractsScreen
import com.tamin.taminhamrah.feature.contracts.navigateToContracts
import com.tamin.taminhamrah.feature.history.HistoryRoute
import com.tamin.taminhamrah.feature.history.historyScreen
import com.tamin.taminhamrah.feature.pensionInquiry.calculatePensionScreen
import com.tamin.taminhamrah.feature.pensionInquiry.deferredInstallmentScreen
import com.tamin.taminhamrah.feature.pensionInquiry.deservedTreatmentScreen
import com.tamin.taminhamrah.feature.pensionInquiry.disabilityPensionScreen
import com.tamin.taminhamrah.feature.pensionInquiry.edictScreen
import com.tamin.taminhamrah.feature.pensionInquiry.girlSurvivorScreen
import com.tamin.taminhamrah.feature.pensionInquiry.issuanceCertificateScreen
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToCalculatePension
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToDeferredInstallment
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToDeservedTreatment
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToDisabilityPension
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToEdict
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToGirlSurvivor
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToIssuanceCertificate
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPayRoll
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPensionInquiry
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPensionSurvivor
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPrescription
import com.tamin.taminhamrah.feature.pensionInquiry.payrollScreen
import com.tamin.taminhamrah.feature.pensionInquiry.pensionInquiryScreen
import com.tamin.taminhamrah.feature.pensionInquiry.pensionSurvivorScreen
import com.tamin.taminhamrah.feature.pensionInquiry.prescriptionScreen
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
import com.tamin.taminhamrah.feature.treatment.TreatmentRoute
import com.tamin.taminhamrah.feature.treatment.treatmentGraph
import com.tamin.taminhamrah.feature.healthProfile.healthProfileScreen
import com.tamin.taminhamrah.feature.healthProfile.navigateToHealthProfile
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import com.tamin.taminhamrah.ui.blur.FloatingGlassNavigationBar
import com.tamin.taminhamrah.ui.blur.NavigationBarItemContent
import com.tamin.taminhamrah.ui.contract.CustomNavigationBarItem
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.openUrl
import com.tamin.taminhamrah.ui.home.HomeViewModel
import com.tamin.taminhamrah.ui.home.contract.HomeEvent
import com.tamin.taminhamrah.ui.home.contract.HomeIntent
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.lifecycle.Lifecycle
import androidx.compose.runtime.getValue
import com.tamin.taminhamrah.ui.blur.safeHazeSource
import taminx.core.core_ui.Res
import taminx.core.core_ui.error_load_menu_failed
import taminx.core.core_ui.ic_home_menu
import taminx.core.core_ui.ic_profile_menu
import taminx.core.core_ui.ic_services_menu
import taminx.core.core_ui.ic_treatment_menu
import taminx.core.core_ui.login_required_desc
import taminx.core.core_ui.login_to_tamin_man
import taminx.core.core_ui.please_login_to_your_account
import taminx.core.core_ui.retry
import taminx.core.core_ui.select_group
import taminx.core.core_ui.tab_home
import taminx.core.core_ui.tab_profile
import taminx.core.core_ui.tab_services
import taminx.core.core_ui.tab_treatment
import taminx.core.core_ui.tamin_man_services
import taminx.core.core_ui.user_type_employer
import taminx.core.core_ui.user_type_insured
import taminx.core.core_ui.user_type_pensioner

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

    val isCartableSelected = currentDestination?.hasRoute<CartableRoute.Main>() == true
    val isTreatmentSelected = currentDestination?.hasRoute<TreatmentRoute.Main>() == true
    val isProfileSelected = currentDestination?.hasRoute<ProfileRoute.Main>() == true
    val isHomeSelected = currentDestination?.hasRoute<Route.Home>() == true


    val navigationItems = listOf(
        NavigationTab(
            title = stringResource(Res.string.tab_home),
            isSelected = isHomeSelected,
            icon = Res.drawable.ic_home_menu,
            onClick = {
                navController.navigate(Route.Home) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ),

        NavigationTab(
            title = stringResource(Res.string.tab_services),
            isSelected = isCartableSelected,
            icon = Res.drawable.ic_services_menu,
            onClick = {
                navController.navigate(CartableRoute.Main) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ),
        NavigationTab(
            title = stringResource(Res.string.tab_treatment),
            isSelected = isTreatmentSelected,
            icon = Res.drawable.ic_treatment_menu,
            onClick = {
                navController.navigate(TreatmentRoute.Main) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ),
        NavigationTab(
            title = stringResource(Res.string.tab_profile),
            isSelected = isProfileSelected,
            icon = Res.drawable.ic_profile_menu,
            onClick = {
                navController.navigate(ProfileRoute.Main()) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ),

        )

    val isBottomBarVisible =
        isHomeSelected || isCartableSelected || isTreatmentSelected || isProfileSelected
    val hazeState = remember { HazeState(initialBlurEnabled = true) }
    Scaffold(
        contentWindowInsets = WindowInsets(0),
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                )
                            )
                        )
                ) {
                    FloatingGlassNavigationBar(
                        hazeState = hazeState,
                        isBlurEnabled = isBottomBarVisible
                    ) {

                        navigationItems.forEach { navigationItem ->

                            val containerColor: Brush =
                                if (navigationItem.isSelected) Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                                    ), tileMode = TileMode.Clamp
                                ) else Brush.sweepGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Transparent
                                    )
                                )
                            val contentColor =
                                if (navigationItem.isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                            CustomNavigationBarItem(
                                icon = {
                                    NavigationBarItemContent(
                                        icon = {
                                            Icon(
                                                painter = painterResource(navigationItem.icon),
                                                null,
                                                tint = contentColor
                                            )
                                        },
                                        label = {
                                            Text(
                                                navigationItem.title,
                                                color = contentColor,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        containerBrush = containerColor,
                                        radius = 24,
                                    )
                                },
                                selected = navigationItem.isSelected,
                                onClick = navigationItem.onClick,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .safeHazeSource(state = hazeState, isEnabled = isBottomBarVisible)
        ) {
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
                        onNavigateToCalculatePension = {
                            navController.navigateToCalculatePension()
                        },
                        onNavigateToPrescription = {
                            navController.navigateToPrescription()
                        },
                        onNavigateToDeservedTreatment = {
                            navController.navigateToDeservedTreatment()
                        },
                        onNavigateToPayRoll = {
                            navController.navigateToPayRoll()
                        },
                        onNavigateToEdict = {
                            navController.navigateToEdict()
                        },
                        onNavigateToIssuanceCertificate = {
                            navController.navigateToIssuanceCertificate()
                        },
                        onNavigateToDeferredInstallment = {
                            navController.navigateToDeferredInstallment()
                        },
                        onNavigateToGirlSurvivor = {
                            navController.navigateToGirlSurvivor()
                        },
                        onNavigateToPensionSurvivor = {
                            navController.navigateToPensionSurvivor()
                        },
                        onNavigateToDisabilityPension = {
                            navController.navigateToDisabilityPension()
                        },
                    )
                }

                treatmentGraph(
                    navController = navController,
                    onNavigateToHealthProfile = { nationalCode ->
                        navController.navigateToHealthProfile(nationalCode)
                    },
                    onBack = { navController.popBackStack() }
                )

                healthProfileScreen(
                    onBack = { navController.popBackStack() }
                )

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
                calculatePensionScreen(onBack = { navController.popBackStack() })
                prescriptionScreen(onBack = { navController.popBackStack() })
                deservedTreatmentScreen(onBack = { navController.popBackStack() })
                payrollScreen(onBack = { navController.popBackStack() })
                edictScreen(onBack = { navController.popBackStack() })
                issuanceCertificateScreen(onBack = { navController.popBackStack() })
                deferredInstallmentScreen(onBack = { navController.popBackStack() })
                girlSurvivorScreen(onBack = { navController.popBackStack() })
                pensionSurvivorScreen(onBack = { navController.popBackStack() })
                disabilityPensionScreen(onBack = { navController.popBackStack() })

                historyScreen()

                contractsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToService = { flag ->
                        when (flag) {
                            FeatureFlag.STUDENT_INSURANCE -> navController.navigateToStudentInsuranceContract()
                            FeatureFlag.FREELANCE_INSURANCE -> navController.navigateToFreelanceInsuranceContract()
                            FeatureFlag.OPTIONAL_INSURANCE -> navController.navigateToOptionalInsuranceContract()
                            FeatureFlag.HOUSEWIFE_INSURANCE -> navController.navigateToHousewifeInsuranceContract()
                            else -> {}
                        }
                    },
                    onOpenUrl = { url -> openUrl(url) }
                )

                workshopsScreen(navController)

                studentInsuranceContractScreen(onBack = { navController.popBackStack() })
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = fadeIn(
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
                ),
                exit = fadeOut(
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
                ),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                )
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
                    Text(stringResource(Res.string.login_to_tamin_man))
                }
            },
            title = {
                Text(
                    stringResource(Res.string.please_login_to_your_account),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    stringResource(Res.string.login_required_desc),
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
    onNavigateToCalculatePension: () -> Unit,
    onNavigateToPrescription: () -> Unit,
    onNavigateToDeservedTreatment: () -> Unit,
    onNavigateToPayRoll: () -> Unit,
    onNavigateToEdict: () -> Unit,
    onNavigateToIssuanceCertificate: () -> Unit,
    onNavigateToDeferredInstallment: () -> Unit,
    onNavigateToGirlSurvivor: () -> Unit,
    onNavigateToPensionSurvivor: () -> Unit,
    onNavigateToDisabilityPension: () -> Unit,
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
        onNavigateToPensionInquiry = onNavigateToPensionInquiry,
        onNavigateToCalculatePension = onNavigateToCalculatePension,
        onNavigateToPrescription = onNavigateToPrescription,
        onNavigateToDeservedTreatment = onNavigateToDeservedTreatment,
        onNavigateToPayRoll = onNavigateToPayRoll,
        onNavigateToEdict = onNavigateToEdict,
        onNavigateToIssuanceCertificate = onNavigateToIssuanceCertificate,
        onNavigateToDeferredInstallment = onNavigateToDeferredInstallment,
        onNavigateToGirlSurvivor = onNavigateToGirlSurvivor,
        onNavigateToPensionSurvivor = onNavigateToPensionSurvivor,
        onNavigateToDisabilityPension = onNavigateToDisabilityPension
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                // Top padding for content breathing room
                .padding(top = 16.dp)
                // Bottom padding so last item scrolls fully above the floating blur bar
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(Res.string.tamin_man_services),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            val userTypes = listOf(
                1 to stringResource(Res.string.user_type_insured),
                2 to stringResource(Res.string.user_type_pensioner),
                3 to stringResource(Res.string.user_type_employer)
            )
            val availableTypes = remember(uiState.menuItems) {
                val typesInData =
                    uiState.menuItems.flatMap { it.showRole.filterNotNull() }.toSet()
                userTypes.filter { it.first in typesInData }.ifEmpty { userTypes }
            }
            var selectedType by remember(availableTypes) {
                mutableStateOf(availableTypes.firstOrNull()?.first ?: 1)
            }
            var expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = availableTypes.find { it.first == selectedType }?.second
                            ?: stringResource(Res.string.select_group),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
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
                        if (!service.message.isNullOrEmpty() && service.status != MenuServiceStatusDN.ACTIVE) {
                            val msgColor =
                                if (service.status == MenuServiceStatusDN.ENABLED_WITH_ERROR)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant

                            Text(
                                text = service.message!!,
                                style = MaterialTheme.typography.labelMedium,
                                color = msgColor,
                                modifier = Modifier.padding(
                                    start = 72.dp,
                                    end = 16.dp,
                                    bottom = 12.dp
                                )
                            )
                        }
                    }
                }
            }

            if (uiState.menuItems.isEmpty() && !uiState.isLoading) {
                Text(
                    stringResource(Res.string.error_load_menu_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 32.dp)
                )
                Button(
                    onClick = { viewModel.sendIntent(HomeIntent.LoadMenu) },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(stringResource(Res.string.retry))
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
    onNavigateToPensionInquiry: () -> Unit,
    onNavigateToCalculatePension: () -> Unit,
    onNavigateToPrescription: () -> Unit,
    onNavigateToDeservedTreatment: () -> Unit,
    onNavigateToPayRoll: () -> Unit,
    onNavigateToEdict: () -> Unit,
    onNavigateToIssuanceCertificate: () -> Unit,
    onNavigateToDeferredInstallment: () -> Unit,
    onNavigateToGirlSurvivor: () -> Unit,
    onNavigateToPensionSurvivor: () -> Unit,
    onNavigateToDisabilityPension: () -> Unit
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
                        FeatureFlag.CALCULATE_WAGE_PENSION -> onNavigateToCalculatePension()
                        FeatureFlag.PRESCRIPTION -> onNavigateToPrescription()
                        FeatureFlag.DESERVED_TREATMENT_101 -> onNavigateToDeservedTreatment()
                        FeatureFlag.PAY_ROLL -> onNavigateToPayRoll()
                        FeatureFlag.EDICT_PENSIONER -> onNavigateToEdict()
                        FeatureFlag.ISSUANCE_WAGE_CERTIFICATE -> onNavigateToIssuanceCertificate()
                        FeatureFlag.DEFERRED_INSTALLMENT_CERTIFICATE -> onNavigateToDeferredInstallment()
                        FeatureFlag.GIRL_SURVIVOR -> onNavigateToGirlSurvivor()
                        FeatureFlag.REQUEST_PENSION_BY_SURVIVOR_112 -> onNavigateToPensionSurvivor()
                        FeatureFlag.DISABILITY_PENSION -> onNavigateToDisabilityPension()
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


