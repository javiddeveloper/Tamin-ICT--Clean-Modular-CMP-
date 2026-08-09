package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuredDocDTO(
    @SerialName("documentType") var documentType: String?,
    @SerialName("id") var id: Long?,
    @SerialName("documentFile") var documentFile: DocumentFileDTO?
)

@Serializable
data class DocumentFileDTO(
    @SerialName("createdBy") var createdBy: String?,
    @SerialName("id") var id: String?,
    @SerialName("image") var image: String?
)
