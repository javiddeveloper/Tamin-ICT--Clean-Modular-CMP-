package com.tamin.taminhamrah.ui.home.services.deservedTreatment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.remote.models.services.treatmentServices.deserved.DeservedTreatmentResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeservedTreatmentViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

//    val mldDeserved = createPager(repository::getDeservedTreatment).flow.cachedIn(viewModelScope)
    val mldDeserved = MutableLiveData<DeservedTreatmentResponse>()


    fun getPersonalInfo() {
        viewModelScope.launch {
            mldDeserved.postValue(callService {
                repository.getDeservedTreatment()
            })
        }
    }

}

