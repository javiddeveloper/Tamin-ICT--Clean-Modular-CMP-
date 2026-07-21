package com.tamin.taminhamrah.ui.home.services.employer.onlineService

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerAgreement
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerCommitmentResponse
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerWorkshop
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.WorkshopContract
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.WorkshopInfoWithoutContract
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.CurrentUserResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.employer.onlineService.model.AgreementDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployerAgreementInfoViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val ARG_BRANCH_CODE = "branchCode"
    val ARG_WORKSHOP_ID = "workshopId"


    val mldRequestOfVerificationCode = MutableLiveData<GeneralRes>()
    val mldResultOfVerificationCode = MutableLiveData<EmployerCommitmentResponse>()
    val mldAgreementResult= MutableLiveData<GeneralRes>()

    val dataModel:AgreementDataModel by lazy { AgreementDataModel() }
    val mldCurrentUser = MutableLiveData<CurrentUserResponse>()
    fun getCurrentUserInfo() {
        viewModelScope.launch {
            mldCurrentUser.postValue(callService {
                repository.getCurrentUser()
            })
        }
    }

    fun getEmployerAgreementInfoList(
        workshopId: String? = "",
        branchCode: String? = ""
    ): Flow<PagingData<EmployerAgreement>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopId.isNullOrEmpty())
            paramsMap["workshopId"] = workshopId
        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode

        return createPager(
            repository::getEmployerAgreementInfoList,
            paramsMap = paramsMap
        ).flow.cachedIn(viewModelScope)
    }

    fun sendCommitmentRequest(mobile: String, email: String) {
        viewModelScope.launch {
            val result = callService { repository.sendCommitmentRequest(mobile, email,"employerEservicesAgreement") }
            mldRequestOfVerificationCode.postValue(result)
        }
    }

    fun getUserInfoWithVerificationCode(verifyCode: String) {
        viewModelScope.launch {
            val result = callService { repository.getUserInfoWithVerificationCode(verifyCode) }
            mldResultOfVerificationCode.postValue(result)
        }
    }

    fun getWorkshopsInfoWithoutContract(): Flow<PagingData<WorkshopInfoWithoutContract>> {

        val paramsMap: MutableMap<String, String> = mutableMapOf()
        return createPager(
            repository::getWorkshopsInfoWithoutContract,
            paramsMap = paramsMap
        ).flow.cachedIn(viewModelScope)
    }

    fun getWorkshopContactList(
        workshopId: String? = "",
        branchCode: String? = ""
    ): Flow<PagingData<WorkshopContract>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopId.isNullOrEmpty())
            paramsMap["workshopId"] = workshopId
        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode

        return createPager(
            repository::getWorkshopContactList,
            paramsMap = paramsMap
        ).flow.cachedIn(viewModelScope)
    }

 fun getEmployerWorkshopList(
        workshopId: String? = "",
        branchCode: String? = ""
    ): Flow<PagingData<EmployerWorkshop>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopId.isNullOrEmpty())
            paramsMap["workshopId"] = workshopId
        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode

        return createPager(
            repository::getEmployerWorkshopList,
            paramsMap = paramsMap
        ).flow.cachedIn(viewModelScope)
    }

    fun postEmployerAgreement() {
        viewModelScope.launch {
            val result = callService { repository.postEmployerAgreement(dataModel) }
            mldAgreementResult.postValue(result)
        }
    }

}