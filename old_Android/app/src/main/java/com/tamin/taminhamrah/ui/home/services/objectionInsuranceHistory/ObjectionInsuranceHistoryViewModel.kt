package com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.services.ObjectionInsuranceHistoryModel
import com.tamin.taminhamrah.data.remote.models.services.objectionInsuranceHistory.CheckStatusConflictResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionInsuranceHistory.FinalConfirmConflictResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionInsuranceHistory.ResultRequestSaveOfObjectionInsuranceResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionInsuranceHistory.SendConfirmConflictResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.ConfirmConflictResponseItem
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ObjectionInsuranceHistoryViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val mldRequestObjectionInsurance = MutableLiveData<ResultRequestSaveOfObjectionInsuranceResponse>()
    val mldSendConfirmConflict = MutableLiveData<SendConfirmConflictResponse>()
    val mldCheckStatusConflict = MutableLiveData<CheckStatusConflictResponse>()
    val mldSendFinalConfirmConflict = MutableLiveData<FinalConfirmConflictResponse>()


    fun checkStatusConflict() {
        viewModelScope.launch {
            mldCheckStatusConflict.postValue(callService {
                repository.checkStatusConflict()
            })
        }
    }

    /**Set the pagination limit to 60 because paging is not done in the user interface due to the design of
    the items and the lack of duplicate year display.*/
    val getObjectionInsuranceHistory = createPager(repository::getObjectionInsuranceHistory,
        Constants.QUERY_PAGE_SIZE_60.toString()
    ).flow.cachedIn(viewModelScope)


    fun postRequestObjectionInsuranceHistory(list :List<ObjectionInsuranceHistoryModel>) {
        viewModelScope.launch {
            mldRequestObjectionInsurance.postValue(callService {
                repository.sendObjectionInsuranceHistory(list)
            })
        }
    }
    fun sendConfirmConflict(list :List<ConfirmConflictResponseItem>) {
        viewModelScope.launch {
            mldSendConfirmConflict.postValue(callService {
                repository.sendConfirmConflict(list)
            })
        }
    }
    fun sendfinalconfirmconflict() {
        viewModelScope.launch {
            mldSendFinalConfirmConflict.postValue(callService {
                 repository.sendFinalConfirmConflict()
            })
        }
    }

}