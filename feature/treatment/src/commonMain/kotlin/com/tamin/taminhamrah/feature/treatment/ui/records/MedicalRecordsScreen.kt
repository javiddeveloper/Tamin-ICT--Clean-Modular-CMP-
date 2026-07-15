package com.tamin.taminhamrah.feature.treatment.ui.records

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.treatment.ui.components.EmptyState
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.PatientItem
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.feature.treatment.ui.model.toPatientItems
import com.tamin.taminhamrah.feature.treatment.ui.prescriptions.PrescriptionsViewModel
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.getBeginningTimestamp
import com.tamin.taminhamrah.util.getOneMonthAgoTimestamp
import com.tamin.taminhamrah.util.getOneYearAgoTimestamp
import com.tamin.taminhamrah.util.getSixMonthsAgoTimestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

/**
 * Record categories of سوابق درمانی.
 *
 * Only [MEDICINE] has an endpoint on this branch; the others are declared so the tab row matches
 * the design and each can be wired as its API lands.
 */
@Serializable
enum class RecordTab(val label: String) {
    ALL("همه"),
    MEDICINE("دارو"),
    VISIT("ویزیت"),
    PARACLINIC("پاراکلینیک")
}

/**
 * Ranges offered by the date filter. [ALL_TIME] asks the endpoint for the full history.
 *
 * «تاریخ دلخواه» (a custom from/to range) is not offered yet: [PrescriptionsIntent.LoadList] already
 * accepts explicit bounds, so it only needs a picker to supply them.
 */
enum class RecordPeriod(val label: String) {
    LAST_MONTH("۱ ماه اخیر"),
    LAST_SIX_MONTHS("۶ ماه اخیر"),
    LAST_YEAR("۱ سال اخیر"),
    ALL_TIME("از ابتدا");

    /** Start bound in epoch millis, as the patient-history endpoint expects. */
    fun startTimestamp(): String = when (this) {
        LAST_MONTH -> getOneMonthAgoTimestamp()
        LAST_SIX_MONTHS -> getSixMonthsAgoTimestamp()
        LAST_YEAR -> getOneYearAgoTimestamp()
        ALL_TIME -> getBeginningTimestamp()
    }
}

/**
 * Medical records ("سوابق درمانی"): patient/period filters, category tabs and the records list.
 *
 * Prescriptions are a tab here rather than a screen of their own — the hub's «نسخه‌های الکترونیک»
 * tile opens this on [RecordTab.MEDICINE]. Layout structure only; the design pass owns colors and
 * the shared card/header components.
 */
@Composable
fun MedicalRecordsScreen(
    nationalCode: String,
    initialTab: RecordTab,
    patients: List<PatientItem>,
    onBack: () -> Unit,
    onOpenRecord: (String) -> Unit,
    viewModel: PrescriptionsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedPatient by remember(nationalCode) { mutableStateOf(nationalCode) }
    var selectedPeriod by remember { mutableStateOf(RecordPeriod.LAST_SIX_MONTHS) }

    // Re-queries whenever the patient or the range changes; both are endpoint parameters.
    LaunchedEffect(selectedPatient, selectedPeriod) {
        viewModel.sendIntent(
            PrescriptionsIntent.LoadList(
                nationalCode = selectedPatient,
                startDate = selectedPeriod.startTimestamp()
            )
        )
    }

    HandleRecordsEvents(events = viewModel.events, snackbarHostState = snackbarHostState)

    MedicalRecordsContent(
        state = state,
        initialTab = initialTab,
        patients = patients,
        selectedPatient = selectedPatient,
        selectedPeriod = selectedPeriod,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onPatientSelected = { selectedPatient = it },
        onPeriodSelected = { selectedPeriod = it },
        onRecordSelected = onOpenRecord
    )
}

@Composable
fun HandleRecordsEvents(
    events: Flow<PrescriptionsEvent>,
    snackbarHostState: SnackbarHostState
) {
    events.collectWithLifecycleAware {
        when (it) {
            is PrescriptionsEvent.ShowToast -> snackbarHostState.showSnackbar(it.message)
        }
    }
}

@Composable
fun MedicalRecordsContent(
    state: PrescriptionsUiState,
    initialTab: RecordTab,
    patients: List<PatientItem>,
    selectedPatient: String,
    selectedPeriod: RecordPeriod,
    onBack: () -> Unit,
    onPatientSelected: (String) -> Unit,
    onPeriodSelected: (RecordPeriod) -> Unit,
    onRecordSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("سوابق درمانی", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = { RecordsCostSummaryBar(state = state) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            RecordFilterRow(
                patients = patients,
                selectedPatient = selectedPatient,
                selectedPeriod = selectedPeriod,
                onPatientSelected = onPatientSelected,
                onPeriodSelected = onPeriodSelected
            )
            RecordTabRow(selected = selectedTab, onSelect = { selectedTab = it })

            when (selectedTab) {
                RecordTab.ALL, RecordTab.MEDICINE -> RecordList(
                    state = state,
                    onRecordSelected = onRecordSelected
                )
                // Visits and paraclinic have no endpoint yet; their branches fill these in.
                RecordTab.VISIT, RecordTab.PARACLINIC -> EmptyState("این بخش به‌زودی افزوده می‌شود.")
            }
        }
    }
}

/**
 * Patient and period filters.
 *
 * Both feed the patient-history query: the patient picks the national code, the period the start
 * bound. Selecting either re-runs the request.
 */
@Composable
private fun RecordFilterRow(
    patients: List<PatientItem>,
    selectedPatient: String,
    selectedPeriod: RecordPeriod,
    onPatientSelected: (String) -> Unit,
    onPeriodSelected: (RecordPeriod) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PatientFilter(
            patients = patients,
            selectedPatient = selectedPatient,
            onPatientSelected = onPatientSelected,
            modifier = Modifier.weight(1f)
        )
        PeriodFilter(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodSelected,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {}) {
            Icon(
                Icons.Default.Search,
                contentDescription = "جست‌وجو",
                tint = LocalTaminColors.current.textSecondary
            )
        }
    }
}

/** Chooses whose records to show: the insured themselves or one of their dependants. */
@Composable
private fun PatientFilter(
    patients: List<PatientItem>,
    selectedPatient: String,
    onPatientSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val current = patients.firstOrNull { it.nationalId == selectedPatient }

    Box(modifier = modifier) {
        AssistChip(
            onClick = { expanded = true },
            label = {
                Text(
                    text = current?.filterLabel ?: "خودم",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            patients.forEach { patient ->
                DropdownMenuItem(
                    text = { Text(patient.filterLabel) },
                    onClick = {
                        onPatientSelected(patient.nationalId)
                        expanded = false
                    },
                    trailingIcon = {
                        if (patient.nationalId == selectedPatient) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = LocalTaminColors.current.teal
                            )
                        }
                    }
                )
            }
        }
    }
}

/** Chooses how far back to query. */
@Composable
private fun PeriodFilter(
    selectedPeriod: RecordPeriod,
    onPeriodSelected: (RecordPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        AssistChip(
            onClick = { expanded = true },
            label = {
                Text(text = selectedPeriod.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            RecordPeriod.entries.forEach { period ->
                DropdownMenuItem(
                    text = { Text(period.label) },
                    onClick = {
                        onPeriodSelected(period)
                        expanded = false
                    },
                    trailingIcon = {
                        if (period == selectedPeriod) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = LocalTaminColors.current.teal
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RecordTabRow(selected: RecordTab, onSelect: (RecordTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        RecordTab.entries.forEach { tab ->
            FilterChip(
                selected = tab == selected,
                onClick = { onSelect(tab) },
                label = { Text(tab.label) }
            )
        }
    }
}

@Composable
private fun RecordList(
    state: PrescriptionsUiState,
    onRecordSelected: (String) -> Unit
) {
    when {
        state.isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        state.prescriptionList.isEmpty() -> EmptyState("هیچ سابقه‌ای یافت نشد.")

        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            items(state.prescriptionList) { record ->
                RecordCard(record = record, onOpenDetails = { onRecordSelected(record.trackingCode) })
            }
        }
    }
}

/** One record: category badge, date, prescriber, location and the tracking code. */
@Composable
private fun RecordCard(
    record: ElectronicPrescriptionPR,
    onOpenDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = record.prescType,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 2.dp)
                    )
                }
                Text(
                    text = record.prescDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "دکتر ${record.docName}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = record.location.ifEmpty { record.specDesc },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onOpenDetails) {
                    Text("جزئیات")
                }
                Text(
                    text = "کد رهگیری: ${record.trackingCode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Totals pinned under the list. Hidden until a price lookup has returned. */
@Composable
private fun RecordsCostSummaryBar(state: PrescriptionsUiState) {
    val price = state.prescriptionPriceList.firstOrNull() ?: return

    Surface(tonalElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            CostCell(label = "جمع کل", value = price.requestPrice, modifier = Modifier.weight(1f))
            CostCell(label = "سهم سازمان", value = price.headSsoPayment, modifier = Modifier.weight(1f))
            CostCell(label = "سهم بیمه‌شده", value = price.headInsuPayment, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CostCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value.toPriceFormat(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@PreviewRtlTheme
@Composable
fun MedicalRecordsPreview() {
    PreviewRtlThemeContent {
        MedicalRecordsContent(
            state = TreatmentMocks.prescriptionsUiState,
            initialTab = RecordTab.MEDICINE,
            patients = TreatmentMocks.mainUiState.toPatientItems(),
            selectedPatient = "",
            selectedPeriod = RecordPeriod.LAST_SIX_MONTHS,
            onBack = {},
            onPatientSelected = {},
            onPeriodSelected = {},
            onRecordSelected = {}
        )
    }
}
