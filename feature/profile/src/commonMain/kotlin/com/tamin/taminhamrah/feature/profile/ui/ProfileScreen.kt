package com.tamin.taminhamrah.feature.profile.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileEvent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileIntent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileMenuItem
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.UserAvatar
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_aparat
import taminx.core.core_ui.ic_arrow_show_more
import taminx.core.core_ui.ic_tamin_logo

@Composable
fun ProfileScreen(
    userId: String? = null,
    viewModel: ProfileViewModel = koinViewModel(),
    onNavigateToIdentity: (String?) -> Unit = {},
    onNavigateToRouteById: (Int) -> Unit = {},
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
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xl),
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

            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }

            item {
                StandardListItem(
                    title = "اطلاعات هویتی",
                    subtitle = "نمایش اطلاعات هویتی و شماره تأمین اجتماعی",
                    icon = painterResource(Res.drawable.ic_tamin_logo),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.IDENTITY_INFO)) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }

            item {
                StandardListItem(
                    title = "تست درخواست تصویر (SendImageRequest)",
                    subtitle = when {
                        state.isImageRequestLoading -> "در حال ارسال..."
                        state.imageRequestResult != null -> "موفق: ${state.imageRequestResult.take(20)}..."
                        state.imageRequestError != null -> "خطا: ${state.imageRequestError}"
                        else -> "برای تست ارسال کلیک کنید"
                    },
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = {
                        val relation = state.taminRelation
                        if (relation != null) {
                            onIntent(ProfileIntent.SendImageRequest(branchCode = relation.brhCode, filter = "edit-text"))
                        }
                    }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "ارتباط فعال با تأمین",
                    subtitle = "وضعیت ارتباط فعال با تأمین اجتماعی",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.ACTIVE_RELATION)) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "مشاهده و ثبت افراد تبعی",
                    subtitle = "مشاهده و ثبت افراد تبعی توسط بیمه شده اصلی",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.LoadSubDominants) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "پرونده الکترونیک من",
                    subtitle = "مشاهده مدارک ثبت شده در سیستم",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.ELECTRONIC_FILE)) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "شماره حساب بانکی",
                    subtitle = "استعلام و ثبت شماره حساب های بانکی",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.LoadBankAccountList) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "تغییر شماره موبایل",
                    subtitle = "جهت شناسایی شما در اپلیکیشن تأمین من",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.CHANGE_MOBILE)) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "تنظیمات",
                    subtitle = "مدیریت ظاهر و امنیت برنامه",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.SETTINGS)) }
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }
            item {
                StandardListItem(
                    title = "خروج از حساب کاربری",
                    icon = painterResource(Res.drawable.ic_aparat),
                    showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                    onClick = { onIntent(ProfileIntent.OnItemClick(ProfileMenuItem.LOGOUT)) }
                )
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
