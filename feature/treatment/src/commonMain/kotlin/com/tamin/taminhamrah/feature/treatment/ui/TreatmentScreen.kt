package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentFlow
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.PatientItem
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMessageType
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.feature.treatment.ui.model.toPatientItems
import com.tamin.taminhamrah.feature.treatment.ui.records.RecordTab
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.TaminPurple500
import com.tamin.taminhamrah.ui.theme.TaminPurple700
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import kotlinx.coroutines.flow.Flow
import org.koin.compose.viewmodel.koinViewModel

/**
 * Treatment dashboard ("درمان" hub): patient card carousel, quick access and the sub-flow grid.
 *
 * Layout structure only — colors, typography and the shared card/header components are owned by
 * the design pass. Sub-flows are separate destinations (see `treatmentGraph`), so system back
 * unwinds through the nav back stack.
 */
@Composable
fun TreatmentScreen(
    onNavigateToRecords: (String, RecordTab) -> Unit,
    viewModel: TreatmentViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Auto-fetch data on composition entry
    LaunchedEffect(Unit) {
        viewModel.sendIntent(TreatmentIntent.InitTreatmentFlow)
    }

    HandleTreatmentEvents(events = viewModel.events, snackbarHostState = snackbarHostState)

    TreatmentContent(
        state = uiState,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::sendIntent,
        onNavigateToRecords = onNavigateToRecords
    )
}

@Composable
fun HandleTreatmentEvents(events: Flow<TreatmentEvent>, snackbarHostState: SnackbarHostState) {
    events.collectWithLifecycleAware {
        when (it) {
            // Failures stay on screen longer than confirmations; the message is already localized.
            is TreatmentEvent.ShowMessage -> snackbarHostState.showSnackbar(
                message = it.message,
                withDismissAction = it.type == TreatmentMessageType.OPERATION_FAILED,
                duration = if (it.type == TreatmentMessageType.OPERATION_FAILED) {
                    SnackbarDuration.Long
                } else {
                    SnackbarDuration.Short
                }
            )
        }
    }
}

@Composable
fun TreatmentContent(
    modifier: Modifier = Modifier,
    state: TreatmentUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onIntent: (TreatmentIntent) -> Unit,
    onNavigateToRecords: (String, RecordTab) -> Unit
) {
    val patients = rememberPatients(state)
    var showDetailDialog by remember { mutableStateOf<String?>(null) }
    val pagerState = rememberPagerState(pageCount = { patients.size })

    SyncSelectedPatient(patients = patients, pagerState = pagerState, state = state, onIntent = onIntent)

    val colors = LocalTaminColors.current

    Scaffold(
        modifier = modifier,
        containerColor = colors.bgPage,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "درمان", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "جست‌وجو")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.teal,
                    titleContentColor = colors.bgSurface,
                    actionIconContentColor = colors.bgSurface
                )
            )
        }
    ) { padding ->
        if (state.isLoading && patients.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = Spacing.sm)
        ) {
            PatientSelectionSection(
                patients = patients,
                pagerState = pagerState,
                deservedList = state.deservedList,
                onShowDetails = { msg -> showDetailDialog = msg }
            )

            val openRecords = { tab: RecordTab ->
                state.selectedNationalCode?.let { onNavigateToRecords(it, tab) }
                Unit
            }

            QuickAccessSection(
                isHealthProfileCompleted = state.isHealthProfileCompleted,
                onOpenRecords = { openRecords(RecordTab.ALL) },
                onOpenHealthProfile = { onIntent(TreatmentIntent.SwitchFlow(TreatmentFlow.HEALTH_PROFILE)) },
                onOpenCenters = { onIntent(TreatmentIntent.SwitchFlow(TreatmentFlow.CENTERS)) }
            )

            ServiceGridSection(
                // «نسخه‌های الکترونیک» is the medicine tab of سوابق درمانی, not a screen of its own.
                onOpenPrescriptions = { openRecords(RecordTab.MEDICINE) },
                onOpenConfirmations = { onIntent(TreatmentIntent.SwitchFlow(TreatmentFlow.CONFIRMATIONS)) },
                onOpenCosts = { onIntent(TreatmentIntent.SwitchFlow(TreatmentFlow.COSTS)) }
            )

            YearlyCostSection()
        }
    }

    if (showDetailDialog != null) {
        AlertDialog(
            onDismissRequest = { showDetailDialog = null },
            confirmButton = {
                TextButton(onClick = { showDetailDialog = null }) {
                    Text("تایید")
                }
            },
            title = { Text("علت عدم استحقاق درمان") },
            text = { Text(showDetailDialog ?: "") }
        )
    }
}

/** Carousel entries, shared with the records patient filter. */
@Composable
private fun rememberPatients(state: TreatmentUiState): List<PatientItem> =
    remember(state.deservedList, state.dependantList, state.mainUserNationalCode) {
        state.toPatientItems()
    }

/** Keeps the pager and the ViewModel's selected patient in step, in both directions. */
@Composable
private fun SyncSelectedPatient(
    patients: List<PatientItem>,
    pagerState: PagerState,
    state: TreatmentUiState,
    onIntent: (TreatmentIntent) -> Unit
) {
    LaunchedEffect(state.selectedNationalCode, patients) {
        val index = patients.indexOfFirst { it.nationalId == state.selectedNationalCode }
        if (index >= 0 && pagerState.currentPage != index) {
            pagerState.scrollToPage(index)
        }
    }

    LaunchedEffect(pagerState.currentPage, patients) {
        if (patients.isNotEmpty() && pagerState.currentPage < patients.size) {
            val selectedPatient = patients[pagerState.currentPage]
            if (selectedPatient.nationalId != state.selectedNationalCode) {
                onIntent(
                    TreatmentIntent.SelectPatient(selectedPatient.nationalId, selectedPatient.fullName)
                )
            }
        }
    }
}

@Composable
fun PatientSelectionSection(
    patients: List<PatientItem>,
    pagerState: PagerState,
    deservedList: List<DeservedTreatmentPR>,
    onShowDetails: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = Spacing.lg),
            pageSpacing = Spacing.md
        ) { page ->
            patients.getOrNull(page)?.let { patient ->
                TreatmentCardItem(
                    patient = patient,
                    deservedList = deservedList,
                    onShowDetails = onShowDetails
                )
            }
        }

        if (patients.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
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
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                },
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

/**
 * Insurance card for one patient. Entitlement is only reported for the main insured, so
 * dependants render as covered.
 */
@Composable
fun TreatmentCardItem(
    patient: PatientItem,
    deservedList: List<DeservedTreatmentPR>,
    onShowDetails: (String) -> Unit
) {
    val mainDeserved = deservedList.firstOrNull()
    val isLoading = !patient.isDependent && mainDeserved == null
    val isError = !patient.isDependent && mainDeserved != null && mainDeserved.message.contains("عدم")

    val colors = LocalTaminColors.current
    val gradient = if (patient.isDependent) DependantCardGradient else MainPatientCardGradient

    // Fixed height so dependant cards match the main one: they carry fewer rows, and a
    // wrap-content card would leave the pager showing ragged neighbours.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(PatientCardHeight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(Spacing.lg)
        ) {
            Text(
                text = if (patient.isDependent) {
                    "کارت الکترونیک بیمهٔ درمان — تحت تکفل"
                } else {
                    "کارت الکترونیک بیمهٔ درمان"
                },
                style = MaterialTheme.typography.labelSmall,
                color = colors.bgSurface
            )
            Spacer(modifier = Modifier.height(Spacing.md))
            Text(
                text = patient.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.bgSurface
            )
            Text(
                text = "کد ملی: ${patient.nationalId}",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.bgSurface
            )
            patient.brhName?.let {
                Text(
                    text = "شعبه: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.bgSurface
                )
            }
            patient.insuranceType?.let {
                Text(
                    text = "نوع بیمه: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.bgSurface
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))
            HorizontalDivider(color = colors.bgSurface.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = when {
                            isLoading -> Icons.Default.Refresh
                            isError -> Icons.Default.Close
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = colors.bgSurface,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = when {
                            isLoading -> "در حال استعلام وضعیت استحقاق..."
                            isError -> "وضعیت حمایت‌های درمانی: فاقد استحقاق"
                            else -> "وضعیت حمایت‌های درمانی: برخوردار هستید"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.bgSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (isError && mainDeserved.message.isNotEmpty()) {
                    TextButton(onClick = { onShowDetails(mainDeserved.message) }) {
                        Text("علت", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickAccessSection(
    isHealthProfileCompleted: Boolean?,
    onOpenRecords: () -> Unit,
    onOpenHealthProfile: () -> Unit,
    onOpenCenters: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md)) {
        SectionLabel("دسترسی سریع")

        TreatmentRow(
            title = "سوابق درمانی من",
            subtitle = "تاریخچهٔ نسخه، ویزیت، پاراکلینیک و آزمایش",
            icon = Icons.AutoMirrored.Filled.List,
            emphasised = true,
            onClick = onOpenRecords
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        TreatmentRow(
            title = "پروندهٔ سلامت من",
            subtitle = "خوداظهاری‌های سلامت و اطلاعات پزشکی",
            icon = Icons.Default.FavoriteBorder,
            // No badge until the health sub-flow reports the status.
            badge = when (isHealthProfileCompleted) {
                true -> "تکمیل شده"
                false -> "تکمیل نشده"
                null -> null
            },
            onClick = onOpenHealthProfile
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        TreatmentRow(
            title = "مراکز درمانی طرف قرارداد",
            subtitle = "جست‌وجوی بیمارستان و داروخانه",
            icon = Icons.Default.LocationOn,
            onClick = onOpenCenters
        )
    }
}

/** The three service tiles, laid out as one row of equal columns. */
@Composable
private fun ServiceGridSection(
    onOpenPrescriptions: () -> Unit,
    onOpenConfirmations: () -> Unit,
    onOpenCosts: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        ServiceTile(
            title = "خسارت متفرقه",
            icon = Icons.Default.ShoppingCart,
            onClick = onOpenCosts,
            modifier = Modifier.weight(1f)
        )
        ServiceTile(
            title = "تاییدیه‌های پزشکی",
            icon = Icons.Default.CheckCircle,
            onClick = onOpenConfirmations,
            modifier = Modifier.weight(1f)
        )
        ServiceTile(
            title = "نسخه‌های الکترونیک",
            icon = Icons.AutoMirrored.Filled.List,
            onClick = onOpenPrescriptions,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ServiceTile(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md, horizontal = Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Current-year treatment spend.
 *
 * The totals endpoint belongs to the costs sub-flow and is not on this branch, so the shares read
 * as placeholders until it lands.
 */
@Composable
private fun YearlyCostSection(
    organisationShare: Long? = null,
    insuredShare: Long? = null
) {
    Column(modifier = Modifier.padding(Spacing.lg)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                Text(
                    text = "هزینه‌های سال جاری",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    ShareCell(
                        label = "سهم سازمان",
                        value = organisationShare,
                        modifier = Modifier.weight(1f)
                    )
                    ShareCell(
                        label = "سهم بیمه‌شده",
                        value = insuredShare,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ShareCell(label: String, value: Long?, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value?.toPriceFormat() ?: "—",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = LocalTaminColors.current.textSecondary,
        modifier = Modifier.padding(bottom = Spacing.sm)
    )
}

/** One tappable entry in the hub. [badge] renders a trailing status chip when present. */
@Composable
private fun TreatmentRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    badge: String? = null,
    emphasised: Boolean = false
) {
    val colors = LocalTaminColors.current
    val container = if (emphasised) colors.teal else colors.bgSurface
    val onContainer = if (emphasised) colors.bgSurface else colors.textPrimary

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = container)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = onContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = onContainer
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = onContainer.copy(alpha = 0.7f)
                )
            }
            badge?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.orangeText,
                    modifier = Modifier
                        .background(colors.orangeBg, MaterialTheme.shapes.small)
                        .padding(horizontal = Spacing.sm, vertical = 2.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@PreviewRtlTheme
@Composable
fun TreatmentScreenPreview() {
    PreviewRtlThemeContent {
        TreatmentContent(
            state = TreatmentMocks.mainUiState,
            onIntent = {},
            onNavigateToRecords = { _, _ -> }
        )
    }
}

/**
 * Patient card gradients, per the درمان design: teal→blue for the insured, purple for dependants.
 *
 * Defined here rather than in [com.tamin.taminhamrah.ui.theme.TaminColors] because the shared
 * palette has no light-mode equivalent yet (its teal→blue lives only in the dark heroGradient) and
 * the cards are owned by this screen.
 */
private val MainPatientCardGradient = Brush.linearGradient(
    listOf(Color(0xFF10AEB9), Color(0xFF1E6FD0))
)

private val DependantCardGradient = Brush.linearGradient(
    listOf(TaminPurple500, TaminPurple700)
)

/** Every carousel card is this tall, whoever the patient is. */
private val PatientCardHeight = 180.dp
