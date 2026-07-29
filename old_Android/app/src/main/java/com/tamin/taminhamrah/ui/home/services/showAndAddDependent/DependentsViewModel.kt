package com.tamin.taminhamrah.ui.home.services.showAndAddDependent

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.BranchListResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DependentInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.FamilyRelationShipModel
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.InquiryEducationCodeResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.InquiryRegistryResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.RequestAddDependent
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.showAndAddDependent.model.AddDependentDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class DependentsViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {
    val mldDependentInfo = MutableLiveData<DependentInfoResponse>()
    val mldRefreshDependent = MutableLiveData<GeneralRes>()
    val mldActiveBranch = MutableLiveData<BranchListResponse>()
    val mldInquiryRegistry = MutableLiveData<InquiryRegistryResponse>()
    val mldInquiryEducationCode = MutableLiveData<InquiryEducationCodeResponse>()
    val mldUploadImage = MutableLiveData<UploadImageResponse>()
    val mldAddDependent = MutableLiveData<GeneralRes>()
    val dataModel by lazy { AddDependentDataModel() }

    fun getDependentInfo() {
        viewModelScope.launch {
            mldDependentInfo.postValue(callService { repository.getDependentInfo() })
        }
    }

    fun getActiveBranch() {
        viewModelScope.launch {
            mldActiveBranch.postValue(callService { repository.getActiveBranch() })
        }
    }

    fun getFamilyRelationShips(): Flow<PagingData<FamilyRelationShipModel>> {
        val array = JsonArray()
        array.add(JsonObject().apply {
            addProperty("property", "dependencyDesc")
            addProperty("operator", "LIKE")
            addProperty("value", "**")
        })
        return createPager(repository::getFamilyRelationShips,
            paramsMap = hashMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString())).flow.cachedIn(
            viewModelScope)
    }

    fun getCityList(cityName: String = "") = createPager(repository::getCityList,
        paramsMap = hashMapOf("cityName" to "*$cityName%*")).flow.cachedIn(viewModelScope)

    fun inquiryRegistryInfo(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String,
    ) {
        viewModelScope.launch {
            mldInquiryRegistry.postValue(callService {
                repository.inquiryRegistryInfo(dependentNationalId,
                    birthDateTimeStamp,
                    dependencyCode)
            })
        }
    }

    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            mldUploadImage.postValue(callService {
                repository.uploadImage(image)
            })
        }
    }

    fun inquiryEducationCode(nationalId: String, educationCode: String) {
        viewModelScope.launch {
            mldInquiryEducationCode.postValue(callService {
                repository.inquiryEducationCode(nationalId, educationCode)
            })
        }
    }

    fun addNewDependent(requestModel: RequestAddDependent) {
        viewModelScope.launch {
            mldAddDependent.postValue(callService {
                repository.addNewDependent(requestModel)
            })
        }
    }

    fun refreshDependent() {
        viewModelScope.launch {
            mldRefreshDependent.postValue(callService {
                repository.refreshDependent()
            })
        }
    }


}