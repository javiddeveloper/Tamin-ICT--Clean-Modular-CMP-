package com.tamin.taminhamrah.ui.home.services.issuanceWageCertificate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants.ARRAY_KEY_FOR_MAP
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.PensionIdModel
import com.tamin.taminhamrah.data.remote.models.services.PensionerIdResponse
import com.tamin.taminhamrah.data.remote.models.services.Recipient
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IssuanceWageCertificateViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {
    val mldCertificateResponse = MutableLiveData<GeneralRes>()
    val mldPensionerIdList = MutableLiveData<PensionerIdResponse>()

    val pensionIdModelList by lazy{
        ArrayList<PensionIdModel>()
    }

    fun getPensionerIdList() {
        viewModelScope.launch {
            mldPensionerIdList.postValue(callService { repository.getPensionerId() })
        }
    }

    fun getReceiverListFlow(branchName: String?): Flow<PagingData<Recipient>> {
        val array = JsonArray()
        if (branchName != null){
            array.add(JsonObject().apply {
                addProperty("property", "recipientName")
                addProperty("value", "*$branchName%*")
                addProperty("operator", "LIKE")
            })
        }
       return createPager(repository::getRecipientList, paramsMap = hashMapOf(ARRAY_KEY_FOR_MAP to array.toString())).flow.cachedIn(viewModelScope)
    }

    fun sendCertificateRequest(
        pensionerId: String,
        recipientId: String,
        branchName: String?
    ) {
        viewModelScope.launch {
            val map = HashMap<String, String?>()
            map["pensionerId"] = pensionerId
            map["recipient"] = recipientId
            map["target"] = ""
            map["branchName"] = branchName
            mldCertificateResponse.postValue(callService { repository.sendCertificateWage(map) })
        }
    }
}