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
data class DastmozdInfoItemPR(
    val hismon1: String,
    val hismon2: String,
    val hismon3: String,
    val hismon4: String,
    val hismon5: String,
    val hismon6: String,
    val hismon7: String,
    val hismon8: String,
    val hismon9: String,
    val hismon10: String,
    val hismon11: String,
    val hismon12: String,
    val hiswage1: String,
    val hiswage2: String,
    val hiswage3: String,
    val hiswage4: String,
    val hiswage5: String,
    val hiswage6: String,
    val hiswage7: String,
    val hiswage8: String,
    val hiswage9: String,
    val hiswage10: String,
    val hiswage11: String,
    val hiswage12: String,
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
