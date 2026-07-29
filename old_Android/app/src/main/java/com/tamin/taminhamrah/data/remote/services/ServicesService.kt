package com.tamin.taminhamrah.data.remote.services

import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.BuildConfig.BASE_URL_OV
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.entity.DownloadFileResponse
import com.tamin.taminhamrah.data.entity.PensionInquiryModel
import com.tamin.taminhamrah.data.remote.models.BaseListResponse
import com.tamin.taminhamrah.data.remote.models.BaseResponse
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
import com.tamin.taminhamrah.data.remote.models.profile.ProfileResponse
import com.tamin.taminhamrah.data.remote.models.responses.CalculateMarriageResponse
import com.tamin.taminhamrah.data.remote.models.services.AcraConfigResponse
import com.tamin.taminhamrah.data.remote.models.services.ActiveRelationResponse
import com.tamin.taminhamrah.data.remote.models.services.AgeResponse
import com.tamin.taminhamrah.data.remote.models.services.AllHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.BankAccountReq
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
import com.tamin.taminhamrah.data.remote.models.services.MarriageGiftReq
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
import com.tamin.taminhamrah.data.remote.models.services.SentryConfig
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
import com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.RequestModel
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
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ServicesService {

    @FormUrlEncoded
    @POST
    suspend fun getAiLawsSearch(
        @Url url: String,
        @Field("prompt") prompt: String,
        @Field("user_name") userName: String?,
    ): LawsAiSearchResponse

    @FormUrlEncoded
    @POST
    suspend fun getAiServiceSearch(
        @Url url: String,
        @Field("data") data: String,
    ): AiServiceResponse

    @POST
    @Multipart
    suspend fun getVoiceAiLawsSearch(
        @Url url: String,
        @Part file: MultipartBody.Part,
        @Query("user_name") userName: String?,
    ): LawsAiSearchResponse


    @POST
    @Multipart
    suspend fun getVoiceAiServiceSearch(
        @Url url: String,
        @Part file: MultipartBody.Part,
        @Part("data") data: String?,
    ): AiServiceResponse


    @GET("pension-inquiry")
    suspend fun getPensionInquiry(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: Int = Constants.QUERY_PAGE_SIZE_10,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<BaseResponse<BaseListResponse<PensionInquiryModel>?>?>?


    @GET("recipients")
    suspend fun getRecipientList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<RecipientResponse?>?

    @GET("shortterm-request/getNoPresenceLoadData")
    suspend fun getWeddingPresent(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<WeddingPresentResponse?>?

    @Headers("Cache-control: no-cache")
    @GET("menu-data")
    suspend fun getServices(@Header(Constants.AUTHENTICATION) token: String): Response<ServiceResponseModel?>?


    @GET
    suspend fun getDirectServices(@Url string: String): Response<ServiceResponseModelNew?>?


    @GET
    suspend fun getAcraConfig(@Url string: String): Response<AcraConfigResponse?>?

    @GET("shortterm/validateMariageNoPresence/{date}/{nationalCode}")
    suspend fun validateMarriageGift(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("date") date: String,
        @Path("nationalCode") nationalCode: String
    ): Response<BaseResponse<String?>?>?


    @GET("users/current-user")
    suspend fun getProfileInfo(
        @Header(Constants.AUTHENTICATION) token: String,
    ): Response<ProfileResponse?>

    @GET("booklet-req/profile-image")
    suspend fun getUserProfileImage(@Header(Constants.AUTHENTICATION) token: String): Response<GeneralStringRes?>?

    //    @FormUrlEncoded
    @POST("stp-no-presence/saveShorttremMariage")
    suspend fun marriageGiftRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("nationalCode") nationalCode: String,
        @Body shortTermRequest: MarriageGiftReq,
        @Query("timeStamp") timeStamp: Long
    ): Response<BaseResponse<Any?>?>?

    @GET("relation-tamins/all")
    suspend fun getInsuranceActiveRelation(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") PageSize: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<ActiveRelationResponse?>

    @GET("central-reg/personal")
    suspend fun getIdentityInfo(
        @Header(Constants.AUTHENTICATION) token: String,
    ): Response<IdentityInfoResponse?>?

    @GET("history-services/historyinfos")
    suspend fun getAllHistoryInsurance(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<AllHistoryResponse?>?


    @GET("pension-inquiry")
    suspend fun getResultOfInquirePension(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<InquirePensionStatusResponse?>?

    @GET("shortterm-request/ArutzView")
    suspend fun getDependantsResponse(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
    ): Response<DependantsResponse?>?

    //http://172.16.13.156:4200/#/stp/commission-confirmation
    @GET("shortterm-request/commission-confrimation")
    suspend fun getConfirmationMedicalAuthorities(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<MedicalAuthoritiesResponse?>?

    @GET("proxy/models/province")
    suspend fun getProvinceList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<ProvinceResponse?>?

    @GET("special-insured-services/cities")
    suspend fun getCitiesOfProvince(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "20",//Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<CityResponse?>?

    @GET("special-insured-services/branches")
    suspend fun getInfoBranch(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "20",//Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<BranchesInfoListResponse?>?


    @GET("history-services/dastmozdinfos")
    suspend fun getWageAndHistoryInsurance(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WageAndHistoryResponse?>?


    @GET("historyprotest-services/conflicthistories")
    suspend fun getObjectionInsuranceHistory(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<ObjectionInsuranceHistoryResponse?>?


    @GET("history-services/userinfos")
    suspend fun getUserInfo(
        @Header(Constants.AUTHENTICATION) token: String,
    ): Response<UserInfoResponse?>?

    // @FormUrlEncoded
    @POST("accounts")
    suspend fun sendBankAccountInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body bankAccountReq: BankAccountReq

    ): Response<GeneralRes?>?

    @GET("personals/accounts")
    suspend fun getBankAccountList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") pageSize: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<BankAccountResponse?>?

    @GET("pensioner-no")
    suspend fun getPensionerId(
        @Header(Constants.AUTHENTICATION) token: String,
    ): Response<PensionerIdResponse?>?

    @GET("fish")
    suspend fun getPensionerPayRoll(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String,
    ): Response<PayRollResponse?>?

    @GET("fish/annoncment")
    suspend fun sendPayRollToInbox(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String,
    ): Response<GeneralRes?>?

    @GET("history-services/historyjobinfos")
    suspend fun getTitlesJob(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<TitlesJobResponse?>?


    //response of this API used for chart====>can not use lazy load
    @GET("history-services/talfighinfos")
    suspend fun getCombinedRecordList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<CombinedRecordResponse?>?

    //response of this API used for chart====>can not use lazy load
    @GET("historyreport-services/talfigh")
    suspend fun downloadTalfighiPdf(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<ResponseBody?>

    @GET("shortterm-request/allRequests")
    suspend fun getViewShorttermRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") pageSize: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<ViewShortTermRequestResponse?>?

    @Streaming
    @GET("historyreport-services/year")
    suspend fun downloadAllHistoryPDF(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<ResponseBody?>

    @Streaming
    @GET("historyreport-services/dastmozd")
    suspend fun downloadWageAndHistoryPDF(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<ResponseBody?>

    @Streaming
    @GET("hokm/report")
    suspend fun downloadEdictPDF(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String,
    ): Response<ResponseBody?>

    @GET("status-certificate/report")
    suspend fun sendCertificateRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String
    ): Response<GeneralRes?>?

    /*
        @GET
        suspend fun calculateMarriage(
            @Header(Constants.AUTHENTICATION) token: String,
            @Url calculateMarriageRequest: String
        ): Response<BaseResponse<List<String>?>?>?
    */

    @GET("shortterm-request/calcMarriage/{timeStamp}")
    suspend fun calculateMarriage(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("timeStamp") date: String,
    ): Response<CalculateMarriageResponse?>?

    @GET("shortterm-request/calcIllness/{StartDateTimeStamp}/{EndDateTimeStamp}/{marital_status}")
    suspend fun calculateWageIllDay(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("StartDateTimeStamp") StartDateTimeStamp: String,
        @Path("EndDateTimeStamp") EndDateTimeStamp: String,
        @Path("marital_status") marital_status: String
    ): Response<BaseResponse<List<String>?>?>?

    @GET("shortterm-request/calcPregnancy/{StartDateTimeStamp}/{EndDateTimeStamp}")
    suspend fun calculateWagePregnancyDays(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("StartDateTimeStamp") StartDateTimeStamp: String,
        @Path("EndDateTimeStamp") EndDateTimeStamp: String
    ): Response<BaseResponse<List<String>?>?>?

    @GET("survivor-request/personal")
    suspend fun getPersonalInfo(
        @Header(Constants.AUTHENTICATION) token: String,
    ): Response<PersonalInfoResponse?>?


    @GET("multiple-workshops/personal-info")
    suspend fun getPersonalInfoDetail(
        @Header(Constants.AUTHENTICATION) token: String,
    ): Response<PersonalInfoResponse?>?


    @GET("survivor-request/condition")
    suspend fun checkGirlSurvivorConditions(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("code") nationalCode: String,
        @Query("rel") relation: String = "04",
        @Query("pensionerId") pensionerId: String,
    ): Response<CheckGirlSurvivorConditionsResponse?>?

    @GET("survivor-request/report")
    suspend fun getGirlSurvivorReport(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("address") address: String,
        @Query("tel") phone: String,
        @Query("postalCode") zipCode: String,
        @Query("fatherName") fatherName: String,
        @Query("birthDate") birthDate: Long,
        @Query("insuranceId") insuranceNumber: String,
        @Query("parentCode") nationalCode: String,
        @Query("pensionerId") pensionId: String
    ): Response<ResponseBody?>

    @POST("female-request")
    suspend fun confirmGirlSurvivor(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body request: ConfirmSurvivorRequest
    ): Response<GeneralRes?>?

    @GET("hokm")
    suspend fun getEdictPensioner(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String
    ): Response<EdictPensionerResponse?>?

    @GET("beneficiary")
    suspend fun getBeneficiary(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") pageSize: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<BeneficiaryResponse?>?

    @GET("certificate/report")
    suspend fun sendCertificateWage(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String,
    ): Response<GeneralRes?>?

    @POST("wage-assignment")
    suspend fun sendRequestDeferredInstallmentCertificate(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body deferredInstallmentRequest: DeferredInstallmentReq
    ): Response<DeferredInstallmentCertificateResponse?>?

    @POST("stp-no-presence/saveShorttremMariage")
    suspend fun marriageGiftRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body shortTermRequest: ShorttremMariageReq
    ): Response<GeneralRes?>?

    @GET("booklet-req/deserve")
    suspend fun getDeservedTreatment(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") requestPage: Int = 1,
        @Query("start") start: Int = 0,
        @Query("limit") queryPageSize: Int = Constants.QUERY_PAGE_SIZE_10,
        @Query("sort") sort: String = "[]",
    ): Response<DeservedTreatmentResponse?>?

    @GET("historyreport-services/sendinstitution")
    suspend fun sendInsuranceHistoryToInstitution(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("type1") type1: Boolean = false,
        @Query("type2") type2: Boolean = false,
        @Query("type3") type3: Boolean = false
    ): Response<SendInsuranceHistoryToInstitutionResponse?>?

    @GET("historyreport-services/sendeblagh")
    suspend fun sendAllInsuranceHistoryToInstitution(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("type") type: Int = 1,
    ): Response<SendInsuranceHistoryToInstitutionResponse?>?

    @GET("multiple-workshops/is-multiple")
    suspend fun isMultipleWorkshops(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("branchCode") branchCode: String,
        @Query("insuranceNumber") insuranceNumber: String
    ): Response<MultipleWorkShopResponse?>?

    @GET("multiple-workshops/calc")
    suspend fun calculateMultipleWorkshops(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("branchCode") branchCode: String,
        @Query("insuranceNumber") insuranceNumber: String
    ): Response<MultipleWorkShopResponse?>?


    @GET("relation-tamins/last-relation")
    suspend fun getLastRelation(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<LastRelationResponse?>?

    @GET("patient-history/{nationalCode}/{dependantUserNationalCode}/{requestType}/{startDate}/{endDate}")
    suspend fun getElectronicPrescriptionList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("requestType") requestTypeId: String,
        @Path("nationalCode") nationalCode: String,
        @Path("dependantUserNationalCode") dependantUserNationalCode: String,
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String,
        @Query("page") requestPage: String,
        @Query("start") start: String,
        @Query("limit") queryPageSize: String,
        @Query("sort") sort: String = "[]",
    ): Response<ElectronicPrescriptionResponse?>?

    @GET("patient-history/detail/{noteHeadID}/{nationalCode}/{childNationalCode}/{type}/{flagSata}")
    suspend fun getElectronicPrescriptionDetail(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("noteHeadID") noteHeadID: String,
        @Path("nationalCode") nationalCode: String,
        @Path("childNationalCode") childNationalCode: String,
        @Path("flagSata") flagSata: String,
        @Path("type") type: String,
        @Query("page") requestPage: String,
        @Query("start") start: String,
        @Query("limit") queryPageSize: String,
        @Query("sort") sort: String = "[]",
    ): Response<ElectronicPrescriptionDetailResponse?>?

    @GET("patient-history/price/{noteHeadID}/{nationalCode}")
    suspend fun getElectronicPrescriptionPrice(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("noteHeadID") noteHeadID: String,
        @Path("nationalCode") nationalCode: String,
        @Query("page") requestPage: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") queryPageSize: Int = Constants.QUERY_PAGE_SIZE_10,
        @Query("sort") sort: String = "[]",
    ): Response<ElectronicPrescriptionPriceResponse?>?

    @GET("patient-history/get-dependent-children/{nationalCode}")
    suspend fun getDependantUnder18(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("nationalCode") nationalCode: String,
        @Query("page") requestPage: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") queryPageSize: Int = Constants.QUERY_PAGE_SIZE_60,
        @Query("sort") sort: String = "[]",
    ): Response<DependantUserUnder18Response?>?

    @GET("proxy/models/city/")
    suspend fun getCityName(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String? = "1",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String? = "[]",
        @Query("start") start: String? = "0",
        @Query("limit") limit: String = "20",//Constants.QUERY_PAGE_SIZE_10.toString()
    ): Response<CityNameListResponse?>?


    @GET("workshop-services/employer/get-all-workshops")
//    @GET("workshop-services/special-contracts")
    suspend fun getWorkshopInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkshopInfoResponse?>?

    @GET("workshop-services/member/get-all")
    suspend fun getWorkshopMembers(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkshopMemberResponse?>?

    @GET("workshop-services/workshop-stackholders/get-all")
    suspend fun getWorkshopStackHolders(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkshopStackHolderResponse?>?

    //upload image
    @Multipart
    @POST("upload-image")
    suspend fun uploadImage(
        @Header(Constants.AUTHENTICATION) token: String,
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse?>?

    //upload image
//    @Headers("Content-Type: application/pdf")
    @Multipart
    @POST("requestissuanceinvoices38/persistPdf-request-issuance-invoices38")
    suspend fun uploadPdfFile(
        @Header(Constants.AUTHENTICATION) token: String,
        @Part image: MultipartBody.Part
    ): Response<GeneralStringRes?>?

    //pregnancy status
    @GET("StpBaseinfo/ShorttermBarTypes")
    suspend fun getPregnancyStatus(@Header(Constants.AUTHENTICATION) token: String): Response<PregnancyStatusResponse?>?

    //pregnancy types
    @GET("StpBaseinfo/ShorttermBarChild")
    suspend fun getPregnancyType(@Header(Constants.AUTHENTICATION) token: String): Response<PregnancyTypesResponse?>?

    @GET("special-insured-services/list-contracts-mobile")
    suspend fun getContractInsuranceList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<ContractListResponse?>?

    //pregnancy types
    @GET("shortterm-request/getRequestInsuredMainInfo")
    suspend fun getLatestInsuranceInfo(@Header(Constants.AUTHENTICATION) token: String): Response<LatestInsuranceInfoResponse?>?

    //request for pregnancy pay
    @POST("stp/saveShorttremPragnent")
    suspend fun sendRequestForPregnancyPay(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body requestForPregnancyPayReq: RequestForPregnancyPayReq
    ): Response<RequestForPregnancyPayResponse?>?

    @GET("shortterm-request/getRequestInsuredMainInfo/Orthosis")
    suspend fun getInsuredOrthosisInfo(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<InsuredOrthosisInfoResponse?>?

    //    @GET("debit-objection/detail-objection-workshop-debit/{workshopId}/{branchCode}") ==>مشاهده جزییات محاسبه گردش حساب بدهی
    @GET("debit-online-payment/workshop-debit/{workshopId}/{branchCode}") // ==>درخواست پرداخت غیرحضوری بدهی های ابلاغ شده
    suspend fun getWorkshopDebtList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkShopDebtResponse?>?

    @GET("workshop-services/workshop-debit/{workshopId}/{branchCode}")
    suspend fun getWorkshopDebtInquiry(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String
    ): Response<WorkShopDebtInquiryResponse?>?

    @GET("debit-objection/eclaim-detail-objection-workshop-debit/{debitNumber}/{branchCode}")
    suspend fun getWorkshopDebtDocumentList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("debitNumber") debitNumber: String,
        @Path("branchCode") branchCode: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkShopDemandDocResponse?>?

    @GET("debit-objection-reports/gardesh-list/{debitNumber}/{branchCode}")
    suspend fun downloadDebtDocumentPdf(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("debitNumber") debitNumber: String,
        @Path("branchCode") branchCode: String,
    ): Response<ResponseBody?>

    @Streaming
    @GET("fish/report")
    suspend fun pensionerPayRollPDF(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String
    ): Response<ResponseBody?>


    @POST("stp/saveShorttremOrthosis")
    suspend fun saveShorttermOrthosis(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body shortTermOrthosisReq: ShortTermOrthosisReq
    ): Response<ShortTermOrthosisResponse?>?

    @GET("workshop-services/payment-sheets")
    suspend fun getWorkshopPaymentSheets(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkshopPaymentSheetResponse?>?


    @GET("debit-reason")
    suspend fun getDebitReasonList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkshopDebitReasonResponse?>?


    @GET("login-services/logininfo")
    suspend fun checkInsuredInfo(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<CheckInsuredInfoResponse?>

    @GET("historyprotest-services/checkstatusnotexist")
    suspend fun checkStatusNotExist(@Header(Constants.AUTHENTICATION) token: String): Response<CheckStatusNotExistModel?>


    @POST("historyprotest-services/saveconflict")
    suspend fun sendObjectionInsuranceHistory(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body listOfObjection: List<ObjectionInsuranceHistoryModel>
    ): Response<ResultRequestSaveOfObjectionInsuranceResponse?>?

    @POST("historyprotest-services/confirmconflict")
    suspend fun sendConfirmConflict(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body list: List<ConfirmConflictResponseItem>
    ): Response<SendConfirmConflictResponse?>?


    @POST("historyprotest-services/finalconfirmconflict")
    suspend fun sendFinalConfirmConflict(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body body: String = ""
    ): Response<FinalConfirmConflictResponse?>?

    @GET("historyprotest-services/checkstatusconflict")
    suspend fun checkStatusConflict(@Header(Constants.AUTHENTICATION) token: String): Response<CheckStatusConflictResponse?>?

    @GET("debit-online-payment/debit-select-pre-check/{debitNumber}/{branchCode}/1")
    suspend fun getDebitPaymentStatus(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("debitNumber") debitNumber: String,
        @Path("branchCode") branchCode: String,
    ): Response<WorkShopPaymentPreCheckResponse?>?


    @POST("debit-online-payment/pay-normal-debit")
    suspend fun normalDebitPayment(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body body: PaymentRequest
    ): Response<PaymentResponse?>?

    @GET
    suspend fun getPaymentInfo(
//        @Header("Cookie") sessionIdAndToken:String,
        @Header(Constants.AUTHENTICATION) token: String,
        @Url paymentUrl: String
    ): Response<PaymentInfoResponse?>?

    @GET
    suspend fun cancelPayment(
        @Header(Constants.AUTHENTICATION) token: String,
        @Url cancelUrl: String
    ): Response<GeneralRes?>?

    /*
        @GET
        suspend fun getPaymentLink(
            @Header(Constants.AUTHENTICATION) token: String,
            @Url paymentUrl: String
        ): Response<PaymentLinkResponse?>?
    */

    @POST
    suspend fun getPaymentLink(
        @Header(Constants.AUTHENTICATION) token: String,
        @Url paymentUrl: String,
        @Body body: PaymentUrlRequest
    ): Response<PaymentLinkResponse?>?


    @GET
    suspend fun getMyElectronicFileDocumentFullSize(
        @Header(Constants.AUTHENTICATION) token: String,
        @Url url: String
    ): Response<ResponseBody?>?

    @GET("reports/my-reports")
    suspend fun getMyReportsOV(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
        @Url url: String = BASE_URL_OV
    ): Response<ViolationResponse?>?

    @GET("province/get-all")
    suspend fun getProvinceListForOV(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
        @Url url: String = BASE_URL_OV
    ): Response<ProvinceResponse?>?

    @POST("reports/save")
    suspend fun sendReportOV(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body body: ViolationRequest,
        @Url url: String = BASE_URL_OV
    ): Response<SendReportResponse?>?

    @POST("report-documents/upload")
    @Multipart
    suspend fun uploadDocumentOV(
        @Header(Constants.AUTHENTICATION) token: String,
        @Part image: MultipartBody.Part,
        @Url url: String = BASE_URL_OV
    ): Response<ViolationUploadResponse?>?


    @GET("report-documents/get-all/{violation_id}")
    suspend fun getAllDocumentOV(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("violation_id") id: Int,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
        @Url url: String = BASE_URL_OV
    ): Response<ViolationUploadResponse?>?


    @GET("proxy/models/branch")
    suspend fun getBranchDetailList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<BranchDetailResponse?>?

    @GET("proxy/models/insurance-type")
    suspend fun getInsuranceTypeList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<InsuranceTypeResponce?>?

    @GET("historyprotest-services/getnotexistrequests")
    suspend fun getNotExistRequests(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<NotExistRequestsResponse?>?

    /*    @GET("inspection-header/for-insurance/{insuranceCode}/{inspectionCode}")
        suspend fun getInspectionInfo(
            @Header(Constants.AUTHENTICATION) token: String,
            @Path("insuranceCode") insuranceCode: String,
            @Path("inspectionCode") inspectionCode: String
        ): Response<InfoInspectionResponse?>?*/

    //    https://eservices.tamin.ir/api/inspection-header/for-insurance/null/0080010005526/0050508245
    @GET("inspection-header/for-insurance/{insuranceId}/{inspectionCode}/{nationalCode} ")
    suspend fun getInspectionInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("insuranceId") insuranceId: String? = null,
        @Path("inspectionCode") inspectionCode: String,
        @Path("nationalCode") nationalCode: String
    ): Response<InfoInspectionResponse?>?


    @POST("historyprotest-services/savenotexist")
    suspend fun sendReqSaveNotExist(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body reqBodySave: BodySaveNonExistentHistory
    ): Response<CheckSaveNotExistModel?>?

    @DELETE("historyprotest-services/deletenotexist/{reqno}/{id}")
    suspend fun deleteNoTexist(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("reqno") reqno: String,
        @Path("id") id: String
    ): Response<DeleteNotExistResponse?>?

    @GET("proxy/models/branch")
    suspend fun getBranchDetailListWithBranchCode(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String = "[]"
    ): Response<BranchDetailResponse?>?


    @GET("proxy/models/insurance-type")
    suspend fun getInsuranceTypeListWithInsuracneTypeCode(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String = "[]"
    ): Response<InsuranceTypeResponce?>?


    @POST("historyprotest-services/finalconfirmnotexist")
    suspend fun sendFinalConfirmNotExist(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body body: String = ""
    ): Response<SendFinalConfirmResponse?>?

    @POST("historyprotest-services/confirmnotexist")
    suspend fun sendConfirmNotExist(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body list: List<ConfirmConflictResponseItem>
    ): Response<SendConfirmNotExistResponse?>?

    @GET("inspection-header/get-all-manager")
    suspend fun getPerformedInspectionList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<InspectionResponse?>?


    @GET("inspection-report/{inspectionNumber}")
    suspend fun downloadPerformedInspectionPdf(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("inspectionNumber") inspectionNumber: String
    ): Response<ResponseBody?>

    @GET("subdominants/getInsuredActiveBranch")
    suspend fun getActiveBranch(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<BranchListResponse?>?

    //request for ill Day
    @POST("stp/saveShorttremIllness")
    suspend fun sendRequestForIllDay(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body illDayReq: RequestPaymentForillDayReq
    ): Response<RequestPaymentForIllDayResponse?>?

    @GET("funeral-no-presence/getFuneralNoPresenceLoadData")
    suspend fun getInfoFuneral(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<FuneralAllowanceResponse?>?

    @GET("shortterm-request/getCovidResult")
    suspend fun getCovidResult(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<CovidResponse?>?

    @GET("shortterm/validateFuneral/{nationalCode}")
    suspend fun inquiryDeceasedInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("nationalCode") nationalCode: String
    ): Response<DeceasedInfoResponse?>?

    @POST("funeral-no-presence/saveShorttremFuneral")
    suspend fun submitRequestFuneralAllowance(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body funeralGrantReq: FuneralAllowanceRequest
    ): Response<RequestAllowanceFuneralResponse?>?

    //After creating a bank account number error, this API is used to register the request again
    @POST("funeral-no-presence/confirmShorttremFuneral/{requestId}")
    suspend fun correctedAccountNumber(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("requestId") requestId: String
    ): Response<CorrectedAccountNumberResponse?>?

    @GET("debit-objection/objection-workshop-debit/{workshopNumber}/{branchCode}")
    suspend fun getWorkshopObjectionableDebitList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("workshopNumber") workshopNumber: String,
        @Path("branchCode") branchCode: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkShopDebtResponse?>?


    @GET("debit-objection/objection-detail/{objectionCode}/")
    suspend fun getWorkshopObjectionSms(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("objectionCode") objectionCode: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<SmsListResponse?>?

    @GET("debit-objection/objection-all")
    suspend fun getAllObjections(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<AllObjectionsResponse?>?

    @GET("debit-objection/diff-days/{orderRecipeDate}")
    suspend fun checkWorkshopDebitObjectionPermission(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("orderRecipeDate") orderRecipeDate: String
    ): Response<GeneralRes?>?


    @GET("workshop-services/get-workshops-info/{workshopId}/{branchCode}")
    suspend fun getSelectedWorkshopInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String
    ): Response<BaseResponse<WorkshopInfoResponse?>?>?


    @GET("assets/data/objection-type.json")
    suspend fun getObjectionTypeList(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<BaseResponse<ObjectionType?>?>?

    @POST("debit-objection/objection-save")
    suspend fun sendDebitObjection(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body request: DebitObjection
    ): Response<DebitObjectionResponse?>?

    @GET("debit-objection-reports/objection/{seqNumber}")
    suspend fun downloadDebitObjectionPDF(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("seqNumber") seqNumber: Long? = 0L
    ): Response<ResponseBody?>

    @GET("debit-objection-reports/comitte/{seqNumber}")
    suspend fun downloadDebitObjectionReportPDF(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("seqNumber") seqNumber: Long? = 0L
    ): Response<ResponseBody?>

    /*
[{"property":"peymanSequence","value":"02100014","operator":"EQ"}]
 */
    @GET("debit-installment/workshop-debit/{workshopNumber}/{branchCode}")
    suspend fun getWorkshopDebt(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("workshopNumber") workshopNumber: String,
        @Path("branchCode") branchCode: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkShopDebtResponse?>?

    @GET("debit-installment/installment-all")
    suspend fun getInstallmentList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<InstallmentListResponse?>?

    @GET("workshop-services/special-contracts")
    suspend fun getSpecialContracts(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkshopInfoResponse?>?

    @GET("subdominants/verifyEducation/{nationalId}/{inquiryLicenseCode}")
    suspend fun getInquiryCertificate(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("nationalId") nationalId: String,
        @Path("inquiryLicenseCode") inquiryLicenseCode: String
    ): Response<BaseResponse<String?>?>?

    /*    @POST("funeral-no-presence/saveShorttremFuneral")
        suspend fun sendRequestForFuneralGrant(
            @Header (Constants.AUTHENTICATION) token: String,
            @Body funeralGrantReq: FinalRequestConfirmFuneralGrant
        ):Response<BaseResponse<String?>?>?*/

    @GET("inspection-header/get-all-insurance")
    suspend fun getListInspectionPerformed(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<InspectionPerformedResponse?>?

    @GET("workers/payment-info")
    suspend fun getWorkersPaymentInfo(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<WorkersPaymentInfoResponse?>?

    @POST("workers/payDebit")
    suspend fun getWorkersPayDebit(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body body: WorkersPayDebitRequest,
//        @Query("device_type") device_type: String? = "android",
        @Query("type") url: String
    ): Response<WorkersPayDebitResponse?>?

    @GET("workers/inpectTicket")
    suspend fun inspectTicket(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("ticket") ticket: String?,
        @Query("paymentInfo") paymentInfo: String?,
    ): Response<GeneralRes?>?


    @GET("inspection-header/get-all-manager")
    suspend fun getWorkshopListInspectionPerformed(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<InspectionPerformedResponse?>?


    @GET("baseinfo/job")
    suspend fun getJobTitle(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<JobTitleResponse?>?


    @POST("inspection-request")
    suspend fun sendInspectionRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body inspectionReq: SubmitResponse
    ): Response<SubmitInspectionRequestResponse?>?


    //current-user
    @GET("users/current-user")
    suspend fun getCurrentUser(@Header(Constants.AUTHENTICATION) token: String): Response<CurrentUserResponse?>?

    @GET("erecords/images")
    suspend fun getElectronicFile(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<ElectronicFileResponse?>?

    @GET("workshop-services/contract/get-all")
    suspend fun getContractList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<ContractResponseNew?>?

    @GET("requestissuanceinvoices38/assignersContracts-request-issuance-invoices38")
    suspend fun getAssignerContractList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<ContractResponse?>?

    @GET("requestissuanceinvoices38/det-request-issuance-invoices38")
    suspend fun getComputationalBaseList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("workshopId") workshopId: String?,
        @Query("contractRow") contractRow: String?,
        @Query("brchCode") branchCode: String?,
        @Query("contractSequence") contractSequence: String?,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<ComputationalBaseResponse?>?


    @GET("health/tcr-price-certificate")
    suspend fun getTreatmentCosts(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<TreatmentCostsExpensesResponse?>?


    @GET("special-insured-services/freelance-check-red-cross-status")
    suspend fun checkRedCrossStatus(@Header(Constants.AUTHENTICATION) token: String): Response<RedCrossStatusResponse?>?

    @GET("special-insured-services/freelance-check-student-status")
    suspend fun checkMedicalStudent(@Header(Constants.AUTHENTICATION) token: String): Response<MedicalStudentResponse?>?

    @GET("health/tcr-price-certificate/report/{repId}")
    suspend fun getTreatmentCostsPDF(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("repId") repId: String,
    ): Response<ResponseBody?>?

    @GET("health/tcr-price-certificate/announcement/{repId}")
    suspend fun sendToInboxTreatmentCosts(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("repId") repId: String,
    ): Response<SendToInboxTreatmentCosts?>?

    @GET("pension-inquiry/announcement/")
    suspend fun sendRequestInquirePensionCertificate(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String
    ): Response<GeneralRes?>?


    @GET("upload-image/{documentId}/0/0")
    suspend fun getComputationalBaseImage(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("documentId") documentId: String? = "",
    ): Response<GeneralRes?>?

    @GET("hokm/annoncment")
    suspend fun sendEdictPensionerToMyInbox(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String
    ): Response<GeneralRes?>?

    @GET("requestissuanceinvoices38/getPdf-request-issuance-invoices38/{documentId}")
    suspend fun downloadComputationalBasePdf(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("documentId") documentId: String? = "",
    ): Response<ResponseBody?>

    @GET("special-insured-services/get-registration-info")
    suspend fun getRegistrationInfo(@Header(Constants.AUTHENTICATION) token: String)
            : Response<ConcludingStudentInsuranceContractResponse?>?

    //    https://eservices.tamin.ir/api/special-insured-services/freelance-calc-debit/1
    @GET("special-insured-services/freelance-calc-debit/{month}")
    suspend fun calculateFreelanceDebit(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("month") month: Int
    ): Response<CalculateFreelanceDebitResponse?>?

    //    https://eservices.tamin.ir/api/special-insured-services/calc-debit/1
    @GET("special-insured-services/calc-debit/{month}")
    suspend fun calculateOptionalInsuranceDebitByMonth(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("month") month: Int
    ): Response<CalculateFreelanceDebitResponse?>?


    @GET("proxy/models/city")
    suspend fun getCityList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<CityResponse?>?


    @POST("insured")
    suspend fun sendInsuranceRegistration(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body request: RegistrationReq
    ): Response<GeneralRes?>?

    @POST("special-insured-services/save-contact")
    suspend fun saveUsersAddressInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body updateAddressInfoRequest: UpdateAddressInfoRequest
    ): Response<GeneralRes?>?


    @GET("special-insured-services/freelance-check-age-and-history")
    suspend fun checkAgeAndHistory(@Header(Constants.AUTHENTICATION) token: String): Response<CheckAgeAndHistoryResponse?>?


    @GET("fraction-special-insured-services/check-age-and-history")
    suspend fun checkFractionAgeAndHistory(@Header(Constants.AUTHENTICATION) token: String): Response<CheckAgeAndHistoryResponse?>?


    @GET("special-insured-services/check-age-and-history")
    suspend fun checkOptionalAgeAndHistory(@Header(Constants.AUTHENTICATION) token: String): Response<CheckAgeAndHistoryResponse?>?


    //    https://eservices.tamin.ir/api/special-insured-services/freelance-check-contract-status
    @GET("special-insured-services/freelance-check-contract-status")
    suspend fun checkContractStatus(@Header(Constants.AUTHENTICATION) token: String): Response<CheckContractStatusResponse?>?


    @GET("special-insured-services/check-contract-status")
    suspend fun checkOptionalInsuranceContractStatus(@Header(Constants.AUTHENTICATION) token: String): Response<CheckContractStatusResponse?>?


    // https://eservices.tamin.ir/api/sep/
    // online-payment-widthout-back?systemType=03
    @GET("sep/online-payment-widthout-back")
    suspend fun checkSuccessPaymentStatus(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("systemType") systemType: String
    ): Response<GeneralRes?>?

    //    https://eservices.tamin.ir/api/special-insured-services/freelance-get-last-payment
    @GET("special-insured-services/freelance-get-last-payment")
    suspend fun getFreelanceLastPayment(@Header(Constants.AUTHENTICATION) token: String): Response<FreelanceLastPaymentResponse?>?

    //    https://eservices.tamin.ir/api/special-insured-services/get-last-payment
    @GET("special-insured-services/get-last-payment")
    suspend fun getOptionalInsuranceLastPayment(@Header(Constants.AUTHENTICATION) token: String): Response<OptionalInsuranceLastPaymentResponse?>?


    @GET("special-insured-services/freelance-get-low-high-premium/1/{premiumRate}/{typePremiumRate}")
    suspend fun getPremiumRateForContract(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("premiumRate") premiumRate: String? = "",
        @Path("typePremiumRate") TypePremiumRate: String? = ""
    ): Response<ContractPremiumRateResponse?>?

    @GET("special-insured-services/get-low-high-premium")
    suspend fun getPremiumRateForOptionalContract(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<ContractPremiumRateResponse?>?

    @GET("special-insured-services/freelance-check-and-calc-salary/{premiumRate}/1/{typePremiumRate}")
    suspend fun checkAndCalculateSalaryForContract(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("premiumRate") premiumRate: String? = "",
        @Path("typePremiumRate") TypePremiumRate: String? = ""
    ): Response<CalculateSalary?>?

    @GET("special-insured-services/check-and-calc-salary/{premiumRate}")
    suspend fun calculateSalaryForOptionalContract(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("premiumRate") premiumRate: String? = ""
    ): Response<CalculateSalary?>?


    @GET("baseinfo/spc-premium-rate")
    suspend fun getPremiumRate(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<ContractPremiumOptionsResponse?>?

    //    @GET("sep/online-payment-new")
    @GET("sep/online-payment-mobile")
    suspend fun insurancePayment(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("start-date") startDate: Long? = 0L,
        @Query("end-date") endDate: Long? = 0L,
        @Query("amount") amount: Long? = 0L,
        @Query("systemType") systemType: String? = "",
        @Query("redirectUri") redirectUri: String? = "",
        @Query("paramPage") paramPage: String? = "",
        /*@Query("device_type") device_type: String? = "android",*/
        @Query("month") month: Int? = 0,
        @Query("url") redirectUrl: String
    ): Response<PaymentResponse?>?

    @GET("special-insured-services/list-self-contract-state")
    suspend fun getListSelfContractState(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<CancelContractReasonsResponse?>?

    @PUT("special-insured-services/freelance-update-self-contract-state/{contractNumber}")
    suspend fun cancelContractRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("contractNumber") contractNumber: String,
        @Body body: CancelContractRequest
    ): Response<GeneralRes?>?

    @PUT("special-insured-services/update-self-contract-state/{contractNumber}")
    suspend fun cancelOptionalContractRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("contractNumber") contractNumber: String,
        @Body body: CancelContractRequest
    ): Response<GeneralRes?>?

    @POST("special-insured-services/freelance-make-a-contract/{selectedSalary}")
    suspend fun makeContract(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("selectedSalary") selectedSalary: String,
        @Body finalConfirmRequest: ContractRequest
    ): Response<FinalConfirmResponse?>?


    @POST("fraction-special-insured-services/make-a-contract")
    suspend fun makeFractionContract(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body req: FractionRequestDataModel
    ): Response<FinalConfirmResponse?>?


    @POST("special-insured-services/make-a-contract/{selectedSalary}")
    suspend fun makeOptionalContract(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("selectedSalary") selectedSalary: String,
        @Body finalConfirmRequest: OptionalContractRequest
    ): Response<FinalConfirmResponse?>?


    @POST("special-insured-services/freelance-make-a-contract-protector/{selectedSalary}")
    suspend fun makeContractByGuardian(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("selectedSalary") selectedSalary: String,
        @Body finalConfirmRequest: ContractByGuardianRequest
    ): Response<FinalConfirmResponse?>?


    @POST("special-insured-services/make-a-contract-by-protector/{selectedSalary}")
    suspend fun makeOptionalContractByGuardian(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("selectedSalary") selectedSalary: String,
        @Body finalConfirmRequest: OptionalContractByGuardian
    ): Response<FinalConfirmResponse?>?


    @PUT("special-insured-services/freelance-update-contract/{premium}")
    suspend fun updateContractRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("premium") premium: String,
        @Body body: UpdateContractRequest
    ): Response<GeneralRes?>?

    @PUT("special-insured-services/update-contract/{premium}")
    suspend fun updateOptionalContractRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("premium") premium: String,
        @Body body: UpdateOptionalContract
    ): Response<GeneralRes?>?

    @PUT("special-insured-services/update-contract-by-protector/{premium}")
    suspend fun updateGuardianOptionalContractRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("premium") premium: String,
        @Body body: UpdateGuardianOptionalContract
    ): Response<GeneralRes?>?


    @PUT("special-insured-services/freelance-update-contract-by-protector/{premium}")
    suspend fun updateGuardianContractRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("premium") premium: String,
        @Body body: UpdateGuardianContract
    ): Response<GeneralRes?>?

    @GET("special-insured-services/freelance-payment-history-head-with-contractNumber/{contractNumber}")
    suspend fun getContractsPaymentsListFreelance(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("contractNumber") contractNumber: String
    ): Response<PaymentListDefaultResponse?>?


    @GET("special-insured-services/freelance-payment-history-detail")
    suspend fun getDetailPaymentFreelance(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("contract-number") contractNumber: String,
        @Query("debit-number") debitNumber: String
    ): Response<DetailPaymentListDefaultResponse?>?

    @GET("special-insured-services/freelance-contract-report/{timestamp}")
    suspend fun downloadContract(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("timestamp") timestamp: String? = "",
    ): Response<ResponseBody?>

    @GET("special-insured-services/contract-report/{timestamp}")
    suspend fun downloadOptionalContract(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("timestamp") timestamp: String? = "",
    ): Response<ResponseBody?>

    @GET("fraction-special-insured-services/contract-report/{timestamp}")
    suspend fun downloadFractionContract(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("timestamp") timestamp: String? = "",
    ): Response<ResponseBody?>

    @GET("debit-installment-reports/installment/{letterNumber}")
    suspend fun downloadInstallmentReport(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("letterNumber") letterNumber: String
    ): Response<ResponseBody?>

    @GET("baseinfo/free-job-wage")
    suspend fun getFreelancerJobTitle(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<FreelancerJobTitlesResponse?>?

    @GET("special-insured-services/freelance-payment-details")
    suspend fun getPaymentCalculationDetailList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("start-date") startDate: String = "0",
        @Query("end-date") endDate: String = "0",
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<PaymentListDefaultResponse?>?


    //    https://eservices.tamin.ir/api/special-insured-services/payment-details?start-date=1653161400000&end-date=1658431800000&page=1&start=0&limit=10&filter=%5B%5D&sort=%5B%5D
    @GET("special-insured-services/payment-details")
    suspend fun getOptionalInsurancePaymentCalculationDetailList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("start-date") startDate: String = "0",
        @Query("end-date") endDate: String = "0",
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<PaymentListDefaultResponse?>?

    @POST("employers")
    suspend fun postNewInsuredInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body dataModel: NewInsuredUserInfoReq?
    ): Response<NewInsuredUserInfoResponse?>?

    @PUT("employers/{requestId}")
    suspend fun updateNewInsuredInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("requestId") requestId: Long? = 0,
        @Body dataModel: NewInsuredUserInfoReq?
    ): Response<NewInsuredUserInfoResponse?>?

    @GET("relation-tamins/isnew/{nationalId}")
    suspend fun checkUserIsNew(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("nationalId") nationalId: String? = "0",
    ): Response<NewInsuredUserStatusResponse?>?

    @GET("personals/summary/{requestId}")
    suspend fun getRequestSummary(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("requestId") requestId: Long? = 0,
    ): Response<NewInsuredSummaryResponse?>?

    @GET("documents")
    suspend fun getInsuredRegistrationDocList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<InsuredDocsResponse?>?

    @PUT("documents/{personalId}")
    suspend fun putInsuredRegistrationDocList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("personalId") personalId: String? = "0",
        @Body body: ArrayList<InsuredDoc>
    ): Response<GeneralRes?>?

    fun putInsuredRegistrationDocList(token: String, list: ArrayList<InsuredDoc>)


    // https://eservices.tamin.ir/api/subdominants/145871492
    @DELETE("subdominants/{personalId}")
    suspend fun deleteRecentlyAddedUser(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("personalId") personalId: Long? = 0,
    ): Response<GeneralRes?>?

    @PUT("requests/confirm/{requestId}")
    suspend fun confirmRecentlyAddedUser(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("requestId") requestId: Long? = 0
    ): Response<ConfirmUserResponse?>?

    @GET("employers")
    suspend fun getWorkshopRecentlyAddedMembers(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkshopRecentLyAddedMemberResponse?>?

    @GET("relation-tamins")
    suspend fun getRecentlyAddedUser(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "0",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkshopRecentLyAddedMemberResponse?>

    //    https://eservices.tamin.ir/api/workshop-services/employer/get-all-employer-agreement-by-national-id
    @GET("workshop-services/employer/get-all-employer-agreement-by-national-id")
//    @GET("workshop-services/employer-agreement-info")
    suspend fun getEmployerAgreementInfoList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<EmployerAgreementResponse?>?

    @GET("workshop-services/request-ticket")
    suspend fun sendCommitmentRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String = "[]"
    ): Response<GeneralRes?>?

    @GET("workshop-services/employer-info/{verificationCode}")
    suspend fun sendVerificationCode(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("verificationCode") verificationCode: String
    ): Response<EmployerCommitmentResponse?>?

    @POST("workshop-service/save-real-person-info")
    suspend fun sendVerificationCode(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body verificationCode: VerifyCodeRequest
    ): Response<GeneralRes?>?

    @GET("workshop-services/employer-workshops-info-with-out-contract")
    suspend fun getWorkshopsInfoWithoutContract(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkshopInfoWithoutContractResponse?>?


    @GET("workshop-services/contract-employer-workshop-info-with-workshop-and-branch-code/{workshopId}/{branchCode}")
    suspend fun getWorkshopContactList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("workshopId") workshopId: String?,
        @Path("branchCode") branchCode: String?,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "100",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<WorkshopContractListResponse?>?

    @GET("workshop-services/get-employer-agreement-by-workshop-id-and-branch-code/{workshopId}/{branchCode}")
    suspend fun getEmployerWorkshopList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("workshopId") workshopId: String?,
        @Path("branchCode") branchCode: String?,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "100",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<EmployerWorkshopResponse?>?

    @POST("workshop-services/employer-agreement")
    suspend fun postEmployerAgreement(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body body: AgreementDataModel,
    ): Response<GeneralRes?>?

    @GET("workshop-service/legal-inquiry/{legalWorkshopID}")
    suspend fun getLegalWorkshopInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("legalWorkshopID") legalWorkshopID: String?
    ): Response<LegalWorkshopInfoResponse?>?

    @GET("workshop-service/inquiry/{nationalCode}/{birthDate}")
    suspend fun getLegalWorkshopCEOInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("nationalCode") nationalCode: String?,
        @Path("birthDate") birthDate: String?
    ): Response<LegalWorkshopCEOInfoResponse?>?


    @GET("workshop-services/request-ticket2")
    suspend fun getVerificationTicketForLegalWorkshopInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String = "[]",
    ): Response<GeneralRes?>?

    //    http://172.16.13.248:7001/eservices/api/workshop-service/save-stack-holders
    @POST("workshop-service/save-stack-holders")
    suspend fun sendVerifyTicketForLegalWorkshop(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body body: TicketRequest,
    ): Response<GeneralRes?>?

    @GET("legal-ticket")
    suspend fun requestOfLegalTicket(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<GeneralRes?>?

    @GET("legal-ticket/{nationalCode}")
    suspend fun requestOfLegalTicketWithNationalCode(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("nationalCode") nationalCode: String
    ): Response<GeneralRes?>?

    //    http://172.16.13.248:7001/eservices/api/legal-ticket/validate/216585
    @POST("legal-ticket/validate/{ticket}")
    suspend fun validateLegalTicket(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("ticket") ticket: String,
    ): Response<GeneralRes?>?

    //    http://172.16.13.248:7001/eservices/api/legal-stakeholders/216585?workshopId=0063250011&branchCode=0070&page=1&start=0&limit=10&filter=%5B%5D&sort=%5B%5D
//    @GET("legal-stakeholders/{ticket}")
    @GET("legal-stakeholders")
    suspend fun getLegalStackHolderListByTicket(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("stackType") stackType: String = "4",
        @Query("workshopId") workshopId: String?,
        @Query("branchCode") branchCode: String?,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<LegalStackHolderResponse?>?


    //    http://172.16.13.248:7001/eservices/api/v.1/legal-stakeholders/units?page=1&start=0&limit=10&filter=%5B%5D&sort=%5B%5D
    @GET("v.1/legal-stakeholders/units")
    suspend fun getLegalStackHolderList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<LegalStackHolderResponse?>?

    @GET("legal-stakeholders/{ticket}")
    suspend fun submitNewAgent(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("workshopId") workshopId: String?,
        @Query("branchCode") branchCode: String?,
        @Path("ticket") ticket: String?,
        @Body req: AgentRequestModel
    ): Response<GeneralRes?>?

    //    http://172.16.13.248:7001/eservices/api/legal-stakeholders/427167
    @POST("legal-stakeholders/{ticket}")
    suspend fun submitNewLegalAgent(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("ticket") ticket: String?,
        @Body request: AgentRequestModel,
    ): Response<GeneralRes?>?

    // http://172.16.13.248:7001/eservices/api/legal-stakeholders/462844/12524557
    @DELETE("legal-stakeholders/{ticket}/{stackId}")
    suspend fun deleteLegalAgent(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("ticket") ticket: String? = "",
        @Path("stackId") stackId: Long? = 0L
    ): Response<GeneralRes?>?

    @GET("proxy/models/dependency")
    suspend fun getRelationShipList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<FamilyRelationShipResponse?>?

    @GET("personals/subdominant")
    suspend fun getDependentInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<DependentInfoResponse?>?

    @GET("subdominants/getOfficeData/{nationalCode}/{timeStampBirthDay}/{dependencyCode}")
    suspend fun inquiryRegistryInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("nationalCode") nationalCode: String,
        @Path("timeStampBirthDay") timeStampBirthDay: String,
        @Path("dependencyCode") dependencyCode: String
    ): Response<InquiryRegistryResponse?>?

    @GET("subdominants/verifyEducation/{nationalId}/{inquiryLicenseCode}")
    suspend fun getInquiryEducation(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("nationalId") nationalId: String,
        @Path("inquiryLicenseCode") inquiryLicenseCode: String
    ): Response<InquiryEducationCodeResponse?>?

    @POST("subdominants")
    suspend fun addNewDependent(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body body: RequestAddDependent
    ): Response<GeneralRes?>?


    @Headers("Accept: application/json")
    @POST("subdominants/transfer")
    suspend fun refreshDependent(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<GeneralRes?>?

    @GET("subdominants/getDataForEducation")
    suspend fun checkRenewCondition(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<DependentInfoResponse?>?

    //submit request by student => code = 1
    //submit request by parents => code = student's national id
    @GET("subdominants/extendEducation/{code}/{educationCode}")
    suspend fun inquiryStudyCodeCertificate(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("code") code: String,
        @Path("educationCode") studyCode: String
    ): Response<GeneralStringRes?>?

    // http://172.16.13.248:7001/eservices/api/requestissuanceinvoices38/contractSubject-request-issuance-invoices38?page=1&start=0&limit=5&filter=%5B%5D&sort=%5B%5D
    @GET("requestissuanceinvoices38/contractSubject-request-issuance-invoices38")
    suspend fun getMafasaHesabContractSubjects(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<MafasaHesabContractSubjectResponse?>?

    //    Request URL: http://172.16.13.248:7001/eservices/api/requestissuanceinvoices38/update-request-issuance-invoices38/0082810145TT02100001TT0210TT01TT01
    @PUT("requestissuanceinvoices38/update-request-issuance-invoices38/{id}")
    suspend fun sendMafasaHesabRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("id") id: String,
        @Body body: MafasaHesabRequestModel
    ): Response<GeneralRes?>?

    @GET("disability-request/subdominant")
    suspend fun getDisabilityDependentInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<DisabilityDependentResponse?>?

//    http://172.16.13.248:7001/eservices/api/debit-installment/installment-all?page=1&start=0&limit=5&filter=[]&sort=[]


    @GET("disability-request/personal")
    suspend fun getDisabilityPersonalInfo(
        @Header(Constants.AUTHENTICATION) token: String,
    ): Response<DisabilityPersonalInfoResponse?>?

    @POST("disability-request")
    suspend fun saveDisabilityUserInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body body: DisabilitySaveInfoRequest
    ): Response<DisabilitySaveInfoResponse?>?

    @GET("pension-request/age")
    suspend fun getUserAge(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("birthDate") birthDate: Long
    ): Response<AgeResponse?>?


    @GET("debit-installment/get-discount/{branchCode}/{debitNumber}/{debitRemain}")
    suspend fun getDebtDiscount(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("branchCode") branchCode: String,
        @Path("debitNumber") debitNumber: String,
        @Path("debitRemain") debitRemain: Long
    ): Response<DebtDiscountResponse?>?

    @POST("debit-installment/installment-save")
    suspend fun installmentDebt(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body request: InstallmentRequestModel
    ): Response<DebtInstallmentResponse?>?

    @GET("disability-request/report")
    suspend fun getMedicalCommissionPdf(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("lastWorkshop") lastWorkshop: String,
    ): Response<ResponseBody?>?

    @PUT("disability-request/{requestId}")
    suspend fun finalConfirmDisabilityRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("requestId") requestId: Long,
        @Body body: DisabilityFinalConfirmRequest
    ): Response<DisabilitySaveInfoResponse?>?

    @PUT("disability-request/{requestId}")
    suspend fun saveDocumentDisability(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("requestId") requestId: Long,
        @Body body: DisabilitySaveDocumentRequest
    ): Response<GeneralRes?>?

    //    https://eservices.tamin.ir/view/assets/pdfs/questionair.pdf
    @GET
    @Streaming
    suspend fun getRegistrationDeclarationForm(
        @Header(Constants.AUTHENTICATION) token: String,
        @Url url: String = "https://eservices.tamin.ir/view/assets/pdfs/questionair.pdf"
    ): Response<ResponseBody?>

    //    http://172.16.13.248:7001/eservices/api/workshop-services/mad38-head/0050005220/0070/00700001/-/-?page=1&start=0&limit=10&filter=%5B%5D&sort=%5B%5D
    @GET("workshop-services/mad38-head/{workshopCode}/{branchCode}/{contractRow}/{mafasaStatus}/{contractNumber}")
    suspend fun getClause38List(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("workshopCode") workshopCode: String?,
        @Path("branchCode") branchCode: String?,
        @Path("contractRow") ContractRow: String?,
        @Path("mafasaStatus") MafasaStatus: String?,
        @Path("contractNumber") contractNumber: String?,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<Clause38Response?>?

    //    https://eservices.tamin.ir/api/workshop-services/mad38-detail/0968210170/0960/09600001/1080611
    @GET("workshop-services/mad38-detail/{workshopCode}/{branchCode}/{contractRow}/{mafasaSerialNo}")
    suspend fun getClause38Detail(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("workshopCode") workshopCode: String?,
        @Path("branchCode") branchCode: String?,
        @Path("contractRow") ContractRow: String?,
        @Path("mafasaSerialNo") mafasaSerialNo: String?,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<Clause38DetailResponse?>?


    //    http://172.16.13.248:7001/eservices/api/removepaper/getLetersubject?contrat=null&page=-1&start=-10&limit=5&filter=[]&sort=[]
    @GET("removepaper/getLetersubject/")
    suspend fun getLetterSubject(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("contrat") contract: String? = null,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<LetterSubjectResponse?>?

    //    https://eservices.tamin.ir/api/removepaper/findLetterHeadert?workshopId=9008300068&page=1&start=0&limit=50&filter=%5B%5D&sort=%5B%5D
    @GET("removepaper/findLetterHeadert/")
    suspend fun getDistantCorrespondenceList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("workshopId") workshopId: String? = null,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<LetterListResponse?>?

    //https://eservices.tamin.ir/api/removepaper/general-letter
    @POST("removepaper/general-letter")
    suspend fun registerLetter(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body request: RegisterLetterRequest? = null
    ): Response<RegisterLetterResponse?>?

    @GET("workshop-services/employer/get-employer-agreement-by-national-id")
    suspend fun getWorkshopListDefinitiveDebt(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<WorkShopListDefinitiveArticle16Response?>?

    @GET("debit-objection/management-workshop-debit/{workshopId}/{branchId}")
    suspend fun getWorkshopsDebtsList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("workshopId") workshopId: String,
        @Path("branchId") branchId: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<WorkshopsDebtListResponse?>?

    @GET("workshop-services/get-workshops-info/{workshopId}/{branchCode}")
    suspend fun getWorkshopInfoDebtArticle16(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String
    ): Response<WorkShopInfoDebtArticle16Response?>?

    @POST("debit-objection/debit-comitte-save")
    suspend fun registrationRequestArticle16(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body body: RegisterArticle16RequestModel
    ): Response<RegisterArticle16Response?>?

    @GET("debit-objection/objection-request/{objectionNumber}")
    suspend fun getRequestInfoArticle16(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("objectionNumber") objectionNumber: Long,
    ): Response<Article16RequestInfoResponse?>?

    @POST("removepaper/general-letter")
    suspend fun sendDistantCorrespondenceRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body body: RegisterLetterRequest
    ): Response<RegisterLetterResponse?>?

    //    https://eservices.tamin.ir/api/removepaper/201784734Pp9008300068
    @DELETE("removepaper/{reqno}")
    suspend fun deleteExistingLetter(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("reqno") reqno: String
    ): Response<GeneralRes?>?

    @GET("shortterm-request/getProcessData/{referenceId}")
    suspend fun getShortTermRequestStatus(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("referenceId") referenceId: String
    ): Response<RequestStatusResponse?>?

    @GET("shortterm-request/getShorttermRequestLoadData/{referenceId}")
    suspend fun getShortTermRequestInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("referenceId") referenceId: String
    ): Response<ShortTermRequestInfoResponse?>?

    @GET("upload-image/{guid}/0/0")
    suspend fun getDocument(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("guid") guid: String? = "",
    ): Response<DownloadFileResponse?>?

    //    https://eservices.tamin.ir/api/removepaper/general-letter/approve/207238721TT0133210149
    @PUT("removepaper/general-letter/approve/{id}")
    suspend fun approveLetter(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("id") id: String? = null
    ): Response<LetterListResponse?>?

    @GET("survivor-request/national-id")
    suspend fun getDeceasedInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("id") id: String = "0"
    ): Response<com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor.DeceasedInfoResponse?>?

    @GET("survivor-request/subdominant")
    suspend fun getSurvivorList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("id") id: String = "0"
    ): Response<SurvivorResponse?>?

    @POST("survivor-request")
    suspend fun saveSurvivorInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body body: SaveSurvivorInfoRequest
    ): Response<GeneralRes?>?

    @GET("survivor-request/list")
    suspend fun confirmSurvivorsList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<ConfirmSurvivorListResponse?>?

    @GET("survivor-request/final-report")
    suspend fun getFinalSurvivorPensionPDF(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<ResponseBody?>?


    @PUT("survivor-request/{requestId}")
    suspend fun submitFinalSurvivorPension(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("requestId") requestId: Int,
        @Body body: RequestModel
    ): Response<GeneralRes?>?

    @GET("survivor-request/age")
    suspend fun getAge(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("birthDate") birthDate: Long = 0L
    ): Response<AgeResponse?>?

    @POST("pension-request")
    suspend fun confirmIdentityAndHistoryInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("ticketCode") authenticationsCode: Long,
        @Body body: ConfirmIdentityAndHistoryInfoRequest
    ): Response<RetirementConfirmIdentityInfoResponse?>?

    @GET("pension-request/checkRequests")
    suspend fun checkRetirementStatus(
        @Header(Constants.AUTHENTICATION) token: String,
    ): Response<RetirementStatusResponse?>?

    @GET("pension-request/getTicket")
    suspend fun getAuthenticationCode(
        @Header(Constants.AUTHENTICATION) token: String,
    ): Response<AuthenticationResponse?>?

    @GET("pension-request/age")
    suspend fun getUserAge(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("birthDate") birthDate: Long?  //birthDate is timestamp
    ): Response<AgeResponse?>?

    @GET("pension-request/personal")
    suspend fun authenticationAndGetPersonalInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("ticketCode") authenticationsCode: Long
    ): Response<RetirementPersonalInfoResponse?>?

    @GET("pension-request")
    suspend fun getRetirementRequestInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String = "[]"
    ): Response<RetirementRequestInfoResponse?>?

    @PUT("pension-request/{requestId}")
    suspend fun sendRetirementDocument(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("requestId") requestId: String,
        @Body body: RetirementSaveDocumentRequest
    ): Response<GeneralRes?>?

    @GET("wage-assignment/request/{requestId}")
    suspend fun getDeferredInstallmentInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("requestId") requestId: String
    ): Response<DeferredInstallmentInfoResponse?>?

    @GET("patient-history/reports-prescription-PDF/{prescriptionID}")
    suspend fun getPrescriptionPdfFile(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("prescriptionID") prescriptionID: String,
    ): Response<ResponseBody?>

    @GET("bld-request-services/building-workshops/normal")
    suspend fun getConstructionFiles(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<ConstructionFileResponse?>?

    @GET("bld-request-services/building-workshops-owners")
    suspend fun getDetailConstructionFile(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<DetailConstructionInfoResponse?>?

    @GET("bld-request-services/building-workshops-owners")
    suspend fun getBeneficiariesWorkshop(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<BeneficiariesConstructionResponse?>?

    @GET("bld-request-services/building-payment-sheet-list-info/{debitNumber}/0/normal")
    suspend fun getPaymentSheetConstructionInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("debitNumber") debitNumber: String
    ): Response<PaymentSheetConstructionFilesResponse?>?

    @GET("bld-request-services/building-workshop-certificate-report/{debitNumber}/normal/{branchCode}")
    suspend fun getCertificatePaymentSheetPDF(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("debitNumber") debitNumber: String,
        @Path("branchCode") branchCode: String
    ): Response<ResponseBody?>?

    @PUT("bld-request-services/issuance-payment-sheet-building-workshop-request/{debitNumber}/0/normal")
    suspend fun issuancePaymentSheet(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("debitNumber") debitNumber: String,
    ): Response<GeneralStringRes?>?

    @GET("bld-request-services/building-workshop-installment-head/{workshopId}/{branchId}")
    suspend fun getInstallmentLetterList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("workshopId") workshopId: String,
        @Path("branchId") branchId: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<InstallmentLetterListResponse?>?

    @GET("bld-request-services/building-workshop-installment-detail/{debitNumber}/{branchId}")
    suspend fun getDetailDebitList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("debitNumber") debitNumber: String,
        @Path("branchId") branchId: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<InstallmentDebitListResponse?>?

    @GET("bld-request-services/building-workshop-installment-list/{debitNumber}/{branchId}")
    suspend fun getInstallmentConstructionList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("debitNumber") debitNumber: String,
        @Path("branchId") branchId: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<InstallmentConstructionListResponse?>?

    @GET("historyprotest-services/getprotestresult/{referenceId}")
    suspend fun getFollowUpResultObjectionNonExistsHistory(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("referenceId") referenceId: String
    ): Response<ResultFollowUpObjectionNonExitsResponse?>?

    /*   @GET("bld-request-services/building-payment-sheet-list-info/{debitNumber}/{oldDebitNumber}/installment")
       suspend fun getPaymentSheetInstallmentConstruction(
           @Header(Constants.AUTHENTICATION) token: String,
           @Path("debitNumber") debitNumber: String,
           @Path("oldDebitNumber") branchId: String,
           @Query("page") page: String = "1",
           @Query("start") start: String = "0",
           @Query("limit") limit: String = "10",
           @Query("filter") filter: String = "[]",
           @Query("sort") sort: String = "[]"
       )
   */
    /*
        @GET("bld-request-services/update-if-installmet/{oldDebitNumber}")
        suspend fun updateInstallmentConstruction(
            @Header(Constants.AUTHENTICATION) token: String,
            @Path("oldDebitNumber") oldDebitNumber: String
        ):Response<GeneralStringRes?>?

    */

    @GET("debit-installment-payment/pay-status-list/{debtNumber}/{branchCode}")
    suspend fun getPaymentDebitList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("debtNumber") debitNumber: String,
        @Path("branchCode") branchCode: String
    ): Response<InstallmentPaymentResponse?>?

    @GET("debit-installment-payment/pay/{debtNumber}")
    suspend fun getPaymentDebitToken(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("debtNumber") debitNumber: String/*,
        @Query("deviceType") deviceType:String = "android",*/
    ): Response<PaymentResponse?>?

    @GET("debit-installment-payment/pay-check/{debtSerialNumber}")
    suspend fun checkPaymentDebt(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("debtSerialNumber") debtSerialNumber: String
    ): Response<GeneralRes?>?

    @GET("debit-installment/paid-installment/{debtSerialNumber}")
    suspend fun getDebtPaidList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("debtSerialNumber") debtSerialNumber: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "20",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<DebtPaidListResponse?>?

    @GET("medical-committee-demand/get-last-demand-details")
    suspend fun getRegisteredMedicalCommission(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "20",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]"
    ): Response<RegisteredMedicalCommissionResponse?>?


    @GET("occurence/insured-relation")
    suspend fun getInsuredRelation(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String = "[]",
    ): Response<InsuredRelationResponse?>?

    @GET("occurence/all-workshop")
    suspend fun getAllWorkshops(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String = "[]",
    ): Response<AllWorkshopsResponse?>?

    @GET("occurence/workshop-history")
    suspend fun getAllWorkshopHistory(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String = "[]",
    ): Response<GeneralRes?>?

    @GET("occurence/workshop-specifications")
    suspend fun getWorkshopSpecification(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String = "[]",
    ): Response<WorkshopSpecificationResponse?>?

    @GET("occurence/office-personalInfo")
    suspend fun getOfficePersonalInfo(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("filter") filter: String = "[]",
    ): Response<OfficePersonalInfoResponse?>?

    @GET("occurrence-document-type")
    suspend fun getDocumentType(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") pageSize: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<DocumentTypeResponse?>?


    //upload image
    @Multipart
    @POST("upload-image/occurrenceImage")
    suspend fun uploadOccurrenceImage(
        @Header(Constants.AUTHENTICATION) token: String,
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse?>?

    @POST("occurence")
    suspend fun sendOccurrenceRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body request: OccurrenceReq
    ): Response<OccurrenceResponse?>?

    //https://eservices.tamin.ir/api/patient-history/lab-result-PDF/0372217400/10060003021667/0372217400
    @GET("patient-history/lab-result-PDF/{patientID}/{noteHeadEprescID}/{currentUserNationalCode}")
    suspend fun downloadTestResultPdf(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("patientID") patientID: String? = "",
        @Path("noteHeadEprescID") noteHeadEprescID: String? = "",
        @Path("currentUserNationalCode") currentUserNationalCode: String? = "",
    ): Response<ResponseBody?>

    //https://eservices.tamin.ir/api/medical-committee-demand/get-last-demand-details
    @GET("medical-committee-demand/get-last-demand-details")
    suspend fun getMedicalMissionList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<MedicalMissionResponse?>?

    @GET(BuildConfig.SENTRY_CONFIG_URL)
    suspend fun getSentryConfig(
        @Header(Constants.AUTHENTICATION) token: String,
    ): Response<SentryConfig?>?


    //todo
    //Is it really get ???
    @GET("personals/subdominant/end/{action}/{identifier}/{date}")
    suspend fun dependentCancellation(
        @Path("action") action: String,
        @Path("identifier") identifier: String,
        @Path("date") date: String
    ): Response<GeneralRes?>


}