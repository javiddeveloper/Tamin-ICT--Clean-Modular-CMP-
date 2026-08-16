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
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailDN.PregnancyStatusDN
import com.tamin.taminhamrah.model.request.DownloadFileResponse
import com.tamin.taminhamrah.model.request.DownloadFileResponse.DownloadFileDetail
import com.tamin.taminhamrah.model.request.PregnancyStatusResponse.PregnancyStatusModel
import com.tamin.taminhamrah.model.request.PregnancyTypeResponse.PregnancyTypeModel
import com.tamin.taminhamrah.model.request.ShortTermRequestInfoResponse
import com.tamin.taminhamrah.model.request.ShortTermStatusResponse
import com.tamin.taminhamrah.remoteDataSource.UserRequestDetailRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
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

    private suspend fun handleLoadDetail(requestId: Long, requestType: Int? = null): Flow<UserRequestDetailState.PartialState> = flow {
        emit(UserRequestDetailState.PartialState.Loading(true))
        try {
            if (requestType == 0) {
                requestType = (UserRequestTypeDN.values().find { it.serviceId == requestType } ?: UserRequestTypeDN.values().first()).serviceId
            }

            val result = oldRepository.getShortTermRequestDetails(requestId.toString())

            when (result) {
                is UserRequestDetailDN -> {
                    val mapResult = mapToPR(result)
                    val pregnancyStatusList = result.pregnancyStatusList
                    val pregnancyTypeList = result.pregnancyTypeList

                    emit(UserRequestDetailState.PartialState.Loaded(mapResult))

                    if (requestType == ServiceTypeId.PREGNANCING.id) {
                        launchPregnancyDetails(requestId.toString())
                    }

                    if (pregnancyStatusList.isNotEmpty() && pregnancyTypeList.isNotEmpty()) {
                        val pregnancyStatusResult = oldRepository.getShortTermRequestDetails(requestId.toString())
                        val pregnancyInfo = mapToPR(pregnancyStatusResult)
                        emit(UserRequestDetailState.PartialState.Loaded(pregnancyInfo))
                    }
                }
                else -> {
                    val pregnancyResult = oldRepository.getShortTermRequestDetails(requestId.toString())
                    emit(UserRequestDetailState.PartialState.Loaded(mapToPR(pregnancyResult)))
                }
            }
        } catch (e: Exception) {
            emit(UserRequestDetailState.PartialState.Error(e.message))
        }
    }.map { partialState ->
        when (partialState) {
            is UserRequestDetailState.PartialState.Loading ->
                currentState.copy(isLoading = partialState.isLoading, error = null)
            is UserRequestDetailState.PartialState.Loaded ->
                currentState.copy(isLoading = false, request = partialState.request, error = null)
            is UserRequestDetailState.PartialState.Error ->
                currentState.copy(isLoading = false, error = partialState.message)
            else -> currentState
        }
    }

    private suspend fun handleLoadPregnancyDetails(referenceId: String, requestType: Int): Flow<UserRequestDetailState.PartialState> = flow {
        try {
            val pregnancyStatus = remoteDataSource.getPregnancyStatus()
            val pregnancyTypes = remoteDataSource.getPregnancyTypes()

            if (pregnancyStatus.baseStatus.serviceStatus != "SUCCESS" ||
                pregnancyTypes.baseStatus.serviceStatus != "SUCCESS") {
                throw Exception("خطا در دریافت اطلاعات حاملگی")
            }

            emit(UserRequestDetailState.PartialState.Error("اطلاعات حاملگی دریافت شد"))
        } catch (e: Exception) {
            emit(UserRequestDetailState.PartialState.Error(e.message))
        }
    }.map { partialState ->
        when (partialState) {
            is UserRequestDetailState.PartialState.Loading ->
                currentState.copy(isLoading = partialState.isLoading, error = null)
            is UserRequestDetailState.PartialState.Error ->
                currentState.copy(isLoading = false, error = partialState.message)
            else -> currentState
        }
    }

    private suspend fun handleLoadArticle16Details(objectionNumber: Long): Flow<UserRequestDetailState.PartialState> = flow {
        try {
            val article16Response = remoteDataSource.getArticle16RequestInfo(objectionNumber)

            if (article16Response.baseStatus.serviceStatus != "SUCCESS") {
                throw Exception("خطا در دریافت اطلاعات مقاله ۱۶")
            }

            val parsedData = mapArticle16Response(article16Response.data)
            emit(UserRequestDetailState.PartialState.Error("اطلاعات مقاله ۱۶ دریافت شد"))
        } catch (e: Exception) {
            emit(UserRequestDetailState.PartialState.Error(e.message))
        }
    }.map { partialState ->
        when (partialState) {
            is UserRequestDetailState.PartialState.Loading ->
                currentState.copy(isLoading = partialState.isLoading, error = null)
            is UserRequestDetailState.PartialState.Error ->
                currentState.copy(isLoading = false, error = partialState.message)
            else -> currentState
        }
    }

    private suspend fun handleLoadDeferredInstallmentDetails(installId: String): Flow<UserRequestDetailState.PartialState> = flow {
        try {
            val deferredInstallmentResponse = remoteDataSource.getDeferredInstallmentInfo(installId)

            if (deferredInstallmentResponse.baseStatus.serviceStatus != "SUCCESS") {
                throw Exception("خطا در دریافت اطلاعات اقساط معوقه")
            }

            val parsedData = mapDeferredInstallmentResponse(deferredInstallmentResponse.data)
            emit(UserRequestDetailState.PartialState.Error("اطلاعات اقساط معوقه دریافت شد"))
        } catch (e: Exception) {
            emit(UserRequestDetailState.PartialState.Error(e.message))
        }
    }.map { partialState ->
        when (partialState) {
            is UserRequestDetailState.PartialState.Loading ->
                currentState.copy(isLoading = partialState.isLoading, error = null)
            is UserRequestDetailState.PartialState.Error ->
                currentState.copy(isLoading = false, error = partialState.message)
            else -> currentState
        }
    }

    private suspend fun handleLoadFollowUpObjectionHistory(referenceId: String): Flow<UserRequestDetailState.PartialState> = flow {
        try {
            val followUpResponse = remoteDataSource.getFollowUpObjectionHistory(referenceId)

            if (followUpResponse.baseStatus.serviceStatus != "SUCCESS") {
                throw Exception("خطا در دریافت تاریخچه اعتراض")
            }

            emit(UserRequestDetailState.PartialState.Error("تاریخچه اعتراض دریافت شد"))
        } catch (e: Exception) {
            emit(UserRequestDetailState.PartialState.Error(e.message))
        }
    }.map { partialState ->
        when (partialState) {
            is UserRequestDetailState.PartialState.Loading ->
                currentState.copy(isLoading = partialState.isLoading, error = null)
            is UserRequestDetailState.PartialState.Error ->
                currentState.copy(isLoading = false, error = partialState.message)
            else -> currentState
        }
    }

    private suspend fun handleDownloadImage(guid: String): Flow<UserRequestDetailState.PartialState> = flow {
        try {
            val documentResponse = remoteDataSource.downloadDocument(guid)

            if (documentResponse.baseStatus.serviceStatus != "SUCCESS") {
                throw Exception("خطا در دانلود فایل")
            }

            val documentDetail = documentResponse.detail
            val docPR = DocumentPR(
                fileName = documentResponse.fileNameRes,
                guid = documentDetail?.guid,
                fileType = documentDetail?.fileType
            )

            currentState.request?.let { existingRequest ->
                val updatedDocuments = (existingRequest.documentsList ?: emptyList()) + docPR
                val updatedRequest = existingRequest.copy(documentsList = updatedDocuments)
                emit(UserRequestDetailState.PartialState.Loaded(updatedRequest))
            }
        } catch (e: Exception) {
            emit(UserRequestDetailState.PartialState.Error(e.message))
        }
    }.map { partialState ->
        when (partialState) {
            is UserRequestDetailState.PartialState.Loading ->
                currentState.copy(isLoading = partialState.isLoading, error = null)
            is UserRequestDetailState.PartialState.Loaded ->
                currentState.copy(isLoading = false, request = partialState.request, error = null)
            is UserRequestDetailState.PartialState.Error ->
                currentState.copy(isLoading = false, error = partialState.message)
            else -> currentState
        }
    }

    override fun reduceState(
        currentState: UserRequestDetailState,
        partialState: UserRequestDetailState.PartialState
    ): UserRequestDetailState = when (partialState) {
        is UserRequestDetailState.PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = null
        )

        is UserRequestDetailState.PartialState.Loaded -> currentState.copy(
            isLoading = false,
            request = partialState.request,
            error = null
        )

        is UserRequestDetailState.PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )

        is UserRequestDetailState.PartialState.SetReferenceId -> currentState.copy(
            requestReferenceId = partialState.referenceId
        )

        is UserRequestDetailState.PartialState.SetRequestType -> currentState.copy(
            requestType = partialState.requestType
        )

        is UserRequestDetailState.PartialState.SetObjectionNumber -> currentState.copy(
            objectionNumber = partialState.objectionNumber
        )

        is UserRequestDetailState.PartialState.SetDeferredInstallmentId -> currentState.copy(
            deferredInstallmentId = partialState.deferredInstallmentId
        )

        is UserRequestDetailState.PartialState.UpdateDocuments -> currentState.copy(
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
                bankName = deferred.bankName,
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

    private fun mapArticle16Response(response: com.tamin.taminhamrah.model.request.Article16RequestInfoResponse.Article16DetailResponse?): UserRequestDetailPR.Article16DetailPR {
        return response?.let { data ->
            UserRequestDetailPR.Article16DetailPR(
                defectDesc = data.defectDesc,
                objectionPhotoList = data.objectionPhotos.map { photo ->
                    UserRequestDetailPR.ObjectionPhotoPR(
                        guid = photo.guid,
                        seqNo = photo.seqNo,
                        type = photo.type,
                        documentTitle = "نسخه پرانتز پزشک"
                    )
                }
            )
        } ?: UserRequestDetailPR.Article16DetailPR(
            defectDesc = null,
            objectionPhotoList = emptyList()
        )
    }

    private fun mapDeferredInstallmentResponse(com.tamin.taminhamrah.model.request.DeferredInstallmentInfoResponse.DeferredInstallmentDetailResponse?): UserRequestDetailPR.DeferredInstallmentPR {
        return data?.let { deferred ->
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
        } ?: UserRequestDetailPR.DeferredInstallmentPR(
            firstName = null,
            lastName = null,
            nationalId = null,
            birthDate = null,
            pensionerNationalId = null,
            userFirstName = null,
            userLastName = null,
            pensionerId = null,
            bankName = null,
            bankBranch = null,
            installmentAmount = null,
            installmentCount = null,
            loanAmount = null,
            guaranteeAmount = null
        )
    }

    companion object ServiceTypeId {
        const val ILL_DAY = 10
        const val ORTHOTICS = 12
        const val ARTICLE16 = 26
        const val PREGNANCING = 11
        const val DEFERRED_INSTALLMENT = 22
        const val MEDICAL_COMMISSION = 27
    }
}