package com.tamin.taminhamrah.ui.home.services.deferredInstallmentCertificate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.services.Bank
import com.tamin.taminhamrah.data.remote.models.services.Beneficiary
import com.tamin.taminhamrah.data.remote.models.services.DeferredInstallmentCertificateResponse
import com.tamin.taminhamrah.data.remote.models.services.PensionIdModel
import com.tamin.taminhamrah.data.remote.models.services.PensionerIdResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeferredInstallmentCertificateViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val pensionIdModelList by lazy{
        ArrayList<PensionIdModel>()
    }

    val mldDeferredInstallmentCertificate =
        MutableLiveData<DeferredInstallmentCertificateResponse>()
    val mldPensionerIdList = MutableLiveData<PensionerIdResponse>()

    fun getPensionerIdList() {
        viewModelScope.launch {
            mldPensionerIdList.postValue(callService { repository.getPensionerId() })
        }
    }

    val guaranteeListFlow = createLocalPager(getGuaranteeList()).flow.cachedIn(viewModelScope)
    private fun getGuaranteeList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("ضمانت برای سایرین", "1"))
        itemList.add(MenuModel("ضمانت برای خودم", "0"))
        return itemList
    }


    fun getBeneficiaryListFlow(bankName: String = ""): Flow<PagingData<Beneficiary>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        val array = JsonArray()

        if (bankName.isNotEmpty()) {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "bankName")
            jsonObj.addProperty("operator", "LIKE")
            jsonObj.addProperty("value", "*$bankName*")
            array.add(jsonObj)
        }
        paramsMap[Constants.ARRAY_KEY_FOR_MAP] = array.toString()
        val result = createPager(repository::getBeneficiary, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    fun sendRequestDeferredInstallmentCertificateForMe(
        bankName: Bank,
        banckBracnh: String,
        garanteeType: String,
        guaranteeAmount: Long,
        installmentAmount: String,
        installmentCount: String,
        loanAmount: Long,
        pensionerId: String
    ) {
        viewModelScope.launch {
            mldDeferredInstallmentCertificate.postValue(callService {
                repository.sendRequestDeferredInstallmentCertificateForMe(
                    bankName,
                    banckBracnh,
                    garanteeType,
                    guaranteeAmount,
                    installmentAmount,
                    installmentCount,
                    loanAmount,
                    pensionerId
                )
            })
        }
    }

        fun sendRequestDeferredInstallmentCertificateForOthers(
            bankName: Bank,
            banckBracnh: String,
            garanteeType: String,
            guaranteeAmount: Long,
            installmentAmount: String,
            installmentCount: String,
            loanAmount: Long,
            pensionerId: String,
            birthDate: String,
            firstName: String,
            lastName: String,
            nationalId: String

        ) {
            viewModelScope.launch {
                mldDeferredInstallmentCertificate.postValue(callService {
                    repository.sendRequestDeferredInstallmentCertificateForOthers(
                    bankName,
                    banckBracnh,
                    garanteeType,
                    guaranteeAmount,
                    installmentAmount,
                    installmentCount,
                    loanAmount,
                    pensionerId,
                    birthDate,
                    firstName,
                    lastName,
                    nationalId
                ) })
            }
        }
    }