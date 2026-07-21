package com.tamin.taminhamrah.ui.home.services.calculateMarriageAllowance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.remote.models.responses.CalculateMarriageResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalculateMarriageAllowanceViewModel @Inject constructor(private val repository : ServiceRepository) : BaseViewModel() {

    val mldCalculateMarriageAllowance = MutableLiveData<CalculateMarriageResponse> ()

    fun calculateMarriageAllowance(timeStamp: String) {
        viewModelScope.launch {
            mldCalculateMarriageAllowance.postValue(callService {
                repository.calculateMarriageAllowance(timeStamp)
            })
//            try {
//                val result = repository.calculateMarriageAllowance(timeStamp)
//                result.let {
//                    mldCalculateMarriageAllowance.postValue(it)
//                }
//            } catch (e: Exception) {
//                mldCalculateMarriageAllowance.postValue(Resource.error(MessageModel(e.message?:e.toString(),0)))
//            }
        }
    }

}