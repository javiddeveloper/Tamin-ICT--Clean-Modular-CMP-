package com.tamin.taminhamrah.feature.profile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileEvent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileIntent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState
import com.tamin.taminhamrah.feature.profile.ui.model.ProfileMenuItem
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.SectionHeaderTitle
import com.tamin.taminhamrah.ui.components.UserAvatar
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.ListItemBadge
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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
    onNavigateToHealthProfile: () -> Unit = {},
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
        onNavigateToHealthProfile = onNavigateToHealthProfile,
        onOpenUrl = onOpenUrl,
        onBackClicked = onBackClicked
    )

    ProfileContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
fun HandleProfileEvents(
    events: Flow<ProfileEvent>,
    onNavigateToIdentity: () -> Unit,
    onNavigateToRouteById: (Int) -> Unit,
    onNavigateToHealthProfile: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    val scope = rememberCoroutineScope()
    events.collectWithLifecycleAware {
        when (it) {
            ProfileEvent.NavigateBack -> {
                scope.launch {
                    onBackClicked()
                }
            }

            ProfileEvent.NavigateToSettings -> {
                // For now, let's assume destinationId for settings is 100 or something,
                // or we can handle it differently.
                scope.launch {
                    // onNavigateToRouteById(100)
                }
            }

            ProfileEvent.NavigateToIdentity -> {
                scope.launch {
                    onNavigateToIdentity()
                }
            }
            ProfileEvent.NavigateToHealthProfile -> {
                scope.launch {
                    onNavigateToHealthProfile()
                }
            }
            is ProfileEvent.OpenUrl -> {
                scope.launch {
                    onOpenUrl(it.url)
                }
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
    val taminColors = LocalTaminColors.current
    Scaffold(modifier = modifier) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 80.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UserAvatar(
                        model = state.profileImage,
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))

                    if (!state.userId.isNullOrEmpty()) {
                        val identity = state.identityInfo
                        val relation = state.taminRelation
                        if (identity != null && relation != null) {
                            Text(
                                text = "${identity.fullName} - ${identity.cityOfBirthName} - ${relation.brhAdress}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // اطلاعات شخصی
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = stringResource(Res.string.profile_personal_info))
                    ListGroupView(
                        containerBorder = BorderStroke(1.dp, taminColors.border),
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
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "پروفایل سلامت",
                    subtitle = "نمایش اطلاعات عمومی سلامت، سبک زندگی و حساسیت‌ها",
                    icon = painterResource(Res.drawable.ic_tamin_logo),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.HEALTH_PROFILE)) }
                )
            }
            // کارتابل
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = stringResource(Res.string.profile_cartable))
                    ListGroupView(
                        containerBorder = BorderStroke(1.dp, taminColors.border),
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

            // امنیت و تنظیمات
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = stringResource(Res.string.profile_security_settings))
                    ListGroupView(
                        containerBorder = BorderStroke(1.dp, taminColors.border),
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

            // پشتیبانی
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = stringResource(Res.string.profile_support_section))
                    ListGroupView(
                        containerBorder = BorderStroke(1.dp, taminColors.border),
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

            // خروج از حساب کاربری
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    ListGroupView(
                        containerBorder = BorderStroke(1.dp, taminColors.dangerBorder),
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
