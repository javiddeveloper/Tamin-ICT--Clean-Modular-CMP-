package com.tamin.taminhamrah.model.objectionInsurance

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ObjectionInsuranceHistoryPR(
    val branchCode: String = "",
    val branchName: String = "",
    val confirmed: Boolean = false,
    val deleted: Boolean = false,
    val historyTypeCode: String = "",
    val historyTypeName: String = "",
    val isDeleted: Boolean = false,
    val newMonth1: String = "",
    val newMonth2: String = "",
    val newMonth3: String = "",
    val newMonth4: String = "",
    val newMonth5: String = "",
    val newMonth6: String = "",
    val newMonth7: String = "",
    val newMonth8: String = "",
    val newMonth9: String = "",
    val newMonth10: String = "",
    val newMonth11: String = "",
    val newMonth12: String = "",
    val oldMonth1: String = "",
    val oldMonth2: String = "",
    val oldMonth3: String = "",
    val oldMonth4: String = "",
    val oldMonth5: String = "",
    val oldMonth6: String = "",
    val oldMonth7: String = "",
    val oldMonth8: String = "",
    val oldMonth9: String = "",
    val oldMonth10: String = "",
    val oldMonth11: String = "",
    val oldMonth12: String = "",
    val prow: String = "",
    val requestNumber: String = "",
    val requestType: String = "",
    val insuredId: String = "",
    val workshopId: String = "",
    val userDesc: String = "",
    val workshopName: String = "",
    val year: String = "",
)
