package com.tamin.taminhamrah.ui.navigation

import androidx.navigation.NavController
import com.tamin.taminhamrah.feature.contractaffair.navigateToContractAffairs
import com.tamin.taminhamrah.feature.contracts.navigateToContracts
import com.tamin.taminhamrah.feature.history.navigateToHistory
import com.tamin.taminhamrah.feature.history.navigateToHistoryJobInfo
import com.tamin.taminhamrah.feature.historyobjection.navigateToHistoryObjection
import com.tamin.taminhamrah.feature.deferredInstallment.navigateToDeferredInstallment
import com.tamin.taminhamrah.feature.orotezprotez.navigateToOrotezProtez
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.navigateToRequestPaymentForIllDays
import com.tamin.taminhamrah.feature.pregnancyPay.navigateToPregnancyPay
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToCalculatePension
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToDeservedTreatment
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToDisabilityPension
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToEdict
import com.tamin.taminhamrah.feature.girlSurvivor.navigateToGirlSurvivor
import com.tamin.taminhamrah.feature.inquiryEducation.navigateToInquiryEducation
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToIssuanceCertificate
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPayRoll
import com.tamin.taminhamrah.feature.pensionStatusInquiry.navigateToPensionStatusInquiry
import com.tamin.taminhamrah.feature.pensionSurvivor.navigateToPensionSurvivor
import com.tamin.taminhamrah.feature.retirementPension.navigateToRetirementPension
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPrescription
import com.tamin.taminhamrah.feature.contracts.navigateToContractFlow
import com.tamin.taminhamrah.feature.contracts.flow.ContractType
import com.tamin.taminhamrah.feature.taminServices.navigateToOccurrence
import com.tamin.taminhamrah.feature.taminServices.navigateToInspection
import com.tamin.taminhamrah.feature.taminServices.navigateToEmployerOnlineServices
import com.tamin.taminhamrah.feature.taminServices.navigateToSendInsuranceHistoryToInstitutions
import com.tamin.taminhamrah.feature.workshops.navigateToAssignerContracts
import com.tamin.taminhamrah.feature.workshops.navigateToContractRows
import com.tamin.taminhamrah.feature.workshops.navigateToWorkshops
import com.tamin.taminhamrah.feature.workshops.navigateToCompleteEmployerInfo
import com.tamin.taminhamrah.feature.workshops.navigateToLegalRepresentativeWorkshops
import com.tamin.taminhamrah.feature.workshops.navigateToDebtObjectionStatus
import com.tamin.taminhamrah.model.common.FeatureFlag

fun NavController.navigateToFeature(flag: FeatureFlag) {
    when (flag) {
        // «مجموع سوابق» — the insured years added up. Menu id 8; it reached nothing before.
        FeatureFlag.COMBINED_RECORD -> navigateToHistory()
        FeatureFlag.MERGE_HISTORY -> navigateToHistory()
        // «سوابق و دستمزد» — menu id 7. The same page: it is where the wage rows are read, and it
        // reached nothing before.
        FeatureFlag.WAGE_AND_HISTORY -> navigateToHistory()
        FeatureFlag.WORKSHOPS -> navigateToWorkshops()
        // «اطلاعات پیمان» in the server menu; the screen it opens is titled «ردیف‌های پیمان».
        FeatureFlag.CONTRACT_INFO -> navigateToContractRows()
        // «واگذارندگان» (1003) — the پیمان‌ها this employer assigned out.
        FeatureFlag.ASSIGNER_CONTRACT -> navigateToAssignerContracts()
        FeatureFlag.COMPLETE_WORKSHOP_INFO -> navigateToCompleteEmployerInfo()
        FeatureFlag.STACK_HOLDER_LIST -> navigateToLegalRepresentativeWorkshops()
        FeatureFlag.CONTRACTS -> navigateToContractAffairs()
        FeatureFlag.FOLLOW_PROTEST_STATUS -> navigateToDebtObjectionStatus()
        FeatureFlag.STUDENT_INSURANCE -> navigateToContractFlow(ContractType.STUDENT)
        FeatureFlag.FREELANCE_INSURANCE -> navigateToContractFlow(ContractType.FREELANCE)
        FeatureFlag.OPTIONAL_INSURANCE -> navigateToContractFlow(ContractType.OPTIONAL)
        FeatureFlag.HOUSEWIFE_INSURANCE -> navigateToContractFlow(ContractType.HOUSEWIFE)
        FeatureFlag.PENSION_INQUIRY -> navigateToPensionStatusInquiry()
        FeatureFlag.RETIREMENT_PENSION -> navigateToRetirementPension()
        FeatureFlag.CALCULATE_WAGE_PENSION -> navigateToCalculatePension()
        FeatureFlag.PRESCRIPTION -> navigateToPrescription()
        FeatureFlag.DESERVED_TREATMENT_101 -> navigateToDeservedTreatment()
        FeatureFlag.PAY_ROLL -> navigateToPayRoll()
        FeatureFlag.EDICT_PENSIONER -> navigateToEdict()
        FeatureFlag.ISSUANCE_WAGE_CERTIFICATE -> navigateToIssuanceCertificate()
        FeatureFlag.DEFERRED_INSTALLMENT_CERTIFICATE -> navigateToDeferredInstallment()
        FeatureFlag.GIRL_SURVIVOR -> navigateToGirlSurvivor()
        FeatureFlag.REQUEST_PENSION_BY_SURVIVOR,
        FeatureFlag.REQUEST_PENSION_BY_SURVIVOR_112 -> navigateToPensionSurvivor()
        FeatureFlag.DISABILITY_PENSION -> navigateToDisabilityPension()
        FeatureFlag.VIEW_TITLE_JOB -> navigateToHistoryJobInfo()
        FeatureFlag.SEND_INSURANCE_HISTORY_TO_INSTITUTION -> navigateToSendInsuranceHistoryToInstitutions()
        FeatureFlag.OROTEZ_PROTEZ -> navigateToOrotezProtez()
        FeatureFlag.REQUEST_PAYMENT_FOR_ILL_DAYS -> navigateToRequestPaymentForIllDays()
        FeatureFlag.OCCURRENCE -> navigateToOccurrence()
        FeatureFlag.LIST_OF_INSPECTIONS_PERFORMED -> navigateToInspection()
        FeatureFlag.REGISTER_AGREEMENT -> navigateToEmployerOnlineServices()
        FeatureFlag.OBJECTION_NON_EXISTENT_HISTORY -> navigateToHistoryObjection()
        FeatureFlag.INQUIRY_EDUCATION -> navigateToInquiryEducation()
        FeatureFlag.REQUEST_FOR_PREGNANCY_PAY -> navigateToPregnancyPay()
        else -> Unit
    }
}
