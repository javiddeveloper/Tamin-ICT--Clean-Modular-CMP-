package com.tamin.taminhamrah.data.remote.models.services.inquirePensionStatus

import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.extentions.formattedDate

class InquirePensionStatusResponse : ListDataModel<InquirePensionStatusModel>()
data class InquirePensionStatusModel(
    val branchName: String? = null,
    val brchCode: String? = null,
    val fullName: String? = null,
    val insuranceNumber: String? = null,
    val nationalId: String? = null,
    val paymentAmount: Int? = null,
    val paymentDate: String? = null,
    val pensionEndDate: String? = null,
    val pensionerBaseDate: String? = null,
    val pensionerId: String? = null,
    val pensionerRisuid: String? = null,
    val pensionerType: String? = null,
    val sexDesc: String? = null,
    val statusDesc: String? = null
) {
    val formattedPaymentDate: String
        get() = paymentDate.formattedDate()

    val formattedPensionEndDate: String
        get() = pensionEndDate.formattedDate()

    val formattedPensionerBaseDate: String
        get() = pensionerBaseDate.formattedDate()
}
