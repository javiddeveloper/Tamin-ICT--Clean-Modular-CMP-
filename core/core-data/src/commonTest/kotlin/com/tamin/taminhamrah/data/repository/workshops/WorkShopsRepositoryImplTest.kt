package com.tamin.taminhamrah.data.repository.workshops

import com.tamin.taminhamrah.data.repository.WorkShopsRepositoryImpl
import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSource
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
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveRequestDTO
import com.tamin.taminhamrah.model.workshop.DebitObjectionSaveResultDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentPreCheckDTO
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDTO
import com.tamin.taminhamrah.model.workshop.EmployerAgreementSubmitRequestDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeContractDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeRequestDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopDTO
import com.tamin.taminhamrah.model.workshop.NewMemberConfirmResultDTO
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDTO
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDTO
import com.tamin.taminhamrah.model.workshop.SmsMessageDTO
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionDTO
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionQuery
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDTO
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Only what `getWorkShopObjections`/`getWorkShopObjectionSms` need — every other method is unused stub. */
private class FakeWorkShopsRemoteDataSource : WorkShopsRemoteDataSource {
    var lastObjectionsQuery: ApiQueryParamDN? = null
        private set
    var lastSmsSeqNo: Long? = null
        private set

    override suspend fun getWorkShopObjections(query: ApiQueryParamDN): ListData<WorkShopObjectionDTO> {
        lastObjectionsQuery = query
        return ListData(list = emptyList(), total = 0)
    }

    override suspend fun getWorkShopObjectionSms(
        objectionCode: Long,
        query: ApiQueryParamDN,
    ): ListData<SmsMessageDTO> {
        lastSmsSeqNo = objectionCode
        return ListData(list = emptyList(), total = 0)
    }

    override suspend fun getAllEmployerAgreementByNationalId(query: ApiQueryParamDN) =
        notImplemented()

    override suspend fun getWorkshopPaymentSheets(query: ApiQueryParamDN) = notImplemented()
    override suspend fun getDebitReasons(query: ApiQueryParamDN) = notImplemented()
    override suspend fun getWorkshopDebitList(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN
    ) = notImplemented()

    override suspend fun getWorkshopDemandDocuments(
        debitNumber: String,
        branchCode: String,
        query: ApiQueryParamDN
    ) = notImplemented()

    override suspend fun getDebitTurnoverPdf(
        debitNumber: String,
        branchCode: String
    ): PdfDownloadDTO = notImplemented()

    override suspend fun checkDebitPayment(
        debitNumber: String,
        branchCode: String
    ): DebitPaymentPreCheckDTO = notImplemented()

    override suspend fun payWorkshopDebit(request: DebitPaymentRequestDTO): DebitPaymentDTO =
        notImplemented()

    override suspend fun getWorkshopDebtInquiry(
        workshopId: String,
        branchCode: String
    ): WorkshopDebtInquiryDTO = notImplemented()

    override suspend fun getWorkshopObjectionableDebitList(
        workshopNumber: String,
        branchCode: String,
        query: ApiQueryParamDN
    ) = notImplemented()

    override suspend fun getObjectionElapsedDays(orderRecipeDate: String): Int = notImplemented()
    override suspend fun saveDebitObjection(request: DebitObjectionSaveRequestDTO): DebitObjectionSaveResultDTO =
        notImplemented()

    override suspend fun getDebitObjectionPdf(seqNumber: Long): PdfDownloadDTO = notImplemented()
    override suspend fun getWorkshopRecentlyAddedMembers(query: ApiQueryParamDN) = notImplemented()
    override suspend fun confirmRecentlyAddedMember(requestId: Long): NewMemberConfirmResultDTO =
        notImplemented()

    override suspend fun deleteRecentlyAddedMember(personalId: Long) = Unit
    override suspend fun checkNewMemberIsNew(nationalId: String): Boolean = notImplemented()
    override suspend fun createNewMemberRegistration(request: NewMemberRegistrationDTO): NewMemberRegistrationResultDTO =
        notImplemented()

    override suspend fun getWorkshopsDebtsList(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN
    ) = notImplemented()

    override suspend fun getArticleSixteenWorkshopInfo(
        workshopId: String,
        branchCode: String
    ): ArticleSixteenWorkshopInfoDTO = notImplemented()

    override suspend fun getArticleSixteenRequestInfo(objectionNumber: Long): ArticleSixteenRequestInfoDTO =
        notImplemented()

    override suspend fun saveArticleSixteenRequest(request: ArticleSixteenSaveRequestDTO): ArticleSixteenSaveResultDTO =
        notImplemented()

    override suspend fun getArticleSixteenReportPdf(seqNumber: Long): PdfDownloadDTO =
        notImplemented()

    override suspend fun getWorkshopMembers(query: ApiQueryParamDN) = notImplemented()
    override suspend fun getWorkshopStackHolders(query: ApiQueryParamDN) = notImplemented()
    override suspend fun getLegalRepresentativeWorkshops(): ListData<LegalRepresentativeWorkshopDTO>? =
        notImplemented()

    override suspend fun getLegalRepresentatives(
        workshopId: String,
        branchCode: String
    ): ListData<LegalRepresentativeDTO>? = notImplemented()

    override suspend fun getLegalRepresentativeWorkshopContracts(
        workshopId: String,
        branchCode: String
    ): ListData<LegalRepresentativeContractDTO>? = notImplemented()

    override suspend fun requestLegalRepresentativeTicket(nationalCode: String?) = notImplemented()
    override suspend fun verifyLegalRepresentativeTicket(ticket: String) = notImplemented()
    override suspend fun submitLegalRepresentative(
        ticket: String,
        request: LegalRepresentativeRequestDTO
    ) = notImplemented()

    override suspend fun deleteLegalRepresentative(ticket: String, stackId: Long) = notImplemented()
    override suspend fun getEmployerWorkshopsWithoutContract(query: ApiQueryParamDN) =
        notImplemented()

    override suspend fun getEmployerAgreementsByWorkshop(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN
    ) = notImplemented()

    override suspend fun getWorkshopContracts(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN
    ) = notImplemented()

    override suspend fun getEmployerWorkshopsWithoutContract(query: ApiQueryParamDN) = notImplemented()
    override suspend fun getEmployerWorkshopContractList(workshopId: String, branchCode: String, query: ApiQueryParamDN) = notImplemented()
    override suspend fun getEmployerAgreementsByWorkshop(workshopId: String, branchCode: String, query: ApiQueryParamDN) = notImplemented()
    override suspend fun getWorkshopContracts(workshopId: String, branchCode: String, query: ApiQueryParamDN) = notImplemented()
    override suspend fun getEmployerAgreementUserInfo(verificationCode: String) = notImplemented()
    override suspend fun requestEmployerAgreementTicket(mobileNumber: String, email: String) =
        notImplemented()

    override suspend fun confirmPaymentTicket(ticket: String) = notImplemented()
    override suspend fun getEmployerWorkshopContractList(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN
    ) = notImplemented()

    override suspend fun submitEmployerAgreement(request: EmployerAgreementSubmitRequestDTO) =
        notImplemented()

    private fun notImplemented(): Nothing =
        throw UnsupportedOperationException("not needed by this test")
    override suspend fun requestEmployerAgreementTicket(mobileNumber: String, email: String) = notImplemented()
    override suspend fun submitEmployerAgreement(request: EmployerAgreementSubmitRequestDTO) = notImplemented()
    override suspend fun confirmPaymentTicket(ticket: String) = notImplemented()
    private fun notImplemented(): Nothing = throw UnsupportedOperationException("not needed by this test")
}

class WorkShopsRepositoryImplTest {

    private val remote = FakeWorkShopsRemoteDataSource()
    private val repository = WorkShopsRepositoryImpl(remote)

    /**
     * The legacy client wrote "شماره اعتراض" into a filter key (`branchCode`) the backend's own
     * filter builder never read there — a silent no-op. This is the deliberate fix: the objection
     * number must filter on [FilterProperty.SEQ_NO], not any branch-code-shaped property, or the
     * search field regresses back to doing nothing against the real backend.
     */
    @Test
    fun getWorkShopObjections_filtersObjectionNumberOnSeqNo() = runTest {
        repository.getWorkShopObjections(WorkShopObjectionQuery(objectionNumber = "1403008720"))

        val filters = remote.lastObjectionsQuery?.filters.orEmpty()
        assertEquals(
            listOf(ApiFilterDN(FilterProperty.SEQ_NO, "1403008720", FilterOperator.EQ)),
            filters,
        )
    }

    @Test
    fun getWorkShopObjections_mapsWorkshopIdAndDebitNumberToTheReusedFilterProperties() = runTest {
        repository.getWorkShopObjections(
            WorkShopObjectionQuery(workshopId = "2361847", debitNumber = "140244190")
        )

        val filters = remote.lastObjectionsQuery?.filters.orEmpty()
        assertEquals(
            setOf(
                ApiFilterDN(FilterProperty.PAYMENT_WORKSHOP_ID, "2361847", FilterOperator.EQ),
                ApiFilterDN(FilterProperty.DEBIT_NUMBER, "140244190", FilterOperator.EQ),
            ),
            filters.toSet(),
        )
    }

    @Test
    fun getWorkShopObjections_blankFiltersAreOmittedEntirely() = runTest {
        repository.getWorkShopObjections(WorkShopObjectionQuery())

        assertEquals(emptyList(), remote.lastObjectionsQuery?.filters)
    }

    @Test
    fun getWorkShopObjectionSms_passesTheSeqNoThrough() = runTest {
        repository.getWorkShopObjectionSms(seqNo = 1403008720L, page = 0)

        assertEquals(1403008720L, remote.lastSmsSeqNo)
    }
}
