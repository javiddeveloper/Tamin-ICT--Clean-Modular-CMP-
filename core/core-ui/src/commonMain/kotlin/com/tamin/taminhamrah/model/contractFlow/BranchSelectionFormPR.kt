package com.tamin.taminhamrah.model.contractFlow

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
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
