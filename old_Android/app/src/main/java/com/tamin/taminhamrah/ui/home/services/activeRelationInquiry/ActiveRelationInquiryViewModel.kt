package com.tamin.taminhamrah.ui.home.services.activeRelationInquiry

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.BaseStatus
import com.tamin.taminhamrah.data.remote.models.services.ActiveRelation
import com.tamin.taminhamrah.data.remote.models.services.ActiveRelationResponse
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.Date
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.Recipient
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveRelationInquiryViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val mldActiveRelation =
        createPager(::getInsuranceActiveRelationSorted).flow.flowOn(Dispatchers.IO).cachedIn(viewModelScope)

    //val mldRecipient = createPager(repository::getRecipientList).flow.cachedIn(viewModelScope)
    val mldCertificateResponse = MutableLiveData<GeneralRes>()
    val mldPensionCheck = MutableLiveData<CheckInsuredInfoResponse>()
    var selectedItem: ActiveRelation? = null
    suspend fun getInsuranceActiveRelationSorted(paramsMap: MutableMap<String, String>?): ActiveRelationResponse {
        val data = repository.getInsuranceActiveRelation(paramsMap)
        val list = data.data?.list?.sortedWith(
            compareByDescending <ActiveRelation> {
                it.endDate
            }.thenByDescending { it.hasRelation() })
        data.data?.list = list
        return data
    }

    fun sendCertificateRequest(
        recipientId: String?,
        branchName: String?,
        branchCode: String?,
        statusCode: String?,
        insuranceNumber: String?,
        endDate: String?
    ) {
        viewModelScope.launch {
            val map = HashMap<String, String?>()
            map["recipient"] = recipientId
            map["target"] = ""
            map["branchName"] = branchName
            map["branchCode"] = branchCode
            map["statusCode"] = statusCode
            map["insuranceNumber"] = insuranceNumber
            map["endDate"] = endDate
            mldCertificateResponse.postValue(callService { repository.sendCertificateRequest(map) })
        }
    }

    //Branch
    fun getBranchRequests(branchName: String = "")
            : kotlinx.coroutines.flow.Flow<PagingData<Recipient>> {
        val array = JsonArray()
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (branchName != "") {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "recipientName")
            jsonObj.addProperty("value", "*$branchName%*")
            jsonObj.addProperty("operator", "LIKE")
            array.add(jsonObj)
        }

        paramsMap[Constants.ARRAY_KEY_FOR_MAP] = array.toString()
        val result = createPager(repository::getRecipientList, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
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

}

