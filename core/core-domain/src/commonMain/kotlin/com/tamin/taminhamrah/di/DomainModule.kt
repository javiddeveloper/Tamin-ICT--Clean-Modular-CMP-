package com.tamin.taminhamrah.di


import com.tamin.taminhamrah.useCases.auth.AuthAuthorizeUrlUseCase
import com.tamin.taminhamrah.useCases.auth.AuthAuthorizeUrlUseCaseImpl
import com.tamin.taminhamrah.useCases.auth.GetSignOutUrlUseCase
import com.tamin.taminhamrah.useCases.auth.DeepLinkManager
import com.tamin.taminhamrah.useCases.auth.DeepLinkManagerImpl
import com.tamin.taminhamrah.useCases.auth.ExchangeCodeForTokensUseCase
import com.tamin.taminhamrah.useCases.auth.ExchangeCodeForTokensUseCaseImpl
import com.tamin.taminhamrah.useCases.auth.HandleAuthDeepLinkUseCase
import com.tamin.taminhamrah.useCases.auth.HandleAuthDeepLinkUseCaseImpl
import com.tamin.taminhamrah.useCases.bankAccount.GetBankAccountListUseCase
import com.tamin.taminhamrah.useCases.bankAccount.RegisterBankAccountUseCase
import com.tamin.taminhamrah.useCases.common.GetRecipientListUseCase
import com.tamin.taminhamrah.useCases.common.GetBeneficiaryUseCase
import com.tamin.taminhamrah.useCases.common.GetCitiesUseCase
import com.tamin.taminhamrah.useCases.common.GetMainMenuUseCase
import com.tamin.taminhamrah.useCases.file.GetElectronicFileUseCase
import com.tamin.taminhamrah.useCases.file.DownloadDocumentUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.pension.GetEdictPensionerUseCase
import com.tamin.taminhamrah.useCases.pension.GetEdictReportPDFUseCase
import com.tamin.taminhamrah.useCases.pension.SendEdictPensionerToMyInboxUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionInquiryUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollPDFUseCase
import com.tamin.taminhamrah.useCases.pension.GetUserAgeUseCase
import com.tamin.taminhamrah.useCases.pension.GetRetirementRequestInfoUseCase
import com.tamin.taminhamrah.useCases.pension.CheckRetirementStatusUseCase
import com.tamin.taminhamrah.useCases.pension.SendRetirementDocumentUseCase
import com.tamin.taminhamrah.useCases.personalInbox.DeleteMyRequestUseCase
import com.tamin.taminhamrah.useCases.personalInbox.InboxInquiryLicenseUseCase
import com.tamin.taminhamrah.useCases.personalInbox.GetPersonalInboxItemsUseCase
import com.tamin.taminhamrah.useCases.personalInbox.GetPersonalInboxSizeUseCase
import com.tamin.taminhamrah.useCases.personalInbox.GetMyRequestPdfUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestTypesUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestsUseCase
import com.tamin.taminhamrah.useCases.personal.GetDeceasedInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetAgeUseCase
import com.tamin.taminhamrah.useCases.personal.GetPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetDisabilityDependentInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetConfirmSurvivorsListUseCase
import com.tamin.taminhamrah.useCases.personal.CheckGirlSurvivorConditionsUseCase
import com.tamin.taminhamrah.useCases.user.GetInsuredActiveBranchUseCase
import com.tamin.taminhamrah.useCases.user.GetRelationTaminAllUseCase
import com.tamin.taminhamrah.useCases.user.GetStatusCertificateReportUseCase
import com.tamin.taminhamrah.useCases.user.GetRecipientsUseCase
import com.tamin.taminhamrah.useCases.user.ChangeMobileUseCase
import com.tamin.taminhamrah.useCases.user.SendImageRequestUseCase
import com.tamin.taminhamrah.useCases.auth.SignOutUseCase
import com.tamin.taminhamrah.useCases.common.GetRolesUseCase
import com.tamin.taminhamrah.useCases.common.SetThemeUseCase
import com.tamin.taminhamrah.useCases.common.SetBiometricEnabledUseCase
import com.tamin.taminhamrah.useCases.common.CompleteBiometricEnrollmentPromptUseCase
import com.tamin.taminhamrah.useCases.common.SetFontSizeUseCase
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.common.GetJobTitleUseCase
import com.tamin.taminhamrah.useCases.common.GetRegistrationDeclarationFormUseCase
import com.tamin.taminhamrah.useCases.contracts.GetContractsUseCase
import com.tamin.taminhamrah.useCases.contracts.CalculateFreelanceSalaryUseCase
import com.tamin.taminhamrah.useCases.contracts.CalculateOptionalSalaryUseCase
import com.tamin.taminhamrah.useCases.contracts.CheckInsurancePaymentStatusUseCase
import com.tamin.taminhamrah.useCases.contracts.GetFreelancePremiumRangeUseCase
import com.tamin.taminhamrah.useCases.contracts.GetFreeJobWagesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetInsurancePaymentUseCase
import com.tamin.taminhamrah.useCases.contracts.GetRegistrationInfoUseCase
import com.tamin.taminhamrah.useCases.contracts.GetSpcPremiumRatesUseCase
import com.tamin.taminhamrah.useCases.contracts.MakeFreelanceContractByGuardianUseCase
import com.tamin.taminhamrah.useCases.contracts.MakeOptionalContractByGuardianUseCase
import com.tamin.taminhamrah.useCases.contracts.MakeContractUseCase
import com.tamin.taminhamrah.useCases.contracts.MakeFreelanceContractUseCase
import com.tamin.taminhamrah.useCases.contracts.SaveContactUseCase
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetHistoryJobInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.pension.GetDisabilityPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetRequestSummaryUseCase
import com.tamin.taminhamrah.useCases.personal.PutInsuredRegistrationDocListUseCase
import com.tamin.taminhamrah.useCases.user.CheckUserIsNewUseCase
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import com.tamin.taminhamrah.useCases.user.TaminRelationUseCase
import com.tamin.taminhamrah.useCases.user.UserProfileImageUseCase
import com.tamin.taminhamrah.useCases.user.VerifyChangeMobileUseCase
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.treatment.GetDeservedTreatmentUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionListUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionDetailUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionPriceUseCase
import com.tamin.taminhamrah.useCases.treatment.GetDependantUnderEighteenUseCase
import com.tamin.taminhamrah.useCases.treatment.GetPrescriptionPdfFileUseCase
import com.tamin.taminhamrah.useCases.treatment.DownloadLabResultPdfUseCase
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientGeneralUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientSelfDeclarativeUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientDrugAllergiesUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientHospitalizationsUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientVisitsUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientLabsUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientImagingUseCase
import com.tamin.taminhamrah.useCases.health.GetRelationTypesUseCase
import com.tamin.taminhamrah.useCases.health.GetAllProvincesUseCase
import com.tamin.taminhamrah.useCases.health.GetProvinceCitiesUseCase
import com.tamin.taminhamrah.useCases.health.GetBloodGroupsUseCase
import com.tamin.taminhamrah.useCases.health.GetMaritalStatusUseCase
import com.tamin.taminhamrah.useCases.health.GetSmokingStatusUseCase
import com.tamin.taminhamrah.useCases.health.GetSelfDeclarableIllnessesUseCase
import com.tamin.taminhamrah.useCases.health.GetSelfDeclarableIllnessesByGroupUseCase
import com.tamin.taminhamrah.useCases.health.GetAllDrugsUseCase
import com.tamin.taminhamrah.useCases.health.UpdatePatientUseCase
import com.tamin.taminhamrah.useCases.health.AddSelfDeclarativeUseCase
import com.tamin.taminhamrah.useCases.health.UpdateSelfDeclarativeUseCase
import com.tamin.taminhamrah.useCases.health.SyncIllnessSelfDeclarativesUseCase
import com.tamin.taminhamrah.useCases.health.SyncDrugAllergiesUseCase
import com.tamin.taminhamrah.useCases.health.GetActFrequenciesUseCase
import com.tamin.taminhamrah.useCases.workshops.CheckObjectionDeadlineUseCase
import com.tamin.taminhamrah.useCases.workshops.ConfirmRecentlyAddedMemberUseCase
import com.tamin.taminhamrah.useCases.workshops.DeleteRecentlyAddedMemberUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticle16DebtsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticle16ReportPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticle16RequestInfoUseCase
import com.tamin.taminhamrah.useCases.workshops.GetArticle16WorkshopInfoUseCase
import com.tamin.taminhamrah.useCases.workshops.GetDebitObjectionPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetDebitReasonsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetDebitTurnoverPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetDemandDocumentsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetObjectionableDebitsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetPaymentSheetsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetRecentlyAddedMembersUseCase
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopDebitsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopDebtInquiryUseCase
import com.tamin.taminhamrah.useCases.workshops.PayWorkshopDebitUseCase
import com.tamin.taminhamrah.useCases.workshops.SaveArticle16RequestUseCase
import com.tamin.taminhamrah.useCases.workshops.SaveDebitObjectionUseCase
import com.tamin.taminhamrah.useCases.agent.SendAgentPromptUseCase
import com.tamin.taminhamrah.useCases.agent.CheckChatAllowedUseCase
import com.tamin.taminhamrah.useCases.agent.DeleteAgentSessionUseCase
import com.tamin.taminhamrah.useCases.agent.DeletePendingAgentMessagesUseCase
import com.tamin.taminhamrah.useCases.agent.GetCachedMessagesUseCase
import com.tamin.taminhamrah.useCases.agent.GetCurrentUserNationalCodeUseCase
import com.tamin.taminhamrah.useCases.agent.GetCurrentUserNationalCodeUseCaseImpl
import com.tamin.taminhamrah.useCases.agent.ObserveCachedMessagesUseCase
import com.tamin.taminhamrah.useCases.agent.PruneEmptyAgentSessionUseCase
import com.tamin.taminhamrah.useCases.agent.GetAgentSessionsUseCase
import com.tamin.taminhamrah.useCases.agent.GetAgentSessionUseCase
import com.tamin.taminhamrah.useCases.agent.SaveCachedMessageUseCase
import com.tamin.taminhamrah.useCases.agent.StartAgentSessionUseCase
import com.tamin.taminhamrah.useCases.agent.UpdateAgentSessionUseCase
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopMembersUseCase
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopStackHoldersUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import com.tamin.taminhamrah.useCases.treatment.GetTreatmentCostsPDFUseCase
import com.tamin.taminhamrah.useCases.treatment.GetTreatmentCostsUseCase
import com.tamin.taminhamrah.useCases.treatment.SendToInboxTreatmentCostsUseCase
import com.tamin.taminhamrah.useCases.treatment.GetMedicalConfirmationsUseCase
import com.tamin.taminhamrah.useCases.treatment.GetMedicalConfirmationPDFUseCase
import com.tamin.taminhamrah.useCases.treatment.SendToInboxMedicalConfirmationUseCase
import com.tamin.taminhamrah.useCases.addDependent.AddNewDependentUseCase
import com.tamin.taminhamrah.useCases.addDependent.GetActiveBranchesUseCase
import com.tamin.taminhamrah.useCases.addDependent.GetDependentInfoUseCase
import com.tamin.taminhamrah.useCases.addDependent.GetFamilyRelationshipsFromProxyUseCase
import com.tamin.taminhamrah.useCases.addDependent.GetFamilyRelationshipsUseCase
import com.tamin.taminhamrah.useCases.addDependent.InquiryEducationCodeUseCase
import com.tamin.taminhamrah.useCases.addDependent.InquiryRegistryUseCase
import com.tamin.taminhamrah.useCases.addDependent.UploadDependentImageUseCase
import com.tamin.taminhamrah.useCases.user.mockUseCases.MockSubdominantUseCase
import com.tamin.taminhamrah.useCases.versionHistory.GetVersionHistoryUseCase
import com.tamin.taminhamrah.useCases.contactUs.GetContactUsUseCase

val domainModule = module {
    // Add Dependent UseCases
    factoryOf(::GetActiveBranchesUseCase)
    factoryOf(::GetFamilyRelationshipsFromProxyUseCase)
    factoryOf(::GetFamilyRelationshipsUseCase)
    factoryOf(::InquiryRegistryUseCase)
    factoryOf(::InquiryEducationCodeUseCase)
    factoryOf(::UploadDependentImageUseCase)
    factoryOf(::AddNewDependentUseCase)
    factoryOf(::GetDependentInfoUseCase)
    factoryOf(::DeepLinkManagerImpl) bind DeepLinkManager::class
    factoryOf(::AuthAuthorizeUrlUseCaseImpl) bind AuthAuthorizeUrlUseCase::class
    factoryOf(::ExchangeCodeForTokensUseCaseImpl) bind ExchangeCodeForTokensUseCase::class
    factoryOf(::HandleAuthDeepLinkUseCaseImpl) bind HandleAuthDeepLinkUseCase::class
    factoryOf(::UserProfileImageUseCase)
    factoryOf(::TaminRelationUseCase)
    factoryOf(::IdentityInfoUseCase)
    factoryOf(::GetPensionInquiryUseCase)
    factoryOf(::GetPensionerIdUseCase)
    factoryOf(::GetPensionerPayRollUseCase)
    factoryOf(::GetPensionerPayRollPDFUseCase)
    factoryOf(::GetUserAgeUseCase)
    factoryOf(::GetRetirementRequestInfoUseCase)
    factoryOf(::CheckRetirementStatusUseCase)
    factoryOf(::GetEdictPensionerUseCase)
    factoryOf(::GetEdictReportPDFUseCase)
    factoryOf(::SendEdictPensionerToMyInboxUseCase)
    factoryOf(::SendImageRequestUseCase)
    factoryOf(::SubdominantUseCase)
    factoryOf(::MockSubdominantUseCase)
    factoryOf(::SignOutUseCase)
    factoryOf(::GetSignOutUrlUseCase)
    factoryOf(::GetBankAccountListUseCase)
    factoryOf(::RegisterBankAccountUseCase)
    factoryOf(::GetInsuredActiveBranchUseCase)
    factoryOf(::GetRelationTaminAllUseCase)
    factoryOf(::GetStatusCertificateReportUseCase)
    factoryOf(::GetRecipientsUseCase)
    factoryOf(::GetElectronicFileUseCase)
    factoryOf(::DownloadDocumentUseCase)
    factoryOf(::GetRecipientListUseCase)
    factoryOf(::GetPersonalInfoUseCase)
    factoryOf(::GetDeceasedInfoUseCase)
    factoryOf(::GetDisabilityDependentInfoUseCase)
    factoryOf(::CheckGirlSurvivorConditionsUseCase)
    factoryOf(::GetConfirmSurvivorsListUseCase)
    factoryOf(::GetAgeUseCase)
    factoryOf(::GetCitiesUseCase)
    factoryOf(::ChangeMobileUseCase)
    factoryOf(::VerifyChangeMobileUseCase)
    factoryOf(::GetBeneficiaryUseCase)
    factoryOf(::GetMainMenuUseCase)
    factoryOf(::GetUserRequestsUseCase)
    factoryOf(::GetUserRequestTypesUseCase)
    factoryOf(::GetTalfighInfosUseCase)
    factoryOf(::GetDastmozdInfosUseCase)
    factoryOf(::GetHistoryJobInfosUseCase)
    factoryOf(::GetPersonalInboxItemsUseCase)
    factoryOf(::GetPersonalInboxSizeUseCase)
    factoryOf(::GetMyRequestPdfUseCase)
    factoryOf(::DeleteMyRequestUseCase)
    factoryOf(::InboxInquiryLicenseUseCase)
    factoryOf(::GetContractsUseCase)
    factoryOf(::GetRegistrationInfoUseCase)
    factoryOf(::GetBranchesUseCase)
    factoryOf(::GetSpcPremiumRatesUseCase)
    factoryOf(::GetFreelancePremiumRangeUseCase)
    factoryOf(::CalculateFreelanceSalaryUseCase)
    factoryOf(::CalculateOptionalSalaryUseCase)
    factoryOf(::MakeFreelanceContractUseCase)
    factoryOf(::MakeContractUseCase)
    factoryOf(::MakeFreelanceContractByGuardianUseCase)
    factoryOf(::MakeOptionalContractByGuardianUseCase)
    factoryOf(::GetInsurancePaymentUseCase)
    factoryOf(::CheckInsurancePaymentStatusUseCase)
    factoryOf(::SaveContactUseCase)
    factoryOf(::GetFreeJobWagesUseCase)
    factoryOf(::UploadImageUseCase)
    factoryOf(::GetDisabilityPersonalInfoUseCase)
    // Agent
    factoryOf(::SendAgentPromptUseCase)
    factoryOf(::CheckChatAllowedUseCase)
    // Agent conversation cache
    factory<GetCurrentUserNationalCodeUseCase> { GetCurrentUserNationalCodeUseCaseImpl(get()) }
    factoryOf(::PruneEmptyAgentSessionUseCase)
    factoryOf(::GetAgentSessionsUseCase)
    factoryOf(::GetAgentSessionUseCase)
    factoryOf(::StartAgentSessionUseCase)
    factoryOf(::SaveCachedMessageUseCase)
    factoryOf(::GetCachedMessagesUseCase)
    factoryOf(::ObserveCachedMessagesUseCase)
    factoryOf(::DeletePendingAgentMessagesUseCase)
    factoryOf(::DeleteAgentSessionUseCase)
    factoryOf(::UpdateAgentSessionUseCase)

    factoryOf(::SendRetirementDocumentUseCase)
    factoryOf(::GetRolesUseCase)


    // Treatment UseCases
    factoryOf(::GetDeservedTreatmentUseCase)
    factoryOf(::GetElectronicPrescriptionListUseCase)
    factoryOf(::GetElectronicPrescriptionDetailUseCase)
    factoryOf(::GetElectronicPrescriptionPriceUseCase)
    factoryOf(::GetDependantUnderEighteenUseCase)
    factoryOf(::GetPrescriptionPdfFileUseCase)
    factoryOf(::DownloadLabResultPdfUseCase)
    factoryOf(::GetUserProfileUseCase)
    factoryOf(::GetJobTitleUseCase)
    factoryOf(::GetRegistrationDeclarationFormUseCase)
    factoryOf(::GetRequestSummaryUseCase)
    factoryOf(::PutInsuredRegistrationDocListUseCase)
    factoryOf(::CheckUserIsNewUseCase)
    // کارگاه‌های کارفرما — the list, then one group per action it launches
    factoryOf(::GetEmployerAgreementsUseCase)
    factoryOf(::GetPaymentSheetsUseCase)
    factoryOf(::GetDebitReasonsUseCase)
    factoryOf(::GetWorkshopDebitsUseCase)
    factoryOf(::GetDemandDocumentsUseCase)
    factoryOf(::GetDebitTurnoverPdfUseCase)
    factoryOf(::PayWorkshopDebitUseCase)
    factoryOf(::GetWorkshopDebtInquiryUseCase)
    factoryOf(::GetObjectionableDebitsUseCase)
    factoryOf(::CheckObjectionDeadlineUseCase)
    factoryOf(::SaveDebitObjectionUseCase)
    factoryOf(::GetDebitObjectionPdfUseCase)
    factoryOf(::GetRecentlyAddedMembersUseCase)
    factoryOf(::ConfirmRecentlyAddedMemberUseCase)
    factoryOf(::DeleteRecentlyAddedMemberUseCase)
    factoryOf(::GetArticle16DebtsUseCase)
    factoryOf(::GetArticle16WorkshopInfoUseCase)
    factoryOf(::GetArticle16RequestInfoUseCase)
    factoryOf(::SaveArticle16RequestUseCase)
    factoryOf(::GetArticle16ReportPdfUseCase)
    factoryOf(::GetWorkshopMembersUseCase)
    factoryOf(::GetWorkshopStackHoldersUseCase)
    factoryOf(::GetTreatmentCostsUseCase)
    factoryOf(::GetTreatmentCostsPDFUseCase)
    factoryOf(::SendToInboxTreatmentCostsUseCase)
    factoryOf(::GetMedicalConfirmationsUseCase)
    factoryOf(::GetMedicalConfirmationPDFUseCase)
    factoryOf(::SendToInboxMedicalConfirmationUseCase)


    // Health UseCases
    factoryOf(::GetPatientGeneralUseCase)
    factoryOf(::GetPatientSelfDeclarativeUseCase)
    factoryOf(::GetPatientDrugAllergiesUseCase)
    factoryOf(::GetPatientHospitalizationsUseCase)
    factoryOf(::GetPatientVisitsUseCase)
    factoryOf(::GetPatientLabsUseCase)
    factoryOf(::GetPatientImagingUseCase)
    factoryOf(::GetVersionHistoryUseCase)
    factoryOf(::SetThemeUseCase)
    factoryOf(::SetBiometricEnabledUseCase)
    factoryOf(::CompleteBiometricEnrollmentPromptUseCase)
    factoryOf(::SetFontSizeUseCase)
    factoryOf(::GetAllProvincesUseCase)
    factoryOf(::GetProvinceCitiesUseCase)
    factoryOf(::GetBloodGroupsUseCase)
    factoryOf(::GetMaritalStatusUseCase)
    factoryOf(::GetRelationTypesUseCase)
    factoryOf(::GetSmokingStatusUseCase)
    factoryOf(::GetSelfDeclarableIllnessesUseCase)
    factoryOf(::GetSelfDeclarableIllnessesByGroupUseCase)
    factoryOf(::GetAllDrugsUseCase)
    factoryOf(::UpdatePatientUseCase)
    factoryOf(::AddSelfDeclarativeUseCase)
    factoryOf(::UpdateSelfDeclarativeUseCase)
    factoryOf(::SyncIllnessSelfDeclarativesUseCase)
    factoryOf(::SyncDrugAllergiesUseCase)
    factoryOf(::GetActFrequenciesUseCase)
    factoryOf(::GetContactUsUseCase)

    // Add Dependent UseCases
    factoryOf(::GetDependentInfoUseCase)
    factoryOf(::GetActiveBranchesUseCase)
    factoryOf(::GetFamilyRelationshipsUseCase)
    factoryOf(::GetFamilyRelationshipsFromProxyUseCase)
    factoryOf(::InquiryRegistryUseCase)
    factoryOf(::InquiryEducationCodeUseCase)
    factoryOf(::UploadDependentImageUseCase)
    factoryOf(::AddNewDependentUseCase)
}

