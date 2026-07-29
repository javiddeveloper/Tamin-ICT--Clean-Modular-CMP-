package com.tamin.taminhamrah.data.remote.models.services.retirementPension

data class RetirementSaveDocumentRequest(
    var pensionRequestDocList: List<RetirementDocumentModel?>? = null,
    val status : String
)
