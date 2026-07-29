package com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay

data class RequestForPregnancyPayReq(
    var barChild: String?=null,
    var barDRid: String?=null,
    var barDd: String?=null,
    var barDemDatTimeStamp: Long?=null,
    var barDrname: String?=null,
    var barEDateTimeStamp: Long?=null,
    var barSDateTimeStamp: Long?=null,
    var barType: String?=null,
    var childNationalId: String?=null,
    var childNationalId2: String? = null,
    var childNationalId3: String? = null,
    var shorttermRequest: ShorttermRequest,
    var wrkPart: String? = null

) {
    data class ShorttermRequest(
        var branchCode: String?=null,
        var branchName: String?=null,
        var insuranceFirstName: String?=null,
        var insuranceLastName: String?=null,
        var mobilNumber: String?=null,
        var nationalCode: String?=null,
        var request: Request= Request,
        var requestFileList: List<RequestFile?>?=null,
        var requestHelpType: String?=null,
        var risuid: String?=null,
        var serviceDateTimeStamp: Int?=null
    ) {
        object Request

        data class RequestFile(
            var documentFile: String?=null,
            var documentType: String?=null,
            var editDate: String?=null,
            var editUser: String?=null,
            var id: String?=null,
            var shorttermRequest: ShorttermRequest?=null

        ) {
            data class ShorttermRequest(
                var request: Request?=null
            ) {
                data class Request(
                    var id: String?=null
                )
            }
        }
    }
}