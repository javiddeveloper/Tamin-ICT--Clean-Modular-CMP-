package com.tamin.taminhamrah.ui.home.services.objectionNonExistentHistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.BaseStatus
import com.tamin.taminhamrah.data.remote.models.services.BodySaveNonExistentHistory
import com.tamin.taminhamrah.data.remote.models.services.BranchDetailModel
import com.tamin.taminhamrah.data.remote.models.services.BranchDetailResponse
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.CityNameListResponse
import com.tamin.taminhamrah.data.remote.models.services.CityNameModel
import com.tamin.taminhamrah.data.remote.models.services.Date
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.remote.models.services.InsuranceTypeModel
import com.tamin.taminhamrah.data.remote.models.services.InsuranceTypeResponce
import com.tamin.taminhamrah.data.remote.models.services.NotExistRequestsResponse
import com.tamin.taminhamrah.data.remote.models.services.ProvinceModel
import com.tamin.taminhamrah.data.remote.models.services.ProvinceResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.CheckSaveNotExistModel
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.CheckStatusNotExistModel
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.DeleteNotExistResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.SendConfirmNotExistResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.SendFinalConfirmResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.ConfirmConflictResponseItem
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ObjectionNotExistentHistoryViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val mldNotExistRequests = MutableLiveData<NotExistRequestsResponse>()
    val mldSaveObjection = MutableLiveData<CheckSaveNotExistModel>()
    val mldDeleteNotExist = MutableLiveData<DeleteNotExistResponse>()
    val mldPensionCheck = MutableLiveData<CheckInsuredInfoResponse>()
    val mldSendDescriptionAndConfirm = MutableLiveData<SendConfirmNotExistResponse>()
    val mldSendFinalConfirm = MutableLiveData<SendFinalConfirmResponse>()
    val mldCheckHasObjection = MutableLiveData<CheckStatusNotExistModel>()
    val mldProvinceList = MutableLiveData<ProvinceResponse>()
    var mldCityList = MutableLiveData<CityNameListResponse>()
    var mldBranchList = MutableLiveData<BranchDetailResponse>()
    var mldInsuranceType = MutableLiveData<InsuranceTypeResponce>()

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


    fun checkStatusNotExitHistory() {
        viewModelScope.launch {
            mldCheckHasObjection.postValue(callService { repository.checkStatusNotExist() })
        }
    }

    val getNotExistRequests = createPager(repository::getNotExistRequests).flow.cachedIn(viewModelScope)

    //Province
    fun getProvinceRequests(
        provinceName: String? = null,
        provinceCode: String? = null
    ): kotlinx.coroutines.flow.Flow<PagingData<ProvinceModel>> {
        val array = JsonArray()
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (!provinceName.isNullOrBlank()) {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "provinceName")
            jsonObj.addProperty("operator", "LIKE")
            jsonObj.addProperty("value", "*$provinceName*")
            array.add(jsonObj)
        }
        if (!provinceCode.isNullOrBlank()) {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "provinceCode")
            jsonObj.addProperty("operator", "EQUAL")
            jsonObj.addProperty("value", provinceCode)
            array.add(jsonObj)
        }
        paramsMap[Constants.ARRAY_KEY_FOR_MAP] = array.toString()
        val result = createPager(repository::getProvince, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    //City
    fun getCityRequests(
        provinceCode: String? = null,
        cityCode: String? = null,
        cityName: String = ""
    ): kotlinx.coroutines.flow.Flow<PagingData<CityNameModel>> {
        val array = JsonArray()
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (cityName != "") {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "cityName")
            jsonObj.addProperty("operator", "LIKE")
            jsonObj.addProperty("value", "*$cityName*")
            array.add(jsonObj)
        }
        provinceCode?.let {
            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "provincecode")
            jsonObj2.addProperty("operator", "EQUAL")
            jsonObj2.addProperty("value", it)
            array.add(jsonObj2)
        }
        cityCode?.let {
            val jsonObj3 = JsonObject()
            jsonObj3.addProperty("property", "cityCode")
            jsonObj3.addProperty("operator", "EQUAL")
            jsonObj3.addProperty("value", cityCode)
            array.add(jsonObj3)
        }
        paramsMap[Constants.ARRAY_KEY_FOR_MAP] = array.toString()
        val result =
            createPager(repository::getCityListByProvinceCodeAndCityName, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    //Branch
    fun getBranchRequests(cityCode: String? = null, branchName: String = "")
            : kotlinx.coroutines.flow.Flow<PagingData<BranchDetailModel>> {
        val array = JsonArray()
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (branchName != "") {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "name")
            jsonObj.addProperty("value", "*$branchName*")
            jsonObj.addProperty("operator", "LIKE")
            array.add(jsonObj)
        }
        if (!cityCode.equals("")) {
            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "cityCode")
            jsonObj2.addProperty("operator", "EQUAL")
            jsonObj2.addProperty("value", cityCode)
            array.add(jsonObj2)

        }
        val jsonObj4 = JsonObject()
        jsonObj4.addProperty("property", "type")
        jsonObj4.addProperty("operator", "EQUAL")
        jsonObj4.addProperty("value", "1")

        val jsonObj5 = JsonObject()
        jsonObj5.addProperty("property", "status")
        jsonObj5.addProperty("operator", "EQUAL")
        jsonObj5.addProperty("value", "1")

        array.add(jsonObj4)
        array.add(jsonObj5)
        paramsMap[Constants.ARRAY_KEY_FOR_MAP] = array.toString()
        val result = createPager(repository::getBranchDetailList, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    //Insurance Type
    fun getInsuranceTypeRequests(insuranceTypeName: String?)
            : kotlinx.coroutines.flow.Flow<PagingData<InsuranceTypeModel>> {
        val array = JsonArray()
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        insuranceTypeName?.let {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "insuranceTypeDesc")
            jsonObj.addProperty("operator", "LIKE")
            jsonObj.addProperty("value", "*$insuranceTypeName*")
            array.add(jsonObj)
        }
        paramsMap[Constants.ARRAY_KEY_FOR_MAP] = array.toString()
        val result = createPager(repository::getInsuranceTypeList, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    fun sendReqSaveNotExist(sendReq: BodySaveNonExistentHistory) {
        viewModelScope.launch {
            val result = callService {
                repository.sendReqSaveNotExist(sendReq)
            }
            mldSaveObjection.postValue(result)
        }
    }

    fun deleteNotExist(reqno: String, id: String) {
        viewModelScope.launch {
            val result = callService {
                repository.deleteNotExist(reqno, id)
            }
            mldDeleteNotExist.postValue(result)
        }
    }

    fun sendConfirmNoTexist(list: List<ConfirmConflictResponseItem>) {
        viewModelScope.launch {
            val result = callService {
                repository.sendConfirmNotExist(list)
            }
            mldSendDescriptionAndConfirm.postValue(result)
        }
    }

    fun sendFinalConfirmNotExist() {
        viewModelScope.launch {
            val result = callService {
                repository.sendFinalConfirmNoTexist()
            }
            mldSendFinalConfirm.postValue(result)
        }
    }
}