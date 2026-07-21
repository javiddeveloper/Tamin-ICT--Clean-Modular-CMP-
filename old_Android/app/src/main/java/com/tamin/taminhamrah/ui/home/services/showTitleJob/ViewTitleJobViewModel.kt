package com.tamin.taminhamrah.ui.home.services.showTitleJob

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.BaseStatus
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.Date
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewTitleJobViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val mldPensionCheck = MutableLiveData<CheckInsuredInfoResponse>()

    val getTitlesJob = createPager(repository::getTitlesJob,
        Constants.QUERY_PAGE_SIZE_10.toString()).flow.cachedIn(viewModelScope)

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