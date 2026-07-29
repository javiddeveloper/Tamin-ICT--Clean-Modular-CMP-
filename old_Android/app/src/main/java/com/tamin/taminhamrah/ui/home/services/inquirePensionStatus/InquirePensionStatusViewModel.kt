package com.tamin.taminhamrah.ui.home.services.inquirePensionStatus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.Recipient
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InquirePensionStatusViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {
    val mldCertificateResponse = MutableLiveData<GeneralRes>()

    val getResultOfInquirePension = createPager(
        repository::getResultOfInquirePension,
        Constants.QUERY_PAGE_SIZE_10.toString()
    ).flow.cachedIn(viewModelScope)

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


    fun sendRequestInquirePensionCertificate(recipientName: String) {
        viewModelScope.launch {
            val map = HashMap<String, String>()
            map["target"] = recipientName
            mldCertificateResponse.postValue( callService { repository.sendRequestInquirePensionCertificate(map) })
        }
    }

    fun sendEdictPensionerToMyInbox(recipientName: String) {
        viewModelScope.launch {
            val map = HashMap<String, String>()
            map["target"] = recipientName
            mldCertificateResponse.postValue( callService { repository.sendRequestInquirePensionCertificate(map) })
        }
    }

}
