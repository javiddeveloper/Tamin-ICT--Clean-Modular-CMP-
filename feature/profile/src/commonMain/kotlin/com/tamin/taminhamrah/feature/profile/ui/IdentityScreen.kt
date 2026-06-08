package com.tamin.taminhamrah.feature.profile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileIntent
import com.tamin.taminhamrah.ui.components.UserAvatar
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_arrow_show_more
import taminx.core.core_ui.ic_tamin_logo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentityScreen(
    userId: String? = null,
    viewModel: ProfileViewModel = koinViewModel(),
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.sendIntent(ProfileIntent.LoadProfile(userId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اطلاعات هویتی") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.identityInfo == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null && uiState.identityInfo == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(text = uiState.error ?: "خطایی رخ داده است", color = MaterialTheme.colorScheme.error)
            }
        } else {
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
                            model = uiState.profileImage,
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))
                    }
                }

                val identity = uiState.identityInfo
                if (identity != null) {
                    item {
                        StandardListItem(
                            title = "نام و نام خانوادگی",
                            subtitle = identity.fullName,
                            icon = painterResource(Res.drawable.ic_tamin_logo),
                            showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                            onClick = {}
                        )
                    }
                    item { HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg)) }
                    item {
                        StandardListItem(
                            title = "نام پدر",
                            subtitle = identity.fatherName,
                            icon = painterResource(Res.drawable.ic_tamin_logo),
                            showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                            onClick = {}
                        )
                    }
                    item { HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg)) }
                    item {
                        StandardListItem(
                            title = "کد ملی",
                            subtitle = identity.nationalId,
                            icon = painterResource(Res.drawable.ic_tamin_logo),
                            showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                            onClick = {}
                        )
                    }
                    item { HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg)) }
                    item {
                        StandardListItem(
                            title = "شماره شناسنامه",
                            subtitle = identity.idCardNumber,
                            icon = painterResource(Res.drawable.ic_tamin_logo),
                            showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                            onClick = {}
                        )
                    }
                    item { HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg)) }
                    item {
                        StandardListItem(
                            title = "محل صدور",
                            subtitle = identity.cityOfIssueName,
                            icon = painterResource(Res.drawable.ic_tamin_logo),
                            showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                            onClick = {}
                        )
                    }
                    item { HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg)) }
                    item {
                        StandardListItem(
                            title = "شماره بیمه (SSN)",
                            subtitle = identity.ssn,
                            icon = painterResource(Res.drawable.ic_tamin_logo),
                            showMoreIcon = painterResource(Res.drawable.ic_arrow_show_more),
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}
