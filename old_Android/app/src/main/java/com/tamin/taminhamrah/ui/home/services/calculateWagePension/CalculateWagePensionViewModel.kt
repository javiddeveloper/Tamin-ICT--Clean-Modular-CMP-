package com.tamin.taminhamrah.ui.home.services.calculateWagePension

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordResponse
import com.tamin.taminhamrah.data.remote.models.services.PersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.MultipleWorkShopResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalculateWagePensionViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {
    val mldCombinedList = MutableLiveData<CombinedRecordResponse>()
    val mldGetWageAndHistory = MutableLiveData<WageAndHistoryResponse>()

    val mldIsMultipleWorkshops = MutableLiveData<MultipleWorkShopResponse>()
    val mldCalculateMultipleWorkshops = MutableLiveData<MultipleWorkShopResponse>()
  //  val mldLastRelation = MutableLiveData<LastRelationResponse>()
   // val mldIdentityInfo = MutableLiveData<UserInfoResponse>()
    val mldPersonalInfo = MutableLiveData<PersonalInfoResponse>()

    fun isMultipleWorkshops(branchCode: String, insuranceNumber: String) {
        viewModelScope.launch {
            mldIsMultipleWorkshops.postValue(callService {
                repository.isMultipleWorkshops(branchCode, insuranceNumber)
            })
        }
    }

    fun calculateMultipleWorkshops(branchCode: String, insuranceNumber: String) {
        viewModelScope.launch {
            mldCalculateMultipleWorkshops.postValue(callService {
                repository.calculateMultipleWorkshops(branchCode, insuranceNumber)
            })
        }
    }

//    fun getLastRelation() {
//        viewModelScope.launch {
//            mldLastRelation.postValue(callService {
//                repository.getLastRelation()
//            })
//        }
//    }

    fun getPersonalInfo(){
        viewModelScope.launch {
            mldPersonalInfo.postValue(callService {
                repository.getPersonalInfoDetail()
            })
        }
    }


//    fun getIdentityInfo() {
//        viewModelScope.launch {
//            val result = callService {
//                repository.getUserInfo()
//            }
//            mldIdentityInfo.postValue(result)
//            if (result.isSuccess) {
//                result.data?.genderCode?.let {
//                    commonRepository.setGender(it)
//                }
//            }
//        }
//    }


    fun getCombinedRecordList() {
        viewModelScope.launch {
            mldCombinedList.postValue(callService {
                repository.getCombinedRecordList(null)
            })
        }
    }

    fun getWageAndInsuranceHistory() {
        viewModelScope.launch {
            mldGetWageAndHistory.postValue(callService {
                repository.getWageAndHistoryInsurance(null)
            })
        }
    }

}