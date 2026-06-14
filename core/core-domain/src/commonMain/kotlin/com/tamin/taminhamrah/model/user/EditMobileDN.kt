package com.tamin.taminhamrah.model.user

data class EditMobileResponseDN(
    val traceId: String? = null,
    val data: EditMobileDN? = null
)

data class EditMobileDN(
    val hash: String? = null,
    val expirationTime: ExpirationTimeDN? = null
)

data class ExpirationTimeDN(
    val year: Int? = null,
    val month: String? = null,
    val nano: Long? = null,
    val monthValue: Int? = null,
    val dayOfMonth: Int? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val second: Int? = null,
    val dayOfWeek: String? = null,
    val dayOfYear: Int? = null,
    val chronology: ChronologyDN? = null
)

data class ChronologyDN(
    val calendarType: String? = null,
    val id: String? = null
)
