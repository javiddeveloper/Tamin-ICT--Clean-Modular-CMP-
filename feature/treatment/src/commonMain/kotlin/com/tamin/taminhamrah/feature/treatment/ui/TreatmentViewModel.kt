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
import com.tamin.taminhamrah.model.common.FeatureGate
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentFeatureFlags
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform

class TreatmentViewModel(
    private val getDeservedTreatmentUseCase: GetDeservedTreatmentUseCase,
    private val getDependantUnderEighteenUseCase: GetDependantUnderEighteenUseCase,
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
            is TreatmentIntent.OpenMiscClaims -> openGated(
                TreatmentFeatureFlags.miscClaims,
                TreatmentEvent.NavigateToMiscClaims,
            )
            is TreatmentIntent.OpenApprovals -> openGated(
                TreatmentFeatureFlags.approvals,
                TreatmentEvent.NavigateToApprovals,
            )
        }
    }

    private fun openRecords(tab: RecordTab): Flow<PartialState> {
        val nationalCode = uiState.value.selectedNationalCode
            ?: // Nothing to open for: the carousel has not resolved a patient yet.
            return emptyFlow()
        return openGated(TreatmentFeatureFlags.records, TreatmentEvent.NavigateToRecords(nationalCode, tab))
    }

    /**
     * Checks the feature's flag before opening [destination].
     *
     * Mirrors how the home services gate: enabled navigates, disabled explains itself, and
     * "enabled with error" does both so a degraded service is still reachable.
     */
    private fun openGated(flag: FeatureFlag, destination: TreatmentEvent): Flow<PartialState> = flow {
        // The hub already holds the menu's answer; only a tap that beats it has to ask again.
        val status = uiState.value.featureStatuses?.get(flag) ?: try {
            featureManager.getFeatureStatus(flag).first()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // A flag lookup that fails must not lock the person out of the feature.
            FeatureStatus.Enabled
        }

        when (val gate = status.toGate()) {
            FeatureGate.Open -> sendEvent(destination)

            is FeatureGate.OpenWithWarning -> {
                gate.message?.let {
                    sendEvent(TreatmentEvent.ShowMessage(it, TreatmentMessageType.OPERATION_FAILED))
                }
                sendEvent(destination)
            }

            is FeatureGate.Blocked -> sendEvent(
                TreatmentEvent.ShowMessage(
                    gate.message ?: if (status is FeatureStatus.TemporaryDisabled) {
                        ErrorUri.FEATURE_TEMPORARILY_UNAVAILABLE.toSingleLineMessage()
                    } else {
                        ErrorUri.FEATURE_UNAVAILABLE.toSingleLineMessage()
                    },
                    TreatmentMessageType.OPERATION_FAILED,
                ),
            )

            // No in-app screen for a web-hosted service yet; saying so beats opening nothing.
            is FeatureGate.OpenWeb -> sendEvent(
                TreatmentEvent.ShowMessage(
                    ErrorUri.FEATURE_UNAVAILABLE.toSingleLineMessage(),
                    TreatmentMessageType.OPERATION_FAILED
                ),
            )
        }
    }

    /**
     * Reads every flag the hub gates on in one go, beside the patient load: the entries shimmer
     * until this answers and the patient data never waits on the menu.
     */
    private fun initTreatmentFlow(): Flow<PartialState> = merge(
        featureStatusFlow(),
        loadPatients(),
    )

    private fun featureStatusFlow(): Flow<PartialState> =
        featureManager.observeFeatureStatuses(TreatmentFeatureFlags.all)
            .map { PartialState.FeatureStatusesLoaded(it) }

    private fun loadPatients(): Flow<PartialState> = flow {
        emit(PartialState.Reset)
        emit(PartialState.Loading(true))

        val nationalCode = try {
            identityInfoUseCase().first().nationalId ?: ""
        } catch (e: CancellationException) {
            throw e
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
                // Map presentation list to ImmutableList for Compose state stability
                val presentationList = list.toPresentation().toImmutableList()
                val fullName = presentationList.firstOrNull()?.fullName ?: "کاربر اصلی"
                emit(PartialState.DeservedLoaded(presentationList))
                emit(PartialState.PatientSelected(nationalCode, fullName))
            }
            .catch { e ->
                emit(PartialState.Error(e.toSingleLineMessage()))
            }

        val dependantFlow: Flow<PartialState> = getDependantUnderEighteenUseCase(nationalCode)
            // Map presentation list to ImmutableList for Compose state stability
            .map { list -> PartialState.DependantsLoaded(list.toPresentation().toImmutableList()) }
            .catch { e ->
                sendEvent(
                    TreatmentEvent.ShowMessage(
                        e.toSingleLineMessage(),
                        TreatmentMessageType.OPERATION_FAILED
                    )
                )
                emit(PartialState.DependantsLoaded(persistentListOf()))
            }

        emitAll(merge(deservedFlow, dependantFlow))
    }

    override fun reduceState(
        currentState: TreatmentUiState,
        partialState: PartialState
    ): TreatmentUiState = when (partialState) {
        // A reload restarts the patients, not the menu's answer — dropping it would shimmer the
        // gated entries again while nothing about their flags has changed.
        is PartialState.Reset -> TreatmentUiState(featureStatuses = currentState.featureStatuses)
        is PartialState.FeatureStatusesLoaded -> currentState.copy(featureStatuses = partialState.statuses)
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
