package com.tamin.taminhamrah.feature.studentInsuranceContract

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.StudentInsuranceContractScreen
import kotlinx.serialization.Serializable

@Serializable
data object StudentInsuranceContractRoute

fun NavController.navigateToStudentInsuranceContract() {
    navigate(StudentInsuranceContractRoute)
}

fun NavGraphBuilder.studentInsuranceContractScreen(onBack: () -> Unit) {
    composable<StudentInsuranceContractRoute> {
        StudentInsuranceContractScreen(onBack = onBack)
    }
}
