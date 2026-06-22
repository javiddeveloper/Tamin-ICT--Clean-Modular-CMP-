package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class ContractEligibilityPR(
    val statusCode: Int,
    val isEligible: Boolean,
    val reasonText: String,
) {
    fun message(insuranceTypeLabel: String = STUDENT_INSURANCE_LABEL): String =
        if (isEligible) {
            "متقاضی محترم، شما به علت: $reasonText دارای شرایط لازم برای عقد قرارداد $insuranceTypeLabel هستید."
        } else {
            "متقاضی محترم، شما به علت: $reasonText فاقد شرایط لازم برای عقد قرارداد $insuranceTypeLabel هستید."
        }

    companion object {
        const val STUDENT_INSURANCE_LABEL = "بیمه دانشجویی"

        private val eligibilityReasons = listOf(
            "داشتن حداقل ۱۰ سال سابقه پرداخت حق بیمه نزد سازمان تأمین اجتماعی",
            "سن کمتر از ۵۰ سال",
            "داشتن ۰ روز سابقه و سن در زمان تقاضا",
            "داشتن حداکثر دو قرارداد حرف و مشاغل",
            "عدم احراز شرایط سن و سابقه",
        )

        fun unavailable(): ContractEligibilityPR = ContractEligibilityPR(
            statusCode = -1,
            isEligible = false,
            reasonText = eligibilityReasons[4],
        )

        fun from(
            statusCode: Int,
            history: String?,
            age: String?,
        ): ContractEligibilityPR {
            val isEligible = statusCode in 1..4
            val reasonText = when {
                !isEligible -> eligibilityReasons[4]
                statusCode == 3 -> buildStatusThreeReason(history, age)
                else -> eligibilityReasons[statusCode - 1]
            }
            return ContractEligibilityPR(
                statusCode = statusCode,
                isEligible = isEligible,
                reasonText = reasonText,
            )
        }

        private fun buildStatusThreeReason(history: String?, age: String?): String {
            if (age.isNullOrBlank() || age.length < 6) {
                return eligibilityReasons[2]
            }
            val year = age.substring(0, 2)
            val month = age.substring(2, 4)
            val day = age.substring(4, 6)
            return "داشتن ${history.orEmpty()} روز سابقه و سن $year/$month/$day در زمان تقاضا"
        }
    }
}
