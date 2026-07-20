package com.tamin.taminhamrah.feature.treatment.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentUiState
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentUiState.PartialState
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMessageType
import com.tamin.taminhamrah.mapper.treatment.toPresentation
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.treatment.GetDependantUnderEighteenUseCase
import com.tamin.taminhamrah.useCases.treatment.GetDeservedTreatmentUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform

class TreatmentViewModel(
    private val getDeservedTreatmentUseCase: GetDeservedTreatmentUseCase,
    private val getDependantUnderEighteenUseCase: GetDependantUnderEighteenUseCase,
    private val identityInfoUseCase: IdentityInfoUseCase
) : BaseViewModel<TreatmentUiState, PartialState, TreatmentEvent, TreatmentIntent>(
    initialState = TreatmentUiState()
) {

    override fun handleIntent(intent: TreatmentIntent): Flow<PartialState> {
        return when (intent) {
            is TreatmentIntent.InitTreatmentFlow -> initTreatmentFlow()
            is TreatmentIntent.SelectPatient -> flow { emit(PartialState.PatientSelected(intent.nationalCode, intent.fullName)) }
        }
    }

    private fun initTreatmentFlow(): Flow<PartialState> = flow {
        emit(PartialState.Reset)
        emit(PartialState.Loading(true))

        val nationalCode = try {
            identityInfoUseCase().first().nationalId ?: ""
        } catch (e: Exception) {
            ""
        }

        if (nationalCode.isEmpty()) {
            emit(PartialState.Error("اطلاعات کاربری یافت نشد."))
            return@flow
        }

        emit(PartialState.MainUserNationalCodeLoaded(nationalCode))
        emit(PartialState.PatientSelected(nationalCode, "کاربر اصلی"))

        val deservedFlow: Flow<PartialState> = getDeservedTreatmentUseCase(nationalCode)
            .transform { list ->
                val presentationList = list.toPresentation()
                val fullName = presentationList.firstOrNull()?.fullName ?: "کاربر اصلی"
                emit(PartialState.DeservedLoaded(presentationList))
                emit(PartialState.PatientSelected(nationalCode, fullName))
            }
            .catch { e -> emit(PartialState.Error(e.message)) }

        val dependantFlow: Flow<PartialState> = getDependantUnderEighteenUseCase(nationalCode)
            .map { list -> PartialState.DependantsLoaded(list.toPresentation()) }
            .catch { e ->
                sendEvent(
                    TreatmentEvent.ShowMessage(
                        e.message ?: "خطا در دریافت لیست همراهان زیر ۱۸ سال",
                        TreatmentMessageType.OPERATION_FAILED
                    )
                )
                emit(PartialState.DependantsLoaded(emptyList()))
            }

        emitAll(merge(deservedFlow, dependantFlow))
    }

    override fun reduceState(
        currentState: TreatmentUiState,
        partialState: PartialState
    ): TreatmentUiState = when (partialState) {
        is PartialState.Reset -> TreatmentUiState()
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.DeservedLoaded -> currentState.copy(isLoading = false, deservedList = partialState.list)
        is PartialState.DependantsLoaded -> currentState.copy(isLoading = false, dependantList = partialState.list)

        // Navigation and sub-flow switches
        is PartialState.HealthProfileStatusLoaded -> currentState.copy(
            healthProfileCompleted = partialState.isCompleted
        )
        is PartialState.PatientSelected -> currentState.copy(
            selectedNationalCode = partialState.nationalCode,
            selectedPatientName = partialState.fullName,
            error = null
        )
        is PartialState.MainUserNationalCodeLoaded -> currentState.copy(
            mainUserNationalCode = partialState.nationalCode
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
