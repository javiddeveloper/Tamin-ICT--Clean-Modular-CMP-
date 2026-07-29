package com.tamin.taminhamrah.data.remote.models.services

data class ShortTermOrthosisReq(
    var shorttermRequest: ShorttermRequest? = null,
    var useNationalid: String? = null,
    var useRel: String? = null,
    var useRelationShip: String? = null,
    var useRfName: String? = null,
    var useRisuId: String? = null,
    var useRlName: String? = null,
    var useTajTimeStamp: Long? = null ,
)


data class RequestOrthosis(
    val id: String? = null
)

data class ShorttermRequest(
    var branchCode: String? = null,
    var branchName: String? = null,
    val insuranceFirstName: String? = null,
    val insuranceLastName: String? = null,
    val mobilNumber: String? = null,
    val nationalCode: String? = null,
    var request: RequestOrthosis? = null,
    var requestFileList: List<RequestFile>? = null,
    val requestHelpType: String? = null,
    var risuid: String? = null,
    var serviceDateTimeStamp: Long? = null
)

data class RequestFile(
    val documentFile: String? = null,
    val documentType: String? = null,
    val editDate: String? = null,
    val editUser: String? = null,
    val id: String? = null,
    val shorttermRequest: ShorttermRequest? = null
)



