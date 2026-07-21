package com.tamin.taminhamrah.ui.home.services.employer.protestStatus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.AllObjectionsResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopObjection
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowObjectionsStatusViewModel @Inject constructor(
    private val repository: ServiceRepository

) : BaseViewModel() {

    val ARG_WORKSHOP_CODE = "ARG_WORKSHOP_CODE"
    val ARG_OBJECTION_NUMBER = "ARG_OBJECTION_NUMBER"
    val ARG_DEBIT_NUMBER = "ARG_DEBIT_NUMBER"

    val mldAllObjections = MutableLiveData<AllObjectionsResponse>()
    val mldPdf = MutableLiveData<PdfDownloadResponse>()

    fun getAllObjections(
        workshopCode: String? = "",
        objectionNumber: String? = "",
        debitNumber: String? = ""
    ): Flow<PagingData<WorkShopObjection>> {

        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopCode.isNullOrEmpty())
            paramsMap["workshopId"] = workshopCode
        if (!objectionNumber.isNullOrEmpty())
            paramsMap["branchCode"] = objectionNumber
        if (!debitNumber.isNullOrEmpty())
            paramsMap["debitNumber"] = debitNumber

        val result = createPager(repository::getAllObjections, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    fun getDebitObjectionPdf(seqNo: Long?, objectionType: String?) {
        viewModelScope.launch {
            when(objectionType){
                "1","2"->
                    mldPdf.postValue(callService { repository.downloadDebitObjectionPDF(seqNo) })
                "3"->
                    mldPdf.postValue(callService { repository.downloadDebitObjectionReportPDF(seqNo) })
            }



        }
    }


}