package com.tamin.taminhamrah.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.addDependent.AddDependentRoute
import com.tamin.taminhamrah.feature.addDependent.addDependentGraph
import com.tamin.taminhamrah.deeplink.DeepLinkDispatcher
import com.tamin.taminhamrah.deeplink.DeepLinkKey
import com.tamin.taminhamrah.useCases.agent.ObserveAgentAvailabilityUseCase
import com.tamin.taminhamrah.deeplink.DeepLinkResolution
import com.tamin.taminhamrah.deeplink.DeepLinkSource
import com.tamin.taminhamrah.deeplink.ResolveDeepLinkUseCase
import com.tamin.taminhamrah.ui.deeplink.DeepLinkHandler
import com.tamin.taminhamrah.ui.deeplink.LocalDeepLinkHandler
import com.tamin.taminhamrah.feature.agent.agentScreen
import com.tamin.taminhamrah.feature.agent.navigateToAgent
import com.tamin.taminhamrah.feature.cartable.CartableRoute
import com.tamin.taminhamrah.feature.cartable.cartableGraph
import com.tamin.taminhamrah.feature.changemobile.changeMobileScreen
import com.tamin.taminhamrah.feature.changemobile.navigateToChangeMobile
import com.tamin.taminhamrah.feature.contractaffair.CONTRACT_AFFAIRS_REFRESH_KEY
import com.tamin.taminhamrah.feature.contractaffair.ContractPremiumPaymentRoute
import com.tamin.taminhamrah.feature.contractaffair.contractAffairsScreen
import com.tamin.taminhamrah.feature.contractaffair.contractPaymentCalcDetailScreen
import com.tamin.taminhamrah.feature.contractaffair.contractPaymentHistoryScreen
import com.tamin.taminhamrah.feature.contractaffair.contractPremiumPaymentScreen
import com.tamin.taminhamrah.feature.contractaffair.navigateToContractPaymentCalcDetail
import com.tamin.taminhamrah.feature.contractaffair.navigateToContractPaymentHistory
import com.tamin.taminhamrah.feature.contractaffair.navigateToContractPremiumPayment
import com.tamin.taminhamrah.feature.contracts.contractFlowScreen
import com.tamin.taminhamrah.feature.contracts.contractsScreen
import com.tamin.taminhamrah.feature.contracts.flow.resolveContractTypeForEdit
import com.tamin.taminhamrah.feature.contracts.navigateToContractFlow
import com.tamin.taminhamrah.feature.contracts.navigateToContracts
import com.tamin.taminhamrah.feature.deferredInstallment.deferredInstallmentScreen
import com.tamin.taminhamrah.feature.deferredInstallment.navigateToDeferredInstallment
import com.tamin.taminhamrah.feature.developerOptions.DebugLoginRoute
import com.tamin.taminhamrah.feature.developerOptions.DeveloperOptionsRoute
import com.tamin.taminhamrah.feature.developerOptions.TokenManagerRoute
import com.tamin.taminhamrah.feature.developerOptions.debugLoginScreen
import com.tamin.taminhamrah.feature.developerOptions.developerOptionsScreen
import com.tamin.taminhamrah.feature.developerOptions.tokenManagerScreen
import com.tamin.taminhamrah.feature.fractionContract.fractionContractScreen
import com.tamin.taminhamrah.feature.girlSurvivor.girlSurvivorScreen
import com.tamin.taminhamrah.feature.healthProfile.healthProfileScreen
import com.tamin.taminhamrah.feature.healthProfile.navigateToHealthProfile
import com.tamin.taminhamrah.feature.history.historyJobInfoScreen
import com.tamin.taminhamrah.feature.history.historyScreen
import com.tamin.taminhamrah.feature.historyobjection.historyObjectionScreen
import com.tamin.taminhamrah.feature.historyobjection.historyObjectionStepperScreen
import com.tamin.taminhamrah.feature.inquiryEducation.inquiryEducationScreen
import com.tamin.taminhamrah.feature.objectionInsurance.objectionInsuranceScreen
import com.tamin.taminhamrah.feature.weddingPresent.navigateToWeddingPresentCalculate
import com.tamin.taminhamrah.feature.weddingPresent.weddingPresentCalculateScreen
import com.tamin.taminhamrah.feature.weddingPresent.weddingPresentScreen
import com.tamin.taminhamrah.feature.calculateWagePension.calculateWagePensionScreen
import com.tamin.taminhamrah.feature.myinbox.MyInboxRoute
import com.tamin.taminhamrah.feature.myinbox.myInboxScreen
import com.tamin.taminhamrah.feature.orotezprotez.orotezProtezScreen
import com.tamin.taminhamrah.feature.payment.PaymentRoute
import com.tamin.taminhamrah.feature.payment.navigateToPayment
import com.tamin.taminhamrah.feature.payment.paymentGraph
import com.tamin.taminhamrah.feature.payment.paymentSandboxScreen
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
import com.tamin.taminhamrah.feature.contractaffair.CONTRACT_AFFAIRS_REFRESH_KEY
import com.tamin.taminhamrah.feature.contractaffair.contractAffairsScreen
import com.tamin.taminhamrah.feature.contractaffair.contractPaymentHistoryScreen
import com.tamin.taminhamrah.feature.contractaffair.contractPaymentCalcDetailScreen
import com.tamin.taminhamrah.feature.contractaffair.contractPremiumPaymentScreen
import com.tamin.taminhamrah.feature.contractaffair.navigateToContractPaymentCalcDetail
import com.tamin.taminhamrah.feature.contractaffair.navigateToContractPaymentHistory
import com.tamin.taminhamrah.feature.contractaffair.navigateToContractPremiumPayment
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.requestPaymentForIllDaysScreen
import com.tamin.taminhamrah.feature.retirementPension.retirementPensionScreen
import com.tamin.taminhamrah.feature.security.SecurityRoute
import com.tamin.taminhamrah.feature.security.securityScreen
import com.tamin.taminhamrah.feature.settings.SettingsRoute
import com.tamin.taminhamrah.feature.settings.settingsScreen
import com.tamin.taminhamrah.feature.stories.navigateToStoryViewer
import com.tamin.taminhamrah.feature.stories.storyViewerScreen
import com.tamin.taminhamrah.feature.taminServices.TaminServicesRoute
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServicesScreen
import com.tamin.taminhamrah.feature.taminServices.inspectionScreen
import com.tamin.taminhamrah.feature.taminServices.occurrenceScreen
import com.tamin.taminhamrah.feature.taminServices.sendInsuranceHistoryToInstitutionsScreen
import com.tamin.taminhamrah.feature.taminServices.taminServicesScreen
import com.tamin.taminhamrah.feature.taminServices.workersPaymentInfoScreen
import com.tamin.taminhamrah.feature.treatment.TreatmentRoute
import com.tamin.taminhamrah.feature.treatment.treatmentGraph
import com.tamin.taminhamrah.feature.userRequest.UserRequestRoute
import com.tamin.taminhamrah.feature.userRequest.navigateToUserRequestDetail
import com.tamin.taminhamrah.feature.userRequest.userRequestGraph
import com.tamin.taminhamrah.feature.weddingPresent.navigateToWeddingPresentCalculate
import com.tamin.taminhamrah.feature.weddingPresent.weddingPresentCalculateScreen
import com.tamin.taminhamrah.feature.weddingPresent.weddingPresentScreen
import com.tamin.taminhamrah.feature.workshops.completeEmployerInfoScreen
import com.tamin.taminhamrah.feature.workshops.debtObjectionStatusScreen
import com.tamin.taminhamrah.feature.workshops.navigateToWorkshops
import com.tamin.taminhamrah.feature.workshops.workshopsScreen
import com.tamin.taminhamrah.feature.userRequest.userRequestGraph
import com.tamin.taminhamrah.mapper.campaign.toPresentation
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.openUrl
import com.tamin.taminhamrah.ui.blur.AppBarScrim
import com.tamin.taminhamrah.ui.blur.FloatingGlassNavigationBar
import com.tamin.taminhamrah.ui.blur.NavigationBarItemContent
import com.tamin.taminhamrah.ui.blur.TopBarScrim
import com.tamin.taminhamrah.ui.blur.safeHazeSource
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.ui.contract.CustomNavigationBarItem
import com.tamin.taminhamrah.ui.home.HomeScreen
import com.tamin.taminhamrah.util.AppConfig
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_home_menu
import taminx.core.core_ui.ic_profile_menu
import taminx.core.core_ui.ic_services_menu
import taminx.core.core_ui.ic_treatment_menu
import taminx.core.core_ui.invalid_deep_link
import taminx.core.core_ui.login_required_desc
import taminx.core.core_ui.login_to_tamin_man
import taminx.core.core_ui.please_login_to_your_account
import taminx.core.core_ui.tab_agent
import taminx.core.core_ui.deep_link_feature_unavailable
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

/** The orb opens the assistant through the deep link gate, so the flag is re-checked on tap. */
private val AGENT_DEEP_LINK = "@" + DeepLinkKey.AGENT.key

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

    // The assistant's entry point needs both the AGENT menu flag and the server's chat permission.
    val observeAgentAvailability: ObserveAgentAvailabilityUseCase = koinInject()
    val isAgentEnabled by remember(observeAgentAvailability) { observeAgentAvailability() }
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

    // Every link — from the OS, a story or the assistant — is resolved here, through the one gate
    // that reads the feature flag. [beforeOpen] runs only once the link is known to open something,
    // so a blocked link leaves the caller's screen where it was.
    val deepLinkDispatcher: DeepLinkDispatcher = koinInject()
    val resolveDeepLink: ResolveDeepLinkUseCase = koinInject()
    val deepLinkHandler = remember(deepLinkDispatcher) {
        DeepLinkHandler { uri, source, onOpened -> deepLinkDispatcher.submit(uri, source, onOpened) }
    }
    // A menu tap on a service with no screen yet says so instead of doing nothing.
    val openService: (FeatureFlag) -> Unit = { flag ->
        if (!navController.navigateToFeature(flag)) {
            snackbarScope.launch { snackbarHostState.showSnackbar(getString(Res.string.deep_link_feature_unavailable)) }
        }
    }
    val openDeepLink: suspend (String, DeepLinkSource, () -> Unit) -> Unit = { uri, source, beforeOpen ->
        when (val resolution = resolveDeepLink(uri, source)) {
            is DeepLinkResolution.OpenFeature -> {
                resolution.notice?.let { snackbarHostState.showSnackbar(it) }
                if (!navController.navigateToDeepLink(resolution.key, resolution.args, beforeOpen)) {
                    snackbarHostState.showSnackbar(getString(Res.string.deep_link_feature_unavailable))
                }
            }
            is DeepLinkResolution.OpenWeb -> {
                beforeOpen()
                openUrl(resolution.url)
            }
            is DeepLinkResolution.Blocked -> snackbarHostState.showSnackbar(
                resolution.message ?: getString(Res.string.deep_link_feature_unavailable)
            )
            is DeepLinkResolution.SendPrompt,
            DeepLinkResolution.Invalid -> snackbarHostState.showSnackbar(getString(Res.string.invalid_deep_link))
        }
    }
    // Links wait in the dispatcher until there is a signed-in session to open them in.
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            deepLinkDispatcher.links.collect { link -> openDeepLink(link.uri, link.source, link.onOpened) }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = fadeIn(
                    animationSpec = tween(durationMillis = 300),
                ),
                exit = fadeOut(
                    animationSpec = tween(durationMillis = 300),
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
                                    onClick = { deepLinkHandler.open(AGENT_DEEP_LINK, DeepLinkSource.APP_CONTENT) },
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
      CompositionLocalProvider(LocalDeepLinkHandler provides deepLinkHandler) {
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
                        onNavigateToService = openService,
                        onNavigateToWeb = { url -> openUrl(url) },
                        onShowMessage = { message ->
                            snackbarScope.launch { snackbarHostState.showSnackbar(message) }
                        },
                        onOpenStory = { index -> navController.navigateToStoryViewer(index) },
                        onNavigateToAllServices = {
                            navController.navigate(TaminServicesRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToAgent = { navController.navigateToAgent() },
                        onNavigateToUserRequests = { refCode ->
                            navController.navigate(UserRequestRoute.List(refCode = refCode))
                        },
                        onNavigateToUserRequestDetail = { requestId, refCode, requestTypeId, title, referenceId ->
                            navController.navigateToUserRequestDetail(
                                requestId = requestId,
                                refCode = refCode,
                                requestTypeId = requestTypeId,
                                title = title,
                                referenceId = referenceId,
                            )
                        },
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
                        navController.navigate(UserRequestRoute.List())
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
                    onNavigateToService = openService,
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
                workersPaymentInfoScreen(
                    onBack = { navController.popBackStack() },
                    onOpenUrl = { url -> openUrl(url) },
                    onNavigateToPayment = { request ->
                        navController.navigateToPayment(request)
                    },
                )

                calculateWagePensionScreen(onBack = { navController.popBackStack() })
                retirementPensionScreen(onBack = { navController.popBackStack() })
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
                objectionInsuranceScreen(onBack = { navController.popBackStack() })
                fractionContractScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToPremiumPayment = { contractNumber, premiumTypeCode, insuranceType ->
                        navController.navigateToContractPremiumPayment(
                            contractNumber,
                            premiumTypeCode,
                            insuranceType,
                        )
                    },
                )
                weddingPresentScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToCalculate = { navController.navigateToWeddingPresentCalculate() },
                )
                weddingPresentCalculateScreen(onBack = { navController.popBackStack() })

                // The shared payment flow. Any feature that has been handed a gateway ticket
                // enters it with navController.navigateToPayment(request); finishing pops back to
                // whichever screen started the payment.
                paymentGraph(
                    navController = navController,
                    onFinished = { navController.popBackStack() },
                )

                storyViewerScreen(onClose = { navController.popBackStack() })
                pensionSurvivorScreen(
                    navController = navController,
                    onBack = { navController.popBackStack() })
                disabilityPensionScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToAddDependent = { navController.navigate(AddDependentRoute) },
                )

                historyScreen(
                    navController = navController,
                    onBack = { navController.popBackStack() })
                historyJobInfoScreen(onBack = { navController.popBackStack() })

                contractsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToService = openService,
                    onOpenUrl = { url -> openUrl(url) }
                )

                contractAffairsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToService = openService,
                    onOpenUrl =  { url -> openUrl(url) },
                    onNavigateToPaymentHistory = { contractNumber, insuranceType ->
                        navController.navigateToContractPaymentHistory(
                            contractNumber,
                            insuranceType
                        )
                    },
                    onNavigateToPremiumPayment = { contractNumber, premiumTypeCode, insuranceType ->
                        navController.navigateToContractPremiumPayment(
                            contractNumber,
                            premiumTypeCode,
                            insuranceType,
                        )
                    },
                    onNavigateToEditContract = { premiumTypeCode, freeJobCode, contractNumber ->
                        val type = resolveContractTypeForEdit(premiumTypeCode, freeJobCode)
                            ?: return@contractAffairsScreen
                        navController.navigateToContractFlow(
                            type = type,
                            editContractNumber = contractNumber,
                        )
                    },
                )

                contractPaymentHistoryScreen(onBack = { navController.popBackStack() })

                contractPremiumPaymentScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToPaymentDetails = { premiumTypeCode, startDate, endDate ->
                        navController.navigateToContractPaymentCalcDetail(
                            premiumTypeCode,
                            startDate,
                            endDate,
                        )
                    },
                    onNavigateToPayment = { request ->
                        navController.navigateToPayment(request) {
                            popUpTo<ContractPremiumPaymentRoute> {
                                inclusive = true
                            }
                        }
                    },
                )

                contractPaymentCalcDetailScreen(onBack = { navController.popBackStack() })

                workshopsScreen(
                    navController,
                    // The debt payment runs in the app's own payment flow, which owns the gateway
                    // address; the feature only hands over the ticket the service issued.
                    onStartPayment = { request -> navController.navigateToPayment(request) },
                )
                completeEmployerInfoScreen(navController)
                debtObjectionStatusScreen(navController)

                myInboxScreen(onNavigateBack = { navController.popBackStack() })

                settingsScreen(onNavigateBack = { navController.popBackStack() })

                userRequestGraph(navController = navController)

                contractFlowScreen(
                    onBack = { navController.popBackStack() },
                    onEditSuccess = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(CONTRACT_AFFAIRS_REFRESH_KEY, true)
                        navController.popBackStack()
                    },
                )

                agentScreen(onNavigateBack = { navController.popBackStack() })

                securityScreen(onNavigateBack = { navController.popBackStack() })

                if (AppConfig.isDebug) {
                    developerOptionsScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToDebugLogin = { navController.navigate(DebugLoginRoute) },
                        onNavigateToTokenManager = { navController.navigate(TokenManagerRoute) },
                        onStartTestPayment = { navController.navigate(PaymentRoute.Sandbox) }
                    )
                    paymentSandboxScreen(
                        navController = navController,
                        onNavigateBack = { navController.popBackStack() }
                    )
                    debugLoginScreen(onNavigateBack = { navController.popBackStack() })
                    tokenManagerScreen(onNavigateBack = { navController.popBackStack() })
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
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = fadeOut(
                    animationSpec = tween(durationMillis = 300)
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

