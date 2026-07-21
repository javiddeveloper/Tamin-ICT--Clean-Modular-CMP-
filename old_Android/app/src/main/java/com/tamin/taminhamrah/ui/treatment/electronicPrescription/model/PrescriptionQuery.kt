package com.tamin.taminhamrah.ui.treatment.electronicPrescription.model

data class PrescriptionQuery(
    val type: String = "1",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val nationalCode: String = "0",
    val dependantCode: String = "0",
    val trigger: Int = 0
)
