package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class BranchSelectionFormPR(
    val provinceCode: String = "",
    val provinceName: String = "",
    val cityCode: String = "",
    val cityName: String = "",
    val branchCode: String = "",
    val branchName: String = "",
) {
    val isValid: Boolean
        get() = provinceCode.isNotBlank() &&
            cityCode.isNotBlank() &&
            branchCode.isNotBlank()
}
