package com.tamin.taminhamrah.model.contractFlow

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.contractFlow.ContractEligibilityReason
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ContractEligibilityPR(
    val statusCode: Int,
    val isEligible: Boolean,
    val reason: ContractEligibilityReason,
    val historyDays: String? = null,
    val ageFormatted: String? = null,
) {
    companion object {
        fun unavailable(): ContractEligibilityPR = ContractEligibilityPR(
            statusCode = -1,
            isEligible = false,
            reason = ContractEligibilityReason.AGE_HISTORY_NOT_MET,
        )

        fun from(
            statusCode: Int,
            history: String?,
            age: String?,
        ): ContractEligibilityPR {
            val isEligible = statusCode in 1..4
            val reason = when {
                !isEligible -> ContractEligibilityReason.AGE_HISTORY_NOT_MET
                statusCode == 3 -> resolveHistoryAndAgeReason(history, age)
                statusCode == 1 -> ContractEligibilityReason.MIN_TEN_YEARS_HISTORY
                statusCode == 2 -> ContractEligibilityReason.AGE_UNDER_FIFTY
                statusCode == 4 -> ContractEligibilityReason.MAX_TWO_FREELANCE_CONTRACTS
                else -> ContractEligibilityReason.AGE_HISTORY_NOT_MET
            }
            val (historyDays, ageFormatted) = if (reason == ContractEligibilityReason.HISTORY_AND_AGE_DYNAMIC) {
                buildHistoryAndAgeArgs(history, age)
            } else {
                null to null
            }
            return ContractEligibilityPR(
                statusCode = statusCode,
                isEligible = isEligible,
                reason = reason,
                historyDays = historyDays,
                ageFormatted = ageFormatted,
            )
        }

        private fun resolveHistoryAndAgeReason(history: String?, age: String?): ContractEligibilityReason {
            if (age.isNullOrBlank() || age.length < 6) {
                return ContractEligibilityReason.ZERO_HISTORY_PLACEHOLDER
            }
            return ContractEligibilityReason.HISTORY_AND_AGE_DYNAMIC
        }

        private fun buildHistoryAndAgeArgs(history: String?, age: String?): Pair<String, String> {
            if (age.isNullOrBlank() || age.length < 6) {
                return "" to ""
            }
            val year = age.substring(0, 2)
            val month = age.substring(2, 4)
            val day = age.substring(4, 6)
            return (history.orEmpty()) to "$year/$month/$day"
        }
    }
}
