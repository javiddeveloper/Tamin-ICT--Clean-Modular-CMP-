package com.tamin.taminhamrah.feature.profile.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.core.model.request.FilterOperator
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState.PartialState
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileIntent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileEvent
import com.tamin.taminhamrah.mapper.identity.toPresentation
import com.tamin.taminhamrah.model.relation.toPresentation
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.useCases.bankAccount.GetBankAccountListUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.SendImageRequestUseCase
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import com.tamin.taminhamrah.useCases.user.TaminRelationUseCase
import com.tamin.taminhamrah.useCases.user.UserProfileImageUseCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

class ProfileViewModel(
    private val tokenStoreManager: TokenStoreManager,
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val getUserProfileImageUseCase: UserProfileImageUseCase,
    private val taminRelationUseCase: TaminRelationUseCase,
    private val sendImageRequestUseCase: SendImageRequestUseCase,
    private val subdominantUseCase: SubdominantUseCase,
    private val getBankAccountListUseCase: GetBankAccountListUseCase,
    private val getInsuredActiveBranchUseCase: GetInsuredActiveBranchUseCase,
    private val getRelationTaminAllUseCase: GetRelationTaminAllUseCase,
    private val getElectronicFileUseCase: GetElectronicFileUseCase,
) : BaseViewModel<ProfileUiState, PartialState, ProfileEvent, ProfileIntent>(
    initialState = ProfileUiState()
) {

    override fun handleIntent(intent: ProfileIntent): Flow<PartialState> {
        return when (intent) {
            is ProfileIntent.LoadProfile -> handleLoadProfile(intent.userId)
            is ProfileIntent.Logout -> handleLogout()
            is ProfileIntent.OnItemClick -> handleItemClick(intent.title)
            is ProfileIntent.SendImageRequest -> handleSendImageRequest(
                intent.branchCode,
                intent.filter
            )
            is ProfileIntent.LoadSubDominants -> handleLoadSubDominants()
            is ProfileIntent.LoadBankAccountList -> handleLoadElectronicFile()
        }
    }

    private fun handleLoadProfile(providedUserId: String?): Flow<PartialState> {

        val userIdFlow = flow {
            emit(PartialState.Loading(true))
            val userId = providedUserId ?: tokenStoreManager.getUserId()
            emit(PartialState.SetUserId(userId))
        }
        val imageFlow = flow {
            getUserProfileImageUseCase().collect { imageBase64 ->
                emit(
                    PartialState.ProfileImageLoaded(
                        imageBase64
                    ))
            }
        }
        val identityFlow = flow {
            identityInfoUseCase().collect { identityInfo ->
                emit(
                    PartialState.IdentityInfoLoaded(
                        identityInfo.toPresentation()
                    ))
            }
        }
        val taminRelationFlow = flow {
            taminRelationUseCase().collect { taminRelation ->
                emit(
                    PartialState.TaminRelationLoaded(taminRelation.toPresentation()
                    )
                )
            }
        }

        return merge(userIdFlow, imageFlow, identityFlow, taminRelationFlow)
    }

    private fun handleLogout(): Flow<PartialState> = flow {
        tokenStoreManager.saveToken(null)
        tokenStoreManager.saveRefreshToken(null)
        tokenStoreManager.setTokenValid(isValid = false)
        sendEvent(ProfileEvent.NavigateBack)
    }

    private fun handleItemClick(title: String): Flow<PartialState> {
        when (title) {
            "تنظیمات" -> sendEvent(ProfileEvent.NavigateToSettings)
            "خروج از حساب کاربری" -> sendIntent(ProfileIntent.Logout)
            "اطلاعات هویتی" -> sendEvent(ProfileEvent.NavigateToIdentity)
            else -> sendEvent(ProfileEvent.ShowToast("کلیک بر روی: $title"))
        }
        return emptyFlow()
    }

    private fun handleSendImageRequest(branchCode: String, filter: String): Flow<PartialState> =
        flow {
            val domainFilters = listOf(
                ApiFilterDN(
                    property = "serialId",
                    operator = FilterOperator.EQ,
                    value = filter
                )
            )
            emit(PartialState.ImageRequestLoading(true))
            sendImageRequestUseCase(branchCode, domainFilters)
                .collect { result ->
                    emit(PartialState.ImageRequestResult(result))
                }
        }

    //todo it should removed from here this is only test
    private fun handleLoadSubDominants(): Flow<PartialState> {
        return flow {
            emit(PartialState.ScreenStateChanged.Loading)
            subdominantUseCase.invoke(
                page = "1",
                start = "0",
                limit = "10",
                filter = "[]",
                sort = "[]"
            ).collect {
                emit(PartialState.ScreenStateChanged.Success)
            }
        }
    }

    private fun handleLoadBankAccountList(): Flow<PartialState> {
        return flow {
            emit(PartialState.ScreenStateChanged.Loading)
            getBankAccountListUseCase.invoke(page = "1",
                start = "0",
                limit = "10",
                filter = "[]",
                sort = "[]").collect {
                    emit(PartialState.ScreenStateChanged.Success)
            }
        }
    }

    private fun handleGetInsuranceActiveBranch(): Flow<PartialState> {
        return flow {
            emit(PartialState.ScreenStateChanged(AsyncState.Loading))
            getInsuredActiveBranchUseCase.invoke().collect {
                emit(PartialState.ScreenStateChanged(AsyncState.Success(Unit)))
            }
        }
    }

    private fun handleGetRelationTaminAll(): Flow<PartialState> {
        return flow {
            emit(PartialState.ScreenStateChanged(AsyncState.Loading))
            getRelationTaminAllUseCase.invoke().collect {
                emit(PartialState.ScreenStateChanged(AsyncState.Success(Unit)))
            }
        }
    }

    private fun handleLoadElectronicFile(): Flow<PartialState> {
        return flow {
            emit(PartialState.ScreenStateChanged(AsyncState.Loading))
            getElectronicFileUseCase.invoke().collect {
                emit(PartialState.ScreenStateChanged(AsyncState.Success(Unit)))
            }
        }
    }


    override fun reduceState(
        currentState: ProfileUiState,
        partialState: PartialState
    ): ProfileUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )

        is PartialState.SetUserId -> currentState.copy(
            isLoading = false,
            userId = partialState.userId
        )

        is PartialState.ProfileImageLoaded -> currentState.copy(
            profileImage = partialState.image
        )
        is PartialState.IdentityInfoLoaded -> currentState.copy(
            identityInfo = partialState.info
        )

        is PartialState.TaminRelationLoaded -> currentState.copy(
            taminRelation = partialState.relation
        )

        is PartialState.ImageRequestLoading -> currentState.copy(
            isImageRequestLoading = partialState.isLoading,
            imageRequestError = null
        )

        is PartialState.ImageRequestResult -> currentState.copy(
            isImageRequestLoading = false,
            imageRequestResult = partialState.result
        )
        is PartialState.ImageRequestError -> currentState.copy(
            isImageRequestLoading = false,
            imageRequestError = partialState.message
        )

        is PartialState.ScreenStateChanged -> currentState
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)

}
