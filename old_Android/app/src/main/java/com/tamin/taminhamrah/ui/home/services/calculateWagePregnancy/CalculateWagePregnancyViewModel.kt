package com.tamin.taminhamrah.ui.home.services.calculateWagePregnancy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.remote.models.MessageModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.repository.LoginRepository
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalculateWagePregnancyViewModel @Inject constructor(val serviceRepository: ServiceRepository,val loginRepository: LoginRepository) :
    BaseViewModel() {

    val mldCalculateWagePregnancy = MutableLiveData<Resource<List<String>?>>()
    val mldCheckGender = MutableLiveData<String>()

    fun calculateWagePregnancyDays(StartDateTimeStamp: String, EndDateTimeStamp: String) {
        viewModelScope.launch {
            mldCalculateWagePregnancy.postValue(Resource.loading(null))
            try {
                val result = serviceRepository.calculateWagePregnancyDays(StartDateTimeStamp, EndDateTimeStamp)
                result.let {
                    mldCalculateWagePregnancy.postValue(it)
                }
            } catch (e: Exception) {
                mldCalculateWagePregnancy.postValue(Resource.error(MessageModel(e.message?:e.toString(),0)))
            }
        }
    }

    fun getGender() {
        val resultGender = commonRepository.getGender()
        if (!resultGender.isNullOrBlank()) {
            mldCheckGender.postValue(resultGender!!)
            return
        } else {
            getUserInfo()
        }
    }

    fun getUserInfo() {
        viewModelScope.launch {
            val response = callService {
                serviceRepository.getIdentityInfo()
            }
            if (response.isSuccess) {
                response.data?.gender?.let {
                    commonRepository.setGender(it)
                    mldCheckGender.postValue(it)
                }
            }
        }
    }

    private fun getRequestProfileInfoUrl(): String {
        return "https://profile.tamin.ir/api/v2.0/users/current-user/info"
    }

}
