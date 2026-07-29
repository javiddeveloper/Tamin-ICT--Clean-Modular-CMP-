package com.tamin.taminhamrah.data.remote.services


import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.BRANCH_ID
import com.tamin.taminhamrah.Constants.DEBIT_NUMBER
import com.tamin.taminhamrah.Constants.FILE_ID
import com.tamin.taminhamrah.Constants.REQUEST_DATE
import com.tamin.taminhamrah.Constants.REQUEST_ID
import com.tamin.taminhamrah.Constants.WORKSHOP_ID
import com.tamin.taminhamrah.data.remote.BaseRemoteDataSource
import com.tamin.taminhamrah.data.remote.models.ai.AiServiceResponse
import com.tamin.taminhamrah.data.remote.models.ai.LawsAiSearchResponse
import com.tamin.taminhamrah.data.remote.models.employer.InsuredDoc
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredUserInfoReq
import com.tamin.taminhamrah.data.remote.models.employer.RegisterLetterRequest
import com.tamin.taminhamrah.data.remote.models.services.AcraConfigResponse
import com.tamin.taminhamrah.data.remote.models.services.AllHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.BankAccountReq
import com.tamin.taminhamrah.data.remote.models.services.BankAccountResponse
import com.tamin.taminhamrah.data.remote.models.services.BodySaveNonExistentHistory
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.ConfirmSurvivorRequest
import com.tamin.taminhamrah.data.remote.models.services.DebitObjection
import com.tamin.taminhamrah.data.remote.models.services.DeferredInstallmentReq
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.InsuranceTypeResponce
import com.tamin.taminhamrah.data.remote.models.services.ObjectionInsuranceHistoryModel
import com.tamin.taminhamrah.data.remote.models.services.ServiceResponseModel
import com.tamin.taminhamrah.data.remote.models.services.ServiceResponseModelNew
import com.tamin.taminhamrah.data.remote.models.services.ShortTermOrthosisReq
import com.tamin.taminhamrah.data.remote.models.services.ShorttremMariageReq
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractByGuardianRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.OptionalContractByGuardian
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.OptionalContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.UpdateAddressInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.cancelContract.CancelContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateGuardianContract
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateGuardianOptionalContract
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateOptionalContract
import com.tamin.taminhamrah.data.remote.models.services.construction.WorkersPayDebitRequest
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.BeneficiariesConstructionResponse
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.ConstructionFileResponse
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.DetailConstructionInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilityFinalConfirmRequest
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilitySaveDocumentRequest
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilitySaveInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.insuranceRegistration.RegistrationReq
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.InspectionPerformedResponse
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit.SubmitResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.AllWorkshopsResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.InsuredRelationResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceReq
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OfficePersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.WorkshopSpecificationResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentRequest
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentUrlRequest
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.RequestModel
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SaveSurvivorInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.FuneralAllowanceRequest
import com.tamin.taminhamrah.data.remote.models.services.requestPaymentForillDay.RequestPaymentForillDayReq
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.CurrentUserResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.LatestInsuranceInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.RequestForPregnancyPayReq
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.ConfirmIdentityAndHistoryInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementSaveDocumentRequest
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.RequestAddDependent
import com.tamin.taminhamrah.data.remote.models.services.violations.ViolationRequest
import com.tamin.taminhamrah.data.remote.models.services.workshop.ConfirmConflictResponseItem
import com.tamin.taminhamrah.data.remote.models.services.workshop.InstallmentListResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebtResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.RegisterArticle16RequestModel
import com.tamin.taminhamrah.data.remote.models.user.ContractListResponse
import com.tamin.taminhamrah.ui.home.services.contracts.model.FractionRequestDataModel
import com.tamin.taminhamrah.ui.home.services.employer.completeInfo.model.TicketRequest
import com.tamin.taminhamrah.ui.home.services.employer.completeInfo.model.VerifyCodeRequest
import com.tamin.taminhamrah.ui.home.services.employer.contract.model.MafasaHesabRequestModel
import com.tamin.taminhamrah.ui.home.services.employer.debt.InstallmentRequestModel
import com.tamin.taminhamrah.ui.home.services.employer.legalStackHolders.model.AgentRequestModel
import com.tamin.taminhamrah.ui.home.services.employer.onlineService.model.AgreementDataModel
import okhttp3.MultipartBody
import javax.inject.Inject

class ServicesRemoteDataSourceImpl @Inject constructor(private val service: ServicesService) :
    ServicesRemoteDataSource,
    BaseRemoteDataSource() {

    private fun getRequestPage(paramsMap: MutableMap<String, String>?): String {
        return paramsMap?.get(Constants.PAGE) ?: Constants.DEFAULT_START_INDEX
    }

    private fun getQueryPageSize(paramsMap: MutableMap<String, String>?): String {
        return paramsMap?.get(Constants.QUERY_PAGE_SIZE) ?: Constants.DEFAULT_QUERY_PAGE_SIZE
    }

    private fun getStartIndex(paramsMap: MutableMap<String, String>?): String {
        return paramsMap?.get(Constants.START) ?: Constants.DEFAULT_START_INDEX
    }

    private fun getArrayList(paramsMap: MutableMap<String, String>?): String {
        return paramsMap?.get(Constants.ARRAY_KEY_FOR_MAP) ?: ""
    }

    private fun getSort(paramsMap: MutableMap<String, String>?) =
        paramsMap?.get(Constants.SORT_KEY_FOR_MAP) ?: ""

    override suspend fun getAiLawsSearch(url: String, prompt: String,userName: String?): LawsAiSearchResponse =
        service.getAiLawsSearch(url = url, prompt = prompt,userName)

    override suspend fun getVoiceAiLawsSearch(
        url: String,
        file: MultipartBody.Part,
        userName: String?,
    ): LawsAiSearchResponse =
        service.getVoiceAiLawsSearch(url,file,userName)

    override suspend fun getAiServiceSearch(
        url: String,
       data: String,
    ): AiServiceResponse =
        service.getAiServiceSearch(url = url,data = data)

    override suspend fun getVoiceAiServiceSearch(
        url: String,
        file: MultipartBody.Part,
        data: String,
    ): AiServiceResponse =  service.getVoiceAiServiceSearch(url,file,data)



    override suspend fun getPensionInquiry(token: String) =
        getResultList { service.getPensionInquiry(token) }

    override suspend fun getRecipientList(token: String, paramsMap: MutableMap<String, String>?) =
        getResultNew({
            service.getRecipientList(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap),
                getArrayList(paramsMap)
            )
        })

    override suspend fun getWeddingPresent(token: String) =
        getResultNew({ service.getWeddingPresent(token) })


    override suspend fun getServices(token: String): ServiceResponseModel =
        getResultNew({ service.getServices(token) })

    override suspend fun getServices(): ServiceResponseModelNew =
//        getResultNew({ service.getDirectServices("https://eservices-test.tamin.ir/mobile/menu_35.txt") })
        getResultNew({ service.getDirectServices("https://ssodcfs.tamin.ir/eservices/menu_data_${BuildConfig.VERSION_CODE}.txt") })

    override suspend fun getAcraConfig(): AcraConfigResponse =
        getResultNew({ service.getAcraConfig("https://ssodcfs.tamin.ir/eservices/acra_config.txt") })


    override suspend fun validateMarriageGift(token: String, date: String, nationalCode: String) =
        getResult { service.validateMarriageGift(token, date, nationalCode) }

    override suspend fun marriageGiftRequest(
        token: String,
        value: ShorttremMariageReq,
    ) = getResultNew({ service.marriageGiftRequest(token, value) })

    override suspend fun getInsuranceActiveRelation(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            service.getInsuranceActiveRelation(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })

    override suspend fun getReceiverList(token: String, paramsMap: MutableMap<String, String>?) =
        getResultNew({
            service.getInsuranceActiveRelation(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })


    override suspend fun getIdentityInfo(token: String) =
        getResultNew({ service.getIdentityInfo(token) })

    override suspend fun getUserInfo(token: String) =
        getResultNew({ service.getUserInfo(token) })

    override suspend fun sendBankAccountInfo(
        token: String,
        accountNumberStr: String,
        accountTypeId: String,
        bankId: String,
        startDate: String,
    ): GeneralRes {

        val req =
            BankAccountReq(accountNumberStr, bankId, accountTypeId, startDate)
        return getResultNew({ service.sendBankAccountInfo(token, req) })
    }

    override suspend fun getBankAccountList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ): BankAccountResponse {

        return getResultNew({
            service.getBankAccountList(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })
    }

    override suspend fun getAllHistoryInsurance(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ): AllHistoryResponse {
        return getResultNew({
            service.getAllHistoryInsurance(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })
    }

    override suspend fun getResultOfInquirePension(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getResultOfInquirePension(
            token,
            getRequestPage(paramsMap),
            getStartIndex(paramsMap),
            getQueryPageSize(paramsMap)
        )
    })

    override suspend fun getDependantsResponse(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            service.getDependantsResponse(
                token, getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })

    override suspend fun getConfirmationMedicalAuthorities(token: String) =
        getResultNew({ service.getConfirmationMedicalAuthorities(token) })

    override suspend fun getTreatmentCosts(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getTreatmentCosts(
            token,
            getRequestPage(paramsMap),
            getStartIndex(paramsMap),
            getQueryPageSize(paramsMap)
        )
    })

    override suspend fun checkRedCrossStatus(token: String) =
        getResultNew({ service.checkRedCrossStatus(token) })

    override suspend fun checkMedicalStudent(token: String) =
        getResultNew({ service.checkMedicalStudent(token) })

    override suspend fun getMyReportsOV(
        token: String,
        paramsMap: MutableMap<String, String>?
    ) = getResultNew({
        service.getMyReportsOV(
            token,
            getRequestPage(paramsMap),
            getStartIndex(paramsMap),
            getQueryPageSize(paramsMap)
        )
    })

    override suspend fun getProvinceListForOV(
        token: String,
        paramsMap: MutableMap<String, String>?
    ) = getResultNew({
        service.getProvinceListForOV(
            token,
            getRequestPage(paramsMap),
            getStartIndex(paramsMap),
            getQueryPageSize(paramsMap)
        )
    })

    override suspend fun sendReportOV(token: String, body: ViolationRequest) = getResultNew({
        service.sendReportOV(token, body)
    })

    override suspend fun uploadDocumentOV(token: String, image: MultipartBody.Part) = getResultNew({
        service.uploadDocumentOV(token, image)
    })

    override suspend fun getAllDocumentOV(
        token: String,
        id: Int,
        paramsMap: MutableMap<String, String>?
    ) = getResultNew({
        service.getAllDocumentOV(
            token, id, getRequestPage(paramsMap),
            getStartIndex(paramsMap),
            getQueryPageSize(paramsMap)
        )
    })

    override suspend fun getListInspectionPerformed(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ): InspectionPerformedResponse {
        return getResultNew({
            service.getListInspectionPerformed(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })
    }


    override suspend fun getWorkshopListInspectionPerformed(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ): InspectionPerformedResponse {
        return getResultNew({
            service.getWorkshopListInspectionPerformed(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })
    }

    override suspend fun getWorkersPaymentInfo(token: String) =
        getResultNew({ service.getWorkersPaymentInfo(token = token) })

    // inspectTicket : check pay result
    override suspend fun inspectTicket(token: String, ticket: String?, paymentInfo: String?) =
        getResultNew({ service.inspectTicket(token, ticket, paymentInfo) })

    override suspend fun getWorkersPayDebit(
        token: String,
        body: WorkersPayDebitRequest,
        type: String
    ) = getResultNew({ service.getWorkersPayDebit(token =token, body = body/*, type*/, url ="mytamin://workers_payment_callback" ) })

    override suspend fun getWageAndHistoryInsurance(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ): WageAndHistoryResponse =
        getResultNew({
            service.getWageAndHistoryInsurance(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })


    override suspend fun getObjectionInsuranceHistory(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            service.getObjectionInsuranceHistory(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })

    override suspend fun getProvinceList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getProvinceList(
            token = token,
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun getCitiesOfProvince(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getCitiesOfProvince(
            token = token,
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun getContractInsuranceList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ): ContractListResponse {
        return getResultNew({
            service.getContractInsuranceList(
                token = token,
                page = getRequestPage(paramsMap),
                start = getStartIndex(paramsMap),
                limit = getQueryPageSize(paramsMap),
                filter = getArrayList(paramsMap),
                sort = getSort(paramsMap)
            )
        })
    }

    override suspend fun getFreelancerJobTitle(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getFreelancerJobTitle(
            token = token,
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun getDetailPaymentFreelance(
        token: String,
        contractNumber: String,
        debitNumber: String,
    ) = getResultNew({
        service.getDetailPaymentFreelance(
            token = token,
            contractNumber = contractNumber,
            debitNumber = debitNumber
        )
    })


    override suspend fun getInfoBranch(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getInfoBranch(
            token = token,
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun getCityNameWithPaging(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getCityName(
            token = token,
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun getCityName(token: String, filter: String) =
        getResultNew({ service.getCityName(token = token, filter = filter) })

    override suspend fun getBranchDetailList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getBranchDetailList(
            token = token,
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun getInsuranceTypeList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getInsuranceTypeList(
            token = token,
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun getInspectionInfo(
        token: String,
        insuranceId: String?,
        inspectionCode: String,
        nationalCode: String,
    ) = getResultNew({
        service.getInspectionInfo(
            token = token,
            insuranceId = insuranceId,
            inspectionCode = inspectionCode,
            nationalCode = nationalCode
        )
    })

    override suspend fun getPensionerId(token: String) =
        getResultNew({ service.getPensionerId(token) })

    override suspend fun getPensionerPayRoll(filter: String, token: String) =
        getResultNew({ service.getPensionerPayRoll(token, filter) })

    // override suspend fun downloadAllHistoryPDF(token: String) = getResult{service.DownloadAllHistoryPDF(token)}

    override suspend fun downloadAllHistoryPDF(token: String) =
        getPdfResult { service.downloadAllHistoryPDF(token) }

    override suspend fun downloadWageAndHistoryPDF(token: String) =
        getPdfResult { service.downloadWageAndHistoryPDF(token) }

    override suspend fun sendPayRollToInbox(filter: String, token: String) =
        getResultNew({ service.sendPayRollToInbox(token, filter) })

    override suspend fun sendToInboxTreatmentCosts(token: String, repId: String) =
        getResultNew({ service.sendToInboxTreatmentCosts(token, repId) })

    override suspend fun downloadEdictPdf(filter: String, token: String) =
        getPdfResult { service.downloadEdictPDF(token, filter) }

    override suspend fun getTitlesJob(token: String, paramsMap: MutableMap<String, String>?) =
        getResultNew({
            service.getTitlesJob(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })

    override suspend fun downloadTalfighiPdf(token: String) =
        getPdfResult { service.downloadTalfighiPdf(token) }

    override suspend fun getViewShorttermRequestList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getViewShorttermRequest(
            token,
            getRequestPage(paramsMap),
            getStartIndex(paramsMap),
            getQueryPageSize(paramsMap)
        )
    })

    override suspend fun sendCertificateRequest(filter: String, token: String) =
        getResultNew({ service.sendCertificateRequest(token, filter) })

    override suspend fun sendRequestInquirePensionCertificate(filter: String, token: String) =
        getResultNew({ service.sendRequestInquirePensionCertificate(token, filter) })

    override suspend fun sendEdictPensionerToMyInbox(filter: String, token: String) =
        getResultNew({ service.sendEdictPensionerToMyInbox(token, filter) })

    override suspend fun sendCertificateWage(filter: String, token: String) =
        getResultNew({ service.sendCertificateWage(token, filter) })

    override suspend fun getEdictPensioner(filter: String, token: String) =
        getResultNew({ service.getEdictPensioner(token, filter) })

    override suspend fun calculateMarriageAllowance(token: String, timeStamp: String) =
        getResultNew({ service.calculateMarriage(token, timeStamp) })

    override suspend fun calculateWageIllDay(
        token: String,
        StartDateTimeStamp: String,
        EndDateTimeStamp: String,
        marital_status: String,
    ) = getResult {
        service.calculateWageIllDay(
            token,
            StartDateTimeStamp,
            EndDateTimeStamp,
            marital_status
        )
    }

    override suspend fun calculateWagePregnancyDays(
        token: String,
        StartDateTimeStamp: String,
        EndDateTimeStamp: String,
    ) =
        getResult {
            service.calculateWagePregnancyDays(
                token,
                StartDateTimeStamp,
                EndDateTimeStamp
            )
        }

    override suspend fun getPersonalInfo(token: String) =
        getResultNew({ service.getPersonalInfo(token) })

    override suspend fun getPersonalInfoDetail(token: String) =
        getResultNew({ service.getPersonalInfoDetail(token) })

    override suspend fun checkGirlSurvivorConditions(
        nationalCode: String,
        pensionerId: String,
        token: String,
    ) = getResultNew({
        service.checkGirlSurvivorConditions(
            token,
            nationalCode,
            "04",
            pensionerId
        )
    })

    override suspend fun getGirlSurvivorReport(
        token: String,
        address: String,
        phone: String,
        zipCode: String,
        fatherName: String,
        birthDate: Long,
        insuranceNumber: String,
        nationalCode: String,
        pensionId: String,
    ) = getPdfResult {
        service.getGirlSurvivorReport(
            token,
            address,
            phone,
            zipCode,
            fatherName,
            birthDate,
            insuranceNumber,
            nationalCode,
            pensionId
        )
    }

    override suspend fun confirmGirlSurvivor(
        request: ConfirmSurvivorRequest,
        token: String,
    ) = getResultNew({ service.confirmGirlSurvivor(token, request) })


    override suspend fun getBeneficiary(
        token: String, paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getBeneficiary(
            token,
            getRequestPage(paramsMap),
            getStartIndex(paramsMap),
            getQueryPageSize(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun getDeservedTreatment(token: String) =
        getResultNew({ service.getDeservedTreatment(token) })

    override suspend fun sendRequestDeferredInstallmentCertificate(
        token: String,
        deferredInstallmentRequest: DeferredInstallmentReq,
    ) = getResultNew({
        service.sendRequestDeferredInstallmentCertificate(
            token,
            deferredInstallmentRequest
        )
    })


    override suspend fun sendInsuranceHistoryToInstitution(
        token: String,
        allHistorySelected: Boolean,
        historyAndWageSelected: Boolean,
        combineHistorySelected: Boolean,
    ) = getResultNew({
        service.sendInsuranceHistoryToInstitution(
            token,
            allHistorySelected,
            historyAndWageSelected,
            combineHistorySelected
        )
    })
    override suspend fun sendAllInsuranceHistoryToInstitution(
        token: String,
    ) = getResultNew({
        service.sendAllInsuranceHistoryToInstitution(
            token,)
    })

    override suspend fun isMultipleWorkshops(
        token: String,
        branchCode: String,
        insuranceNumber: String,
    ) = getResultNew({ (service.isMultipleWorkshops(token, branchCode, insuranceNumber)) })

    override suspend fun calculateMultipleWorkshops(
        token: String,
        branchCode: String,
        insuranceNumber: String,
    ) = getResultNew({ (service.calculateMultipleWorkshops(token, branchCode, insuranceNumber)) })

    override suspend fun getLastRelation(token: String) =
        getResultNew({ service.getLastRelation(token) })

    override suspend fun getElectronicPrescriptionDetail(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({

        val noteHeadID = paramsMap?.get("noteHeadID") ?: ""
        val type = paramsMap?.get("type") ?: ""
        val nationalCode = paramsMap?.get("nationalCode") ?: ""
        val childNationalCode = paramsMap?.get("childNationalCode") ?: "0"
        val flagSata = paramsMap?.get("flagSata") ?: "null"

        service.getElectronicPrescriptionDetail(
            token = token,
            noteHeadID = noteHeadID,
            type = type,
            nationalCode = nationalCode,
            childNationalCode = if (nationalCode == childNationalCode) "0" else childNationalCode,
            flagSata = if (flagSata.isEmpty()) "null" else flagSata,
            requestPage = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            queryPageSize = getQueryPageSize(paramsMap)
        )
    })

    override suspend fun getElectronicPrescriptionPrice(
        token: String,
        noteHeadID: String,
        nationalCode: String,
    ) = getResultNew({ service.getElectronicPrescriptionPrice(token, noteHeadID, nationalCode) })

    override suspend fun getDependantUserUnder18(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        val nationalCode = paramsMap?.get("nationalCode") ?: ""
        service.getDependantUnder18(token, nationalCode)
    })

    override suspend fun getWorkshopInfo(token: String, paramsMap: MutableMap<String, String>?) =
        getResultNew({

            val array = JsonArray()
            if (paramsMap?.containsKey("workshopId") == true) {
                val jsonObj1 = JsonObject()
                jsonObj1.addProperty("property", "workshopId")
                jsonObj1.addProperty("value", paramsMap["workshopId"])
                jsonObj1.addProperty("operator", "EQ")
                array.add(jsonObj1)
            }

            if (paramsMap?.containsKey("branchCode") == true) {
                val jsonObj2 = JsonObject()
                jsonObj2.addProperty("property", "branchCode")
                jsonObj2.addProperty("value", paramsMap["branchCode"])
                jsonObj2.addProperty("operator", "EQ")
                array.add(jsonObj2)
            }

            val filter = array.toString()

            service.getWorkshopInfo(
                token,
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
                filter = filter
            )
        })

    override suspend fun getWorkshopMemberList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({


            val array = JsonArray()
            if (paramsMap?.containsKey("workshop.workshopId") == true) {
                val jsonObj1 = JsonObject()
                jsonObj1.addProperty("property", "workshop.workshopId")
                jsonObj1.addProperty("value", paramsMap["workshop.workshopId"])
                jsonObj1.addProperty("operator", "EQ")
                array.add(jsonObj1)
            }
            if (paramsMap?.containsKey("workshop.branchCode") == true) {
                val jsonObj2 = JsonObject()
                jsonObj2.addProperty("property", "workshop.branchCode")
                jsonObj2.addProperty("value", paramsMap["workshop.branchCode"])
                jsonObj2.addProperty("operator", "EQ")
                array.add(jsonObj2)
            }
            if (paramsMap?.containsKey("insurance.id") == true) {
                val jsonObj2 = JsonObject()
                jsonObj2.addProperty("property", "insurance.id")
                jsonObj2.addProperty("value", paramsMap["insurance.id"])
                jsonObj2.addProperty("operator", "EQ")
                array.add(jsonObj2)
            }
            if (paramsMap?.containsKey("insurance.nationalId") == true) {
                val jsonObj2 = JsonObject()
                jsonObj2.addProperty("property", "insurance.nationalId")
                jsonObj2.addProperty("value", paramsMap["insurance.nationalId"])
                jsonObj2.addProperty("operator", "EQ")
                array.add(jsonObj2)
            }

            service.getWorkshopMembers(
                token,
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
                filter = array.toString()
            )
        })

    override suspend fun getWorkshopStackHolderList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({

            val array = JsonArray()
            if (paramsMap?.containsKey("workshopId.workshopId") == true) {
                val jsonObj1 = JsonObject()
                jsonObj1.addProperty("property", "workshopId.workshopId")
                jsonObj1.addProperty("value", paramsMap["workshopId.workshopId"])
                jsonObj1.addProperty("operator", "EQ")
                array.add(jsonObj1)
            }
            if (paramsMap?.containsKey("workshopId.branchCode") == true) {
                val jsonObj2 = JsonObject()
                jsonObj2.addProperty("property", "workshopId.branchCode")
                jsonObj2.addProperty("value", paramsMap["workshopId.branchCode"])
                jsonObj2.addProperty("operator", "EQ")
                array.add(jsonObj2)
            }
            if (paramsMap?.containsKey("workshopId.id") == true) {
                val jsonObj2 = JsonObject()
                jsonObj2.addProperty("property", "workshopId.id")
                jsonObj2.addProperty("value", paramsMap["workshopId.id"])
                jsonObj2.addProperty("operator", "EQ")
                array.add(jsonObj2)
            }
            if (paramsMap?.containsKey("workshopId.nationalId") == true) {
                val jsonObj2 = JsonObject()
                jsonObj2.addProperty("property", "workshopId.nationalId")
                jsonObj2.addProperty("value", paramsMap["workshopId.nationalId"])
                jsonObj2.addProperty("operator", "EQ")
                array.add(jsonObj2)
            }

            service.getWorkshopStackHolders(
                token,
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
                filter = array.toString()
            )
        })

    override suspend fun getWorkshopDebtList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getWorkshopDebtList(
            token,
            workshopId = paramsMap?.get("workshopId") ?: "",
            branchCode = paramsMap?.get("branchCode") ?: "",
            page = getRequestPage(paramsMap),
            limit = getQueryPageSize(paramsMap),
            start = getStartIndex(paramsMap),
        )
    })

    override suspend fun getWorkshopDebtInquiry(
        token: String,
        workshopId: String,
        branchCode: String,
    ) = getResultNew({ service.getWorkshopDebtInquiry(token, workshopId, branchCode) })

    override suspend fun getInsuredOrthosisInfo(token: String) =
        getResultNew({ service.getInsuredOrthosisInfo(token) })


    override suspend fun getElectronicPrescriptionList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        val requestTypeId = paramsMap?.get("requestTypeId") ?: ""
        val nationalCode = paramsMap?.get("nationalCode") ?: ""
        val dependantUserNationalCode = paramsMap?.get("dependantUserNationalCode") ?: ""
        val startDate = paramsMap?.get("startDate") ?: ""
        val endDate = paramsMap?.get("endDate") ?: ""
        val requestPage = getRequestPage(paramsMap)
        val start = getStartIndex(paramsMap)
        val queryPageSize = getQueryPageSize(paramsMap)

        service.getElectronicPrescriptionList(
            token,
            requestTypeId,
            nationalCode,
            dependantUserNationalCode,
            startDate,
            endDate,
            requestPage,
            start,
            queryPageSize
        )
    })

    override suspend fun getCombinedRecordList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            service.getCombinedRecordList(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })


    override suspend fun sendRequestForPregnancyPay(
        token: String,
        requestForPregnancyPayReq: RequestForPregnancyPayReq,
    ) = getResultNew({ service.sendRequestForPregnancyPay(token, requestForPregnancyPayReq) })

    override suspend fun uploadImage(token: String, image: MultipartBody.Part) =
        getResultNew({ service.uploadImage(token, image) }, false)

    override suspend fun uploadPdfFile(token: String, image: MultipartBody.Part) =
        getResultNew({ service.uploadPdfFile(token, image) }, false)

    override suspend fun getCurrentUser(token: String): CurrentUserResponse =
        getResultNew({ service.getCurrentUser(token) })


    override suspend fun getPregnancyStatus(token: String) =
        getResultNew({ service.getPregnancyStatus(token) })


    override suspend fun getPregnancyType(token: String) =
        getResultNew({ service.getPregnancyType(token) })


    override suspend fun getLatestInsuranceInfo(token: String): LatestInsuranceInfoResponse =
        getResultNew({ service.getLatestInsuranceInfo(token) })


    override suspend fun getWorkshopDemandDocs(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getWorkshopDebtDocumentList(
            token,
            paramsMap?.get("debtNumber") ?: "",
            paramsMap?.get("branchCode") ?: "",
            getRequestPage(paramsMap),
            getStartIndex(paramsMap),
            getQueryPageSize(paramsMap)
        )
    })

    override suspend fun downloadDebtDocumentPdf(
        token: String,
        debtNumber: String,
        branchCode: String,
    ) = getPdfResult { service.downloadDebtDocumentPdf(token, debtNumber, branchCode) }

    override suspend fun saveShortTermOrthosis(
        token: String,
        requestShortTermOrthosis: ShortTermOrthosisReq,
    ) = getResultNew({ service.saveShorttermOrthosis(token, requestShortTermOrthosis) })

    override suspend fun getWorkshopDebitReasonList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({

        service.getDebitReasonList(
            token = token,
            page = getRequestPage(paramsMap),
            limit = getQueryPageSize(paramsMap),
            start = getStartIndex(paramsMap)
        )
    })


    override suspend fun pensionerPayRollPDF(token: String, filter: String) =
        getPdfResult {
            service.pensionerPayRollPDF(token, filter)
        }

    override suspend fun checkInsuredInfo(
        token: String,
    ): CheckInsuredInfoResponse = getResultNew({
        service.checkInsuredInfo(token)
    })

    override suspend fun getWorkshopPaymentSheets(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({

        val array = JsonArray()

        if (paramsMap?.containsKey("workshopId") == true) {
            val jsonObj1 = JsonObject()
            jsonObj1.addProperty("property", "workshopId")
            jsonObj1.addProperty("value", paramsMap["workshopId"])
            jsonObj1.addProperty("operator", "EQ")
            array.add(jsonObj1)
        }

        if (paramsMap?.containsKey("branchCode") == true) {
            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "branchCode")
            jsonObj2.addProperty("value", paramsMap["branchCode"])
            jsonObj2.addProperty("operator", "EQ")
            array.add(jsonObj2)
        }

        if (paramsMap?.containsKey("payIdFrom") == true) {
            val jsonObj3 = JsonObject()
            jsonObj3.addProperty("property", "payIdFrom")
            jsonObj3.addProperty("value", paramsMap["payIdFrom"])
            jsonObj3.addProperty("operator", "EQ")
            array.add(jsonObj3)
        }
        if (paramsMap?.containsKey("payIdTo") == true) {
            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "payIdTo")
            jsonObj2.addProperty("value", paramsMap["payIdTo"])
            jsonObj2.addProperty("operator", "EQ")
            array.add(jsonObj2)
        }
        if (paramsMap?.containsKey("docDateFrom") == true) {
            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "docDateFrom")
            jsonObj2.addProperty("value", paramsMap["docDateFrom"])
            jsonObj2.addProperty("operator", "EQ")
            array.add(jsonObj2)
        }
        if (paramsMap?.containsKey("docDateTo") == true) {
            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "docDateTo")
            jsonObj2.addProperty("value", paramsMap["docDateTo"])
            jsonObj2.addProperty("operator", "EQ")
            array.add(jsonObj2)
        }
        if (paramsMap?.containsKey("debitReason") == true) {
            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "debitReason")
            jsonObj2.addProperty("value", paramsMap["debitReason"])
            jsonObj2.addProperty("operator", "EQ")
            array.add(jsonObj2)
        }
        if (paramsMap?.containsKey("paymentSheetStatus") == true) {
            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "paymentSheetStatus")
            jsonObj2.addProperty("value", paramsMap["paymentSheetStatus"])
            jsonObj2.addProperty("operator", "EQ")
            array.add(jsonObj2)
        }

        val filter = array.toString()

        service.getWorkshopPaymentSheets(
            token,
            page = getRequestPage(paramsMap),
            limit = getQueryPageSize(paramsMap),
            start = getStartIndex(paramsMap),
            filter = filter
        )
    })


    override suspend fun sendObjectionInsuranceHistory(
        token: String,
        list: List<ObjectionInsuranceHistoryModel>,
    ) = getResultNew({ service.sendObjectionInsuranceHistory(token, list) })

    override suspend fun sendConfirmConflict(
        token: String,
        list: List<ConfirmConflictResponseItem>,
    ) = getResultNew({ service.sendConfirmConflict(token, list) })

    override suspend fun sendConfirmNotExist(
        token: String,
        list: List<ConfirmConflictResponseItem>,
    ) = getResultNew({ service.sendConfirmNotExist(token, list) })

    override suspend fun getActiveBranch(token: String) =
        getResultNew({ service.getActiveBranch(token) })

    override suspend fun sendRequestForIllDay(
        token: String,
        illDayReq: RequestPaymentForillDayReq,
    ) = getResultNew({ service.sendRequestForIllDay(token, illDayReq) })

    override suspend fun getInfoFuneral(token: String) =
        getResultNew({ service.getInfoFuneral(token) })


    override suspend fun checkWorkshopDebitObjectionPermission(
        token: String,
        orderRecipeDate: String,
    ) = getResultNew({
        service.checkWorkshopDebitObjectionPermission(token, orderRecipeDate)
    })

    override suspend fun downloadDebitObjectionPDF(token: String, seqNumber: Long?) =
        getPdfResult { service.downloadDebitObjectionPDF(token, seqNumber) }


    override suspend fun downloadDebitObjectionReportPDF(
        token: String, seqNumber: Long?,
    ) = getPdfResult {
        service.downloadDebitObjectionReportPDF(token, seqNumber)
    }

    override suspend fun downloadContractPdf(
        token: String, timeStamp: String,
    ) = getPdfResult {
        service.downloadContract(token, timeStamp)
    }

    override suspend fun downloadOptionalContractPdf(
        token: String, timeStamp: String,
    ) = getPdfResult {
        service.downloadOptionalContract(token, timeStamp)
    }

    override suspend fun downloadFractionContractPdf(
        token: String, timeStamp: String,
    ) = getPdfResult {
        service.downloadFractionContract(token, timeStamp)
    }

    override suspend fun downloadInstallmentReport(
        token: String, letterNumber: String
    ) = getPdfResult {
        service.downloadInstallmentReport(token, letterNumber)
    }


    override suspend fun getSelectedWorkshopInfo(
        token: String,
        workshopId: String,
        branchCode: String,
    ) = getResult {
        service.getSelectedWorkshopInfo(token, workshopId, branchCode)
    }

    override suspend fun getObjectionTypeList(token: String) =
        getResult { service.getObjectionTypeList(token) }

    override suspend fun getWorkshopObjectionableDebitList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getWorkshopObjectionableDebitList(
            token,
            workshopNumber = paramsMap?.get("workshopId") ?: "",
            branchCode = paramsMap?.get("branchCode") ?: "",
            page = getRequestPage(paramsMap),
            limit = getQueryPageSize(paramsMap),
            start = getStartIndex(paramsMap)
        )
    })

    override suspend fun getAllObjections(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({

        val array = JsonArray()
        if (paramsMap?.containsKey("workshopId") == true) {
            val jsonObj1 = JsonObject()
            jsonObj1.addProperty("property", "workshopId")
            jsonObj1.addProperty("value", paramsMap["workshopId"])
            jsonObj1.addProperty("operator", "EQ")
            array.add(jsonObj1)
        }

        if (paramsMap?.containsKey("seqNo") == true) {
            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "seqNo")
            jsonObj2.addProperty("value", paramsMap["seqNo"])
            jsonObj2.addProperty("operator", "EQ")
            array.add(jsonObj2)
        }
        if (paramsMap?.containsKey("debitNumber") == true) {
            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "debitNumber")
            jsonObj2.addProperty("value", paramsMap["debitNumber"])
            jsonObj2.addProperty("operator", "EQ")
            array.add(jsonObj2)
        }

        val filter = array.toString()



        service.getAllObjections(
            token, page = getRequestPage(paramsMap),
            limit = getQueryPageSize(paramsMap),
            start = getStartIndex(paramsMap),
            filter = filter
        )
    })

    override suspend fun getObjectionsSms(token: String, paramsMap: MutableMap<String, String>?) =
        getResultNew({
            service.getWorkshopObjectionSms(
                token,
                objectionCode = if (paramsMap != null && paramsMap.containsKey("objectionId")) (paramsMap["objectionId"]
                    ?: "") else "",
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap)
            )
        })


    override suspend fun sendReqSaveNotExist(
        token: String,
        sendReq: BodySaveNonExistentHistory,
    ) = getResultNew({ service.sendReqSaveNotExist(token, sendReq) })

    override suspend fun deleteNotExist(
        token: String,
        reqno: String,
        id: String,
    ) = getResultNew({ service.deleteNoTexist(token, reqno = reqno, id = id) })

    override suspend fun sendFinalConfirmConflict(
        token: String,
    ) = getResultNew({ service.sendFinalConfirmConflict(token) })

    override suspend fun sendFinalConfirmNotExist(
        token: String,
    ) = getResultNew({ service.sendFinalConfirmNotExist(token) })

    override suspend fun checkStatusConflict(
        token: String,
    ) = getResultNew({ service.checkStatusConflict(token) })

    override suspend fun checkStatusNotExist(token: String) =
        getResultNew({ service.checkStatusNotExist(token) })

    override suspend fun getDebitPaymentStatus(
        token: String,
        debitNumber: String,
        branchCode: String,
    ) = getResultNew({ service.getDebitPaymentStatus(token, debitNumber, branchCode) })

    override suspend fun normalDebitPayment(
        token: String, paymentRequest: PaymentRequest,
    ) = getResultNew({ service.normalDebitPayment(token, paymentRequest) })

    override suspend fun getPaymentInfo(
        token: String,
        url: String,
    ) = getResultNew({
        service.getPaymentInfo(/*"BIGipServerTFH-POOL=2264928428.33315.0000; path=/; Httponly; Secure",*/
            token,
            url
        )
    })

    override suspend fun cancelPayment(
        token: String,
        url: String,
    ) = getResultNew({
        service.cancelPayment(
            token,
            url
        )
    })

    override suspend fun getPaymentLink(
        token: String,
        url: String,
        body: PaymentUrlRequest
    ) = getResultNew({
        service.getPaymentLink(/*"BIGipServerTFH-POOL=2264928428.33315.0000; path=/; Httponly; Secure",*/
            token = token,
            paymentUrl = url,
            body = body
        )
    })

    override suspend fun getPerformedInspectionList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ) = getResultNew({
        service.getPerformedInspectionList(
            token,
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = ""
        )
    })

    override suspend fun downloadPerformedInspectionPdf(token: String, inspectionNumber: String) =
        getTaminPdf { service.downloadPerformedInspectionPdf(token, inspectionNumber) }

    override suspend fun downloadInspectionPdf(
        token: String,
        inspectionNumber: String,
    ) = getPdfResult {
        service.downloadPerformedInspectionPdf(token, inspectionNumber)
    }


    override suspend fun getBranchDetailListWithBranchCode(
        token: String, paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getBranchDetailListWithBranchCode(
            token = token,
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun getInsuranceTypeListWithInsuracneTypeCode(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ): InsuranceTypeResponce = getResultNew({
        service.getInsuranceTypeListWithInsuracneTypeCode(
            token = token,
            filter = getArrayList(paramsMap)
        )
    })


    override suspend fun getNotExistRequests(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            service.getNotExistRequests(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })

    override suspend fun getCovidResult(token: String) =
        getResultNew({ service.getCovidResult(token) })

    override suspend fun inquiryDeceasedInfo(token: String, nationalCode: String) =
        getResultNew({ service.inquiryDeceasedInfo(token, nationalCode) })

    override suspend fun submitRequestFuneralAllowance(
        token: String,
        funeralGrantReq: FuneralAllowanceRequest,
    ) = getResultNew({ service.submitRequestFuneralAllowance(token, funeralGrantReq) })


    override suspend fun correctedAccountNumber(token: String, requestId: String) =
        getResultNew({ service.correctedAccountNumber(token, requestId) })

    override suspend fun sendInspectionRequest(token: String, submitResponse: SubmitResponse) =
        getResultNew({ service.sendInspectionRequest(token, submitResponse) })


    override suspend fun sendDebitObjection(token: String, request: DebitObjection) =
        getResultNew({ service.sendDebitObjection(token, request) })


    override suspend fun getInquiryCertificate(
        token: String,
        nationalId: String,
        inquiryLicenseCode: String,
    ) = getResult { service.getInquiryCertificate(token, nationalId, inquiryLicenseCode) }

    override suspend fun getJobTitle(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getJobTitle(
            token = token,
            page = getRequestPage(paramsMap),
            limit = getQueryPageSize(paramsMap),
            start = getStartIndex(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun getProfileInfo(token: String) =
        getResultNew({ service.getProfileInfo(token) })

    override suspend fun getUserProfileImage(token: String) =
        getResultNew({ service.getUserProfileImage(token) })

    override suspend fun getElectronicFile(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getElectronicFile(
            token = token,
            page = getRequestPage(paramsMap),
            limit = getQueryPageSize(paramsMap),
            start = getStartIndex(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })


    override suspend fun getMyElectronicFileDocumentFullSize(
        token: String,
        url: String,
    ) = getPdfResult {
        service.getMyElectronicFileDocumentFullSize(
            token = token,
            url = url
        )
    }

    override suspend fun getTreatmentCostsPDF(
        token: String,
        repId: String,
    ) = getPdfResult {
        service.getTreatmentCostsPDF(
            token = token,
            repId = repId,
        )
    }


    override suspend fun getContractList(token: String, paramsMap: MutableMap<String, String>?) =
        getResultNew({
            /*  var filter = ""
              val array = JsonArray()
              if (paramsMap?.containsKey("workshopId") == true) {
                  val jsonObj1 = JsonObject()
                  jsonObj1.addProperty("property", "workshopId")
                  jsonObj1.addProperty("value", paramsMap["workshopId"])
                  jsonObj1.addProperty("operator", "EQ")
                  array.add(jsonObj1)
              }

              if (paramsMap?.containsKey("branchCode") == true) {
                  val jsonObj2 = JsonObject()
                  jsonObj2.addProperty("property", "branchCode")
                  jsonObj2.addProperty("value", paramsMap.get("branchCode"))
                  jsonObj2.addProperty("operator", "EQ")
                  array.add(jsonObj2)
              }

              filter = array.toString()*/

            service.getContractList(
                token,
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
                filter = getArrayList(paramsMap),
            )
        })

    override suspend fun getAssignerContractList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            service.getAssignerContractList(
                token,
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
                filter = getArrayList(paramsMap)
            )
        })

    override suspend fun getComputationalBaseList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            service.getComputationalBaseList(
                token,
                workshopId = paramsMap?.get("workshopId"),
                contractRow = paramsMap?.get("contractRow"),
                branchCode = paramsMap?.get("branchCode"),
                contractSequence = paramsMap?.get("contractSequence"),
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap)
            )
        })

    override suspend fun getDocumentImage(token: String, id: String?) =
        getResultNew({ service.getComputationalBaseImage(token, id) })

    override suspend fun downloadComputationalBasePdf(
        token: String,
        documentId: String,
    ) = getTaminPdf { service.downloadComputationalBasePdf(token, documentId) }

    override suspend fun getRegistrationInfo(token: String) =
        getResultNew({ service.getRegistrationInfo(token) })

    override suspend fun calculateFreelanceDebit(token: String, month: Int) =
        getResultNew({ service.calculateFreelanceDebit(token, month) })

    override suspend fun calculateOptionalInsuranceDebitByMonth(token: String, month: Int) =
        getResultNew({ service.calculateOptionalInsuranceDebitByMonth(token, month) })

    override suspend fun saveUsersAddressInfo(
        token: String,
        updateAddressInfoRequest: UpdateAddressInfoRequest,
    ) = getResultNew({ service.saveUsersAddressInfo(token, updateAddressInfoRequest) })


    override suspend fun getCityList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({

        val array = JsonArray()
        if (paramsMap?.containsKey("cityName") == true) {
            val jsonObj1 = JsonObject()
            jsonObj1.addProperty("property", "cityName")
            jsonObj1.addProperty("value", "*${paramsMap["cityName"]}*%")
            jsonObj1.addProperty("operator", "LIKE")
            array.add(jsonObj1)
        }
        val sortArray = JsonArray()
        val jsonObj1 = JsonObject()
        jsonObj1.addProperty("property", "cityCode")
        jsonObj1.addProperty("direction", "ASC")
        sortArray.add(jsonObj1)

        service.getCityList(
            token,
            page = getRequestPage(paramsMap),
            limit = getQueryPageSize(paramsMap),
            start = getStartIndex(paramsMap),
            filter = array.toString(),
            sort = sortArray.toString()
        )
    })

    override suspend fun sendInsuranceRegistration(
        token: String,
        request: RegistrationReq,
    ) = getResultNew({ service.sendInsuranceRegistration(token, request) })

    override suspend fun checkAgeAndHistory(token: String) =
        getResultNew({ service.checkAgeAndHistory(token) })


    override suspend fun checkFractionAgeAndHistory(token: String) =
        getResultNew({ service.checkFractionAgeAndHistory(token) })

    override suspend fun checkOptionalAgeAndHistory(token: String) =
        getResultNew({ service.checkOptionalAgeAndHistory(token) })


    override suspend fun checkContractStatus(token: String) =
        getResultNew({ service.checkContractStatus(token) })

    override suspend fun checkOptionalInsuranceContractStatus(token: String) =
        getResultNew({ service.checkOptionalInsuranceContractStatus(token) })

    override suspend fun checkSuccessPaymentStatus(token: String, systemType: String) =
        getResultNew({ service.checkSuccessPaymentStatus(token, systemType) })

    override suspend fun getFreelanceLastPayment(token: String) =
        getResultNew({ service.getFreelanceLastPayment(token) })

    override suspend fun getOptionalInsuranceLastPayment(token: String) =
        getResultNew({ service.getOptionalInsuranceLastPayment(token) })

    override suspend fun getContractPremiumRate(
        token: String,
        premiumRate: String,
        typePremiumRate: String,
    ) = getResultNew({ service.getPremiumRateForContract(token, premiumRate, typePremiumRate) })

    override suspend fun getOptionalContractPremiumRate(
        token: String,
    ) = getResultNew({ service.getPremiumRateForOptionalContract(token) })

    override suspend fun checkAndCalculateSalaryForContract(
        token: String,
        premiumRate: String,
        typePremiumRate: String,
    ) = getResultNew({
        service.checkAndCalculateSalaryForContract(
            token,
            premiumRate,
            typePremiumRate
        )
    })


    override suspend fun calculateSalaryForOptionalContract(
        token: String,
        premiumRate: String,
    ) = getResultNew({
        service.calculateSalaryForOptionalContract(
            token,
            premiumRate
        )
    })
    /*

       override suspend fun makeContract(
            token: String,
            premiumRate: String,
            finalConfirm: FinalSendRequestCreateContact
        ) = getResultNew({service.makeContract(token,premiumRate,finalConfirm)})
    */

    override suspend fun makeContract(
        token: String,
        selectedSalary: Int,
        finalConfirmRequest: ContractRequest,
    ) = getResultNew({
        service.makeContract(
            token,
            selectedSalary.toString(),
            finalConfirmRequest
        )
    })

    override suspend fun makeFractionContract(
        token: String,
        req: FractionRequestDataModel,
    ) = getResultNew({
        service.makeFractionContract(token, req)
    })


    override suspend fun makeOptionalContract(
        token: String,
        selectedSalary: Int,
        finalConfirmRequest: OptionalContractRequest,
    ) = getResultNew({
        service.makeOptionalContract(
            token,
            selectedSalary.toString(),
            finalConfirmRequest
        )
    })


    override suspend fun makeContractByGuardian(
        token: String,
        selectedSalary: Int,
        finalConfirmRequest: ContractByGuardianRequest,
    ) = getResultNew(
        {
            service.makeContractByGuardian(
                token,
                selectedSalary.toString(),
                finalConfirmRequest
            )
        })

    override suspend fun makeOptionalContractByGuardian(
        token: String,
        selectedSalary: Int,
        finalConfirmRequest: OptionalContractByGuardian,
    ) = getResultNew(
        {
            service.makeOptionalContractByGuardian(
                token,
                selectedSalary.toString(),
                finalConfirmRequest
            )
        })

    override suspend fun updateContract(
        token: String,
        premium: String,
        body: UpdateContractRequest,
    ) = getResultNew({
        service.updateContractRequest(token = token, premium = premium, body = body)
    })

    override suspend fun updateOptionalContract(
        token: String,
        premium: String,
        body: UpdateOptionalContract,
    ) = getResultNew({
        service.updateOptionalContractRequest(token = token, premium = premium, body = body)
    })

    override suspend fun updateGuardianOptionalContract(
        token: String,
        premium: String,
        body: UpdateGuardianOptionalContract,
    ) = getResultNew({
        service.updateGuardianOptionalContractRequest(token = token, premium = premium, body = body)
    })

    override suspend fun updateGuardianContract(
        token: String,
        premium: String,
        body: UpdateGuardianContract,
    ) = getResultNew({
        service.updateGuardianContractRequest(token = token, premium = premium, body = body)
    })

    override suspend fun insurancePayment(
        token: String,
        startDate: Long?,
        endDate: Long?,
        amount: Long?,
        systemType: String?,
        paramPage: String?,
        month: Int?,
        redirectUrl: String
    ) = getResultNew({
        service.insurancePayment(
            token,
            startDate,
            endDate,
            amount,
            systemType,
            paramPage,
            month = month,
            redirectUrl = redirectUrl
        )
    })

    override suspend fun getPremiumOptions(token: String) =
        getResultNew({ service.getPremiumRate(token) })


    override suspend fun getCancelContractReasons(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({

        service.getListSelfContractState(
            token,
            page = getRequestPage(paramsMap),
            limit = getQueryPageSize(paramsMap),
            start = getStartIndex(paramsMap),
            filter = "[]"
        )
    })

    override suspend fun cancelContractRequest(
        token: String,
        contractNumber: String,
        body: CancelContractRequest,
    ) = getResultNew({
        service.cancelContractRequest(token = token, contractNumber = contractNumber, body = body)
    })

    override suspend fun cancelOptionalContractRequest(
        token: String,
        contractNumber: String,
        body: CancelContractRequest,
    ) = getResultNew({
        service.cancelOptionalContractRequest(
            token = token,
            contractNumber = contractNumber,
            body = body
        )
    })

    override suspend fun getContractsPaymentsListFreelance(token: String, contractNumber: String) =
        getResultNew({
            service.getContractsPaymentsListFreelance(
                token = token,
                contractNumber = contractNumber
            )
        })

    override suspend fun getPaymentCalculationDetailList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getPaymentCalculationDetailList(
            token = token,
            startDate = paramsMap?.getValue("start_date") ?: "0",
            endDate = paramsMap?.getValue("end_date") ?: "0",
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun postNewInsuredInfo(token: String, dataModel: NewInsuredUserInfoReq?) =
        getResultNew({
            // val model = Gson().fromJson("{\"personal\":{\"nation\":\"01\",\"countryId\":\"0001\",\"cityOfBirthId\":\"1870\",\"cityOfIssueId\":\"1870\",\"dateOfBirth\":\"2002-12-16T20:30:00.000Z\",\"firstName\":\"سمیه\",\"lastName\":\"مدبری\",\"nationalId\":\"0083834001\"},\"relationWithTamin\":{\"id\":\"\",\"personal\":{},\"organizationId\":\"0010\",\"workshopId\":\"0010000162\",\"dateOfStart\":\"2022-12-16T20:30:00.000Z\",\"job\":\"2041AG\"}}", NewInsuredUserInfoReq::class.java)
            service.postNewInsuredInfo(
                token = token,
                dataModel
            )
        })


    override suspend fun updateNewInsuredInfo(
        token: String,
        requestId: Long?,
        dataModel: NewInsuredUserInfoReq?,
    ) =
        getResultNew({
            service.updateNewInsuredInfo(
                token = token,
                requestId,
                dataModel
            )
        })

    override suspend fun getInsuredRegistrationDocList(token: String, personalId: Long?) =
        getResultNew({
            // { "property":"personal.id", "value":"146698415", "operator":"EQUAL" }]

            val array = JsonArray()
            val jsonObj1 = JsonObject()
            jsonObj1.addProperty("property", "personal.id")
            jsonObj1.addProperty("value", personalId.toString())
            jsonObj1.addProperty("operator", "EQUAL")
            array.add(jsonObj1)

            service.getInsuredRegistrationDocList(
                token,
                page = "0",
                limit = "10",
                start = "0",
                filter = array.toString()
            )
        })

    override suspend fun putInsuredRegistrationDocList(
        token: String,
        personalId: String?,
        list: ArrayList<InsuredDoc>,
    ) =
        getResultNew({
            service.putInsuredRegistrationDocList(
                token = token,
                personalId,
                list
            )
        })

    override suspend fun checkUserIsNew(token: String, nationalId: String?) =
        getResultNew({
            service.checkUserIsNew(
                token = token,
                nationalId
            )
        })

    override suspend fun getRequestSummary(token: String, requestId: Long?) =
        getResultNew({
            service.getRequestSummary(
                token = token,
                requestId
            )
        })

    override suspend fun deleteRecentlyAddedUser(
        token: String,
        personalId: Long?,
    ) =
        getResultNew({
            service.deleteRecentlyAddedUser(token, personalId)
        })

    override suspend fun confirmRecentlyAddedUser(
        token: String,
        requestId: Long?,
    ) =
        getResultNew({
            service.confirmRecentlyAddedUser(token, requestId)
        })

    override suspend fun getWorkshopRecentlyAddedMembers(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            val filter = JsonArray()
            if (paramsMap?.containsKey("workshopId") == true) {
                val jsonObj1 = JsonObject()
                jsonObj1.addProperty("property", "workshopId")
                jsonObj1.addProperty("value", paramsMap["workshopId"])
                jsonObj1.addProperty("operator", "EQUAL")
                filter.add(jsonObj1)
            }
            if (paramsMap?.containsKey("organizationId") == true) {
                val jsonObj2 = JsonObject()
                jsonObj2.addProperty("property", "organizationId")
                jsonObj2.addProperty("value", paramsMap["organizationId"])
                jsonObj2.addProperty("operator", "EQUAL")
                filter.add(jsonObj2)
            }
            if (paramsMap?.containsKey("personal.nationalId") == true) {
                val jsonObj2 = JsonObject()
                jsonObj2.addProperty("property", "personal.nationalId")
                jsonObj2.addProperty("value", paramsMap["personal.nationalId"])
                jsonObj2.addProperty("operator", "EQ")
                filter.add(jsonObj2)
            }

            if (paramsMap?.containsKey("personal.request.status.requestCode") == true) {
                val jsonObj2 = JsonObject()
                jsonObj2.addProperty("property", "personal.request.status.requestCode")
                jsonObj2.addProperty("value", paramsMap["personal.request.status.requestCode"])
                jsonObj2.addProperty("operator", "EQ")
                filter.add(jsonObj2)
            }

            val sort = JsonArray()
            val jsonObj1 = JsonObject()
            jsonObj1.addProperty("property", "personal.request.refCode")
            jsonObj1.addProperty("direction", "DESC")
            sort.add(jsonObj1)

            service.getWorkshopRecentlyAddedMembers(
                token,
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
                filter = filter.toString(),
                sort = sort.toString()
            )
        })

    override suspend fun getRecentlyAddedUser(
        token: String,
        personalRequestId: Long?,
    ) =
        getResultNew({

            //  filter: [{"property":"personal.request.id","value":"145871490","operator":"EQ"}]

            val filter = JsonArray()

            val jsonObj1 = JsonObject()
            jsonObj1.addProperty("property", "personal.request.id")
            jsonObj1.addProperty("value", personalRequestId?.toString())
            jsonObj1.addProperty("operator", "EQ")
            filter.add(jsonObj1)


            service.getRecentlyAddedUser(
                token,
                filter = filter.toString()
            )
        })


    override suspend fun getAllInstallmentList(
        token: String,
        paramsMap: MutableMap<String, String>?

    ): InstallmentListResponse {

        val array = JsonArray()

        if (paramsMap?.containsKey("workshopId") == true) {
            val jsonObj1 = JsonObject()
            jsonObj1.addProperty("property", "workshopId")
            jsonObj1.addProperty("value", paramsMap["workshopId"])
            jsonObj1.addProperty("operator", "EQ")
            array.add(jsonObj1)
        }

        if (paramsMap?.containsKey("letterDate") == true) {
            val jsonObj1 = JsonObject()
            jsonObj1.addProperty("property", "letterDate")
            jsonObj1.addProperty("value", paramsMap["letterDate"])
            jsonObj1.addProperty("operator", "EQ")
            array.add(jsonObj1)
        }

        return getResultNew({
            service.getInstallmentList(
                token,
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
                filter = array.toString()
            )
        })
    }

    override suspend fun getWorkshopDebtInfo(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkShopDebtResponse {
        /*
      [{"property":"peymanSequence","value":"02100014","operator":"EQ"}]
       */
        val array = JsonArray()

        if (paramsMap?.containsKey("peymanSequence") == true) {
            val jsonObj1 = JsonObject()
            jsonObj1.addProperty("property", "peymanSequence")
            jsonObj1.addProperty("value", paramsMap["peymanSequence"])
            jsonObj1.addProperty("operator", "EQ")
            array.add(jsonObj1)
        }
        return getResultNew({
            service.getWorkshopDebt(
                token,
                paramsMap?.get("workshopId") ?: "",
                paramsMap?.get("branchCode") ?: "",
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
                filter = array.toString()
            )
        })
    }

    override suspend fun getSpecialContactList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkshopInfoResponse {
        val array = JsonArray()

        if (paramsMap?.containsKey("workshopId") == true) {
            val jsonObj1 = JsonObject()
            jsonObj1.addProperty(
                "property", "workshopId"
            )
            jsonObj1.addProperty("value", paramsMap["workshopId"])
            jsonObj1.addProperty("operator", "EQ")
            array.add(jsonObj1)
        }

        if (paramsMap?.containsKey("branchCode") == true) {
            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "branchCode")
            jsonObj2.addProperty("value", paramsMap["branchCode"])
            jsonObj2.addProperty("operator", "EQ")
            array.add(jsonObj2)
        }
        if (paramsMap?.containsKey("contractRow") == true) {
            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "contractRow")
            jsonObj2.addProperty("value", paramsMap["contractRow"])
            jsonObj2.addProperty("operator", "EQ")
            array.add(jsonObj2)
        }


        val filter = array.toString()

        return getResultNew({
            service.getSpecialContracts(
                token,
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
                filter = filter
            )
        })
    }

    override suspend fun getEmployerAgreementInfoList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            val array = JsonArray()
            if (paramsMap?.containsKey("workshopId") == true) {
                val jsonObj1 = JsonObject()
                jsonObj1.addProperty("property", "workshop.workshopId")
                jsonObj1.addProperty("value", paramsMap["workshopId"])
                jsonObj1.addProperty("operator", "EQ")
                array.add(jsonObj1)
            }

            if (paramsMap?.containsKey("branchCode") == true) {
                val jsonObj2 = JsonObject()
                jsonObj2.addProperty("property", "workshop.branchCode")
                jsonObj2.addProperty("value", paramsMap["branchCode"])
                jsonObj2.addProperty("operator", "EQ")
                array.add(jsonObj2)
            }
            if (paramsMap?.containsKey("workshopStatusCode") == true) {
                val jsonObj2 = JsonObject()
                jsonObj2.addProperty("property", "workshop.workshopStatus.workshopStatusCode")
                jsonObj2.addProperty("value", paramsMap["workshopStatusCode"])
                jsonObj2.addProperty("operator", "EQ")
                array.add(jsonObj2)
            }

            val filter = array.toString()

            service.getEmployerAgreementInfoList(
                token,
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
                filter = filter
            )
        })

    override suspend fun sendCommitmentRequest(
        token: String,
        mobile: String,
        email: String,
        serviceName: String,
    ) =
        getResultNew({
            val array = JsonArray()

            val jsonObj1 = JsonObject()
            jsonObj1.addProperty("property", "mobileNumber")
            jsonObj1.addProperty("value", mobile)
            jsonObj1.addProperty("operator", "EQ")
            array.add(jsonObj1)

            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "email")
            jsonObj2.addProperty("value", email)
            jsonObj2.addProperty("operator", "EQ")
            array.add(jsonObj2)

            val jsonObj3 = JsonObject()
            jsonObj3.addProperty("property", "serviceName")
            jsonObj3.addProperty("value", serviceName)
            jsonObj3.addProperty("operator", "EQ")
            array.add(jsonObj3)


            val filter = array.toString()

            service.sendCommitmentRequest(
                token,
                filter = filter
            )
        })

    override suspend fun sendVerificationCode(token: String, verifyCode: String) =
        getResultNew({ service.sendVerificationCode(token, verifyCode) })

    override suspend fun sendVerificationCode(token: String, verifyCode: VerifyCodeRequest) =
        getResultNew({ service.sendVerificationCode(token, verifyCode) })

    override suspend fun getWorkshopsInfoWithoutContract(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            service.getWorkshopsInfoWithoutContract(
                token,
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap)
            )
        })

    override suspend fun getWorkshopContactList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            service.getWorkshopContactList(
                token,
                workshopId = paramsMap?.get("workshopId"),
                branchCode = paramsMap?.get("branchCode"),
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap)
            )
        })

    override suspend fun getEmployerWorkshopList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            service.getEmployerWorkshopList(
                token,
                workshopId = paramsMap?.get("workshopId"),
                branchCode = paramsMap?.get("branchCode"),
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap)
            )
        })


    override suspend fun postEmployerAgreement(token: String, body: AgreementDataModel) =
        getResultNew({ service.postEmployerAgreement(token, body) })

    override suspend fun getLegalWorkshopInfo(token: String, legalWorkshopID: String?) =
        getResultNew({ service.getLegalWorkshopInfo(token, legalWorkshopID) })

    override suspend fun getLegalWorkshopCEOInfo(
        token: String,
        nationalCode: String?,
        birthdate: String?,
    ) =
        getResultNew({ service.getLegalWorkshopCEOInfo(token, nationalCode, birthdate) })


    override suspend fun getVerificationTicketForLegalWorkshopInfo(
        token: String,
        mobileNumber: String?,
        email: String?,
        nationalCode: String?,
    ) =
        getResultNew({

            val array = JsonArray()

            val jsonObj1 = JsonObject()
            jsonObj1.addProperty("property", "mobileNumber")
            jsonObj1.addProperty("value", mobileNumber)
            jsonObj1.addProperty("operator", "EQ")
            array.add(jsonObj1)

            val jsonObj2 = JsonObject()
            jsonObj2.addProperty("property", "email")
            jsonObj2.addProperty("value", email)
            jsonObj2.addProperty("operator", "EQ")
            array.add(jsonObj2)

            val jsonObj4 = JsonObject()
            jsonObj4.addProperty("property", "nationalCode")
            jsonObj4.addProperty("value", nationalCode)
            jsonObj4.addProperty("operator", "EQ")
            array.add(jsonObj4)

            val jsonObj3 = JsonObject()
            jsonObj3.addProperty("property", "serviceName")
            jsonObj3.addProperty("value", "saveStackHolder")
            jsonObj3.addProperty("operator", "EQ")
            array.add(jsonObj3)

            val filter = array.toString()

            service.getVerificationTicketForLegalWorkshopInfo(token, filter)
        })

    override suspend fun sendVerifyTicketForLegalWorkshop(token: String, body: TicketRequest) =
        getResultNew({ service.sendVerifyTicketForLegalWorkshop(token, body) })


    override suspend fun getOptionalInsurancePaymentCalculationDetailList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getOptionalInsurancePaymentCalculationDetailList(
            token = token,
            startDate = paramsMap?.getValue("start_date") ?: "0",
            endDate = paramsMap?.getValue("end_date") ?: "0",
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun getLegalStackHolderList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getLegalStackHolderList(
            token = token,
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun requestLegalStackHolderTicket(token: String, nationalCode: String?) =
        getResultNew({
            if (nationalCode.isNullOrEmpty())
                service.requestOfLegalTicket(token)
            else
                service.requestOfLegalTicketWithNationalCode(token, nationalCode)
        })

    override suspend fun verifyLegalStackHolderTicket(token: String, ticket: String) =
        getResultNew({ service.validateLegalTicket(token, ticket) })

    override suspend fun getLegalAgentList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        service.getLegalStackHolderListByTicket(
            token = token,
            workshopId = paramsMap?.get("workshopId"),
            branchCode = paramsMap?.get("branchCode"),
            //ticket = paramsMap?.get("verificationCode"),
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = getArrayList(paramsMap)
        )
    })

    override suspend fun submitNewLegalAgent(
        token: String,
        ticket: String,
        request: AgentRequestModel,
    ) =
        getResultNew({ service.submitNewLegalAgent(token, ticket, request) })

    override suspend fun deleteLegalAgent(
        token: String,
        ticket: String?,
        stackId: Long?,
    ) =
        getResultNew({ service.deleteLegalAgent(token, ticket, stackId) })

    override suspend fun getFamilyRelationShips(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            service.getRelationShipList(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap),
                getArrayList(paramsMap)
            )
        })

    override suspend fun getDependentInfo(token: String) =
        getResultNew({ service.getDependentInfo(token) })

    override suspend fun inquiryRegistryInfo(
        token: String,
        nationalCode: String,
        timeStampBirthDay: String,
        dependencyCode: String,
    ) = getResultNew({
        service.inquiryRegistryInfo(token, nationalCode, timeStampBirthDay, dependencyCode)
    })

    override suspend fun inquiryEducationCode(
        token: String,
        nationalId: String,
        inquiryLicenseCode: String,
    ) = getResultNew({ service.getInquiryEducation(token, nationalId, inquiryLicenseCode) })

    override suspend fun addNewDependent(
        token: String,
        requestBody: RequestAddDependent,
    ) = getResultNew({
        service.addNewDependent(token = token, body = requestBody)
    })

    override suspend fun refreshDependent(token: String) = getResultNew({
        service.refreshDependent(token = token)
    })

    override suspend fun checkRenewCondition(token: String) = getResultNew({
        service.checkRenewCondition(token = token)
    })

    override suspend fun inquiryStudyCodeCertificate(
        token: String,
        code: String,
        studyCode: String,
    ) = getResultNew({
        service.inquiryStudyCodeCertificate(token = token, code = code, studyCode = studyCode)
    })

    override suspend fun getMafasaHesabContractSubjects(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) =
        getResultNew({
            service.getMafasaHesabContractSubjects(
                token,
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
            )
        })

    override suspend fun sendMafasaHesabRequest(
        token: String,
        id: String,
        body: MafasaHesabRequestModel,
    ) =
        getResultNew({ service.sendMafasaHesabRequest(token, id, body) })


    override suspend fun getDisabilityDependentInfo(token: String) = getResultNew({
        service.getDisabilityDependentInfo(token = token)
    })

    override suspend fun getDisabilityPersonalInfo(token: String) = getResultNew({
        service.getDisabilityPersonalInfo(token = token)
    })

    override suspend fun saveDisabilityUserInfo(token: String, body: DisabilitySaveInfoRequest) =
        getResultNew({
            service.saveDisabilityUserInfo(token = token, body = body)
        })

    override suspend fun getUserAge(token: String, birthDate: Long) = getResultNew({
        service.getUserAge(token = token, birthDate = birthDate)
    })

    override suspend fun getMedicalCommissionPdf(
        token: String,
        lastWorkShop: String,
    ) = getPdfResult {
        service.getMedicalCommissionPdf(token = token, lastWorkshop = lastWorkShop)
    }

    override suspend fun finalConfirmDisabilityRequest(
        token: String,
        requestId: Long,
        body: DisabilityFinalConfirmRequest,
    ) = getResultNew({
        service.finalConfirmDisabilityRequest(token = token, requestId = requestId, body = body)
    })

    override suspend fun saveDocumentDisability(
        token: String,
        requestId: Long,
        body: DisabilitySaveDocumentRequest,
    ) = getResultNew({
        service.saveDocumentDisability(token = token, requestId = requestId, body = body)
    })

    override suspend fun getRegistrationDeclarationForm(token: String) =
        getPdfResult {
            service.getRegistrationDeclarationForm(
                token,
                "https://eservices.tamin.ir/view/assets/pdfs/questionair.pdf"
            )
        }

    override suspend fun getDebtDiscount(
        token: String,
        branchCode: String,
        debitNumber: String,
        debitRemain: Long
    ) = getResultNew({
        service.getDebtDiscount(token, branchCode, debitNumber, debitRemain)
    })

    override suspend fun installmentDebt(token: String, request: InstallmentRequestModel) =
        getResultNew({ service.installmentDebt(token, request) })


    override suspend fun getClause38List(token: String, paramsMap: MutableMap<String, String>?) =
        getResultNew({
            service.getClause38List(
                token,
                workshopCode = paramsMap?.get("workshopCode"),
                branchCode = paramsMap?.get("branchCode"),
                ContractRow = paramsMap?.get("contractRow"),
                MafasaStatus = "-",//paramsMap?.get("mafasaStatus") ?: "-",
                contractNumber = "-",//paramsMap?.get("contractNumber") ?: "-",
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
            )
        })

    override suspend fun getClause38Detail(token: String, paramsMap: MutableMap<String, String>?) =
        getResultNew({
            service.getClause38Detail(
                token,
                workshopCode = paramsMap?.get("workshopCode"),
                branchCode = paramsMap?.get("branchCode"),
                ContractRow = paramsMap?.get("contractRow"),
                mafasaSerialNo = paramsMap?.get("mafasaSerialNo") ?: "-",
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
            )
        })


    override suspend fun getLetterSubject(token: String, paramsMap: MutableMap<String, String>?) =
        getResultNew({
            service.getLetterSubject(
                token,
                contract = paramsMap?.get("contract"),
                page = getRequestPage(paramsMap),
                limit = getQueryPageSize(paramsMap),
                start = getStartIndex(paramsMap),
            )
        })

    override suspend fun deleteExistLetter(
        token: String,
        letterRequestId: Long?, workshopId: String?
    ) = getResultNew({
        service.deleteExistingLetter(
            token,
            reqno = "${letterRequestId}Pp${workshopId}"
        )
    })


    override suspend fun registerLetter(token: String, dataModel: RegisterLetterRequest?) =
        getResultNew({
            service.registerLetter(
                token = token,
                dataModel
            )
        })


    override suspend fun getWorkshopListDefinitiveDebt(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        val array = JsonArray()
        if (paramsMap?.containsKey("workshopId") == true) {
            array.add(JsonObject().apply {
                addProperty("property", "workshop.workshopId")
                addProperty("value", paramsMap["workshopId"])
                addProperty("operator", "EQ")
            })
        }

        if (paramsMap?.containsKey("branchCode") == true) {
            array.add(JsonObject().apply {
                addProperty("property", "workshop.branchCode")
                addProperty("value", paramsMap["branchCode"])
                addProperty("operator", "EQ")
            })
        }

        service.getWorkshopListDefinitiveDebt(
            token,
            page = getRequestPage(paramsMap),
            limit = getQueryPageSize(paramsMap),
            start = getStartIndex(paramsMap),
            filter = array.toString()
        )

    })


    override suspend fun getWorkshopsDebtsList(
        token: String,
        paramsMap: MutableMap<String, String>?,
    ) = getResultNew({
        val array = JsonArray()
        if (paramsMap?.containsKey(Constants.DEBIT_NUMBER) == true) {
            array.add(JsonObject().apply {
                addProperty("property", "debitNumber")
                addProperty("value", paramsMap[Constants.DEBIT_NUMBER])
                addProperty("operator", "EQ")
            })
        }

        if (paramsMap?.containsKey(Constants.AGREEMENT_ROW) == true) {
            array.add(JsonObject().apply {
                addProperty("property", "peymanSequence")
                addProperty("value", paramsMap[Constants.AGREEMENT_ROW])
                addProperty("operator", "EQ")
            })
        }
        service.getWorkshopsDebtsList(
            token = token,
            workshopId = paramsMap?.get(Constants.WORKSHOP_ID) ?: "0",
            branchId = paramsMap?.get(Constants.BRANCH_ID) ?: "0",
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            filter = array.toString(),
            limit = getQueryPageSize(paramsMap)
        )
    })

    override suspend fun getWorkshopInfoDebtArticle16(
        token: String,
        paramsMap: MutableMap<String, String>?
    ) = getResultNew({
        service.getWorkshopInfoDebtArticle16(
            token = token,
            workshopId = paramsMap?.get(Constants.WORKSHOP_ID) ?: "0",
            branchCode = paramsMap?.get(Constants.BRANCH_ID) ?: "0"
        )
    })

    override suspend fun registrationRequestArticle16(
        token: String,
        body: RegisterArticle16RequestModel
    ) = getResultNew({ service.registrationRequestArticle16(token = token, body = body) })

    override suspend fun getRequestInfoArticle16(
        token: String,
        objectionNumber: Long
    ) = getResultNew({
        service.getRequestInfoArticle16(
            token = token,
            objectionNumber = objectionNumber
        )
    })

    override suspend fun approveDistantLetter(
        token: String,
        workshopId: String?, requestId: Long?
    ) =
        getResultNew({ service.approveLetter(token, "${requestId}TT${workshopId}") })


    override suspend fun sendDistantCorrespondenceRequest(
        token: String,
        dataModel: RegisterLetterRequest
    ) =
        getResultNew({ service.sendDistantCorrespondenceRequest(token, dataModel) })

    override suspend fun getDistantCorrespondenceList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ) = getResultNew({
        service.getDistantCorrespondenceList(
            token = token,
            workshopId = paramsMap?.get(Constants.WORKSHOP_ID) ?: "0",
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap)
        )
    })

    override suspend fun getShortTermRequestStatus(
        token: String,
        referenceId: String
    ) = getResultNew({
        service.getShortTermRequestStatus(
            token = token,
            referenceId = referenceId
        )
    })

    override suspend fun getShortTermRequestInfo(
        token: String,
        referenceId: String
    ) = getResultNew({ service.getShortTermRequestInfo(token = token, referenceId = referenceId) })

    override suspend fun getDocument(
        token: String,
        guid: String
    ) = getResultNew({ service.getDocument(token = token, guid = guid) })

    override suspend fun getDeceasedInfo(
        token: String,
        nationalCode: String
    ) = getResultNew({
        service.getDeceasedInfo(token = token, id = nationalCode)
    })

    override suspend fun getSurvivorList(
        token: String,
        deceasedNationalId: String
    ) = getResultNew({
        service.getSurvivorList(token = token, id = deceasedNationalId)
    })

    override suspend fun saveSurvivorInfo(
        token: String,
        body: SaveSurvivorInfoRequest
    ) = getResultNew({
        service.saveSurvivorInfo(token = token, body = body)
    })

    override suspend fun confirmSurvivorsList(token: String) =
        getResultNew({ service.confirmSurvivorsList(token = token) })

    override suspend fun getFinalSurvivorPensionPDF(token: String) = getPdfResult {
        service.getFinalSurvivorPensionPDF(token = token)
    }

    override suspend fun submitFinalSurvivorPension(token: String, requestId: Int) = getResultNew({
        service.submitFinalSurvivorPension(
            token = token,
            body = RequestModel(id = requestId),
            requestId = requestId
        )
    })

    override suspend fun getAge(token: String, birthDate: Long) = getResultNew({
        service.getAge(token = token, birthDate = birthDate)
    })


    override suspend fun checkRetirementStatus(token: String) = getResultNew({
        service.checkRetirementStatus(token)
    })

    override suspend fun getAuthenticationCode(token: String) = getResultNew({
        service.getAuthenticationCode(token)
    })

    override suspend fun getUserAge(token: String, birthDate: Long?) = getResultNew({
        service.getUserAge(token = token, birthDate = birthDate)
    })

    override suspend fun authenticationAndGetPersonalInfo(
        token: String,
        authenticationsCode: Long,
    ) =
        getResultNew({
            service.authenticationAndGetPersonalInfo(
                token = token,
                authenticationsCode = authenticationsCode
            )
        })

    override suspend fun confirmIdentityAndHistoryInfo(
        token: String,
        authenticationsCode: Long,
        body: ConfirmIdentityAndHistoryInfoRequest,
    ) = getResultNew({
        service.confirmIdentityAndHistoryInfo(
            token = token,
            authenticationsCode = authenticationsCode,
            body = body
        )
    })

    override suspend fun getRetirementRequestInfo(
        token: String,
        filter: String,
    ) = getResultNew({
        service.getRetirementRequestInfo(token = token, filter = filter)
    })

    override suspend fun sendRetirementDocument(
        token: String,
        requestId: String,
        body: RetirementSaveDocumentRequest
    ) = getResultNew({
        service.sendRetirementDocument(token = token, requestId = requestId, body = body)
    })

    override suspend fun getDeferredInstallmentInfo(
        token: String,
        requestId: String
    ) = getResultNew({ service.getDeferredInstallmentInfo(token = token, requestId = requestId) })

    override suspend fun getPrescriptionPdfFile(
        token: String,
        prescriptionID: String
    ) = getPdfResult {
        service.getPrescriptionPdfFile(
            token = token,
            prescriptionID = prescriptionID
        )
    }

    override suspend fun getConstructionFiles(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ConstructionFileResponse {
        val array = JsonArray()
        paramsMap?.let { params ->
            if (params.containsKey(FILE_ID)) {
                array.add(JsonObject().apply {
                    addProperty("property", "fileNo")
                    addProperty("value", paramsMap[FILE_ID])
                    addProperty("operator", "EQ")
                })
            }
            if (params.containsKey(REQUEST_ID)) {
                array.add(JsonObject().apply {
                    addProperty("property", "reqNo")
                    addProperty("value", paramsMap[REQUEST_ID])
                    addProperty("operator", "EQ")
                })
            }
            if (params.containsKey(WORKSHOP_ID)) {
                array.add(JsonObject().apply {
                    addProperty("property", "workshop.workshopId")
                    addProperty("value", paramsMap[WORKSHOP_ID])
                    addProperty("operator", "EQ")
                })
            }

            if (params.containsKey(BRANCH_ID)) {
                array.add(JsonObject().apply {
                    addProperty("property", "workshop.branchCode")
                    addProperty("value", paramsMap[BRANCH_ID])
                    addProperty("operator", "EQ")
                })
            }
            if (params.containsKey(REQUEST_DATE)) {
                array.add(JsonObject().apply {
                    addProperty("property", REQUEST_DATE)
                    addProperty("value", paramsMap[REQUEST_DATE])
                    addProperty("operator", "EQ")
                })
            }
        }
        return getResultNew({
            service.getConstructionFiles(
                token = token,
                page = getRequestPage(paramsMap),
                start = getStartIndex(paramsMap),
                filter = array.toString(),
                limit = getQueryPageSize(paramsMap)
            )
        })
    }

    override suspend fun getDetailConstructionFile(
        token: String,
        paramMap: MutableMap<String, String>?
    ): DetailConstructionInfoResponse {
        val array = JsonArray()
        paramMap?.let { params ->
            when {
                params.containsKey(REQUEST_ID) -> {
                    array.add(JsonObject().apply {
                        addProperty("property", "reqNo")
                        addProperty("value", params[REQUEST_ID])
                        addProperty("operator", "EQ")
                    })
                }

                params.containsKey(FILE_ID) -> {
                    array.add(JsonObject().apply {
                        addProperty("property", "fileNo")
                        addProperty("value", params[FILE_ID])
                        addProperty("operator", "EQ")
                    })
                }

                params.containsKey(REQUEST_DATE) -> {
                    array.add(JsonObject().apply {
                        addProperty("property", "bldprdate")
                        addProperty("value", params[REQUEST_DATE])
                        addProperty("operator", "EQ")
                    })
                }
            }
        }
        return getResultNew({
            service.getDetailConstructionFile(
                token = token,
                page = getRequestPage(paramMap),
                start = getStartIndex(paramMap),
                limit = getQueryPageSize(paramMap),
                filter = array.toString()
            )
        })
    }

    override suspend fun getBeneficiariesWorkshop(
        token: String,
        paramMap: MutableMap<String, String>?
    ): BeneficiariesConstructionResponse {
        val array = JsonArray()
        paramMap?.let { params ->

            if (params.containsKey(REQUEST_ID)) {
                array.add(JsonObject().apply {
                    addProperty("property", "reqNo")
                    addProperty("value", params[REQUEST_ID])
                    addProperty("operator", "EQ")
                })
            }

            if (params.containsKey(FILE_ID)) {
                array.add(JsonObject().apply {
                    addProperty("property", "fileNo")
                    addProperty("value", params[FILE_ID])
                    addProperty("operator", "EQ")
                })
            }

            if (params.containsKey(REQUEST_DATE)) {
                array.add(JsonObject().apply {
                    addProperty("property", "bldprdate")
                    addProperty("value", params[REQUEST_DATE])
                    addProperty("operator", "EQ")
                })

            }
        }
        return getResultNew({
            service.getBeneficiariesWorkshop(
                token = token,
                page = getRequestPage(paramMap),
                start = getStartIndex(paramMap),
                limit = getQueryPageSize(paramMap),
                filter = array.toString()
            )
        })
    }

    override suspend fun getPaymentSheetConstructionInfo(
        token: String,
        debitNumber: String
    ) = getResultNew({
        service.getPaymentSheetConstructionInfo(
            token = token,
            debitNumber = debitNumber
        )
    })

    override suspend fun getCertificatePaymentSheetPDF(
        token: String,
        debitNumber: String,
        branchCode: String
    ) = getPdfResult {
        service.getCertificatePaymentSheetPDF(
            token = token,
            debitNumber = debitNumber,
            branchCode = branchCode
        )
    }

    override suspend fun issuancePaymentSheet(
        token: String,
        debitNumber: String,
    ) = getResultNew({
        service.issuancePaymentSheet(token = token, debitNumber = debitNumber)
    })

    override suspend fun getInstallmentLetterList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ) = getResultNew({
        service.getInstallmentLetterList(
            token = token,
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = "[]",
            workshopId = paramsMap?.get(WORKSHOP_ID) ?: "",
            branchId = paramsMap?.get(BRANCH_ID) ?: ""
        )
    })

    override suspend fun getDetailDebitList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ) = getResultNew({
        service.getDetailDebitList(
            token = token,
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = "[]",
            debitNumber = paramsMap?.get(DEBIT_NUMBER) ?: "",
            branchId = paramsMap?.get(BRANCH_ID) ?: ""
        )
    })

    override suspend fun getInstallmentConstructionList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ) = getResultNew({
        service.getInstallmentConstructionList(
            token = token,
            page = getRequestPage(paramsMap),
            start = getStartIndex(paramsMap),
            limit = getQueryPageSize(paramsMap),
            filter = "[]",
            debitNumber = paramsMap?.get(DEBIT_NUMBER) ?: "",
            branchId = paramsMap?.get(BRANCH_ID) ?: ""
        )
    })

    override suspend fun getFollowUpResultObjectionNonExistsHistory(
        token: String,
        referenceId: String
    ) = getResultNew({
        service.getFollowUpResultObjectionNonExistsHistory(
            token = token,
            referenceId = referenceId
        )
    })

    override suspend fun getPaymentDebitList(
        token: String,
        debtNumber: String,
        branchCode: String
    ) = getResultNew({
        service.getPaymentDebitList(
            token = token,
            debitNumber = debtNumber,
            branchCode = branchCode
        )
    })

    override suspend fun getPaymentDebitToken(
        token: String,
        debtNumber: String,
    ) = getResultNew({ service.getPaymentDebitToken(token = token, debitNumber = debtNumber) })


    override suspend fun checkPaymentDebt(token: String, debtSerialNumber: String) = getResultNew({
        service.checkPaymentDebt(token = token, debtSerialNumber = debtSerialNumber)
    })

    override suspend fun getDebtPaidList(
        token: String,
        debtSerialNumber: String
    ) = getResultNew({
        service.getDebtPaidList(
            token = token,
            debtSerialNumber = debtSerialNumber
        )
    })

    override suspend fun getRegisteredMedicalCommission(token: String) =
        getResultNew({ service.getRegisteredMedicalCommission(token = token) })

    override suspend fun getInsuredRelation(
        token: String,
        nationalCode: String?
    ): InsuredRelationResponse {

        val array = JsonArray()
        val jsonObj1 = JsonObject()
        jsonObj1.addProperty("property", "workshopId")
        jsonObj1.addProperty("value", nationalCode)
        jsonObj1.addProperty("operator", "EQUAL")
        array.add(jsonObj1)

        return getResultNew({
            service.getInsuredRelation(
                token = token,
                filter = array.toString()
            )
        })
    }


    override suspend fun getAllWorkshops(
        token: String,
        nationalCode: String?
    ): AllWorkshopsResponse {
        val array = JsonArray()
        val jsonObj1 = JsonObject()
        jsonObj1.addProperty("property", "workshopId")
        jsonObj1.addProperty("value", nationalCode)
        jsonObj1.addProperty("operator", "EQUAL")
        array.add(jsonObj1)

        return getResultNew({ service.getAllWorkshops(token = token, filter = array.toString()) })

    }

    override suspend fun getAllWorkshopHistory(
        token: String,
        workshopCode:String?,
        nationalCode:String?,
        branchCode:String?,
        insuranceNumber:String?,
        occurrenceDate:String?): GeneralRes {

        val array = JsonArray()
        for (i in 1..5){
            val jsonObj = JsonObject()
            when(i){
                1->{
                    jsonObj.addProperty("property", "workshopCode")
                    jsonObj.addProperty("value", workshopCode)
                }
                2->{
                    jsonObj.addProperty("property", "nationalCode")
                    jsonObj.addProperty("value", nationalCode)
                }
                3->{
                    jsonObj.addProperty("property", "branchCode")
                    jsonObj.addProperty("value", branchCode)
                }
                4->{
                    jsonObj.addProperty("property", "insuranceNumber")
                    jsonObj.addProperty("value", insuranceNumber)
                }
                5->{
                    jsonObj.addProperty("property", "occurrenceDate")
                    jsonObj.addProperty("value", occurrenceDate)
                }
            }
            jsonObj.addProperty("operator", "EQUAL")
            array.add(jsonObj)
        }

        return getResultNew({ service.getAllWorkshopHistory(token = token, filter = array.toString()) })

    }

    override suspend fun getWorkshopSpecification(
        token: String,
        workshopCode: String?,
        branchCode: String?
    ): WorkshopSpecificationResponse {
        val array = JsonArray()
        val jsonObj1 = JsonObject()
        jsonObj1.addProperty("property", "workshopCode")
        jsonObj1.addProperty("value", workshopCode)
        jsonObj1.addProperty("operator", "EQUAL")
        array.add(jsonObj1)

        val jsonObj2 = JsonObject()
        jsonObj2.addProperty("property", "branchCode")
        jsonObj2.addProperty("value", branchCode)
        jsonObj2.addProperty("operator", "EQUAL")
        array.add(jsonObj2)


        return getResultNew({
            service.getWorkshopSpecification(
                token = token,
                filter = array.toString()
            )
        })
    }

    override suspend fun getOfficePersonalInfo(
        token: String,
        nationalCode: String?,
        birthDate: Long?,
        workshopCode: String?,
        branchCode: String?
    ): OfficePersonalInfoResponse {
        val array = JsonArray()
        val jsonObj1 = JsonObject()
        jsonObj1.addProperty("property", "nationalCode")
        jsonObj1.addProperty("value", nationalCode)
        jsonObj1.addProperty("operator", "EQUAL")
        array.add(jsonObj1)

        val jsonObj2 = JsonObject()
        jsonObj2.addProperty("property", "birthDate")
        jsonObj2.addProperty("value", birthDate.toString())
        jsonObj2.addProperty("operator", "EQUAL")
        array.add(jsonObj2)

        val jsonObj3 = JsonObject()
        jsonObj3.addProperty("property", "workshopCode")
        jsonObj3.addProperty("value", workshopCode)
        jsonObj3.addProperty("operator", "EQUAL")
        array.add(jsonObj2)

        val jsonObj4 = JsonObject()
        jsonObj4.addProperty("property", "branchCode")
        jsonObj4.addProperty("value", branchCode)
        jsonObj4.addProperty("operator", "EQUAL")
        array.add(jsonObj2)

        return getResultNew({
            service.getOfficePersonalInfo(
                token = token,
                filter = array.toString()
            )
        })
    }

    override suspend fun getDocumentType(
        token: String,
        paramsMap: MutableMap<String, String>?,
    )= getResultNew({
            service.getDocumentType(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap)
            )
        })

    override suspend fun uploadOccurrenceImage(token: String, image: MultipartBody.Part) =
        getResultNew({ service.uploadOccurrenceImage(token, image) }, false)


    override suspend fun sendOccurrenceRequest(
        token: String,
        request: OccurrenceReq
    ) = getResultNew({ service.sendOccurrenceRequest(token, request) })

    override suspend fun downloadTestResultPdf(
        token: String, patientID: String,noteHeadEprescID: String,currentUserNationalCode: String
    ) = getPdfResult {
        service.downloadTestResultPdf(token, patientID, noteHeadEprescID,currentUserNationalCode)
    }

    override suspend fun getMedicalMissionList(token: String, paramsMap: MutableMap<String, String>?) =
        getResultNew({
            service.getMedicalMissionList(
                token,
                getRequestPage(paramsMap),
                getStartIndex(paramsMap),
                getQueryPageSize(paramsMap),
                getArrayList(paramsMap)
            )
        })

    override suspend fun dependentCancellation(
        token: String,
        action: String,
        identifier: String,
        date: String
    ) = getResultNew<GeneralRes>({ service.dependentCancellation(action, identifier, date) })
}

