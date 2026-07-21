package com.tamin.taminhamrah.ui.home.services.identityinfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.remote.models.services.CityNameListResponse
import com.tamin.taminhamrah.data.remote.models.services.CityNameModel
import com.tamin.taminhamrah.data.remote.models.services.IdentityInfoResponse.IdentityInfo
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdentityInfoInquiryViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val mldIdentityInfo = MutableLiveData<IdentityInfo?>()
    val mldIdTaminNumber = MutableLiveData<String?>()
    val mldCityBirth = MutableLiveData<CityNameListResponse?>()
    val mldCityPlaceIssue = MutableLiveData<CityNameListResponse?>()
    val mldGetAllCity = MutableLiveData<ListDataModel<CityNameModel>?>()

    fun getIdentityInfo() {
        viewModelScope.launch {
            val response = callService {
                repository.getIdentityInfo()
            }
            if (response.isSuccess) {
                response.data?.apply {
                    ssn?.let {
                        mldIdTaminNumber.postValue(it)
                    }
                    gender?.let {
                        commonRepository.setGender(it)
                    }
                    mobileNumber = commonRepository.getUserPhoneNumber()
                    email = commonRepository.getUserEmail()
                }
            }
            mldIdentityInfo.postValue(response.data)
        }
    }

    fun getCityName(cityBirth: String, cityPlaceIssue: String) {
        viewModelScope.launch {
            val map = HashMap<String,String>()
            map["cityCode"] = cityBirth
            val cityBirthRes = callService { repository.getCityName(map) }
            mldCityBirth.postValue(cityBirthRes)
            if (cityBirth == cityPlaceIssue) {
                mldCityPlaceIssue.postValue(cityBirthRes)
            } else {
                val mapPlaceIssue = HashMap<String,String>()
                mapPlaceIssue["cityCode"] = cityPlaceIssue
                mldCityPlaceIssue.postValue(callService { repository.getCityName(mapPlaceIssue) })
            }
        }
    }
}

