package com.tamin.taminhamrah.ui.home.services.historyinsurance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants.QUERY_PAGE_SIZE_60
import com.tamin.taminhamrah.data.remote.models.BaseStatus
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
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
class AllHistoryInsuranceViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val mldPensionCheck = MutableLiveData<CheckInsuredInfoResponse>()
    val mldDownloadPDF = MutableLiveData<PdfDownloadResponse>()

    /**Set the pagination limit to 60 because paging is not done in the user interface due to the design of
     the items and the lack of duplicate year display.*/
    val getAllHistoryInsurance = createPager(repository::getAllHistoryInsurance,QUERY_PAGE_SIZE_60.toString()).flow.cachedIn(viewModelScope)

    fun downloadAllHistoryPDF() {
        viewModelScope.launch {
                mldDownloadPDF.postValue(callService { repository.downloadAllHistoryPDF() })
        }
    }

//    fun getUserName() = commonRepository.getUserName()

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
