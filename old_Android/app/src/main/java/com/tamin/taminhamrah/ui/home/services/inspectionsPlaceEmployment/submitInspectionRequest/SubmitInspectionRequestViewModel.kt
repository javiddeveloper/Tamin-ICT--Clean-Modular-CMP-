package com.tamin.taminhamrah.ui.home.services.inspectionsPlaceEmployment.submitInspectionRequest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.services.BranchDetailModel
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit.InfoInspectionResponse
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit.JobTitleModel
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit.SubmitInspectionRequestResponse
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit.SubmitResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.CurrentUserResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.inspectionsPlaceEmployment.model.SubmitInspectionDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubmitInspectionRequestViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val getJobsTitle = createPager(repository::getJobsTitle).flow.cachedIn(viewModelScope)
    val mldUserInfo = MutableLiveData<CurrentUserResponse>()
    val mldSendInspection = MutableLiveData<SubmitInspectionRequestResponse>()
    val mldInspectionInfo = MutableLiveData<InfoInspectionResponse>()
    var selectedEndDateString: Long = 0
    var selectedStartDateString: Long = 0

    val dataModel by lazy {
        SubmitInspectionDataModel()
    }
    fun getProfileInfo() {
        viewModelScope.launch {
            mldUserInfo.postValue(callService {
                repository.getCurrentUser()
            })
        }
    }

    fun sendInspectionRequest(submitResponse: SubmitResponse) {
        viewModelScope.launch {
            mldSendInspection.postValue(callService {
                repository.sendInspectionRequest(submitResponse)
            })
        }
    }

    fun getJob(jobTitle: String? = null): Flow<PagingData<JobTitleModel>> {
        val array = JsonArray()

        array.add(JsonObject().apply {
                if (!jobTitle.isNullOrBlank()) {
                    addProperty("property", "jobDescription")
                    addProperty("value", "*${jobTitle}*")
                    addProperty("operator", "LIKE")
                } else {
                    addProperty("property", "jobDescription")
                    addProperty("value", "*")
                    addProperty("operator", "LIKE")
                }
            })


        val result = createPager(repository::getJobsTitle, paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString()))
        return result.flow.cachedIn(viewModelScope)
    }

    //Branch
    fun getBranchRequests(branchName: String = "")
            : Flow<PagingData<BranchDetailModel>> {
        val array = JsonArray()

        if (branchName != "") {
            array.add( JsonObject().apply {
                addProperty("property", "name")
                addProperty("value", "*$branchName*")
                addProperty("operator", "LIKE")
            })
        }
        array.add(JsonObject().apply {
            addProperty("property", "type")
            addProperty("operator", "EQUAL")
            addProperty("value", "1")
        })
        array.add(JsonObject().apply {
            addProperty("property", "status")
            addProperty("operator", "EQUAL")
            addProperty("value", "1")
        })

        val result = createPager(repository::getBranchDetailList, paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString()))
        return result.flow.cachedIn(viewModelScope)
    }

    fun getWorkshopInspectionInfo(
        insuranceId: String?,
        inspectionCode: String,
        nationalCode: String
    ) {
        viewModelScope.launch {
            val result = callService { repository.getInspectionInfo(insuranceId, inspectionCode, nationalCode) }

            mldInspectionInfo.postValue(result)
        }
    }

}