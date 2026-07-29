package com.tamin.taminhamrah.data.remote.models.services.disabilityPension

data class DisabilitySaveDocumentRequest(
    var pensionRequestDocList: List<PensionRequestDoc?>? = null,
    val status : String
)
