package com.tamin.taminhamrah.ui.home.services.disabilityPension

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.IdentityInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilityDependentResponse
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilityFinalConfirmRequest
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilityPersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilitySaveInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilitySaveInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.medicalCommission.RegisteredMedicalCommissionModel
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.LoadingState
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.disabilityPension.model.DisabilityPensionDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class DisabilityPensionViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val mldRegistrationInfo = MutableLiveData<IdentityInfoResponse>()
    val mldDependentInfo = MutableLiveData<DisabilityDependentResponse>()
    val mldRefreshDependent = MutableLiveData<GeneralRes>()
    val mldPersonalInfo = MutableLiveData<DisabilityPersonalInfoResponse>()
    val mldCombinedList = MutableLiveData<CombinedRecordResponse>()
    val mldSaveDisabilityUserInfo = MutableLiveData<DisabilitySaveInfoResponse>()
    val mldFinalConfirm = MutableLiveData<DisabilitySaveInfoResponse>()
    val mldMedicalCommissionPdf = MutableLiveData<PdfDownloadResponse>()
    val mldUploadImage = MutableLiveData<UploadImageResponse>()
    val mldSaveDocument = MutableLiveData<GeneralRes>()

    val dataModel by lazy {
        DisabilityPensionDataModel()
    }

    fun getImageUrl() = "https://ssodcfs.tamin.ir/Eservices/icon-eservices/protest.svg"


    fun getRegistrationUserInfo() {
        viewModelScope.launch {
            mldRegistrationInfo.postValue(callService { repository.getIdentityInfo() })
        }
    }

    fun getDisabilityPensionDependentInfo() {
        viewModelScope.launch {
            mldDependentInfo.postValue(callService { repository.getDisabilityDependentInfo() })
        }
    }

    fun refreshDependent() {
        viewModelScope.launch {
            mldRefreshDependent.postValue(callService {
                repository.refreshDependent()
            })
        }
    }

    fun getDisabilityPersonalInfo() {
        viewModelScope.launch {
            mldLoadingState.postValue(LoadingState.LOADING)
            val personalInfoResponse = repository.getDisabilityPersonalInfo()
            var birthDate = 0L
            if (personalInfoResponse.isSuccess) {
                birthDate = personalInfoResponse.data?.personal?.dateOfBirth ?: 0L
                val ageResponse = repository.getUserAge(birthDate)
                if (ageResponse.isSuccess) {
                    val ageArray = ageResponse.data.age?.split(",") ?: emptyList()
                    if (ageArray.size >= 3) {
                        personalInfoResponse.data?.apply {
                            yearsAge = ageArray[0]
                            monthsAge = ageArray[1]
                            daysAge = ageArray[2]
                            strAge = "$yearsAge سال و $monthsAge ماه و $daysAge روز"
                        }
                    }
                }
            } else
                mldErrorState.postValue(personalInfoResponse)

            mldLoadingState.postValue(LoadingState.NOT_LOADING)
            mldPersonalInfo.postValue(personalInfoResponse)
        }
    }

    fun getCombinedRecordList() {
        viewModelScope.launch {
            mldCombinedList.postValue(callService {
                repository.getCombinedRecordList(null)
            })
        }
    }

    fun getMedicalCommissionPDF(lastWorkShop: String) {
        viewModelScope.launch {
            mldMedicalCommissionPdf.postValue(callService {
                repository.getMedicalCommissionPdf(lastWorkShop)
            })
        }
    }

    fun saveDisabilityUserInfo(body: DisabilitySaveInfoRequest) {
        viewModelScope.launch {
            mldSaveDisabilityUserInfo.postValue(callService {
                repository.saveDisabilityUserInfo(body = body)
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

    fun finalConfirmDisabilityRequest(requestId: Long, body: DisabilityFinalConfirmRequest) {
        viewModelScope.launch {
            mldFinalConfirm.postValue(callService {
                repository.finalConfirmDisabilityRequest(requestId = requestId, body = body)
            })
        }
    }

    fun saveAndConfirmRequest() {
        viewModelScope.launch {
            mldLoadingState.postValue(LoadingState.LOADING)
            val saveUserInfoResponse =
                repository.saveDisabilityUserInfo(dataModel.saveInfoRequestModel)
            if (saveUserInfoResponse.isSuccess) {
                dataModel.requestId = saveUserInfoResponse.data.request?.id ?: 0
                val saveDocumentResponse = repository.saveDocumentDisability(
                    requestId = dataModel.requestId,
                    body = dataModel.getSaveDocumentRequestModel()
                )
                if (saveDocumentResponse.isSuccess) {
                    val finalConfirmResponse = repository.finalConfirmDisabilityRequest(
                        requestId = dataModel.requestId,
                        body = DisabilityFinalConfirmRequest(id = dataModel.requestId, status = "0")
                    )
                    mldLoadingState.postValue(LoadingState.NOT_LOADING)
                    mldFinalConfirm.postValue(finalConfirmResponse)
                } else {
                    mldLoadingState.postValue(LoadingState.NOT_LOADING)
                    mldSaveDocument.postValue(saveDocumentResponse)
                }
            } else {
                mldLoadingState.postValue(LoadingState.NOT_LOADING)
                mldSaveDisabilityUserInfo.postValue(saveUserInfoResponse)
            }
        }
    }

    fun getConfirmSteps() = arrayListOf(
        MenuModel(titleStringResId = R.string.confirm_rules, isSelected = true),
        MenuModel(titleStringResId = R.string.confirm_identity_and_call_info, isSelected = true),
        MenuModel(titleStringResId = R.string.confirm_workshop_and_branch, isSelected = true),
        MenuModel(titleStringResId = R.string.confirm_history, isSelected = true),
        MenuModel(titleStringResId = R.string.confirm_medical_commission, isSelected = true),
    )


    fun getRegisteredMedicalCommission(): Flow<PagingData<RegisteredMedicalCommissionModel>> {
        return createPager(
            repository::getRegisteredMedicalCommission,
        ).flow.cachedIn(viewModelScope)
    }
}