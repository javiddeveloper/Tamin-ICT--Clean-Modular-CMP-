package com.tamin.taminhamrah.ui.home.services.pensionSurvivor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.IdentityInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.AgeResponse
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.ConfirmSurvivorListResponse
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.DeceasedInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SaveSurvivorInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SurvivorResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.LoadingState
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.pensionSurvivor.model.PensionSurvivorDataModel
import com.tamin.taminhamrah.utils.extentions.isNumericString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class PensionSurvivorViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val dataModel by lazy {
        PensionSurvivorDataModel()
    }

    val mldRegistrationInfo = MutableLiveData<IdentityInfoResponse>()
    val mldDeceasedInfo = MutableLiveData<DeceasedInfoResponse>()
    val mldUploadImage = MutableLiveData<UploadImageResponse>()
    val mldSurvivorList = MutableLiveData<SurvivorResponse>()
    val mldSaveSurvivorInfo = MutableLiveData<GeneralRes>()
    val mldConfirmSurvivorList = MutableLiveData<ConfirmSurvivorListResponse>()
    val mldPDF = MutableLiveData<PdfDownloadResponse>()
    val mldFinalConfirm = MutableLiveData<GeneralRes>()
    val mldAge = MutableLiveData<AgeResponse>()

    fun getRegistrationUserInfo() {
        viewModelScope.launch {
            mldRegistrationInfo.postValue(callService { repository.getIdentityInfo() })
        }
    }

    fun getDeceasedInfo(nationalCode: String) {
        viewModelScope.launch {
            mldLoadingState.postValue(LoadingState.LOADING)
            var birthDate: Long? = null
            val deceasedInfoResponse = repository.getDeceasedInfo(nationalCode)
            if (deceasedInfoResponse.isSuccess) {
                birthDate = deceasedInfoResponse.data.personal?.dateOfBirth ?:0
               val ageResponse =  birthDate.let { repository.getAge(birthDate) }
                if (ageResponse.isSuccess) {
                    // if ((ageResponse.await().data.age)?.replace(",", "")?.isDigitsOnly() == true) {
                    val ageStr = ageResponse.data.age?.replace(",", "")
                    if (ageStr?.isNumericString() == true) {
                        val ageArray = ageResponse.data.age?.split(",") ?: emptyList()
                        if (ageArray.size >= 3) {
                            deceasedInfoResponse.data.apply {
                                yearsAge = ageArray[0]
                                monthsAge = ageArray[1]
                                daysAge = ageArray[2]
                            }
                        }
                    }
                }
            }
            mldLoadingState.postValue(LoadingState.NOT_LOADING)
            mldDeceasedInfo.postValue(deceasedInfoResponse)
        }
    }

    fun getSurvivorList(deceasedNationalId: String) {
        viewModelScope.launch {
            mldSurvivorList.postValue(callService { repository.getSurvivorList(deceasedNationalId) })
        }
    }

    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            mldUploadImage.postValue(callService {
                repository.uploadImage(image)
            })
        }
    }

    fun saveSurvivorInfo(body: SaveSurvivorInfoRequest) {
        viewModelScope.launch {
            mldSaveSurvivorInfo.postValue(callService {
                repository.saveSurvivorInfo(body)
            })
        }
    }

    fun confirmSurvivorsList() {
        viewModelScope.launch {
            mldConfirmSurvivorList.postValue(callService {
                repository.confirmSurvivorsList()
            })
        }
    }

    fun getFinalSurvivorPensionPDF() {
        viewModelScope.launch {
            mldPDF.postValue(callService {
                repository.getFinalSurvivorPensionPDF()
            })
        }
    }

    fun submitFinalSurvivorPension(requestId: Int) {
        viewModelScope.launch {
            mldFinalConfirm.postValue(callService {
                repository.submitFinalSurvivorPension(requestId = requestId)
            })
        }
    }


}