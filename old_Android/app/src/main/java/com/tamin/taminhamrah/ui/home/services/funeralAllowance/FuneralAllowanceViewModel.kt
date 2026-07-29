package com.tamin.taminhamrah.ui.home.services.funeralAllowance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.CorrectedAccountNumberResponse
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.DeceasedInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.FuneralAllowanceRequest
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.FuneralAllowanceResponse
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.RequestAllowanceFuneralResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FuneralAllowanceViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val mldFuneralInfoResponse = MutableLiveData<FuneralAllowanceResponse>()
    val mldInquiryDeceasedInfo = MutableLiveData<DeceasedInfoResponse>()
    val mldSubmitAllowanceFuneral = MutableLiveData<RequestAllowanceFuneralResponse>()
    val mldCorrectedAccountNumber = MutableLiveData<CorrectedAccountNumberResponse>()
    val requestModel by lazy {
        FuneralAllowanceRequest()
    }
    var requestId = 0L

    fun getInfoFuneral() {
        viewModelScope.launch {
            mldFuneralInfoResponse.postValue(callService {
                callService { repository.getInfoFuneral() }
            })
        }
    }

    fun inquiryDeceasedInfo(nationalCode: String) {
        viewModelScope.launch {
            mldInquiryDeceasedInfo.postValue(callService {
                repository.inquiryDeceasedInfo(nationalCode)
            })
        }
    }

    fun submitRequestFuneralAllowance(funeralGrantReq: FuneralAllowanceRequest) {
        viewModelScope.launch {
            mldSubmitAllowanceFuneral.postValue(callService {
                repository.submitRequestFuneralAllowance(funeralGrantReq)
            })
        }
    }

    fun correctedAccountNumber(requestId: String) {
        viewModelScope.launch {
            mldCorrectedAccountNumber.postValue(callService {
                repository.correctedAccountNumber(requestId)
            })
        }
    }


}