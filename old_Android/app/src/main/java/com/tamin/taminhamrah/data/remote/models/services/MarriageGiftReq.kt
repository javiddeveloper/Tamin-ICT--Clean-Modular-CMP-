package com.tamin.taminhamrah.data.remote.models.services

data class MarriageGiftReq(
    var request: WeddingPresentRequest? = null,
    var requestFileList: ArrayList<Any>? = null,
    var risuid: String? = null,
    var insuranceFirstName: String? = null,
    var insuranceLastName: String? = null,
    var nationalCode: String? = null,
    var requestHelpType: String? = null,
    var mobilNumber: String? = null,
    var serviceDateTimeStamp: String? = null,
    var branchCode: String? = null,
    var branchName: String? = null
)


