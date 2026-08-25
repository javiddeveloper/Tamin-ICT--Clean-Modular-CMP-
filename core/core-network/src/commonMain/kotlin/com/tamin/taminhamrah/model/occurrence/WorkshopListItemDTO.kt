package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

/**
 * `occurence/all-workshop` doesn't return keyed JSON objects like every other occurrence
 * endpoint — each row is a bare positional array, e.g. `["1412345", "کارگاه تولیدی الف", "014"]`.
 * [WorkshopListItemDTOSerializer] maps that array shape onto named fields at deserialization time
 * so the rest of the app never has to deal with raw [kotlinx.serialization.json.JsonElement]s.
 */
@Serializable(with = WorkshopListItemDTOSerializer::class)
data class WorkshopListItemDTO(
    val workshopCode: String?,
    val name: String?,
    val branchCode: String?,
)

object WorkshopListItemDTOSerializer : KSerializer<WorkshopListItemDTO> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("WorkshopListItemDTO") {
        element<String?>("workshopCode")
        element<String?>("name")
        element<String?>("branchCode")
    }

    override fun deserialize(decoder: Decoder): WorkshopListItemDTO {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("WorkshopListItemDTO can only be deserialized from JSON")
        val row = jsonDecoder.decodeJsonElement().jsonArray
        return WorkshopListItemDTO(
            workshopCode = row.contentOrNullAt(0),
            name = row.contentOrNullAt(1),
            branchCode = row.contentOrNullAt(2),
        )
    }

    override fun serialize(encoder: Encoder, value: WorkshopListItemDTO) {
        error("WorkshopListItemDTO is response-only and does not support serialization")
    }

    private fun JsonArray.contentOrNullAt(index: Int): String? = try {
        getOrNull(index)?.jsonPrimitive?.contentOrNull
    } catch (e: Exception) {
        null
    }
}
