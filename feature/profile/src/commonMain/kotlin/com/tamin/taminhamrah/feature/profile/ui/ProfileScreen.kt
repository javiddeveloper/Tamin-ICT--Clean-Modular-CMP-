package com.tamin.taminhamrah.feature.profile.ui


import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileEvent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileIntent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState
import com.tamin.taminhamrah.feature.profile.ui.model.ProfileMenuItem
import com.tamin.taminhamrah.ui.LocalThemeRevealController
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemBadge
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.SectionHeaderTitle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.UserAvatar
import com.tamin.taminhamrah.ui.components.ValidationStatusCard
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.motion.ScrollMotionState
import com.tamin.taminhamrah.ui.motion.motionFade
import com.tamin.taminhamrah.ui.motion.motionParallax
import com.tamin.taminhamrah.ui.motion.motionScale
import com.tamin.taminhamrah.ui.motion.rememberMotionSnapFlingBehavior
import com.tamin.taminhamrah.ui.motion.rememberScrollMotionState
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import com.tamin.taminhamrah.util.toPersianDigits
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_communication
import taminx.core.core_ui.ic_exit
import taminx.core.core_ui.ic_history
import taminx.core.core_ui.ic_identity
import taminx.core.core_ui.ic_inbox
import taminx.core.core_ui.ic_mobile
import taminx.core.core_ui.ic_moon
import taminx.core.core_ui.ic_number
import taminx.core.core_ui.ic_person
import taminx.core.core_ui.ic_privacy
import taminx.core.core_ui.ic_request
import taminx.core.core_ui.ic_send
import taminx.core.core_ui.ic_setting
import taminx.core.core_ui.ic_share
import taminx.core.core_ui.ic_sun
import taminx.core.core_ui.ic_support
import taminx.core.core_ui.profile_active_relation
import taminx.core.core_ui.profile_bank_account
import taminx.core.core_ui.profile_cartable
import taminx.core.core_ui.profile_change_mobile
import taminx.core.core_ui.profile_contact_me
import taminx.core.core_ui.profile_dependents
import taminx.core.core_ui.profile_dependents_badge_test
import taminx.core.core_ui.profile_electronic_file
import taminx.core.core_ui.profile_identity_info
import taminx.core.core_ui.profile_logout
import taminx.core.core_ui.profile_personal_inbox
import taminx.core.core_ui.profile_personal_info
import taminx.core.core_ui.profile_requests
import taminx.core.core_ui.profile_security
import taminx.core.core_ui.profile_security_settings
import taminx.core.core_ui.profile_settings
import taminx.core.core_ui.profile_share
import taminx.core.core_ui.profile_support
import taminx.core.core_ui.profile_support_section
import taminx.core.core_ui.profile_title
import taminx.core.core_ui.profile_version_history
import androidx.compose.ui.unit.lerp as dpLerp

@Composable
fun ProfileScreen(
    userId: String? = null,
    viewModel: ProfileViewModel = koinViewModel(),
    onNavigateToIdentity: (String?) -> Unit = {},
    onNavigateToVersionHistory: () -> Unit = {},
    onNavigateToDependentsList: () -> Unit = {},
    onNavigateToRouteById: (Int) -> Unit = {},
    onOpenUrl: (String) -> Unit = {},
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val hazeState = remember { HazeState(initialBlurEnabled = true) }
    val lazyListState = rememberLazyListState()
    val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)
    var themeButtonCenter by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(userId) {
        viewModel.sendIntent(ProfileIntent.LoadProfile(userId))
    }

    LaunchedEffect(motionState, lazyListState) {
        motionState.observeLazyListState(lazyListState)
    }

    HandleProfileEvents(
        events = viewModel.events,
        onNavigateToIdentity = { onNavigateToIdentity(userId) },
        onNavigateToVersionHistory = onNavigateToVersionHistory,
        onNavigateToDependentsList = onNavigateToDependentsList,
        onNavigateToRouteById = onNavigateToRouteById,
        onOpenUrl = onOpenUrl,
        onBackClicked = onBackClicked
    )

    ProfileContent(
        state = uiState,
        hazeState = hazeState,
        lazyListState = lazyListState,
        motionState = motionState,
        themeButtonCenter = themeButtonCenter,
        onThemeButtonCenterChange = { themeButtonCenter = it },
        onIntent = viewModel::sendIntent,
    )
}

@Composable
fun HandleProfileEvents(
    events: Flow<ProfileEvent>,
    onNavigateToIdentity: () -> Unit,
    onNavigateToVersionHistory: () -> Unit,
    onNavigateToDependentsList: () -> Unit,
    onNavigateToRouteById: (Int) -> Unit,
    onOpenUrl: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    events.collectWithLifecycleAware {
        when (it) {
            ProfileEvent.NavigateBack -> {
                onBackClicked()
            }

            ProfileEvent.NavigateToSettings -> {
                // onNavigateToRouteById(100)
            }

            ProfileEvent.NavigateToIdentity -> {
                onNavigateToIdentity()
            }

            ProfileEvent.NavigateToVersionHistory -> {
                onNavigateToVersionHistory()
            }

            ProfileEvent.NavigateToDependentsList -> {
                onNavigateToDependentsList()
            }

            is ProfileEvent.OpenUrl -> {
                onOpenUrl(it.url)
            }

            is ProfileEvent.ShowToast -> {
                // Handle toast
            }
        }
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    state: ProfileUiState,
    hazeState: HazeState,
    lazyListState: LazyListState,
    motionState: ScrollMotionState,
    themeButtonCenter: Offset,
    onThemeButtonCenterChange: (Offset) -> Unit,
    onIntent: (ProfileIntent) -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val revealController = LocalThemeRevealController.current
    val isDark = taminColors == DarkTaminColors
    val decaySpec = rememberSplineBasedDecay<Float>()
    val topBarGradient = remember(isDark) { Brush.horizontalGradient(taminColors.profileGradientStops) }
    val defaultBorder = remember(taminColors) { BorderStroke(1.dp, taminColors.border) }
    val dangerBorder = remember(taminColors) { BorderStroke(1.dp, taminColors.dangerBorder) }
    val snapFlingBehavior = rememberMotionSnapFlingBehavior(
        lazyListState = lazyListState,
        motionState = motionState,
        decayAnimationSpec = decaySpec
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            val headerProgress = motionState.progress
            Box {
                Column(
                    modifier = Modifier.hazeSource(state = hazeState)
                ) {
                    TaminTopAppBar(
                        modifier = Modifier,
                        title = "",
                        centerTitle = false,
                        action = {
                            Box(
                                modifier = Modifier.onGloballyPositioned { coords ->
                                    val centerInRoot = coords.positionInRoot() +
                                        Offset(
                                            coords.size.width / 2f,
                                            coords.size.height / 2f
                                        )
                                    onThemeButtonCenterChange(centerInRoot)
                                }
                            ) {
                                TaminTopAppBarButton(
                                    icon = vectorResource(if (isDark) Res.drawable.ic_moon else Res.drawable.ic_sun),
                                    contentDescription = null,
                                    onClick = {
                                        if (revealController != null) {
                                            revealController.trigger(origin = themeButtonCenter) {
                                                onIntent(ProfileIntent.ToggleTheme(!isDark))
                                            }
                                        } else {
                                            onIntent(ProfileIntent.ToggleTheme(!isDark))
                                        }
                                    },
                                    bordered = true
                                )
                            }
                        },
                        background = topBarGradient,
                        bottomPadding = dpLerp(Spacing.xxxl, Spacing.sm, headerProgress)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(Res.string.profile_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(y = (-32).dp)
                                    .motionFade(motionState, startProgress = 0.2f, endProgress = 0.7f)
                                    .motionParallax(motionState, parallaxDistance = 20.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = dpLerp(Spacing.xl, Spacing.none, headerProgress))
                                    .padding(horizontal = Spacing.sm)
                                    .motionParallax(motionState, parallaxDistance = 45.dp)
                                    .motionScale(
                                        state = motionState,
                                        minScale = 0.8f,
                                        maxScale = 1f,
                                        transformOrigin = TransformOrigin(1f, 0.5f)
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                            ) {
                                UserAvatar(
                                    model = state.profileImage,
                                    isLoading = state.isProfileImageLoading
                                )
                                Column {
                                    Text(
                                        text = state.identityInfo?.fullName ?: "تست تست تست",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = taminColors.txtNameProfile
                                    )
                                    NumericText(
                                        text = state.identityInfo?.nationalId
                                            ?.toPersianDigits()
                                            .orEmpty(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = taminColors.txtNatProfile
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(dpLerp(Spacing.sm, Spacing.none, headerProgress)))
                    }
                    Spacer(modifier = Modifier.height(dpLerp(Spacing.xxxl, 35.dp, headerProgress)))
                }
                ValidationStatusCard(
                    hazeState = hazeState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = Spacing.lg),
                    title = "نام نویسی شده تست",
                    subtitle = "حساب شما تأیید و فعال است تست",
                    badgeText = "معتبر تست ",
                    isValid = true
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = lazyListState,
            flingBehavior = snapFlingBehavior,
            // Same rubber band as the treatment hub: what the list cannot scroll bends instead
            // of stopping dead at the edge.
            overscrollEffect = rememberJellyOverscroll(),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                top = Spacing.lg,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 80.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = stringResource(Res.string.profile_personal_info))
                    ListGroupView(
                        containerBorder = defaultBorder,
                        items = persistentListOf(
                            ListItemData(
                                title = stringResource(Res.string.profile_identity_info),
                                leadingIconPainter = painterResource(Res.drawable.ic_identity),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientPrimary
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.IDENTITY_INFO)) }
                            ),
                            ListItemData(
                                title = stringResource(Res.string.profile_dependents),
                                leadingIconPainter = painterResource(Res.drawable.ic_person),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientPrimary
                                ),
                                badge = ListItemBadge(
                                    text = stringResource(Res.string.profile_dependents_badge_test),
                                    backgroundColor = taminColors.blueBg,
                                    textColor = taminColors.blueText
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.LoadSubDominants) }
                            ),
                            ListItemData(
                                title = stringResource(Res.string.profile_active_relation),
                                leadingIconPainter = painterResource(Res.drawable.ic_communication),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientPrimary
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.ACTIVE_RELATION)) }
                            ),
                            ListItemData(
                                title = stringResource(Res.string.profile_electronic_file),
                                leadingIconPainter = painterResource(Res.drawable.ic_request),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientPrimary
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.ELECTRONIC_FILE)) }
                            ),
                            ListItemData(
                                title = stringResource(Res.string.profile_bank_account),
                                leadingIconPainter = painterResource(Res.drawable.ic_number),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientPrimary
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.LoadBankAccountList) }
                            ),
                            ListItemData(
                                title = stringResource(Res.string.profile_change_mobile),
                                leadingIconPainter = painterResource(Res.drawable.ic_mobile),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientPrimary
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.CHANGE_MOBILE)) }
                            )
                        )
                    )
                }
            }
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = stringResource(Res.string.profile_cartable))
                    ListGroupView(
                        containerBorder = defaultBorder,
                        items = persistentListOf(
                            ListItemData(
                                title = stringResource(Res.string.profile_requests),
                                leadingIconPainter = painterResource(Res.drawable.ic_request),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientSecondary
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.REQUESTS)) }
                            ),
                            ListItemData(
                                title = stringResource(Res.string.profile_personal_inbox),
                                leadingIconPainter = painterResource(Res.drawable.ic_inbox),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientSecondary
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.PERSONAL_INBOX)) }
                            )
                        )
                    )
                }
            }
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = stringResource(Res.string.profile_security_settings))
                    ListGroupView(
                        containerBorder = defaultBorder,
                        items = persistentListOf(
                            ListItemData(
                                title = stringResource(Res.string.profile_security),
                                leadingIconPainter = painterResource(Res.drawable.ic_privacy),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientNeutral
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.SECURITY)) }
                            ),
                            ListItemData(
                                title = stringResource(Res.string.profile_settings),
                                leadingIconPainter = painterResource(Res.drawable.ic_setting),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientNeutral
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.SETTINGS)) }
                            )
                        )
                    )
                }
            }
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = stringResource(Res.string.profile_support_section))
                    ListGroupView(
                        containerBorder = defaultBorder,
                        items = persistentListOf(
                            ListItemData(
                                title = stringResource(Res.string.profile_support),
                                leadingIconPainter = painterResource(Res.drawable.ic_support),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientSecondary
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.SUPPORT)) }
                            ),
                            ListItemData(
                                title = stringResource(Res.string.profile_contact_me),
                                leadingIconPainter = painterResource(Res.drawable.ic_send),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientSecondary
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.CONTACT_ME)) }
                            ),
                            ListItemData(
                                title = stringResource(Res.string.profile_share),
                                leadingIconPainter = painterResource(Res.drawable.ic_share),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientSecondary
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.SHARE)) }
                            ),
                            ListItemData(
                                title = stringResource(Res.string.profile_version_history),
                                leadingIconPainter = painterResource(Res.drawable.ic_history),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientSecondary
                                ),
                                showArrow = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.VERSION_HISTORY)) }
                            )
                        )
                    )
                }
            }
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    ListGroupView(
                        containerBorder = dangerBorder,
                        items = persistentListOf(
                            ListItemData(
                                title = stringResource(Res.string.profile_logout),
                                leadingIconPainter = painterResource(Res.drawable.ic_exit),
                                colors = ListItemColors(
                                    titleColor = taminColors.dangerText,
                                    leadingIconBackgroundColor = taminColors.dangerBg,
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientDanger
                                ),
                                showArrow = false,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.LOGOUT)) }
                            )
                        )
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ProfileScreenPreview() {
    PreviewRtlThemeContent {
        val hazeState = remember { HazeState(initialBlurEnabled = true) }
        val lazyListState = rememberLazyListState()
        val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)
        var themeButtonCenter by remember { mutableStateOf(Offset.Zero) }

        ProfileContent(
            state = ProfileUiState(
                userId = "1234567890",
                isLoading = false,
            ),
            hazeState = hazeState,
            lazyListState = lazyListState,
            motionState = motionState,
            themeButtonCenter = themeButtonCenter,
            onThemeButtonCenterChange = { themeButtonCenter = it },
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ProfileScreenPreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        val hazeState = remember { HazeState(initialBlurEnabled = true) }
        val lazyListState = rememberLazyListState()
        val motionState = rememberScrollMotionState(maxMotionDistance = 120.dp)
        var themeButtonCenter by remember { mutableStateOf(Offset.Zero) }

        ProfileContent(
            state = ProfileUiState(
                userId = "1234567890",
                isLoading = false,
            ),
            hazeState = hazeState,
            lazyListState = lazyListState,
            motionState = motionState,
            themeButtonCenter = themeButtonCenter,
            onThemeButtonCenterChange = { themeButtonCenter = it },
            onIntent = {}
        )
    }
}

