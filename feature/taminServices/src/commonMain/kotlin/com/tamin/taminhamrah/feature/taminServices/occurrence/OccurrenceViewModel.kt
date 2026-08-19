package com.tamin.taminhamrah.feature.taminServices.occurrence

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceEvent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceIntent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceUiState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceUiState.PartialState
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.Gender
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.toPR
import com.tamin.taminhamrah.model.occurrence.OccurrenceSubmitRequestDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceUploadedDocDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toJalaliParts
import com.tamin.taminhamrah.useCases.history.GetUserInfosUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetAllWorkshopsUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetInsuredRelationUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetOccurrenceDocTypesUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetOccurrencePersonalInfoUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetWorkshopSpecUseCase
import com.tamin.taminhamrah.useCases.occurrence.SubmitOccurrenceUseCase
import com.tamin.taminhamrah.useCases.occurrence.UploadOccurrenceImageUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

/** The legacy "occurence" submit endpoint's reporterType for a self-filed report — this flow has no reporter-type picker. */
private const val REPORTER_TYPE_SELF = "1"

/** Converts a Jalali "yyyy/mm/dd" date string to epoch milliseconds, as the legacy submit endpoint expects. */
private fun String.toEpochMillisFromJalali(): Long {
    val (year, month, day) = toJalaliParts() ?: return 0L
    return PersianDateFormatter.toEpochMillis(year, month, day)
}

class OccurrenceViewModel(
    private val getPersonalInfoUseCase: GetOccurrencePersonalInfoUseCase,
    private val getUserInfosUseCase: GetUserInfosUseCase,
    private val getAllWorkshopsUseCase: GetAllWorkshopsUseCase,
    private val getWorkshopSpecUseCase: GetWorkshopSpecUseCase,
    private val getInsuredRelationUseCase: GetInsuredRelationUseCase,
    private val getDocTypesUseCase: GetOccurrenceDocTypesUseCase,
    private val uploadImageUseCase: UploadOccurrenceImageUseCase,
    private val submitOccurrenceUseCase: SubmitOccurrenceUseCase,
) : BaseViewModel<OccurrenceUiState, PartialState, OccurrenceEvent, OccurrenceIntent>(
    initialState = OccurrenceUiState()
) {

    override fun handleIntent(intent: OccurrenceIntent): Flow<PartialState> = when (intent) {
        is OccurrenceIntent.LoadInitialData -> loadInitialData()
        is OccurrenceIntent.GoToNextStep -> flow { emit(PartialState.GoToNextStep) }
        is OccurrenceIntent.GoToPreviousStep -> goToPreviousStep()
        is OccurrenceIntent.SelectWorkshop -> selectWorkshop(intent)
        is OccurrenceIntent.UpdatePersonInfo -> flow { emit(PartialState.PersonInfoUpdated(intent.personInfo)) }
        is OccurrenceIntent.UpdateWorkshop -> flow { emit(PartialState.WorkshopUpdated(intent.workshop)) }
        is OccurrenceIntent.UpdateJobDetails -> flow { emit(PartialState.JobDetailsUpdated(intent.jobDetails)) }
        is OccurrenceIntent.UpdateWorkHours -> flow { emit(PartialState.WorkHoursUpdated(intent.workHours)) }
        is OccurrenceIntent.UpdateAccident -> flow { emit(PartialState.AccidentUpdated(intent.accident)) }
        is OccurrenceIntent.UploadDocument -> uploadDocument(intent)
        is OccurrenceIntent.RemoveDocument -> removeDocument(intent)
        is OccurrenceIntent.SubmitOccurrence -> submitOccurrence()
    }

    private fun Flow<PartialState>.ignoreError(): Flow<PartialState> =
        catch { e -> sendEvent(OccurrenceEvent.ShowToast(e.toSingleLineMessage())) }

    private fun loadInitialData(): Flow<PartialState> = flow {
        var nationalId = ""
        try {
            val userInfo = getUserInfosUseCase()
            nationalId = userInfo.nationalID.orEmpty()
            emit(PartialState.UserInfoUpdated(userInfo.toPR()))
        } catch (e: Exception) {
            sendEvent(OccurrenceEvent.ShowToast(e.toSingleLineMessage()))
        }
        emitAll(
            merge(
                fetchWorkshops(nationalId).ignoreError(),
                fetchDocTypes().ignoreError(),
                fetchInsuredRelation(nationalId).ignoreError(),
            )
        )
    }.onStart {
        emit(PartialState.Loading(true))
    }.onCompletion {
        emit(PartialState.Loading(false))
    }

    private fun fetchWorkshops(nationalId: String): Flow<PartialState> = flow {
        val workshops = getAllWorkshopsUseCase(nationalId).map { it.toPR() }
        emit(PartialState.WorkshopUpdated(uiState.value.workshop.copy(workshops = workshops)))
    }

    private fun fetchDocTypes(): Flow<PartialState> = flow {
        val types = getDocTypesUseCase().map { it.toPR() }
        emit(PartialState.DocumentSubmitUpdated(uiState.value.documentSubmit.copy(docTypes = types)))
    }

    private fun fetchInsuredRelation(nationalId: String): Flow<PartialState> = flow {
        val relation = getInsuredRelationUseCase(nationalId)
        emit(
            PartialState.JobDetailsUpdated(
                uiState.value.jobDetails.copy(
                    insuranceType = relation.insuranceType,
                    insuranceTypeCode = relation.insuranceTypeCode,
                    branchCode = relation.branchCode,
                    branchName = relation.branchName,
                )
            )
        )
    }

    private fun goToPreviousStep(): Flow<PartialState> = flow {
        if (uiState.value.currentStep == OccurrenceStep.PERSON_INFO) {
            sendEvent(OccurrenceEvent.NavigateBack)
        } else {
            emit(PartialState.GoToPreviousStep)
        }
    }

    private fun selectWorkshop(intent: OccurrenceIntent.SelectWorkshop): Flow<PartialState> = flow {
        emit(
            PartialState.WorkshopUpdated(
                uiState.value.workshop.copy(
                    selectedWorkshop = intent.workshop,
                    isWorkshopSpecLoading = true,
                )
            )
        )
        try {
            val spec = getWorkshopSpecUseCase(intent.workshop.workshopCode, intent.workshop.branchCode)
            emit(
                PartialState.WorkshopUpdated(
                    uiState.value.workshop.copy(
                        selectedWorkshop = intent.workshop.copy(name = spec.name),
                        employerName = spec.employerName,
                        employerPhone = spec.employerPhone,
                        workshopAddress = spec.address,
                        workshopPostalCode = spec.postalCode,
                        workshopPhone = spec.phone,
                        isWorkshopSpecLoading = false,
                    )
                )
            )
            val userInfo = uiState.value.personInfo.userInfo
            val personalInfo = getPersonalInfoUseCase(
                nationalCode = userInfo?.nationalID.orEmpty(),
                birthDate = userInfo?.birthDateTimestamp?.toString().orEmpty(),
                workshopCode = intent.workshop.workshopCode,
                branchCode = intent.workshop.branchCode,
            )
            emit(PartialState.PersonInfoUpdated(uiState.value.personInfo.copy(personalInfo = personalInfo.toPR())))
            emit(
                PartialState.JobDetailsUpdated(
                    uiState.value.jobDetails.copy(
                        fullName = personalInfo.fullName,
                        nationality = spec.nationality,
                        nationalityCode = spec.nationalityCode,
                        gender = personalInfo.gender,
                    )
                )
            )
        } catch (e: Exception) {
            emit(PartialState.WorkshopUpdated(uiState.value.workshop.copy(isWorkshopSpecLoading = false)))
            sendEvent(OccurrenceEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun uploadDocument(intent: OccurrenceIntent.UploadDocument): Flow<PartialState> = flow {
        val current = uiState.value.documentSubmit
        emit(PartialState.DocumentSubmitUpdated(current.copy(isUploadingDoc = true)))
        try {
            val guid = uploadImageUseCase(intent.fileName, intent.fileBytes)
            val newDoc = OccurrenceUploadedDocDN(
                typeId = intent.typeId,
                typeName = intent.typeName,
                fileName = intent.fileName,
                guid = guid,
            )
            emit(
                PartialState.DocumentSubmitUpdated(
                    uiState.value.documentSubmit.copy(
                        isUploadingDoc = false,
                        uploadedDocuments = uiState.value.documentSubmit.uploadedDocuments + newDoc,
                    )
                )
            )
        } catch (e: Exception) {
            emit(PartialState.DocumentSubmitUpdated(uiState.value.documentSubmit.copy(isUploadingDoc = false)))
            sendEvent(OccurrenceEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun removeDocument(intent: OccurrenceIntent.RemoveDocument): Flow<PartialState> = flow {
        emit(
            PartialState.DocumentSubmitUpdated(
                uiState.value.documentSubmit.copy(
                uploadedDocuments = uiState.value.documentSubmit.uploadedDocuments.filter { it.guid != intent.guid }
            )
        ))
    }

    private fun submitOccurrence(): Flow<PartialState> = flow {
        val state = uiState.value
        emit(PartialState.Submitting(true))
        try {
            val personalInfo = state.personInfo.personalInfo
            val userInfo = state.personInfo.userInfo
            val request = OccurrenceSubmitRequestDN(
                nationalCode = userInfo?.nationalID?.takeIf { it.isNotBlank() }
                    ?: personalInfo?.nationalCode.orEmpty(),
                firstName = personalInfo?.firstName.orEmpty(),
                lastName = personalInfo?.lastName.orEmpty(),
                gender = Gender.fromCode(personalInfo?.gender)?.legacyCode ?: 0,
                nationalityCode = state.jobDetails.nationalityCode.toIntOrNull() ?: 0,
                insuranceType = state.jobDetails.insuranceType,
                insuranceTypeCode = state.jobDetails.insuranceTypeCode,
                insuranceNumber = userInfo?.insuranceNumber?.takeIf { it.isNotBlank() }
                    ?: personalInfo?.insuranceNumber.orEmpty(),
                branchCode = state.jobDetails.branchCode,
                branchName = state.jobDetails.branchName,
                birthDate = state.personInfo.birthDate.toEpochMillisFromJalali(),
                workshopId = state.workshop.selectedWorkshop?.id.orEmpty(),
                workshopBranchCode = state.workshop.selectedWorkshop?.branchCode.orEmpty(),
                workshopName = state.workshop.selectedWorkshop?.name.orEmpty(),
                employerName = state.workshop.employerName,
                employerPhone = state.workshop.employerPhone,
                workshopAddress = state.workshop.workshopAddress,
                workshopPostalCode = state.workshop.workshopPostalCode,
                workshopPhone = state.workshop.workshopPhone,
                employmentDate = state.jobDetails.employmentDate.toEpochMillisFromJalali(),
                maritalStatus = state.jobDetails.maritalStatus.toIntOrNull() ?: 0,
                jobTitle = state.jobDetails.jobTitle,
                workLocation = state.jobDetails.workLocation,
                transportation = state.workHours.transportation,
                workStartTime = state.workHours.workStartTime,
                workEndTime = state.workHours.workEndTime,
                homeAddress = state.workHours.homeAddress,
                homePhone = state.workHours.homePhone,
                homePostalCode = state.workHours.homePostalCode,
                accidentDate = state.accident.accidentDate.toEpochMillisFromJalali(),
                accidentTime = state.accident.accidentTime,
                accidentOutcomeId = state.accident.accidentOutcomeId.toIntOrNull() ?: 0,
                exactLocation = state.accident.exactLocation,
                description = state.accident.description,
                reporterType = REPORTER_TYPE_SELF,
                documents = state.documentSubmit.uploadedDocuments,
            )
            val result = submitOccurrenceUseCase(request)
            emit(PartialState.Submitting(false))
            sendEvent(OccurrenceEvent.DisplaySuccessModal(result.trackingCode))
        } catch (e: Exception) {
            emit(PartialState.Submitting(false))
            sendEvent(OccurrenceEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    override fun reduceState(
        currentState: OccurrenceUiState,
        partialState: PartialState
    ): OccurrenceUiState =
        when (partialState) {
            is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
            is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting)
            is PartialState.GoToNextStep -> {
                val next =
                    OccurrenceStep.entries.getOrElse(currentState.currentStep.ordinal + 1) { currentState.currentStep }
                currentState.copy(currentStep = next)
            }

            is PartialState.GoToPreviousStep -> {
                val prev =
                    OccurrenceStep.entries.getOrElse(currentState.currentStep.ordinal - 1) { currentState.currentStep }
                currentState.copy(currentStep = prev)
            }

            is PartialState.PersonInfoUpdated -> currentState.copy(personInfo = partialState.personInfo)
            is PartialState.UserInfoUpdated -> currentState.copy(
                personInfo = currentState.personInfo.copy(
                    userInfo = partialState.userInfo
                )
            )

            is PartialState.WorkshopUpdated -> currentState.copy(workshop = partialState.workshop)
            is PartialState.JobDetailsUpdated -> currentState.copy(jobDetails = partialState.jobDetails)
            is PartialState.WorkHoursUpdated -> currentState.copy(workHours = partialState.workHours)
            is PartialState.AccidentUpdated -> currentState.copy(accident = partialState.accident)
            is PartialState.DocumentSubmitUpdated -> currentState.copy(documentSubmit = partialState.documentSubmit)
        }

    override fun createErrorState(message: String): PartialState {
        sendEvent(OccurrenceEvent.ShowToast(message))
        return PartialState.Loading(false)
    }
}
