package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestUiState.PartialState
import com.tamin.taminhamrah.feature.workshops.ui.model.SettlementDocumentTypesWithSubcontractor
import com.tamin.taminhamrah.feature.workshops.ui.model.SettlementSubjectImageTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.workshop.AssignerContractPR
import com.tamin.taminhamrah.model.workshop.SettlementDocumentDN
import com.tamin.taminhamrah.model.workshop.SettlementRequestDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.useCases.workshops.GetSettlementSubjectsUseCase
import com.tamin.taminhamrah.useCases.workshops.SubmitSettlementRequestUseCase
import com.tamin.taminhamrah.useCases.workshops.UploadSettlementPdfUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/** درخواست مفاصاحساب for one پیمان of واگذارندگان. */
class SettlementRequestViewModel(
    private val getSettlementSubjects: GetSettlementSubjectsUseCase,
    private val uploadAttachment: WorkshopAttachmentUploader,
    private val uploadSettlementPdf: UploadSettlementPdfUseCase,
    private val submitSettlementRequest: SubmitSettlementRequestUseCase,
) : BaseViewModel<
    SettlementRequestUiState,
    PartialState,
    SettlementRequestEvent,
    SettlementRequestIntent,
    >(initialState = SettlementRequestUiState()) {

    override fun handleIntent(intent: SettlementRequestIntent): Flow<PartialState> = when (intent) {
        is SettlementRequestIntent.Open -> open(intent.contract)
        SettlementRequestIntent.ContractToggled -> just(PartialState.ContractToggled)
        is SettlementRequestIntent.FieldChanged -> changeField(intent.field, intent.value)
        is SettlementRequestIntent.DateChanged -> just(PartialState.DateChanged(intent.field, intent.date))
        is SettlementRequestIntent.SubcontractorChanged ->
            just(PartialState.SubcontractorChanged(intent.hasSubcontractor))

        SettlementRequestIntent.LoadSubjects -> loadSubjects()
        is SettlementRequestIntent.SubjectSelected -> just(PartialState.SubjectSelected(intent.subject))
        is SettlementRequestIntent.AddDocument ->
            upload(intent.fileName, intent.bytes, intent.typeCode, isSubjectImage = false)

        is SettlementRequestIntent.RemoveDocument -> just(PartialState.DocumentRemoved(intent.index))
        is SettlementRequestIntent.AddSubjectImage -> upload(
            fileName = intent.fileName,
            bytes = intent.bytes,
            typeCode = SettlementSubjectImageTypes.first().code,
            isSubjectImage = true,
        )

        SettlementRequestIntent.RemoveSubjectImage -> just(PartialState.SubjectImageRemoved)
        SettlementRequestIntent.Next -> next()
        SettlementRequestIntent.Previous -> previous()
        is SettlementRequestIntent.StepSelected -> flow {
            // Forward stays behind «مرحلهٔ بعد», where the step's rules are checked.
            if (intent.step < uiState.value.step) emit(PartialState.StepChanged(intent.step))
        }
    }

    private fun just(partialState: PartialState): Flow<PartialState> = flow { emit(partialState) }

    /** A new پیمان starts a new form; the subjects are fetched up front so the picker is ready when reached. */
    private fun open(contract: AssignerContractPR): Flow<PartialState> = flow {
        if (uiState.value.contract == contract) return@flow
        emit(PartialState.Opened(contract))
        emitAll(loadSubjects())
    }

    private fun loadSubjects(): Flow<PartialState> = flow {
        if (uiState.value.isSubjectsLoading) return@flow
        emit(PartialState.SubjectsLoading)
        val subjects = getSettlementSubjects().map { it.toPresentation() }.toImmutableList()
        emit(PartialState.SubjectsLoaded(subjects))
    }.catch {
        // Empty rather than stuck loading: opening the picker again asks once more.
        emit(PartialState.SubjectsLoaded(persistentListOf()))
        sendEvent(SettlementRequestEvent.ShowServerMessage(it.toSingleLineMessage()))
    }

    /**
     * Keeps each field to what it can hold: digits for every amount and code, free text for the two
     * the old app typed as text (subject 01's شماره طرح and ردیف بودجه), and a مکانیکی share capped at
     * 100 that fills in its دستی counterpart.
     */
    private fun changeField(field: SettlementField, raw: String): Flow<PartialState> = flow {
        val form = uiState.value.termsForm
        val isFreeText = form == SettlementTermsForm.PRICE_LIST &&
            (field == SettlementField.TEXT1 || field == SettlementField.TEXT2)
        if (isFreeText) {
            emit(PartialState.FieldChanged(field, raw))
            return@flow
        }
        val digits = raw.digitsOnly()
        if (form == SettlementTermsForm.MECHANICAL_SHARE && field == SettlementField.TEXT1) {
            val share = digits.toIntOrNull()?.coerceAtMost(SETTLEMENT_FULL_SHARE)
            emit(PartialState.FieldChanged(SettlementField.TEXT1, share?.toString().orEmpty()))
            emit(
                PartialState.FieldChanged(
                    SettlementField.TEXT2,
                    share?.let { (SETTLEMENT_FULL_SHARE - it).toString() }.orEmpty(),
                ),
            )
            return@flow
        }
        emit(PartialState.FieldChanged(field, digits))
    }

    /**
     * Puts a picked file on the server and keeps only what came back.
     *
     * The request's own documents may be PDFs, which go to their own route; the conditions' image is
     * only ever an image.
     */
    private fun upload(
        fileName: String,
        bytes: ByteArray,
        typeCode: String,
        isSubjectImage: Boolean,
    ): Flow<PartialState> = flow {
        emit(PartialState.UploadingChanged(true))
        val pdfRoute: (suspend (UploadImageRequestDN) -> String)? = if (isSubjectImage) {
            null
        } else {
            { request -> uploadSettlementPdf(request.fileName, request.bytes) }
        }
        val attachment = uploadAttachment(
            fileName = fileName,
            bytes = bytes,
            typeCode = typeCode,
            // The widest table, so a file picked before «پیمانکاری فرعی» was withdrawn still resolves.
            types = if (isSubjectImage) SettlementSubjectImageTypes else SettlementDocumentTypesWithSubcontractor,
            uploadPdf = pdfRoute,
        )
        emit(PartialState.AttachmentAdded(attachment, isSubjectImage))
    }.catch {
        emit(PartialState.UploadingChanged(false))
        sendEvent(SettlementRequestEvent.ShowServerMessage(it.toSingleLineMessage()))
    }

    /** Checks the step; moves on when it passes, and on the last step files the request. */
    private fun next(): Flow<PartialState> = flow {
        val state = uiState.value
        val errors = state.errorsOf(state.step)
        if (errors.isNotEmpty()) {
            emit(PartialState.Rejected(errors))
            return@flow
        }
        val following = SettlementStep.entries.getOrNull(state.step.ordinal + 1)
        if (following != null) {
            emit(PartialState.StepChanged(following))
            return@flow
        }
        val contract = state.contract ?: return@flow
        if (state.isSubmitting) return@flow
        emit(PartialState.SubmittingChanged(true))
        submitSettlementRequest(state.toRequest(contract))
        emit(PartialState.Submitted)
    }.catch {
        emit(PartialState.SubmittingChanged(false))
        sendEvent(SettlementRequestEvent.ShowServerMessage(it.toSingleLineMessage()))
    }

    private fun previous(): Flow<PartialState> = flow {
        val preceding = SettlementStep.entries.getOrNull(uiState.value.step.ordinal - 1) ?: return@flow
        emit(PartialState.StepChanged(preceding))
    }

    override fun reduceState(
        currentState: SettlementRequestUiState,
        partialState: PartialState,
    ): SettlementRequestUiState = when (partialState) {
        is PartialState.Opened -> SettlementRequestUiState(contract = partialState.contract)
        PartialState.ContractToggled -> currentState.copy(isContractOpen = !currentState.isContractOpen)
        is PartialState.FieldChanged -> currentState.withField(partialState.field, partialState.value)
        is PartialState.DateChanged -> currentState.withDate(partialState.field, partialState.date)
        is PartialState.SubcontractorChanged -> currentState.copy(
            hasSubcontractor = partialState.hasSubcontractor,
            errors = currentState.errors.remove(SettlementField.SUBCONTRACTOR),
        )

        PartialState.SubjectsLoading -> currentState.copy(isSubjectsLoading = true)
        is PartialState.SubjectsLoaded ->
            currentState.copy(isSubjectsLoading = false, subjects = partialState.subjects)

        is PartialState.SubjectSelected -> currentState.copy(
            subject = partialState.subject,
            // The old app's clearValues(): conditions typed under one subject mean nothing under another.
            terms = SettlementTerms(),
            errors = persistentMapOf(),
        )

        is PartialState.UploadingChanged -> currentState.copy(isUploading = partialState.isUploading)
        is PartialState.AttachmentAdded -> if (partialState.isSubjectImage) {
            currentState.copy(
                isUploading = false,
                terms = currentState.terms.copy(image = persistentListOf(partialState.attachment)),
                errors = currentState.errors.remove(SettlementField.SUBJECT_IMAGE),
            )
        } else {
            currentState.copy(
                isUploading = false,
                attachments = currentState.attachments.add(partialState.attachment),
                errors = currentState.errors.remove(SettlementField.DOCUMENTS),
            )
        }

        is PartialState.DocumentRemoved ->
            currentState.copy(attachments = currentState.attachments.removeAt(partialState.index))

        PartialState.SubjectImageRemoved ->
            currentState.copy(terms = currentState.terms.copy(image = persistentListOf()))

        is PartialState.StepChanged ->
            currentState.copy(step = partialState.step, errors = persistentMapOf())

        is PartialState.Rejected -> currentState.copy(errors = partialState.errors)
        is PartialState.SubmittingChanged -> currentState.copy(isSubmitting = partialState.isSubmitting)
        PartialState.Submitted -> currentState.copy(isSubmitting = false, isSubmitted = true)
        PartialState.Failed ->
            currentState.copy(isUploading = false, isSubmitting = false, isSubjectsLoading = false)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Failed
}

/** Applies a typed value to its field, and let's go of whatever error that field was showing. */
private fun SettlementRequestUiState.withField(
    field: SettlementField,
    value: String,
): SettlementRequestUiState {
    val cleared = errors.remove(field)
    return when (field) {
        SettlementField.LETTER_NUMBER -> copy(letterNumber = value, errors = cleared)
        SettlementField.AMOUNT -> copy(amount = value, errors = cleared)
        // The rial equivalent is only required because of this one, so its complaint goes with it.
        SettlementField.CURRENCY_AMOUNT ->
            copy(currencyAmount = value, errors = cleared.remove(SettlementField.CURRENCY_IN_RIAL))

        SettlementField.CURRENCY_IN_RIAL -> copy(currencyInRial = value, errors = cleared)
        SettlementField.OWNER -> copy(terms = terms.copy(owner = value), errors = cleared)
        SettlementField.TEXT1 -> copy(terms = terms.copy(text1 = value), errors = cleared)
        SettlementField.TEXT2 -> copy(terms = terms.copy(text2 = value), errors = cleared)
        SettlementField.AMOUNT1 -> copy(terms = terms.copy(amount1 = value), errors = cleared)
        SettlementField.AMOUNT2 -> copy(terms = terms.copy(amount2 = value), errors = cleared)
        SettlementField.AMOUNT3 -> copy(terms = terms.copy(amount3 = value), errors = cleared)
        SettlementField.AMOUNT4 -> copy(terms = terms.copy(amount4 = value), errors = cleared)
        SettlementField.LETTER_DATE,
        SettlementField.START_DATE,
        SettlementField.END_DATE,
        SettlementField.SUBCONTRACTOR,
        SettlementField.DOCUMENTS,
        SettlementField.SUBJECT,
        SettlementField.SUBJECT_IMAGE,
        -> this
    }
}

private fun SettlementRequestUiState.withDate(
    field: SettlementField,
    date: SettlementDate,
): SettlementRequestUiState = when (field) {
    SettlementField.LETTER_DATE -> copy(letterDate = date, errors = errors.remove(field))
    // A new start date can settle the end date's order complaint as well.
    SettlementField.START_DATE ->
        copy(startDate = date, errors = errors.remove(field).remove(SettlementField.END_DATE))

    SettlementField.END_DATE -> copy(endDate = date, errors = errors.remove(field))
    else -> this
}

/**
 * The request as the domain takes it. Only called once every step has passed [errorsOf], so the
 * dates and the subject are known to be set.
 */
private fun SettlementRequestUiState.toRequest(contract: AssignerContractPR): SettlementRequestDN {
    val form = termsForm
    val deducts = form == SettlementTermsForm.DRIVERS || form == SettlementTermsForm.EQUIPMENT
    return SettlementRequestDN(
        workshopId = contract.card.workshopId,
        contractRow = contract.contractRow,
        branchCode = contract.branchCode,
        contractSequence = contract.contractSequence,
        letterNumber = letterNumber,
        letterDate = letterDate.isoGregorian(),
        startDate = startDate.isoGregorian(),
        endDate = endDate.isoGregorian(),
        hasSubcontractor = hasSubcontractor == true,
        amount = amount.toLongOrNull() ?: 0L,
        currencyAmount = currencyAmount.toLongOrNull() ?: 0L,
        currencyAmountInRial = currencyInRial.toLongOrNull() ?: 0L,
        documents = attachments.map {
            SettlementDocumentDN(documentId = it.guid, categoryCode = it.type.code, isPdf = it.isPdf)
        },
        subjectCode = subject?.id.orEmpty(),
        subjectOwner = terms.owner,
        subjectText1 = terms.text1,
        subjectText2 = terms.text2,
        subjectAmount1 = terms.amount1,
        subjectAmount2 = if (deducts) {
            settlementRemainder(amount, currencyInRial, terms.amount1).toString()
        } else {
            terms.amount2
        },
        subjectAmount3 = terms.amount3,
        subjectAmount4 = terms.amount4,
        subjectImageGuid = terms.image.firstOrNull()?.guid.orEmpty(),
    )
}

private fun SettlementDate?.isoGregorian(): String =
    this?.let { PersianDateFormatter.toIsoGregorian(it.year, it.month, it.day) }.orEmpty()
