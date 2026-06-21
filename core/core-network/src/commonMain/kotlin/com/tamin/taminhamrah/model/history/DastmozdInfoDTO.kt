package com.tamin.taminhamrah.model.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DastmozdInfoDTO(
    @SerialName("list") val list: List<DastmozdInfoItemDTO>? = null,
    @SerialName("total") val total: Int? = null
)

@Serializable
data class DastmozdInfoItemDTO(
    @SerialName("hismon1") val hismon1: String? = null,
    @SerialName("hismon2") val hismon2: String? = null,
    @SerialName("hismon3") val hismon3: String? = null,
    @SerialName("hismon4") val hismon4: String? = null,
    @SerialName("hismon5") val hismon5: String? = null,
    @SerialName("hismon6") val hismon6: String? = null,
    @SerialName("hismon7") val hismon7: String? = null,
    @SerialName("hismon8") val hismon8: String? = null,
    @SerialName("hismon9") val hismon9: String? = null,
    @SerialName("hismon10") val hismon10: String? = null,
    @SerialName("hismon11") val hismon11: String? = null,
    @SerialName("hismon12") val hismon12: String? = null,
    @SerialName("hiswage1") val hiswage1: String? = null,
    @SerialName("hiswage2") val hiswage2: String? = null,
    @SerialName("hiswage3") val hiswage3: String? = null,
    @SerialName("hiswage4") val hiswage4: String? = null,
    @SerialName("hiswage5") val hiswage5: String? = null,
    @SerialName("hiswage6") val hiswage6: String? = null,
    @SerialName("hiswage7") val hiswage7: String? = null,
    @SerialName("hiswage8") val hiswage8: String? = null,
    @SerialName("hiswage9") val hiswage9: String? = null,
    @SerialName("hiswage10") val hiswage10: String? = null,
    @SerialName("hiswage11") val hiswage11: String? = null,
    @SerialName("hiswage12") val hiswage12: String? = null,
    @SerialName("hisyear") val hisyear: String? = null,
    @SerialName("id") val id: Int? = null,
    @SerialName("risufname") val risufname: String? = null,
    @SerialName("risubirthdate") val risubirthdate: String? = null,
    @SerialName("risuidserial2") val risuidserial2: String? = null,
    @SerialName("risuidserial1") val risuidserial1: String? = null,
    @SerialName("rwshname") val rwshname: String? = null,
    @SerialName("expcitycode") val expcitycode: String? = null,
    @SerialName("brhcode") val brhcode: String? = null,
    @SerialName("risuidno") val risuidno: String? = null,
    @SerialName("risudname") val risudname: String? = null,
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("risulname") val risulname: String? = null,
    @SerialName("risunatcode") val risunatcode: String? = null,
    @SerialName("brhname") val brhname: String? = null,
    @SerialName("historytypedesc") val historytypedesc: String? = null,
    @SerialName("rwshid") val rwshid: String? = null
)
