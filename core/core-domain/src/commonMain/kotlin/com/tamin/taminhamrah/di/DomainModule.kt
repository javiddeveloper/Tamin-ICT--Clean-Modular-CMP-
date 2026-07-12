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
import com.tamin.taminhamrah.useCases.common.GetRecipientListUseCase
import com.tamin.taminhamrah.useCases.common.GetBeneficiaryUseCase
import com.tamin.taminhamrah.useCases.common.GetMainMenuUseCase
import com.tamin.taminhamrah.useCases.file.GetElectronicFileUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.pension.GetEdictPensionerUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionInquiryUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollPDFUseCase
import com.tamin.taminhamrah.useCases.pension.GetUserAgeUseCase
import com.tamin.taminhamrah.useCases.pension.GetRetirementRequestInfoUseCase
import com.tamin.taminhamrah.useCases.personalInbox.GetPersonalInboxItemsUseCase
import com.tamin.taminhamrah.useCases.personalInbox.GetPersonalInboxSizeUseCase
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
import com.tamin.taminhamrah.useCases.user.ChangeMobileUseCase
import com.tamin.taminhamrah.useCases.user.SendImageRequestUseCase
import com.tamin.taminhamrah.useCases.auth.SignOutUseCase
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
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
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.pension.GetDisabilityPersonalInfoUseCase
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
import com.tamin.taminhamrah.useCases.treatment.DownloadTestResultPdfUseCase
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import com.tamin.taminhamrah.useCases.workshops.GetAllEmployerAgreementByNationalIdUseCase
import com.tamin.taminhamrah.useCases.workshops.GetAllPaymentSheetsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopDebitUseCase
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopDebtInquiryUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
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
    factoryOf(::GetEdictPensionerUseCase)
    factoryOf(::SendImageRequestUseCase)
    factoryOf(::SubdominantUseCase)
    factoryOf(::SignOutUseCase)
    factoryOf(::GetSignOutUrlUseCase)
    factoryOf(::GetBankAccountListUseCase)
    factoryOf(::GetInsuredActiveBranchUseCase)
    factoryOf(::GetRelationTaminAllUseCase)
    factoryOf(::GetElectronicFileUseCase)
    factoryOf(::GetRecipientListUseCase)
    factoryOf(::GetPersonalInfoUseCase)
    factoryOf(::GetDeceasedInfoUseCase)
    factoryOf(::GetDisabilityDependentInfoUseCase)
    factoryOf(::CheckGirlSurvivorConditionsUseCase)
    factoryOf(::GetConfirmSurvivorsListUseCase)
    factoryOf(::GetAgeUseCase)
    factoryOf(::ChangeMobileUseCase)
    factoryOf(::VerifyChangeMobileUseCase)
    factoryOf(::GetBeneficiaryUseCase)
    factoryOf(::GetMainMenuUseCase)
    factoryOf(::GetUserRequestsUseCase)
    factoryOf(::GetUserRequestTypesUseCase)
    factoryOf(::GetTalfighInfosUseCase)
    factoryOf(::GetDastmozdInfosUseCase)
    factoryOf(::GetPersonalInboxItemsUseCase)
    factoryOf(::GetPersonalInboxSizeUseCase)
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
    factoryOf(::GetAllEmployerAgreementByNationalIdUseCase)
    factoryOf(::GetAllPaymentSheetsUseCase)
    factoryOf(::GetWorkshopDebitUseCase)
    factoryOf(::GetWorkshopDebtInquiryUseCase)
    factoryOf(::GetDisabilityPersonalInfoUseCase)

    // Treatment UseCases
    factoryOf(::GetDeservedTreatmentUseCase)
    factoryOf(::GetElectronicPrescriptionListUseCase)
    factoryOf(::GetElectronicPrescriptionDetailUseCase)
    factoryOf(::GetElectronicPrescriptionPriceUseCase)
    factoryOf(::GetDependantUnderEighteenUseCase)
    factoryOf(::GetPrescriptionPdfFileUseCase)
    factoryOf(::DownloadTestResultPdfUseCase)
    factoryOf(::GetUserProfileUseCase)
}
