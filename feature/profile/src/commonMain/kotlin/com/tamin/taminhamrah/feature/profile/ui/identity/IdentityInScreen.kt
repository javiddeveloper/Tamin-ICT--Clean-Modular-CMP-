package com.tamin.taminhamrah.feature.profile.ui.identity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInEvent
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInIntent
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun IdentityInRoute(
    viewModel: IdentityInViewModel,
    onNavigateToRoute: (String) -> Unit, // Assuming String for now as per template, though TaminScreens was mentioned
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(IdentityInIntent.LoadIdentity)
    }

    HandleIdentityInEvents(
        events = viewModel.events,
        onNavigateToRoute = onNavigateToRoute,
        onBackClicked = onBackClicked
    )

    IdentityInScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onNavigateToRoute = onNavigateToRoute,
    )
}

@Composable
fun HandleIdentityInEvents(
    events: Flow<IdentityInEvent>,
    onNavigateToRoute: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            IdentityInEvent.NavigateBack -> onBackClicked()
        }
    }
}

@Composable
fun IdentityInScreen(
    modifier: Modifier = Modifier,
    state: IdentityInUiState,
    onIntent: (IdentityInIntent) -> Unit,
    onNavigateToRoute: (String) -> Unit,
) {
    val taminColors = LocalTaminColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaminTopAppBar(
                title = "اطلاعات هویتی",
                centerTitle = true,
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = { onIntent(IdentityInIntent.OnBackClicked) }
                    )
                }
            )
        },
        containerColor = taminColors.bgPage
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Spacing.lg)
        ) {
            // Figma Design: white background with border
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = Color(0x1A000000), // #1A000000 from vector
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(Spacing.lg)
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = taminColors.teal)
                    }
                } else if (state.error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.error,
                            color = taminColors.dangerText,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    state.identityInfo?.let { info ->
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(Spacing.md)
                        ) {
                            item { IdentityDetailItem("نام", info.firstName) }
                            item { IdentityDetailItem("نام خانوادگی", info.lastName) }
                            item { IdentityDetailItem("نام پدر", info.fatherName) }
                            item { IdentityDetailItem("کد ملی", info.nationalId.toPersianDigits()) }
                            item { IdentityDetailItem("شماره شناسنامه", info.idCardNumber.toPersianDigits()) }
                            item { IdentityDetailItem("تاریخ تولد", info.dateOfBirthFormatted.toPersianDigits()) }
                            item { IdentityDetailItem("جنسیت", info.genderDisplay) }
                            item { IdentityDetailItem("محل تولد", info.cityOfBirthName) }
                            item { IdentityDetailItem("محل صدور", info.cityOfIssueName) }
                            item { IdentityDetailItem("شماره بیمه (SSN)", info.ssn.toPersianDigits()) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IdentityDetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = taminColors.textSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = taminColors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        HorizontalDivider(color = taminColors.border.copy(alpha = 0.5f))
    }
}

@PreviewRtlTheme
@Composable
fun PreviewIdentityInScreen() {
    PreviewRtlThemeContent {
        IdentityInScreen(
            onIntent = {},
            onNavigateToRoute = {},
            state = IdentityInUiState(
                identityInfo = com.tamin.taminhamrah.model.identity.IdentityInfoPR(
                    firstName = "محمد",
                    lastName = "احمدی",
                    fatherName = "علی",
                    nationalId = "1234567890",
                    idCardNumber = "12345",
                    dateOfBirthFormatted = "1370/01/01",
                    genderDisplay = "مرد",
                    cityOfBirthName = "تهران",
                    cityOfIssueName = "تهران",
                    ssn = "9876543210",
                    cityOfBirthId = "",
                    cityOfIssueId = "",
                    countryId = "",
                    dateOfBirth = 0L,
                    fullName = "محمد احمدی",
                    gender = "",
                    id = 0,
                    idCardSerial = ""
                )
            )
        )
    }
}
