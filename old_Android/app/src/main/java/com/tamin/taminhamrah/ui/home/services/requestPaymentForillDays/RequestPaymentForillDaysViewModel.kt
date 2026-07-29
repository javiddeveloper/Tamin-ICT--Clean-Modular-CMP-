package com.tamin.taminhamrah.ui.home.services.requestPaymentForillDays

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.remote.models.services.CovidResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.requestPaymentForillDay.RequestPaymentForIllDayResponse
import com.tamin.taminhamrah.data.remote.models.services.requestPaymentForillDay.RequestPaymentForillDayReq
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.LatestInsuranceInfoResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class RequestPaymentForillDaysViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val mldLatestInsuranceInfo = MutableLiveData<LatestInsuranceInfoResponse>()

    fun getLatestInsuranceInfo() {
        viewModelScope.launch {
            mldLatestInsuranceInfo.postValue(callService {
                repository.getLatestInsuranceInfo()
            })
        }
    }

    fun getCityList(cityName: String = "") = createPager(repository::getCityList,
        paramsMap = hashMapOf("cityName" to "*$cityName%*")).flow.cachedIn(viewModelScope)


    val mldUploadImage = MutableLiveData<UploadImageResponse>()
    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            mldUploadImage.postValue(callService {
                repository.uploadImage(image)
            })
        }
    }

    val mldCovidResult = MutableLiveData<CovidResponse>()

    fun getCovidResult() {
        viewModelScope.launch {
            val result = callService {
                repository.getCovidResult()
            }
            mldCovidResult.postValue(result)
        }
    }

    val mldSendRequestForIllDay = MutableLiveData<RequestPaymentForIllDayResponse>()

    fun sendRequestForIllDay(illDayReq: RequestPaymentForillDayReq) {
        viewModelScope.launch {
            mldSendRequestForIllDay.postValue(callService {
                repository.sendRequestForIllDay(illDayReq)
            })
        }
    }


}