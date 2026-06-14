package com.tamin.taminhamrah.model.erecords.images

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ElectronicFileResponse (
    @SerialName("categoryName") val categoryName: String? = null,
    @SerialName("contentServer") val contentServer: String? = null,
    @SerialName("countNumger") val countNumger: Int? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("rowNumber") val rowNumber: Int? = null,
    @SerialName("thumb") val thumb: String? = null,
    @SerialName("type") val type: String? = null,
    )
