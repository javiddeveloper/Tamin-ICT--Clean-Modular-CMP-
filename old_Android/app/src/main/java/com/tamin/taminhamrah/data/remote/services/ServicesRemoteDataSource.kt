package com.tamin.taminhamrah.data.remote.services

import com.tamin.taminhamrah.data.entity.DownloadFileResponse
import com.tamin.taminhamrah.data.entity.PensionInquiryModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.ai.AiServiceResponse
import com.tamin.taminhamrah.data.remote.models.ai.LawsAiSearchResponse
import com.tamin.taminhamrah.data.remote.models.electronicFile.myElectronicFile.ElectronicFileResponse
import com.tamin.taminhamrah.data.remote.models.employer.ConfirmUserResponse
import com.tamin.taminhamrah.data.remote.models.employer.DebtDiscountResponse
import com.tamin.taminhamrah.data.remote.models.employer.InsuredDoc
import com.tamin.taminhamrah.data.remote.models.employer.InsuredDocsResponse
import com.tamin.taminhamrah.data.remote.models.employer.LetterListResponse
import com.tamin.taminhamrah.data.remote.models.employer.LetterSubjectResponse
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredSummaryResponse
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredUserInfoReq
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredUserInfoResponse
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredUserStatusResponse
import com.tamin.taminhamrah.data.remote.models.employer.RegisterLetterRequest
import com.tamin.taminhamrah.data.remote.models.employer.RegisterLetterResponse
import com.tamin.taminhamrah.data.remote.models.employer.debit.DebtPaidListResponse
import com.tamin.taminhamrah.data.remote.models.employer.debit.InstallmentPaymentResponse
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerAgreementResponse
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerCommitmentResponse
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerWorkshopResponse
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.WorkshopContractListResponse
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.WorkshopInfoWithoutContractResponse
import com.tamin.taminhamrah.data.remote.models.employer.legalStackHolder.LegalStackHolderResponse
import com.tamin.taminhamrah.data.remote.models.employer.legalWorkshop.LegalWorkshopCEOInfoResponse
import com.tamin.taminhamrah.data.remote.models.employer.legalWorkshop.LegalWorkshopInfoResponse
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.profile.ProfileResponse
import com.tamin.taminhamrah.data.remote.models.responses.CalculateMarriageResponse
import com.tamin.taminhamrah.data.remote.models.services.AcraConfigResponse
import com.tamin.taminhamrah.data.remote.models.services.ActiveRelationResponse
import com.tamin.taminhamrah.data.remote.models.services.AgeResponse
import com.tamin.taminhamrah.data.remote.models.services.AllHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.BankAccountResponse
import com.tamin.taminhamrah.data.remote.models.services.BeneficiaryResponse
import com.tamin.taminhamrah.data.remote.models.services.BodySaveNonExistentHistory
import com.tamin.taminhamrah.data.remote.models.services.BranchDetailResponse
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.CityNameListResponse
import com.tamin.taminhamrah.data.remote.models.services.CityResponse
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordResponse
import com.tamin.taminhamrah.data.remote.models.services.ConfirmSurvivorRequest
import com.tamin.taminhamrah.data.remote.models.services.CovidResponse
import com.tamin.taminhamrah.data.remote.models.services.DebitObjection
import com.tamin.taminhamrah.data.remote.models.services.DebitObjectionResponse
import com.tamin.taminhamrah.data.remote.models.services.DeferredInstallmentCertificateResponse
import com.tamin.taminhamrah.data.remote.models.services.DeferredInstallmentReq
import com.tamin.taminhamrah.data.remote.models.services.DependantsResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.GeneralStringRes
import com.tamin.taminhamrah.data.remote.models.services.IdentityInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.InsuranceTypeResponce
import com.tamin.taminhamrah.data.remote.models.services.LastRelationResponse
import com.tamin.taminhamrah.data.remote.models.services.MedicalStudentResponse
import com.tamin.taminhamrah.data.remote.models.services.NotExistRequestsResponse
import com.tamin.taminhamrah.data.remote.models.services.ObjectionInsuranceHistoryModel
import com.tamin.taminhamrah.data.remote.models.services.ObjectionInsuranceHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.PayRollResponse
import com.tamin.taminhamrah.data.remote.models.services.PensionerIdResponse
import com.tamin.taminhamrah.data.remote.models.services.PersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.ProvinceResponse
import com.tamin.taminhamrah.data.remote.models.services.RecipientResponse
import com.tamin.taminhamrah.data.remote.models.services.RedCrossStatusResponse
import com.tamin.taminhamrah.data.remote.models.services.SendInsuranceHistoryToInstitutionResponse
import com.tamin.taminhamrah.data.remote.models.services.ServiceResponseModel
import com.tamin.taminhamrah.data.remote.models.services.ServiceResponseModelNew
import com.tamin.taminhamrah.data.remote.models.services.ShortTermOrthosisReq
import com.tamin.taminhamrah.data.remote.models.services.ShortTermOrthosisResponse
import com.tamin.taminhamrah.data.remote.models.services.ShorttremMariageReq
import com.tamin.taminhamrah.data.remote.models.services.TitlesJobResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.UserInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.ViewShortTermRequestResponse
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.WeddingPresentResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.BranchesInfoListResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CalculateFreelanceDebitResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CalculateSalary
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckAgeAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckContractStatusResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ConcludingStudentInsuranceContractResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractByGuardianRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractPremiumOptionsResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractPremiumRateResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.FinalConfirmResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.FreelanceLastPaymentResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.FreelancerJobTitlesResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.OptionalContractByGuardian
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.OptionalContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.OptionalInsuranceLastPaymentResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.UpdateAddressInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.cancelContract.CancelContractReasonsResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.cancelContract.CancelContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateContractRequest
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateGuardianContract
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateGuardianOptionalContract
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract.UpdateOptionalContract
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.PaymentListDefaultResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.detailPayment.DetailPaymentListDefaultResponse
import com.tamin.taminhamrah.data.remote.models.services.construction.WorkersPayDebitRequest
import com.tamin.taminhamrah.data.remote.models.services.construction.WorkersPayDebitResponse
import com.tamin.taminhamrah.data.remote.models.services.construction.WorkersPaymentInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.BeneficiariesConstructionResponse
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.ConstructionFileResponse
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.DetailConstructionInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.InstallmentConstructionListResponse
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.InstallmentDebitListResponse
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.InstallmentLetterListResponse
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.PaymentSheetConstructionFilesResponse
import com.tamin.taminhamrah.data.remote.models.services.contract.Clause38DetailResponse
import com.tamin.taminhamrah.data.remote.models.services.contract.Clause38Response
import com.tamin.taminhamrah.data.remote.models.services.contract.ComputationalBaseResponse
import com.tamin.taminhamrah.data.remote.models.services.contract.ContractResponse
import com.tamin.taminhamrah.data.remote.models.services.contract.ContractResponseNew
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilityDependentResponse
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilityFinalConfirmRequest
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilityPersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilitySaveDocumentRequest
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilitySaveInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.DisabilitySaveInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.disabilityPension.medicalCommission.RegisteredMedicalCommissionResponse
import com.tamin.taminhamrah.data.remote.models.services.edict.EdictPensionerResponse
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.DependantUserUnder18Response
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.ElectronicPrescriptionDetailResponse
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.ElectronicPrescriptionPriceResponse
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.ElectronicPrescriptionResponse
import com.tamin.taminhamrah.data.remote.models.services.girlSurvivor.CheckGirlSurvivorConditionsResponse
import com.tamin.taminhamrah.data.remote.models.services.inquirePensionStatus.InquirePensionStatusResponse
import com.tamin.taminhamrah.data.remote.models.services.insuranceRegistration.RegistrationReq
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.InspectionPerformedResponse
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit.InfoInspectionResponse
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit.JobTitleResponse
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit.SubmitInspectionRequestResponse
import com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit.SubmitResponse
import com.tamin.taminhamrah.data.remote.models.services.mafasaHesab.MafasaHesabContractSubjectResponse
import com.tamin.taminhamrah.data.remote.models.services.medicalAuthorities.MedicalAuthoritiesResponse
import com.tamin.taminhamrah.data.remote.models.services.medicalMission.MedicalMissionResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.CheckSaveNotExistModel
import com.tamin.taminhamrah.data.remote.models.services.objectionInsuranceHistory.CheckStatusConflictResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.CheckStatusNotExistModel
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.DeleteNotExistResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionInsuranceHistory.FinalConfirmConflictResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionInsuranceHistory.ResultRequestSaveOfObjectionInsuranceResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionInsuranceHistory.SendConfirmConflictResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.SendConfirmNotExistResponse
import com.tamin.taminhamrah.data.remote.models.services.objectionNonExistentHistory.SendFinalConfirmResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.AllWorkshopsResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.DocumentTypeResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.InsuredRelationResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceReq
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OfficePersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.WorkshopSpecificationResponse
import com.tamin.taminhamrah.data.remote.models.services.orthosisInfoResponse.InsuredOrthosisInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentLinkResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentRequest
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentUrlRequest
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.ConfirmSurvivorListResponse
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SaveSurvivorInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.SurvivorResponse
import com.tamin.taminhamrah.data.remote.models.services.performedInspections.InspectionResponse
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.CorrectedAccountNumberResponse
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.DeceasedInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.FuneralAllowanceRequest
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.FuneralAllowanceResponse
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.RequestAllowanceFuneralResponse
import com.tamin.taminhamrah.data.remote.models.services.requestPaymentForillDay.RequestPaymentForIllDayResponse
import com.tamin.taminhamrah.data.remote.models.services.requestPaymentForillDay.RequestPaymentForillDayReq
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.CurrentUserResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.LatestInsuranceInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.PregnancyStatusResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.PregnancyTypesResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.RequestForPregnancyPayReq
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.RequestForPregnancyPayResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.AuthenticationResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.ConfirmIdentityAndHistoryInfoRequest
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementConfirmIdentityInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementPersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementRequestInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementSaveDocumentRequest
import com.tamin.taminhamrah.data.remote.models.services.retirementPension.RetirementStatusResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.BranchListResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DependentInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.FamilyRelationShipResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.InquiryEducationCodeResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.InquiryRegistryResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.RequestAddDependent
import com.tamin.taminhamrah.data.remote.models.services.treatmentServices.costs.SendToInboxTreatmentCosts
import com.tamin.taminhamrah.data.remote.models.services.treatmentServices.costs.TreatmentCostsExpensesResponse
import com.tamin.taminhamrah.data.remote.models.services.treatmentServices.deserved.DeservedTreatmentResponse
import com.tamin.taminhamrah.data.remote.models.services.violations.SendReportResponse
import com.tamin.taminhamrah.data.remote.models.services.violations.ViolationRequest
import com.tamin.taminhamrah.data.remote.models.services.violations.ViolationResponse
import com.tamin.taminhamrah.data.remote.models.services.violations.ViolationUploadResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.AllObjectionsResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.ConfirmConflictResponseItem
import com.tamin.taminhamrah.data.remote.models.services.workshop.DebtInstallmentResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.InstallmentListResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.MultipleWorkShopResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.ObjectionType
import com.tamin.taminhamrah.data.remote.models.services.workshop.SmsListResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebtInquiryResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebtResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDemandDocResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopPaymentPreCheckResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopDebitReasonResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopMemberResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopPaymentSheetResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopRecentLyAddedMemberResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopStackHolderResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.Article16RequestInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.RegisterArticle16RequestModel
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.RegisterArticle16Response
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkShopInfoDebtArticle16Response
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkShopListDefinitiveArticle16Response
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkshopsDebtListResponse
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.DeferredInstallmentInfoResponse
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.RequestStatusResponse
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.ResultFollowUpObjectionNonExitsResponse
import com.tamin.taminhamrah.data.remote.models.showRequestInfo.ShortTermRequestInfoResponse
import com.tamin.taminhamrah.data.remote.models.user.ContractListResponse
import com.tamin.taminhamrah.ui.home.services.contracts.model.FractionRequestDataModel
import com.tamin.taminhamrah.ui.home.services.employer.completeInfo.model.TicketRequest
import com.tamin.taminhamrah.ui.home.services.employer.completeInfo.model.VerifyCodeRequest
import com.tamin.taminhamrah.ui.home.services.employer.contract.model.MafasaHesabRequestModel
import com.tamin.taminhamrah.ui.home.services.employer.debt.InstallmentRequestModel
import com.tamin.taminhamrah.ui.home.services.employer.legalStackHolders.model.AgentRequestModel
import com.tamin.taminhamrah.ui.home.services.employer.onlineService.model.AgreementDataModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

interface ServicesRemoteDataSource {

    // suspend fun getServices(token: String): Resource<ServicesResponse?>

    suspend fun getAiLawsSearch(url: String, prompt: String,userName: String?): LawsAiSearchResponse
    suspend fun getVoiceAiLawsSearch(url: String, file: MultipartBody.Part,userName: String?): LawsAiSearchResponse
    suspend fun getAiServiceSearch(url: String,       data: String): AiServiceResponse
    suspend fun getVoiceAiServiceSearch(url: String, file: MultipartBody.Part,  data: String): AiServiceResponse

    suspend fun getPensionInquiry(token: String): Resource<List<PensionInquiryModel>?>
    suspend fun getRecipientList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): RecipientResponse

    @Deprecated("use getServices() instead of getServices(token: String)")
    suspend fun getServices(token: String): ServiceResponseModel

    suspend fun getServices(): ServiceResponseModelNew
    suspend fun getAcraConfig(): AcraConfigResponse
    suspend fun getWeddingPresent(token: String): WeddingPresentResponse
    suspend fun validateMarriageGift(
        token: String,
        date: String,
        nationalCode: String
    ): Resource<String?>

    suspend fun marriageGiftRequest(
        token: String,
        value: ShorttremMariageReq
    ): GeneralRes
    /*  suspend fun getServices(employer: Boolean): Resource<List<ServiceEntity>?>*/

    suspend fun getInsuranceActiveRelation(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ActiveRelationResponse

    suspend fun getReceiverList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ActiveRelationResponse

    suspend fun getIdentityInfo(token: String): IdentityInfoResponse
    suspend fun getUserInfo(token: String): UserInfoResponse
    suspend fun sendBankAccountInfo(
        token: String,
        accountNumberStr: String,
        accountTypeId: String,
        bankId: String,
        startDate: String
    ): GeneralRes

    suspend fun getBankAccountList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): BankAccountResponse

    suspend fun getAllHistoryInsurance(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): AllHistoryResponse

    suspend fun getResultOfInquirePension(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): InquirePensionStatusResponse

    suspend fun getDependantsResponse(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): DependantsResponse

    suspend fun getConfirmationMedicalAuthorities(token: String): MedicalAuthoritiesResponse

    suspend fun getWageAndHistoryInsurance(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WageAndHistoryResponse


    suspend fun getObjectionInsuranceHistory(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ObjectionInsuranceHistoryResponse

    suspend fun getTitlesJob(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): TitlesJobResponse

    suspend fun getProvinceList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ProvinceResponse

    suspend fun getCitiesOfProvince(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): CityResponse

    suspend fun getInfoBranch(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): BranchesInfoListResponse

    suspend fun getCityNameWithPaging(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): CityNameListResponse

    suspend fun getCityName(
        token: String,
        filter: String
    ): CityNameListResponse

    suspend fun getBranchDetailList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): BranchDetailResponse

    suspend fun getInsuranceTypeList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): InsuranceTypeResponce

    suspend fun getInspectionInfo(
        token: String,
        insuranceId: String?,
        inspectionCode: String,
        nationalCode: String
    ): InfoInspectionResponse

    suspend fun getPensionerId(token: String): PensionerIdResponse

    suspend fun getPensionerPayRoll(filter: String, token: String): PayRollResponse
    suspend fun sendPayRollToInbox(filter: String, token: String): GeneralRes


    suspend fun downloadEdictPdf(filter: String, token: String): PdfDownloadResponse
    suspend fun downloadAllHistoryPDF(token: String): PdfDownloadResponse
    suspend fun downloadWageAndHistoryPDF(token: String): PdfDownloadResponse
    suspend fun downloadTalfighiPdf(token: String): PdfDownloadResponse

    suspend fun getViewShorttermRequestList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ViewShortTermRequestResponse

    suspend fun sendCertificateRequest(filter: String, token: String): GeneralRes

    suspend fun sendRequestInquirePensionCertificate(filter: String, token: String): GeneralRes

    suspend fun sendCertificateWage(filter: String, token: String): GeneralRes

    suspend fun getEdictPensioner(filter: String, token: String): EdictPensionerResponse
    suspend fun calculateMarriageAllowance(
        token: String,
        timeStamp: String
    ): CalculateMarriageResponse

    suspend fun calculateWageIllDay(
        token: String,
        StartDateTimeStamp: String,
        EndDateTimeStamp: String,
        marital_status: String
    ): Resource<List<String>?>

    suspend fun calculateWagePregnancyDays(
        token: String,
        StartDateTimeStamp: String,
        EndDateTimeStamp: String
    ): Resource<List<String>?>

    suspend fun getPersonalInfo(token: String): PersonalInfoResponse
    suspend fun getPersonalInfoDetail(token: String): PersonalInfoResponse


    suspend fun checkGirlSurvivorConditions(
        nationalCode: String,
        pensionerId: String,
        token: String
    ): CheckGirlSurvivorConditionsResponse

    suspend fun getGirlSurvivorReport(
        token: String,
        address: String,
        phone: String,
        zipCode: String,
        fatherName: String,
        birthDate: Long,
        insuranceNumber: String,
        nationalCode: String,
        pensionId: String
    ): PdfDownloadResponse

    suspend fun confirmGirlSurvivor(request: ConfirmSurvivorRequest, token: String): GeneralRes

    suspend fun getBeneficiary(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): BeneficiaryResponse

    suspend fun sendRequestDeferredInstallmentCertificate(
        token: String,
        deferredInstallmentRequest: DeferredInstallmentReq
    ): DeferredInstallmentCertificateResponse

    suspend fun getDeservedTreatment(token: String): DeservedTreatmentResponse
    suspend fun checkInsuredInfo(token: String): CheckInsuredInfoResponse

    suspend fun sendInsuranceHistoryToInstitution(
        token: String,
        allHistorySelected: Boolean,
        historyAndWageSelected: Boolean,
        combineHistorySelected: Boolean
    ): SendInsuranceHistoryToInstitutionResponse

    suspend fun sendAllInsuranceHistoryToInstitution(
        token: String,
    ): SendInsuranceHistoryToInstitutionResponse

    suspend fun isMultipleWorkshops(
        token: String,
        branchCode: String,
        insuranceNumber: String
    ): MultipleWorkShopResponse

    suspend fun calculateMultipleWorkshops(
        token: String,
        branchCode: String,
        insuranceNumber: String
    ): MultipleWorkShopResponse

    suspend fun getLastRelation(token: String): LastRelationResponse

    suspend fun getElectronicPrescriptionList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ElectronicPrescriptionResponse

    suspend fun getCombinedRecordList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): CombinedRecordResponse

    suspend fun getElectronicPrescriptionDetail(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ElectronicPrescriptionDetailResponse


    suspend fun getElectronicPrescriptionPrice(
        token: String,
        noteHeadID: String,
        nationalCode: String
    ): ElectronicPrescriptionPriceResponse

    suspend fun getDependantUserUnder18(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): DependantUserUnder18Response

    //RequestForPregnancyPayFragment
    suspend fun sendRequestForPregnancyPay(
        token: String,
        requestForPregnancyPayReq: RequestForPregnancyPayReq
    ): RequestForPregnancyPayResponse


    //Upload Image
    suspend fun uploadImage(
        token: String,
        image: MultipartBody.Part
    ): UploadImageResponse

    //Upload Image
    suspend fun uploadPdfFile(
        token: String,
        image: MultipartBody.Part
    ): GeneralStringRes

    //pregnancy status
    suspend fun getPregnancyStatus(token: String): PregnancyStatusResponse

    suspend fun getWorkshopInfo(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkshopInfoResponse

    suspend fun getInsuredOrthosisInfo(token: String): InsuredOrthosisInfoResponse

    suspend fun getWorkshopMemberList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkshopMemberResponse

    suspend fun getWorkshopStackHolderList(token: String, paramsMap: MutableMap<String, String>?)
            : WorkshopStackHolderResponse

    suspend fun getWorkshopDebtList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkShopDebtResponse

    suspend fun getWorkshopDebtInquiry(
        token: String,
        workshopId: String,
        branchCode: String
    ): WorkShopDebtInquiryResponse

    //pregnancy type
    suspend fun getPregnancyType(token: String): PregnancyTypesResponse

    //latest insurance info
    suspend fun getLatestInsuranceInfo(token: String): LatestInsuranceInfoResponse

    suspend fun getWorkshopDemandDocs(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkShopDemandDocResponse

    suspend fun downloadDebtDocumentPdf(
        token: String,
        debtNumber: String,
        branchCode: String
    ): PdfDownloadResponse

    suspend fun pensionerPayRollPDF(
        token: String,
        filter: String
    ): PdfDownloadResponse

    suspend fun getWorkshopPaymentSheets(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkshopPaymentSheetResponse

    suspend fun saveShortTermOrthosis(
        token: String,
        requestShortTermOrthosis: ShortTermOrthosisReq
    ): ShortTermOrthosisResponse

    suspend fun getWorkshopDebitReasonList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkshopDebitReasonResponse

    suspend fun sendObjectionInsuranceHistory(
        token: String,
        list: List<ObjectionInsuranceHistoryModel>
    ): ResultRequestSaveOfObjectionInsuranceResponse

    suspend fun sendConfirmConflict(
        token: String,
        list: List<ConfirmConflictResponseItem>
    ): SendConfirmConflictResponse

    suspend fun sendFinalConfirmConflict(token: String): FinalConfirmConflictResponse
    suspend fun sendFinalConfirmNotExist(token: String): SendFinalConfirmResponse

    suspend fun checkStatusConflict(token: String): CheckStatusConflictResponse

    suspend fun checkStatusNotExist(token: String): CheckStatusNotExistModel

    suspend fun getBranchDetailListWithBranchCode(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): BranchDetailResponse

    suspend fun getInsuranceTypeListWithInsuracneTypeCode(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): InsuranceTypeResponce

    suspend fun getNotExistRequests(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): NotExistRequestsResponse

    suspend fun sendReqSaveNotExist(
        token: String,
        reqBodySave: BodySaveNonExistentHistory
    ): CheckSaveNotExistModel

    suspend fun deleteNotExist(token: String, reqno: String, id: String): DeleteNotExistResponse

    suspend fun sendConfirmNotExist(
        token: String,
        list: List<ConfirmConflictResponseItem>
    ): SendConfirmNotExistResponse

    suspend fun getDebitPaymentStatus(
        token: String,
        debitNumber: String,
        branchCode: String
    ): WorkShopPaymentPreCheckResponse

    suspend fun normalDebitPayment(
        token: String,
        paymentRequest: PaymentRequest
    ): PaymentResponse

    suspend fun getPaymentInfo(
        token: String,
        url: String
    ): PaymentInfoResponse

    suspend fun cancelPayment(
        token: String,
        url: String
    ): GeneralRes

    suspend fun getPaymentLink(
        token: String,
        url: String,
        body: PaymentUrlRequest
    ): PaymentLinkResponse

    suspend fun getPerformedInspectionList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): InspectionResponse

    suspend fun downloadPerformedInspectionPdf(
        token: String,
        inspectionNumber: String
    ): Resource<Response<ResponseBody?>?>

    suspend fun downloadInspectionPdf(
        token: String,
        inspectionNumber: String
    ): PdfDownloadResponse

    suspend fun getActiveBranch(token: String): BranchListResponse

    suspend fun getWorkshopObjectionableDebitList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkShopDebtResponse

    suspend fun getAllObjections(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): AllObjectionsResponse

    suspend fun getObjectionsSms(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): SmsListResponse

    suspend fun getCovidResult(token: String): CovidResponse

    suspend fun sendRequestForIllDay(
        token: String,
        illDayReq: RequestPaymentForillDayReq
    ): RequestPaymentForIllDayResponse

    suspend fun checkWorkshopDebitObjectionPermission(
        token: String,
        orderRecipeDate: String
    ): GeneralRes

    suspend fun downloadDebitObjectionPDF(
        token: String,
        seqNumber: Long?
    ): PdfDownloadResponse

    suspend fun downloadDebitObjectionReportPDF(
        token: String,
        seqNumber: Long?
    ): PdfDownloadResponse

    suspend fun downloadContractPdf(
        token: String,
        timeStamp: String
    ): PdfDownloadResponse

    suspend fun downloadOptionalContractPdf(
        token: String,
        timeStamp: String
    ): PdfDownloadResponse

    suspend fun downloadFractionContractPdf(
        token: String,
        timeStamp: String
    ): PdfDownloadResponse

    suspend fun downloadInstallmentReport(
        token: String, letterNumber: String
    ): PdfDownloadResponse

    suspend fun getSelectedWorkshopInfo(
        token: String,
        workshopId: String,
        branchCode: String
    ): Resource<WorkshopInfoResponse?>


    suspend fun getObjectionTypeList(token: String): Resource<ObjectionType?>
    suspend fun getInfoFuneral(token: String): FuneralAllowanceResponse

    suspend fun inquiryDeceasedInfo(token: String, nationalCode: String): DeceasedInfoResponse

    suspend fun submitRequestFuneralAllowance(
        token: String,
        funeralGrantReq: FuneralAllowanceRequest
    ): RequestAllowanceFuneralResponse

    suspend fun correctedAccountNumber(
        token: String,
        requestId: String
    ): CorrectedAccountNumberResponse

    suspend fun sendDebitObjection(token: String, request: DebitObjection): DebitObjectionResponse


    suspend fun getSpecialContactList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkshopInfoResponse

    suspend fun getWorkshopDebtInfo(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkShopDebtResponse

    suspend fun getAllInstallmentList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): InstallmentListResponse
//    suspend fun getInquiryCertificate(token:String,nationalId:String,inquiryLicenseCode:String): Resource<String?>


    suspend fun getMyReportsOV(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ViolationResponse

    suspend fun getProvinceListForOV(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ProvinceResponse

    suspend fun sendReportOV(token: String, body: ViolationRequest): SendReportResponse
    suspend fun uploadDocumentOV(token: String, image: MultipartBody.Part): ViolationUploadResponse
    suspend fun getAllDocumentOV(
        token: String,
        id: Int,
        paramsMap: MutableMap<String, String>?
    ): ViolationUploadResponse

    suspend fun getListInspectionPerformed(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): InspectionPerformedResponse

    suspend fun getWorkshopListInspectionPerformed(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): InspectionPerformedResponse

    suspend fun getWorkersPaymentInfo(token: String): WorkersPaymentInfoResponse
    suspend fun getWorkersPayDebit(
        token: String,
        body: WorkersPayDebitRequest,
        type: String = "mobile"
    ): WorkersPayDebitResponse

    // inspectTicket : check pay result
    suspend fun inspectTicket(token: String, ticket: String?, paymentInfo: String?): GeneralRes
    suspend fun getInquiryCertificate(
        token: String,
        nationalId: String,
        inquiryLicenseCode: String
    ): Resource<String?>

    suspend fun getJobTitle(token: String, paramsMap: MutableMap<String, String>?): JobTitleResponse

    suspend fun getContractList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ContractResponseNew

    suspend fun getAssignerContractList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ContractResponse

    suspend fun getComputationalBaseList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ComputationalBaseResponse

    suspend fun sendInspectionRequest(
        token: String,
        submitResponse: SubmitResponse
    ): SubmitInspectionRequestResponse

    suspend fun getProfileInfo(token: String): ProfileResponse
    suspend fun getUserProfileImage(token: String): GeneralStringRes

    //current user
    suspend fun getCurrentUser(token: String): CurrentUserResponse

    suspend fun getElectronicFile(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ElectronicFileResponse

    suspend fun getTreatmentCostsPDF(token: String, repId: String): PdfDownloadResponse

    suspend fun getMyElectronicFileDocumentFullSize(token: String, url: String): PdfDownloadResponse

    suspend fun getTreatmentCosts(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): TreatmentCostsExpensesResponse

    suspend fun checkRedCrossStatus(token: String): RedCrossStatusResponse
    suspend fun checkMedicalStudent(token: String): MedicalStudentResponse


    suspend fun sendToInboxTreatmentCosts(
        token: String,
        repId: String
    ): SendToInboxTreatmentCosts

    suspend fun getDocumentImage(
        token: String,
        id: String?
    ): GeneralRes

    suspend fun sendEdictPensionerToMyInbox(filter: String, token: String): GeneralRes

    suspend fun downloadComputationalBasePdf(
        token: String,
        id: String
    ): Resource<Response<ResponseBody?>?>

    suspend fun getRegistrationInfo(token: String): ConcludingStudentInsuranceContractResponse

    suspend fun calculateFreelanceDebit(token: String, month: Int): CalculateFreelanceDebitResponse

    suspend fun calculateOptionalInsuranceDebitByMonth(
        token: String,
        month: Int
    ): CalculateFreelanceDebitResponse

    suspend fun getCityList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): CityResponse


    suspend fun sendInsuranceRegistration(token: String, request: RegistrationReq): GeneralRes

    suspend fun saveUsersAddressInfo(
        token: String,
        updateAddressInfoRequest: UpdateAddressInfoRequest
    ): GeneralRes

    suspend fun checkAgeAndHistory(token: String): CheckAgeAndHistoryResponse

    suspend fun checkFractionAgeAndHistory(token: String): CheckAgeAndHistoryResponse


    suspend fun checkOptionalAgeAndHistory(token: String): CheckAgeAndHistoryResponse


    suspend fun checkContractStatus(token: String): CheckContractStatusResponse

    suspend fun checkOptionalInsuranceContractStatus(token: String): CheckContractStatusResponse

    suspend fun checkSuccessPaymentStatus(token: String, systemType: String): GeneralRes

    suspend fun getFreelanceLastPayment(token: String): FreelanceLastPaymentResponse

    suspend fun getOptionalInsuranceLastPayment(token: String): OptionalInsuranceLastPaymentResponse

    suspend fun getContractPremiumRate(
        token: String,
        premiumRate: String,
        typePremiumRate: String
    ): ContractPremiumRateResponse

    suspend fun getOptionalContractPremiumRate(
        token: String
    ): ContractPremiumRateResponse

    suspend fun checkAndCalculateSalaryForContract(
        token: String,
        premiumRate: String,
        typePremiumRate: String
    ): CalculateSalary

    suspend fun calculateSalaryForOptionalContract(
        token: String,
        premiumRate: String
    ): CalculateSalary

    suspend fun makeContract(
        token: String,
        selectedSalary: Int,
        finalConfirmRequest: ContractRequest
    ): FinalConfirmResponse

    suspend fun makeFractionContract(
        token: String,
        req: FractionRequestDataModel
    ): FinalConfirmResponse


    suspend fun makeOptionalContract(
        token: String,
        selectedSalary: Int,
        finalConfirmRequest: OptionalContractRequest
    ): FinalConfirmResponse

    suspend fun makeContractByGuardian(
        token: String,
        selectedSalary: Int,
        finalConfirmRequest: ContractByGuardianRequest
    ): FinalConfirmResponse

    suspend fun makeOptionalContractByGuardian(
        token: String,
        selectedSalary: Int,
        finalConfirmRequest: OptionalContractByGuardian
    ): FinalConfirmResponse


    suspend fun updateContract(
        token: String,
        premium: String,
        body: UpdateContractRequest
    ): GeneralRes

    suspend fun updateOptionalContract(
        token: String,
        premium: String,
        body: UpdateOptionalContract
    ): GeneralRes


    suspend fun updateGuardianOptionalContract(
        token: String,
        premium: String,
        body: UpdateGuardianOptionalContract
    ): GeneralRes

    suspend fun updateGuardianContract(
        token: String,
        premium: String,
        body: UpdateGuardianContract
    ): GeneralRes

    suspend fun insurancePayment(
        token: String,
        startDate: Long?,
        endDate: Long?,
        amount: Long?,
        systemType: String?,
        paramPage: String?,
        month: Int?,
        redirectUrl: String
    ): PaymentResponse

    suspend fun getPremiumOptions(token: String): ContractPremiumOptionsResponse

    suspend fun getCancelContractReasons(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): CancelContractReasonsResponse

    suspend fun cancelContractRequest(
        token: String,
        contractNumber: String,
        body: CancelContractRequest
    ): GeneralRes

    suspend fun cancelOptionalContractRequest(
        token: String,
        contractNumber: String,
        body: CancelContractRequest
    ): GeneralRes

    suspend fun getContractsPaymentsListFreelance(
        token: String,
        contractNumber: String
    ): PaymentListDefaultResponse

    suspend fun getPaymentCalculationDetailList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): PaymentListDefaultResponse


    suspend fun postNewInsuredInfo(
        token: String,
        dataModel: NewInsuredUserInfoReq?
    ): NewInsuredUserInfoResponse

    suspend fun updateNewInsuredInfo(
        token: String,
        requestId: Long?,
        dataModel: NewInsuredUserInfoReq?
    ): NewInsuredUserInfoResponse

    suspend fun getInsuredRegistrationDocList(
        token: String,
        personalId: Long?,
    ): InsuredDocsResponse

    suspend fun putInsuredRegistrationDocList(
        token: String,
        personalId: String?,
        list: ArrayList<InsuredDoc>
    ): GeneralRes

    suspend fun checkUserIsNew(token: String, nationalId: String?): NewInsuredUserStatusResponse
    suspend fun getRequestSummary(token: String, requestId: Long?): NewInsuredSummaryResponse

    suspend fun deleteRecentlyAddedUser(
        token: String,
        personalId: Long?
    ): GeneralRes

    suspend fun confirmRecentlyAddedUser(
        token: String,
        requestId: Long?
    ): ConfirmUserResponse

    suspend fun getWorkshopRecentlyAddedMembers(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkshopRecentLyAddedMemberResponse

    suspend fun getRecentlyAddedUser(
        token: String,
        personalRequestId: Long?
    ): WorkshopRecentLyAddedMemberResponse

    suspend fun getEmployerAgreementInfoList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): EmployerAgreementResponse

    suspend fun sendCommitmentRequest(
        token: String,
        mobile: String,
        email: String,
        serviceName: String
    ): GeneralRes

    suspend fun sendVerificationCode(token: String, verifyCode: String): EmployerCommitmentResponse

    suspend fun sendVerificationCode(token: String, verifyCode: VerifyCodeRequest): GeneralRes

    suspend fun getWorkshopsInfoWithoutContract(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkshopInfoWithoutContractResponse

    suspend fun getWorkshopContactList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkshopContractListResponse

    suspend fun getEmployerWorkshopList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): EmployerWorkshopResponse

    suspend fun postEmployerAgreement(token: String, body: AgreementDataModel): GeneralRes

    suspend fun getLegalWorkshopInfo(
        token: String,
        legalWorkshopID: String?
    ): LegalWorkshopInfoResponse

    suspend fun getLegalWorkshopCEOInfo(
        token: String,
        nationalCode: String?,
        birthdate: String?
    ): LegalWorkshopCEOInfoResponse

    suspend fun getVerificationTicketForLegalWorkshopInfo(
        token: String,
        mobileNumber: String?,
        email: String?,
        nationalCode: String?
    ): GeneralRes

    suspend fun sendVerifyTicketForLegalWorkshop(token: String, body: TicketRequest): GeneralRes


    suspend fun getFreelancerJobTitle(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): FreelancerJobTitlesResponse

    suspend fun getDetailPaymentFreelance(
        token: String,
        contractNumber: String,
        debitNumber: String
    ): DetailPaymentListDefaultResponse


    suspend fun getContractInsuranceList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ContractListResponse


    suspend fun getOptionalInsurancePaymentCalculationDetailList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): PaymentListDefaultResponse

    suspend fun getLegalStackHolderList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): LegalStackHolderResponse

    suspend fun requestLegalStackHolderTicket(
        token: String,
        nationalCode: String?
    ): GeneralRes

    suspend fun verifyLegalStackHolderTicket(
        token: String,
        ticket: String
    ): GeneralRes

    suspend fun getLegalAgentList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): LegalStackHolderResponse

    suspend fun submitNewLegalAgent(
        token: String,
        ticket: String,
        request: AgentRequestModel
    ): GeneralRes

    suspend fun deleteLegalAgent(
        token: String,
        ticket: String?,
        stackId: Long?
    ): GeneralRes

    suspend fun getFamilyRelationShips(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): FamilyRelationShipResponse

    suspend fun getDependentInfo(token: String): DependentInfoResponse

    suspend fun inquiryRegistryInfo(
        token: String,
        nationalCode: String,
        timeStampBirthDay: String,
        dependencyCode: String
    ): InquiryRegistryResponse

    suspend fun inquiryEducationCode(
        token: String,
        nationalId: String,
        inquiryLicenseCode: String
    ): InquiryEducationCodeResponse

    suspend fun addNewDependent(token: String, requestBody: RequestAddDependent): GeneralRes

    suspend fun refreshDependent(token: String): GeneralRes

    suspend fun checkRenewCondition(token: String): DependentInfoResponse

    suspend fun inquiryStudyCodeCertificate(
        token: String,
        code: String,
        studyCode: String
    ): GeneralStringRes

    suspend fun getMafasaHesabContractSubjects(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): MafasaHesabContractSubjectResponse

    suspend fun sendMafasaHesabRequest(
        token: String,
        id: String,
        body: MafasaHesabRequestModel
    ): GeneralRes

    suspend fun getDisabilityDependentInfo(token: String): DisabilityDependentResponse

    suspend fun getDisabilityPersonalInfo(token: String): DisabilityPersonalInfoResponse

    suspend fun saveDisabilityUserInfo(
        token: String,
        body: DisabilitySaveInfoRequest
    ): DisabilitySaveInfoResponse

    suspend fun getUserAge(token: String, birthDate: Long): AgeResponse

    suspend fun getMedicalCommissionPdf(token: String, lastWorkShop: String): PdfDownloadResponse

    suspend fun finalConfirmDisabilityRequest(
        token: String,
        requestId: Long,
        body: DisabilityFinalConfirmRequest
    ): DisabilitySaveInfoResponse

    suspend fun saveDocumentDisability(
        token: String,
        requestId: Long,
        body: DisabilitySaveDocumentRequest
    ): GeneralRes

    suspend fun getRegistrationDeclarationForm(token: String): PdfDownloadResponse

    suspend fun getClause38List(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): Clause38Response

    suspend fun getClause38Detail(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): Clause38DetailResponse

    suspend fun getDebtDiscount(
        token: String,
        branchCode: String,
        debitNumber: String,
        debitRemain: Long
    ): DebtDiscountResponse

    suspend fun installmentDebt(
        token: String,
        request: InstallmentRequestModel
    ): DebtInstallmentResponse

    suspend fun getLetterSubject(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): LetterSubjectResponse

    suspend fun deleteExistLetter(
        token: String,
        letterRequestId: Long?, workshopId: String?
    ): GeneralRes

    suspend fun registerLetter(
        token: String,
        dataModel: RegisterLetterRequest?
    ): RegisterLetterResponse

    suspend fun getWorkshopListDefinitiveDebt(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkShopListDefinitiveArticle16Response

    suspend fun getWorkshopsDebtsList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkshopsDebtListResponse

    suspend fun getWorkshopInfoDebtArticle16(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): WorkShopInfoDebtArticle16Response

    suspend fun registrationRequestArticle16(
        token: String,
        body: RegisterArticle16RequestModel
    ): RegisterArticle16Response

    suspend fun getRequestInfoArticle16(
        token: String,
        objectionNumber: Long
    ): Article16RequestInfoResponse

    suspend fun approveDistantLetter(
        token: String,
        workshopId: String?,
        requestId: Long?
    ): LetterListResponse

    suspend fun getShortTermRequestStatus(token: String, referenceId: String): RequestStatusResponse

    suspend fun getShortTermRequestInfo(
        token: String,
        referenceId: String
    ): ShortTermRequestInfoResponse

    suspend fun getDocument(token: String, guid: String): DownloadFileResponse
    suspend fun sendDistantCorrespondenceRequest(
        token: String,
        dataModel: RegisterLetterRequest
    ): RegisterLetterResponse

    suspend fun getDistantCorrespondenceList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): LetterListResponse


    suspend fun getDeceasedInfo(
        token: String,
        nationalCode: String
    ): com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.DeceasedInfoResponse

    suspend fun getSurvivorList(token: String, deceasedNationalId: String): SurvivorResponse

    suspend fun saveSurvivorInfo(token: String, body: SaveSurvivorInfoRequest): GeneralRes

    suspend fun confirmSurvivorsList(token: String): ConfirmSurvivorListResponse

    suspend fun getFinalSurvivorPensionPDF(token: String): PdfDownloadResponse

    suspend fun submitFinalSurvivorPension(token: String, requestId: Int): GeneralRes

    suspend fun getAge(token: String, birthDate: Long): AgeResponse


    suspend fun checkRetirementStatus(
        token: String
    ): RetirementStatusResponse

    suspend fun getAuthenticationCode(
        token: String
    ): AuthenticationResponse

    suspend fun getUserAge(
        token: String,
        birthDate: Long?
    ): AgeResponse

    suspend fun authenticationAndGetPersonalInfo(
        token: String,
        authenticationsCode: Long
    ): RetirementPersonalInfoResponse

    suspend fun confirmIdentityAndHistoryInfo(
        token: String,
        authenticationsCode: Long,
        body: ConfirmIdentityAndHistoryInfoRequest
    ): RetirementConfirmIdentityInfoResponse

    suspend fun getRetirementRequestInfo(
        token: String,
        filter: String
    ): RetirementRequestInfoResponse


    suspend fun sendRetirementDocument(
        token: String,
        requestId: String,
        body: RetirementSaveDocumentRequest
    ): GeneralRes

    suspend fun getDeferredInstallmentInfo(
        token: String,
        requestId: String
    ): DeferredInstallmentInfoResponse

    suspend fun getPrescriptionPdfFile(token: String, prescriptionID: String): PdfDownloadResponse

    suspend fun getConstructionFiles(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): ConstructionFileResponse

    suspend fun getDetailConstructionFile(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): DetailConstructionInfoResponse

    suspend fun getBeneficiariesWorkshop(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): BeneficiariesConstructionResponse

    suspend fun getPaymentSheetConstructionInfo(
        token: String,
        debitNumber: String
    ): PaymentSheetConstructionFilesResponse

    suspend fun getCertificatePaymentSheetPDF(
        token: String,
        debitNumber: String,
        branchCode: String
    ): PdfDownloadResponse

    suspend fun issuancePaymentSheet(token: String, debitNumber: String): GeneralStringRes
    suspend fun getInstallmentLetterList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): InstallmentLetterListResponse

    suspend fun getDetailDebitList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): InstallmentDebitListResponse

    suspend fun getInstallmentConstructionList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): InstallmentConstructionListResponse

    suspend fun getFollowUpResultObjectionNonExistsHistory(
        token: String,
        referenceId: String
    ): ResultFollowUpObjectionNonExitsResponse


    // suspend fun getPaymentSheetInstallmentConstruction(token: String,debitNumber:String,oldDebitNumber: String): GeneralStringRes
    //  suspend fun updateInstallmentConstruction(token: String,oldDebitNumber:String): GeneralStringRes

    suspend fun getPaymentDebitList(
        token: String,
        debtNumber: String,
        branchCode: String
    ): InstallmentPaymentResponse

    suspend fun getPaymentDebitToken(token: String, debtNumber: String): PaymentResponse
    suspend fun checkPaymentDebt(token: String, debtSerialNumber: String): GeneralRes
    suspend fun getDebtPaidList(token: String, debtSerialNumber: String): DebtPaidListResponse
    suspend fun getRegisteredMedicalCommission(token: String): RegisteredMedicalCommissionResponse
    suspend fun getInsuredRelation(token: String, nationalCode: String?): InsuredRelationResponse
    suspend fun getAllWorkshops(token: String, nationalCode: String?): AllWorkshopsResponse
    suspend fun getAllWorkshopHistory(
        token: String,
        workshopCode: String?,
        nationalCode: String?,
        branchCode: String?,
        insuranceNumber: String?,
        occurrenceDate: String?
    ): GeneralRes

    suspend fun getWorkshopSpecification(
        token: String,
        workshopCode: String?,
        branchCode: String?
    ): WorkshopSpecificationResponse

    suspend fun getOfficePersonalInfo(
        token: String,
        nationalCode: String?,
        birthDate: Long?,
        workshopCode: String?,
        branchCode: String?
    ): OfficePersonalInfoResponse

    suspend fun getDocumentType(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): DocumentTypeResponse

    suspend fun uploadOccurrenceImage(
        token: String,
        image: MultipartBody.Part
    ): UploadImageResponse

    suspend fun sendOccurrenceRequest(
        token: String,
        request: OccurrenceReq
    ): OccurrenceResponse

    suspend fun downloadTestResultPdf(
        token: String, patientID: String, noteHeadEprescID: String, currentUserNationalCode: String
    ): PdfDownloadResponse

    suspend fun getMedicalMissionList(
        token: String,
        paramsMap: MutableMap<String, String>?
    ): MedicalMissionResponse


    suspend fun dependentCancellation(
        token: String,
        action: String,
        identifier: String,
        date: String
    ): GeneralRes
}



