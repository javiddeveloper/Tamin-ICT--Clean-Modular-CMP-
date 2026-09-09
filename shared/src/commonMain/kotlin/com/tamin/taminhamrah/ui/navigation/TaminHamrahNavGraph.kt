package com.tamin.taminhamrah.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.addDependent.AddDependentRoute
import com.tamin.taminhamrah.feature.addDependent.addDependentGraph
import com.tamin.taminhamrah.feature.agent.AgentDestination
import com.tamin.taminhamrah.feature.agent.agentScreen
import com.tamin.taminhamrah.feature.agent.navigateToAgent
import com.tamin.taminhamrah.feature.cartable.CartableRoute
import com.tamin.taminhamrah.feature.cartable.cartableGraph
import com.tamin.taminhamrah.feature.changemobile.changeMobileScreen
import com.tamin.taminhamrah.feature.changemobile.navigateToChangeMobile
import com.tamin.taminhamrah.feature.contracts.contractsScreen
import com.tamin.taminhamrah.feature.contracts.navigateToContracts
import com.tamin.taminhamrah.feature.deferredInstallment.deferredInstallmentScreen
import com.tamin.taminhamrah.feature.deferredInstallment.navigateToDeferredInstallment
import com.tamin.taminhamrah.feature.developerOptions.DeveloperOptionsRoute
import com.tamin.taminhamrah.feature.developerOptions.developerOptionsScreen
import com.tamin.taminhamrah.feature.girlSurvivor.girlSurvivorScreen
import com.tamin.taminhamrah.feature.healthProfile.healthProfileScreen
import com.tamin.taminhamrah.feature.healthProfile.navigateToHealthProfile
import com.tamin.taminhamrah.feature.history.historyJobInfoScreen
import com.tamin.taminhamrah.feature.history.historyScreen
import com.tamin.taminhamrah.feature.historyobjection.historyObjectionScreen
import com.tamin.taminhamrah.feature.historyobjection.historyObjectionStepperScreen
import com.tamin.taminhamrah.feature.inquiryEducation.inquiryEducationScreen
import com.tamin.taminhamrah.feature.myinbox.MyInboxRoute
import com.tamin.taminhamrah.feature.myinbox.myInboxScreen
import com.tamin.taminhamrah.feature.orotezprotez.orotezProtezScreen
import com.tamin.taminhamrah.feature.pensionInquiry.calculatePensionScreen
import com.tamin.taminhamrah.feature.pensionInquiry.deservedTreatmentScreen
import com.tamin.taminhamrah.feature.pensionInquiry.disabilityPensionScreen
import com.tamin.taminhamrah.feature.pensionInquiry.edictScreen
import com.tamin.taminhamrah.feature.pensionInquiry.issuanceCertificateScreen
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToDeservedTreatment
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToDisabilityPension
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPrescription
import com.tamin.taminhamrah.feature.pensionInquiry.payrollScreen
import com.tamin.taminhamrah.feature.pensionInquiry.prescriptionScreen
import com.tamin.taminhamrah.feature.pensionStatusInquiry.pensionStatusInquiryGraph
import com.tamin.taminhamrah.feature.pensionSurvivor.navigateToPensionSurvivor
import com.tamin.taminhamrah.feature.pensionSurvivor.pensionSurvivorScreen
import com.tamin.taminhamrah.feature.pregnancyPay.pregnancyPayScreen
import com.tamin.taminhamrah.feature.profile.ProfileRoute
import com.tamin.taminhamrah.feature.profile.profileGraph
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.requestPaymentForIllDaysScreen
import com.tamin.taminhamrah.feature.security.SecurityRoute
import com.tamin.taminhamrah.feature.security.securityScreen
import com.tamin.taminhamrah.feature.settings.SettingsRoute
import com.tamin.taminhamrah.feature.settings.settingsScreen
import com.tamin.taminhamrah.feature.contracts.contractFlowScreen
import com.tamin.taminhamrah.feature.taminServices.TaminServicesRoute
import com.tamin.taminhamrah.feature.taminServices.inspectionScreen
import com.tamin.taminhamrah.feature.taminServices.occurrenceScreen
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServicesScreen
import com.tamin.taminhamrah.feature.taminServices.sendInsuranceHistoryToInstitutionsScreen
import com.tamin.taminhamrah.feature.taminServices.taminServicesScreen
import com.tamin.taminhamrah.feature.treatment.TreatmentRoute
import com.tamin.taminhamrah.feature.treatment.treatmentGraph
import com.tamin.taminhamrah.feature.userRequest.UserRequestRoute
import com.tamin.taminhamrah.feature.userRequest.userRequestGraph
import com.tamin.taminhamrah.feature.workshops.completeEmployerInfoScreen
import com.tamin.taminhamrah.feature.workshops.navigateToWorkshops
import com.tamin.taminhamrah.feature.workshops.workshopsScreen
import com.tamin.taminhamrah.mapper.campaign.toPresentation
import com.tamin.taminhamrah.mapper.home.featuredServices
import com.tamin.taminhamrah.mapper.home.toQuickAccessSections
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.model.home.HomeServiceSection
import com.tamin.taminhamrah.openUrl
import com.tamin.taminhamrah.ui.blur.AppBarScrim
import com.tamin.taminhamrah.ui.blur.FloatingGlassNavigationBar
import com.tamin.taminhamrah.ui.blur.NavigationBarItemContent
import com.tamin.taminhamrah.ui.blur.TopBarScrim
import com.tamin.taminhamrah.ui.blur.safeHazeSource
import com.tamin.taminhamrah.ui.components.CampaignCarousel
import com.tamin.taminhamrah.ui.components.HeaderSuggestionChip
import com.tamin.taminhamrah.ui.components.HomeAgentAskBar
import com.tamin.taminhamrah.ui.components.HomeFeaturedSection
import com.tamin.taminhamrah.ui.components.HomeHeader
import com.tamin.taminhamrah.ui.components.HomeQuickAccessSection
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.ui.contract.CustomNavigationBarItem
import com.tamin.taminhamrah.ui.home.HomeViewModel
import com.tamin.taminhamrah.ui.home.contract.HomeEvent
import com.tamin.taminhamrah.ui.home.contract.HomeIntent
import com.tamin.taminhamrah.ui.home.contract.HomeUiState
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.AppConfig
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.error_load_menu_failed
import taminx.core.core_ui.home_ask_agent_cd
import taminx.core.core_ui.home_ask_agent_hint
import taminx.core.core_ui.home_suggestion_booklet
import taminx.core.core_ui.home_suggestion_history
import taminx.core.core_ui.home_suggestion_retirement
import taminx.core.core_ui.ic_home_menu
import taminx.core.core_ui.ic_profile_menu
import taminx.core.core_ui.ic_services_menu
import taminx.core.core_ui.ic_treatment_menu
import taminx.core.core_ui.login_required_desc
import taminx.core.core_ui.login_to_tamin_man
import taminx.core.core_ui.please_login_to_your_account
import taminx.core.core_ui.retry
import taminx.core.core_ui.tab_agent
import taminx.core.core_ui.tab_home
import taminx.core.core_ui.tab_profile
import taminx.core.core_ui.tab_services
import taminx.core.core_ui.tab_treatment

private enum class BottomTab { HOME, SERVICES, TREATMENT, PROFILE, OTHER }

private fun NavDestination?.toBottomTab(): BottomTab = when {
    this == null -> BottomTab.OTHER
    hasRoute<Route.Home>() -> BottomTab.HOME
    hasRoute<TaminServicesRoute>() -> BottomTab.SERVICES
    hasRoute<TreatmentRoute.Main>() -> BottomTab.TREATMENT
    hasRoute<ProfileRoute.Main>() -> BottomTab.PROFILE
    else -> BottomTab.OTHER
}

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

    // بررسی Feature Flag سراسری Agent برای کنترل نمایش FAB
    val featureManager: FeatureManager = koinInject()
    val isAgentEnabled by featureManager
        .getFeatureStatus(FeatureFlag.AGENT)
        .map { it is FeatureStatus.Enabled }
        .collectAsState(initial = false)
    val currentTab = currentDestination.toBottomTab()
    val isBottomBarVisible = currentTab != BottomTab.OTHER
    val agentLabel = stringResource(Res.string.tab_agent)

    val navigationItems = listOf(
        NavigationTab(
            title = stringResource(Res.string.tab_home),
            isSelected = currentTab == BottomTab.HOME,
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
            isSelected = currentTab == BottomTab.SERVICES,
            icon = Res.drawable.ic_services_menu,
            onClick = {
                navController.navigate(TaminServicesRoute) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        ),
        NavigationTab(
            title = stringResource(Res.string.tab_treatment),
            isSelected = currentTab == BottomTab.TREATMENT,
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
            isSelected = currentTab == BottomTab.PROFILE,
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


    val hazeState = remember { HazeState(initialBlurEnabled = true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = fadeIn(
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 300),
                ),
                exit = fadeOut(
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 300),
                ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = AppBarScrim.bottomGradient)
                ) {
                    val selectedIndex = remember(currentTab) {
                        navigationItems.indexOfFirst { it.isSelected }.coerceAtLeast(0)
                    }
                    FloatingGlassNavigationBar(
                        hazeState = hazeState,
                        selectedIndex = selectedIndex,
                        itemCount = navigationItems.size,
                        isBlurEnabled = isBottomBarVisible,
                        trailingButton = if (isAgentEnabled) {
                            {
                                AgentOrbButton(
                                    onClick = { navController.navigateToAgent() },
                                    contentDescription = agentLabel,
                                )
                            }
                        } else null,
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
                .consumeWindowInsets(paddingValues)
                .safeHazeSource(state = hazeState, isEnabled = isBottomBarVisible)
        ) {
            NavHost(
                navController = navController,
                startDestination = Route.Home
            ) {
                composableWithFadeTransitions<Route.Home> {
                    HomeScreen(
                        onNavigateToService = { flag -> navController.navigateToFeature(flag) },
                        onNavigateToWeb = { url -> openUrl(url) },
                        onShowMessage = { message ->
                            snackbarScope.launch { snackbarHostState.showSnackbar(message) }
                        },
                        onNavigateToAllServices = {
                            navController.navigate(TaminServicesRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToAgent = { navController.navigateToAgent() },
                    )
                }

                treatmentGraph(
                    navController = navController,
                    onBack = { navController.popBackStack() },
                    onNavigateToHealthProfile = { nationalCode ->
                        navController.navigateToHealthProfile(nationalCode)
                    },
                )

                healthProfileScreen(
                    onBack = { navController.popBackStack() }
                )

                profileGraph(
                    navController = navController,
                    onNavigateToIdentity = { userId ->
                        navController.navigate(ProfileRoute.Identity(userId))
                    },
                    onNavigateToElectronicFile = {
                        navController.navigate(ProfileRoute.ElectronicFile)
                    },
                    onNavigateToChangeMobile = {
                        navController.navigateToChangeMobile()
                    },
                    onNavigateToMyInbox = {
                        navController.navigate(MyInboxRoute)
                    },
                    onNavigateToSecurity = {
                        navController.navigate(SecurityRoute)
                    },
                    onNavigateToDeveloperOptions = {
                        navController.navigate(DeveloperOptionsRoute)
                    },
                    onNavigateToAddDependent = {
                        navController.navigate(AddDependentRoute)
                    },
                    onNavigateToSettings = {
                        navController.navigate(SettingsRoute)
                    },
                    onNavigateToUserRequests = {
                        navController.navigate(UserRequestRoute.List)
                    },

                    onOpenUrl = { url -> openUrl(url) },
                    onBack = { navController.popBackStack() }
                )

                addDependentGraph(
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )

                changeMobileScreen(
                    onBack = { navController.popBackStack() },
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

                taminServicesScreen(
                    onNavigateToService = { flag -> navController.navigateToFeature(flag) },
                    onOpenUrl = { url -> openUrl(url) },
                    onBackClicked = { navController.popBackStack() }
                )

                sendInsuranceHistoryToInstitutionsScreen(
                    onBack = { navController.popBackStack() },
                    onDone = { navController.popBackStack() }
                )

                inspectionScreen(
                    onBack = { navController.popBackStack() }
                )

                employerOnlineServicesScreen(
                    onBack = { navController.popBackStack() }
                )

                pensionStatusInquiryGraph(
                    onBack = { navController.popBackStack() }
                )
                occurrenceScreen(
                    onBack = { navController.popBackStack() },
                    onDone = { navController.popBackStack() },
                )

                calculatePensionScreen(onBack = { navController.popBackStack() })
                prescriptionScreen(onBack = { navController.popBackStack() })
                deservedTreatmentScreen(onBack = { navController.popBackStack() })
                payrollScreen(onBack = { navController.popBackStack() })
                edictScreen(onBack = { navController.popBackStack() })
                issuanceCertificateScreen(
                    onBack = { navController.popBackStack() },
                    onGoHome = {
                        navController.navigate(Route.Home) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
                deferredInstallmentScreen(onBack = { navController.popBackStack() })
                girlSurvivorScreen(onBack = { navController.popBackStack() })
                inquiryEducationScreen(onBack = { navController.popBackStack() })
                pensionSurvivorScreen(
                    navController = navController,
                    onBack = { navController.popBackStack() })
                disabilityPensionScreen(onBack = { navController.popBackStack() })

                historyScreen()
                historyJobInfoScreen(onBack = { navController.popBackStack() })

                contractsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToService = { flag ->
                        navController.navigateToFeature(flag)
                    },
                    onOpenUrl = { url -> openUrl(url) }
                )

                workshopsScreen(navController, onOpenUrl = { url -> openUrl(url) })
                completeEmployerInfoScreen(navController)

                myInboxScreen(onNavigateBack = { navController.popBackStack() })

                settingsScreen(onNavigateBack = { navController.popBackStack() })

                userRequestGraph(navController = navController)

                contractFlowScreen(onBack = { navController.popBackStack() })

                // Maps the assistant's destination ids to real routes. Ids come from
                // AgentDestination; anything unmapped is ignored rather than crashing.
                agentScreen(
                    onNavigateToDestination = { destination ->
                        when (destination) {
                            AgentDestination.DISABILITY_PENSION -> navController.navigateToDisabilityPension()
                            AgentDestination.DEFERRED_INSTALLMENT -> navController.navigateToDeferredInstallment()
                            AgentDestination.CONTRACTS -> navController.navigateToContracts()
                            AgentDestination.WORKSHOPS -> navController.navigateToWorkshops()
                            AgentDestination.PRESCRIPTION -> navController.navigateToPrescription()
                            AgentDestination.DESERVED_TREATMENT -> navController.navigateToDeservedTreatment()
                            AgentDestination.PENSION_SURVIVOR -> navController.navigateToPensionSurvivor()
                            // Remaining AgentDestination ids have no screen in this app yet.
                            // Until they do, the assistant must not offer a button for them —
                            // see DeepLinkAgentService.
                            else -> Unit
                        }
                    }
                )

                securityScreen(onNavigateBack = { navController.popBackStack() })

                if (AppConfig.isDebug) {
                    developerOptionsScreen(onNavigateBack = { navController.popBackStack() })
                }

                orotezProtezScreen(onBack = { navController.popBackStack() })

                historyObjectionScreen(
                    navController = navController,
                    onBack = { navController.popBackStack() })

                historyObjectionStepperScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateHome = { navController.popBackStack(Route.Home, inclusive = false) },
                )

                requestPaymentForIllDaysScreen(onBack = { navController.popBackStack() })

                pregnancyPayScreen(onBack = { navController.popBackStack() })

                healthProfileScreen(onBack = { navController.popBackStack() })
            }

            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = fadeIn(
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
                ),
                exit = fadeOut(
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
                ),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                TopBarScrim()
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

/** What the placeholder home column insets its content by; the carousel needs to know it. */
private val HomeContentPadding = 16.dp

/**
 * How far the AI ask-bar drops below the header's bottom edge — half its own height
 * ([com.tamin.taminhamrah.ui.theme.ButtonDimens.height] / 2), matching the profile screen's status
 * card. This is the trailing spacer under the header, so the bar (bottom-aligned over it) ends up
 * straddling the gradient edge. The header carries enough bottom padding that its chips clear it.
 */
private val HomeAskBarOverlap = 28.dp

/**
 * Measures the content [inset] wider than the column allows, so a full-bleed child can reach the
 * screen edge from inside a padded, center-aligned column. Placement is symmetric, which is what
 * cancels the padding — the parent's own width is fixed, so nothing else moves.
 *
 * Local to this screen on purpose: it exists only because the placeholder home column pads all of
 * its children, and it goes away with the placeholder.
 */
private fun Modifier.ignoreHorizontalPadding(inset: Dp) = layout { measurable, constraints ->
    val width = constraints.maxWidth + inset.roundToPx() * 2
    val placeable = measurable.measure(
        constraints.copy(minWidth = width, maxWidth = width)
    )
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}

@Composable
fun HomeScreen(
    onNavigateToService: (FeatureFlag) -> Unit,
    onNavigateToWeb: (String) -> Unit,
    // No default: a disabled feature says why through this, and a caller that omitted it used to
    // drop the message silently — the tap then did nothing at all.
    onShowMessage: (String) -> Unit,
    onNavigateToAllServices: () -> Unit,
    onNavigateToAgent: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.NavigateToService -> onNavigateToService(event.flag)
                is HomeEvent.NavigateToWeb -> onNavigateToWeb(event.url)
                is HomeEvent.ShowMessage -> onShowMessage(event.message)
            }
        }
    }

    HomeScreenContent(
        uiState = uiState,
        onNavigateToAgent = onNavigateToAgent,
        onNavigateToAllServices = onNavigateToAllServices,
        onCampaignClick = { viewModel.sendIntent(HomeIntent.OnCampaignClick(it)) },
        onSectionSelected = { viewModel.sendIntent(HomeIntent.OnSectionSelected(it)) },
        onServiceClick = { viewModel.sendIntent(HomeIntent.OnServiceClick(it)) },
        onRetry = { viewModel.sendIntent(HomeIntent.LoadMenu) },
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToAgent: () -> Unit,
    onNavigateToAllServices: () -> Unit,
    onCampaignClick: (FeatureFlag) -> Unit,
    onSectionSelected: (HomeServiceSection) -> Unit,
    onServiceClick: (MainServiceDN) -> Unit,
    onRetry: () -> Unit,
) {
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
                .padding(horizontal = HomeContentPadding)
                // Bottom padding so last item scrolls fully above the floating blur bar
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header + AI ask-bar. The bar straddles the header's bottom edge the way the profile
            // screen's status card does: the header sits in a Column with a trailing spacer that
            // reserves the bar's lower half, and the bar is bottom-aligned in the Box over it.
            Box(modifier = Modifier.ignoreHorizontalPadding(HomeContentPadding)) {
                Column {
                    HomeHeader(
                        fullName = uiState.identityFullName,
                        hasDarmanCoverage = uiState.hasDarmanCoverage,
                        hasActiveRelation = uiState.hasActiveRelation,
                    )
                    if (uiState.isAgentEnabled) {
                        Spacer(modifier = Modifier.height(HomeAskBarOverlap))
                    }
                }
                if (uiState.isAgentEnabled) {
                    HomeAgentAskBar(
                        hint = stringResource(Res.string.home_ask_agent_hint),
                        contentDescription = stringResource(Res.string.home_ask_agent_cd),
                        onClick = onNavigateToAgent,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = HomeContentPadding),
                    )
                }
            }

            if (uiState.isAgentEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(top = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    listOf(
                        stringResource(Res.string.home_suggestion_retirement),
                        stringResource(Res.string.home_suggestion_history),
                        stringResource(Res.string.home_suggestion_booklet),
                    ).forEach { suggestion ->
                        HeaderSuggestionChip(text = suggestion, onClick = onNavigateToAgent)
                    }
                }
            }

            // Full-bleed on purpose. A pager clips along its scroll axis, so leaving it inside this
            // column's 16dp inset would cut the peeking neighbor down from 34 to 18 and leave the
            // cards' merged shadow with a hard vertical edge 16dp in from the screen.
            CampaignCarousel(
                campaigns = uiState.campaigns.toPresentation(),
                onCampaignClick = onCampaignClick,
                modifier = Modifier
                    .ignoreHorizontalPadding(HomeContentPadding)
                    .padding(top = Spacing.xlg),
            )

            Spacer(modifier = Modifier.height(8.dp))

            HomeQuickAccessSection(
                sections = uiState.menuItems.toQuickAccessSections(),
                selectedSection = uiState.selectedSection,
                onSectionSelected = onSectionSelected,
                onServiceClick = onServiceClick,
                onSeeAll = onNavigateToAllServices,
                modifier = Modifier.padding(top = Spacing.lg),
            )

            HomeFeaturedSection(
                services = uiState.menuItems.featuredServices(),
                onServiceClick = onServiceClick,
                modifier = Modifier.padding(top = Spacing.md),
            )


            if (uiState.menuItems.isEmpty() && !uiState.isLoading) {
                Text(
                    stringResource(Res.string.error_load_menu_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 32.dp)
                )
                Button(
                    onClick = onRetry,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(stringResource(Res.string.retry))
                }
            }
        }
    }
}

private fun previewHomeUiState() = HomeUiState(
    isLoading = false,
    menuItems = (HomeServiceSection.FREQUENT.members + HomeServiceSection.FEATURED.members)
        .distinct()
        .map { MainServiceDN(id = it.id, name = it.name, status = MenuServiceStatusDN.ACTIVE) },
    identityFullName = "سنا حقیقی",
    hasDarmanCoverage = true,
    hasActiveRelation = true,
    isAgentEnabled = true,
)

@PreviewRtlTheme
@Composable
private fun HomeScreenPreview() {
    PreviewRtlThemeContent {
        HomeScreenContent(
            uiState = previewHomeUiState(),
            onNavigateToAgent = {},
            onNavigateToAllServices = {},
            onCampaignClick = {},
            onSectionSelected = {},
            onServiceClick = {},
            onRetry = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HomeScreenPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        HomeScreenContent(
            uiState = previewHomeUiState(),
            onNavigateToAgent = {},
            onNavigateToAllServices = {},
            onCampaignClick = {},
            onSectionSelected = {},
            onServiceClick = {},
            onRetry = {},
        )
    }
}
