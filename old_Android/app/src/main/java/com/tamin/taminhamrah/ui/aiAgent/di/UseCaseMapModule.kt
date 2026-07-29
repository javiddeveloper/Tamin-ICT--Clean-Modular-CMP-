package com.tamin.taminhamrah.ui.aiAgent.di

import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.LawUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.appointment.AppointmentUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd.AverageWagePerDateUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd.AverageWageUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd.DastmozInforCalcIllnessPensionerUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd.DastmozdInfosLastUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd.DastmozdInfosPerYearUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd.DastmozdInfosSalaryUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd.DastmozdInfosSumTotalUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd.DastmozdInfosUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.profile.DependentsUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editBankAccount.EditBankAccountNumberUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editBankAccount.EditBankAccountGetUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editBankAccount.EditBankAccountSubmitUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editBankAccount.EditBankAccountCancelUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editPhoneNumber.EditPhoneNumberUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editPhoneNumber.EditPhoneNumberGetUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editPhoneNumber.EditPhoneNumberSendOtpUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editPhoneNumber.EditPhoneNumberVerifyOtpUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editPhoneNumber.EditPhoneNumberCancelUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.prescription.ElectronicPrescriptionLastUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.prescription.ElectronicPrescriptionUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.payment.WorkerPaymentUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.pregnancy.PregnancyPayUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.pension.EligibleAmountPensionUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.fish.FishLastUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.fish.FishUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.common.GeneralResponseUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.jobHistory.HistoryServicesUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.hokm.HokmLastUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.hokm.HokmUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.jobHistory.JobHistoryAllUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.jobHistory.JobHistoryLastUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd.LastPayUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.tracking.LastTrackingCodeUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.tracking.TrackingCodeUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.treatment.MedicalEntitlementUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.message.MessageUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.common.NotAvailableUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.pension.PensionInquireLastUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.pension.PensionInquiryAllUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.profile.AddDependentUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.profile.ProfileUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.illness.RepIllnessLastUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.illness.RepIllnessUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.treatment.TreatmentCostsUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.illness.WageCompensationUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.DependentCancellationCancelUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.DependentCancellationConfirmUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.DependentCancellationGetUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.DependentCancellationSubmitUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation.DependentCancellationUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.disabilityPension.DisabilityPensionUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.jobHistory.HistoryServicesLastUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.inquiryEducation.InquiryEducationUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.inquiryEducation.InquiryEducationGetUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.inquiryEducation.InquiryEducationSubmitUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.inquiryEducation.InquiryEducationCancelUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.orthosis.OrthosisUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.weddingPresent.WeddingPresentUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.weddingPresent.WeddingPresentGetUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.weddingPresent.WeddingPresentValidateUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.weddingPresent.WeddingPresentCalculateUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.weddingPresent.WeddingPresentSubmitUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.weddingPresent.WeddingPresentCancelUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.deferredInstallmentCertificate.DeferredInstallmentCertificateUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.medicalAuthorities.ConfirmationMedicalAuthoritiesUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.contract.RegisterContractUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.funeralAllowance.FuneralAllowanceGetUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.funeralAllowance.FuneralAllowanceValidateUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.funeralAllowance.FuneralAllowanceSaveUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.funeralAllowance.FuneralAllowanceConfirmUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.funeralAllowance.FuneralAllowanceCancelUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.legalWorkshop.LegalWorkShopUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence.OccurrenceAccidentUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence.OccurrenceCancelUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence.OccurrencePersonalUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence.OccurrenceReportGetUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence.OccurrenceReportUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence.OccurrenceSubmitUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence.OccurrenceWorkshopUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UseCaseMapModule {

    @Provides
    @Singleton
    fun provideChatActionUseCaseMap(
        generalResponseUseCase: GeneralResponseUseCase,
        dastmozdInfosSumTotalUseCase: DastmozdInfosSumTotalUseCase,
        dastmozdInfosUseCase: DastmozdInfosUseCase,
        dastmozdInfosSalaryUseCase: DastmozdInfosSalaryUseCase,
        dastmozdInfosPerYearUseCase: DastmozdInfosPerYearUseCase,
        dastmozdInfosLastUseCase: DastmozdInfosLastUseCase,
        averageWageUseCase: AverageWageUseCase,
        averageWagePerDateUseCase: AverageWagePerDateUseCase,
        fishUseCase: FishUseCase,
        fishLastUseCase: FishLastUseCase,
        dependentsUseCase: DependentsUseCase,
        electronicPrescriptionUseCase: ElectronicPrescriptionUseCase,
        electronicPrescriptionLastUseCase: ElectronicPrescriptionLastUseCase,
        historyServicesUseCase: HistoryServicesUseCase,
        jobHistoryAllUseCase: JobHistoryAllUseCase,
        lastPayUseCase: LastPayUseCase,
        jobHistoryLastUseCase: JobHistoryLastUseCase,
        medicalEntitlementUseCase: MedicalEntitlementUseCase,
        pensionInquiryUseCase: PensionInquiryAllUseCase,
        pensionInquireLastUseCase: PensionInquireLastUseCase,
        wageCompensationUseCase: WageCompensationUseCase,
        treatmentCostsUseCase: TreatmentCostsUseCase,
        eligibleAmountPensionUseCase: EligibleAmountPensionUseCase,
        hokmUseCase: HokmUseCase,
        hokmLastUseCase: HokmLastUseCase,
        editPhoneNumberUseCase: EditPhoneNumberUseCase,
        editPhoneNumberGetUseCase: EditPhoneNumberGetUseCase,
        editPhoneNumberSendOtpUseCase: EditPhoneNumberSendOtpUseCase,
        editPhoneNumberVerifyOtpUseCase: EditPhoneNumberVerifyOtpUseCase,
        editPhoneNumberCancelUseCase: EditPhoneNumberCancelUseCase,
        appointmentUseCase: AppointmentUseCase,
        repIllnessLastUseCase: RepIllnessLastUseCase,
        repIllnessUseCase: RepIllnessUseCase,
        trackingCodeUseCase: TrackingCodeUseCase,
        lastTrackingCodeUseCase: LastTrackingCodeUseCase,
        messageUseCase: MessageUseCase,
        editBankAccountNumberUseCase: EditBankAccountNumberUseCase,
        editBankAccountGetUseCase: EditBankAccountGetUseCase,
        editBankAccountSubmitUseCase: EditBankAccountSubmitUseCase,
        editBankAccountCancelUseCase: EditBankAccountCancelUseCase,
        notAvailableUseCase: NotAvailableUseCase,
        profileUseCase: ProfileUseCase,
        addDependentUseCase: AddDependentUseCase,
        dastmozInforCalcIllnessPensionerUseCase: DastmozInforCalcIllnessPensionerUseCase,
        lawUseCase: LawUseCase,
        workerPaymentUseCase: WorkerPaymentUseCase,
        pregnancyPayUseCase: PregnancyPayUseCase,
        dependentCancellationUseCase: DependentCancellationUseCase,
        dependentCancellationGetUseCase: DependentCancellationGetUseCase,
        dependentCancellationSubmitUseCase: DependentCancellationSubmitUseCase,
        dependentCancellationConfirmUseCase: DependentCancellationConfirmUseCase,
        dependentCancellationCancelUseCase: DependentCancellationCancelUseCase,
        orthosisUseCase: OrthosisUseCase,
        deferredInstallmentCertificateUseCase: DeferredInstallmentCertificateUseCase,
        weddingPresentUseCase: WeddingPresentUseCase,
        weddingPresentGetUseCase: WeddingPresentGetUseCase,
        weddingPresentValidateUseCase: WeddingPresentValidateUseCase,
        weddingPresentCalculateUseCase: WeddingPresentCalculateUseCase,
        weddingPresentSubmitUseCase: WeddingPresentSubmitUseCase,
        weddingPresentCancelUseCase: WeddingPresentCancelUseCase,
        disabilityPensionUseCase: DisabilityPensionUseCase,
        inquiryEducationUseCase: InquiryEducationUseCase,
        inquiryEducationGetUseCase: InquiryEducationGetUseCase,
        inquiryEducationSubmitUseCase: InquiryEducationSubmitUseCase,
        inquiryEducationCancelUseCase: InquiryEducationCancelUseCase,
        registerContractUseCase: RegisterContractUseCase,
        funeralAllowanceGetUseCase: FuneralAllowanceGetUseCase,
        funeralAllowanceValidateUseCase: FuneralAllowanceValidateUseCase,
        funeralAllowanceSaveUseCase: FuneralAllowanceSaveUseCase,
        funeralAllowanceConfirmUseCase: FuneralAllowanceConfirmUseCase,
        funeralAllowanceCancelUseCase: FuneralAllowanceCancelUseCase,
        confirmationMedicalAuthoritiesUseCase: ConfirmationMedicalAuthoritiesUseCase,
        historyServicesLastUseCase: HistoryServicesLastUseCase,
        occurrenceReportUseCase: OccurrenceReportUseCase,
        occurrenceReportGetUseCase: OccurrenceReportGetUseCase,
        occurrenceWorkshopUseCase: OccurrenceWorkshopUseCase,
        occurrencePersonalUseCase: OccurrencePersonalUseCase,
        occurrenceAccidentUseCase: OccurrenceAccidentUseCase,
        occurrenceSubmitUseCase: OccurrenceSubmitUseCase,
        occurrenceCancelUseCase: OccurrenceCancelUseCase,
        legalWorkShopUseCase: LegalWorkShopUseCase

    ): List<@JvmSuppressWildcards ServiceUseCase> {
        return listOf(
            generalResponseUseCase,
            dastmozdInfosSumTotalUseCase,
            dastmozdInfosUseCase,
            dastmozdInfosSalaryUseCase,
            dastmozdInfosPerYearUseCase,
            dastmozdInfosLastUseCase,
            fishUseCase,
            averageWageUseCase,
            averageWagePerDateUseCase,
            dependentsUseCase,
            electronicPrescriptionUseCase,
            historyServicesUseCase,
            jobHistoryAllUseCase,
            lastPayUseCase,
            medicalEntitlementUseCase,
            pensionInquiryUseCase,
            appointmentUseCase,
            wageCompensationUseCase,
            jobHistoryLastUseCase,
            treatmentCostsUseCase,
            hokmUseCase,
            hokmLastUseCase,
            editPhoneNumberUseCase,
            editPhoneNumberGetUseCase,
            editPhoneNumberSendOtpUseCase,
            editPhoneNumberVerifyOtpUseCase,
            editPhoneNumberCancelUseCase,
            eligibleAmountPensionUseCase,
            pensionInquireLastUseCase,
            repIllnessLastUseCase,
            repIllnessUseCase,
            lastTrackingCodeUseCase,
            notAvailableUseCase,
            trackingCodeUseCase,
            editBankAccountNumberUseCase,
            editBankAccountGetUseCase,
            editBankAccountSubmitUseCase,
            editBankAccountCancelUseCase,
            profileUseCase,
            fishLastUseCase,
            electronicPrescriptionLastUseCase,
            addDependentUseCase,
            dastmozInforCalcIllnessPensionerUseCase,
            messageUseCase,
            lawUseCase,
            workerPaymentUseCase,
            pregnancyPayUseCase,
            dependentCancellationUseCase,
            dependentCancellationGetUseCase,
            dependentCancellationSubmitUseCase,
            dependentCancellationConfirmUseCase,
            dependentCancellationCancelUseCase,
            historyServicesLastUseCase,
            orthosisUseCase,
            disabilityPensionUseCase,
            inquiryEducationUseCase,
            inquiryEducationGetUseCase,
            inquiryEducationSubmitUseCase,
            inquiryEducationCancelUseCase,
            deferredInstallmentCertificateUseCase,
            weddingPresentUseCase,
            weddingPresentGetUseCase,
            weddingPresentValidateUseCase,
            weddingPresentCalculateUseCase,
            weddingPresentSubmitUseCase,
            weddingPresentCancelUseCase,
            confirmationMedicalAuthoritiesUseCase,
            historyServicesLastUseCase,
            registerContractUseCase,
            funeralAllowanceGetUseCase,
            funeralAllowanceValidateUseCase,
            funeralAllowanceSaveUseCase,
            funeralAllowanceConfirmUseCase,
            funeralAllowanceCancelUseCase,
            orthosisUseCase,
            occurrenceReportUseCase,
            occurrenceReportGetUseCase,
            occurrenceWorkshopUseCase,
            occurrencePersonalUseCase,
            occurrenceAccidentUseCase,
            occurrenceSubmitUseCase,
            occurrenceCancelUseCase,
            legalWorkShopUseCase
        )
    }

}
