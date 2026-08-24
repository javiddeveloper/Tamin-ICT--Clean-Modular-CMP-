package com.tamin.taminhamrah.feature.pensionSurvivor

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.PensionSurvivorScreen
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.SurvivorInfoScreen
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object PensionSurvivorRoute

@Serializable
data class SurvivorInfoRoute(
    val firstName: String,
    val lastName: String,
    val nationalId: String,
    val fatherName: String,
    val idCardNumber: String,
    val cityOfIssue: String,
    val genderCode: String,
    val genderDesc: String,
    val dateOfBirth: String,
    val insuranceId: String,
    val tendencyCode: String,
    val deceasedNationalId: String,
)

fun NavController.navigateToPensionSurvivor(navOptions: NavOptions? = null) {
    navigate(PensionSurvivorRoute, navOptions)
}

fun NavGraphBuilder.pensionSurvivorScreen(
    navController: NavController,
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<PensionSurvivorRoute> {
        PensionSurvivorScreen(
            onBack = onBack,
            onNavigateToSurvivorInfo = { survivor, deceasedNationalId ->
                navController.navigate(
                    SurvivorInfoRoute(
                        firstName = survivor.firstName,
                        lastName = survivor.lastName,
                        nationalId = survivor.nationalId,
                        fatherName = survivor.fatherName,
                        idCardNumber = survivor.idCardNumber,
                        cityOfIssue = survivor.cityOfIssue,
                        genderCode = survivor.genderCode,
                        genderDesc = survivor.genderDesc,
                        dateOfBirth = survivor.dateOfBirth,
                        insuranceId = survivor.insuranceId,
                        tendencyCode = survivor.tendencyCode,
                        deceasedNationalId = deceasedNationalId,
                    ),
                )
            },
        )
    }

    composableWithFadeTransitions<SurvivorInfoRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<SurvivorInfoRoute>()
        SurvivorInfoScreen(
            survivor = route.toSurvivor(),
            deceasedNationalId = route.deceasedNationalId,
            onBack = { navController.popBackStack() },
        )
    }
}

private fun SurvivorInfoRoute.toSurvivor(): SurvivorDependentPR {
    return SurvivorDependentPR(
        firstName = firstName,
        lastName = lastName,
        nationalId = nationalId,
        fatherName = fatherName,
        idCardNumber = idCardNumber,
        cityOfIssue = cityOfIssue,
        genderCode = genderCode,
        genderDesc = genderDesc,
        dateOfBirth = dateOfBirth,
        insuranceId = insuranceId,
        tendencyCode = tendencyCode,
    )
}
