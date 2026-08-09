package com.tamin.taminhamrah.ui.navigation

import androidx.navigation.NavController
import com.tamin.taminhamrah.feature.contracts.navigateToContracts
import com.tamin.taminhamrah.feature.history.navigateToHistory
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
import com.tamin.taminhamrah.feature.workshops.navigateToWorkshops
import com.tamin.taminhamrah.model.common.FeatureFlag

fun NavController.navigateToFeature(flag: FeatureFlag) {
    when (flag) {
        FeatureFlag.MERGE_HISTORY -> navigateToHistory()
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
        else -> Unit
    }
}
