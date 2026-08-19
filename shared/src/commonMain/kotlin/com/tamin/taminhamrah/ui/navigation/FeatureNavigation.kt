package com.tamin.taminhamrah.ui.navigation

import androidx.navigation.NavController
import com.tamin.taminhamrah.feature.contracts.navigateToContracts
import com.tamin.taminhamrah.feature.history.navigateToHistory
import com.tamin.taminhamrah.feature.history.navigateToHistoryJobInfo
import com.tamin.taminhamrah.feature.orotezprotez.navigateToOrotezProtez
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToCalculatePension
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToDeferredInstallment
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToDeservedTreatment
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToDisabilityPension
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToEdict
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToGirlSurvivor
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToIssuanceCertificate
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPayRoll
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPensionInquiry
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPensionSurvivor
import com.tamin.taminhamrah.feature.pensionInquiry.navigateToPrescription
import com.tamin.taminhamrah.feature.studentInsuranceContract.navigateToFreelanceInsuranceContract
import com.tamin.taminhamrah.feature.studentInsuranceContract.navigateToHousewifeInsuranceContract
import com.tamin.taminhamrah.feature.studentInsuranceContract.navigateToOptionalInsuranceContract
import com.tamin.taminhamrah.feature.studentInsuranceContract.navigateToStudentInsuranceContract
import com.tamin.taminhamrah.feature.taminServices.navigateToSendInsuranceHistoryToInstitutions
import com.tamin.taminhamrah.feature.workshops.navigateToWorkshops
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
        FeatureFlag.CONTRACTS -> navigateToContracts()
        FeatureFlag.STUDENT_INSURANCE -> navigateToStudentInsuranceContract()
        FeatureFlag.FREELANCE_INSURANCE -> navigateToFreelanceInsuranceContract()
        FeatureFlag.OPTIONAL_INSURANCE -> navigateToOptionalInsuranceContract()
        FeatureFlag.HOUSEWIFE_INSURANCE -> navigateToHousewifeInsuranceContract()
        FeatureFlag.PENSION_INQUIRY -> navigateToPensionInquiry()
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
        else -> Unit
    }
}
