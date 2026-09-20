package com.tamin.taminhamrah.feature.treatment.ui.prescriptions

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsUiState
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsUiState.PartialState
import com.tamin.taminhamrah.mapper.treatment.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPricePR
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.treatment.DownloadLabResultPdfUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionDetailUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionListUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionPriceUseCase
import com.tamin.taminhamrah.useCases.treatment.GetPrescriptionPdfFileUseCase
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.util.getCurrentTimestamp
import com.tamin.taminhamrah.util.getSixMonthsAgoTimestamp
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class PrescriptionsViewModel(
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val getElectronicPrescriptionListUseCase: GetElectronicPrescriptionListUseCase,
    private val getElectronicPrescriptionDetailUseCase: GetElectronicPrescriptionDetailUseCase,
    private val getElectronicPrescriptionPriceUseCase: GetElectronicPrescriptionPriceUseCase,
    private val getPrescriptionPdfFileUseCase: GetPrescriptionPdfFileUseCase,
    private val downloadLabResultPdfUseCase: DownloadLabResultPdfUseCase
) : BaseViewModel<PrescriptionsUiState, PartialState, PrescriptionsEvent, PrescriptionsIntent>(
    initialState = PrescriptionsUiState()
) {

    override fun handleIntent(intent: PrescriptionsIntent): Flow<PartialState> {
        return when (intent) {
            is PrescriptionsIntent.LoadList -> loadList(intent)
            is PrescriptionsIntent.SelectPrescription -> selectPrescription(intent)
            is PrescriptionsIntent.ClearSelectedPrescription -> flow { emit(PartialState.PrescriptionCleared) }
            is PrescriptionsIntent.DownloadPdf -> downloadPdf(intent)
            is PrescriptionsIntent.DownloadLabResult -> downloadLabResult(intent)
            is PrescriptionsIntent.DismissPdfViewer -> flow { emit(PartialState.ViewerPdfChanged(null)) }
            is PrescriptionsIntent.LoadRecordPrices -> loadRecordPrices(intent)
        }
    }

    /**
     * The signed-in person's national code, from the identity use case rather than the token
     * store — the same source the treatment hub uses, so both screens agree on who is signed in.
     *
     * Falls back to "0", never "": these values are URL path segments, so an empty one collapses
     * `patient-history/{nationalCode}/...` into a double slash and the endpoint 404s. The previous
     * app used the same placeholder.
     */
    private suspend fun getLoggedNationalCode(): String = try {
        identityInfoUseCase().first().nationalId?.takeIf { it.isNotBlank() } ?: NO_NATIONAL_CODE
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        NO_NATIONAL_CODE
    }

    /**
     * What the list is currently showing. Emitting a new query supersedes the one in flight.
     *
     * A [MutableSharedFlow] rather than a [kotlinx.coroutines.flow.MutableStateFlow] on purpose:
     * a state flow drops a value equal to the current one, which would make «تلاش دوباره» after a
     * failure do nothing at all. `replay = 1` hands the pending query to the pipeline the moment it
     * subscribes, so the first request is never lost.
     */
    private val listQuery = MutableSharedFlow<RecordListQuery>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /** Guards [listPipeline] so the reducer is handed it once and never a second copy. */
    private var listPipelineStarted = false

    /**
     * Re-queries the list, cancelling whatever request was still running.
     *
     * Every other intent here finishes and frees its slot; a list request never does, because the
     * repository ends in `emitAll` over a Room query that stays open. [BaseViewModel] merges intent
     * flows rather than switching between them, so returning a fresh flow per `LoadList` left every
     * earlier request alive:
     *
     *  - each one kept writing *its* tab's rows into the state whenever Room changed, so the list
     *    could revert to the previous tab's contents — or to an empty one, which the screen shows
     *    as «چیزی یافت نشد»;
     *  - and `flatMapMerge` only runs `DEFAULT_CONCURRENCY` (16) flows at once, so after about
     *    sixteen tab switches or searches it stopped collecting new intents entirely and the screen
     *    froze on whatever it last had.
     *
     * Handing the reducer one long-lived pipeline and switching the query inside it fixes both: one
     * slot for the life of the ViewModel, and `flatMapLatest` cancels the superseded request.
     */
    private fun loadList(intent: PrescriptionsIntent.LoadList): Flow<PartialState> {
        listQuery.tryEmit(
            RecordListQuery(
                patientNationalCode = intent.nationalCode,
                requestTypeIds = intent.requestTypeIds,
                startDate = intent.startDate,
                endDate = intent.endDate,
            ),
        )
        if (listPipelineStarted) return emptyFlow()
        listPipelineStarted = true
        return listPipeline()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun listPipeline(): Flow<PartialState> =
        listQuery.flatMapLatest { query -> records(query) }

    private fun records(query: RecordListQuery): Flow<PartialState> = flow {
        // Loading(true) keeps the existing list on screen during a refresh, so PullToRefresh
        // overlays cleanly instead of blanking the page.
        emit(PartialState.Loading(true))
        val nationalCode = getLoggedNationalCode()
        // Defaults to the «۶ ماه اخیر» period the records filter advertises.
        val startD = query.startDate ?: getSixMonthsAgoTimestamp()
        val endD = query.endDate ?: getCurrentTimestamp()

        // One request per category. «همه» asks for each type and the lists are merged here,
        // newest first; a single-type tab is just a list of one. The repository encodes how
        // "self" versus a dependant is addressed, so the raw patient code is handed straight in.
        val perType = query.requestTypeIds.map { requestTypeId ->
            getElectronicPrescriptionListUseCase(
                requestTypeId,
                nationalCode,
                query.patientNationalCode,
                startD,
                endD,
            )
        }
        combine(perType) { lists ->
            lists.toList()
                .flatten()
                .sortedByDescending { it.prescDate }
        }.collect { merged ->
            emit(PartialState.PrescriptionsLoaded(merged.toPresentation()))
        }
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    /**
     * Opens a record: its items and its price, side by side.
     *
     * Both repositories answer from the cache and then keep watching it, so neither flow ever
     * completes. Collected one after the other, the price request was never made at all — the
     * items held the coroutine for as long as the screen was open. Merged, each runs on its own.
     */
    private fun selectPrescription(intent: PrescriptionsIntent.SelectPrescription): Flow<PartialState> = flow {
        emit(PartialState.PrescriptionSelected(intent.noteHeadID))
        emit(PartialState.Loading(true))
        val nationalCode = getLoggedNationalCode()

        // Raw patient code and flagSata; the repository encodes "self" and an absent flagSata
        // the way the endpoint expects.
        val details: Flow<PartialState> = getElectronicPrescriptionDetailUseCase(
            intent.noteHeadID, nationalCode, intent.nationalCode, intent.flagSata, intent.type,
        ).map { PartialState.PrescriptionDetailsLoaded(it.toPresentation()) }
        val prices: Flow<PartialState> = getElectronicPrescriptionPriceUseCase(intent.noteHeadID, nationalCode)
            .map { PartialState.PrescriptionPricesLoaded(it.toPresentation()) }

        emitAll(
            merge(
                details.catch { emit(PartialState.Error(it.toSingleLineMessage())) },
                // Supplementary to the items: without a price the screen totals the items instead,
                // so a failure here is not the screen's failure.
                prices.catch { },
            ),
        )
    }

    /**
     * Fetches the insured share for each record.
     *
     * One request per record, because the price endpoint is keyed by a single `noteHeadID` and the
     * list carries no amount. Only run when the cost filter is actually in use, and records whose
     * price fails are simply left out rather than failing the whole search.
     */
    private fun loadRecordPrices(intent: PrescriptionsIntent.LoadRecordPrices): Flow<PartialState> =
        flow {
            emit(PartialState.LoadingPrices(true))
            val nationalCode = getLoggedNationalCode()
            val prices = mutableMapOf<String, ElectronicPrescriptionPricePR>()
            intent.noteHeadIds.forEach { noteHeadId ->
                try {
                    getElectronicPrescriptionPriceUseCase(noteHeadId, nationalCode)
                        .first()
                        .firstOrNull()
                        ?.let { price -> prices[noteHeadId] = price.toPresentation() }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // A record without a price stays unpriced rather than failing the whole load.
                }
            }
            emit(PartialState.RecordPricesLoaded(prices))
            emit(PartialState.LoadingPrices(false))
        }

    /**
     * Fetches the prescription PDF. Only reached when the viewer finds no copy already on the
     * device, so an export the person has downloaded before costs no request at all.
     */
    private fun downloadPdf(intent: PrescriptionsIntent.DownloadPdf): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        emit(PartialState.ViewerPdfChanged(null))
        try {
            getPrescriptionPdfFileUseCase(intent.prescriptionID).collect { pdfDn ->
                emit(PartialState.ViewerPdfChanged(pdfDn.toPresentation()))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
            emit(PartialState.ViewerDownloadFailed)
        }
    }

    private fun downloadLabResult(intent: PrescriptionsIntent.DownloadLabResult): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        emit(PartialState.ViewerPdfChanged(null))
        val patientID = intent.patientID ?: ""
        val noteHeadEprescID = intent.noteHeadEprescID ?: ""
        val currentUserNationalCode = getLoggedNationalCode()

        try {
            downloadLabResultPdfUseCase(patientID, noteHeadEprescID, currentUserNationalCode).collect { pdfDn ->
                emit(PartialState.ViewerPdfChanged(pdfDn.toPresentation()))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
            emit(PartialState.ViewerDownloadFailed)
        }
    }

    override fun reduceState(
        currentState: PrescriptionsUiState,
        partialState: PartialState
    ): PrescriptionsUiState = when (partialState) {
        is PartialState.Reset -> PrescriptionsUiState()
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.PrescriptionsLoaded -> currentState.copy(isLoading = false, prescriptionList = partialState.list)
        is PartialState.PrescriptionDetailsLoaded -> currentState.copy(isLoading = false, prescriptionDetailList = partialState.list)
        // Not the end of loading: the price can land before the items it prices, and clearing the
        // flag then would show the empty state for a record that has items on the way.
        is PartialState.PrescriptionPricesLoaded -> currentState.copy(prescriptionPriceList = partialState.list.toImmutableList())
        is PartialState.ViewerPdfChanged -> currentState.copy(
            isLoading = false,
            viewerPdf = partialState.pdf,
            viewerDownloadFailed = false,
        )
        is PartialState.ViewerDownloadFailed -> currentState.copy(
            isLoading = false,
            viewerDownloadFailed = true,
        )
        // The previous record's items and price go with it, so a slow answer for this one cannot
        // leave the last record's totals under its heading.
        is PartialState.PrescriptionSelected -> currentState.copy(
            selectedNoteHeadId = partialState.noteHeadID,
            prescriptionDetailList = emptyList(),
            prescriptionPriceList = persistentListOf(),
        )
        is PartialState.RecordPricesLoaded -> currentState.copy(
            recordPrices = (currentState.recordPrices + partialState.prices).toImmutableMap(),
        )
        is PartialState.LoadingPrices -> currentState.copy(isLoadingPrices = partialState.isLoading)
        is PartialState.PrescriptionCleared -> currentState.copy(
            selectedNoteHeadId = null,
            prescriptionDetailList = emptyList(),
            prescriptionPriceList = persistentListOf()
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

/** Placeholder for an unknown national code, matching the previous app's path segment. */
private const val NO_NATIONAL_CODE = "0"

/**
 * Everything the records list is queried by.
 *
 * A value type so the pipeline can be driven by "the current query" rather than by a stream of
 * intents — swapping one for another is what lets the previous request be canceled.
 */
private data class RecordListQuery(
    val patientNationalCode: String,
    val requestTypeIds: List<String>,
    val startDate: String?,
    val endDate: String?,
)
