package com.tamin.taminhamrah.ui.navigation

import androidx.navigation.NavController
import com.tamin.taminhamrah.feature.contracts.navigateToContracts
import com.tamin.taminhamrah.feature.history.navigateToHistory
import com.tamin.taminhamrah.feature.history.navigateToHistoryJobInfo
import com.tamin.taminhamrah.feature.historyobjection.navigateToHistoryObjection
import com.tamin.taminhamrah.feature.deferredInstallment.navigateToDeferredInstallment
import com.tamin.taminhamrah.feature.orotezprotez.navigateToOrotezProtez
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
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPrescription
import com.tamin.taminhamrah.feature.studentInsuranceContract.navigateToFreelanceInsuranceContract
import com.tamin.taminhamrah.feature.studentInsuranceContract.navigateToHousewifeInsuranceContract
import com.tamin.taminhamrah.feature.studentInsuranceContract.navigateToOptionalInsuranceContract
import com.tamin.taminhamrah.feature.studentInsuranceContract.navigateToStudentInsuranceContract
import com.tamin.taminhamrah.feature.taminServices.navigateToOccurrence
import com.tamin.taminhamrah.feature.taminServices.navigateToInspection
import com.tamin.taminhamrah.feature.taminServices.navigateToSendInsuranceHistoryToInstitutions
import com.tamin.taminhamrah.feature.workshops.navigateToWorkshops
import com.tamin.taminhamrah.feature.workshops.navigateToLegalRepresentativeWorkshops
import com.tamin.taminhamrah.model.common.FeatureFlag

fun NavController.navigateToFeature(flag: FeatureFlag) {
    when (flag) {
        FeatureFlag.MERGE_HISTORY -> navigateToHistory()
        FeatureFlag.WORKSHOPS -> navigateToWorkshops()
        FeatureFlag.STACK_HOLDER_LIST -> navigateToLegalRepresentativeWorkshops()
        FeatureFlag.CONTRACTS -> navigateToContracts()
        FeatureFlag.STUDENT_INSURANCE -> navigateToStudentInsuranceContract()
        FeatureFlag.FREELANCE_INSURANCE -> navigateToFreelanceInsuranceContract()
        FeatureFlag.OPTIONAL_INSURANCE -> navigateToOptionalInsuranceContract()
        FeatureFlag.HOUSEWIFE_INSURANCE -> navigateToHousewifeInsuranceContract()
        FeatureFlag.PENSION_INQUIRY -> navigateToPensionStatusInquiry()
        FeatureFlag.CALCULATE_WAGE_PENSION -> navigateToCalculatePension()
        FeatureFlag.PRESCRIPTION -> navigateToPrescription()
        FeatureFlag.DESERVED_TREATMENT_101 -> navigateToDeservedTreatment()
        FeatureFlag.PAY_ROLL -> navigateToPayRoll()
        FeatureFlag.EDICT_PENSIONER -> navigateToEdict()
        FeatureFlag.ISSUANCE_WAGE_CERTIFICATE -> navigateToIssuanceCertificate()
        FeatureFlag.DEFERRED_INSTALLMENT_CERTIFICATE -> navigateToDeferredInstallment()
        FeatureFlag.GIRL_SURVIVOR -> navigateToGirlSurvivor()
        FeatureFlag.REQUEST_PENSION_BY_SURVIVOR_112 -> navigateToPensionSurvivor()
        FeatureFlag.DISABILITY_PENSION -> navigateToDisabilityPension()
        FeatureFlag.VIEW_TITLE_JOB -> navigateToHistoryJobInfo()
        FeatureFlag.SEND_INSURANCE_HISTORY_TO_INSTITUTION -> navigateToSendInsuranceHistoryToInstitutions()
        FeatureFlag.OROTEZ_PROTEZ -> navigateToOrotezProtez()
        FeatureFlag.OCCURRENCE -> navigateToOccurrence()
        FeatureFlag.LIST_OF_INSPECTIONS_PERFORMED -> navigateToInspection()
        FeatureFlag.OBJECTION_NON_EXISTENT_HISTORY -> navigateToHistoryObjection()
        FeatureFlag.INQUIRY_EDUCATION -> navigateToInquiryEducation()
        FeatureFlag.REQUEST_FOR_PREGNANCY_PAY -> navigateToPregnancyPay()
        else -> Unit
    }
}
