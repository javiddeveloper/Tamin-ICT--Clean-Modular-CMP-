package com.tamin.taminhamrah.feature.pensionInquiry

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.pensionInquiry.ui.PensionInquiryScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.calculatePension.CalculatePensionScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.prescription.PrescriptionScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.deservedTreatment.DeservedTreatmentScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.PayRollScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.EdictScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.IssuanceCertificateScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.deferredInstallment.DeferredInstallmentScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.girlSurvivor.GirlSurvivorScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.pensionSurvivor.PensionSurvivorScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.DisabilityPensionScreen
import kotlinx.serialization.Serializable

@Serializable
data object PensionInquiryRoute

@Serializable
data object CalculatePensionRoute

@Serializable
data object PrescriptionRoute

@Serializable
data object DeservedTreatmentRoute

@Serializable
data object PayRollRoute

@Serializable
data object EdictRoute

@Serializable
data object IssuanceCertificateRoute

@Serializable
data object DeferredInstallmentRoute

@Serializable
data object GirlSurvivorRoute

@Serializable
data object PensionSurvivorRoute

@Serializable
data object DisabilityPensionRoute

fun NavController.navigateToPensionInquiry(navOptions: NavOptions? = null) {
    navigate(PensionInquiryRoute, navOptions)
}

fun NavController.navigateToPensionInquiry(builder: NavOptionsBuilder.() -> Unit) {
    navigate(PensionInquiryRoute, builder)
}

fun NavController.navigateToCalculatePension(navOptions: NavOptions? = null) {
    navigate(CalculatePensionRoute, navOptions)
}

fun NavController.navigateToPrescription(navOptions: NavOptions? = null) {
    navigate(PrescriptionRoute, navOptions)
}

fun NavController.navigateToDeservedTreatment(navOptions: NavOptions? = null) {
    navigate(DeservedTreatmentRoute, navOptions)
}

fun NavController.navigateToPayRoll(navOptions: NavOptions? = null) {
    navigate(PayRollRoute, navOptions)
}

fun NavController.navigateToEdict(navOptions: NavOptions? = null) {
    navigate(EdictRoute, navOptions)
}

fun NavController.navigateToIssuanceCertificate(navOptions: NavOptions? = null) {
    navigate(IssuanceCertificateRoute, navOptions)
}

fun NavController.navigateToDeferredInstallment(navOptions: NavOptions? = null) {
    navigate(DeferredInstallmentRoute, navOptions)
}

fun NavController.navigateToGirlSurvivor(navOptions: NavOptions? = null) {
    navigate(GirlSurvivorRoute, navOptions)
}

fun NavController.navigateToPensionSurvivor(navOptions: NavOptions? = null) {
    navigate(PensionSurvivorRoute, navOptions)
}

fun NavController.navigateToDisabilityPension(navOptions: NavOptions? = null) {
    navigate(DisabilityPensionRoute, navOptions)
}

fun NavGraphBuilder.pensionInquiryScreen() {
    composable<PensionInquiryRoute> {
        PensionInquiryScreen()
    }
}

fun NavGraphBuilder.calculatePensionScreen(onBack: () -> Unit) {
    composable<CalculatePensionRoute> {
        CalculatePensionScreen(onBack = onBack)
    }
}

fun NavGraphBuilder.prescriptionScreen(onBack: () -> Unit) {
    composable<PrescriptionRoute> {
        PrescriptionScreen(onBack = onBack)
    }
}

fun NavGraphBuilder.deservedTreatmentScreen(onBack: () -> Unit) {
    composable<DeservedTreatmentRoute> {
        DeservedTreatmentScreen(onBack = onBack)
    }
}

fun NavGraphBuilder.payrollScreen(onBack: () -> Unit) {
    composable<PayRollRoute> {
        PayRollScreen(onBack = onBack)
    }
}

fun NavGraphBuilder.edictScreen(onBack: () -> Unit) {
    composable<EdictRoute> {
        EdictScreen(onBack = onBack)
    }
}

fun NavGraphBuilder.issuanceCertificateScreen(onBack: () -> Unit) {
    composable<IssuanceCertificateRoute> {
        IssuanceCertificateScreen(onBack = onBack)
    }
}

fun NavGraphBuilder.deferredInstallmentScreen(onBack: () -> Unit) {
    composable<DeferredInstallmentRoute> {
        DeferredInstallmentScreen(onBack = onBack)
    }
}

fun NavGraphBuilder.girlSurvivorScreen(onBack: () -> Unit) {
    composable<GirlSurvivorRoute> {
        GirlSurvivorScreen(onBack = onBack)
    }
}

fun NavGraphBuilder.pensionSurvivorScreen(onBack: () -> Unit) {
    composable<PensionSurvivorRoute> {
        PensionSurvivorScreen(onBack = onBack)
    }
}

fun NavGraphBuilder.disabilityPensionScreen(onBack: () -> Unit) {
    composable<DisabilityPensionRoute> {
        DisabilityPensionScreen(onBack = onBack)
    }
}

