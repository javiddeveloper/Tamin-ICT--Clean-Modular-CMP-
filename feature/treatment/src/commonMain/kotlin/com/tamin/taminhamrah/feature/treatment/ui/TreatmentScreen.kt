package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.treatment.ui.components.CategoryTile
import com.tamin.taminhamrah.feature.treatment.ui.components.InsuranceCard
import com.tamin.taminhamrah.feature.treatment.ui.components.InsuranceCardCarousel
import com.tamin.taminhamrah.feature.treatment.ui.components.QuickAccessCard
import com.tamin.taminhamrah.feature.treatment.ui.components.SectionLabel
import com.tamin.taminhamrah.feature.treatment.ui.components.TreatmentEmptyState
import com.tamin.taminhamrah.feature.treatment.ui.components.TreatmentHeader
import com.tamin.taminhamrah.feature.treatment.ui.components.TreatmentHeaderButton
import com.tamin.taminhamrah.feature.treatment.ui.components.TreatmentNavigationCard
import com.tamin.taminhamrah.feature.treatment.ui.contract.*
import com.tamin.taminhamrah.feature.treatment.ui.model.PatientItem
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminRed
import com.tamin.taminhamrah.ui.theme.TaminRedDark
import com.tamin.taminhamrah.ui.theme.TaminTeal500
import com.tamin.taminhamrah.ui.theme.TaminTeal900
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
    onSearch: (() -> Unit)? = null,
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
    modifier: Modifier = Modifier,
    state: TreatmentUiState,
    onIntent: (TreatmentIntent) -> Unit,
    onOpenMedicalRecords: () -> Unit = {},
    onOpenHealthProfile: () -> Unit = {},
    onOpenCenters: () -> Unit = {},
    onOpenPrescriptions: () -> Unit = {},
    onOpenMedicalApprovals: () -> Unit = {},
    onOpenMiscClaims: () -> Unit = {},
    onSearch: (() -> Unit)? = null,
) {
    val patients = rememberPatients(state)
    var entitlementReason by remember { mutableStateOf<String?>(null) }
    val pagerState = rememberPagerState(pageCount = { patients.size })

    LaunchedEffect(state.selectedNationalCode, patients) {
        val index = patients.indexOfFirst { it.nationalId == state.selectedNationalCode }
        if (index >= 0 && pagerState.currentPage != index) {
            pagerState.scrollToPage(index)
        }
    }

    LaunchedEffect(pagerState.currentPage, patients) {
        if (patients.isNotEmpty() && pagerState.currentPage < patients.size) {
            val selected = patients[pagerState.currentPage]
            if (selected.nationalId != state.selectedNationalCode) {
                onIntent(TreatmentIntent.SelectPatient(selected.nationalId, selected.fullName))
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LocalTaminColors.current.bgPage)
            .verticalScroll(rememberScrollState()),
    ) {
        TreatmentHeader(
            title = "درمان",
            centerTitle = false,
            action = onSearch?.let {
                {
                    TreatmentHeaderButton(
                        icon = Icons.Filled.Search,
                        contentDescription = "جست‌وجو",
                        onClick = it,
                        bordered = true,
                    )
                }
            },
            // Deep enough for the carousel to ride up into without covering the title.
            modifier = Modifier.padding(bottom = CARD_OVERLAP + Spacing.xl),
        )
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
            Spacer(modifier = Modifier.height(Spacing.xxl + CARD_OVERLAP))
        }
    }

    entitlementReason?.let { reason ->
        AlertDialog(
            onDismissRequest = { entitlementReason = null },
            confirmButton = {
                TextButton(onClick = { entitlementReason = null }) { Text("تایید") }
            },
            title = { Text("علت عدم استحقاق درمان") },
            text = { Text(reason) },
        )
    }
}

/** Builds the display list: the main insured person first, then dependants under 18. */
@Composable
private fun rememberPatients(state: TreatmentUiState): List<PatientItem> =
    remember(state.deservedList, state.dependantList, state.mainUserNationalCode) {
        buildList {
            state.mainUserNationalCode?.let { code ->
                val mainUser = state.deservedList.firstOrNull()
                add(
                    PatientItem(
                        nationalId = code,
                        fullName = mainUser?.fullName
                            ?: state.selectedPatientName
                            ?: "بیمه‌شده اصلی",
                        isDependent = false,
                        brhName = mainUser?.brhName,
                        insuranceType = mainUser?.insuranceType,
                    ),
                )
            }
            state.dependantList.forEach { dependant ->
                add(
                    PatientItem(
                        nationalId = dependant.nationalId,
                        fullName = dependant.fullName,
                        isDependent = true,
                        relation = "تحت تکفل",
                    ),
                )
            }
        }
    }

@Composable
private fun PatientCarousel(
    state: TreatmentUiState,
    patients: List<PatientItem>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onShowEntitlementReason: (String) -> Unit,
) {
    when {
        state.isLoading && patients.isEmpty() -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(CARD_LOADING_HEIGHT),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = LocalTaminColors.current.teal)
        }

        patients.isEmpty() -> TreatmentEmptyState(
            message = state.error ?: "بیمه‌شده‌ای برای نمایش وجود ندارد",
        )

        else -> InsuranceCardCarousel(
            pageCount = patients.size,
            pagerState = pagerState,
        ) { page ->
            val patient = patients[page]
            val coverage = coverageFor(patient, state.deservedList)
            InsuranceCard(
                holderName = patient.fullName,
                nationalId = patient.nationalId,
                coverageLabel = coverage.label,
                background = coverage.background,
                footerAction = coverage.reason?.let { reason ->
                    { EntitlementReasonChip(onClick = { onShowEntitlementReason(reason) }) }
                },
            )
        }
    }
}

private val CARD_LOADING_HEIGHT = 160.dp

@Composable
private fun EntitlementReasonChip(onClick: () -> Unit) {
    Text(
        text = "علت",
        style = MaterialTheme.typography.labelMedium,
        color = Color.White,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.22f), CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
    )
}

/** How the card presents one patient's entitlement: colour, wording and failure reason. */
private data class Coverage(
    val label: String,
    val background: Brush,
    val reason: String?,
)

@Composable
private fun coverageFor(
    patient: PatientItem,
    deservedList: List<DeservedTreatmentPR>,
): Coverage {
    val mainDeserved = deservedList.firstOrNull()
    // Dependants inherit the main insured person's entitlement, so only the main record
    // is ever pending or rejected.
    val isPending = !patient.isDependent && mainDeserved == null
    val isRejected = !patient.isDependent &&
        mainDeserved != null &&
        mainDeserved.message.contains("عدم")

    return when {
        isPending -> Coverage(
            label = "در حال استعلام وضعیت استحقاق…",
            background = Brush.verticalGradient(
                listOf(LocalTaminColors.current.textMuted, LocalTaminColors.current.chevron),
            ),
            reason = null,
        )

        isRejected -> Coverage(
            label = "فاقد استحقاق درمان",
            background = Brush.verticalGradient(listOf(TaminRedDark, TaminRed)),
            reason = mainDeserved?.message?.takeIf { it.isNotEmpty() },
        )

        else -> Coverage(
            label = "وضعیت حمایت‌های درمانی: برخوردار هستید",
            background = Brush.verticalGradient(listOf(TaminTeal900, TaminTeal500)),
            reason = null,
        )
    }
}

@Composable
private fun TreatmentQuickAccess(
    onOpenMedicalRecords: () -> Unit,
    onOpenHealthProfile: () -> Unit,
    onOpenCenters: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier.padding(horizontal = Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        SectionLabel(text = "دسترسی سریع")
        QuickAccessCard(
            title = "سوابق درمانی من",
            subtitle = "تاریخچهٔ نسخه، ویزیت، پاراکلینیک و آزمایش",
            icon = Icons.AutoMirrored.Filled.List,
            trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            onClick = onOpenMedicalRecords,
        )
        TreatmentNavigationCard(
            title = "پروندهٔ سلامت من",
            subtitle = "خوداظهاری سلامت و اطلاعات پزشکی",
            icon = Icons.Filled.Favorite,
            iconTint = colors.blueText,
            iconBackground = Brush.linearGradient(listOf(colors.blueBg, colors.blueBg)),
            trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            onClick = onOpenHealthProfile,
        )
        TreatmentNavigationCard(
            title = "مراکز درمانی طرف قرارداد",
            subtitle = "جست‌وجوی بیمارستان و داروخانه",
            icon = Icons.Filled.LocationOn,
            iconTint = colors.teal,
            iconBackground = Brush.linearGradient(listOf(colors.greenBg, colors.greenBg)),
            trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            onClick = onOpenCenters,
        )
    }
}

@Composable
private fun TreatmentCategories(
    onOpenPrescriptions: () -> Unit,
    onOpenMedicalApprovals: () -> Unit,
    onOpenMiscClaims: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.padding(horizontal = Spacing.page),
        horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        CategoryTile(
            label = "نسخه‌های الکترونیک",
            icon = Icons.AutoMirrored.Filled.List,
            iconTint = colors.blueText,
            iconBackground = Brush.linearGradient(listOf(colors.blueBg, colors.blueBg)),
            onClick = onOpenPrescriptions,
            modifier = Modifier.weight(1f),
        )
        CategoryTile(
            label = "تاییدیه‌های پزشکی",
            icon = Icons.Filled.Favorite,
            iconTint = colors.teal,
            iconBackground = Brush.linearGradient(listOf(colors.greenBg, colors.greenBg)),
            onClick = onOpenMedicalApprovals,
            modifier = Modifier.weight(1f),
        )
        CategoryTile(
            label = "خسارت متفرقه",
            icon = Icons.Filled.DateRange,
            iconTint = colors.orangeText,
            iconBackground = Brush.linearGradient(listOf(colors.orangeBg, colors.orangeBg)),
            onClick = onOpenMiscClaims,
            modifier = Modifier.weight(1f),
        )
    }
}

@PreviewRtlTheme
@Composable
fun TreatmentScreenPreview() {
    PreviewRtlThemeContent {
        TreatmentContent(
            state = TreatmentMocks.mainUiState,
            onIntent = {},
            onSearch = {},
        )
    }
}
