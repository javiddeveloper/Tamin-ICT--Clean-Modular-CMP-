package com.tamin.taminhamrah.model.changeMobile

data class EditMobileResponsePR(
    val traceId: String = "",
    val data: EditMobilePR? = null
)

data class EditMobilePR(
    val hash: String = "",
    val expirationTime: ExpirationTimePR? = null
)

data class ExpirationTimePR(
    val year: Int = 0,
    val month: String = "",
    val nano: Long = 0L,
    val monthValue: Int = 0,
    val dayOfMonth: Int = 0,
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0,
    val dayOfWeek: String = "",
    val dayOfYear: Int = 0,
    val chronology: ChronologyPR? = null
)

data class ChronologyPR(
    val calendarType: String = "",
    val id: String = ""
)
