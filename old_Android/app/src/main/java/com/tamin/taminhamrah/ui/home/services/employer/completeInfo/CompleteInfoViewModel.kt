package com.tamin.taminhamrah.ui.home.services.employer.completeInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerAgreement
import com.tamin.taminhamrah.data.remote.models.employer.legalWorkshop.LegalWorkshopCEOInfoResponse
import com.tamin.taminhamrah.data.remote.models.employer.legalWorkshop.LegalWorkshopInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.CityModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.ProvinceModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.BranchesInfoListModel
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.CurrentUserResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.employer.completeInfo.model.TicketRequest
import com.tamin.taminhamrah.ui.home.services.employer.completeInfo.model.VerifyCodeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompleteInfoViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val mldUserInfo = MutableLiveData<CurrentUserResponse>()
    val mldTicketResult = MutableLiveData<GeneralRes>()
    val mldVerification = MutableLiveData<GeneralRes>()

    val mldLegalWorkshopInfo = MutableLiveData<LegalWorkshopInfoResponse>()
    val mldLegalWorkshopCEOInfo = MutableLiveData<LegalWorkshopCEOInfoResponse>()

    fun getUserInfo() {
        viewModelScope.launch {
            val result = callService {
                repository.getCurrentUser()
            }
            mldUserInfo.postValue(result)
        }
    }

    fun sendCommitmentRequest(mobile: String, email: String) {
        viewModelScope.launch {
            val result =
                callService { repository.sendCommitmentRequest(mobile, email, "saveStackHolder") }
            mldTicketResult.postValue(result)
        }
    }

    fun postRealWorkshopVerificationCode(
        branchCode: String,
        workshopCode: String,
        ticketCode: String
    ) {
        viewModelScope.launch {
            val result = callService {
                repository.postRealWorkshopVerificationCode(
                    VerifyCodeRequest(
                        branchCode,
                        workshopCode,
                        ticketCode
                    )
                )
            }
            mldVerification.postValue(result)
        }
    }

    //Provinces
    var provinceFlow: Flow<PagingData<ProvinceModel>>? = null
    fun getProvincesList(
        provinceName: String? = null,
        provinceCode: String? = null
    ): Flow<PagingData<ProvinceModel>>? {
        val array = JsonArray()

        if (!provinceName.isNullOrEmpty()) {
            JsonObject().apply {
                addProperty("property", "provinceName")
                addProperty("operator", "LIKE")
                addProperty("value", "*$provinceName*")
                array.add(this)
            }
        }
        if (!provinceCode.isNullOrBlank()) {
            JsonObject().apply {
                addProperty("property", "provinceCode")
                addProperty("operator", "EQUAL")
                addProperty("value", provinceCode)
                array.add(this)
            }
        }

        provinceFlow = createPager(
            repository::getProvince,
            paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString())
        ).flow.cachedIn(
            viewModelScope
        )

        return provinceFlow
    }

    //Cities
    var cityFlow: Flow<PagingData<CityModel>>? = null
    fun getCitiesOfProvince(
        provinceCode: String? = null
    ): Flow<PagingData<CityModel>>? {
        val array = JsonArray()

        if (!provinceCode.isNullOrBlank()) {
            JsonObject().apply {
                addProperty("property", "provincecode")
                addProperty("operator", "EQ")
                addProperty("value", provinceCode)
                array.add(this)
            }
        }

        cityFlow = createPager(
            repository::getCitiesOfProvince,
            paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString())
        ).flow.cachedIn(
            viewModelScope
        )

        return cityFlow
    }

    //branches of city of province
    var branchesFlow: Flow<PagingData<BranchesInfoListModel>>? = null
    fun getBranchesOfCity(
        cityCode: String? = null
    ): Flow<PagingData<BranchesInfoListModel>>? {
        val array = JsonArray()

        if (!cityCode.isNullOrBlank()) {
            JsonObject().apply {
                addProperty("property", "cityCode")
                addProperty("operator", "EQ")
                addProperty("value", cityCode)
                array.add(this)
            }
        }

        branchesFlow = createPager(
            repository::getInfoBranch,
            paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString())
        ).flow.cachedIn(viewModelScope)

        return branchesFlow
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


    val companyTypeFlow = createLocalPager(getCompanyTypeList()).flow.cachedIn(viewModelScope)
    fun getCompanyTypeList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("خصوصی", "03"))
        itemList.add(MenuModel("دولتی", "01"))
        itemList.add(MenuModel("شخصيت حقوقي خارجي", "04"))
        itemList.add(MenuModel("عمومي - غيردولتي", "02"))

        return itemList
    }

    fun getLegalWorkshopInfo(legalWorkshopID: String?) {
        viewModelScope.launch {
            val result = callService { repository.getLegalWorkshopInfo(legalWorkshopID) }
            mldLegalWorkshopInfo.postValue(result)
        }
    }

    fun getLegalWorkshopCEOInfo(nationalCode: String?, birthdate: String?) {
        viewModelScope.launch {
            val result = callService { repository.getLegalWorkshopCEOInfo(nationalCode, birthdate) }
            mldLegalWorkshopCEOInfo.postValue(result)
        }
    }

    fun getVerificationTicketForLegalWorkshopInfo(
        mobileNumber: String?,
        email: String?,
        nationalCode: String?
    ) {
        viewModelScope.launch {

            val result = callService {
                repository.getVerificationTicketForLegalWorkshopInfo(
                    mobileNumber,
                    email,
                    nationalCode
                )
            }
            mldTicketResult.postValue(result)
        }
    }

    fun sendVerifyTicketForLegalWorkshop(
        birthDate: String?,
        branchCode: String?,
        email: String?,
        legalWorkshopTypeCode: String?,
        mobile: String?,
        nationalId: String?,
        telephone: String?,
        ticketCode: String?,
        workshopId: String?,
        workshopNationalCode: String?
    ) {
        viewModelScope.launch {

            val body = TicketRequest(
                birthDate,
                branchCode,
                email,
                legalWorkshopTypeCode,
                mobile,
                nationalId,
                telephone,
                ticketCode,
                workshopId,
                workshopNationalCode
            )

            val result = callService { repository.sendVerifyTicketForLegalWorkshop(body) }
            mldTicketResult.postValue(result)
        }
    }

}