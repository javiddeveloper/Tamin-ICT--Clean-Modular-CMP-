package com.tamin.taminhamrah.ui.home.services.contracts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.BaseStatus
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.CityModel
import com.tamin.taminhamrah.data.remote.models.services.CityNameListResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.ProvinceModel
import com.tamin.taminhamrah.data.remote.models.services.ProvinceResponse
import com.tamin.taminhamrah.data.remote.models.services.RedCrossStatusResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.BranchesInfoListModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.BranchesInfoListResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CalculateSalary
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckAgeAndHistoryModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckAgeAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckContractStatusResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ConcludingStudentInsuranceContractResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractByGuardianRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractPremiumOptionsResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractPremiumRateResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.FinalConfirmResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.FreelancerJobTitleModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.OptionalContractByGuardian
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.OptionalContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.UpdateAddressInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.cancelContract.CancelContractReasonsModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.cancelContract.CancelContractReasonsResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.cancelContract.CancelContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateGuardianContract
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateGuardianOptionalContract
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateOptionalContract
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.LoadingState
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.contracts.ContractBaseFragment.Companion.REQUEST_CODE_IMAGE_DOC
import com.tamin.taminhamrah.ui.home.services.contracts.ContractBaseFragment.Companion.REQUEST_CODE_IMAGE_GUARDIANSHIP
import com.tamin.taminhamrah.ui.home.services.contracts.model.FractionRequestDataModel
import com.tamin.taminhamrah.ui.home.services.studentContract.model.ContractDataModel
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ContractViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {
    val mldRegistrationInfo = MutableLiveData<ConcludingStudentInsuranceContractResponse>()
    val mldCheckContractCondition = MutableLiveData<CheckAgeAndHistoryResponse>()
    val mldSaveUsersAddressInfo = MutableLiveData<GeneralRes>()
    val mldPremiumRateForContract = MutableLiveData<ContractPremiumRateResponse>()
    val mldCheckAndCalculateSalaryForContract = MutableLiveData<CalculateSalary>()
    val mldGetCityWhitOutPaging = MutableLiveData<CityNameListResponse>()
    val mldGetProvinceWhitOutPaging = MutableLiveData<ProvinceResponse>()
    val mldGetInfoOfBranch = MutableLiveData<BranchesInfoListResponse>()
    val mldFinalRequestMakeContract = MutableLiveData<FinalConfirmResponse>()
    val mldRequestCancelContract = MutableLiveData<GeneralRes>()
    val mldUpdateContract = MutableLiveData<GeneralRes>()
    val mldDownloadImage = MutableLiveData<GeneralRes>()
    val mldDownloadImageGuardian = MutableLiveData<GeneralRes>()
    val mldPdf = MutableLiveData<PdfDownloadResponse>()
    val mldPremiumOptions = MutableLiveData<ContractPremiumOptionsResponse>()

    val mldGetListSelfContractState = MutableLiveData<CancelContractReasonsResponse>()

    val mldUploadImageContractDoc = MutableLiveData<UploadImageResponse>()

    val mldUploadImageGuardianshipDoc = MutableLiveData<UploadImageResponse>()
    val mldCheckRedCrossStatus = MutableLiveData<RedCrossStatusResponse>()

    val dataModel: ContractDataModel by lazy { ContractDataModel() }

    var imageDownloaded = false


    fun checkRedCrossStatus() {
        viewModelScope.launch {
            mldCheckRedCrossStatus.postValue(callService { repository.checkRedCrossStatus() })
        }
    }

    fun checkMedicalStudent() {
        viewModelScope.launch {
            val response = callService { repository.checkMedicalStudent() }

            val finalResponse = if (response.data?.equals("ok14") == true)
                RedCrossStatusResponse(true)
            else
                RedCrossStatusResponse(false)

            finalResponse.baseStatus = BaseStatus(null, ServiceStatus.SUCCESS)
            mldCheckRedCrossStatus.postValue(finalResponse)
        }

    }


    fun downloadContractImage(guid: String, imageType: Int) {
        viewModelScope.launch {
            if (imageType == 2)
                mldDownloadImage.postValue(callService {
                    repository.getDocumentImage(guid)
                })
            else
                mldDownloadImageGuardian.postValue(callService {
                    repository.getDocumentImage(guid)
                })
        }
    }

    fun getRegistrationInfo(isOptional: Boolean = false, isFraction: Boolean = false) {
        viewModelScope.launch {
            mldLoadingState.postValue(LoadingState.LOADING)

            val checkCondition = async(Dispatchers.IO) {
                if (isFraction)
                    repository.checkFractionAgeAndHistory()
                else if (isOptional)
                    repository.checkOptionalAgeAndHistory()
                else
                    repository.checkAgeAndHistory()

            }
            val registration = async(Dispatchers.IO) {
                repository.getRegistrationInfo()
            }
            val responseCheckCondition = checkCondition.await()
            val responseRegistration = registration.await()
            mldLoadingState.postValue(LoadingState.NOT_LOADING)

            if (!responseCheckCondition.isSuccess) {
                mldErrorState.postValue(responseCheckCondition)
            } else if (!responseRegistration.isSuccess) {
                if (responseCheckCondition.data != null) {
                    mldErrorState.postValue(responseRegistration)
                } else {
                    mldCheckContractCondition.postValue(responseCheckCondition)
                }
            } else {

                //   getCityName(responseCheckCondition.data?.city)
                mldRegistrationInfo.postValue(responseRegistration) //(call : onCheckContractCondition)
                mldCheckContractCondition.postValue(responseCheckCondition) //(call : onCheckContractCondition)
            }
        }
    }

    fun checkContractCondition(isOptional: Boolean) {
        Timber.tag("checkAgeAndHis").v("isOptional=$isOptional")
        viewModelScope.launch {
            mldCheckContractCondition.postValue(callService { if (isOptional) repository.checkOptionalAgeAndHistory() else repository.checkAgeAndHistory() })
        }
    }

    fun getAsyncBranchInfo(provinceCode: String? = null, cityCode: String? = null) {
        viewModelScope.launch {
            val provinceOfBranch = async { getProvinceOfBranch(provinceCode = provinceCode) }
            val cityOfBranch =
                async { getCityOfBranch(provinceCode = provinceCode, cityCode = cityCode) }
            val addressOfBranch = async { getInfoBranch(cityCode = cityCode) }


            val provinceResponse = provinceOfBranch.await()
            val cityResponse = callService { cityOfBranch.await() }
            val branchResponse = callService { addressOfBranch.await() }

            mldGetProvinceWhitOutPaging.postValue(provinceResponse)
            mldGetCityWhitOutPaging.postValue(cityResponse)
            mldGetInfoOfBranch.postValue(branchResponse)
        }
    }


    fun updateUserAddressInfo(updateAddressInfoRequest: UpdateAddressInfoRequest) {
        viewModelScope.launch {
            mldSaveUsersAddressInfo.postValue(callService {
                repository.saveUsersAddressInfo(
                    updateAddressInfoRequest
                )
            })
        }
    }


    //Provinces
    fun getProvincesList(
        provinceName: String? = null,
        provinceCode: String? = null
    ): Flow<PagingData<ProvinceModel>> {
        val array = JsonArray()

        if (!provinceName.isNullOrBlank()) {
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
        return createPager(
            repository::getProvince,
            paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString())
        ).flow.cachedIn(
            viewModelScope
        )
    }


    private suspend fun getProvinceOfBranch(
        provinceCode: String? = null
    ): ProvinceResponse {

        val array = JsonArray()

        if (!provinceCode.isNullOrBlank()) {
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", "provinceCode")
            jsonObj.addProperty("operator", "EQUAL")
            jsonObj.addProperty("value", provinceCode)
            array.add(jsonObj)
        }
        return repository.getProvince(paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString()))
    }

    // BRANCH
    fun getBranchesInfoList(
        cityCode: String? = null,
        branchName: String = ""
    ): Flow<PagingData<BranchesInfoListModel>> {
        val array = JsonArray()

        if (branchName != "") {
            JsonObject().apply {
                addProperty("property", "name")
                addProperty("value", "*$branchName*")
                addProperty("operator", "LIKE")
                array.add(this)
            }
        }

        if (!cityCode.isNullOrBlank()) {
            JsonObject().apply {
                addProperty("property", "cityCode")
                addProperty("operator", "EQUAL")
                addProperty("value", cityCode)
                array.add(this)
            }
        }
        return createPager(
            repository::getInfoBranch,
            paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString())
        ).flow.cachedIn(
            viewModelScope
        )

    }

    private suspend fun getInfoBranch(
        cityCode: String? = null
    ): BranchesInfoListResponse {
        val array = JsonArray()

        if (!cityCode.isNullOrBlank()) {
            JsonObject().apply {
                addProperty("property", "cityCode")
                addProperty("operator", "EQUAL")
                addProperty("value", cityCode)
                array.add(this)
            }
        }
        return repository.getInfoBranch(paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString()))
    }

    // CITY
    private suspend fun getCityOfBranch(
        provinceCode: String? = null,
        cityCode: String? = null,
    ): CityNameListResponse {
        val array = JsonArray()

        provinceCode?.let {
            JsonObject().apply {
                addProperty("property", "provincecode")
                addProperty("operator", "EQUAL")
                addProperty("value", it)
                array.add(this)

            }
        }
//        cityCode?.let {
//            JsonObject().apply {
//                addProperty("property", "cityCode")
//                addProperty("operator", "EQUAL")
//                addProperty("value", cityCode)
//                addProperty("direction", "ASC")
//                array.add(this)
//
//            }
//        }
//        JsonObject().apply {
//            addProperty("property", "recipientName")
//            addProperty("value", cityCode)
//            array.add(this)
//        }

        return repository.getCityListByProvinceCodeAndCityName(
            mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString())
        )
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


    //Cities
    fun getCitiesList(
        provinceCode: String? = null,
        cityCode: String? = null,
        cityName: String = ""
    ): Flow<PagingData<CityModel>> {
        val array = JsonArray()
        if (cityName != "") {
            JsonObject().apply {
                addProperty("property", "cityName")
                addProperty("operator", "LIKE")
//                addProperty("value", "*$cityName*")
                addProperty("value", "*$cityName*%")
                array.add(this)
            }
        }
        provinceCode?.let {
            JsonObject().apply {
                addProperty("property", "provincecode")
                addProperty("operator", "EQUAL")
                addProperty("value", it)
                array.add(this)
            }
        }
        cityCode?.let {
            JsonObject().apply {
                addProperty("property", "cityCode")
                addProperty("operator", "EQUAL")
                addProperty("value", cityCode)
                array.add(this)
            }
        }

        return createPager(
            repository::getCitiesOfProvince,
            paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString())
        ).flow.cachedIn(viewModelScope)

    }

    fun uploadImage(image: MultipartBody.Part, requestType: Int = REQUEST_CODE_IMAGE_DOC) {
        viewModelScope.launch {
            val response = callService {
                repository.uploadImage(image)
            }
            if (requestType == REQUEST_CODE_IMAGE_GUARDIANSHIP)
                mldUploadImageGuardianshipDoc.postValue(response)
            else
                mldUploadImageContractDoc.postValue(response)

        }
    }

    fun getContractPremiumRate(premiumRate: String, typePremiumRate: String) {
        viewModelScope.launch {
            mldPremiumRateForContract.postValue(callService {
                repository.getContractPremiumRate(premiumRate, typePremiumRate)
            })
        }
    }

    fun getOptionalContractPremiumRate() {
        viewModelScope.launch {
            mldPremiumRateForContract.postValue(callService {
                repository.getOptionalContractPremiumRate()
            })
        }
    }

    fun checkAndCalculateSalaryForContract(premiumRate: String, typePremiumRate: String) {
        viewModelScope.launch {
            mldCheckAndCalculateSalaryForContract.postValue(callService {
                repository.checkAndCalculateSalaryForContract(premiumRate, typePremiumRate)
            })
        }
    }

    fun calculateSalaryForOptionalContract(premiumRate: String) {
        viewModelScope.launch {
            mldCheckAndCalculateSalaryForContract.postValue(callService {
                repository.calculateSalaryForOptionalContract(premiumRate)
            })
        }
    }


    fun getPremiumOptions() {
        viewModelScope.launch {
            mldPremiumOptions.postValue(callService { repository.getPremiumOptions() })
        }
    }

    fun getCancelContractReasons(): Flow<PagingData<CancelContractReasonsModel>> {
        val result = createPager(repository::getCancelContractReasons)
        return result.flow.cachedIn(viewModelScope)
    }

    fun requestCancelContract(body: CancelContractRequest, id: String, isOptional: Boolean) {
        viewModelScope.launch {
            mldRequestCancelContract.postValue(callService {
                if (isOptional)
                    repository.cancelOptionalContractRequest(
                        contractNumber = id,
                        body = body
                    )
                else
                    repository.cancelContractRequest(
                        contractNumber = id,
                        body = body
                    )
            })
        }
    }


    fun getFreelancerJobTitles(
        jobTitle: String? = null
    ): Flow<PagingData<FreelancerJobTitleModel>> {
        val array = JsonArray()
        if (jobTitle != "") {
            JsonObject().apply {
                addProperty("property", "discrioption")
                addProperty("operator", "LIKE")
                addProperty("value", "*$jobTitle*")
                array.add(this)
            }
        }
        return createPager(
            repository::getFreelancerJobTitle,
            paramsMap = mutableMapOf(Constants.ARRAY_KEY_FOR_MAP to array.toString())
        ).flow.cachedIn(viewModelScope)
    }

    fun getEligibilityStatusList() = Utility.getEligibilityStatusList()

    //getAddressOfBranch
    fun getSelfContractStateList(): Flow<PagingData<CancelContractReasonsModel>> =
        createPager(repository::getCancelContractReasons).flow.cachedIn(viewModelScope)


    fun updateContract(body: UpdateContractRequest, Premium: String) {
        viewModelScope.launch {
            mldUpdateContract.postValue(callService {
                repository.updateContract(
                    premium = Premium,
                    body = body
                )
            })
        }
    }

    fun updateOptionalContract(body: UpdateOptionalContract, Premium: String) {
        viewModelScope.launch {
            mldUpdateContract.postValue(callService {
                repository.updateOptionalContract(
                    premium = Premium,
                    body = body
                )
            })
        }
    }

    fun updateGuardianOptionalContract(body: UpdateGuardianOptionalContract, Premium: String) {
        viewModelScope.launch {
            mldUpdateContract.postValue(callService {
                repository.updateGuardianOptionalContract(
                    premium = Premium,
                    body = body
                )
            })
        }
    }


    fun makeContract(selectedSalary: Int, finalConfirmRequest: ContractRequest) {
        viewModelScope.launch {
            mldFinalRequestMakeContract.postValue(callService {
                repository.makeContract(selectedSalary, finalConfirmRequest)
            })
        }
    }

    fun makeFractionContract(req: FractionRequestDataModel = FractionRequestDataModel()) {
        /* fake data
        mldFinalRequestMakeContract.postValue( FinalConfirmResponse(FinalConfirmModel(4564545L,4564564465L)).apply { baseStatus =
            BaseStatus(MessageModel("message"),ServiceStatus.SUCCESS) })
*/

        viewModelScope.launch {
            mldFinalRequestMakeContract.postValue(callService {
                repository.makeFractionContract(req)
            })
        }
    }

    fun makeContractByGuardian(
        selectedSalary: Int,
        finalConfirmRequest: ContractByGuardianRequest
    ) {
        viewModelScope.launch {
            mldFinalRequestMakeContract.postValue(callService {
                repository.makeContractByGuardian(selectedSalary, finalConfirmRequest)
            })
        }
    }

    fun makeOptionalContract(selectedSalary: Int, finalConfirmRequest: OptionalContractRequest) {
        viewModelScope.launch {
            mldFinalRequestMakeContract.postValue(callService {
                repository.makeOptionalContract(selectedSalary, finalConfirmRequest)
            })
        }
    }

    fun makeOptionalContractByGuardian(
        selectedSalary: Int,
        finalConfirmRequest: OptionalContractByGuardian
    ) {
        viewModelScope.launch {
            mldFinalRequestMakeContract.postValue(callService {
                repository.makeOptionalContractByGuardian(selectedSalary, finalConfirmRequest)
            })
        }
    }

    fun updateGuardianContract(body: UpdateGuardianContract, Premium: String) {
        viewModelScope.launch {
            mldUpdateContract.postValue(callService {
                repository.updateGuardianContract(
                    premium = Premium,
                    body = body
                )
            })
        }
    }

    fun loadBranchInfo(data: CheckAgeAndHistoryModel) {
        dataModel.apply {
            provinceName = data.provinceName ?: ""
            cityNameOfBranch = data.city ?: ""
            val address = data.organizationAddress ?: ""
            address.replace("-", ",")
            branchAddress = buildString {
                append(provinceName.takeIf {
                    it.isNotBlank()
                })
                if (provinceName.isNotBlank() && cityNameOfBranch.isNotBlank())
                    append(", ")
                append(cityNameOfBranch.takeIf {
                    it.isNotBlank()
                })
                if (cityNameOfBranch.isNotBlank() && address.isNotBlank())
                    append(", ")
                append(address.takeIf { it.isNotBlank() })
            }

        }
    }
}