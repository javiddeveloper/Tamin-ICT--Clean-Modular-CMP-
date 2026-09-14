package com.tamin.taminhamrah.feature.pensionInquiry

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.pensionInquiry.ui.prescription.PrescriptionScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.deservedTreatment.DeservedTreatmentScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.PayRollScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.EdictScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.IssuanceCertificateScreen
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.DisabilityPensionScreen
import kotlinx.serialization.Serializable

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
data object DisabilityPensionRoute

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

fun NavController.navigateToDisabilityPension(navOptions: NavOptions? = null) {
    navigate(DisabilityPensionRoute, navOptions)
}

fun NavGraphBuilder.prescriptionScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<PrescriptionRoute> {
        PrescriptionScreen(onBack = onBack)
    }
}

fun NavGraphBuilder.deservedTreatmentScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<DeservedTreatmentRoute> {
        DeservedTreatmentScreen(onBack = onBack)
    }
}

fun NavGraphBuilder.payrollScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<PayRollRoute> {
        PayRollScreen(onBack = onBack)
    }
}

fun NavGraphBuilder.edictScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<EdictRoute> {
        EdictScreen(onBack = onBack)
    }
}

fun NavGraphBuilder.issuanceCertificateScreen(onBack: () -> Unit, onGoHome: () -> Unit) {
    composableWithFadeTransitions<IssuanceCertificateRoute> {
        IssuanceCertificateScreen(onBack = onBack, onGoHome = onGoHome)
    }
}

fun NavGraphBuilder.disabilityPensionScreen(
    onBack: () -> Unit,
    onNavigateToAddDependent: () -> Unit = {},
) {
    composableWithFadeTransitions<DisabilityPensionRoute> {
        DisabilityPensionScreen(onBack = onBack, onNavigateToAddDependent = onNavigateToAddDependent)
    }
}
