package com.tamin.taminhamrah.dataSource.workshopsSource

import com.tamin.taminhamrah.apiService.WorkShopsApiService
import com.tamin.taminhamrah.model.BaseUrlKey
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestInfoDTO
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveRequestDTO
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveResultDTO
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoDTO
import com.tamin.taminhamrah.model.workshop.AssignerContractDTO
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDTO
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveRequestDTO
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveResultDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentPreCheckDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDTO
import com.tamin.taminhamrah.model.workshop.DebitReasonDTO
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.EmployerAgreementSubmitRequestDTO
import com.tamin.taminhamrah.model.workshop.EmployerCommitmentInfoDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeContractDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeRequestDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopDTO
import com.tamin.taminhamrah.model.workshop.NewMemberConfirmResultDTO
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDTO
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.SettlementCertificateDTO
import com.tamin.taminhamrah.model.workshop.SettlementCertificateDetailDTO
import com.tamin.taminhamrah.model.workshop.SettlementRequestDTO
import com.tamin.taminhamrah.model.workshop.SettlementSubjectDTO
import com.tamin.taminhamrah.model.workshop.SmsMessageDTO
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDTO
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionDTO
import com.tamin.taminhamrah.model.workshop.WorkshopContractDTO
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDTO
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDTO
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDTO
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDTO
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage
import com.tamin.taminhamrah.tools.readPdfChannel
import com.tamin.taminhamrah.util.NetworkConstants
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/** `serviceName` the `request-ticket` endpoint expects for the Employer → Online Services flow. */
private const val EMPLOYER_ESERVICES_AGREEMENT = "employerEservicesAgreement"

/** What the old client puts in a `mad38-head` path segment it does not filter on. */
private const val UNFILTERED_PATH_SEGMENT = "-"

internal class WorkShopsRemoteDataSourceImpl(
    private val apiService: WorkShopsApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
    private val developerOptionsRepository: DeveloperOptionsRepository,
) : WorkShopsRemoteDataSource {
    private val legalRepresentativeListQuery = ApiQueryParamDN(page = 1, start = 0, limit = 1000)
    // ---------------------------------------------------------------- کارگاه‌های کارفرما

    override suspend fun getAllEmployerAgreementByNationalId(
        query: ApiQueryParamDN
    ): ListData<EmployerAgreementDTO> = call {
        apiService.getAllEmployerAgreementByNationalId(query.toQueries()).extractData()
    }

    // ------------------------------------------------------------------- ردیف‌های پیمان

    override suspend fun getEmployerAgreementsByWorkshop(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN,
    ): ListData<EmployerAgreementDTO> = call {
        apiService.getEmployerAgreementsByWorkshop(workshopId, branchCode, query.toQueries())
            .extractData()
    }

    override suspend fun getWorkshopContracts(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN,
    ): ListData<WorkshopContractDTO> = call {
        apiService.getWorkshopContracts(workshopId, branchCode, query.toQueries()).extractData()
    }

    // ---------------------------------------------------------------------------- واگذارندگان

    override suspend fun getAssignerContracts(
        query: ApiQueryParamDN,
    ): ListData<AssignerContractDTO> = call {
        apiService.getAssignerContracts(query.toQueries()).extractData()
    }

    override suspend fun getComputationalBases(
        workshopId: String,
        contractRow: String,
        brchCode: String,
        contractSequence: String,
        query: ApiQueryParamDN,
    ): ListData<ComputationalBaseDTO> = call {
        apiService.getComputationalBases(
            workshopId = workshopId,
            contractRow = contractRow,
            // Abbreviated on purpose — the published parameter is `brchCode`, not `branchCode`.
            brchCode = brchCode,
            contractSequence = contractSequence,
            queries = query.toQueries(),
        ).extractData()
    }

    override suspend fun getComputationalBasePdf(documentId: String): PdfDownloadDTO = call {
        PdfDownloadDTO(
            pdf = InputStreamDTO(
                // Drained inside `execute`; `body<ByteReadChannel>()` hands back a channel the
                // response has already finalized and reads as an empty file.
                pdf = apiService.getComputationalBasePdf(documentId).readPdfChannel()
            )
        )
    }

    override suspend fun getSettlementSubjects(
        query: ApiQueryParamDN,
    ): ListData<SettlementSubjectDTO> = call {
        apiService.getSettlementSubjects(query.toQueries()).extractData()
    }

    override suspend fun getSettlementCertificates(
        workshopId: String,
        branchCode: String,
        contractRow: String,
        query: ApiQueryParamDN,
    ): ListData<SettlementCertificateDTO> = call {
        apiService.getSettlementCertificates(
            workshopCode = workshopId,
            branchCode = branchCode,
            contractRow = contractRow,
            // The old client sends its "no filter" value for both, hardcoded there too.
            mafasaStatus = UNFILTERED_PATH_SEGMENT,
            contractNumber = UNFILTERED_PATH_SEGMENT,
            queries = query.toQueries(),
        ).extractData()
    }

    override suspend fun getSettlementCertificateDetail(
        workshopId: String,
        branchCode: String,
        contractRow: String,
        serial: String,
        query: ApiQueryParamDN,
    ): ListData<SettlementCertificateDetailDTO> = call {
        apiService.getSettlementCertificateDetail(
            workshopCode = workshopId,
            branchCode = branchCode,
            contractRow = contractRow,
            serial = serial,
            queries = query.toQueries(),
        ).extractData()
    }

    override suspend fun uploadSettlementPdf(fileName: String, bytes: ByteArray): String = call {
        val content = MultiPartFormDataContent(
            formData {
                // `file`, the part name the old app's multipart body uses.
                append(
                    key = "file",
                    value = bytes,
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, ContentType.Application.Pdf.toString())
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    },
                )
            },
        )
        val id = apiService.uploadSettlementPdf(content).extractData()?.jsonPrimitive?.contentOrNull
        // A blank id would file the document under nothing, so it fails here instead of at submit.
        requireNotNull(id?.takeIf { it.isNotBlank() }) { "persistPdf answered without an id" }
    }

    override suspend fun submitSettlementRequest(
        id: String,
        request: SettlementRequestDTO,
    ): String = call {
        apiService.submitSettlementRequest(id, request).extractMessage()
    }

    // -------------------------------------------------------------------------- برگ پرداخت‌ها

    override suspend fun getWorkshopPaymentSheets(
        query: ApiQueryParamDN
    ): ListData<PaymentSheetDTO> = call {
        apiService.getWorkshopPaymentSheets(query.toQueries()).extractData()
    }

    override suspend fun getDebitReasons(
        query: ApiQueryParamDN
    ): ListData<DebitReasonDTO> = call {
        apiService.getDebitReasons(query.toQueries()).extractData()
    }

    // ------------------------------------------------- گردش حساب بدهی + پرداخت

    override suspend fun getWorkshopDebitList(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): ListData<WorkShopDebtDTO> = call {
        apiService.getWorkshopDebitList(workshopId, branchCode, query.toQueries()).extractData()
    }

    override suspend fun getWorkshopDemandDocuments(
        debitNumber: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): ListData<WorkshopDemandDocDTO> = call {
        apiService.getWorkshopDemandDocuments(debitNumber, branchCode, query.toQueries()).extractData()
    }

    override suspend fun getDebitTurnoverPdf(
        debitNumber: String,
        branchCode: String
    ): PdfDownloadDTO = call {
        PdfDownloadDTO(
            pdf = InputStreamDTO(
                pdf = apiService.getDebitTurnoverPdf(debitNumber, branchCode).readPdfChannel()
            )
        )
    }

    override suspend fun checkDebitPayment(
        debitNumber: String,
        branchCode: String
    ): DebitPaymentPreCheckDTO = call {
        apiService.checkDebitPayment(debitNumber, branchCode).extractData()
    }

    override suspend fun payWorkshopDebit(request: DebitPaymentRequestDTO): DebitPaymentDTO = call {
        apiService.payWorkshopDebit(request).extractData()
    }

    override suspend fun confirmPaymentTicket(ticket: String) {
        call {
            // Read from Developer Options rather than the constant: this address is absolute, so
            // it overrides whatever base URL the client it travels on was built with, and pointing
            // "سرویس TFH" at a test host used to leave this one call on production.
            val url = developerOptionsRepository.getEffectiveBaseUrl(BaseUrlKey.TFH) +
                NetworkConstants.TFH_TICKET_PATH + ticket
            // Extracted rather than ignored: that is what turns a refusal envelope into a throw,
            // which is the whole of what this call reports.
            apiService.getPaymentTicketInfo(url).extractData()
        }
    }

    // ---------------------------------------------------------------------- استعلام بدهی کارگاه

    override suspend fun getWorkshopDebtInquiry(
        workshopId: String,
        branchCode: String
    ): WorkshopDebtInquiryDTO = call {
        apiService.getWorkshopDebtInquiry(workshopId, branchCode).extractData()
    }

    // ------------------------------------------------------------------------------ اعتراض به بدهی

    override suspend fun getWorkshopObjectionableDebitList(
        workshopNumber: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): ListData<WorkShopDebtDTO> = call {
        apiService.getWorkshopObjectionableDebitList(workshopNumber, branchCode, query.toQueries())
            .extractData()
    }

    override suspend fun getObjectionElapsedDays(orderRecipeDate: String): Int = call {
        apiService.getObjectionElapsedDays(orderRecipeDate).extractData()
    }

    override suspend fun saveDebitObjection(
        request: DebitObjectionSaveRequestDTO
    ): DebitObjectionSaveResultDTO = call {
        apiService.saveDebitObjection(request).extractData()
    }

    override suspend fun getDebitObjectionPdf(seqNumber: Long): PdfDownloadDTO = call {
        PdfDownloadDTO(
            pdf = InputStreamDTO(pdf = apiService.getDebitObjectionPdf(seqNumber).readPdfChannel())
        )
    }

    // ------------------------------------------------- نام نویسی غیر حضوری بیمه شده

    override suspend fun getWorkshopRecentlyAddedMembers(
        query: ApiQueryParamDN
    ): ListData<WorkshopNewMemberDTO> = call {
        apiService.getWorkshopRecentlyAddedMembers(query.toQueries()).extractData()
    }

    override suspend fun confirmRecentlyAddedMember(
        requestId: Long
    ): NewMemberConfirmResultDTO = call {
        apiService.confirmRecentlyAddedMember(requestId).extractData()
    }

    override suspend fun deleteRecentlyAddedMember(personalId: Long) {
        call { apiService.deleteRecentlyAddedMember(personalId) }
    }

    override suspend fun checkNewMemberIsNew(nationalId: String): Boolean = call {
        apiService.checkNewMemberIsNew(nationalId).extractData()
    }

    override suspend fun createNewMemberRegistration(
        request: NewMemberRegistrationDTO,
    ): NewMemberRegistrationResultDTO = call {
        apiService.createNewMemberRegistration(request).extractData()
    }

    // ---------------------------------------------------------------------- رسیدگی به بدهی ماده ۱۶

    override suspend fun getWorkshopsDebtsList(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN
    ): ListData<WorkshopsDebtListModelDTO> = call {
        apiService.getWorkshopsDebtsList(workshopId, branchId, query.toQueries()).extractData()
    }

    override suspend fun getArticleSixteenWorkshopInfo(
        workshopId: String,
        branchCode: String
    ): ArticleSixteenWorkshopInfoDTO = call {
        apiService.getArticleSixteenWorkshopInfo(workshopId, branchCode).extractData()
    }

    override suspend fun getArticleSixteenRequestInfo(
        objectionNumber: Long
    ): ArticleSixteenRequestInfoDTO = call {
        apiService.getArticleSixteenRequestInfo(objectionNumber).extractData()
    }

    override suspend fun saveArticleSixteenRequest(
        request: ArticleSixteenSaveRequestDTO
    ): ArticleSixteenSaveResultDTO = call {
        apiService.saveArticleSixteenRequest(request).extractData()
    }

    override suspend fun getArticleSixteenReportPdf(seqNumber: Long): PdfDownloadDTO = call {
        PdfDownloadDTO(
            pdf = InputStreamDTO(pdf = apiService.getArticleSixteenReportPdf(seqNumber).readPdfChannel())
        )
    }

    // ------------------------------------------------------------------------ کارکنان / ذینفعان

    override suspend fun getWorkshopMembers(
        query: ApiQueryParamDN
    ): ListData<WorkshopMemberDTO> = call {
        apiService.getWorkshopMembers(query.toQueries()).extractData()
    }

    override suspend fun getWorkshopStackHolders(
        query: ApiQueryParamDN
    ): ListData<WorkshopStackHolderDTO> = call {
        apiService.getWorkshopStackHolders(query.toQueries()).extractData()
    }

    // ------------------------------------------------- خدمات غیرحضوری کارفرما (employerEservicesAgreement)

    override suspend fun requestEmployerAgreementTicket(
        mobileNumber: String,
        email: String,
    ): String = call {
        val filter = queryBuilder.buildFilterJson(
            listOf(
                ApiFilterDN(FilterProperty.MOBILE_NUMBER, mobileNumber, FilterOperator.EQ),
                ApiFilterDN(FilterProperty.EMAIL, email, FilterOperator.EQ),
                ApiFilterDN(FilterProperty.SERVICE_NAME, EMPLOYER_ESERVICES_AGREEMENT, FilterOperator.EQ),
            )
        )
        apiService.requestEmployerAgreementTicket(filter).extractMessage()
    }

    override suspend fun getEmployerAgreementUserInfo(
        verificationCode: String,
    ): EmployerCommitmentInfoDTO = call {
        apiService.getEmployerAgreementUserInfo(verificationCode).extractData()
    }

    override suspend fun getEmployerWorkshopsWithoutContract(
        query: ApiQueryParamDN
    ): ListData<WorkshopWithoutContractDTO> = call {
        apiService.getEmployerWorkshopsWithoutContract(query.toQueries()).extractData()
    }

    override suspend fun getEmployerWorkshopContractList(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): ListData<WorkshopContractRowDTO> = call {
        apiService.getEmployerWorkshopContractList(workshopId, branchCode, query.toQueries())
            .extractData()
    }

    override suspend fun submitEmployerAgreement(
        request: EmployerAgreementSubmitRequestDTO,
    ): String = call {
        apiService.submitEmployerAgreement(request).extractMessage()
    }

    override suspend fun getWorkShopObjections(
        query: ApiQueryParamDN
    ): ListData<WorkShopObjectionDTO> = call {
        apiService.getWorkShopObjections(query.toQueries()).extractData()
    }

    override suspend fun getWorkShopObjectionSms(
        objectionCode: Long,
        query: ApiQueryParamDN,
    ): ListData<SmsMessageDTO> = call {
        apiService.getWorkShopObjectionSms(objectionCode, query.toQueries()).extractData()
    }

    private fun ApiQueryParamDN.toQueries(): Map<String, String> = queryBuilder.buildQuery(this)

    /**
     * The one error contract every call in this source shares: business failures keep the server's
     * own message, anything else (transport, serialization) surfaces as a connection error.
     */
    private suspend fun <T> call(block: suspend () -> T): T = try {
        block()
    } catch (e: TaminErrorUriException) {
        throw errorParser.parseGeneralError(e)
    } catch (e: Exception) {
        throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
    }

    override suspend fun getLegalRepresentativeWorkshops(): ListData<LegalRepresentativeWorkshopDTO>? {
        return try {
            val queries = queryBuilder.buildQuery(legalRepresentativeListQuery)
            val response = apiService.getLegalRepresentativeWorkshops(queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getLegalRepresentatives(
        workshopId: String,
        branchCode: String
    ): ListData<LegalRepresentativeDTO>? {
        return try {
            val queries = queryBuilder.buildQuery(legalRepresentativeListQuery) + mapOf(
                "stackType" to "4",
                "workshopId" to workshopId,
                "branchCode" to branchCode,
            )
            val response = apiService.getLegalRepresentatives(queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getLegalRepresentativeWorkshopContracts(
        workshopId: String,
        branchCode: String
    ): ListData<LegalRepresentativeContractDTO>? {
        return try {
            val queries = queryBuilder.buildQuery(legalRepresentativeListQuery)
            val response = apiService.getLegalRepresentativeWorkshopContracts(workshopId, branchCode, queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun requestLegalRepresentativeTicket(nationalCode: String?) {
        try {
            val response = if (nationalCode.isNullOrEmpty()) {
                apiService.requestLegalTicket()
            } else {
                apiService.requestLegalTicketWithNationalCode(nationalCode)
            }
            response.extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun verifyLegalRepresentativeTicket(ticket: String) {
        try {
            apiService.validateLegalTicket(ticket).extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun submitLegalRepresentative(ticket: String, request: LegalRepresentativeRequestDTO) {
        try {
            apiService.submitLegalRepresentative(ticket, request).extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun deleteLegalRepresentative(ticket: String, stackId: Long) {
        try {
            apiService.deleteLegalRepresentative(ticket, stackId).extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }
}
