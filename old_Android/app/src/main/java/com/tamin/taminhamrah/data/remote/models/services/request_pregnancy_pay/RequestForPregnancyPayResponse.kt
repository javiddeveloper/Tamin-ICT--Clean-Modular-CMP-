package com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class RequestForPregnancyPayResponse (var data :RequestForPregnancyPayModel? = null) : BaseResponseNew()

    data class RequestForPregnancyPayModel(
        val barChild: String?,
        val barDRid: String?,
        val barDd: String?,
        val barDemDat: Long?,
        val barDemDatTimeStamp: Long?,
        val barDrname: String?,
        val barEDate: Long?,
        val barEDateTimeStamp: Long?,
        val barKind: Any?,
        val barSDate: Long?,
        val barSDateTimeStamp: Long?,
        val barTimes: Any?,
        val barType: String?,
        val barWkStatus: Any?,
        val childNationalId: String?,
        val shorttermRequest: ShorttermRequest?,
        val wrkPart: Any?
    ) {
        data class ShorttermRequest(
            val bankAccount: String?,
            val bankName: String?,
            val branchCode: String?,
            val branchName: String?,
            val branchWorkshop: Any?,
            val consequential: Any?,
            val flag: Boolean?,
            val genderCode: Any?,
            val insuranceFirstName: String?,
            val insuranceLastName: String?,
            val insuranceStatus: String?,
            val insuranceStatusDesc: String?,
            val insuranceType: String?,
            val insuranceTypeDesc: String?,
            val mobilNumber: String?,
            val nationalCode: String?,
            val partnerNationalId: Any?,
            val payDocNo: Any?,
            val payment: Any?,
            val request: Request?,
            val requestFileList: List<RequestFile?>?,
            val requestFileList1: Any?,
            val requestHelpType: String?,
            val requestHelpTypeDesc: String?,
            val requestedBrchName: Any?,
            val resultMessage: String?,
            val risuid: String?,
            val serviceDate: Long?,
            val serviceDateTimeStamp: Int?,
            val shorttemRequestId: Any?,
            val stringDocFiles: Any?,
            val weddingTimestamp: Int?,
            val workshopCode: String?,
            val workshopName: String?
        ) {
            data class Request(
                val brchCode: String?,
                val editDate: Long?,
                val editUser: String?,
                val id: Int?,
                val refrenceCode: String?,
                val requestDate: Long?,
                val requestType: RequestType?,
                val status: String?,
                val statusId: String?,
                val statusName: String?,
                val systemType: String?,
                val userId: String?
            ) {
                data class RequestType(
                    val form: String?,
                    val requestTypeCode: String?,
                    val requestTypeDesc: String?,
                    val systemId: String?
                )
            }

            data class RequestFile(
                val documentFile: String?,
                val documentType: String?,
                val editDate: Any?,
                val editUser: String?,
                val id: Int?
            )
        }
    }
