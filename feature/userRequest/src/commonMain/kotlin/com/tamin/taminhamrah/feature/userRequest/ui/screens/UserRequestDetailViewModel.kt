package com.tamin.taminhamrah.feature.userRequest.ui.screens

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.core.data.repository.UserRequestDetailRepository
import com.tamin.taminhamrah.core.data.mapper.userRequestDetail.mapToDetail
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.DocumentPR
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailContract
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailEvent
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailIntent
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailState
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailDN
import javax.inject.Inject

class UserRequestDetailViewModel @Inject constructor(
    private val remoteDataSource: UserRequestDetailRemoteDataSource,
    private val repository: UserRequestDetailRepository
) : BaseViewModel<UserRequestDetailState, UserRequestDetailState.PartialState, UserRequestDetailEvent, UserRequestDetailIntent>(
    initialState = UserRequestDetailState()
) {

    override fun handleIntent(intent: UserRequestDetailIntent): Flow<UserRequestDetailState.PartialState> {
        return when (intent) {
            is UserRequestDetailIntent.NavigateBack -> flow {
                sendEvent(UserRequestDetailEvent.NavigateBack)
            }

            is UserRequestDetailIntent.LoadDetail -> handleLoadDetail(intent.requestId)
            is UserRequestDetailIntent.LoadPregnancyDetails -> handleLoadPregnancyDetails(intent.referenceId, intent.requestType)
            is UserRequestDetailIntent.LoadArticle16Details -> handleLoadArticle16Details(intent.objectionNumber)
            is UserRequestDetailIntent.LoadDeferredInstallmentDetails -> handleLoadDeferredInstallmentDetails(intent.installId)
            is UserRequestDetailIntent.LoadFollowUpObjectionHistory -> handleLoadFollowUpObjectionHistory(intent.referenceId)
            is UserRequestDetailIntent.DownloadImage -> handleDownloadImage(intent.guid)
        }
    }

    private fun handleLoadDetail(requestId: Long, requestType: Int? = null): Flow<UserRequestDetailState.PartialState> = flow {
        emit(UserRequestDetailState.PartialState.Loading(true))
        try {
            val result = repository.getShortTermRequestDetails(requestId.toString())

            result.let { dn ->
                mapToPregnancyStatusAndTypes(dn)?.let { (pregnancyStatus, pregnancyTypes) ->
                    val prResult = mapToPR(dn)
                    emit(UserRequestDetailState.PartialState.SetReferenceId(requestId.toString()))
                    emit(UserRequestDetailState.PartialState.SetRequestType(requestType ?: 0))
                    emit(UserRequestDetailState.PartialState.SetObjectionNumber(intent.objectionNumber))
                    emit(UserRequestDetailState.PartialState.Loaded(prResult))
                } ?: run {
                    val prResult = mapToPR(result as UserRequestDetailDN)
                    emit(UserRequestDetailState.PartialState.SetReferenceId(requestId.toString()))
                    emit(UserRequestDetailState.PartialState.SetRequestType(requestType ?: 0))
                    emit(UserRequestDetailState.PartialState.Loaded(prResult))
                }
            }

            if (requestType == UserRequestDetailViewModel.ServiceTypeId.PREGNANCING) {
                val pregnancyStatusResponse = remoteDataSource.getPregnancyStatus()
                val pregnancyTypesResponse = remoteDataSource.getPregnancyTypes()

                if (pregnancyStatusResponse.baseStatus.serviceStatus == "SUCCESS" &&
                    pregnancyTypesResponse.baseStatus.serviceStatus == "SUCCESS") {

                    emit(UserRequestDetailState.PartialState.Error(
                        "اطلاعات حاملگی دریافت شد: ${pregnancyStatusResponse.ListDataResponse.list.size} نوع\n" +
                        "اطلاعات نوزاد: ${pregnancyTypesResponse.ListDataResponse.list.size} نوع"
                    ))
                }
            }
        } catch (e: Exception) {
            emit(UserRequestDetailState.PartialState.Error(e.message ?: "خطای ناشناخته در دریافت اطلاعات"))
        }
    }.catch { error ->
        emit(UserRequestDetailState.PartialState.Error(error.message ?: "خطا در لود کردن جزئیات"))
    }

    private suspend fun handleLoadPregnancyDetails(referenceId: String, requestType: Int): Flow<UserRequestDetailState.PartialState> = flow {
        try {
            val pregnancyStatus = remoteDataSource.getPregnancyStatus()
            val pregnancyTypes = remoteDataSource.getPregnancyTypes()

            if (pregnancyStatus.baseStatus.serviceStatus == "SUCCESS") {
                val statusList = pregnancyStatus.ListDataResponse.list.map { model ->
                    UserRequestDetailState.PartialState.UpdateDocuments(
                        (currentState.request?.documentsList ?: emptyList()) + DocumentPR(
                            fileName = model.name,
                            guid = model.code,
                            fileType = "pregnancy_status"
                        )
                    )
                }
                statusList.forEach { emit(it) }
            }

            if (pregnancyTypes.baseStatus.serviceStatus == "SUCCESS") {
                val typeList = pregnancyTypes.ListDataResponse.list.map { model ->
                    UserRequestDetailState.PartialState.UpdateDocuments(
                        (currentState.request?.documentsList ?: emptyList()) + DocumentPR(
                            fileName = model.name,
                            guid = model.code,
                            fileType = "pregnancy_type"
                        )
                    )
                }
                typeList.forEach { emit(it) }
            }

            emit(UserRequestDetailState.PartialState.Error("اطلاعات حاملگی دریافت شد"))
        } catch (e: Exception) {
            emit(UserRequestDetailState.PartialState.Error(e.message ?: "خطا در دریافت اطلاعات حاملگی"))
        }
    }

    private fun handleLoadArticle16Details(objectionNumber: Long): Flow<UserRequestDetailState.PartialState> = flow {
        try {
            val response = remoteDataSource.getArticle16RequestInfo(objectionNumber)

            if (response.baseStatus.serviceStatus != "SUCCESS") {
                throw Exception("خطا در دریافت اطلاعات مقاله ۱۶")
            }

            val detail = response.data
            val photos = detail?.objectionPhotos ?: emptyList()

            val updatedDocuments = (currentState.request?.documentsList ?: emptyList()) + photos.map { photo ->
                DocumentPR(
                    fileName = photo.guid,
                    guid = photo.guid,
                    fileType = photo.type ?: "objection_photo"
                )
            }

            emit(UserRequestDetailState.PartialState.UpdateDocuments(updatedDocuments))
            emit(UserRequestDetailState.PartialState.Error("اطلاعات مقاله ۱۶ با موفقیت دریافت شد (${photos.size} عکس)"))
        } catch (e: Exception) {
            emit(UserRequestDetailState.PartialState.Error(e.message ?: "خطا در دریافت اطلاعات مقاله ۱۶"))
        }
    }

    private fun handleLoadDeferredInstallmentDetails(installId: String): Flow<UserRequestDetailState.PartialState> = flow {
        try {
            val response = remoteDataSource.getDeferredInstallmentInfo(installId)

            if (response.baseStatus.serviceStatus != "SUCCESS") {
                throw Exception("خطا در دریافت اطلاعات اقساط معوقه")
            }

            val detail = response.data
            val loansInfo = mapLoanInstallmentInfo(detail)

            emit(UserRequestDetailState.PartialState.UpdateDocuments(
                (currentState.request?.documentsList ?: emptyList()) + DocumentPR(
                    fileName = "نیاز مالی${detail?.loanAmount ?: ""}",
                    guid = installId,
                    fileType = "loan_installment"
                )
            ))

            emit(UserRequestDetailState.PartialState.Error("اطلاعات اقساط معوقه دریافت شد"))
        } catch (e: Exception) {
            emit(UserRequestDetailState.PartialState.Error(e.message ?: "خطا در دریافت اطلاعات اقساط معوقه"))
        }
    }

    private fun handleLoadFollowUpObjectionHistory(referenceId: String): Flow<UserRequestDetailState.PartialState> = flow {
        try {
            val response = remoteDataSource.getFollowUpObjectionHistory(referenceId)

            if (response.baseStatus.serviceStatus != "SUCCESS") {
                throw Exception("خطا در دریافت تاریخچه اعتراض")
            }

            val historyList = response.ListDataResponse.list.map { history ->
                UserRequestDetailState.PartialState.UpdateDocuments(
                    (currentState.request?.documentsList ?: emptyList()) + DocumentPR(
                        fileName = "${history.reqno} - ${history.resultDesc}",
                        guid = history.reqno,
                        fileType = "objection_history"
                    )
                )
            }

            historyList.forEach { emit(it) }
            emit(UserRequestDetailState.PartialState.Error("تاریخچه اعتراض دریافت شد (${response.ListDataResponse.list.size} مورد)"))
        } catch (e: Exception) {
            emit(UserRequestDetailState.PartialState.Error(e.message ?: "خطا در دریافت تاریخچه اعتراض"))
        }
    }

    private fun handleDownloadImage(guid: String): Flow<UserRequestDetailState.PartialState> = flow {
        try {
            val documentResponse = remoteDataSource.downloadDocument(guid)

            if (documentResponse.baseStatus.serviceStatus != "SUCCESS") {
                throw Exception("خطا در دانلود فایل")
            }

            val docPR = DocumentPR(
                fileName = documentResponse.fileNameRes,
                guid = documentResponse.detail?.guid,
                fileType = documentResponse.detail?.fileType ?: "downloaded"
            )

            emit(UserRequestDetailState.PartialState.UpdateDocuments(
                (currentState.request?.documentsList ?: emptyList()) + docPR
            ))
        } catch (e: Exception) {
            emit(UserRequestDetailState.PartialState.Error(e.message ?: "خطا در دانلود فایل"))
        }
    }

    private fun mapToPregnancyStatusAndTypes(
        dn: UserRequestDetailDN
    ): Pair<UserRequestDetailState.PartialState.UpdateDocuments, UserRequestDetailState.PartialState.UpdateDocuments>? {
        if (dn.pregnancyStatusList.isEmpty() || dn.pregnancyTypeList.isEmpty()) {
            return null
        }

        val newDocsStatus = dn.pregnancyStatusList.map { status ->
            UserRequestDetailState.PartialState.UpdateDocuments(
                (currentState.request?.documentsList ?: emptyList()) + DocumentPR(
                    fileName = status.name,
                    guid = status.code,
                    fileType = "pregnancy_status_${status.code}"
                )
            )
        }

        val newDocsType = dn.pregnancyTypeList.map { type ->
            UserRequestDetailState.PartialState.UpdateDocuments(
                (currentState.request?.documentsList ?: emptyList()) + DocumentPR(
                    fileName = type.name,
                    guid = type.code,
                    fileType = "pregnancy_type_${type.code}"
                )
            )
        }

        return Pair(newDocsStatus.first(), newDocsType.first())
    }

    private fun mapLoanInstallmentInfo(detail: com.tamin.taminhamrah.model.request.DeferredInstallmentInfoResponse.DeferredInstallmentDetailResponse?) =
        UserRequestDetailState.PartialState.Error("اطلاعات اقساط معوقه دریافت شد")

    override fun reduceState(
        currentState: UserRequestDetailState,
        partialState: UserRequestDetailState.PartialState
    ): UserRequestDetailState = when (partialState) {
        is UserRequestDetailState.PartialState.Loading ->
            currentState.copy(isLoading = partialState.isLoading, error = null)

        is UserRequestDetailState.PartialState.Loaded ->
            currentState.copy(isLoading = false, request = partialState.request, error = null)

        is UserRequestDetailState.PartialState.Error ->
            currentState.copy(isLoading = false, error = partialState.message)

        is UserRequestDetailState.PartialState.SetReferenceId ->
            currentState.copy(requestReferenceId = partialState.referenceId)

        is UserRequestDetailState.PartialState.SetRequestType ->
            currentState.copy(requestType = partialState.requestType)

        is UserRequestDetailState.PartialState.SetObjectionNumber ->
            currentState.copy(objectionNumber = partialState.objectionNumber)

        is UserRequestDetailState.PartialState.SetDeferredInstallmentId ->
            currentState.copy(deferredInstallmentId = partialState.deferredInstallmentId)

        is UserRequestDetailState.PartialState.UpdateDocuments ->
            currentState.copy(
                request = currentState.request?.copy(
                    documentsList = partialState.documents
                )
            )
    }

    override fun createErrorState(message: String): UserRequestDetailState.PartialState =
        UserRequestDetailState.PartialState.Error(message)

    private fun mapToPR(dn: UserRequestDetailDN): UserRequestDetailPR = UserRequestDetailPR(
        statusDetailStatus = dn.statusModel?.let { status ->
            UserRequestDetailPR.StatusDetailPR(
                dateAcc = status.dateAcc?.toString(),
                processResult = status.processResult,
                rejectReason = status.rejectReason
            )
        },
        graphicDetailInfo = dn.infoModel?.let { info ->
            UserRequestDetailPR.InfoDetailPR(
                mobile = info.mobile,
                risuFullName = info.risuFullName,
                risuid = info.risuid,
                orthoticsList = info.orthoticsList.map { orth ->
                    UserRequestDetailPR.OrthoticsPR(
                        bankAccount = orth.bankAccount,
                        bankName = orth.bankName,
                        branchName = orth.branchName,
                        resultMessage = orth.resultMessage,
                        fileList = orth.fileList.map { file ->
                            UserRequestDetailPR.RequestFilePR(
                                documentFile = file.documentFile,
                                documentType = file.documentType
                            )
                        },
                        useTaj = orth.useTaj?.toString(),
                        bimSDate = orth.bimSDate?.toString(),
                        bimEdate = orth.bimEdate?.toString(),
                        bimDrname = orth.bimDrname,
                        bimDrid = orth.bimDrid
                    )
                },
                consequentialList = info.consequentialList.map { con ->
                    UserRequestDetailPR.ConsequentialPR(
                        risuId = con.risuId,
                        bletEndDate = con.bletEndDate,
                        birthDate = con.birthDate,
                        relationship = con.relationship,
                        risuLName = con.risuLName,
                        risuFname = con.risuFname,
                        cityName = con.cityName,
                        risuIdNo = con.risuIdNo
                    )
                },
                illnessList = info.illnessList.map { ill ->
                    UserRequestDetailPR.IllnessPR(
                        bankAccount = ill.bankAccount,
                        bankName = ill.bankName,
                        branchName = ill.branchName,
                        resultMessage = ill.resultMessage,
                        fileList = ill.fileList.map { file ->
                            UserRequestDetailPR.RequestFilePR(
                                documentFile = file.documentFile,
                                documentType = file.documentType
                            )
                        },
                        bimSDate = ill.bimSDate?.toString(),
                        bimEdate = ill.bimEdate?.toString(),
                        bimDrname = ill.bimDrname,
                        bimDrid = ill.bimDrid
                    )
                },
                pregnancyList = info.pregnancyList.map { preg ->
                    UserRequestDetailPR.PregnancyPR(
                        barDrid = preg.barDrid,
                        barDd = preg.barDd,
                        barDemDat = preg.barDemDat?.toString(),
                        drName = preg.drName,
                        startDate = preg.startDate?.toString(),
                        endDate = preg.endDate?.toString(),
                        barType = preg.barType,
                        barChild = preg.barChild,
                        orthoticsRequest = UserRequestDetailPR.OrthoticsPR(
                            bankAccount = preg.orthoticsRequest.bankAccount,
                            bankName = preg.orthoticsRequest.bankName,
                            branchName = preg.orthoticsRequest.branchName,
                            resultMessage = preg.orthoticsRequest.resultMessage,
                            fileList = preg.orthoticsRequest.fileList.map { file ->
                                UserRequestDetailPR.RequestFilePR(
                                    documentFile = file.documentFile,
                                    documentType = file.documentType
                                )
                            },
                            useTaj = preg.orthoticsRequest.useTaj?.toString(),
                            bimSDate = preg.orthoticsRequest.bimSDate?.toString(),
                            bimEdate = preg.orthoticsRequest.bimEdate?.toString(),
                            bimDrname = preg.orthoticsRequest.bimDrname,
                            bimDrid = preg.orthoticsRequest.bimDrid
                        )
                    )
                }
            )
        },
        pregnancyList = dn.pregnancyStatusList.map { pregStatus ->
            UserRequestDetailPR.PregnancyPR(
                barDrid = pregStatus.code,
                barDd = null,
                barDemDat = null,
                drName = pregStatus.name,
                startDate = null,
                endDate = null,
                barType = null,
                barChild = null,
                orthoticsRequest = UserRequestDetailPR.OrthoticsPR(
                    bankAccount = null,
                    bankName = null,
                    branchName = null,
                    resultMessage = null,
                    fileList = emptyList(),
                    useTaj = null,
                    bimSDate = null,
                    bimEdate = null,
                    bimDrname = null,
                    bimDrid = null
                )
            )
        },
        article16Detail = dn.article16Detail?.let { article16 ->
            UserRequestDetailPR.Article16DetailPR(
                defectDesc = article16.defectDesc,
                objectionPhotoList = article16.objectionPhotoList.map { photo ->
                    UserRequestDetailPR.ObjectionPhotoPR(
                        guid = photo.guid,
                        seqNo = photo.seqNo,
                        type = photo.type,
                        documentTitle = "نسخه پرانتز پزشک"
                    )
                }
            )
        },
        deferredInstallmentDetail = dn.deferredInstallmentDetail?.let { deferred ->
            UserRequestDetailPR.DeferredInstallmentPR(
                firstName = deferred.firstName,
                lastName = deferred.lastName,
                nationalId = deferred.nationalId,
                birthDate = deferred.birthDate?.toString(),
                pensionerNationalId = deferred.pensionerNationalId,
                userFirstName = deferred.userFirstName,
                userLastName = deferred.userLastName,
                pensionerId = deferred.pensionerId,
                bankName = deferred.bank?.bankName,
                bankBranch = deferred.bankBranch,
                installmentAmount = deferred.installmentAmount?.toString(),
                installmentCount = deferred.installmentCount?.toString(),
                loanAmount = deferred.loanAmount?.toString(),
                guaranteeAmount = deferred.guaranteeAmount?.toString()
            )
        },
        followUpObjectionList = dn.followUpObjectionHistory.map { history ->
            UserRequestDetailPR.ResultFollowUpObjectionPR(
                requestNumber = history.requestNumber,
                requestType = history.requestType,
                branchId = history.branchId,
                requestDesc = history.requestDesc,
                statusDesc = history.statusDesc,
                answerTypeDesc = history.answerTypeDesc,
                resultDesc = history.resultDesc,
                userDesc = history.userDesc,
                requestDate = history.requestDate,
                answerDate = history.answerDate,
                branchName = history.branchName
            )
        },
        documentsList = dn.documentsList?.map { doc ->
            UserRequestDetailPR.DocumentPR(
                fileName = doc.documentFile,
                fileContent = null,
                uri = null,
                fileType = doc.documentType
            )
        } ?: emptyList()
    )

    companion object ServiceTypeId {
        const val ILL_DAY = 10
        const val ORTHOTICS = 12
        const val ARTICLE16 = 26
        const val PREGNANCING = 11
        const val DEFERRED_INSTALLMENT = 22
        const val MEDICAL_COMMISSION = 27
    }
}