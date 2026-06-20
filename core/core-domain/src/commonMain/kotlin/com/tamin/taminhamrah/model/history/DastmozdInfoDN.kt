package com.tamin.taminhamrah.model.history

data class DastmozdInfoDN(
    val list: List<DastmozdInfoItemDN>?,
    val total: Int?
)

data class WageDetailDN(
    val month: String?,
    val wage: String?
)

data class DastmozdInfoItemDN(
    val wageDetails: List<WageDetailDN>,
    val hisyear: String?,
    val id: Int?,
    val risufname: String?,
    val risubirthdate: String?,
    val risuidserial2: String?,
    val risuidserial1: String?,
    val rwshname: String?,
    val expcitycode: String?,
    val brhcode: String?,
    val risuidno: String?,
    val risudname: String?,
    val risuid: String?,
    val risulname: String?,
    val risunatcode: String?,
    val brhname: String?,
    val historytypedesc: String?,
    val rwshid: String?
)
