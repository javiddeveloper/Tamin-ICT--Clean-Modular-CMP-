package com.tamin.taminhamrah.ui.navigation

import androidx.navigation.NavController
import com.tamin.taminhamrah.deeplink.DeepLinkKey
import com.tamin.taminhamrah.deeplink.PrescriptionDetailLink
import com.tamin.taminhamrah.feature.agent.navigateToAgent
import com.tamin.taminhamrah.feature.contractaffair.navigateToContractAffairs
import com.tamin.taminhamrah.feature.contracts.flow.ContractType
import com.tamin.taminhamrah.feature.contracts.navigateToContractFlow
import com.tamin.taminhamrah.feature.deferredInstallment.navigateToDeferredInstallment
import com.tamin.taminhamrah.feature.girlSurvivor.navigateToGirlSurvivor
import com.tamin.taminhamrah.feature.history.navigateToHistory
import com.tamin.taminhamrah.feature.history.navigateToHistoryJobInfo
import com.tamin.taminhamrah.feature.historyobjection.navigateToHistoryObjection
import com.tamin.taminhamrah.feature.inquiryEducation.navigateToInquiryEducation
import com.tamin.taminhamrah.feature.orotezprotez.navigateToOrotezProtez
import com.tamin.taminhamrah.feature.calculateWagePension.navigateToCalculateWagePension
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToDeservedTreatment
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToDisabilityPension
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToEdict
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToIssuanceCertificate
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPayRoll
import com.tamin.taminhamrah.feature.pensionStatusInquiry.navigateToPensionStatusInquiry
import com.tamin.taminhamrah.feature.pensionSurvivor.navigateToPensionSurvivor
import com.tamin.taminhamrah.feature.pregnancyPay.navigateToPregnancyPay
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.navigateToRequestPaymentForIllDays
import com.tamin.taminhamrah.feature.retirementPension.navigateToRetirementPension
import com.tamin.taminhamrah.feature.taminServices.navigateToEmployerOnlineServices
import com.tamin.taminhamrah.feature.taminServices.navigateToInspection
import com.tamin.taminhamrah.feature.taminServices.navigateToOccurrence
import com.tamin.taminhamrah.feature.taminServices.navigateToSendInsuranceHistoryToInstitutions
import com.tamin.taminhamrah.feature.taminServices.navigateToWorkersPaymentInfo
import com.tamin.taminhamrah.feature.treatment.navigateToPrescriptionDetail
import com.tamin.taminhamrah.feature.treatment.navigateToPrescriptions
import com.tamin.taminhamrah.feature.weddingPresent.navigateToWeddingPresent
import com.tamin.taminhamrah.feature.weddingPresent.navigateToWeddingPresentCalculate
import com.tamin.taminhamrah.feature.workshops.navigateToAssignerContracts
import com.tamin.taminhamrah.feature.workshops.navigateToCompleteEmployerInfo
import com.tamin.taminhamrah.feature.workshops.navigateToContractRows
import com.tamin.taminhamrah.feature.workshops.navigateToDebtObjectionStatus
import com.tamin.taminhamrah.feature.workshops.navigateToLegalRepresentativeWorkshops
import com.tamin.taminhamrah.feature.workshops.navigateToWorkshops
import com.tamin.taminhamrah.model.common.FeatureFlag

/**
 * Opens the screen behind [flag]. Returns false when no screen exists for it in this app yet (or
 * only a placeholder does), so the caller can say the service is unavailable instead of silently
 * doing nothing or showing a blank page. [beforeOpen] runs only when a screen is about to open.
 *
 * This does not check the flag. Menu taps check it in their view models; links check it in
 * `ResolveDeepLinkUseCase` — go through one of those, never straight here.
 */
/**
 * Opens what a resolved deep link points at. A key whose screen takes arguments is opened with
 * them — `prescription_detail` opens that prescription; without usable arguments, or for any other
 * key, the flag's own screen opens. The flag has already been checked by the gate.
 */
fun NavController.navigateToDeepLink(key: DeepLinkKey, args: Map<String, String>, beforeOpen: () -> Unit = {}): Boolean {
    if (key == DeepLinkKey.PRESCRIPTION_DETAIL) {
        PrescriptionDetailLink.fromArgs(args)?.let { link ->
            beforeOpen()
            navigateToPrescriptionDetail(link.patientNationalCode, link.noteHeadId, link.type, link.flagSata)
            return true
        }
    }
    return navigateToFeature(key.flag, beforeOpen)
}

fun NavController.navigateToFeature(flag: FeatureFlag, beforeOpen: () -> Unit = {}): Boolean {
    val open: () -> Unit = when (flag) {
        FeatureFlag.AGENT -> screen { navigateToAgent() }
        // «مجموع سوابق» — the insured years added up. Menu id 8; it reached nothing before.
        FeatureFlag.COMBINED_RECORD -> screen { navigateToHistory() }
        FeatureFlag.MERGE_HISTORY -> screen { navigateToHistory() }
        // «سوابق و دستمزد» — menu id 7. The same page: it is where the wage rows are read, and it
        // reached nothing before.
        FeatureFlag.WAGE_AND_HISTORY -> screen { navigateToHistory() }
        FeatureFlag.WORKSHOPS -> screen { navigateToWorkshops() }
        // «اطلاعات پیمان» in the server menu; the screen it opens is titled «ردیف‌های پیمان».
        FeatureFlag.CONTRACT_INFO -> screen { navigateToContractRows() }
        // «واگذارندگان» (1003) — the پیمان‌ها this employer assigned out.
        FeatureFlag.ASSIGNER_CONTRACT -> screen { navigateToAssignerContracts() }
        FeatureFlag.COMPLETE_WORKSHOP_INFO -> screen { navigateToCompleteEmployerInfo() }
        FeatureFlag.STACK_HOLDER_LIST -> screen { navigateToLegalRepresentativeWorkshops() }
        FeatureFlag.CONTRACTS -> screen { navigateToContractAffairs() }
        FeatureFlag.FOLLOW_PROTEST_STATUS -> screen { navigateToDebtObjectionStatus() }
        FeatureFlag.STUDENT_INSURANCE -> screen { navigateToContractFlow(ContractType.STUDENT) }
        FeatureFlag.FREELANCE_INSURANCE -> screen { navigateToContractFlow(ContractType.FREELANCE) }
        FeatureFlag.OPTIONAL_INSURANCE -> screen { navigateToContractFlow(ContractType.OPTIONAL) }
        FeatureFlag.HOUSEWIFE_INSURANCE -> screen { navigateToContractFlow(ContractType.HOUSEWIFE) }
        FeatureFlag.PENSION_INQUIRY -> screen { navigateToPensionStatusInquiry() }
        FeatureFlag.CALCULATE_WAGE_PENSION,
        FeatureFlag.CALCULATE_WAGE_PENSION_109 -> screen { navigateToCalculateWagePension() }
        FeatureFlag.RETIREMENT_PENSION -> screen { navigateToRetirementPension() }
        // «نسخه‌های الکترونیک» lives in the treatment tab; the pensioner module's PrescriptionScreen
        // is an empty placeholder and showed a blank page.
        FeatureFlag.PRESCRIPTION -> screen { navigateToPrescriptions() }
        FeatureFlag.DESERVED_TREATMENT_101 -> screen { navigateToDeservedTreatment() }
        FeatureFlag.PAY_ROLL -> screen { navigateToPayRoll() }
        FeatureFlag.EDICT_PENSIONER -> screen { navigateToEdict() }
        FeatureFlag.ISSUANCE_WAGE_CERTIFICATE -> screen { navigateToIssuanceCertificate() }
        FeatureFlag.DEFERRED_INSTALLMENT_CERTIFICATE -> screen { navigateToDeferredInstallment() }
        FeatureFlag.GIRL_SURVIVOR -> screen { navigateToGirlSurvivor() }
        FeatureFlag.REQUEST_PENSION_BY_SURVIVOR,
        FeatureFlag.REQUEST_PENSION_BY_SURVIVOR_112 -> screen { navigateToPensionSurvivor() }
        FeatureFlag.DISABILITY_PENSION -> screen { navigateToDisabilityPension() }
        FeatureFlag.VIEW_TITLE_JOB -> screen { navigateToHistoryJobInfo() }
        FeatureFlag.SEND_INSURANCE_HISTORY_TO_INSTITUTION -> screen { navigateToSendInsuranceHistoryToInstitutions() }
        FeatureFlag.OROTEZ_PROTEZ -> screen { navigateToOrotezProtez() }
        FeatureFlag.REQUEST_PAYMENT_FOR_ILL_DAYS -> screen { navigateToRequestPaymentForIllDays() }
        FeatureFlag.OCCURRENCE -> screen { navigateToOccurrence() }
        FeatureFlag.WORKERS_PAYMENT_INFO -> screen { navigateToWorkersPaymentInfo() }
        FeatureFlag.LIST_OF_INSPECTIONS_PERFORMED -> screen { navigateToInspection() }
        FeatureFlag.REGISTER_AGREEMENT -> screen { navigateToEmployerOnlineServices() }
        FeatureFlag.OBJECTION_NON_EXISTENT_HISTORY -> screen { navigateToHistoryObjection() }
        FeatureFlag.INQUIRY_EDUCATION -> screen { navigateToInquiryEducation() }
        // «کسری از ماه» has only an empty placeholder screen until its phase 2 UI is built; opening
        // it showed a blank page, so it counts as not built yet.
        FeatureFlag.FRACTION_CONTRACT -> return false
        FeatureFlag.WEDDING_PRESENT -> screen { navigateToWeddingPresent() }
        FeatureFlag.CALCULATE_MARRIAGE_ALLOWANCE -> screen { navigateToWeddingPresentCalculate() }
        FeatureFlag.REQUEST_FOR_PREGNANCY_PAY -> screen { navigateToPregnancyPay() }
        else -> return false
    }
    beforeOpen()
    open()
    return true
}

/** Marks a `when` branch of [navigateToFeature] as the navigation to run, not to run now. */
private inline fun screen(noinline navigate: () -> Unit): () -> Unit = navigate
