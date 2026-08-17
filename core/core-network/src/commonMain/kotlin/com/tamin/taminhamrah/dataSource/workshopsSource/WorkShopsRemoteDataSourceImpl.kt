package com.tamin.taminhamrah.dataSource.workshopsSource

import com.tamin.taminhamrah.apiService.WorkShopsApiService
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.Article16RequestInfoDTO
import com.tamin.taminhamrah.model.workshop.Article16SaveRequestDTO
import com.tamin.taminhamrah.model.workshop.Article16SaveResultDTO
import com.tamin.taminhamrah.model.workshop.Article16WorkshopInfoDTO
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveRequestDTO
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveResultDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentPreCheckDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDTO
import com.tamin.taminhamrah.model.workshop.DebitReasonDTO
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.NewMemberConfirmResultDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDTO
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDTO
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.readPdfChannel

internal class WorkShopsRemoteDataSourceImpl(
    private val apiService: WorkShopsApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : WorkShopsRemoteDataSource {

    // ---------------------------------------------------------------- کارگاه‌های کارفرما

    override suspend fun getAllEmployerAgreementByNationalId(
        query: ApiQueryParamDN
    ): ListData<EmployerAgreementDTO> = call {
        apiService.getAllEmployerAgreementByNationalId(query.toQueries()).extractData()
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

    // ---------------------------------------------------------------------- رسیدگی به بدهی ماده ۱۶

    override suspend fun getWorkshopsDebtsList(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN
    ): ListData<WorkshopsDebtListModelDTO> = call {
        apiService.getWorkshopsDebtsList(workshopId, branchId, query.toQueries()).extractData()
    }

    override suspend fun getArticle16WorkshopInfo(
        workshopId: String,
        branchCode: String
    ): Article16WorkshopInfoDTO = call {
        apiService.getArticle16WorkshopInfo(workshopId, branchCode).extractData()
    }

    override suspend fun getArticle16RequestInfo(
        objectionNumber: Long
    ): Article16RequestInfoDTO = call {
        apiService.getArticle16RequestInfo(objectionNumber).extractData()
    }

    override suspend fun saveArticle16Request(
        request: Article16SaveRequestDTO
    ): Article16SaveResultDTO = call {
        apiService.saveArticle16Request(request).extractData()
    }

    override suspend fun getArticle16ReportPdf(seqNumber: Long): PdfDownloadDTO = call {
        PdfDownloadDTO(
            pdf = InputStreamDTO(pdf = apiService.getArticle16ReportPdf(seqNumber).readPdfChannel())
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
}
