package com.tamin.taminhamrah.ui.home.services.insuranceRegistration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.BranchDetailModel
import com.tamin.taminhamrah.data.remote.models.services.CityModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.insuranceRegistration.DocumentFile
import com.tamin.taminhamrah.data.remote.models.services.insuranceRegistration.PersonalInfo
import com.tamin.taminhamrah.data.remote.models.services.insuranceRegistration.RegistrationReq
import com.tamin.taminhamrah.data.remote.models.services.insuranceRegistration.RelationWithTamin
import com.tamin.taminhamrah.data.remote.models.services.insuranceRegistration.RequestFile
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class InsuranceRegistrationViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    //val mldRegistrationInfo = MutableLiveData<ConcludingStudentInsuranceContractResponse>()

    fun getRegistrationInfo() {
        /* viewModelScope.launch {
             mldRegistrationInfo.postValue(callService { repository.getRegistrationInfo()})
         }*/
    }

    val insuranceTypeFlow = createLocalPager(getInsuranceList()).flow.cachedIn(viewModelScope)
    fun getInsuranceList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("اختیاری", "9"))
        itemList.add(MenuModel("حرف و مشاغل آزاد", "10"))
        itemList.add(MenuModel("بیمه زنان خانه دار", "129"))
        itemList.add(MenuModel("بیمه دانشجویان", "147"))

        return itemList
    }
    fun getCityListFlow(
        cityName: String? = ""
    ): Flow<PagingData<CityModel>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!cityName.isNullOrEmpty())
            paramsMap["cityName"] = cityName

        val result = createPager(repository::getCityList, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    fun getBranchRequests(branchName: String = "")
            : Flow<PagingData<BranchDetailModel>> {
        val array = JsonArray()
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (branchName != "") {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "name")
            jsonObj.addProperty("value", "*$branchName*")
            jsonObj.addProperty("operator", "LIKE")
            array.add(jsonObj)
        }

        paramsMap[Constants.ARRAY_KEY_FOR_MAP] = array.toString()
        val result = createPager(repository::getBranchDetailList, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    val mldUploadImage = MutableLiveData<UploadImageResponse>()
    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            mldUploadImage.postValue(callService {
                repository.uploadImage(image)
            })
        }
    }
    val mldRegistrationResult = MutableLiveData<GeneralRes>()
    fun postInsuranceRegistration(
        selectedInsuranceId: String,
        selectedBirthCityId: String,
        selectedIssuedCityId: String,
        selectedBranchId: String,
        serialNumber: String?,
        fileListUploaded: ArrayList<UploadedImageModel>
    ) {

        val fileList = ArrayList<RequestFile>()
        fileListUploaded.forEach {
            fileList.add(RequestFile(documentFile =DocumentFile(it.guid) , documentType = it.imageType,null, null))
        }
        val request = RegistrationReq(PersonalInfo(selectedBranchId, selectedBirthCityId, selectedIssuedCityId, serialNumber , fileList),
            RelationWithTamin(selectedInsuranceId))
        viewModelScope.launch {
            mldRegistrationResult.postValue(callService {
                repository.sendInsuranceRegistration(request)
            })
        }
    }
}