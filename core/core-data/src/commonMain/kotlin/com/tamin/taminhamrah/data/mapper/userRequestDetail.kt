package com.tamin.taminhamrah.core.data.mapper.userRequestDetail

import com.tamin.taminhamrah.core.data.mapper.utils.mapJalaliDate
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailDN
import com.tamin.taminhamrah.model.request.DownloadFileResponse
import com.tamin.taminhamrah.model.request.DownloadFileResponse.DownloadFileDetail
import com.tamin.taminhamrah.model.request.PregnancyStatusResponse.PregnancyStatusModel
import com.tamin.taminhamrah.model.request.PregnancyTypeResponse.PregnancyTypeModel
import com.tamin.taminhamrah.model.request.ShortTermRequestInfoResponse.ShortTermRequestInfoModel.ShortTermOrthoticsRequest
import com.tamin.taminhamrah.model.request.ShortTermStatusResponse
import com.tamin.taminhamrah.ui.model.userRequest.*

fun mapToDetail(
    statusResponse: ShortTermStatusResponse,
    infoResponse: com.tamin.taminhamrah.model.request.ShortTermRequestInfoResponse,
    pregnancyStatusList: List<PregnancyStatusModel>,
    pregnancyTypeList: List<PregnancyTypeModel>,
    article16Response: com.tamin.taminhamrah.model.request.Article16RequestInfoResponse?,
    deferredInstallmentResponse: com.tamin.taminhamrah.model.request.DeferredInstallmentInfoResponse?,
    followUpResponse: com.tamin.taminhamrah.model.request.ResultFollowUpObjectionNonExitsResponse,
    documentList: List<DownloadFileDetail>,
    isPregnancy: Boolean = false
): UserRequestDetailDN {
    val statusModel = statusResponse.ListDataResponse.list.firstOrNull()?.let { status ->
        UserRequestDetailDN.StatusDN(
            dateAcc = status.dateAcc,
            processResult = status.processResult,
            rejectReason = status.rejectReason
        )
    }

    val infoModel = infoResponse.ListDataResponse.list.firstOrNull()?.let { info ->
        val orthoticsList = info.shorttermArutz?.map { orth ->
            UserRequestDetailDN.OrthoticsDN(
                bankAccount = orth.shorttermRequest.bankAccount,
                bankName = orth.shorttermRequest.bankName,
                branchName = orth.shorttermRequest.branchName,
                resultMessage = orth.shorttermRequest.resultMessage,
                fileList = orth.shorttermRequest.requestFileList?.map { file ->
                    UserRequestDetailDN.RequestFileDN(
                        documentFile = file.documentFile,
                        documentType = file.documentType
                    )
                } ?: emptyList(),
                useTaj = orth.useTaj,
                bimSDate = orth.bimSDate,
                bimEdate = orth.bimEdate,
                bimDrname = orth.bimDrname,
                bimDrid = orth.bimDrid
            )
        } ?: emptyList()

        val illnessList = info.shorttermIllness?.map { ill ->
            UserRequestDetailDN.IllnessDN(
                bankAccount = ill.shorttermRequest.bankAccount,
                bankName = ill.shorttermRequest.bankName,
                branchName = ill.shorttermRequest.branchName,
                resultMessage = ill.shorttermRequest.resultMessage,
                fileList = ill.shorttermRequest.requestFileList?.map { file ->
                    UserRequestDetailDN.RequestFileDN(
                        documentFile = file.documentFile,
                        documentType = file.documentType
                    )
                } ?: emptyList(),
                bimSDate = ill.bimSDate,
                bimEdate = ill.bimEdate,
                bimDrname = ill.bimDrname,
                bimDrid = ill.bimDrid
            )
        } ?: emptyList()

        val pregnancyList = info.shorttermPragnent?.map { preg ->
            val orthoticsRequest = UserRequestDetailDN.OrthoticsDN(
                bankAccount = preg.shorttermRequest.bankAccount,
                bankName = preg.shorttermRequest.bankName,
                branchName = preg.shorttermRequest.branchName,
                resultMessage = preg.shorttermRequest.resultMessage,
                fileList = preg.shorttermRequest.requestFileList?.map { file ->
                    UserRequestDetailDN.RequestFileDN(
                        documentFile = file.documentFile,
                        documentType = file.documentType
                    )
                } ?: emptyList(),
                useTaj = preg.shorttermRequest.bankAccount, // empty for pregnancy
                bimSDate = preg.barDemDat,
                bimEdate = null,
                bimDrname = preg.drName,
                bimDrid = null
            )

            UserRequestDetailDN.PregnancyDN(
                barDrid = preg.barDrid,
                barDd = preg.barDd,
                barDemDat = preg.barDemDat,
                drName = preg.drName,
                startDate = preg.barSDate,
                endDate = preg.barEDate,
                barType = preg.barType,
                barChild = preg.barChild,
                orthoticsRequest = orthoticsRequest
            )
        } ?: emptyList()

        UserRequestDetailDN.InfoDN(
            mobile = info.mobile,
            risuFullName = info.risuFullName,
            risuid = info.risuid,
            orthoticsList = orthoticsList,
            consequentialList = info.consequential?.map { con ->
                UserRequestDetailDN.ConsequentialDN(
                    risuId = con.risuId,
                    bletEndDate = con.bletEndDate,
                    birthDate = con.birthDate,
                    relationship = con.relationship,
                    risuLName = con.risuLName,
                    risuFname = con.risuFname,
                    cityName = con.cityName,
                    risuIdNo = con.risuIdNo
                )
            } ?: emptyList(),
            illnessList = illnessList,
            pregnancyList = pregnancyList
        )
    } ?: null

    val pregnancyStatusDN = pregnancyStatusList.map { status ->
        UserRequestDetailDN.PregnancyStatusDN(
            code = status.code,
            name = status.name
        )
    }

    val pregnancyTypeDN = pregnancyTypeList.map { type ->
        UserRequestDetailDN.PregnancyTypeDN(
            code = type.code,
            name = type.name
        )
    }

    val article16Detail = article16Response?.data?.let { article16 ->
        UserRequestDetailDN.Article16DetailDN(
            defectDesc = article16.defectDesc,
            objectionPhotoList = article16.objectionPhotos?.map { photo ->
                UserRequestDetailDN.ObjectionPhotoDN(
                    guid = photo.guid,
                    seqNo = photo.seqNo,
                    type = photo.type
                )
            } ?: emptyList()
        )
    }

    val deferredInstallmentDetail = deferredInstallmentResponse?.data?.let { deferred ->
        UserRequestDetailDN.DeferredInstallmentDetailDN(
            firstName = deferred.firstName,
            lastName = deferred.lastName,
            nationalId = deferred.nationalId,
            birthDate = deferred.birthDate,
            pensionerNationalId = deferred.pensionerNationalId,
            userFirstName = deferred.userFirstName,
            userLastName = deferred.userLastName,
            pensionerId = deferred.pensionerId,
            bankName = deferred.bank?.bankName,
            bankBranch = deferred.bankBranch,
            installmentAmount = deferred.installmentAmount,
            installmentCount = deferred.installmentCount,
            loanAmount = deferred.loanAmount,
            guaranteeAmount = deferred.guaranteeAmount
        )
    }

    val followUpObjectionDN = followUpResponse.ListDataResponse.list.map { history ->
        UserRequestDetailDN.ResultFollowUpObjectionDN(
            requestNumber = history.reqno,
            requestType = history.reqtype,
            branchId = history.brchcode,
            requestDesc = history.requestDesc,
            statusDesc = history.cStatusDesc,
            answerTypeDesc = history.answerTypeDesc,
            resultDesc = history.resultDesc,
            userDesc = history.userDesc,
            requestDate = history.requestDate,
            answerDate = history.answerDate,
            branchName = history.brchName
        )
    }

    val documentsDN = documentList.map { doc ->
        UserRequestDetailDN.RequestFileDN(
            documentFile = doc.guid,
            documentType = doc.fileType
        )
    }

    return UserRequestDetailDN(
        statusModel = statusModel,
        infoModel = infoModel,
        pregnancyStatusList = pregnancyStatusDN,
        pregnancyTypeList = pregnancyTypeDN,
        article16Detail = article16Detail,
        deferredInstallmentDetail = deferredInstallmentDetail,
        followUpObjectionHistory = followUpObjectionDN,
        documentsList = documentsDN
    )
}