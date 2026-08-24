package com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.SurvivorContactDraft
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoEvent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoUiState
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoUiState.PartialState
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.DependencyTypeDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.personal.SaveSurvivorInfoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.pension_survivor_survivor_info_save_success

class SurvivorInfoViewModel(
    private val saveSurvivorInfoUseCase: SaveSurvivorInfoUseCase,
) : BaseViewModel<SurvivorInfoUiState, PartialState, SurvivorInfoEvent, SurvivorInfoIntent>(
    initialState = SurvivorInfoUiState(),
) {

    override fun handleIntent(intent: SurvivorInfoIntent): Flow<PartialState> =
        handleIntentInternal(intent).catch { error ->
            sendEvent(SurvivorInfoEvent.ShowError(error.toSingleLineMessage()))
            emit(createErrorState(error.toSingleLineMessage()))
        }

    private fun handleIntentInternal(intent: SurvivorInfoIntent): Flow<PartialState> = flow {
        when (intent) {
            is SurvivorInfoIntent.Init -> {
                if (uiState.value.survivor == null) {
                    emit(
                        PartialState.Initialized(
                            survivor = intent.survivor,
                            deceasedNationalId = intent.deceasedNationalId,
                            address = intent.address,
                            phoneNumber = intent.phoneNumber,
                            mobileNumber = intent.mobileNumber,
                        ),
                    )
                }
            }

            is SurvivorInfoIntent.AddressChanged -> {
                emit(PartialState.AddressChanged(intent.value))
            }

            is SurvivorInfoIntent.PhoneNumberChanged -> {
                emit(PartialState.PhoneNumberChanged(intent.value.filter(Char::isDigit).take(MAX_PHONE_LENGTH)))
            }

            is SurvivorInfoIntent.MobileNumberChanged -> {
                emit(PartialState.MobileNumberChanged(intent.value.filter(Char::isDigit).take(MAX_MOBILE_LENGTH)))
            }

            SurvivorInfoIntent.Save -> saveSurvivor()
            SurvivorInfoIntent.OnBack -> sendEvent(SurvivorInfoEvent.NavigateBack)
        }
    }

    override fun reduceState(
        currentState: SurvivorInfoUiState,
        partialState: PartialState,
    ): SurvivorInfoUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Initialized -> currentState.copy(
            survivor = partialState.survivor,
            deceasedNationalId = partialState.deceasedNationalId,
            address = partialState.address,
            phoneNumber = partialState.phoneNumber,
            mobileNumber = partialState.mobileNumber,
        )
        is PartialState.AddressChanged -> currentState.copy(address = partialState.value)
        is PartialState.PhoneNumberChanged -> currentState.copy(phoneNumber = partialState.value)
        is PartialState.MobileNumberChanged -> currentState.copy(mobileNumber = partialState.value)
        is PartialState.Error -> currentState.copy(isLoading = false)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private suspend fun FlowCollector<PartialState>.saveSurvivor() {
        val state = uiState.value
        val survivor = state.survivor ?: return
        if (state.isLoading) return

        emit(PartialState.Loading(true))
        saveSurvivorInfoUseCase(
            body = SaveSurvivorInfoDN(
                address = state.address.ifBlank { null },
                birthDate = survivor.dateOfBirth.toLongOrNull(),
                survivorInsuranceId = survivor.insuranceId.ifBlank { null },
                survivorNationalId = survivor.nationalId.ifBlank { null },
                dependencyType = survivor.tendencyCode
                    .ifBlank { null }
                    ?.let { DependencyTypeDN(code = it) },
                fatherName = survivor.fatherName.ifBlank { null },
                firstName = survivor.firstName.ifBlank { null },
                gender = survivor.genderCode.ifBlank { null },
                idCardNumber = survivor.idCardNumber.ifBlank { null },
                insuranceNumber = survivor.insuranceId.ifBlank { null },
                issuePlace = survivor.cityOfIssue.ifBlank { null },
                lastName = survivor.lastName.ifBlank { null },
                mobileNumber = state.mobileNumber.ifBlank { null },
                deceasedNationalId = state.deceasedNationalId.ifBlank { null },
                pensionRequestDocList = emptyList(),
                phoneNumber = state.phoneNumber.ifBlank { null },
                status = INITIAL_STATUS,
            ),
        ).collect { message ->
            emit(PartialState.Loading(false))
            sendEvent(
                SurvivorInfoEvent.Saved(
                    nationalId = survivor.nationalId,
                    draft = SurvivorContactDraft(
                        address = state.address,
                        phoneNumber = state.phoneNumber,
                        mobileNumber = state.mobileNumber,
                    ),
                ),
            )
            sendEvent(
                SurvivorInfoEvent.ShowSuccess(
                    message = message?.takeIf(String::isNotBlank)
                        ?: getString(Res.string.pension_survivor_survivor_info_save_success),
                ),
            )
            sendEvent(SurvivorInfoEvent.NavigateBack)
        }
    }

    private companion object {
        const val INITIAL_STATUS = "3"
        const val MAX_PHONE_LENGTH = 11
        const val MAX_MOBILE_LENGTH = 11
    }
}
