package com.tamin.taminhamrah.model.personal

data class InsuredDocDN(
    val documentType: String,
    val id: Long,
    val documentFile: DocumentFileDN
)

data class DocumentFileDN(
    val createdBy: String,
    val id: String,
    val image: String
)
