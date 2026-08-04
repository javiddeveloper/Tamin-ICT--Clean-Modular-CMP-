package com.tamin.taminhamrah.feature.studentInsuranceContract

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.StudentInsuranceContractScreen
import com.tamin.taminhamrah.model.studentContract.InsuranceContractKind
import kotlinx.serialization.Serializable

@Serializable
data class StudentInsuranceContractRoute(
    val kind: String = InsuranceContractKind.STUDENT.name,
)

fun NavController.navigateToStudentInsuranceContract(
    kind: InsuranceContractKind = InsuranceContractKind.STUDENT,
) {
    navigate(StudentInsuranceContractRoute(kind = kind.name))
}

fun NavController.navigateToFreelanceInsuranceContract() {
    navigateToStudentInsuranceContract(InsuranceContractKind.FREELANCE)
}

fun NavController.navigateToOptionalInsuranceContract() {
    navigateToStudentInsuranceContract(InsuranceContractKind.OPTIONAL)
}

fun NavController.navigateToHousewifeInsuranceContract() {
    navigateToStudentInsuranceContract(InsuranceContractKind.HOUSEWIFE)
}

fun NavGraphBuilder.studentInsuranceContractScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<StudentInsuranceContractRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<StudentInsuranceContractRoute>()
        val contractKind = InsuranceContractKind.entries
            .firstOrNull { it.name == route.kind }
            ?: InsuranceContractKind.STUDENT
        StudentInsuranceContractScreen(
            contractKind = contractKind,
            onBack = onBack,
        )
    }
}
