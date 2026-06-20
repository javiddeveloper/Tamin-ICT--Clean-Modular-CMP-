package com.tamin.taminhamrah.model.history

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class DastmozdInfoPR(
    val list: List<DastmozdInfoItemPR>,
    val total: Int
)

@Immutable
@Serializable
data class WageDetailPR(
    val month: String,
    val wage: String
)

@Immutable
@Serializable
data class DastmozdInfoItemPR(
    val wageDetails: List<WageDetailPR>,
    val hisyear: String,
    val id: Int,
    val risufname: String,
    val risubirthdate: String,
    val risuidserial2: String,
    val risuidserial1: String,
    val rwshname: String,
    val expcitycode: String,
    val brhcode: String,
    val risuidno: String,
    val risudname: String,
    val risuid: String,
    val risulname: String,
    val risunatcode: String,
    val brhname: String,
    val historytypedesc: String,
    val rwshid: String
)
