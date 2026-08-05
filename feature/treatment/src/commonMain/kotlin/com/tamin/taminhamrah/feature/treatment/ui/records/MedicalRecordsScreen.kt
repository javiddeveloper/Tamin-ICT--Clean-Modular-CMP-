package com.tamin.taminhamrah.feature.treatment.ui.records

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.tamin.taminhamrah.feature.treatment.ui.model.PatientItemPR
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordPeriod
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordSearchCriteria
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.feature.treatment.ui.model.rememberJalaliMonthNames
import com.tamin.taminhamrah.feature.treatment.ui.model.rememberRecordTabLabels
import com.tamin.taminhamrah.feature.treatment.ui.model.toCategoryLabel
import com.tamin.taminhamrah.feature.treatment.ui.model.toJalaliDateLabel
import com.tamin.taminhamrah.feature.treatment.ui.model.toJalaliMonthLabel
import com.tamin.taminhamrah.feature.treatment.ui.model.toPatientList
import com.tamin.taminhamrah.feature.treatment.ui.prescriptions.PrescriptionsViewModel
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPR
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPricePR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.StaggeredEntranceState
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.amount_total
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_health_profile
import taminx.core.core_ui.ic_tamin_medical_approvals
import taminx.core.core_ui.ic_tamin_medical_centers
import taminx.core.core_ui.ic_tamin_prescriptions
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.patient_dependant_relation
import taminx.core.core_ui.patient_main_insured_fallback
import taminx.core.core_ui.records_date_from
import taminx.core.core_ui.records_date_to
import taminx.core.core_ui.records_doctor_named
import taminx.core.core_ui.records_empty_period
import taminx.core.core_ui.records_empty_search
import taminx.core.core_ui.records_self
import taminx.core.core_ui.records_title
import taminx.core.core_ui.share_insured
import taminx.core.core_ui.share_organization

/** Shown on the person chip until the patient list arrives. */

/** Which filter panel is open over the list; only one shows at a time, or none. */
private enum class RecordFilter { PATIENT, PERIOD }

/** A filter chip toggles its own panel: open it, or close it if it is already the open one. */
private fun RecordFilter?.toggle(target: RecordFilter): RecordFilter? =
    if (this == target) null else target

/**
 * The period chooser's rows.
 *
 * Keyed on the resolved labels, so the panel gets the same [ImmutableList] instance on every
 * recomposition — the presets themselves never change at runtime.
 */
@Composable
private fun rememberPeriodOptions(): ImmutableList<Pair<RecordPeriod, String>> {
    val labels = RecordPeriod.entries.map { stringResource(it.label) }
    return remember(labels) {
        RecordPeriod.entries.mapIndexed { index, period -> period to labels[index] }
            .toImmutableList()
    }
}

/**
 * The chooser behind a filter chip.
 *
 * Material's own menu rather than a panel of our own: it anchors to the chip that opened it,
 * animates out of that anchor, and brings the platform's outside-tap and back handling with it.
 */
@Composable
private fun <T> RecordFilterMenu(
    expanded: Boolean,
    options: ImmutableList<Pair<T, String>>,
    isSelected: (T) -> Boolean,
    onDismiss: () -> Unit,
    onSelect: (T) -> Unit,
) {
    val colors = LocalTaminColors.current
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        // The menu keeps the card surface the panel had: menu defaults are a tighter radius and
        // a tonal fill, which read as a system menu dropped onto the screen rather than as ours.
        shape = RoundedCornerShape(CornerRadius.card),
        containerColor = colors.bgSurface,
        border = BorderStroke(1.dp, colors.border),
        shadowElevation = Elevation.md,
    ) {
        options.forEach { (value, label) ->
            val selected = isSelected(value)
            DropdownMenuItem(
                modifier = if (selected) Modifier.background(colors.greenBg) else Modifier,
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selected) colors.teal else colors.textPrimary,
                    )
                },
                trailingIcon = if (!selected) {
                    null
                } else {
                    {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_tamin_check),
                            contentDescription = null,
                            tint = colors.teal,
                            modifier = Modifier.size(IconSize.small),
                        )
                    }
                },
                onClick = { onSelect(value) },
            )
        }
    }
}

/** The date trigger reads back the chosen custom range instead of the bare "تاریخ دلخواه". */
@Composable
private fun periodLabel(period: RecordPeriod, customRange: Pair<String, String>?): String {
    if (period != RecordPeriod.CUSTOM || customRange == null) return stringResource(period.label)
    val from = PersianDateFormatter.formatTimestamp(customRange.first.toLongOrNull())
    val to = PersianDateFormatter.formatTimestamp(customRange.second.toLongOrNull())
    return "$from - $to"
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

/** Where the period filter starts, and what clearing a custom range falls back to. */
private val DefaultRecordPeriod = RecordPeriod.LAST_SIX_MONTHS

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
    // Keyed on the data the list is built from, not the whole state: a selection or a cost total
    // arriving must not rebuild it and invalidate everything the patient chip feeds.
    // Resolved here because toPatientList runs inside the remember below, where composition —
    // and so a resource lookup — is not available.
    val mainInsuredFallback = stringResource(Res.string.patient_main_insured_fallback)
    val dependantRelation = stringResource(Res.string.patient_dependant_relation)

    val patients = remember(
        treatmentState.mainUserNationalCode,
        treatmentState.deservedList,
        treatmentState.dependantList,
        mainInsuredFallback,
        dependantRelation,
    ) {
        treatmentState.toPatientList(mainInsuredFallback, dependantRelation)
    }

    // A shortcut opens this with no code; fall back to whoever the dashboard has selected.
    val effectiveNationalCode = nationalCode.ifBlank {
        treatmentState.selectedNationalCode ?: treatmentState.mainUserNationalCode ?: ""
    }

    var selectedPatient by remember(effectiveNationalCode) { mutableStateOf(effectiveNationalCode) }
    var selectedPeriod by remember { mutableStateOf(DefaultRecordPeriod) }
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

    // «سهم شما» is not in the list response, so it comes from the price endpoint — one request per
    // record, only for records whose price is not already cached. Also feeds the cost filter.
    LaunchedEffect(state.prescriptionList) {
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
            selectedTab = criteria.tab
            // A searched range replaces the period preset, so the two can never disagree — and
            // clearing it hands control back to the preset. Without that second branch a range
            // cleared in the sheet keeps filtering the list with nothing on screen saying so.
            val range = criteria.resolvedRange()
            when {
                range != null -> {
                    customRange = range
                    selectedPeriod = RecordPeriod.CUSTOM
                }

                selectedPeriod == RecordPeriod.CUSTOM -> {
                    customRange = null
                    selectedPeriod = DefaultRecordPeriod
                }
            }
            // Keep the end that was filled in, so reopening the sheet shows the range actually
            // being queried rather than the half of it the person typed.
            searchCriteria = range
                ?.let { criteria.copy(startDate = it.first, endDate = it.second) }
                ?: criteria
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
    patients: ImmutableList<PatientItemPR>,
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

    // Resolved once, up here, because the blocks below read them from inside a remember.
    val selfLabel = stringResource(Res.string.records_self)
    val monthNames = rememberJalaliMonthNames()

    // The search runs over the whole list, so it is done once per input change rather than on
    // every recomposition — and once, not twice, since the empty check reads the same result.
    val visibleRecords = remember(state.prescriptionList, searchCriteria, state.recordPrices) {
        state.prescriptionList.filter { searchCriteria.matches(it, state.recordPrices) }
    }
    // The totals bar sums what is actually on screen, so it agrees with the cards above it. Keyed
    // on the same inputs as the list, so filtering re-totals and nothing else does.
    val visibleRecordPrices = remember(visibleRecords, state.recordPrices) {
        visibleRecords
            .mapNotNull { state.recordPrices[it.noteHeadEprescID] }
            .toImmutableList()
    }

    val recordGroups = remember(visibleRecords, monthNames) {
        visibleRecords.groupBy { it.prescDate.toJalaliMonthLabel(monthNames) }
    }

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
            title = stringResource(Res.string.records_date_from),
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
            title = stringResource(Res.string.records_date_to),
            onDismiss = { rangeStart = null },
            onConfirm = { y, m, d ->
                onCustomRangePicked(start, PersianDateFormatter.toEpochMillis(y, m, d).toString())
                rangeStart = null
            },
        )
    }

    val staggerState = rememberStaggeredEntranceState(key = selectedPatient to selectedPeriod to selectedTab)

    // Outside the list: a dialog cannot live in a LazyColumn item, and the failure is the only
    // thing worth attending to while it is up.
    ErrorStateView(message = state.error, onDismiss = onBack, onRetry = onRetry)

    Scaffold(
        modifier = modifier,
        containerColor = colors.bgPage,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.records_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = onBack,
                    )
                },
            ) {
                TimelineFilterBar(
                    // Never blank: the insured person is the default until the list arrives.
                    personLabel = currentPatient?.filterLabel(selfLabel) ?: selfLabel,
                    dateLabel = periodLabel(selectedPeriod, customRange),
                    dropdownIcon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    searchIcon = vectorResource(Res.drawable.ic_tamin_search),
                    onPersonClick = { openFilter = openFilter.toggle(RecordFilter.PATIENT) },
                    onDateClick = { openFilter = openFilter.toggle(RecordFilter.PERIOD) },
                    onSearchClick = { showSearchSheet = true },
                    personExpanded = openFilter == RecordFilter.PATIENT,
                    dateExpanded = openFilter == RecordFilter.PERIOD,
                    personMenu = {
                        RecordFilterMenu(
                            expanded = openFilter == RecordFilter.PATIENT,
                            options = remember(patients, selectedPatient, selfLabel) {
                                patients
                                    .map { it.nationalId to it.filterLabel(selfLabel) }
                                    .ifEmpty { listOf(selectedPatient to selfLabel) }
                                    .toImmutableList()
                            },
                            isSelected = { it == selectedPatient },
                            onDismiss = { openFilter = null },
                            onSelect = {
                                onPatientSelected(it)
                                openFilter = null
                            },
                        )
                    },
                    dateMenu = {
                        RecordFilterMenu(
                            expanded = openFilter == RecordFilter.PERIOD,
                            options = rememberPeriodOptions(),
                            isSelected = { it == selectedPeriod },
                            onDismiss = { openFilter = null },
                            onSelect = { period ->
                                openFilter = null
                                if (period == RecordPeriod.CUSTOM) pickingRangeStart = true
                                else onPeriodSelected(period)
                            },
                        )
                    },
                )
            }
        },
        bottomBar = {
            RecordsTotals(
                prices = visibleRecordPrices,
                hasRecords = visibleRecords.isNotEmpty(),
                isLoadingPrices = state.isLoadingPrices,
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
        // Lazy: a long history composes only the cards on screen, and a price arriving redraws
        // just the rows that show it.
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item(key = "categories") {
                TreatmentFilterChipRow(
                    // A remember-keyed factory, not a map here: a fresh list per recomposition
                    // is a changed argument, and the row would redraw on every list update.
                    categories = rememberRecordTabLabels(),
                    selectedIndex = RecordTab.chips.indexOf(selectedTab).coerceAtLeast(0),
                    onSelect = { onTabSelected(RecordTab.chips[it]) },
                )
            }

            when {
                state.isLoading && state.prescriptionList.isEmpty() ->
                    item { RecordsShimmerSkeleton() }

                // Guarded on error: a failed request cannot tell an empty period from an
                // unreachable one.
                state.error == null && visibleRecords.isEmpty() -> item {
                    TaminEmptyState(
                        message = if (searchCriteria.nameQuery.isNotBlank()) {
                            stringResource(Res.string.records_empty_search, searchCriteria.nameQuery)
                        } else {
                            stringResource(Res.string.records_empty_period, stringResource(selectedTab.label))
                        },
                    )
                }

                else -> recordTimeline(
                    groups = recordGroups,
                    prices = state.recordPrices,
                    isLoadingPrices = state.isLoadingPrices,
                    onRecordSelected = onRecordSelected,
                    staggerState = staggerState,
                )
            }
        }
        }
    }
}

/** Records grouped under their Jalali month, as the design shows. */
private fun LazyListScope.recordTimeline(
    groups: Map<String, List<ElectronicPrescriptionPR>>,
    prices: Map<String, ElectronicPrescriptionPricePR>,
    isLoadingPrices: Boolean,
    onRecordSelected: (ElectronicPrescriptionPR) -> Unit,
    staggerState: StaggeredEntranceState? = null,
) {
    groups.forEach { (monthLabel, monthRecords) ->
        item(key = monthLabel) { RecordGroupHeader(text = monthLabel) }

        // Deliberately unkeyed: a merged «همه» query could in principle repeat a record id, and a
        // duplicate key is a crash — position is identity enough for a list that reloads wholesale.
        itemsIndexed(monthRecords) { index, record ->
            val accent = recordAccent(record.prescType)
            MedicalRecordCard(
                // An id neither a tab nor the extra labels know shows as-is, rather than blank.
                category = record.prescType.toCategoryLabel()
                    ?.let { stringResource(it) }
                    ?: record.prescType,
                date = record.prescDate.toJalaliDateLabel(),
                title = stringResource(Res.string.records_doctor_named, record.docName),
                subtitle = record.location.ifBlank { record.specDesc },
                // The list endpoint carries no amount, so «سهم شما» comes from the per-record
                // price lookup; it reads as unknown until that arrives. Old app: the insured's
                // share is headSsoPayment (headInsuPayment is the organization's share).
                // The list endpoint carries no amount, so the share comes from the per-record
                // price lookup. It shimmers only while that lookup is out; once it is back, a
                // record with no price shows the absent marker rather than shimmering forever.
                shareAmount = prices[record.noteHeadEprescID]
                    ?.headSsoPayment
                    ?.toLongOrNull()
                    ?.toPriceFormat()
                    ?: UNKNOWN_AMOUNT.takeIf { !isLoadingPrices },
                accentColor = accent.content,
                accentContainerColor = accent.container,
                categoryIcon = accent.icon,
                onClick = { onRecordSelected(record) },
                // Gap between cards only, as the group's spacedBy arrangement gave before.
                modifier = Modifier
                    .staggeredItemEntrance(index = index, key = record.noteHeadEprescID, state = staggerState)
                    .padding(horizontal = Spacing.page)
                    .padding(bottom = if (index == monthRecords.lastIndex) 0.dp else Spacing.cardGap),
            )
        }

        item(key = "$monthLabel-gap") { Spacer(modifier = Modifier.height(Spacing.sm)) }
    }
}

/**
 * Totals pinned under the list, summing exactly the records on screen.
 *
 * Kept mounted for as long as there are records so it never appears and disappears mid-scroll:
 * while the per-record prices are still arriving it shimmers in place, and the figures fill in
 * underneath. With no records at all there is nothing to total, so it stands down entirely.
 */
@Composable
private fun RecordsTotals(
    prices: ImmutableList<ElectronicPrescriptionPricePR>,
    hasRecords: Boolean,
    isLoadingPrices: Boolean,
) {
    if (!hasRecords) return
    val pending = prices.isEmpty() && isLoadingPrices
    CostTotalsBar(
        insuredShareLabel = stringResource(Res.string.share_insured),
        insuredShareAmount = prices.totalOf { it.headSsoPayment }.takeUnless { pending },
        organizationShareLabel = stringResource(Res.string.share_organization),
        organizationShareAmount = prices.totalOf { it.headInsuPayment }.takeUnless { pending },
        totalLabel = stringResource(Res.string.amount_total),
        totalAmount = prices.totalOf { it.requestPrice }.takeUnless { pending },
    )
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
            RecordAccent(colors.orangeText, colors.orangeBg, vectorResource(Res.drawable.ic_tamin_health_profile))
        in RecordTab.PARACLINIC.requestTypeIds ->
            RecordAccent(colors.blueText, colors.blueBg, vectorResource(Res.drawable.ic_tamin_medical_approvals))
        RecordTab.medicalServiceTypeId, RecordTab.pharmacyTypeId ->
            RecordAccent(colors.greenText, colors.greenBg, vectorResource(Res.drawable.ic_tamin_medical_centers))
        else ->
            RecordAccent(colors.teal, colors.greenBg, vectorResource(Res.drawable.ic_tamin_prescriptions))
    }
}

@Composable
private fun RecordsShimmerSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        repeat(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .taminSurface(CornerRadius.cardCompact)
                    .shimmer(),
            )
        }
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
            patients = TreatmentMocks.mainUiState.toPatientList(
                mainInsuredFallback = stringResource(Res.string.patient_main_insured_fallback),
                dependantRelation = stringResource(Res.string.patient_dependant_relation),
            ),
            selectedPatient = "1234567890",
            onRetry = {},
            selectedPeriod = RecordPeriod.LAST_SIX_MONTHS,
            customRange = null,
            selectedTab = RecordTab.MEDICINE,
            onBack = {},
            onTabSelected = {},
            onPatientSelected = {},
            onPeriodSelected = {},
            onRecordSelected = {},
            onCustomRangePicked = { _, _ -> },
            searchCriteria = RecordSearchCriteria(),
            onSearchApplied = {},
        )
    }
}

