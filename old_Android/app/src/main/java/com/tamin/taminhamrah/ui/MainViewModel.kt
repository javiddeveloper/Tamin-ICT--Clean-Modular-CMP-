package com.tamin.taminhamrah.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.entity.ProfileModel
import com.tamin.taminhamrah.data.local.models.ApplicationThemeEnum
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumInsuranceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {
    fun logOut() {
        commonRepository.logOut()
    }

    val mldProfile = MutableLiveData<Resource<ProfileModel?>>()
    fun getProfileInfo() {
        mldProfile.postValue(Resource.success(commonRepository.getUserInfo()))
        /*viewModelScope.launch {
            mldProfile.postValue(Resource.loading(null))
            try {
                val result = loginRepository.getProfileInfo(getRequestUrl())
                mldProfile.postValue(result)

              *//*  if (result.isSuccess) {
                    result.data?.nationalCode?.let { fetchEligibilityFromApi(it) }
                }*//*

            } catch (e: Exception) {
                mldProfile.postValue(Resource.error(MessageModel(e.message ?: e.toString(), 0)))
            }
        }*/
    }

    val mldCheckSuccessPayment = MutableLiveData<GeneralRes>()

    fun updatePaymentStatus() {
        viewModelScope.launch {
            getSystemType()?.let {
                if (it == EnumInsuranceType.TYPE_DEBT.systemType) {
                   getEmployerDebtSerialNumber()?.let {debtSerialNumber->
                       val result =  repository.checkPaymentDebt(debtSerialNumber)
                       if (result.isSuccess)
                           saveEmployerDebtSerialNumber("")
                   }
                } else {
                    val result = callService {
                        repository.checkSuccessPaymentStatus(it)
                    }

                    if (result.isSuccess)
                        saveSystemType("")

                    mldCheckSuccessPayment.postValue(result)
                }
            }
        }
    }

    fun updateWorkersPaymentStatus() {
        viewModelScope.launch {
            getSystemType()?.let {
                val result  =  callService {
                    val ticket = repository.getWorkerPayTicket() ?: ""
                    val info = repository.getWorkerPayInfo()?: ""
                    repository.saveWorkerPayInfo(null ,null)
//                    TODO: We Must Received this info from DeepLink Data
                    repository.inspectTicket(ticket , info)
                }
                if(result.isSuccess) {
                    saveSystemType("")
                }
                mldCheckSuccessPayment.postValue(result)
            }
        }
    }

    private fun checkPaymentDebt(debtSerialNumber: String) {
        viewModelScope.launch {
          callService { repository.checkPaymentDebt(debtSerialNumber) }
        }
    }
}