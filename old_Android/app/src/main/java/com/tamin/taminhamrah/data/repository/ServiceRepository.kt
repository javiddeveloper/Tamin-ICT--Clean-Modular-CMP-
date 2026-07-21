package com.tamin.taminhamrah.data.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.data.entity.DownloadFileResponse
import com.tamin.taminhamrah.data.entity.ProfileModel
import com.tamin.taminhamrah.data.entity.WorkshopInfoModel
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.data.local.services.ServiceLocalDataSource
import com.tamin.taminhamrah.data.remote.models.ListData
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.employer.InsuredDoc
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredUserInfoReq
import com.tamin.taminhamrah.data.remote.models.employer.RegisterLetterRequest
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.profile.asDomainModel
import com.tamin.taminhamrah.data.remote.models.services.AcraConfigResponse
import com.tamin.taminhamrah.data.remote.models.services.Bank
import com.tamin.taminhamrah.data.remote.models.services.BodySaveNonExistentHistory
import com.tamin.taminhamrah.data.remote.models.services.BranchDetailResponse
import com.tamin.taminhamrah.data.remote.models.services.CityNameListResponse
import com.tamin.taminhamrah.data.remote.models.services.ConfirmSurvivorRequest
import com.tamin.taminhamrah.data.remote.models.services.DebitObjection
import com.tamin.taminhamrah.data.remote.models.services.DeferredInstallmentReq
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.InsuranceTypeResponce
import com.tamin.taminhamrah.data.remote.models.services.ObjectionInsuranceHistoryModel
import com.tamin.taminhamrah.data.remote.models.services.PayRollResponse
import com.tamin.taminhamrah.data.remote.models.services.ProvinceResponse
import com.tamin.taminhamrah.data.remote.models.services.ServiceResponseModelNew
import com.tamin.taminhamrah.data.remote.models.services.ShortTermOrthosisReq
import com.tamin.taminhamrah.data.remote.models.services.ShorttremMariageReq
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.BranchesInfoListResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckAgeAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ConcludingStudentInsuranceContractResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractByGuardianRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.FreelancerJobTitlesResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.OptionalContractByGuardian
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.OptionalContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.UpdateAddressInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.cancelContract.CancelContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateGuardianContract
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateGuardianOptionalContract
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateOptionalContract
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.CalculationModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.PaymentCalculationDetailListResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.PaymentListModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.PaymentListResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.detailPayment.DetailPaymentListModel
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.detailPayment.DetailPaymentListResponse
import com.tamin.taminhamrah.data.remote.models.services.construction.WorkersPayDebitRequest
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilityFinalConfirmRequest
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilitySaveDocumentRequest
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilitySaveInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.edict.EdictPensionerResponse
import com.tamin.taminhamrah.data.remote.models.services.insuranceRegistration.RegistrationReq
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit.SubmitResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceReq
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentRequest
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentUrlRequest
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SaveSurvivorInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.FuneralAllowanceRequest
import com.tamin.taminhamrah.data.remote.models.services.requestPaymentForillDay.RequestPaymentForillDayReq
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.LatestInsuranceInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.RequestForPregnancyPayReq
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.ConfirmIdentityAndHistoryInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementSaveDocumentRequest
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.RequestAddDependent
import com.tamin.taminhamrah.data.remote.models.services.violations.ViolationRequest
import com.tamin.taminhamrah.data.remote.models.services.workshop.ConfirmConflictResponseItem
import com.tamin.taminhamrah.data.remote.models.services.workshop.ObjectionType
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopMemberResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.RegisterArticle16RequestModel
import com.tamin.taminhamrah.data.remote.models.user.LackEntitlementResponse
import com.tamin.taminhamrah.data.remote.services.ServicesRemoteDataSource
import com.tamin.taminhamrah.data.remote.user.UserRemoteDataSource
import com.tamin.taminhamrah.di.interceptor.TokenHolder
import com.tamin.taminhamrah.ui.home.services.contracts.model.FractionRequestDataModel
import com.tamin.taminhamrah.ui.home.services.employer.completeInfo.model.TicketRequest
import com.tamin.taminhamrah.ui.home.services.employer.completeInfo.model.VerifyCodeRequest
import com.tamin.taminhamrah.ui.home.services.employer.contract.model.MafasaHesabRequestModel
import com.tamin.taminhamrah.ui.home.services.employer.debt.InstallmentRequestModel
import com.tamin.taminhamrah.ui.home.services.employer.legalStackHolders.model.AgentRequestModel
import com.tamin.taminhamrah.ui.home.services.employer.onlineService.model.AgreementDataModel
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject

class ServiceRepository @Inject constructor(
    private val remoteDataSource: ServicesRemoteDataSource,
    private val localDataSource: ServiceLocalDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val pre: PreferenceManager,
    @ApplicationContext val context: Context
) {

    private fun createFilterWithEQOperator(map: Map<String, String?>): String {
        val array = JsonArray()
        map.forEach { (key, value) ->
            val jsonObj = JsonObject()
            jsonObj.addProperty("property", key)
            jsonObj.addProperty("value", value)
            jsonObj.addProperty("operator", "EQ")
            array.add(jsonObj)
        }

        return array.toString()
    }


    private fun createFilterWithEqualOperator(map: Map<String, String?>): String {

        val array = JsonArray()

        map.forEach { (key, value) ->

            val jsonObj = JsonObject()
            jsonObj.addProperty("property", key)
            jsonObj.addProperty("value", value)
            jsonObj.addProperty("operator", "EQUAL")
            array.add(jsonObj)
        }

        return array.toString()
    }

    private fun createFilterWithLikeOperator(map: Map<String, String?>): String {

        val array = JsonArray()

        map.forEach { (key, value) ->

            val jsonObj = JsonObject()
            jsonObj.addProperty("property", key)
            jsonObj.addProperty("value", value)
            jsonObj.addProperty("operator", "LIKE")
            array.add(jsonObj)
        }

        return array.toString()
    }

    /* init {
         pre.setToken("Bearer eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIwMDgzODM0MDAxIiwiaXNzIjoiaHR0cDovL2lkbS50YW1pbi5pciIsImF1ZCI6IjFlMzkyMzE4MWEwODE4MDQxYzFkMTkyYzM4MDUwNDBmIiwiZXhwIjoxNjI1MzA5MDM0LCJ1cm46dGFtaW46and0OmNsYWltOnRva2VuLXR5cGUiOiJhY2Nlc3NfdG9rZW4iLCJ1cm46dGFtaW46and0OmNsYWltOmdyb3VwcyI6WyJBTEwgVVNFUlMiLCJBTEwgVVNFUlMiXSwianRpIjoiOGZPZXpvYzB2bUt2U2VsZUttdGxFUSIsImlhdCI6MTYyNTMwNTQzNCwibmJmIjoxNjI1MzA1MzE0fQ.WPJChVqrBzc1XiVkTxpKVbabIW4PaZcvdvAifUpOA0_NRYgnDSk28pfJ2Vw2LfZaMVqR-n2OH2NcZPJjquTmN1HJSz_uN6MwF3N-lzOdxwOov4tdl7xSAO04sNPXTgmK8GVD9ylMj8UlW2UweKFMLG5WVPlMDH9b5_FbBJeKulP_U1I3c5IeZ9u73rDB7BTkL35FZjVo4kj3P95kjiguYTJvl26wbUjnFdR66qzzHW224wd-GDYhEtyqTJfClsyfQGgAQpmjzYG7ELzxsayMm9-ZMVzcE4KBhXm6HAj0kTeSRiwEw9MYc2KQrrFR0bLbnJBv5i3RkBo8Nfce2VLXow")
     }
 */

    /*
        //   suspend fun getServices(): Resource<List<ServiceMainModel>?> {
        suspend fun getServices(): ServiceResponseModel {
            val data = ServiceResponseModel()

            val result = localDataSource.getServices(TokenHolder.getAccessToken(pre))
            return when (result.baseStatus) {

                ServiceStatus.SUCCESS -> {

                    val resultList: List<ServiceMainModel>?
                    val list = result.data?.getServiceList()?.groups

                    list?.forEach { mainService ->
                        val services = mainService.serviceList?.filter { it.active }

                        mainService.serviceList = services
                    }

                    resultList = list?.filter { it.serviceList?.isNotEmpty() == true }

    */

    suspend fun getNationalCode(): String {
        val nationalCode = pre.getUserNationalCode()
        return if (nationalCode.isNullOrEmpty()) {
            val userInfo = getProfileInfo()
            setUserInfo(userInfo.asDomainModel())
            return getNationalCode()
        } else {
            nationalCode
        }
    }

    fun setUserInfo(item: ProfileModel) {

        pre.setUserFullName(item.fullName)
        pre.setUserNationalCode(item.nationalCode)
        pre.setUserEmail(item.email)
        pre.setUserPhoneNumber(item.phonenumber)


    }

    suspend fun getServices(): ServiceResponseModelNew =
//        if (/*Utility.isDebuggable(context) ||*/ BuildConfig.DEBUG) {
        localDataSource.getServices()
//        } else {
//            remoteDataSource.getServices()
//        }

    fun getLocalAcraConfig() = localDataSource.getAcraConfig()
    suspend fun getRemoteAcraConfig() = remoteDataSource.getAcraConfig()

    fun saveAcraConfig(acraConfigResponse: AcraConfigResponse) =
        localDataSource.saveAcraConfig(acraConfigResponse)

    suspend fun getTreatmentServices(): ServiceResponseModelNew //=
    {
        val serviceList = if (Utility.isDebuggable(context) || BuildConfig.DEBUG) {
            localDataSource.getServices()
        } else {
            remoteDataSource.getServices()
        }

        serviceList.data = serviceList.data?.filter { it.name == "خدمات درمانی" && it.type == 2 }

        return serviceList
    }

    fun saveWorkerPayInfo(ticket: String?, info: String?) {
        localDataSource.saveWorkerPayInfo(ticket ?: "", info ?: "")
    }

    fun getWorkerPayTicket() = localDataSource.getWorkerPayTicket()
    fun getWorkerPayInfo() = localDataSource.getWorkerPayInfo()

    //
    fun getInsuredServiceListFromJsonFile() = localDataSource.getInsuredServiceListFromJsonFile()
    //1475614443

//
//    fun getPensionerServiceListFromJsonFile() =
//        localDataSource.getPensionerServiceListFromJsonFile().map { it.asDomainModel() }


    // suspend fun saveServices(list: List<ServiceEntity>) = localDataSource.saveServices(list)
    //  fun isEmployerMode() = localDataSource.isEmployerMode()
    suspend fun getPensionInquiry() =
        remoteDataSource.getPensionInquiry(TokenHolder.getAccessToken(pre))

    suspend fun getRecipientList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getRecipientList(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun sendCertificateRequest(map: HashMap<String, String?>) =
        remoteDataSource.sendCertificateRequest(
            createFilterWithEqualOperator(map),
            TokenHolder.getAccessToken(pre)
        )


    suspend fun sendRequestInquirePensionCertificate(map: HashMap<String, String>) =
        remoteDataSource.sendRequestInquirePensionCertificate(
            createFilterWithEQOperator(map),
            token = TokenHolder.getAccessToken(pre)
        )


    suspend fun sendCertificateWage(map: HashMap<String, String?>): GeneralRes =
        remoteDataSource.sendCertificateWage(
            createFilterWithEqualOperator(map),
            TokenHolder.getAccessToken(pre)
        )


    suspend fun getCityNameWithPaging(map: MutableMap<String, String>): CityNameListResponse =
        remoteDataSource.getCityNameWithPaging(TokenHolder.getAccessToken(pre), map)

    suspend fun getCityName(map: HashMap<String, String>): CityNameListResponse =
        remoteDataSource.getCityName(
            TokenHolder.getAccessToken(pre),
            createFilterWithEqualOperator(map)
        )

    suspend fun getEdictPensioner(map: HashMap<String, String>): EdictPensionerResponse =
        remoteDataSource.getEdictPensioner(
            createFilterWithEqualOperator(map),
            TokenHolder.getAccessToken(pre)
        )


    suspend fun calculateMarriageAllowance(timeStamp: String) =
        remoteDataSource.calculateMarriageAllowance(TokenHolder.getAccessToken(pre), timeStamp)

    suspend fun calculateWageIllDay(
        StartDateTimeStamp: String,
        EndDateTimeStamp: String,
        marital_status: String
    ) =
        remoteDataSource.calculateWageIllDay(
            TokenHolder.getAccessToken(pre),
            StartDateTimeStamp,
            EndDateTimeStamp,
            marital_status
        )

    suspend fun calculateWagePregnancyDays(StartDateTimeStamp: String, EndDateTimeStamp: String) =
        remoteDataSource.calculateWagePregnancyDays(
            TokenHolder.getAccessToken(pre),
            StartDateTimeStamp,
            EndDateTimeStamp
        )

    suspend fun getWeddingPresent() =
        remoteDataSource.getWeddingPresent(TokenHolder.getAccessToken(pre))

    suspend fun getAllHistoryInsurance(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getAllHistoryInsurance(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getResultOfInquirePension(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getResultOfInquirePension(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getDependantsResponse(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getDependantsResponse(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getListInspectionPerformed(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getListInspectionPerformed(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getMyReportsOV(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getMyReportsOV(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getProvinceListForOV(
        paramsMap: MutableMap<String, String>?
    ) = remoteDataSource.getProvinceListForOV(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun sendReportOV(body: ViolationRequest) =
        remoteDataSource.sendReportOV(TokenHolder.getAccessToken(pre), body)

    suspend fun uploadDocumentOV(image: MultipartBody.Part) =
        remoteDataSource.uploadDocumentOV(TokenHolder.getAccessToken(pre), image)

    suspend fun getAllDocumentOV(id: Int, paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getAllDocumentOV(TokenHolder.getAccessToken(pre), id, paramsMap)

    suspend fun getWorkshopListInspectionPerformed(
        paramsMap: MutableMap<String, String>?
    ) = remoteDataSource.getWorkshopListInspectionPerformed(
        TokenHolder.getAccessToken(pre),
        paramsMap
    )

    suspend fun getWageAndHistoryInsurance(paramsMap: MutableMap<String, String>?): WageAndHistoryResponse =
        remoteDataSource.getWageAndHistoryInsurance(TokenHolder.getAccessToken(pre), paramsMap)


    suspend fun getWorkersPaymentInfo(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkersPaymentInfo(TokenHolder.getAccessToken(pre))

    suspend fun getWorkersPayDebit(body: WorkersPayDebitRequest) =
        remoteDataSource.getWorkersPayDebit(TokenHolder.getAccessToken(pre), body)

    // inspectTicket : check pay result
    suspend fun inspectTicket(ticket: String?, paymentInfo: String?) =
        remoteDataSource.inspectTicket(TokenHolder.getAccessToken(pre), ticket, paymentInfo)

    suspend fun getObjectionInsuranceHistory(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getObjectionInsuranceHistory(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getTitlesJob(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getTitlesJob(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun validateMarriageGift(date: String, nationalCode: String) =
        remoteDataSource.validateMarriageGift(TokenHolder.getAccessToken(pre), date, nationalCode)

    suspend fun marriageGiftRequest(value: ShorttremMariageReq) =
        remoteDataSource.marriageGiftRequest(TokenHolder.getAccessToken(pre), value)

    suspend fun sendObjectionInsuranceHistory(list: List<ObjectionInsuranceHistoryModel>) =
        remoteDataSource.sendObjectionInsuranceHistory(TokenHolder.getAccessToken(pre), list)

    suspend fun sendConfirmConflict(list: List<ConfirmConflictResponseItem>) =
        remoteDataSource.sendConfirmConflict(TokenHolder.getAccessToken(pre), list)

    suspend fun sendConfirmNotExist(list: List<ConfirmConflictResponseItem>) =
        remoteDataSource.sendConfirmNotExist(TokenHolder.getAccessToken(pre), list)

    suspend fun sendFinalConfirmConflict() =
        remoteDataSource.sendFinalConfirmConflict(TokenHolder.getAccessToken(pre))

    suspend fun sendFinalConfirmNoTexist() =
        remoteDataSource.sendFinalConfirmNotExist(TokenHolder.getAccessToken(pre))

    suspend fun checkStatusConflict() =
        remoteDataSource.checkStatusConflict(TokenHolder.getAccessToken(pre))

    suspend fun getInsuranceActiveRelation(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getInsuranceActiveRelation(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getIdentityInfo() =
        remoteDataSource.getIdentityInfo(TokenHolder.getAccessToken(pre))

    suspend fun getUserInfo() = remoteDataSource.getUserInfo(TokenHolder.getAccessToken(pre))

    suspend fun sendBankAccountInfo(
        accountNumberStr: String,
        accountTypeId: String,
        bankId: String,
        startDate: String
    ) = remoteDataSource.sendBankAccountInfo(
        TokenHolder.getAccessToken(pre),
        accountNumberStr,
        accountTypeId,
        bankId,
        startDate
    )

    suspend fun getBankAccountList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getBankAccountList(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getPensionerId() =
        remoteDataSource.getPensionerId(TokenHolder.getAccessToken(pre))

    suspend fun getPensionerPayRoll(
        map: HashMap<String, String>
    ): PayRollResponse {
        return remoteDataSource.getPensionerPayRoll(
            createFilterWithEqualOperator(map),
            TokenHolder.getAccessToken(pre)
        )
    }

    suspend fun getPensionerPayRoll(
        filter: String
    ): PayRollResponse {
        return remoteDataSource.getPensionerPayRoll(
            filter,
            TokenHolder.getAccessToken(pre)
        )
    }

    suspend fun sendPayRollToInbox(map: HashMap<String, String>) =
        remoteDataSource.sendPayRollToInbox(
            createFilterWithEqualOperator(map),
            TokenHolder.getAccessToken(pre)
        )


    suspend fun downloadTalfighiPdf() =
        remoteDataSource.downloadTalfighiPdf(TokenHolder.getAccessToken(pre))


    suspend fun downloadEdictPdf(map: HashMap<String, String>): PdfDownloadResponse {
        val filter: String = createFilterWithEqualOperator(map)
        return remoteDataSource.downloadEdictPdf(filter, TokenHolder.getAccessToken(pre))

    }

    suspend fun downloadAllHistoryPDF() =
        remoteDataSource.downloadAllHistoryPDF(TokenHolder.getAccessToken(pre))


    suspend fun downloadWageAndHistoryPDF() =
        remoteDataSource.downloadWageAndHistoryPDF(TokenHolder.getAccessToken(pre))


    suspend fun getViewShorttermRequestList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getViewShorttermRequestList(
            TokenHolder.getAccessToken(pre),
            paramsMap
        )

    suspend fun getPersonalInfo() =
        remoteDataSource.getPersonalInfo(TokenHolder.getAccessToken(pre))

    suspend fun getPersonalInfoDetail() =
        remoteDataSource.getPersonalInfo(TokenHolder.getAccessToken(pre))

    suspend fun checkGirlSurvivorConditions(nationalCode: String, pensionerId: String) =
        remoteDataSource.checkGirlSurvivorConditions(
            nationalCode,
            pensionerId,
            TokenHolder.getAccessToken(pre)
        )

    suspend fun getGirlSurvivorReport(
        address: String,
        phone: String,
        zipCode: String,
        fatherName: String,
        birthDate: Long,
        insuranceNumber: String,
        nationalCode: String,
        pensionId: String
    ) = remoteDataSource.getGirlSurvivorReport(
        TokenHolder.getAccessToken(pre),
        address,
        phone,
        zipCode,
        fatherName,
        birthDate,
        insuranceNumber,
        nationalCode,
        pensionId
    )


    suspend fun confirmGirlSurvivor(request: ConfirmSurvivorRequest) =
        remoteDataSource.confirmGirlSurvivor(request, TokenHolder.getAccessToken(pre))


    suspend fun getBeneficiary(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getBeneficiary(TokenHolder.getAccessToken(pre), paramsMap)


    suspend fun sendRequestDeferredInstallmentCertificateForMe(
        bankName: Bank,
        banckBracnh: String,
        garanteeType: String,
        guaranteeAmount: Long,
        installmentAmount: String,
        installmentCount: String,
        loanAmount: Long,
        pensionerId: String
    ) = remoteDataSource.sendRequestDeferredInstallmentCertificate(
        TokenHolder.getAccessToken(pre),
        DeferredInstallmentReq(
            bankName,
            banckBracnh,
            garanteeType,
            guaranteeAmount,
            installmentAmount,
            installmentCount,
            loanAmount,
            pensionerId
        )
    )


    suspend fun sendRequestDeferredInstallmentCertificateForOthers(
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
    ) = remoteDataSource.sendRequestDeferredInstallmentCertificate(
        TokenHolder.getAccessToken(pre),
        DeferredInstallmentReq(
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
        )
    )


    suspend fun getDeservedTreatment() =
        remoteDataSource.getDeservedTreatment(TokenHolder.getAccessToken(pre))

    suspend fun sendInsuranceHistoryToInstitution(
        allHistorySelected: Boolean,
        historyAndWageSelected: Boolean,
        combineHistorySelected: Boolean
    ) = remoteDataSource.sendInsuranceHistoryToInstitution(
        TokenHolder.getAccessToken(pre),
        allHistorySelected,
        historyAndWageSelected,
        combineHistorySelected
    )

    suspend fun sendAllInsuranceHistoryToInstitution(
    ) = remoteDataSource.sendAllInsuranceHistoryToInstitution(
        TokenHolder.getAccessToken(pre),
    )

    suspend fun isMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ) = remoteDataSource.isMultipleWorkshops(
        TokenHolder.getAccessToken(pre),
        branchCode,
        insuranceNumber
    )


    suspend fun calculateMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ) = remoteDataSource.calculateMultipleWorkshops(
        TokenHolder.getAccessToken(pre),
        branchCode,
        insuranceNumber
    )


    suspend fun getLastRelation() =
        remoteDataSource.getLastRelation(TokenHolder.getAccessToken(pre))


    suspend fun getElectronicPrescriptionList(
        paramsMap: MutableMap<String, String>?
    ) = remoteDataSource.getElectronicPrescriptionList(
        TokenHolder.getAccessToken(pre),
        paramsMap
    )

    suspend fun getCombinedRecordList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getCombinedRecordList(TokenHolder.getAccessToken(pre), paramsMap)


    suspend fun getElectronicPrescriptionDetail(
        paramsMap: MutableMap<String, String>?
    ) = remoteDataSource.getElectronicPrescriptionDetail(
        TokenHolder.getAccessToken(pre),
        paramsMap
    )

    suspend fun getElectronicPrescriptionPrice(noteHeadID: String, nationalCode: String) =
        remoteDataSource.getElectronicPrescriptionPrice(
            TokenHolder.getAccessToken(pre),
            noteHeadID,
            nationalCode
        )


    suspend fun getDependantUserUnder18(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getDependantUserUnder18(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getInsuredOrthosisInfo() =
        remoteDataSource.getInsuredOrthosisInfo(TokenHolder.getAccessToken(pre))


    suspend fun getWorkshopInfo(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkshopInfo(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getWorkshopMemberList(
        paramsMap: MutableMap<String, String>?
    ): WorkshopMemberResponse {
        return remoteDataSource.getWorkshopMemberList(TokenHolder.getAccessToken(pre), paramsMap)
    }

    suspend fun getWorkshopStackHolderList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkshopStackHolderList(TokenHolder.getAccessToken(pre), paramsMap)


    suspend fun sendRequestForPregnancyPay(requestForPregnancyPayReq: RequestForPregnancyPayReq) =
        remoteDataSource.sendRequestForPregnancyPay(
            TokenHolder.getAccessToken(pre),
            requestForPregnancyPayReq
        )

    suspend fun sendRequestForIllDay(illDayReq: RequestPaymentForillDayReq) =
        remoteDataSource.sendRequestForIllDay(TokenHolder.getAccessToken(pre), illDayReq)

    suspend fun getWorkshopDebtList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkshopDebtList(TokenHolder.getAccessToken(pre), paramsMap)


    suspend fun getWorkshopDebtInquiry(workshopId: String, branchCode: String) =
        remoteDataSource.getWorkshopDebtInquiry(
            TokenHolder.getAccessToken(pre),
            workshopId,
            branchCode
        )


    suspend fun uploadImage(image: MultipartBody.Part) =
        remoteDataSource.uploadImage(TokenHolder.getAccessToken(pre), image)

    suspend fun uploadPdfFile(image: MultipartBody.Part) =
        remoteDataSource.uploadPdfFile(TokenHolder.getAccessToken(pre), image)

    suspend fun checkInsuredInfo() =
        remoteDataSource.checkInsuredInfo(TokenHolder.getAccessToken(pre))


    suspend fun getPregnancyStatus() =
        remoteDataSource.getPregnancyStatus(TokenHolder.getAccessToken(pre))


    suspend fun getPregnancyType() =
        remoteDataSource.getPregnancyType(TokenHolder.getAccessToken(pre))


    suspend fun getLatestInsuranceInfo(): LatestInsuranceInfoResponse =
        remoteDataSource.getLatestInsuranceInfo(TokenHolder.getAccessToken(pre))

    suspend fun getWorkshopDemandDocs(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkshopDemandDocs(TokenHolder.getAccessToken(pre), paramsMap)


    suspend fun downloadDebtDocumentPdf(
        debtNumber: String,
        branchCode: String
    ) = remoteDataSource.downloadDebtDocumentPdf(
        TokenHolder.getAccessToken(pre),
        debtNumber,
        branchCode
    )

    suspend fun pensionerPayRollPDF(map: HashMap<String, String?>) =
        remoteDataSource.pensionerPayRollPDF(
            TokenHolder.getAccessToken(pre),
            createFilterWithEqualOperator(map)
        )


    suspend fun getWorkshopPaymentSheets(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkshopPaymentSheets(TokenHolder.getAccessToken(pre), paramsMap)


    suspend fun saveShorttermOrthosis(requestShortTermOrthosis: ShortTermOrthosisReq) =
        remoteDataSource.saveShortTermOrthosis(
            TokenHolder.getAccessToken(pre),
            requestShortTermOrthosis
        )

    suspend fun checkStatusNotExist() =
        remoteDataSource.checkStatusNotExist(TokenHolder.getAccessToken(pre))

    suspend fun getDebitReasonList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkshopDebitReasonList(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getDebitPaymentStatus(branchCode: String, debitNumber: String) =
        remoteDataSource.getDebitPaymentStatus(
            TokenHolder.getAccessToken(pre),
            debitNumber,
            branchCode
        )

    suspend fun normalDebitPayment(
        branchCode: String?, workshopId: String?, debitNumber: String?,
        peymanSequence: String?, seporde: Boolean
    ) = remoteDataSource.normalDebitPayment(
        TokenHolder.getAccessToken(pre),
        PaymentRequest(
            branchCode,
            workshopId,
            debitNumber,
            peymanSequence,
            if (seporde) "1" else "0"
        )
    )


    suspend fun getPaymentInfo(url: String) =
        remoteDataSource.getPaymentInfo(TokenHolder.getAccessToken(pre), url)

    suspend fun cancelPayment(url: String) =
        remoteDataSource.cancelPayment(TokenHolder.getAccessToken(pre), url)

    suspend fun getPaymentLink(url: String, body: PaymentUrlRequest) =
        remoteDataSource.getPaymentLink(
            token = TokenHolder.getAccessToken(pre),
            url = url,
            body = body
        )

    suspend fun getProvince(
        paramsMap: MutableMap<String, String>?
    ): ProvinceResponse {
        return remoteDataSource.getProvinceList(
            TokenHolder.getAccessToken(pre),
            paramsMap
        )
    }

    suspend fun getCitiesOfProvince(
        paramsMap: MutableMap<String, String>?
    ) = remoteDataSource.getCitiesOfProvince(
        TokenHolder.getAccessToken(pre),
        paramsMap
    )


    suspend fun getFreelancerJobTitle(
        paramsMap: MutableMap<String, String>?
    ): FreelancerJobTitlesResponse {
        return remoteDataSource.getFreelancerJobTitle(
            TokenHolder.getAccessToken(pre),
            paramsMap
        )
    }

    suspend fun getInfoBranch(
        paramsMap: MutableMap<String, String>?
    ): BranchesInfoListResponse {
        return remoteDataSource.getInfoBranch(
            TokenHolder.getAccessToken(pre),
            paramsMap
        )
    }

    suspend fun getCityListByProvinceCodeAndCityName(
        paramsMap: MutableMap<String, String>?
    ): CityNameListResponse {
        return remoteDataSource.getCityNameWithPaging(
            TokenHolder.getAccessToken(pre),
            paramsMap
        )
    }

    suspend fun getBranchDetailList(
        paramsMap: MutableMap<String, String>?
    ): BranchDetailResponse {
        /*   val array = JsonArray()
           if (branchName != "") {
               val jsonObj = JsonObject()
               jsonObj.addProperty("property", "name")
               jsonObj.addProperty("value", "*$branchName*")
               jsonObj.addProperty("operator", "LIKE")
               array.add(jsonObj)
           }
           if (!cityCode.equals("")) {
               val jsonObj2 = JsonObject()
               jsonObj2.addProperty("property", "cityCode")
               jsonObj2.addProperty("operator", "EQUAL")
               jsonObj2.addProperty("value", cityCode)
               array.add(jsonObj2)

           }
           val jsonObj4 = JsonObject()
           jsonObj4.addProperty("property", "type")
           jsonObj4.addProperty("operator", "EQUAL")
           jsonObj4.addProperty("value", "1")

           val jsonObj5 = JsonObject()
           jsonObj5.addProperty("property", "status")
           jsonObj5.addProperty("operator", "EQUAL")
           jsonObj5.addProperty("value", "1")


           array.add(jsonObj4)
           array.add(jsonObj5)*/

        return remoteDataSource.getBranchDetailList(
            TokenHolder.getAccessToken(pre),
            paramsMap
        )
    }


    suspend fun getInsuranceTypeList(
        paramsMap: MutableMap<String, String>?
    ): InsuranceTypeResponce {
        return remoteDataSource.getInsuranceTypeList(
            TokenHolder.getAccessToken(pre),
            paramsMap
        )
    }

    suspend fun getBranchDetailListWithBranchCode(paramsMap: MutableMap<String, String>?): BranchDetailResponse {
        return remoteDataSource.getBranchDetailListWithBranchCode(
            TokenHolder.getAccessToken(pre),
            paramsMap
        )
    }

    suspend fun getDependentInfo() =
        remoteDataSource.getDependentInfo(TokenHolder.getAccessToken(pre))

    suspend fun getInsuranceTypeListWithInsuranceTypeCode(paramsMap: MutableMap<String, String>?)
            : InsuranceTypeResponce {
        return remoteDataSource.getInsuranceTypeListWithInsuracneTypeCode(
            TokenHolder.getAccessToken(pre), paramsMap
        )
    }

    suspend fun getNotExistRequests(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getNotExistRequests(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun sendReqSaveNotExist(sendReq: BodySaveNonExistentHistory) =
        remoteDataSource.sendReqSaveNotExist(TokenHolder.getAccessToken(pre), sendReq)

    suspend fun deleteNotExist(reqno: String, id: String) =
        remoteDataSource.deleteNotExist(TokenHolder.getAccessToken(pre), reqno, id)

    suspend fun getPerformedInspectionList(
        paramsMap: MutableMap<String, String>?
    ) = remoteDataSource.getPerformedInspectionList(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getPerformedInspectionPdf(
        inspectionNumber: String
    ): Resource<String?> {
        val result =
            remoteDataSource.downloadPerformedInspectionPdf(
                TokenHolder.getAccessToken(pre),
                inspectionNumber
            )

        return when (result.status) {

            Resource.Status.SUCCESS -> {

                val filePath =
                    Utility.writeResponseBodyToDisk(
                        "بازرسی شماره $inspectionNumber",
                        context,
                        result.data?.body()
                    )
                Resource.success(filePath)

                //  Resource.success("")
            }

            Resource.Status.LOADING -> {
                Resource.loading()
            }

            Resource.Status.ERROR -> {
                Resource.error(result.message)
            }

            Resource.Status.NEED_REFRESH_TOKEN -> {
                Resource.needRefreshToken(result.message)
            }

            Resource.Status.NEED_NETWORK -> {
                Resource.needNetwork()
            }
        }
    }

    suspend fun getInspectionPdf(
        inspectionNumber: String
    ) = remoteDataSource.downloadInspectionPdf(TokenHolder.getAccessToken(pre), inspectionNumber)

    suspend fun getCovidResult() = remoteDataSource.getCovidResult(TokenHolder.getAccessToken(pre))

    suspend fun getWorkshopObjectionableDebtList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkshopObjectionableDebitList(
            TokenHolder.getAccessToken(pre),
            paramsMap
        )

    suspend fun getAllObjections(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getAllObjections(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getObjectionSms(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getObjectionsSms(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun checkWorkshopDebitObjectionPermission(orderRecipeDate: String) =
        remoteDataSource.checkWorkshopDebitObjectionPermission(
            TokenHolder.getAccessToken(pre),
            orderRecipeDate
        )


    suspend fun downloadDebitObjectionPDF(seqNo: Long?) =
        remoteDataSource.downloadDebitObjectionPDF(TokenHolder.getAccessToken(pre), seqNo)


    suspend fun downloadDebitObjectionReportPDF(seqNo: Long?) =
        remoteDataSource.downloadDebitObjectionReportPDF(TokenHolder.getAccessToken(pre), seqNo)

    suspend fun downloadContractPdf() =
        remoteDataSource.downloadContractPdf(
            TokenHolder.getAccessToken(pre),
            System.currentTimeMillis().toString()
        )

    suspend fun downloadOptionalContract() =
        remoteDataSource.downloadOptionalContractPdf(
            TokenHolder.getAccessToken(pre),
            System.currentTimeMillis().toString()
        )

    suspend fun downloadFractionContract() =
        remoteDataSource.downloadFractionContractPdf(
            TokenHolder.getAccessToken(pre),
            System.currentTimeMillis().toString()
        )

    suspend fun downloadInstallmentReport(letterNumber: String) =
        remoteDataSource.downloadInstallmentReport(
            TokenHolder.getAccessToken(pre),
            letterNumber
        )

    suspend fun getSelectedWorkshopInfo(
        workshopId: String,
        branchCode: String
    ): Resource<WorkshopInfoModel?> {
        val result = remoteDataSource.getSelectedWorkshopInfo(
            TokenHolder.getAccessToken(pre),
            workshopId, branchCode
        )

        return when (result.status) {

            Resource.Status.SUCCESS -> {
//                Resource.success(result.data?.listasDomainModel())
                Resource.loading()
            }

            Resource.Status.LOADING -> {
                Resource.loading()
            }

            Resource.Status.ERROR -> {
                Resource.error(result.message)
            }

            Resource.Status.NEED_REFRESH_TOKEN -> {
                Resource.needRefreshToken(result.message)
            }

            Resource.Status.NEED_NETWORK -> {
                Resource.needNetwork()
            }
        }
        Timber.tag("Loading: getWorkshopI").d(System.currentTimeMillis().toString())

    }

    fun getObjectionType(): ObjectionType {

        return localDataSource.getObjectionTypeList(TokenHolder.getAccessToken(pre))

//        remoteDataSource.getObjectionTypeList(TokenHolder.getAccessToken(pre))
    }

    suspend fun getInfoFuneral() = remoteDataSource.getInfoFuneral(TokenHolder.getAccessToken(pre))
    suspend fun inquiryDeceasedInfo(nationalCode: String) =
        remoteDataSource.inquiryDeceasedInfo(TokenHolder.getAccessToken(pre), nationalCode)

    suspend fun submitRequestFuneralAllowance(funeralGrantReq: FuneralAllowanceRequest) =
        remoteDataSource.submitRequestFuneralAllowance(
            TokenHolder.getAccessToken(pre),
            funeralGrantReq
        )

    suspend fun correctedAccountNumber(requestId: String) =
        remoteDataSource.correctedAccountNumber(TokenHolder.getAccessToken(pre), requestId)

    suspend fun sendDebitObjection(request: DebitObjection) =
        remoteDataSource.sendDebitObjection(TokenHolder.getAccessToken(pre), request)

    suspend fun getInquiryCertificate(nationalId: String, inquiryLicenseCode: String) =
        remoteDataSource.getInquiryCertificate(
            TokenHolder.getAccessToken(pre),
            nationalId,
            inquiryLicenseCode
        )


    suspend fun getInspectionInfo(
        insuranceId: String?,
        inspectionCode: String,
        nationalCode: String
    ) =
        remoteDataSource.getInspectionInfo(
            TokenHolder.getAccessToken(pre),
            insuranceId,
            inspectionCode,
            nationalCode
        )

    suspend fun getJobsTitle(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getJobTitle(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun sendInspectionRequest(submitResponse: SubmitResponse) =
        remoteDataSource.sendInspectionRequest(TokenHolder.getAccessToken(pre), submitResponse)

    suspend fun getProfileInfo(
        tempToken: String? = null
    ) =
        remoteDataSource.getProfileInfo(
            if (tempToken.isNullOrBlank()) {
                TokenHolder.getAccessToken(pre)
            } else {
                "Bearer $tempToken"
            }
        )

    suspend fun getUserProfileImage() =
        remoteDataSource.getUserProfileImage(TokenHolder.getAccessToken(pre))

    suspend fun getCurrentUser() = remoteDataSource.getCurrentUser(TokenHolder.getAccessToken(pre))

    suspend fun getElectronicFile(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getElectronicFile(TokenHolder.getAccessToken(pre), paramsMap)

    //parent == insurance Number
    suspend fun getMyElectronicFileDocumentFullSize(url: String) =
        remoteDataSource.getMyElectronicFileDocumentFullSize(TokenHolder.getAccessToken(pre), url)

    suspend fun downloadDebitObjectionReportPDF(
        id: Long,
        parent: String
    ) = remoteDataSource.downloadDebitObjectionReportPDF(TokenHolder.getAccessToken(pre), id)


    suspend fun getTreatmentCostsPDF(
        id: String,
    ) = remoteDataSource.getTreatmentCostsPDF(TokenHolder.getAccessToken(pre), id)

    suspend fun sendToInboxTreatmentCosts(
        id: String,
    ) = remoteDataSource.sendToInboxTreatmentCosts(TokenHolder.getAccessToken(pre), id)


    suspend fun getContractList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getContractList(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getAssignerContractList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getAssignerContractList(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getComputationalBaseList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getComputationalBaseList(TokenHolder.getAccessToken(pre), paramsMap)


    suspend fun getTreatmentCosts(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getTreatmentCosts(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getConfirmationMedicalAuthorities(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getConfirmationMedicalAuthorities(TokenHolder.getAccessToken(pre))

    suspend fun checkRedCrossStatus() =
        remoteDataSource.checkRedCrossStatus(TokenHolder.getAccessToken(pre))

    suspend fun checkMedicalStudent() =
        remoteDataSource.checkMedicalStudent(TokenHolder.getAccessToken(pre))


    suspend fun getDocumentImage(id: String?): GeneralRes {
        val result = remoteDataSource.getDocumentImage(TokenHolder.getAccessToken(pre), id)
        if (result.isSuccess) {
            val imageBytes = Base64.decode(result.data.toString(), Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val imageUri = ImageUtils.saveImageExternal(decodedImage, context)
            result.data = imageUri ?: ""
        }
        return result

    }

    suspend fun downloadComputationalBasePdf(
        documentId: String,
        fileName: String
    ): Resource<String?> {
        val result = remoteDataSource.downloadComputationalBasePdf(
            TokenHolder.getAccessToken(pre),
            documentId
        )

        return when (result.status) {

            Resource.Status.SUCCESS -> {

                val filePath =
                    Utility.writeResponseBodyToDisk(fileName, context, result.data?.body())
                Resource.success(filePath)

                //  Resource.success("")
            }

            Resource.Status.LOADING -> {
                Resource.loading()
            }

            Resource.Status.ERROR -> {
                Resource.error(result.message)
            }

            Resource.Status.NEED_REFRESH_TOKEN -> {
                Resource.needRefreshToken(result.message)
            }

            Resource.Status.NEED_NETWORK -> {
                Resource.needNetwork()
            }
        }

    }

    suspend fun sendEdictPensionerToMyInbox(map: HashMap<String, String>): GeneralRes {
        val filter: String = createFilterWithEqualOperator(map)
        return remoteDataSource.sendEdictPensionerToMyInbox(filter, TokenHolder.getAccessToken(pre))
    }

    suspend fun calculateFreelanceDebitByMonth(month: Int) =
        remoteDataSource.calculateFreelanceDebit(TokenHolder.getAccessToken(pre), month)

    suspend fun calculateOptionalInsuranceDebitByMonth(month: Int) =
        remoteDataSource.calculateOptionalInsuranceDebitByMonth(
            TokenHolder.getAccessToken(pre),
            month
        )

    suspend fun getRegistrationInfo(): ConcludingStudentInsuranceContractResponse {
        Timber.tag("ParaleleServiceCall").i("getRegistrationInfo Called: ")

        return remoteDataSource.getRegistrationInfo(TokenHolder.getAccessToken(pre))
    }

    suspend fun getCityList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getCityList(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun sendInsuranceRegistration(request: RegistrationReq) =
        remoteDataSource.sendInsuranceRegistration(TokenHolder.getAccessToken(pre), request)

    suspend fun saveUsersAddressInfo(updateAddressInfoRequest: UpdateAddressInfoRequest) =
        remoteDataSource.saveUsersAddressInfo(
            TokenHolder.getAccessToken(pre),
            updateAddressInfoRequest
        )

    suspend fun checkAgeAndHistory(): CheckAgeAndHistoryResponse {
        return remoteDataSource.checkAgeAndHistory(TokenHolder.getAccessToken(pre))
    }

    suspend fun checkFractionAgeAndHistory(): CheckAgeAndHistoryResponse {
        return remoteDataSource.checkFractionAgeAndHistory(TokenHolder.getAccessToken(pre))
    }

    suspend fun checkOptionalAgeAndHistory(): CheckAgeAndHistoryResponse {
        return remoteDataSource.checkOptionalAgeAndHistory(TokenHolder.getAccessToken(pre))
    }

    suspend fun checkContractStatus() =
        remoteDataSource.checkContractStatus(TokenHolder.getAccessToken(pre))

    suspend fun checkOptionalInsuranceContractStatus() =
        remoteDataSource.checkOptionalInsuranceContractStatus(TokenHolder.getAccessToken(pre))

    suspend fun checkSuccessPaymentStatus(systemType: String) =
        remoteDataSource.checkSuccessPaymentStatus(TokenHolder.getAccessToken(pre), systemType)

    suspend fun getFreelanceLastPayment() =
        remoteDataSource.getFreelanceLastPayment(TokenHolder.getAccessToken(pre))

    suspend fun getOptionalInsuranceLastPayment() =
        remoteDataSource.getOptionalInsuranceLastPayment(TokenHolder.getAccessToken(pre))

    suspend fun getContractPremiumRate(premiumRate: String, typePremiumRate: String) =
        remoteDataSource.getContractPremiumRate(
            TokenHolder.getAccessToken(pre),
            premiumRate,
            typePremiumRate
        )


    suspend fun getOptionalContractPremiumRate() =
        remoteDataSource.getOptionalContractPremiumRate(TokenHolder.getAccessToken(pre))

    suspend fun checkAndCalculateSalaryForContract(premiumRate: String, typePremiumRate: String) =
        remoteDataSource.checkAndCalculateSalaryForContract(
            TokenHolder.getAccessToken(pre),
            premiumRate,
            typePremiumRate
        )

    suspend fun calculateSalaryForOptionalContract(premiumRate: String) =
        remoteDataSource.calculateSalaryForOptionalContract(
            TokenHolder.getAccessToken(pre),
            premiumRate
        )

    suspend fun makeContract(selectedSalary: Int, finalConfirmRequest: ContractRequest) =
        remoteDataSource.makeContract(
            TokenHolder.getAccessToken(pre),
            selectedSalary,
            finalConfirmRequest
        )

    suspend fun makeFractionContract(req: FractionRequestDataModel) =
        remoteDataSource.makeFractionContract(TokenHolder.getAccessToken(pre), req)


    suspend fun makeOptionalContract(
        selectedSalary: Int,
        finalConfirmRequest: OptionalContractRequest
    ) =
        remoteDataSource.makeOptionalContract(
            TokenHolder.getAccessToken(pre),
            selectedSalary,
            finalConfirmRequest
        )

    suspend fun makeContractByGuardian(
        selectedSalary: Int,
        finalConfirmRequest: ContractByGuardianRequest
    ) =
        remoteDataSource.makeContractByGuardian(
            TokenHolder.getAccessToken(pre),
            selectedSalary,
            finalConfirmRequest
        )


    suspend fun makeOptionalContractByGuardian(
        selectedSalary: Int,
        finalConfirmRequest: OptionalContractByGuardian
    ) =
        remoteDataSource.makeOptionalContractByGuardian(
            TokenHolder.getAccessToken(pre),
            selectedSalary,
            finalConfirmRequest
        )


    suspend fun updateContract(body: UpdateContractRequest, premium: String) =
        remoteDataSource.updateContract(TokenHolder.getAccessToken(pre), premium, body)

    suspend fun updateOptionalContract(body: UpdateOptionalContract, premium: String) =
        remoteDataSource.updateOptionalContract(TokenHolder.getAccessToken(pre), premium, body)

    suspend fun updateGuardianOptionalContract(
        body: UpdateGuardianOptionalContract,
        premium: String
    ) =
        remoteDataSource.updateGuardianOptionalContract(
            TokenHolder.getAccessToken(pre),
            premium,
            body
        )


    suspend fun updateGuardianContract(body: UpdateGuardianContract, premium: String) =
        remoteDataSource.updateGuardianContract(TokenHolder.getAccessToken(pre), premium, body)


    suspend fun getPremiumOptions() =
        remoteDataSource.getPremiumOptions(TokenHolder.getAccessToken(pre))

    suspend fun getCancelContractReasons(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getCancelContractReasons(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun insurancePayment(
        startDate: Long?,
        endDate: Long?,
        amount: Long?,
        systemType: String?,
        paramPage: String?,
        month: Int?,
        redirectUrl: String
    ) = remoteDataSource.insurancePayment(
        TokenHolder.getAccessToken(pre),
        startDate,
        endDate, amount, systemType, paramPage, month, redirectUrl
    )

    suspend fun cancelContractRequest(body: CancelContractRequest, contractNumber: String) =
        remoteDataSource.cancelContractRequest(
            token = TokenHolder.getAccessToken(pre),
            contractNumber = contractNumber,
            body = body
        )

    suspend fun cancelOptionalContractRequest(body: CancelContractRequest, contractNumber: String) =
        remoteDataSource.cancelOptionalContractRequest(
            token = TokenHolder.getAccessToken(pre),
            contractNumber = contractNumber,
            body = body
        )


    suspend fun getContractsPaymentsListFreelance(contractNumber: String): PaymentListResponse {

        val result = remoteDataSource.getContractsPaymentsListFreelance(
            token = TokenHolder.getAccessToken(pre),
            contractNumber = contractNumber
        )

        var response = PaymentListResponse().apply {
            status = result.status
            family = result.family
            reason = result.reason
            baseStatus = result.baseStatus
        }
        val list = ArrayList<PaymentListModel>()
        result.data?.list?.forEach { item ->
            list.add(
                PaymentListModel(
                    nationalId = item?.get(1) as String?,
                    insuranceId = item?.get(2) as String?,
                    debtNumber = item?.get(3) as String?,
                    startTermPayment = item?.get(5) as String?,
                    endTermPayment = item?.get(6) as String?,
                    totalDebt = item?.get(7) as Double?,
                    paymentDeadLine = item?.get(8) as String?,
                    amountPayment = item?.get(9) as Double?,
                    datePayment = item?.get(10) as String?,
                    statusContract = item?.get(11) as String?,
                    statusRecipient = item?.get(12) as String?
                )
            )
        }
        response.data = ListData()
        response.data?.list = list
        return response
    }

    suspend fun getPaymentCalculationDetailList(paramsMap: MutableMap<String, String>?): PaymentCalculationDetailListResponse {
        val result = remoteDataSource.getPaymentCalculationDetailList(
            token = TokenHolder.getAccessToken(pre),
            paramsMap
        )

        var response = PaymentCalculationDetailListResponse().apply {
            status = result.status
            family = result.family
            reason = result.reason
            baseStatus = result.baseStatus
        }
        val list = ArrayList<CalculationModel>()
        result.data?.list?.forEach { item ->
            list.add(
                CalculationModel(
                    year = item?.get(0) as? String,
                    month = item?.get(1) as? String,
                    day = item?.get(2) as? String,
                    description = item?.get(3) as? String,
                    wage = item?.get(4) as? Double,
                    amount = item?.get(5) as? Double
                )
            )
        }
        response.data = ListData()
        response.data?.list = list
        return response
    }

    suspend fun getDetailPaymentFreelance(
        contractNumber: String,
        debitNumber: String
    ): DetailPaymentListResponse {

        val result = remoteDataSource.getDetailPaymentFreelance(
            token = TokenHolder.getAccessToken(pre),
            contractNumber = contractNumber,
            debitNumber = debitNumber
        )

        var response = DetailPaymentListResponse().apply {
            status = result.status
            family = result.family
            reason = result.reason
            baseStatus = result.baseStatus
        }
        val list = ArrayList<DetailPaymentListModel>()
        result.data?.list?.forEach { item ->
            list.add(
                DetailPaymentListModel(
                    year = item?.get(0) as String?,
                    month = item?.get(1) as String?,
                    day = item?.get(2) as String?,
                    descDebtAmount = item?.get(3) as String?,
                    wage = item?.get(4) as Double?,
                    debtAmount = item?.get(5) as Double?
                )
            )
        }
        response.data = ListData()
        response.data?.list = list
        return response
    }

    suspend fun getContractInsuranceList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getContractInsuranceList(
            TokenHolder.getAccessToken(pre), paramsMap
        )


    suspend fun getLegalWorkshopInfo(legalWorkshopID: String?) =
        remoteDataSource.getLegalWorkshopInfo(TokenHolder.getAccessToken(pre), legalWorkshopID)

    suspend fun getLegalWorkshopCEOInfo(nationalCode: String?, birthdate: String?) =
        remoteDataSource.getLegalWorkshopCEOInfo(
            TokenHolder.getAccessToken(pre),
            nationalCode,
            birthdate
        )

    suspend fun getVerificationTicketForLegalWorkshopInfo(
        mobileNumber: String?,
        email: String?,
        nationalCode: String?
    ) = remoteDataSource.getVerificationTicketForLegalWorkshopInfo(
        TokenHolder.getAccessToken(pre),
        mobileNumber,
        email,
        nationalCode
    )

    suspend fun sendVerifyTicketForLegalWorkshop(body: TicketRequest) =
        remoteDataSource.sendVerifyTicketForLegalWorkshop(TokenHolder.getAccessToken(pre), body)


    suspend fun getEmployerAgreementInfoList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getEmployerAgreementInfoList(TokenHolder.getAccessToken(pre), paramsMap)


    suspend fun getSpecialContactList(paramsMap: MutableMap<String, String>?): WorkshopInfoResponse {
        Timber.tag("workshopData").i("workshop service called ")
        return remoteDataSource.getSpecialContactList(TokenHolder.getAccessToken(pre), paramsMap)

    }

    suspend fun getWorkshopDebtInfo(
        paramsMap: MutableMap<String, String>?
    ) = remoteDataSource.getWorkshopDebtInfo(
        TokenHolder.getAccessToken(pre),
        paramsMap
    )

    suspend fun getAllInstallmentList(
        paramsMap: MutableMap<String, String>?
    ) = remoteDataSource.getAllInstallmentList(
        TokenHolder.getAccessToken(pre),
        paramsMap
    )

    suspend fun sendCommitmentRequest(mobile: String, email: String, serviceName: String) =
        remoteDataSource.sendCommitmentRequest(
            TokenHolder.getAccessToken(pre),
            mobile,
            email,
            serviceName
        )

    suspend fun getUserInfoWithVerificationCode(verifyCode: String) =
        remoteDataSource.sendVerificationCode(TokenHolder.getAccessToken(pre), verifyCode)

    suspend fun postRealWorkshopVerificationCode(verifyCode: VerifyCodeRequest) =
        remoteDataSource.sendVerificationCode(TokenHolder.getAccessToken(pre), verifyCode)

    suspend fun getWorkshopsInfoWithoutContract(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkshopsInfoWithoutContract(TokenHolder.getAccessToken(pre), paramsMap)


    suspend fun getWorkshopContactList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkshopContactList(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getEmployerWorkshopList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getEmployerWorkshopList(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun postEmployerAgreement(body: AgreementDataModel) =
        remoteDataSource.postEmployerAgreement(TokenHolder.getAccessToken(pre), body)


    suspend fun checkUserIsNew(nationalId: String?) =
        remoteDataSource.checkUserIsNew(TokenHolder.getAccessToken(pre), nationalId)

    suspend fun getRequestSummary(requestId: Long?) =
        remoteDataSource.getRequestSummary(TokenHolder.getAccessToken(pre), requestId)

    suspend fun deleteRecentlyAddedUser(
        personalId: Long?
    ) = remoteDataSource.deleteRecentlyAddedUser(TokenHolder.getAccessToken(pre), personalId)

    suspend fun confirmRecentlyAddedUser(
        requestId: Long?
    ) = remoteDataSource.confirmRecentlyAddedUser(TokenHolder.getAccessToken(pre), requestId)


    suspend fun postNewInsuredInfo(dataModel: NewInsuredUserInfoReq?) =
        remoteDataSource.postNewInsuredInfo(TokenHolder.getAccessToken(pre), dataModel)

    suspend fun updateNewInsuredInfo(requestId: Long?, dataModel: NewInsuredUserInfoReq?) =
        remoteDataSource.updateNewInsuredInfo(TokenHolder.getAccessToken(pre), requestId, dataModel)

    suspend fun getInsuredRegistrationDocList(personalId: Long?) =
        remoteDataSource.getInsuredRegistrationDocList(TokenHolder.getAccessToken(pre), personalId)

    suspend fun putInsuredRegistrationDocList(personalId: String?, list: ArrayList<InsuredDoc>) =
        remoteDataSource.putInsuredRegistrationDocList(
            TokenHolder.getAccessToken(pre),
            personalId,
            list
        )


    suspend fun getWorkshopRecentlyAddedMembers(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkshopRecentlyAddedMembers(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getRecentlyAddedUser(personalRequestId: Long?) =
        remoteDataSource.getRecentlyAddedUser(TokenHolder.getAccessToken(pre), personalRequestId)

    suspend fun getOptionalInsurancePaymentCalculationDetailList(paramsMap: MutableMap<String, String>?): PaymentCalculationDetailListResponse {
        val result = remoteDataSource.getOptionalInsurancePaymentCalculationDetailList(
            token = TokenHolder.getAccessToken(pre),
            paramsMap
        )

        var response = PaymentCalculationDetailListResponse().apply {
            status = result.status
            family = result.family
            reason = result.reason
            baseStatus = result.baseStatus
        }
        val list = ArrayList<CalculationModel>()
        result.data?.list?.forEach { item ->
            list.add(
                CalculationModel(
                    year = item?.get(0) as? String,
                    month = item?.get(1) as? String,
                    day = item?.get(2) as? String,
                    description = item?.get(3) as? String,
                    wage = item?.get(4) as? Double,
                    amount = item?.get(5) as? Double
                )
            )
        }
        response.data = ListData()
        response.data?.list = list
        return response
    }

    suspend fun getLegalStackHolderList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getLegalStackHolderList(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun requestLegalStackHolderTicket(nationalCode: String? = null) =
        remoteDataSource.requestLegalStackHolderTicket(
            TokenHolder.getAccessToken(pre),
            nationalCode
        )

    suspend fun verifyLegalStackHolderTicket(ticket: String) =
        remoteDataSource.verifyLegalStackHolderTicket(TokenHolder.getAccessToken(pre), ticket)

    suspend fun getLegalAgentList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getLegalAgentList(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun submitNewLegalAgent(ticket: String, request: AgentRequestModel) =
        remoteDataSource.submitNewLegalAgent(TokenHolder.getAccessToken(pre), ticket, request)

    suspend fun deleteLegalAgent(ticket: String?, stackId: Long?) =
        remoteDataSource.deleteLegalAgent(TokenHolder.getAccessToken(pre), ticket, stackId)

    suspend fun getFamilyRelationShips(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getFamilyRelationShips(
            TokenHolder.getAccessToken(pre),
            paramsMap
        )

    suspend fun getActiveBranch() =
        remoteDataSource.getActiveBranch(TokenHolder.getAccessToken(pre))

    suspend fun inquiryRegistryInfo(
        nationalCode: String,
        timeStampBirthDay: String,
        dependencyCode: String,
    ) = remoteDataSource.inquiryRegistryInfo(
        TokenHolder.getAccessToken(pre),
        nationalCode,
        timeStampBirthDay,
        dependencyCode
    )

    suspend fun inquiryEducationCode(nationalId: String, inquiryLicenseCode: String) =
        remoteDataSource.inquiryEducationCode(
            TokenHolder.getAccessToken(pre),
            nationalId,
            inquiryLicenseCode
        )

    suspend fun addNewDependent(requestBody: RequestAddDependent) =
        remoteDataSource.addNewDependent(TokenHolder.getAccessToken(pre), requestBody)

    suspend fun dependentCancellation(
        action: String,
        identifier: String,
        date: String
    ) = remoteDataSource.dependentCancellation(
        TokenHolder.getAccessToken(pre),
        action,
        identifier,
        date
    )

    suspend fun refreshDependent() =
        remoteDataSource.refreshDependent(TokenHolder.getAccessToken(pre))

    suspend fun checkRenewCondition() =
        remoteDataSource.checkRenewCondition(TokenHolder.getAccessToken(pre))

    suspend fun inquiryStudyCodeCertificate(code: String, studyCode: String) =
        remoteDataSource.inquiryStudyCodeCertificate(
            TokenHolder.getAccessToken(pre),
            code = code,
            studyCode = studyCode
        )

    suspend fun getDisabilityDependentInfo() =
        remoteDataSource.getDisabilityDependentInfo(token = TokenHolder.getAccessToken(pre))

    suspend fun getDisabilityPersonalInfo() =
        remoteDataSource.getDisabilityPersonalInfo(token = TokenHolder.getAccessToken(pre))

    suspend fun saveDisabilityUserInfo(body: DisabilitySaveInfoRequest) =
        remoteDataSource.saveDisabilityUserInfo(
            token = TokenHolder.getAccessToken(pre),
            body = body
        )

    suspend fun getUserAge(birthDate: Long) =
        remoteDataSource.getUserAge(token = TokenHolder.getAccessToken(pre), birthDate = birthDate)

    suspend fun getMedicalCommissionPdf(lastWorkShop: String) =
        remoteDataSource.getMedicalCommissionPdf(
            token = TokenHolder.getAccessToken(pre),
            lastWorkShop = lastWorkShop
        )

    suspend fun finalConfirmDisabilityRequest(
        requestId: Long,
        body: DisabilityFinalConfirmRequest
    ) = remoteDataSource.finalConfirmDisabilityRequest(
        token = TokenHolder.getAccessToken(pre),
        requestId = requestId,
        body = body
    )

    suspend fun saveDocumentDisability(requestId: Long, body: DisabilitySaveDocumentRequest) =
        remoteDataSource.saveDocumentDisability(
            token = TokenHolder.getAccessToken(pre),
            requestId = requestId,
            body = body
        )

    suspend fun getMafasaHesabContractSubjects(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getMafasaHesabContractSubjects(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun sendMafasaHesabRequest(id: String, body: MafasaHesabRequestModel) =
        remoteDataSource.sendMafasaHesabRequest(TokenHolder.getAccessToken(pre), id, body)

    suspend fun getRegistrationDeclarationForm() =
        remoteDataSource.getRegistrationDeclarationForm(TokenHolder.getAccessToken(pre))

    suspend fun getDebtDiscount(branchCode: String, debitNumber: String, debitRemain: Long) =
        remoteDataSource.getDebtDiscount(
            TokenHolder.getAccessToken(pre),
            branchCode,
            debitNumber,
            debitRemain
        )

    suspend fun installmentDebt(request: InstallmentRequestModel) =
        remoteDataSource.installmentDebt(TokenHolder.getAccessToken(pre), request)

    suspend fun getClause38List(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getClause38List(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getClause38Detail(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getClause38Detail(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getLetterSubject(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getLetterSubject(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun deleteExistLetter(letterRequestId: Long?, workshopId: String?) =
        remoteDataSource.deleteExistLetter(
            TokenHolder.getAccessToken(pre),
            letterRequestId,
            workshopId
        )

    suspend fun registerLetter(request: RegisterLetterRequest) =
        remoteDataSource.registerLetter(TokenHolder.getAccessToken(pre), request)


    suspend fun getWorkshopListDefinitiveDebt(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkshopListDefinitiveDebt(
            token = TokenHolder.getAccessToken(pre),
            paramsMap = paramsMap
        )

    suspend fun getWorkshopsDebtsList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkshopsDebtsList(
            token = TokenHolder.getAccessToken(pre),
            paramsMap = paramsMap
        )

    suspend fun getWorkshopInfoDebtArticle16(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getWorkshopInfoDebtArticle16(
            token = TokenHolder.getAccessToken(pre),
            paramsMap = paramsMap
        )


    suspend fun registrationRequestArticle16(body: RegisterArticle16RequestModel) =
        remoteDataSource.registrationRequestArticle16(
            token = TokenHolder.getAccessToken(pre),
            body = body
        )

    suspend fun getRequestInfoArticle16(objectionNumber: Long) =
        remoteDataSource.getRequestInfoArticle16(
            token = TokenHolder.getAccessToken(pre),
            objectionNumber = objectionNumber
        )

    suspend fun approveDistantLetter(requestId: Long?, workshopId: String?) =
        remoteDataSource.approveDistantLetter(
            token = TokenHolder.getAccessToken(pre), workshopId, requestId
        )

    suspend fun getShortTermRequestStatus(referenceId: String) =
        remoteDataSource.getShortTermRequestStatus(
            token = TokenHolder.getAccessToken(pre),
            referenceId = referenceId
        )

    suspend fun getShortTermRequestInfo(referenceId: String) =
        remoteDataSource.getShortTermRequestInfo(
            token = TokenHolder.getAccessToken(pre),
            referenceId = referenceId
        )

    suspend fun getDocument(id: String, title: String): DownloadFileResponse {
        val result = remoteDataSource.getDocument(TokenHolder.getAccessToken(pre), id)
        if (result.isSuccess) {
            val imageBytes = Base64.decode(result.data.toString(), Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val imageUri = ImageUtils.saveImageExternal(decodedImage, context)
            result.uri = imageUri
            result.detail?.fileName = title
        }
        return result
    }

    suspend fun sendDistantCorrespondenceRequest(dataModel: RegisterLetterRequest) =
        remoteDataSource.sendDistantCorrespondenceRequest(
            TokenHolder.getAccessToken(pre),
            dataModel
        )

    suspend fun getDistantCorrespondenceList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getDistantCorrespondenceList(TokenHolder.getAccessToken(pre), paramsMap)


    suspend fun getDeceasedInfo(nationalCode: String) =
        remoteDataSource.getDeceasedInfo(
            token = TokenHolder.getAccessToken(pre),
            nationalCode = nationalCode
        )

    suspend fun getSurvivorList(deceasedNationalId: String) =
        remoteDataSource.getSurvivorList(
            token = TokenHolder.getAccessToken(pre),
            deceasedNationalId = deceasedNationalId
        )

    suspend fun saveSurvivorInfo(body: SaveSurvivorInfoRequest) =
        remoteDataSource.saveSurvivorInfo(token = TokenHolder.getAccessToken(pre), body = body)

    suspend fun confirmSurvivorsList() =
        remoteDataSource.confirmSurvivorsList(token = TokenHolder.getAccessToken(pre))

    suspend fun getFinalSurvivorPensionPDF() =
        remoteDataSource.getFinalSurvivorPensionPDF(token = TokenHolder.getAccessToken(pre))

    suspend fun submitFinalSurvivorPension(requestId: Int) =
        remoteDataSource.submitFinalSurvivorPension(
            token = TokenHolder.getAccessToken(pre),
            requestId = requestId
        )

    suspend fun getAge(birthDate: Long) =
        remoteDataSource.getAge(token = TokenHolder.getAccessToken(pre), birthDate = birthDate)


    suspend fun checkRetirementStatus() =
        remoteDataSource.checkRetirementStatus(TokenHolder.getAccessToken(pre))

    suspend fun getAuthenticationCode() =
        remoteDataSource.getAuthenticationCode(TokenHolder.getAccessToken(pre))

    suspend fun getUserAge(birthDate: Long?) =
        remoteDataSource.getUserAge(token = TokenHolder.getAccessToken(pre), birthDate = birthDate)

    suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long) =
        remoteDataSource.authenticationAndGetPersonalInfo(
            token = TokenHolder.getAccessToken(pre),
            authenticationsCode
        )

    suspend fun getRetirementRequestInfo(map: HashMap<String, String>) =
        remoteDataSource.getRetirementRequestInfo(
            token = TokenHolder.getAccessToken(pre),
            filter = createFilterWithEQOperator(map)
        )

    suspend fun confirmIdentityAndHistoryInfo(
        authenticationsCode: Long,
        body: ConfirmIdentityAndHistoryInfoRequest,
    ) = remoteDataSource.confirmIdentityAndHistoryInfo(
        token = TokenHolder.getAccessToken(pre),
        authenticationsCode = authenticationsCode,
        body = body
    )

    suspend fun sendRetirementDocument(
        requestId: String,
        body: RetirementSaveDocumentRequest
    ) = remoteDataSource.sendRetirementDocument(
        token = TokenHolder.getAccessToken(pre),
        requestId = requestId,
        body = body
    )

    suspend fun getLetterAttachedImage(guid: String): DownloadFileResponse {
        val result = remoteDataSource.getDocument(TokenHolder.getAccessToken(pre), guid)
        if (result.isSuccess) {
            val imageBytes = Base64.decode(result.data.toString(), Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val imageUri = ImageUtils.saveImageExternal(decodedImage, context)
            result.uri = imageUri
        }
        return result
    }

    suspend fun getDeferredInstallmentInfo(requestId: String) =
        remoteDataSource.getDeferredInstallmentInfo(
            token = TokenHolder.getAccessToken(pre),
            requestId = requestId
        )

    suspend fun getPrescriptionPdfFile(prescriptionID: String) =
        remoteDataSource.getPrescriptionPdfFile(
            token = TokenHolder.getAccessToken(pre),
            prescriptionID = prescriptionID
        )

    suspend fun getConstructionFiles(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getConstructionFiles(
            token = TokenHolder.getAccessToken(pre),
            paramsMap = paramsMap
        )

    suspend fun getDetailConstructionFile(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getDetailConstructionFile(
            token = TokenHolder.getAccessToken(pre),
            paramsMap = paramsMap
        )

    suspend fun getBeneficiariesWorkshop(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getBeneficiariesWorkshop(
            token = TokenHolder.getAccessToken(pre),
            paramsMap = paramsMap
        )

    suspend fun getPaymentSheetConstructionInfo(debitNumber: String) =
        remoteDataSource.getPaymentSheetConstructionInfo(
            token = TokenHolder.getAccessToken(pre),
            debitNumber = debitNumber
        )

    suspend fun getCertificatePaymentSheetPDF(debitNumber: String, branchCode: String) =
        remoteDataSource.getCertificatePaymentSheetPDF(
            token = TokenHolder.getAccessToken(pre),
            debitNumber = debitNumber,
            branchCode = branchCode
        )

    suspend fun issuancePaymentSheet(debitNumber: String) =
        remoteDataSource.issuancePaymentSheet(
            debitNumber = debitNumber,
            token = TokenHolder.getAccessToken(pre)
        )

    suspend fun getInstallmentLetterList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getInstallmentLetterList(
            token = TokenHolder.getAccessToken(pre),
            paramsMap = paramsMap
        )

    suspend fun getDetailDebitList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getDetailDebitList(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getInstallmentConstructionList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getInstallmentConstructionList(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun getFollowUpResultObjectionNonExistsHistory(referenceId: String) =
        remoteDataSource.getFollowUpResultObjectionNonExistsHistory(
            token = TokenHolder.getAccessToken(pre),
            referenceId = referenceId
        )
    //   suspend fun updateInstallmentConstruction(oldDebitNumber:String)= remoteDataSource.updateInstallmentConstruction(token = TokenHolder.getAccessToken(pre), oldDebitNumber = oldDebitNumber)

    suspend fun getPaymentDebitList(debtNumber: String, branchCode: String) =
        remoteDataSource.getPaymentDebitList(
            token = TokenHolder.getAccessToken(pre),
            debtNumber = debtNumber,
            branchCode = branchCode
        )

    suspend fun getPaymentDebitToken(debtNumber: String) =
        remoteDataSource.getPaymentDebitToken(
            token = TokenHolder.getAccessToken(pre),
            debtNumber = debtNumber
        )

    suspend fun getDebtPaidList(debtSerialNumber: String) = remoteDataSource.getDebtPaidList(
        token = TokenHolder.getAccessToken(pre),
        debtSerialNumber = debtSerialNumber
    )

    suspend fun checkPaymentDebt(debtSerialNumber: String) = remoteDataSource.checkPaymentDebt(
        token = TokenHolder.getAccessToken(pre),
        debtSerialNumber = debtSerialNumber
    )

    suspend fun getRegisteredMedicalCommission(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getRegisteredMedicalCommission(token = TokenHolder.getAccessToken(pre))

    suspend fun getInsuredRelation(nationalCode: String?) =
        remoteDataSource.getInsuredRelation(TokenHolder.getAccessToken(pre), nationalCode)

    suspend fun getAllWorkshops(nationalCode: String?) =
        remoteDataSource.getAllWorkshops(TokenHolder.getAccessToken(pre), nationalCode)

    suspend fun getAllWorkshopHistory(
        workshopCode: String?,
        nationalCode: String?,
        branchCode: String?,
        insuranceNumber: String?,
        occurrenceDate: String?
    ) = remoteDataSource.getAllWorkshopHistory(
        TokenHolder.getAccessToken(pre),
        workshopCode,
        nationalCode,
        branchCode,
        insuranceNumber,
        occurrenceDate
    )

    suspend fun getWorkshopSpecification(workshopCode: String?, branchCode: String?) =
        remoteDataSource.getWorkshopSpecification(
            TokenHolder.getAccessToken(pre),
            workshopCode,
            branchCode
        )

    suspend fun getOfficePersonalInfo(
        nationalCode: String?,
        birthDate: Long?,
        workshopCode: String?,
        branchCode: String?
    ) = remoteDataSource.getOfficePersonalInfo(
        TokenHolder.getAccessToken(pre),
        nationalCode,
        birthDate,
        workshopCode,
        branchCode
    )

    suspend fun getDocumentType(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getDocumentType(TokenHolder.getAccessToken(pre), paramsMap)

    suspend fun uploadOccurrenceImage(image: MultipartBody.Part) =
        remoteDataSource.uploadOccurrenceImage(TokenHolder.getAccessToken(pre), image)

    suspend fun sendOccurrenceRequest(request: OccurrenceReq) =
        remoteDataSource.sendOccurrenceRequest(TokenHolder.getAccessToken(pre), request)

    suspend fun downloadTestResultPdf(
        patientID: String,
        noteHeadEprescID: String,
        currentUserNationalCode: String
    ) =
        remoteDataSource.downloadTestResultPdf(
            TokenHolder.getAccessToken(pre),
            patientID,
            noteHeadEprescID,
            currentUserNationalCode
        )

    suspend fun getMedicalMissionList(paramsMap: MutableMap<String, String>?) =
        remoteDataSource.getMedicalMissionList(TokenHolder.getAccessToken(pre), paramsMap)


    suspend fun getBookletStatus(): LackEntitlementResponse {
        val result = userRemoteDataSource.getLackEntitlement(
            token = TokenHolder.getAccessToken(pre),
            nationalCode = pre.getUserNationalCode() ?: ""
        )
        return result
    }


}






