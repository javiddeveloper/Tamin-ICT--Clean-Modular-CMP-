package com.tamin.taminhamrah.ui.home.services.mergeHistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.BaseStatus
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordModel
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordResponse
import com.tamin.taminhamrah.data.remote.models.services.Date
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.remote.models.services.SendInsuranceHistoryToInstitutionModel
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.LoadingState
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.MultipleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MergeHistoryViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val mldPensionCheck = MutableLiveData<CheckInsuredInfoResponse>()
    val mldDownloadPdf: MultipleLiveData<PdfDownloadResponse> =  MultipleLiveData<PdfDownloadResponse>()
    val mldWageAndHistory = MutableLiveData<WageAndHistoryResponse>()
    val mldCombinedHistory = MutableLiveData<CombinedRecordResponse>()
    var originalYearList: MutableList<CombinedRecordModel> = mutableListOf()

    val mldSendHistoryCertificate = MultipleLiveData<SendInsuranceHistoryToInstitutionModel>()

    fun sendAllInsuranceHistoryToInstitution(){
        viewModelScope.launch {
            val result = callService { repository.sendAllInsuranceHistoryToInstitution() }
            if (result.isSuccess && result.data != null) {
                mldSendHistoryCertificate.postValue(result.data)
            }
        }
    }
    fun getHistoryInfo() {
        viewModelScope.launch {
            mldLoadingState.postValue(LoadingState.LOADING)

            val combineHistory = async(Dispatchers.IO) {
                repository.getCombinedRecordList(getDefaultParamsMap())
            }
            val wageAndHistory = async(Dispatchers.IO) {
                repository.getWageAndHistoryInsurance(getDefaultParamsMap())
            }
            val responseCombineHistory = combineHistory.await()
            val responseWageAndHistory = wageAndHistory.await()
            mldLoadingState.postValue(LoadingState.NOT_LOADING)

            when {
                !responseCombineHistory.isSuccess -> {
                    responseCombineHistory.isBackToPrevious = true
                    mldErrorState.postValue(responseCombineHistory)
                }
                !responseWageAndHistory.isSuccess -> {
                    responseCombineHistory.isBackToPrevious = true
                    mldErrorState.postValue(responseWageAndHistory)
                }
                else -> {
                    mldCombinedHistory.postValue(responseCombineHistory)
                    mldWageAndHistory.postValue(responseWageAndHistory)
                }
            }

        }
    }

    fun downloadAllHistoryPdf() {
        viewModelScope.launch {
           val result = callService { repository.downloadAllHistoryPDF() }
            mldDownloadPdf.postValue(result)
        }
    }
    fun downloadCombinedRecordPdf() {
        viewModelScope.launch {
            mldDownloadPdf.postValue(callService { repository.downloadTalfighiPdf() })
        }
    }

    fun downloadWageAndHistoryPdf() {
        viewModelScope.launch {
            mldDownloadPdf.postValue(repository.downloadWageAndHistoryPDF())
        }
    }

    fun pensionCheck() {
        viewModelScope.launch {
            if (commonRepository.getUserType() == EnumTypeUser.ANONYMOUS.title) {
                val result = callService { repository.checkInsuredInfo() }
                mldPensionCheck.postValue(result)
                if (result.isSuccess) {
                    commonRepository.setUserType(result.data?.getUserType().toString())
                }
            } else {
                val result = CheckInsuredInfoResponse()
                result.baseStatus = BaseStatus(null, ServiceStatus.SUCCESS)
                result.data = Date(typeUser = commonRepository.getUserType())
                mldPensionCheck.postValue(result)
            }
        }
    }


    /**Set the pagination limit to 60 because paging is not done in the user interface due to the design of
    the items and the lack of duplicate year display.*/
    fun getDefaultParamsMap(): MutableMap<String, String> {
        val paramsMap = mutableMapOf<String, String>()
        paramsMap[Constants.PAGE] = Constants.DEFAULT_START_INDEX
        paramsMap[Constants.QUERY_PAGE_SIZE] = Constants.QUERY_PAGE_SIZE_60.toString()
        paramsMap[Constants.START] = Constants.DEFAULT_START_INDEX
        return paramsMap
    }

    val mldDownloadHistoryList =
        createLocalPager(getDownloadHistoryList()).flow.cachedIn(viewModelScope)

    fun getDownloadHistoryList(): ArrayList<MenuModel> {
        val list = ArrayList<MenuModel>()
        list.add(MenuModel("کلیه سوابق", "0"))
        list.add(MenuModel("سوابق و ریز دستمزدها بعد 86", "1"))
        list.add(MenuModel("سوابق تلفیقی", "2"))
        return list
    }


}

