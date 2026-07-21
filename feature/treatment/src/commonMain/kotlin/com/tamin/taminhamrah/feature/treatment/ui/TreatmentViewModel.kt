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
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
import com.tamin.taminhamrah.useCases.personal.GetDisabilityDependentInfoUseCase
import com.tamin.taminhamrah.mapper.personal.toPresentation
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
    private val getDisabilityDependentInfoUseCase: GetDisabilityDependentInfoUseCase,
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val featureManager: FeatureManager
) : BaseViewModel<TreatmentUiState, PartialState, TreatmentEvent, TreatmentIntent>(
    initialState = TreatmentUiState()
) {

    override fun handleIntent(intent: TreatmentIntent): Flow<PartialState> {
        return when (intent) {
            is TreatmentIntent.InitTreatmentFlow -> initTreatmentFlow()
            is TreatmentIntent.SelectPatient -> flow { emit(PartialState.PatientSelected(intent.nationalCode, intent.fullName)) }
            is TreatmentIntent.OpenRecords -> openRecords(intent.tab)
        }
    }

    /**
     * Checks the feature's flag before opening the records screen.
     *
     * Mirrors how the home services gate: enabled navigates, disabled explains itself, and
     * "enabled with error" does both so a degraded service is still reachable.
     */
    private fun openRecords(tab: RecordTab): Flow<PartialState> = flow {
        val nationalCode = uiState.value.selectedNationalCode
            ?: // Nothing to open for: the carousel has not resolved a patient yet.
            return@flow

        val status = try {
            featureManager.getFeatureStatus(FeatureFlag.PRESCRIPTION).first()
        } catch (e: Exception) {
            // A flag lookup that fails must not lock the person out of the feature.
            FeatureStatus.Enabled
        }

        when (status) {
            is FeatureStatus.Enabled ->
                sendEvent(TreatmentEvent.NavigateToRecords(nationalCode, tab))

            is FeatureStatus.EnabledWithError -> {
                status.message?.let {
                    sendEvent(TreatmentEvent.ShowMessage(it, TreatmentMessageType.OPERATION_FAILED))
                }
                sendEvent(TreatmentEvent.NavigateToRecords(nationalCode, tab))
            }

            is FeatureStatus.Disabled -> sendEvent(
                TreatmentEvent.ShowMessage(
                    status.message ?: FEATURE_UNAVAILABLE,
                    TreatmentMessageType.OPERATION_FAILED,
                ),
            )

            is FeatureStatus.TemporaryDisabled -> sendEvent(
                TreatmentEvent.ShowMessage(
                    status.message ?: FEATURE_TEMPORARILY_UNAVAILABLE,
                    TreatmentMessageType.OPERATION_FAILED,
                ),
            )

            // No in-app screen for a web-hosted service yet; saying so beats opening nothing.
            is FeatureStatus.WebView -> sendEvent(
                TreatmentEvent.ShowMessage(FEATURE_UNAVAILABLE, TreatmentMessageType.OPERATION_FAILED),
            )
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
            .catch { e ->
                // A null exception message would blank the error state and the hub would render
                // "no insured person" for what was actually a failed request.
                emit(PartialState.Error(e.message?.takeIf { it.isNotBlank() } ?: ERROR_LOAD_COVERAGE))
            }

        val dependantFlow: Flow<PartialState> = getDependantUnderEighteenUseCase(nationalCode)
            .map { list -> PartialState.DependantsLoaded(list.toPresentation()) }
            .catch { e ->
                sendEvent(
                    TreatmentEvent.ShowMessage(
                        e.message?.takeIf { it.isNotBlank() } ?: ERROR_LOAD_DEPENDANTS,
                        TreatmentMessageType.OPERATION_FAILED
                    )
                )
                emit(PartialState.DependantsLoaded(emptyList()))
            }

        // Spouse and older children: the under-18 endpoint cannot return them, and this one
        // carries the relation label the filter shows. Filters are empty because the endpoint
        // resolves the family from the signed-in user.
        val familyFlow: Flow<PartialState> = getDisabilityDependentInfoUseCase(emptyList())
            .map { list -> PartialState.FamilyDependantsLoaded(list.toPresentation()) }
            .catch {
                // The carousel and the under-18 list must still work, so this stays quiet.
                emit(PartialState.FamilyDependantsLoaded(emptyList()))
            }

        emitAll(merge(deservedFlow, dependantFlow, familyFlow))
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
        is PartialState.FamilyDependantsLoaded -> currentState.copy(
            isLoading = false,
            familyDependantList = partialState.list,
        )

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

private const val ERROR_LOAD_COVERAGE = "خطا در دریافت وضعیت استحقاق درمان"
private const val ERROR_LOAD_DEPENDANTS = "خطا در دریافت لیست همراهان زیر ۱۸ سال"

private const val FEATURE_UNAVAILABLE = "این سرویس در حال حاضر در دسترس نیست."
private const val FEATURE_TEMPORARILY_UNAVAILABLE = "این سرویس موقتاً در دسترس نیست."
