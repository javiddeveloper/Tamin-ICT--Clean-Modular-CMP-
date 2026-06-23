package com.tamin.taminhamrah.feature.pensionInquiry.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryUiState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryUiState.PartialState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryEvent
import com.tamin.taminhamrah.mapper.common.toPresentation
import com.tamin.taminhamrah.mapper.pension.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.useCases.common.GetBeneficiaryUseCase
import com.tamin.taminhamrah.useCases.pension.GetEdictPensionerUseCase
import com.tamin.taminhamrah.useCases.common.GetRecipientListUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionInquiryUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollUseCase
import com.tamin.taminhamrah.useCases.pension.GetDisabilityPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetDeceasedInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetAgeUseCase
import com.tamin.taminhamrah.useCases.personal.GetPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetDisabilityDependentInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetConfirmSurvivorsListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PensionInquiryViewModel(
    private val getPensionInquiryUseCase: GetPensionInquiryUseCase,
    private val getRecipientListUseCase: GetRecipientListUseCase,
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
    private val getEdictPensionerUseCase: GetEdictPensionerUseCase,
    private val getPersonalInfoUseCase: GetPersonalInfoUseCase,
    private val getBeneficiaryUseCase: GetBeneficiaryUseCase,
    private val getDeceasedInfoUseCase: GetDeceasedInfoUseCase,
    private val getAgeUseCase: GetAgeUseCase,
    private val getDisabilityDependentInfoUseCase: GetDisabilityDependentInfoUseCase,
    private val getConfirmSurvivorsListUseCase: GetConfirmSurvivorsListUseCase,
    private val getPensionerPayRollUseCase: GetPensionerPayRollUseCase,
    private val getDisabilityPersonalInfoUseCase: GetDisabilityPersonalInfoUseCase,
) : BaseViewModel<PensionInquiryUiState, PartialState, PensionInquiryEvent, PensionInquiryIntent>(
    initialState = PensionInquiryUiState()
) {

    init {
        sendIntent(PensionInquiryIntent.LoadPersonalInfo)
        sendIntent(PensionInquiryIntent.LoadAge(1379L))
        sendIntent(PensionInquiryIntent.LoadDisabilityPersonalInfo)
    }

    override fun handleIntent(intent: PensionInquiryIntent): Flow<PartialState> {
        return when (intent) {
            is PensionInquiryIntent.LoadPensionInquiry -> handleLoadPensionInquiry()
            is PensionInquiryIntent.LoadPensionerIds -> handleLoadPensionerIds()
            is PensionInquiryIntent.LoadEdict -> handleLoadEdict(intent.pensionerId)
            is PensionInquiryIntent.LoadRecipients -> handleLoadRecipients()
            is PensionInquiryIntent.LoadBeneficiaryList -> handleLoadBeneficiaryList()
            is PensionInquiryIntent.LoadPersonalInfo -> handleLoadPersonalInfo()
            is PensionInquiryIntent.LoadDeceasedInfo -> handleLoadDeceasedInfo(intent.nationalId)
            is PensionInquiryIntent.LoadAge -> handleLoadAge(intent.birthDate)
            is PensionInquiryIntent.LoadDisabilityDependentInfo -> handleLoadDisabilityDependentInfo()
            is PensionInquiryIntent.LoadConfirmSurvivorsList -> handleLoadConfirmSurvivorsList()
            is PensionInquiryIntent.LoadPensionerPayRoll -> handleLoadPensionerPayRoll(intent.filters)
            is PensionInquiryIntent.LoadDisabilityPersonalInfo -> handleLoadDisabilityPersonalInfo()
        }
    }

    private fun handleLoadDisabilityPersonalInfo(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getDisabilityPersonalInfoUseCase().collect { personalInfo ->
                emit(PartialState.DisabilityPersonalInfoLoaded(personalInfo.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadPensionerPayRoll(filters: List<ApiFilterDN>): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPensionerPayRollUseCase(filters).collect { payRoll ->
                emit(PartialState.PayRollLoaded(payRoll.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadConfirmSurvivorsList(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getConfirmSurvivorsListUseCase(emptyList()).collect { list ->
                emit(PartialState.ConfirmSurvivorsListLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadDisabilityDependentInfo(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getDisabilityDependentInfoUseCase(emptyList()).collect { list ->
                emit(PartialState.DisabilityDependentInfoLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadPersonalInfo(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPersonalInfoUseCase().collect { personalInfo ->
                emit(PartialState.PersonalInfoLoaded(personalInfo?.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadAge(birthDate: Long): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getAgeUseCase(birthDate).collect { age ->
                emit(PartialState.AgeLoaded(age.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadRecipients(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getRecipientListUseCase().collect { list ->
                emit(PartialState.RecipientsLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadPensionInquiry(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPensionInquiryUseCase().collect { list ->
                val presentationList = list.toPresentation()
                emit(PartialState.PensionListLoaded(presentationList))
                if (presentationList.isNotEmpty()) {
                    sendIntent(PensionInquiryIntent.LoadDeceasedInfo(presentationList.first().nationalId))
                }
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadPensionerIds(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPensionerIdUseCase().collect { list ->
                val presentationList = list.toPresentation()
                emit(PartialState.PensionerIdsLoaded(presentationList))
                if (presentationList.isNotEmpty()) {
                    sendIntent(PensionInquiryIntent.LoadEdict(presentationList.first().pensionerId))
                }
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadBeneficiaryList(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getBeneficiaryUseCase().collect { list ->
                val presentationList = list.toPresentation()
                emit(PartialState.BeneficiaryListLoaded(presentationList))
                if (presentationList.isNotEmpty()){
                    sendIntent(PensionInquiryIntent.LoadPensionInquiry)
                }
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadEdict(pensionerId: String): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val query = ApiQueryParamDN(
                filters = listOf(
                    ApiFilterDN(FilterProperty.START_DATE, "14000101", FilterOperator.EQUAL),
                    ApiFilterDN(FilterProperty.PENSIONER_ID, pensionerId, FilterOperator.EQUAL)
                )
            )
            getEdictPensionerUseCase(query).collect { edict ->
                emit(PartialState.EdictLoaded(edict?.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadDeceasedInfo(nationalId: String): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getDeceasedInfoUseCase(nationalId).collect { deceasedInfo ->
                emit(PartialState.DeceasedInfoLoaded(deceasedInfo.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: PensionInquiryUiState,
        partialState: PartialState
    ): PensionInquiryUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.PensionListLoaded -> currentState.copy(
            isLoading = false,
            pensionList = partialState.list
        )

        is PartialState.PensionerIdsLoaded -> currentState.copy(
            isLoading = false,
            pensionerIds = partialState.list
        )

        is PartialState.RecipientsLoaded -> currentState.copy(
            isLoading = false,
            recipients = partialState.list
        )

        is PartialState.EdictLoaded -> currentState.copy(
            isLoading = false,
            edictPensioner = partialState.edict
        )
        is PartialState.PersonalInfoLoaded -> currentState.copy(
            isLoading = false,
            personalInfo = partialState.personalInfo
        )
        is PartialState.BeneficiaryListLoaded -> currentState.copy(
            isLoading = false,
            beneficiaryList = partialState.list
        )
        is PartialState.DeceasedInfoLoaded -> currentState.copy(
            isLoading = false,
            deceasedInfo = partialState.deceasedInfo
        )
        is PartialState.DisabilityDependentInfoLoaded -> currentState.copy(
            isLoading = false,
            disabilityDependentInfo = partialState.list
        )
        is PartialState.ConfirmSurvivorsListLoaded -> currentState.copy(
            isLoading = false,
            confirmSurvivorsList = partialState.list
        )
        is PartialState.AgeLoaded -> currentState.copy(
            isLoading = false,
            age = partialState.age
        )
        is PartialState.PayRollLoaded -> currentState.copy(
            isLoading = false,
            payRoll = partialState.payRoll
        )
        is PartialState.DisabilityPersonalInfoLoaded -> currentState.copy(
            isLoading = false,
            disabilityPersonalInfo = partialState.disabilityPersonalInfo
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
