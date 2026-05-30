package com.tamin.taminhamrah.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.ui.components.UserAvatar
import com.tamin.taminhamrah.ui.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String?,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    UserAvatar(
                        model = uiState.profileImageBase64?.takeIf { it.isNotBlank() },
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))

                    if (uiState.isLoadingImage) {
                        CircularProgressIndicator()
                    }

                    uiState.imageError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    if (!userId.isNullOrEmpty()) {
                        Text(
                            text = userId,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
            }

            item {
                IdentityInfoSection(uiState = uiState)
            }
        }
    }
}

@Composable
private fun IdentityInfoSection(uiState: ProfileUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = "اطلاعات هویتی (central-reg/personal)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        if (uiState.isLoadingIdentity) {
            RowLoading(label = "در حال دریافت اطلاعات...")
        }

        uiState.identityError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        uiState.identityInfo?.let { info ->
            IdentityField("نام", info.firstName)
            IdentityField("نام خانوادگی", info.lastName)
            IdentityField("نام پدر", info.fatherName)
            IdentityField("کد ملی", info.nationalId)
            IdentityField("شماره شناسنامه", info.idCardNumber)
            IdentityField("شهر تولد", info.cityOfBirthName)
            IdentityField("شهر صدور", info.cityOfIssueName)
            IdentityField("کد شهر تولد", info.cityOfBirthId)
            IdentityField("کد شهر صدور", info.cityOfIssueId)
        }
    }
}

@Composable
private fun RowLoading(label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        CircularProgressIndicator()
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun IdentityField(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
