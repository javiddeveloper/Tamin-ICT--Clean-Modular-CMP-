package com.tamin.taminhamrah.feature.profile.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_aparat
import taminx.core.core_ui.ic_tamin_logo
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
                .fillMaxSize()
                .padding(paddingValues),
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
                    SectionHeaderTitle(title = "اطلاعات شخصی")
                    ListGroupView(
                        containerBorder = BorderStroke(1.dp, taminColors.border),
                        items = persistentListOf(
                            ListItemData(
                                title = "اطلاعات هویتی",
                                leadingIconPainter = painterResource(Res.drawable.ic_identity),
                                showChevron = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.IDENTITY_INFO)) }
                            ),
                            ListItemData(
                                title = "مشاهده و ثبت افراد تبعی",
                                leadingIconPainter = painterResource(Res.drawable.ic_person),
                                badge = ListItemBadge(
                                    text = "۳ نفر",
                                    backgroundColor = taminColors.blueBg,
                                    textColor = taminColors.blueText
                                ),
                                showChevron = true,
                                onClick = { onIntent(ProfileIntent.LoadSubDominants) }
                            ),
                            ListItemData(
                                title = "ارتباط فعال با تأمین",
                                leadingIconPainter = painterResource(Res.drawable.ic_communication),
                                showChevron = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.ACTIVE_RELATION)) }
                            ),
                            ListItemData(
                                title = "پرونده الکترونیک",
                                leadingIconPainter = painterResource(Res.drawable.ic_request),
                                showChevron = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.ELECTRONIC_FILE)) }
                            ),
                            ListItemData(
                                title = "شماره حساب بانکی",
                                leadingIconPainter = painterResource(Res.drawable.ic_number),
                                showChevron = true,
                                onClick = { onIntent(ProfileIntent.LoadBankAccountList) }
                            ),
                            ListItemData(
                                title = "تغییر شماره موبایل",
                                leadingIconPainter = painterResource(Res.drawable.ic_mobile),
                                showChevron = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.CHANGE_MOBILE)) }
                            )
                        )
                    )
                }
            }

            // کارتابل
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = "کارتابل")
                    ListGroupView(
                        containerBorder = BorderStroke(1.dp, taminColors.border),
                        items = persistentListOf(
                            ListItemData(
                                title = "درخواست ها",
                                leadingIconPainter = painterResource(Res.drawable.ic_request),
                                showChevron = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.REQUESTS)) }
                            ),
                            ListItemData(
                                title = "صندوق شخصی",
                                leadingIconPainter = painterResource(Res.drawable.ic_inbox),
                                showChevron = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.PERSONAL_INBOX)) }
                            )
                        )
                    )
                }
            }

            // امنیت و تنظیمات
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = "امنیت و تنظیمات")
                    ListGroupView(
                        containerBorder = BorderStroke(1.dp, taminColors.border),
                        items = persistentListOf(
                            ListItemData(
                                title = "امنیت",
                                leadingIconPainter = painterResource(Res.drawable.ic_privacy),
                                showChevron = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.SECURITY)) }
                            ),
                            ListItemData(
                                title = "تنظیمات",
                                leadingIconPainter = painterResource(Res.drawable.ic_setting),
                                showChevron = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.SETTINGS)) }
                            )
                        )
                    )
                }
            }

            // پشتیبانی
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg)) {
                    SectionHeaderTitle(title = "پشتیبانی")
                    ListGroupView(
                        containerBorder = BorderStroke(1.dp, taminColors.border),
                        items = persistentListOf(
                            ListItemData(
                                title = "پشتیبانی",
                                leadingIconPainter = painterResource(Res.drawable.ic_support),
                                showChevron = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.SUPPORT)) }
                            ),
                            ListItemData(
                                title = "تماس با من",
                                leadingIconPainter = painterResource(Res.drawable.ic_send),
                                showChevron = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.CONTACT_ME)) }
                            ),
                            ListItemData(
                                title = "اشتراک‌گذاری",
                                leadingIconPainter = painterResource(Res.drawable.ic_share),
                                showChevron = true,
                                onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.SHARE)) }
                            ),
                            ListItemData(
                                title = "تاریخچهٔ نسخه",
                                leadingIconPainter = painterResource(Res.drawable.ic_history),
                                showChevron = true,
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
                                title = "خروج از حساب کاربری",
                                leadingIconPainter = painterResource(Res.drawable.ic_exit),
                                colors = ListItemColors(
                                    titleColor = taminColors.dangerText,
                                    leadingIconBackgroundColor = taminColors.dangerBg,
                                    leadingIconTintColor = taminColors.dangerText
                                ),
                                showChevron = false,
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
