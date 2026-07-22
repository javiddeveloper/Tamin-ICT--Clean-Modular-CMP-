package com.tamin.taminhamrah.feature.treatment.ui.records

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentViewModel
import com.tamin.taminhamrah.feature.treatment.ui.components.CostTotalsBar
import com.tamin.taminhamrah.feature.treatment.ui.components.MedicalRecordCard
import com.tamin.taminhamrah.feature.treatment.ui.components.RecordGroupHeader
import com.tamin.taminhamrah.feature.treatment.ui.components.TimelineFilterBar
import com.tamin.taminhamrah.feature.treatment.ui.components.TreatmentFilterChipRow
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsUiState
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentIntent
import com.tamin.taminhamrah.feature.treatment.ui.model.PatientItem
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordPeriod
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordSearchCriteria
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.feature.treatment.ui.model.toJalaliDateLabel
import com.tamin.taminhamrah.feature.treatment.ui.model.toJalaliMonthLabel
import com.tamin.taminhamrah.feature.treatment.ui.model.toPatientList
import com.tamin.taminhamrah.feature.treatment.ui.prescriptions.PrescriptionsViewModel
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPR
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPricePR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.icons.TaminIcons
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.flow.Flow
import org.koin.compose.viewmodel.koinViewModel

/** Shown on the person chip until the patient list arrives. */
private const val SELF_LABEL = "خودم"

/** Which filter panel is open over the list; only one shows at a time, or none. */
private enum class RecordFilter { PATIENT, PERIOD }

/** A filter chip toggles its own panel: open it, or close it if it is already the open one. */
private fun RecordFilter?.toggle(target: RecordFilter): RecordFilter? =
    if (this == target) null else target

/**
 * The full-width filter chooser that drops below the bar.
 *
 * Rendered as an overlay in the content with a [Scrim] behind it, so it floats over the list,
 * dismisses on an outside tap, and always lands in the same place regardless of which chip opened
 * it — unlike a menu anchored to one trigger.
 */
@Composable
private fun <T> RecordFilterPanel(
    options: List<Pair<T, String>>,
    isSelected: (T) -> Boolean,
    onSelect: (T) -> Unit,
) {
    val colors = LocalTaminColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.sm),
        shape = RoundedCornerShape(CornerRadius.card),
        color = colors.bgSurface,
        border = BorderStroke(1.dp, colors.border),
        shadowElevation = Elevation.md,
    ) {
        Column {
            options.forEach { (value, label) ->
                val selected = isSelected(value)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(value) }
                        .background(if (selected) colors.greenBg else colors.bgSurface)
                        .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selected) colors.teal else colors.textPrimary,
                    )
                    if (selected) {
                        Icon(
                            imageVector = TaminIcons.Check,
                            contentDescription = null,
                            tint = colors.teal,
                            modifier = Modifier.size(IconSize.small),
                        )
                    }
                }
            }
        }
    }
}

/** A transparent full-size catch layer: a tap anywhere behind the panel closes it. */
@Composable
private fun Scrim(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onDismiss,
            ),
    )
}

/** The date trigger reads back the chosen custom range instead of the bare "تاریخ دلخواه". */
private fun periodLabel(period: RecordPeriod, customRange: Pair<String, String>?): String {
    if (period != RecordPeriod.CUSTOM || customRange == null) return period.label
    val from = PersianDateFormatter.formatTimestamp(customRange.first.toLongOrNull())
    val to = PersianDateFormatter.formatTimestamp(customRange.second.toLongOrNull())
    return "$from - $to"
}

/**
 * Failure state for the records list: what went wrong and how to recover, nothing more.
 *
 * No retry button — the list is pull-to-refresh, so one gesture both reloads a good list and
 * recovers from a failure, instead of the screen offering two ways to do the same thing.
 */
@Composable
private fun RecordsErrorState(message: String) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(Spacing.page),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = TaminIcons.Cross,
            contentDescription = null,
            tint = colors.dangerText,
            modifier = Modifier.size(IconSize.large),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "برای تلاش دوباره، صفحه را به پایین بکشید.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

/** The one place the list request is built, so a retry always repeats the current filters. */
private fun retryIntent(
    nationalCode: String,
    tab: RecordTab,
    period: RecordPeriod,
    customRange: Pair<String, String>? = null,
) = PrescriptionsIntent.LoadList(
    nationalCode = nationalCode,
    requestTypeIds = tab.requestTypeIds,
    startDate = customRange?.first ?: period.startTimestamp(),
    endDate = customRange?.second,
)

/** Shown while a total has not loaded, so a blank never reads as zero spend. */
private const val UNKNOWN_AMOUNT = "—"

/**
 * Medical records ("سوابق درمانی"): patient and period filters, the category chips, and the
 * records timeline grouped by month.
 *
 * Prescriptions are a category here rather than a screen of their own — the hub's
 * «نسخه‌های الکترونیک» tile opens this on [RecordTab.MEDICINE].
 */
@Composable
fun MedicalRecordsScreen(
    nationalCode: String,
    initialTab: RecordTab,
    onBack: () -> Unit,
    onOpenRecord: (record: ElectronicPrescriptionPR, patientNationalCode: String) -> Unit,
    viewModel: PrescriptionsViewModel = koinViewModel(),
    treatmentViewModel: TreatmentViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val treatmentState by treatmentViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Self-contained like the health screen: it owns the patient list rather than being handed it,
    // (re)loading the dashboard's dependants itself so the nav graph passes only the route args.
    LaunchedEffect(Unit) {
        if (treatmentState.mainUserNationalCode == null) {
            treatmentViewModel.sendIntent(TreatmentIntent.InitTreatmentFlow)
        }
    }
    val patients = remember(treatmentState) { treatmentState.toPatientList() }

    // A shortcut opens this with no code; fall back to whoever the dashboard has selected.
    val effectiveNationalCode = nationalCode.ifBlank {
        treatmentState.selectedNationalCode ?: treatmentState.mainUserNationalCode ?: ""
    }

    var selectedPatient by remember(effectiveNationalCode) { mutableStateOf(effectiveNationalCode) }
    var selectedPeriod by remember { mutableStateOf(RecordPeriod.LAST_SIX_MONTHS) }
    var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }
    // Set only by the تاریخ دلخواه picker; null means the selected preset decides the range.
    var customRange by remember { mutableStateOf<Pair<String, String>?>(null) }
    var searchCriteria by remember { mutableStateOf(RecordSearchCriteria()) }

    // Patient, period and category are all endpoint parameters, so any change re-queries.
    // Held until a patient resolves, so the shortcut path never fires a blank-code query.
    LaunchedEffect(selectedPatient, selectedPeriod, selectedTab, customRange) {
        if (selectedPatient.isBlank()) return@LaunchedEffect
        viewModel.sendIntent(retryIntent(selectedPatient, selectedTab, selectedPeriod, customRange))
    }

    // Prices cost one request per record, so they are only fetched once a cost bound is set and
    // only for records that do not already have one.
    LaunchedEffect(searchCriteria, state.prescriptionList) {
        if (!searchCriteria.filtersOnAmount) return@LaunchedEffect
        val missing = state.prescriptionList
            .map { it.noteHeadEprescID }
            .filter { it !in state.recordPrices }
        if (missing.isNotEmpty()) {
            viewModel.sendIntent(
                PrescriptionsIntent.LoadRecordPrices(missing, selectedPatient),
            )
        }
    }

    HandleRecordsEvents(events = viewModel.events, snackbarHostState = snackbarHostState)

    MedicalRecordsContent(
        state = state,
        patients = patients,
        selectedPatient = selectedPatient,
        selectedPeriod = selectedPeriod,
        customRange = customRange,
        selectedTab = selectedTab,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onTabSelected = { selectedTab = it },
        onPatientSelected = { selectedPatient = it },
        onPeriodSelected = { selectedPeriod = it },
        onRecordSelected = { onOpenRecord(it, selectedPatient) },
        onRetry = {
            viewModel.sendIntent(retryIntent(selectedPatient, selectedTab, selectedPeriod, customRange))
        },
        onCustomRangePicked = { start, end ->
            customRange = start to end
            selectedPeriod = RecordPeriod.CUSTOM
        },
        searchCriteria = searchCriteria,
        onSearchApplied = { criteria ->
            searchCriteria = criteria
            selectedTab = criteria.tab
            // A searched date range replaces the period preset, so both cannot disagree.
            if (criteria.startDate != null && criteria.endDate != null) {
                customRange = criteria.startDate to criteria.endDate
                selectedPeriod = RecordPeriod.CUSTOM
            }
        },
    )
}

@Composable
fun HandleRecordsEvents(
    events: Flow<PrescriptionsEvent>,
    snackbarHostState: SnackbarHostState,
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
    patients: List<PatientItem>,
    selectedPatient: String,
    selectedPeriod: RecordPeriod,
    customRange: Pair<String, String>?,
    selectedTab: RecordTab,
    onBack: () -> Unit,
    onTabSelected: (RecordTab) -> Unit,
    onPatientSelected: (String) -> Unit,
    onPeriodSelected: (RecordPeriod) -> Unit,
    onRecordSelected: (ElectronicPrescriptionPR) -> Unit,
    onRetry: () -> Unit,
    onCustomRangePicked: (startDate: String, endDate: String) -> Unit,
    searchCriteria: RecordSearchCriteria,
    onSearchApplied: (RecordSearchCriteria) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val colors = LocalTaminColors.current
    var openFilter by remember { mutableStateOf<RecordFilter?>(null) }
    var showSearchSheet by remember { mutableStateOf(false) }
    var pickingRangeStart by remember { mutableStateOf(false) }
    var rangeStart by remember { mutableStateOf<String?>(null) }

    val currentPatient = patients.firstOrNull { it.nationalId == selectedPatient }

    if (showSearchSheet) {
        RecordSearchSheet(
            initial = searchCriteria,
            onDismiss = { showSearchSheet = false },
            onApply = {
                onSearchApplied(it)
                showSearchSheet = false
            },
        )
    }

    if (pickingRangeStart) {
        TaminJalaliDatePicker(
            title = "از تاریخ",
            onDismiss = { pickingRangeStart = false },
            onConfirm = { y, m, d ->
                rangeStart = PersianDateFormatter.toEpochMillis(y, m, d).toString()
                pickingRangeStart = false
            },
        )
    }

    // The second step opens as soon as the first date is chosen; the old app refused to search
    // with only one end of the range, so both are required before the query runs.
    rangeStart?.let { start ->
        TaminJalaliDatePicker(
            title = "تا تاریخ",
            onDismiss = { rangeStart = null },
            onConfirm = { y, m, d ->
                onCustomRangePicked(start, PersianDateFormatter.toEpochMillis(y, m, d).toString())
                rangeStart = null
            },
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = colors.bgPage,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = "سوابق درمانی",
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = TaminIcons.ChevronBack,
                        contentDescription = "بازگشت",
                        onClick = onBack,
                    )
                },
            ) {
                TimelineFilterBar(
                    // Never blank: the insured person is the default until the list arrives.
                    personLabel = currentPatient?.filterLabel ?: SELF_LABEL,
                    dateLabel = periodLabel(selectedPeriod, customRange),
                    dropdownIcon = TaminIcons.ChevronBack,
                    searchIcon = TaminIcons.Search,
                    onPersonClick = { openFilter = openFilter.toggle(RecordFilter.PATIENT) },
                    onDateClick = { openFilter = openFilter.toggle(RecordFilter.PERIOD) },
                    onSearchClick = { showSearchSheet = true },
                    personExpanded = openFilter == RecordFilter.PATIENT,
                    dateExpanded = openFilter == RecordFilter.PERIOD,
                )
            }
        },
        bottomBar = { RecordsTotals(prices = state.prescriptionPriceList) },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = onRetry,
            modifier = Modifier.fillMaxSize(),
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            TreatmentFilterChipRow(
                categories = RecordTab.chips.map { it.label },
                selectedIndex = RecordTab.chips.indexOf(selectedTab).coerceAtLeast(0),
                onSelect = { onTabSelected(RecordTab.chips[it]) },
            )

            when {
                // A failed request and a genuinely empty result read very differently, so they
                // get different states. Both recover the same way: pull to refresh.
                state.error != null -> RecordsErrorState(message = state.error)

                state.prescriptionList.none { searchCriteria.matches(it, state.recordPrices) } -> TaminEmptyState(
                    message = if (searchCriteria.nameQuery.isNotBlank()) {
                        "موردی با «${searchCriteria.nameQuery}» یافت نشد."
                    } else {
                        "در بازهٔ انتخاب‌شده، سابقهٔ «${selectedTab.label}» ثبت نشده است."
                    },
                )

                state.prescriptionList.isEmpty() -> TaminEmptyState(
                    message = "در بازهٔ انتخاب‌شده، سابقهٔ «${selectedTab.label}» ثبت نشده است.",
                )

                else -> RecordTimeline(
                    records = state.prescriptionList.filter { searchCriteria.matches(it, state.recordPrices) },
                    onRecordSelected = onRecordSelected,
                )
            }
        }
        }

        // Both filters share one full-width panel that drops below the bar, so neither can anchor
        // to the wrong chip. A tap on the scrim behind it dismisses.
        openFilter?.let { filter ->
            Scrim(onDismiss = { openFilter = null })
            when (filter) {
                RecordFilter.PATIENT -> RecordFilterPanel(
                    options = patients
                        .map { it.nationalId to it.filterLabel }
                        .ifEmpty { listOf(selectedPatient to SELF_LABEL) },
                    isSelected = { it == selectedPatient },
                    onSelect = {
                        onPatientSelected(it)
                        openFilter = null
                    },
                )

                RecordFilter.PERIOD -> RecordFilterPanel(
                    options = RecordPeriod.entries.map { it to it.label },
                    isSelected = { it == selectedPeriod },
                    onSelect = { period ->
                        openFilter = null
                        if (period == RecordPeriod.CUSTOM) pickingRangeStart = true
                        else onPeriodSelected(period)
                    },
                )
            }
        }
        }
    }
}

/** Records grouped under their Jalali month, as the design shows. */
@Composable
private fun RecordTimeline(
    records: List<ElectronicPrescriptionPR>,
    onRecordSelected: (ElectronicPrescriptionPR) -> Unit,
) {
    val groups = remember(records) { records.groupBy { it.prescDate.toJalaliMonthLabel() } }

    groups.forEach { (monthLabel, monthRecords) ->
        RecordGroupHeader(text = monthLabel)
        Column(
            modifier = Modifier.padding(horizontal = Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
        ) {
            monthRecords.forEach { record ->
                val accent = recordAccent(record.prescType)
                MedicalRecordCard(
                    category = record.prescType.toCategoryLabel(),
                    date = record.prescDate.toJalaliDateLabel(),
                    title = "دکتر ${record.docName}",
                    subtitle = record.location.ifBlank { record.specDesc },
                    // The list endpoint carries no per-record amount — the old app's response has
                    // no payment field either — so the share reads as unknown until the detail's
                    // price lookup runs. Never show another number here: the label says «سهم شما».
                    shareAmount = UNKNOWN_AMOUNT,
                    accentColor = accent.content,
                    accentContainerColor = accent.container,
                    categoryIcon = accent.icon,
                    onClick = { onRecordSelected(record) },
                )
            }
        }
        Box(modifier = Modifier.height(Spacing.sm))
    }
}

/** Totals pinned under the list; hidden until a price lookup has returned. */
@Composable
private fun RecordsTotals(prices: List<ElectronicPrescriptionPricePR>) {
    if (prices.isEmpty()) return
    CostTotalsBar(
        insuredShareLabel = "سهم بیمه‌شده",
        insuredShareAmount = prices.totalOf { it.headInsuPayment },
        organizationShareLabel = "سهم سازمان",
        organizationShareAmount = prices.totalOf { it.headSsoPayment },
        totalLabel = "جمع کل",
        totalAmount = prices.totalOf { it.requestPrice },
    )
}

/** One dropdown shape for both filters, so they stay visually identical. */
@Composable
private fun <T> FilterMenu(
    expanded: Boolean,
    options: List<Pair<T, String>>,
    isSelected: (T) -> Boolean,
    onDismiss: () -> Unit,
    onSelect: (T) -> Unit,
) {
    val colors = LocalTaminColors.current
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        options.forEach { (value, label) ->
            DropdownMenuItem(
                text = { Text(text = label, color = colors.textPrimary) },
                onClick = { onSelect(value) },
                trailingIcon = {
                    if (isSelected(value)) {
                        Icon(
                            imageVector = TaminIcons.Check,
                            contentDescription = null,
                            tint = colors.teal,
                        )
                    }
                },
            )
        }
    }
}

/** Palette and glyph a record category is drawn with, all from the theme. */
private data class RecordAccent(
    val content: Color,
    val container: Color,
    val icon: ImageVector,
)

@Composable
private fun recordAccent(prescType: String): RecordAccent {
    val colors = LocalTaminColors.current
    return when (prescType) {
        in RecordTab.VISIT.requestTypeIds ->
            RecordAccent(colors.orangeText, colors.orangeBg, TaminIcons.HealthProfile)
        in RecordTab.PARACLINIC.requestTypeIds ->
            RecordAccent(colors.blueText, colors.blueBg, TaminIcons.MedicalApprovals)
        RecordTab.medicalServiceTypeId, RecordTab.pharmacyTypeId ->
            RecordAccent(colors.greenText, colors.greenBg, TaminIcons.MedicalCenters)
        else ->
            RecordAccent(colors.teal, colors.greenBg, TaminIcons.Prescriptions)
    }
}

/**
 * Sums a money column, which the API reports as strings.
 *
 * Returns [UNKNOWN_AMOUNT] for an empty list: totals only arrive per opened record, so nothing
 * loaded means "not known yet" rather than a genuine zero.
 */
private fun List<ElectronicPrescriptionPricePR>.totalOf(
    selector: (ElectronicPrescriptionPricePR) -> String,
): String {
    if (isEmpty()) return UNKNOWN_AMOUNT
    return sumOf { selector(it).toLongOrNull() ?: 0L }.toPriceFormat()
}

@PreviewRtlTheme
@Composable
fun MedicalRecordsPreview() {
    PreviewRtlThemeContent {
        MedicalRecordsContent(
            state = TreatmentMocks.prescriptionsUiState,
            patients = TreatmentMocks.mainUiState.toPatientList(),
            selectedPatient = "1234567890",
            selectedPeriod = RecordPeriod.LAST_SIX_MONTHS,
            customRange = null,
            selectedTab = RecordTab.MEDICINE,
            onBack = {},
            onTabSelected = {},
            onPatientSelected = {},
            onPeriodSelected = {},
            onRecordSelected = {},
            onRetry = {},
            onCustomRangePicked = { _, _ -> },
            searchCriteria = RecordSearchCriteria(),
            onSearchApplied = {},
        )
    }
}

/** Turns the endpoint's numeric category into its Persian name; unknown ids show as-is. */
private fun String.toCategoryLabel(): String =
    RecordTab.entries.firstOrNull { it != RecordTab.ALL && this in it.requestTypeIds }?.label
        ?: RecordTab.labelForTypeId(this)
        ?: this
