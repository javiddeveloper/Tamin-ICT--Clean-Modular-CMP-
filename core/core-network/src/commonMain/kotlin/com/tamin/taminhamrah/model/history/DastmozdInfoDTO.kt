package com.tamin.taminhamrah.model.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DastmozdInfoDTO(
    @SerialName("list") val list: List<DastmozdInfoItemDTO>?,
    @SerialName("total") val total: Int?
)

@Serializable
data class DastmozdInfoItemDTO(
    @SerialName("hismon1") val hismon1: String?,
    @SerialName("hismon2") val hismon2: String?,
    @SerialName("hismon3") val hismon3: String?,
    @SerialName("hismon4") val hismon4: String?,
    @SerialName("hismon5") val hismon5: String?,
    @SerialName("hismon6") val hismon6: String?,
    @SerialName("hismon7") val hismon7: String?,
    @SerialName("hismon8") val hismon8: String?,
    @SerialName("hismon9") val hismon9: String?,
    @SerialName("hismon10") val hismon10: String?,
    @SerialName("hismon11") val hismon11: String?,
    @SerialName("hismon12") val hismon12: String?,
    @SerialName("hiswage1") val hiswage1: String?,
    @SerialName("hiswage2") val hiswage2: String?,
    @SerialName("hiswage3") val hiswage3: String?,
    @SerialName("hiswage4") val hiswage4: String?,
    @SerialName("hiswage5") val hiswage5: String?,
    @SerialName("hiswage6") val hiswage6: String?,
    @SerialName("hiswage7") val hiswage7: String?,
    @SerialName("hiswage8") val hiswage8: String?,
    @SerialName("hiswage9") val hiswage9: String?,
    @SerialName("hiswage10") val hiswage10: String?,
    @SerialName("hiswage11") val hiswage11: String?,
    @SerialName("hiswage12") val hiswage12: String?,
    @SerialName("hisyear") val hisyear: String?,
    @SerialName("id") val id: Int?,
    @SerialName("risufname") val risufname: String?,
    @SerialName("risubirthdate") val risubirthdate: String?,
    @SerialName("risuidserial2") val risuidserial2: String?,
    @SerialName("risuidserial1") val risuidserial1: String?,
    @SerialName("rwshname") val rwshname: String?,
    @SerialName("expcitycode") val expcitycode: String?,
    @SerialName("brhcode") val brhcode: String?,
    @SerialName("risuidno") val risuidno: String?,
    @SerialName("risudname") val risudname: String?,
    @SerialName("risuid") val risuid: String?,
    @SerialName("risulname") val risulname: String?,
    @SerialName("risunatcode") val risunatcode: String?,
    @SerialName("brhname") val brhname: String?,
    @SerialName("historytypedesc") val historytypedesc: String?,
    @SerialName("rwshid") val rwshid: String?
)
