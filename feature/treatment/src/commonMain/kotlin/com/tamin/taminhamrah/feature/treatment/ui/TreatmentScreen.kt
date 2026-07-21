package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentUiState
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.treatment.ui.contract.*
import com.tamin.taminhamrah.feature.treatment.ui.healthProfile.HealthProfileContent
import com.tamin.taminhamrah.feature.treatment.ui.healthProfile.HealthProfileViewModel
import com.tamin.taminhamrah.feature.treatment.ui.model.PatientItem
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentPR
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.feature.treatment.ui.model.toPatientList
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.icons.TaminIcons
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.koin.compose.viewmodel.koinViewModel

/** How far the insured-person carousel rides up into the teal header. */
private val CARD_OVERLAP = 40.dp

/**
 * Treatment hub: the insured person's electronic health-insurance cards, the quick-access
 * destinations, and the service categories.
 */
@Composable
fun TreatmentScreen(
    viewModel: TreatmentViewModel = koinViewModel(),
    onOpenMedicalRecords: () -> Unit = {},
    onOpenHealthProfile: () -> Unit = {},
    onOpenCenters: () -> Unit = {},
    onOpenPrescriptions: () -> Unit = {},
    onOpenMedicalApprovals: () -> Unit = {},
    onOpenMiscClaims: () -> Unit = {},
    onSearch: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    // Auto-fetch data on composition entry
    LaunchedEffect(Unit) {
        viewModel.sendIntent(TreatmentIntent.InitTreatmentFlow)
    }

    HandleTreatmentEvents(events = viewModel.events)

    TreatmentContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onOpenMedicalRecords = onOpenMedicalRecords,
        onOpenHealthProfile = onOpenHealthProfile,
        onOpenCenters = onOpenCenters,
        onOpenPrescriptions = onOpenPrescriptions,
        onOpenMedicalApprovals = onOpenMedicalApprovals,
        onOpenMiscClaims = onOpenMiscClaims,
        onSearch = onSearch,
    )
}

@Composable
fun HandleTreatmentEvents(events: Flow<TreatmentEvent>) {
    events.collectWithLifecycleAware {
        when (it) {
            is TreatmentEvent.ShowMessage -> Unit // TODO: surface via snackbar/toast
        }
    }
}

@Composable
fun TreatmentContent(
    state: TreatmentUiState,
    onIntent: (TreatmentIntent) -> Unit,
    modifier: Modifier = Modifier,
    onOpenMedicalRecords: () -> Unit = {},
    onOpenHealthProfile: () -> Unit = {},
    onOpenCenters: () -> Unit = {},
    onOpenPrescriptions: () -> Unit = {},
    onOpenMedicalApprovals: () -> Unit = {},
    onOpenMiscClaims: () -> Unit = {},
    onSearch: () -> Unit = {},
) {
    val patients = remember(state) { state.toPatientList() }
    var entitlementReason by remember { mutableStateOf<String?>(null) }
    val pagerState = rememberPagerState(pageCount = { patients.size })

    SyncPagerWithSelection(
        state = state,
        patients = patients,
        pagerState = pagerState,
        onIntent = onIntent,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LocalTaminColors.current.bgPage)
            .verticalScroll(rememberScrollState()),
    ) {
        TreatmentHubHeader(onSearch = onSearch)
        // The whole body shifts up together, so the overlap does not leave a gap below.
        Column(modifier = Modifier.offset(y = -CARD_OVERLAP)) {
            PatientCarousel(
                state = state,
                patients = patients,
                pagerState = pagerState,
                onShowEntitlementReason = { entitlementReason = it },
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            TreatmentQuickAccess(
                healthProfileCompleted = state.healthProfileCompleted,
                onOpenMedicalRecords = onOpenMedicalRecords,
                onOpenHealthProfile = onOpenHealthProfile,
                onOpenCenters = onOpenCenters,
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            TreatmentCategories(
                onOpenPrescriptions = onOpenPrescriptions,
                onOpenMedicalApprovals = onOpenMedicalApprovals,
                onOpenMiscClaims = onOpenMiscClaims,
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            TreatmentCostSummary(
                insuredShare = state.insuredShareTotal,
                organizationShare = state.organizationShareTotal,
            )
            Spacer(modifier = Modifier.height(Spacing.xxl + CARD_OVERLAP))
        }
    }

    entitlementReason?.let { reason ->
        EntitlementReasonDialog(
            reason = reason,
            onDismiss = { entitlementReason = null },
        )
    }
}

@Composable
private fun TreatmentHubHeader(onSearch: () -> Unit) {
    TaminTopAppBar(
        title = "درمان",
        centerTitle = false,
        action = {
            TaminTopAppBarButton(
                icon = TaminIcons.Search,
                contentDescription = "جست‌وجو",
                onClick = onSearch,
                bordered = true,
            )
        },
        // Runs deep enough for the carousel to ride up into it. The gradient and the
        // status-bar fill are left at their defaults, which is what puts the visible
        // step between the two bands.
        bottomPadding = CARD_OVERLAP + Spacing.xl,
    )


}

/**
 * Keeps the pager and the selected national code in step in both directions: a restored
 * selection scrolls the pager, and a swipe reports the new selection back.
 */
@Composable
private fun SyncPagerWithSelection(
    state: TreatmentUiState,
    patients: List<PatientItem>,
    pagerState: PagerState,
    onIntent: (TreatmentIntent) -> Unit,
) {
    LaunchedEffect(state.selectedNationalCode, patients) {
        val index = patients.indexOfFirst { it.nationalId == state.selectedNationalCode }
        if (index >= 0 && pagerState.currentPage != index) {
            pagerState.scrollToPage(index)
        }
    }

    LaunchedEffect(pagerState.currentPage, patients) {
        val selected = patients.getOrNull(pagerState.currentPage) ?: return@LaunchedEffect
        if (selected.nationalId != state.selectedNationalCode) {
            onIntent(TreatmentIntent.SelectPatient(selected.nationalId, selected.fullName))
        }
    }
}

@Composable
private fun EntitlementReasonDialog(
    reason: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("تایید") }
        },
        title = { Text("علت عدم استحقاق درمان") },
        text = { Text(reason) },
    )
}
@PreviewRtlTheme
@Composable
fun TreatmentScreenPreview() {
    PreviewRtlThemeContent {
        TreatmentContent(
            state = TreatmentMocks.mainUiState,
            onIntent = {},
        )
    }
}

@Composable
fun DecorativeQRCode(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(CornerRadius.xs))
            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(CornerRadius.xs))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            repeat(8) { rowIndex ->
                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    repeat(8) { colIndex ->
                        val isPixel = remember {
                            // Classic QR finder patterns at corners
                            (rowIndex < 3 && colIndex < 3) ||
                                (rowIndex < 3 && colIndex >= 5) ||
                                (rowIndex >= 5 && colIndex < 3) ||
                                // Random bytes simulation
                                (rowIndex + colIndex) % 2 == 0 ||
                                (rowIndex * colIndex) % 3 == 0
                        }
                        Box(
                            modifier = Modifier
                                .size(2.dp)
                                .background(if (isPixel) Color.Black else Color.White)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PatientSelectionSection(
    patients: List<PatientItem>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    deservedList: List<DeservedTreatmentPR>,
    onShowDetails: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "انتخاب بیمه‌شده",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
            color = MaterialTheme.colorScheme.onBackground
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = Spacing.md),
            pageSpacing = Spacing.sm
        ) { page ->
            val patient = patients.getOrNull(page)
            if (patient != null) {
                TreatmentCardItem(
                    patient = patient,
                    deservedList = deservedList,
                    onShowDetails = onShowDetails
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Indicator dots
        if (patients.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(patients.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (isSelected) 8.dp else 6.dp)
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun TreatmentCardItem(
    patient: PatientItem,
    deservedList: List<DeservedTreatmentPR>,
    onShowDetails: (String) -> Unit
) {
    val mainDeserved = deservedList.firstOrNull()
    val hasDeserved = if (!patient.isDependent && mainDeserved != null) {
        !mainDeserved.message.contains("عدم")
    } else true

    val isError = !patient.isDependent && mainDeserved != null && !hasDeserved
    val isLoading = !patient.isDependent && mainDeserved == null

    // Curated Harmonious Gradient Palette
    val gradientColors = when {
        isLoading -> listOf(Color(0xFF78909C), Color(0xFFB0BEC5)) // Gray/Blue-gray loading
        isError -> listOf(Color(0xFFC62828), Color(0xFFEF5350)) // Premium red
        else -> listOf(Color(0xFF2E7D32), Color(0xFF4CAF50)) // Premium green
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(CornerRadius.md),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(Spacing.md)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row: Brand & Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (patient.isDependent) "کارت خدمات درمانی تحت تکفل" else "کارت خدمات درمانی اصلی",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "تأمین همراه",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                // Middle Row: Details & Barcode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = Spacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = patient.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "کد ملی: ${patient.nationalId}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        if (patient.brhName != null) {
                            Text(
                                text = "شعبه: ${patient.brhName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        if (patient.insuranceType != null) {
                            Text(
                                text = "نوع بیمه: ${patient.insuranceType}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    DecorativeQRCode(modifier = Modifier.size(65.dp))
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                // Bottom Row: Status text & detail button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                isLoading -> Icons.Default.Refresh
                                isError -> Icons.Default.Close
                                else -> Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when {
                                isLoading -> "در حال استعلام وضعیت استحقاق..."
                                isError -> "فاقد استحقاق درمان"
                                else -> "مشمول حمایت درمانی تأمین اجتماعی"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }

                    if (isError && mainDeserved.message.isNotEmpty()) {
                        Button(
                            onClick = { onShowDetails(mainDeserved.message) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.25f),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("علت عدم استحقاق", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardMenuSection(
    activePatientName: String,
    onFlowSelected: (TreatmentFlow) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier.padding(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = "در حال مشاهده اطلاعات درمان برای: $activePatientName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xs))

        DashboardMenuItem(
            title = "سلامت من",
            desc = "پرونده سلامت، خوداظهاری و حساسیت‌های دارویی",
            icon = Icons.Default.Person,
            onClick = { onFlowSelected(TreatmentFlow.HEALTH_PROFILE) }
        )
    }
}

@Composable
fun DashboardMenuItem(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(CornerRadius.sm),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.md))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
