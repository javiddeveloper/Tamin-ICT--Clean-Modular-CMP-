package com.tamin.taminhamrah.ui.home.services.girlservivor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.ConfirmSurvivorRequest
import com.tamin.taminhamrah.data.remote.models.services.DependencyType
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.PersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.asDomainModel
import com.tamin.taminhamrah.data.remote.models.services.girlSurvivor.CheckGirlSurvivorConditionsResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GirlSurvivorViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {
    val mldIdentityInfo = MutableLiveData<PersonalInfoResponse>()
    val mldCondition = MutableLiveData<CheckGirlSurvivorConditionsResponse>()
    val mldReport = MutableLiveData<PdfDownloadResponse>()
    val mldConfirm = MutableLiveData<GeneralRes>()

    fun getPersonalInfo() {
        viewModelScope.launch {
            mldIdentityInfo.postValue(callService { repository.getPersonalInfo() })
        }
    }

    fun checkGirlSurvivorConditions(valueNationalCode: String, pensionerId: String) {
        viewModelScope.launch {
            mldCondition.postValue(callService {
                repository.checkGirlSurvivorConditions(
                    valueNationalCode,
                    pensionerId
                )
            })
        }
    }

    fun getGirlSurvivorReport(
        address: String,
        phone: String,
        zipCode: String,
        fatherName: String,
        birthDate: Long,
        insuranceNumber: String,
        nationalCode: String,
        pensionId: String
    ) {
        viewModelScope.launch {
            mldReport.postValue(callService {
                repository.getGirlSurvivorReport(
                    address,
                    phone,
                    zipCode,
                    fatherName,
                    birthDate,
                    insuranceNumber,
                    nationalCode,
                    pensionId
                )
            })
        }
    }

    fun confirmGirlSurvivor() {
        viewModelScope.launch {
            mldIdentityInfo.value?.data?.asDomainModel()?.let {
                val request = ConfirmSurvivorRequest(
                    address = it.address,
                    age = "33",
                    birthDate = it.birthDateTimeStamp,
                    insuranceId = it.insuranceNumber,
                    nationalId = it.nationalCode,
                    deathDate = it.deathDate,
                    deathType = null,
                    dependencyType = DependencyType(code = "04"),
                    firstName = it.firstName,
                    idNumber = it.idCardNumber,
                    insuranceNumber = it.insuranceNumber,
                    lastName = it.lastName,
                    mobileNumber = it.mobileNumber,
                    nationalCode = it.parentInfo?.nationalCode,
                    pensionId = it.parentInfo?.pensionId,
                    phoneNumber = it.phoneNumber,
                    status = "0",
                    gender = it.gender
                )
                mldConfirm.postValue(callService { repository.confirmGirlSurvivor(request) })
            }
        }
    }

}


