package com.tamin.taminhamrah.ui.home.services.employer.legalStackHolders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerWorkshop
import com.tamin.taminhamrah.data.remote.models.employer.legalStackHolder.LegalStackHolder
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.employer.legalStackHolders.model.AgentRequestModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LegalStackHolderViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val mldTicketResult = MutableLiveData<GeneralRes>()
    val mldVerifyTicketResult = MutableLiveData<GeneralRes>()
    val mldVerifyUser = MutableLiveData<GeneralRes>()
    val mldDeleteUser = MutableLiveData<GeneralRes>()

    fun getLegalStackHolderList(
        workshopId: String? = "",
        branchCode: String? = ""
    ): Flow<PagingData<LegalStackHolder>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopId.isNullOrEmpty())
            paramsMap["workshopId"] = workshopId
        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode

        return createPager(
            repository::getLegalStackHolderList,
            paramsMap = paramsMap
        ).flow.cachedIn(viewModelScope)
    }

    fun requestTicket(nationalId: String?=null) {
        viewModelScope.launch {
            val result = callService { repository.requestLegalStackHolderTicket(nationalId) }
            mldTicketResult.postValue(result)
        }
    }

    fun verifyTicket(ticket: String) {
        viewModelScope.launch {
            val result = callService {
                repository.verifyLegalStackHolderTicket(ticket)
            }
            mldVerifyTicketResult.postValue(result)
        }
    }

    fun getLegalAgentList(
        workshopId: String? = "",
        branchCode: String? = "",
        verificationCode: String? = ""
    ): Flow<PagingData<LegalStackHolder>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (!workshopId.isNullOrEmpty())
            paramsMap["workshopId"] = workshopId

        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode

        if (!verificationCode.isNullOrEmpty())
            paramsMap["verificationCode"] = verificationCode

        return createPager(
            repository::getLegalAgentList,
            paramsMap = paramsMap
        ).flow.cachedIn(viewModelScope)
    }

    val reqModel :AgentRequestModel by lazy { AgentRequestModel() }
    fun submitNewLegalAgent(ticket: String) {

        viewModelScope.launch {
            mldVerifyUser.postValue(callService {
                repository.submitNewLegalAgent(
                    ticket,
                    reqModel
                )
            })
        }
    }

    fun deleteLegalAgent(ticket: String?, stackId: Long?) {
        viewModelScope.launch {
            mldDeleteUser.postValue(callService { repository.deleteLegalAgent(ticket, stackId) })
        }
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
    }}

