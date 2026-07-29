package com.tamin.taminhamrah.ui.home.services.historyCertificate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.BaseStatus
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.Date
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.remote.models.services.SendInsuranceHistoryToInstitutionResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryCertificateViewModel @Inject constructor(private val repository: ServiceRepository) : BaseViewModel() {

    val mldSendHistoryCertificate = MutableLiveData<SendInsuranceHistoryToInstitutionResponse>()
    val mldPensionCheck = MutableLiveData<CheckInsuredInfoResponse>()

    fun sendInsuranceHistoryToInstitution(
        allHistorySelected: Boolean,
        historyAndWageSelected: Boolean,
        combineHistorySelected: Boolean
    ) {
        viewModelScope.launch {
            mldSendHistoryCertificate.postValue(callService {
                repository.sendInsuranceHistoryToInstitution(
                    allHistorySelected,
                    historyAndWageSelected,
                    combineHistorySelected
                )
            })
        }
    }

    fun getHistoryInsuranceTypeList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("کلیه سوابق", "1"))
        itemList.add(MenuModel("سوابق و ریز دستمزدها بعد از سال 86", "2"))
        itemList.add(MenuModel("سوابق تلفیقی", "3"))
        return itemList
    }

    fun pensionCheck() {
        viewModelScope.launch {
            if(commonRepository.getUserType() == EnumTypeUser.ANONYMOUS.title ) {
                val result = callService { repository.checkInsuredInfo() }
                mldPensionCheck.postValue(result)
                if (result.isSuccess) {
                    commonRepository.setUserType(result.data?.getUserType().toString())
                }
            }else {
                val result = CheckInsuredInfoResponse()
                result.baseStatus = BaseStatus(null, ServiceStatus.SUCCESS)
                result.data = Date(typeUser= commonRepository.getUserType())
                mldPensionCheck.postValue(result)
            }
        }
    }

}