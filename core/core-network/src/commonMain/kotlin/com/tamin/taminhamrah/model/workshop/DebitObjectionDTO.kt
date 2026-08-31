package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A document attached to an objection: the guid `upload-image` handed back, plus its type code. */
@Serializable
data class ObjectionPhotoDTO(
    @SerialName("guid") val guid: String? = null,
    @SerialName("type") val type: String? = null,
)

/**
 * Body of `POST debit-objection/objection-save`.
 *
 * The `typeN` columns are the flat mirror of [objectionPhotos]: slot *n* holds the document type of
 * the *n*-th uploaded file, blank when that slot is empty. The service reads both, so
 * `objectionTypeSlots` builds them from the same list rather than letting the two disagree.
 * Eighteen slots is the service's own ceiling (`type19` exists but the old client never filled it).
 *
 * `seporde` and `status` are `"1"` / `"0"` strings, not booleans.
 */
@Serializable
data class DebitObjectionSaveRequestDTO(
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("debitNumber") val debitNumber: String? = null,
    @SerialName("debitStepCode") val debitStepCode: String? = null,
    @SerialName("debitStatCode") val debitStatCode: String? = null,
    @SerialName("peymanSequence") val agreementRow: String? = null,
    @SerialName("badviNo") val primaryVoteNumber: String? = null,
    @SerialName("badviDate") val primaryVoteDate: String? = null,
    @SerialName("objectionDate") val objectionDate: String = "",
    @SerialName("objectionDesc") val objectionDescription: String? = null,
    @SerialName("objectionType") val objectionType: String? = null,
    @SerialName("objectionPhotos") val objectionPhotos: List<ObjectionPhotoDTO> = emptyList(),
    @SerialName("seporde") val deposit: String = "0",
    @SerialName("status") val status: String = "0",
    @SerialName("type1") val type1: String = "",
    @SerialName("type2") val type2: String = "",
    @SerialName("type3") val type3: String = "",
    @SerialName("type4") val type4: String = "",
    @SerialName("type5") val type5: String = "",
    @SerialName("type6") val type6: String = "",
    @SerialName("type7") val type7: String = "",
    @SerialName("type8") val type8: String = "",
    @SerialName("type9") val type9: String = "",
    @SerialName("type10") val type10: String = "",
    @SerialName("type11") val type11: String = "",
    @SerialName("type12") val type12: String = "",
    @SerialName("type13") val type13: String = "",
    @SerialName("type14") val type14: String = "",
    @SerialName("type15") val type15: String = "",
    @SerialName("type16") val type16: String = "",
    @SerialName("type17") val type17: String = "",
    @SerialName("type18") val type18: String = "",
) {
    companion object {
        /** How many `typeN` slots the request carries. */
        const val TYPE_SLOT_COUNT = 18
    }
}

/**
 * Fills the eighteen `typeN` slots from the attached documents, in order.
 *
 * Slots past the end of [photos] stay blank, and more than [TYPE_SLOT_COUNT] documents are
 * ignored rather than throwing — the old client indexed a fixed-size array here and would crash
 * on a shorter list.
 */
fun DebitObjectionSaveRequestDTO.withTypeSlots(
    photos: List<ObjectionPhotoDTO>,
): DebitObjectionSaveRequestDTO {
    fun slot(index: Int): String = photos.getOrNull(index)?.type.orEmpty()
    return copy(
        objectionPhotos = photos.take(DebitObjectionSaveRequestDTO.TYPE_SLOT_COUNT),
        type1 = slot(0), type2 = slot(1), type3 = slot(2), type4 = slot(3), type5 = slot(4),
        type6 = slot(5), type7 = slot(6), type8 = slot(7), type9 = slot(8), type10 = slot(9),
        type11 = slot(10), type12 = slot(11), type13 = slot(12), type14 = slot(13),
        type15 = slot(14), type16 = slot(15), type17 = slot(16), type18 = slot(17),
    )
}

/** Result of `objection-save`; [refId] is the tracking code the success message quotes. */
@Serializable
data class DebitObjectionSaveResultDTO(
    @SerialName("seqNo") val seqNo: Long? = null,
    @SerialName("refId") val refId: String? = null,
    @SerialName("createDate") val createDate: Long? = null,
    @SerialName("defectDesc") val defectDescription: String? = null,
)
