package com.tamin.taminhamrah.ui.home.services.retirementRequest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.local.models.TimerState
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.IdentityInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.AuthenticationResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.ConfirmIdentityAndHistoryInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementConfirmIdentityInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementPersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementRequestInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementSaveDocumentRequest
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementStatusResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.LoadingState
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.retirementRequest.RetirementPensionFragment.Companion.IMAGE_DOC_REQUEST_CODE
import com.tamin.taminhamrah.ui.home.services.retirementRequest.model.RetirementDataModel
import com.tamin.taminhamrah.utils.TimerUtil
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.isNumericString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class RetirementPensionViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {
    val dataModel by lazy {
        RetirementDataModel()
    }

    fun getImageUrl() = "https://ssodcfs.tamin.ir/Eservices/icon-eservices/protest.svg"

    val mldUserInfo = MutableLiveData<IdentityInfoResponse>()
    val mldWageAndHistory = MutableLiveData<WageAndHistoryResponse>()
    val mldCombinedHistory = MutableLiveData<CombinedRecordResponse>()
    val mldCheckPensionRegistration = MutableLiveData<GeneralRes>()
    val mldAuthenticationCode = MutableLiveData<AuthenticationResponse>()
    val vldCheckingPossibility = MutableLiveData<GeneralRes>()
    val cldCheckRetirementStatus = MutableLiveData<RetirementStatusResponse>()
    val mldAuthenticationAndGetPersonalInfo = MutableLiveData<RetirementPersonalInfoResponse>()
    val mldUploadImage = MutableLiveData<UploadImageResponse>()
    val mldUploadResignation = MutableLiveData<UploadImageResponse>()
    val mldRequestInfo = MutableLiveData<RetirementRequestInfoResponse>()
    val mldConfirmIdentityAndHistory = MutableLiveData<RetirementConfirmIdentityInfoResponse>()
    val mldSendRetirementDocument = MutableLiveData<GeneralRes>()
    val mldSendRetirementResignationDocument = MutableLiveData<GeneralRes>()
    val mldSendDocument = MutableLiveData<GeneralRes>()
    private val timerIntent = TimerUtil(viewModelScope)
    val timerStateFlow: StateFlow<TimerState> = timerIntent.timerStateFlow

    fun timerStart() = timerIntent.timerStart(900)//900000

    fun resumeTimer() = timerIntent.resumeTimer()
    fun getUserInfoAndUserAge() {
        viewModelScope.launch {
            var birthDate: Long? = null
            val identityInfoResponse = callService({ repository.getIdentityInfo() },
                isBackToPrevious = true)
            if (identityInfoResponse.isSuccess) {
                birthDate = identityInfoResponse.data?.dateOfBirth
            }
            val ageResponse = callService({ repository.getUserAge(birthDate) },
                isBackToPrevious = true)
            if (ageResponse.isSuccess) {
                val ageArray = ageResponse.data.age?.split(",") ?: emptyList()
                if (ageArray.size >= 3) {
                    dataModel.apply {
                        yearsAge = if (ageArray[0].isNumericString()) ageArray[0].toInt() else 0
                        monthsAge = ageArray[1]
                        daysAge = ageArray[2]
                        strAge = "$yearsAge سال و $monthsAge ماه و $daysAge روز"
                    }
                }
            } else {
                mldErrorState.postValue(ageResponse)
            }
            mldUserInfo.postValue(identityInfoResponse)
        }
    }

    fun checkRetirementStatus() {
        viewModelScope.launch {
            cldCheckRetirementStatus.postValue(callService(startLoading = true,
                call = { repository.checkRetirementStatus() }, isBackToPrevious = true))
        }
    }

    fun getHistoryInfo() {
        viewModelScope.launch {
            val combinedHistoryDeffer = async { repository.getCombinedRecordList(null) }
            val wageAndHistoryDeffer = async { repository.getWageAndHistoryInsurance(null) }
            val combinedHistoryResponse = combinedHistoryDeffer.await()
            val wageAndHistoryResponse = wageAndHistoryDeffer.await()
            when {
                !wageAndHistoryResponse.isSuccess -> {
                    wageAndHistoryResponse.isBackToPrevious = true
                    mldErrorState.postValue(wageAndHistoryResponse)
                }
                !combinedHistoryResponse.isSuccess -> {
                    wageAndHistoryResponse.isBackToPrevious = true
                    mldErrorState.postValue(combinedHistoryResponse)
                }
            }
            val list = combinedHistoryResponse.data?.list ?: emptyList()
            if (combinedHistoryResponse.isSuccess && list.isNotEmpty()) {
                val normalizedDuration = Utility.normalizeHistoryDuration(
                    list[0].historyYears,
                    list[0].historyMonths,
                    list[0].historyDays
                )
                dataModel.sumHistoryDays = list[0].sumHistoryYears ?: "0"
                dataModel.historyYears = normalizedDuration.years.toString()
                dataModel.historyMonths = normalizedDuration.months.toString()
                dataModel.historyDays = normalizedDuration.days.toString()
            }
            mldWageAndHistory.postValue(wageAndHistoryResponse)
            hideLoading()
        }
    }

    fun getAuthenticationCode() {
        viewModelScope.launch {
            mldAuthenticationCode.postValue(callService { repository.getAuthenticationCode() })
        }
    }

    fun showLoading() {
        mldLoadingState.postValue(LoadingState.LOADING)
    }

    fun hideLoading() {
        mldLoadingState.postValue(LoadingState.NOT_LOADING)
    }

    fun authenticationAndGetPersonalInfo(authenticationsCode: Long) {
        viewModelScope.launch {
            mldAuthenticationAndGetPersonalInfo.postValue(callService
            { repository.authenticationAndGetPersonalInfo(authenticationsCode = authenticationsCode) })
        }
    }

    fun uploadImage(image: MultipartBody.Part, requestType: Int = IMAGE_DOC_REQUEST_CODE) {
        viewModelScope.launch {
            val response = callService {
                repository.uploadImage(image)
            }
            if (requestType == IMAGE_DOC_REQUEST_CODE)
                mldUploadImage.postValue(response)
            else
                mldUploadResignation.postValue(response)
        }
    }

    fun getRetirementRequestInfo(requestId: String) {
        viewModelScope.launch {
            mldRequestInfo.postValue(callService {
                repository.getRetirementRequestInfo(hashMapOf("request.id" to requestId))
            })
        }
    }

    fun confirmIdentityAndHistoryInfo(
        authenticationsCode: Long,
        body: ConfirmIdentityAndHistoryInfoRequest,
    ) {
        viewModelScope.launch {
            mldConfirmIdentityAndHistory.postValue(callService {
                repository.confirmIdentityAndHistoryInfo(authenticationsCode = authenticationsCode,
                    body = body)
            })
        }
    }

    fun sendRetirementDocument(requestId: String, body: RetirementSaveDocumentRequest) {
        viewModelScope.launch {
            mldSendRetirementDocument.postValue(callService {
                repository.sendRetirementDocument(requestId = requestId,
                    body = body)
            })
        }
    }


}