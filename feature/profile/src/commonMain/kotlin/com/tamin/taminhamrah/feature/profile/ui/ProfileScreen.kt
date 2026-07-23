package com.tamin.taminhamrah.feature.profile.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.ic_sun
import taminx.core.core_ui.ic_moon
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.UserAvatar
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileEvent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileIntent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState
import com.tamin.taminhamrah.feature.profile.ui.model.ProfileMenuItem
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
import kotlin.math.abs
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.SectionHeaderTitle
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.ListItemBadge
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.LocalThemeRevealController
import com.tamin.taminhamrah.ui.components.ValidationStatusCard
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_identity
import taminx.core.core_ui.ic_person
import taminx.core.core_ui.ic_communication
import taminx.core.core_ui.ic_request
import taminx.core.core_ui.ic_number
import taminx.core.core_ui.ic_mobile
import taminx.core.core_ui.ic_inbox
import taminx.core.core_ui.ic_privacy
import taminx.core.core_ui.ic_setting
import taminx.core.core_ui.ic_support
import taminx.core.core_ui.ic_send
import taminx.core.core_ui.ic_share
import taminx.core.core_ui.ic_history
import taminx.core.core_ui.ic_exit
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
import taminx.core.core_ui.profile_title
import taminx.core.core_ui.profile_share
import taminx.core.core_ui.profile_support
import taminx.core.core_ui.profile_support_section
import taminx.core.core_ui.profile_version_history

@Composable
fun ProfileScreen(
    userId: String? = null,
    viewModel: ProfileViewModel = koinViewModel(),
    onNavigateToIdentity: (String?) -> Unit = {},
    onNavigateToRouteById: (Int) -> Unit = {},
    onOpenUrl: (String) -> Unit = {},
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.sendIntent(ProfileIntent.LoadProfile(userId))
    }

    HandleProfileEvents(
        events = viewModel.events,
        onNavigateToIdentity = { onNavigateToIdentity(userId) },
        onNavigateToRouteById = onNavigateToRouteById,
        onOpenUrl = onOpenUrl,
        onBackClicked = onBackClicked
    )

    val onIntent = remember(viewModel) {
        { intent: ProfileIntent -> viewModel.sendIntent(intent) }
    }

    ProfileContent(
        state = uiState,
        onIntent = onIntent,
    )
}

@Composable
fun HandleProfileEvents(
    events: Flow<ProfileEvent>,
    onNavigateToIdentity: () -> Unit,
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
    onIntent: (ProfileIntent) -> Unit,
) {
    val density = LocalDensity.current
    val taminColors = LocalTaminColors.current
    val hazeState = remember { HazeState(initialBlurEnabled = true) }
    val scrollState = rememberProfileScrollState()
    val decaySpec = rememberSplineBasedDecay<Float>()
    val snapFlingBehavior = remember(scrollState, decaySpec) {
        snapFlingBehavior(
            snapLayoutInfoProvider = object : SnapLayoutInfoProvider {
                override fun calculateSnapOffset(velocity: Float): Float {
                    val lazyListState = scrollState.lazyListState
                    if (lazyListState.firstVisibleItemIndex == 0) {
                        val currentOffset = lazyListState.firstVisibleItemScrollOffset.toFloat()
                        val maxScrollPx = scrollState.maxScrollPx
                        if (currentOffset > 0 && currentOffset < maxScrollPx) {
                            val targetOffset = if (abs(velocity) > 500f) {
                                if (velocity > 0) maxScrollPx else 0f
                            } else {
                                if (currentOffset < maxScrollPx / 2f) 0f else maxScrollPx
                            }
                            return targetOffset - currentOffset
                        }
                    }
                    return 0f
                }
            },
            decayAnimationSpec = decaySpec,
            snapAnimationSpec = spring(stiffness = Spring.StiffnessLow)
        )
    }
    val progress = scrollState.progress
    val motionState = ProfileMotionState(progress)
    val revealController = LocalThemeRevealController.current
    var themeButtonCenter by remember { mutableStateOf(Offset.Zero) }
    val isDark = taminColors == DarkTaminColors
    val topBarGradient = remember(isDark) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }
    val defaultBorder = remember(taminColors) { BorderStroke(1.dp, taminColors.border) }
    val dangerBorder = remember(taminColors) { BorderStroke(1.dp, taminColors.dangerBorder) }
    val identityInfoTitle = stringResource(Res.string.profile_identity_info)
    val identityInfoIcon = painterResource(Res.drawable.ic_identity)
    val dependentsTitle = stringResource(Res.string.profile_dependents)
    val dependentsIcon = painterResource(Res.drawable.ic_person)
    val dependentsBadgeText = stringResource(Res.string.profile_dependents_badge_test)
    val activeRelationTitle = stringResource(Res.string.profile_active_relation)
    val activeRelationIcon = painterResource(Res.drawable.ic_communication)
    val electronicFileTitle = stringResource(Res.string.profile_electronic_file)
    val electronicFileIcon = painterResource(Res.drawable.ic_request)
    val bankAccountTitle = stringResource(Res.string.profile_bank_account)
    val bankAccountIcon = painterResource(Res.drawable.ic_number)
    val changeMobileTitle = stringResource(Res.string.profile_change_mobile)
    val changeMobileIcon = painterResource(Res.drawable.ic_mobile)
    val personalInfoSectionTitle = stringResource(Res.string.profile_personal_info)
    val personalInfoItems: ImmutableList<ListItemData> = remember(taminColors, onIntent) {
        persistentListOf(
            ListItemData(
                title = identityInfoTitle,
                leadingIconPainter = identityInfoIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientPrimary
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.IDENTITY_INFO)) }
            ),
            ListItemData(
                title = dependentsTitle,
                leadingIconPainter = dependentsIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientPrimary
                ),
                badge = ListItemBadge(
                    text = dependentsBadgeText,
                    backgroundColor = taminColors.blueBg,
                    textColor = taminColors.blueText
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.LoadSubDominants) }
            ),
            ListItemData(
                title = activeRelationTitle,
                leadingIconPainter = activeRelationIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientPrimary
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.ACTIVE_RELATION)) }
            ),
            ListItemData(
                title = electronicFileTitle,
                leadingIconPainter = electronicFileIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientPrimary
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.ELECTRONIC_FILE)) }
            ),
            ListItemData(
                title = bankAccountTitle,
                leadingIconPainter = bankAccountIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientPrimary
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.LoadBankAccountList) }
            ),
            ListItemData(
                title = changeMobileTitle,
                leadingIconPainter = changeMobileIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientPrimary
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.CHANGE_MOBILE)) }
            )
        )
    }
    val requestsTitle = stringResource(Res.string.profile_requests)
    val requestsIcon = painterResource(Res.drawable.ic_request)
    val personalInboxTitle = stringResource(Res.string.profile_personal_inbox)
    val personalInboxIcon = painterResource(Res.drawable.ic_inbox)
    val cartableSectionTitle = stringResource(Res.string.profile_cartable)
    val cartableItems: ImmutableList<ListItemData> = remember(taminColors, onIntent) {
        persistentListOf(
            ListItemData(
                title = requestsTitle,
                leadingIconPainter = requestsIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientSecondary
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.REQUESTS)) }
            ),
            ListItemData(
                title = personalInboxTitle,
                leadingIconPainter = personalInboxIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientSecondary
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.PERSONAL_INBOX)) }
            )
        )
    }
    val securityTitle = stringResource(Res.string.profile_security)
    val securityIcon = painterResource(Res.drawable.ic_privacy)
    val settingsTitle = stringResource(Res.string.profile_settings)
    val settingsIcon = painterResource(Res.drawable.ic_setting)
    val securitySettingsSectionTitle = stringResource(Res.string.profile_security_settings)
    val securityItems: ImmutableList<ListItemData> = remember(taminColors, onIntent) {
        persistentListOf(
            ListItemData(
                title = securityTitle,
                leadingIconPainter = securityIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientNeutral
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.SECURITY)) }
            ),
            ListItemData(
                title = settingsTitle,
                leadingIconPainter = settingsIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientNeutral
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.SETTINGS)) }
            )
        )
    }
    val supportTitle = stringResource(Res.string.profile_support)
    val supportIcon = painterResource(Res.drawable.ic_support)
    val contactMeTitle = stringResource(Res.string.profile_contact_me)
    val contactMeIcon = painterResource(Res.drawable.ic_send)
    val shareTitle = stringResource(Res.string.profile_share)
    val shareIcon = painterResource(Res.drawable.ic_share)
    val versionHistoryTitle = stringResource(Res.string.profile_version_history)
    val versionHistoryIcon = painterResource(Res.drawable.ic_history)
    val supportSectionTitle = stringResource(Res.string.profile_support_section)
    val supportItems: ImmutableList<ListItemData> = remember(taminColors, onIntent) {
        persistentListOf(
            ListItemData(
                title = supportTitle,
                leadingIconPainter = supportIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientSecondary
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.SUPPORT)) }
            ),
            ListItemData(
                title = contactMeTitle,
                leadingIconPainter = contactMeIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientSecondary
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.CONTACT_ME)) }
            ),
            ListItemData(
                title = shareTitle,
                leadingIconPainter = shareIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientSecondary
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.SHARE)) }
            ),
            ListItemData(
                title = versionHistoryTitle,
                leadingIconPainter = versionHistoryIcon,
                colors = ListItemColors(
                    leadingIconTintColor = taminColors.bgIconProfile,
                    leadingIconBackgroundGradient = taminColors.iconGradientSecondary
                ),
                showArrow = true,
                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.VERSION_HISTORY)) }
            )
        )
    }
    val logoutTitle = stringResource(Res.string.profile_logout)
    val logoutIcon = painterResource(Res.drawable.ic_exit)
    val logoutItems: ImmutableList<ListItemData> = remember(taminColors, onIntent) {
        persistentListOf(
            ListItemData(
                title = logoutTitle,
                leadingIconPainter = logoutIcon,
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
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
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
                                    themeButtonCenter = centerInRoot
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
                        bottomPadding = motionState.topBarBottomPadding
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(Res.string.profile_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                modifier = Modifier
                                    .graphicsLayer {
                                        alpha = motionState.titleAlpha
                                        translationY = with(density) { motionState.titleTranslationY.toPx() }
                                    }
                                    .align(Alignment.TopStart)
                                    .offset(y = (-32).dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = motionState.rowTopPadding)
                                    .padding(horizontal = Spacing.sm)
                                    .graphicsLayer {
                                        translationY = with(density) { motionState.rowTranslationY.toPx() }
                                        scaleX = motionState.avatarScale
                                        scaleY = motionState.avatarScale
                                        transformOrigin = TransformOrigin(1f, 0.5f)
                                    },
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
                                    Text(
                                        text = state.identityInfo?.nationalId ?: "22222222",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = taminColors.txtNatProfile
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(motionState.topBarContentSpacerHeight))
                    }
                    Spacer(modifier = Modifier.height(motionState.extraSpacerHeight))
                }
                ValidationStatusCard(
                    hazeState = hazeState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = Spacing.lg),
                    title = "نام نویسی شده تست",
                    subtitle = "حساب شما تأیید و فعال است تست",
                    badgeText = "معتبر تست ",
                    isValid = false
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = scrollState.lazyListState,
            flingBehavior = snapFlingBehavior,
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
                    SectionHeaderTitle(title = personalInfoSectionTitle)
                    ListGroupView(
                        containerBorder = defaultBorder,
                        items = personalInfoItems
                    )
                }
            }
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = cartableSectionTitle)
                    ListGroupView(
                        containerBorder = defaultBorder,
                        items = cartableItems
                    )
                }
            }
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = securitySettingsSectionTitle)
                    ListGroupView(
                        containerBorder = defaultBorder,
                        items = securityItems
                    )
                }
            }
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = supportSectionTitle)
                    ListGroupView(
                        containerBorder = defaultBorder,
                        items = supportItems
                    )
                }
            }
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    ListGroupView(
                        containerBorder = dangerBorder,
                        items = logoutItems
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
        ProfileContent(
            state = ProfileUiState(
                userId = "1234567890",
                isLoading = false,
            ),
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ProfileScreenPreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        ProfileContent(
            state = ProfileUiState(
                userId = "1234567890",
                isLoading = false,
            ),
            onIntent = {}
        )
    }
}
