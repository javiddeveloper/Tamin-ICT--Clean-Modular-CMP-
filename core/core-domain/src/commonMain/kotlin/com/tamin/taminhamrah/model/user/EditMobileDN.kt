package com.tamin.taminhamrah.model.user

data class EditMobileResponseDN(
    val traceId: String? = null,
    val data: EditMobileDN? = null
)

data class EditMobileDN(
    val hash: String? = null,
    val expirationTime: Long? = null
)
