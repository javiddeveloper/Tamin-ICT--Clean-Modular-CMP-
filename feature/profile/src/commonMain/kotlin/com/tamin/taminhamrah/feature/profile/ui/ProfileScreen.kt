package com.tamin.taminhamrah.feature.profile.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.components.UserAvatar
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_aparat
import taminx.core.core_ui.ic_arrow_show_more
import taminx.core.core_ui.ic_tamin_logo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String?,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                        model = uiState.profileImageBase64 ?: "",
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))

                    if (!userId.isNullOrEmpty()) {
                        Text(
                            text = userId,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                    onClick = {}
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
                    onClick = {}
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
                    onClick = {}
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
                    onClick = {}
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
                    onClick = {}
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
                    onClick = {}
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
                    onClick = {}
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
                    onClick = {}
                )
            }
        }
    }
}
