package com.tamin.taminhamrah.data.remote.models.ai

import com.google.gson.annotations.SerializedName

// --- 1. General AI Response DTO ---
data class AiBotResponseDto(
    @SerializedName("session_id") val sessionId: String?,
    @SerializedName("entities") val entities: List<AiStepDto>?
)

data class AiStepDto(
    @SerializedName("key") val key: String?,
    @SerializedName("success_message") val successMessage: String?,
    @SerializedName("payload") val payload: AiPayloadDto?
)

data class AiPayloadDto(
    @SerializedName("filter") val filter: List<String>?,
    @SerializedName("limit") val limit: Int?,
    @SerializedName("page") val page: Int?
)

// --- 2. Job Titles DTOs ---
data class TitlesJobResponseDto(
    @SerializedName("data") val data: TitlesJobDataDto? = null
)
data class TitlesJobDataDto(
    @SerializedName("total") val total: Int? = null,
    @SerializedName("list") val list: List<TitlesJobDto>? = null
)
data class TitlesJobDto(
    val id: String? = null,
    val rwshId: String? = null,
    val rwshName: String? = null,
    val startDate: String? = null,
    val branchCode: String? = null,
    val jobDesc: String? = null,
    val risuid: String? = null
)

// --- 3. Wage/History DTOs ---
data class WageHistoryResponseDto(
    @SerializedName("list") val list: List<YearlyWageRecordDto>? = null
)

data class YearlyWageRecordDto(
    val year: String?,
    // A list of 12 months for this year.
    // Each entry contains (Month Index 1-12, Days, Wage)
    val monthlyRecords: List<MonthlyWageDto>?
)

data class MonthlyWageDto(
    val monthIndex: Int,
    val days: String?,
    val wage: String?
)

// --- 4. Dependents (Booklet) DTOs ---
data class DependentsResponseDto(
    val list: List<DependentDto>? = null
)
data class DependentDto(
    val fullName: String?,
    val relation: String?
)

// --- 5. Edict (Hokm) DTOs ---
data class EdictResponseDto(
    val edictInfo: EdictInfoDto? = null,
    val totalPayableMonthly: String? = null // sumPay2 or total from list
)

data class EdictInfoDto(
    val pensionStartDate: String? = null,
    val originalHistory: String? = null, // Combined Year/Month/Day
    val additionalHistory: String? = null,
    val pensionAmount: String? = null // SumPay or similar
)
