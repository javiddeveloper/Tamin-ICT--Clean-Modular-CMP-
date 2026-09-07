package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toDomainPage
import com.tamin.taminhamrah.data.mapper.toDto
import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSource
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeContractListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeRequestDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeWorkshopListDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtQuery
import com.tamin.taminhamrah.model.workshop.AssignerContractDN
import com.tamin.taminhamrah.model.workshop.AssignerContractQuery
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDN
import com.tamin.taminhamrah.model.workshop.ComputationalBaseQuery
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestInfoDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveRequestDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveResultDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoDN
import com.tamin.taminhamrah.model.workshop.DebitObjectionRequestDN
import com.tamin.taminhamrah.model.workshop.DebitObjectionResultDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentPreCheckDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDN
import com.tamin.taminhamrah.model.workshop.ContractRowQuery
import com.tamin.taminhamrah.model.workshop.DebitReasonDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementByWorkshopDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementSubmissionDN
import com.tamin.taminhamrah.model.workshop.EmployerContactInfoDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetQuery
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDN
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberQuery
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberQuery
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderQuery
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toDto
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Turns the feature's typed queries into the ExtJS-style `filter` array every workshop service
 * takes, and the wire rows back into domain models.
 *
 * The filter *property names* differ per endpoint for the same concept (`workshop.workshopId` on
 * the agreement list, a bare `workshopId` on payment sheets, `workshopId.workshopId` on
 * stakeholders, `organizationId` for the branch on the registration list). That mapping is this
 * class's job — nothing above it should know the service spells one idea four ways.
 */
class WorkShopsRepositoryImpl(
    private val remoteDataSource: WorkShopsRemoteDataSource,
) : WorkShopsRepository {

    // ------------------------------------------------------------ کارگاه‌های کارفرما

    override suspend fun getEmployerAgreements(
        query: WorkshopListQuery
    ): PagedListDN<EmployerAgreementDN> {
        val filters = buildFilters {
            add(FilterProperty.WORKSHOP_ID, query.workshopId)
            add(FilterProperty.WORKSHOP_BRANCH_CODE, query.branchCode)
            add(FilterProperty.WORKSHOP_STATUS_CODE, query.status?.code)
        }
        return remoteDataSource
            .getAllEmployerAgreementByNationalId(pageQuery(query.page, query.pageSize, filters))
            .toDomainPage { it.toDomain() }
    }

    // ------------------------------------------------------------------ ردیف‌های پیمان

    /*
     * Both contract-row calls take the workshop and branch as path segments, so neither builds a
     * filter — the query carries page and size only.
     */

    override suspend fun getContractRowsWithAgreement(
        query: ContractRowQuery,
    ): PagedListDN<EmployerAgreementDN> =
        remoteDataSource.getEmployerAgreementsByWorkshop(
            workshopId = query.workshopId,
            branchCode = query.branchCode,
            query = pageQuery(query.page, query.pageSize),
        ).toDomainPage { it.toDomain() }

    override suspend fun getContractRowsWithoutAgreement(
        query: ContractRowQuery,
    ): PagedListDN<WorkshopContractDN> =
        remoteDataSource.getWorkshopContracts(
            workshopId = query.workshopId,
            branchCode = query.branchCode,
            query = pageQuery(query.page, query.pageSize),
        ).toDomainPage { it.toDomain() }

    // ---------------------------------------------------------------------------- واگذارندگان

    /**
     * The one workshop list whose identity travels as a *filter*, not as path segments.
     *
     * The old client builds exactly these three clauses — `workshop.workshopId`,
     * `workshop.branchCode`, `contractRow` — and omits any that is blank. `FilterBuilder` already
     * drops blanks, so an optional کد شعبه simply widens the search here instead of addressing a
     * route that does not exist, which is what the same blank does on ردیف‌های پیمان.
     */
    override suspend fun getAssignerContracts(
        query: AssignerContractQuery,
    ): PagedListDN<AssignerContractDN> {
        val filters = buildFilters {
            add(FilterProperty.WORKSHOP_ID, query.workshopId)
            add(FilterProperty.WORKSHOP_BRANCH_CODE, query.branchCode)
            add(FilterProperty.CONTRACT_ROW, query.contractRow)
        }
        return remoteDataSource
            .getAssignerContracts(pageQuery(query.page, query.pageSize, filters))
            .toDomainPage { it.toDomain() }
    }

    /**
     * The four identity keys go as plain query parameters, not as a filter array — the only
     * workshop call that mixes the two styles. `brchCode` is the service's own abbreviation.
     */
    override suspend fun getComputationalBases(
        query: ComputationalBaseQuery,
    ): PagedListDN<ComputationalBaseDN> =
        remoteDataSource.getComputationalBases(
            workshopId = query.workshopId,
            contractRow = query.contractRow,
            brchCode = query.branchCode,
            contractSequence = query.contractSequence,
            query = pageQuery(query.page, query.pageSize),
        ).toDomainPage { it.toDomain() }

    override suspend fun getComputationalBasePdf(documentId: String): PdfDownloadDN =
        remoteDataSource.getComputationalBasePdf(documentId).toDomain()

    // -------------------------------------------------------------------- برگ پرداخت‌ها

    override suspend fun getPaymentSheets(query: PaymentSheetQuery): PagedListDN<PaymentSheetDN> {
        val filters = buildFilters {
            add(FilterProperty.PAYMENT_WORKSHOP_ID, query.workshopId)
            add(FilterProperty.PAYMENT_BRANCH_CODE, query.branchCode)
            add(FilterProperty.PAY_ID_FROM, query.payIdFrom)
            add(FilterProperty.PAY_ID_TO, query.payIdTo)
            // A date the user never picked is left out entirely. The old client sent "0" for an
            // untouched picker, which the service reads as a real bound.
            add(FilterProperty.DOC_DATE_FROM, query.docDateFrom?.toString())
            add(FilterProperty.DOC_DATE_TO, query.docDateTo?.toString())
            // Codes, not the labels the picker displays — the old client sent the display text.
            add(FilterProperty.DEBIT_REASON, query.debitReasonCode)
            add(FilterProperty.PAYMENT_SHEET_STATUS, query.status?.code)
        }
        return remoteDataSource
            .getWorkshopPaymentSheets(pageQuery(query.page, query.pageSize, filters))
            .toDomainPage { it.toDomain() }
    }

    override suspend fun getDebitReasons(page: Int): PagedListDN<DebitReasonDN> =
        remoteDataSource.getDebitReasons(pageQuery(page))
            .toDomainPage { it.toDomain() }

    // --------------------------------------------------- گردش حساب بدهی + پرداخت

    override suspend fun getWorkshopDebits(
        workshopId: String,
        branchCode: String,
        page: Int,
    ): PagedListDN<WorkShopDebtDN> =
        remoteDataSource.getWorkshopDebitList(workshopId, branchCode, pageQuery(page))
            .toDomainPage { it.toDomain() }

    override suspend fun getDemandDocuments(
        debitNumber: String,
        branchCode: String,
        page: Int,
    ): PagedListDN<WorkshopDemandDocDN> =
        remoteDataSource.getWorkshopDemandDocuments(debitNumber, branchCode, pageQuery(page))
            .toDomainPage { it.toDomain() }

    override suspend fun getDebitTurnoverPdf(debitNumber: String, branchCode: String): PdfDownloadDN =
        remoteDataSource.getDebitTurnoverPdf(debitNumber, branchCode).toDomain()

    override suspend fun checkDebitPayment(
        debitNumber: String,
        branchCode: String,
    ): DebitPaymentPreCheckDN =
        remoteDataSource.checkDebitPayment(debitNumber, branchCode).toDomain()

    override suspend fun payWorkshopDebit(request: DebitPaymentRequestDN): DebitPaymentDN =
        remoteDataSource.payWorkshopDebit(request.toDto()).toDomain()

    // ------------------------------------------------------------ استعلام بدهی کارگاه

    override suspend fun getWorkshopDebtInquiry(
        workshopId: String,
        branchCode: String,
    ): WorkshopDebtInquiryDN =
        remoteDataSource.getWorkshopDebtInquiry(workshopId, branchCode).toDomain()

    // -------------------------------------------------------------------- اعتراض به بدهی

    override suspend fun getObjectionableDebits(
        workshopId: String,
        branchCode: String,
        page: Int,
    ): PagedListDN<WorkShopDebtDN> =
        remoteDataSource.getWorkshopObjectionableDebitList(workshopId, branchCode, pageQuery(page))
            .toDomainPage { it.toDomain() }

    override suspend fun getObjectionElapsedDays(orderRecipeDate: String): Int =
        remoteDataSource.getObjectionElapsedDays(orderRecipeDate)

    override suspend fun saveDebitObjection(
        request: DebitObjectionRequestDN,
    ): DebitObjectionResultDN = remoteDataSource.saveDebitObjection(request.toDto()).toDomain()

    override suspend fun getDebitObjectionPdf(seqNo: Long): PdfDownloadDN =
        remoteDataSource.getDebitObjectionPdf(seqNo).toDomain()

    // --------------------------------------------- نام نویسی غیر حضوری بیمه شده

    override suspend fun getRecentlyAddedMembers(
        query: WorkshopNewMemberQuery,
    ): PagedListDN<WorkshopNewMemberDN> {
        val filters = buildFilters {
            add(FilterProperty.PAYMENT_WORKSHOP_ID, query.workshopId)
            add(FilterProperty.ORGANIZATION_ID, query.branchCode)
            add(FilterProperty.PERSONAL_NATIONAL_ID, query.nationalId)
            add(FilterProperty.PERSONAL_REQUEST_STATUS_CODE, query.requestStatus?.code)
        }
        return remoteDataSource
            .getWorkshopRecentlyAddedMembers(pageQuery(query.page, query.pageSize, filters))
            .toDomainPage { it.toDomain() }
    }

    override suspend fun confirmRecentlyAddedMember(requestId: Long): String =
        remoteDataSource.confirmRecentlyAddedMember(requestId).refCode.orEmpty()

    override suspend fun deleteRecentlyAddedMember(personalId: Long) =
        remoteDataSource.deleteRecentlyAddedMember(personalId)

    override suspend fun checkNewMemberIsNew(nationalId: String): Boolean =
        remoteDataSource.checkNewMemberIsNew(nationalId)

    override suspend fun createNewMemberRegistration(
        request: NewMemberRegistrationDN,
    ): NewMemberRegistrationResultDN =
        remoteDataSource.createNewMemberRegistration(request.toDto()).toDomain()

    // ------------------------------------------------------------------------- ماده ۱۶

    override suspend fun getArticleSixteenDebts(
        query: ArticleSixteenDebtQuery,
    ): PagedListDN<WorkshopsDebtListModelDN> {
        val filters = buildFilters {
            add(FilterProperty.DEBIT_NUMBER, query.debitNumber)
            add(FilterProperty.PEYMAN_SEQUENCE, query.agreementRow)
        }
        return remoteDataSource
            .getWorkshopsDebtsList(
                query.workshopId,
                query.branchCode,
                pageQuery(query.page, query.pageSize, filters),
            )
            .toDomainPage { it.toDomain() }
    }

    override suspend fun getArticleSixteenWorkshopInfo(
        workshopId: String,
        branchCode: String,
    ): ArticleSixteenWorkshopInfoDN =
        remoteDataSource.getArticleSixteenWorkshopInfo(workshopId, branchCode).toDomain()

    override suspend fun getArticleSixteenRequestInfo(objectionNumber: Long): ArticleSixteenRequestInfoDN =
        remoteDataSource.getArticleSixteenRequestInfo(objectionNumber).toDomain()

    override suspend fun saveArticleSixteenRequest(
        request: ArticleSixteenSaveRequestDN,
    ): ArticleSixteenSaveResultDN = remoteDataSource.saveArticleSixteenRequest(request.toDto()).toDomain()

    override suspend fun getArticleSixteenReportPdf(seqNo: Long): PdfDownloadDN =
        remoteDataSource.getArticleSixteenReportPdf(seqNo).toDomain()

    // ------------------------------------------------------------- کارکنان / ذینفعان

    override suspend fun getWorkshopMembers(
        query: WorkshopMemberQuery,
    ): PagedListDN<WorkshopMemberDN> {
        val filters = buildFilters {
            add(FilterProperty.WORKSHOP_ID, query.workshopId)
            add(FilterProperty.WORKSHOP_BRANCH_CODE, query.branchCode)
            add(FilterProperty.INSURANCE_ID, query.insuranceNumber)
            add(FilterProperty.INSURANCE_NATIONAL_ID, query.nationalId)
        }
        return remoteDataSource.getWorkshopMembers(pageQuery(query.page, query.pageSize, filters))
            .toDomainPage { it.toDomain() }
    }

    override suspend fun getWorkshopStackHolders(
        query: WorkshopStackHolderQuery,
    ): PagedListDN<WorkshopStackHolderDN> {
        val filters = buildFilters {
            add(FilterProperty.WORKSHOPID_ID, query.workshopId)
            add(FilterProperty.WORKSHOPID_BRANCH_CODE, query.branchCode)
            // The old client crossed these two over, so a stakeholder search filtered on the wrong
            // column. Each value goes to the property that names it.
            add(FilterProperty.INSURANCE_ID, query.insuranceNumber)
            add(FilterProperty.INSURANCE_NATIONAL_ID, query.nationalId)
        }
        return remoteDataSource
            .getWorkshopStackHolders(pageQuery(query.page, query.pageSize, filters))
            .toDomainPage { it.toDomain() }
    }

    // ------------------------------------------------- خدمات غیرحضوری کارفرما (employerEservicesAgreement)

    override suspend fun requestEmployerAgreementTicket(mobile: String, email: String): String =
        remoteDataSource.requestEmployerAgreementTicket(mobileNumber = mobile, email = email)

    override suspend fun getEmployerAgreementContactInfo(
        verificationCode: String,
    ): EmployerContactInfoDN =
        remoteDataSource.getEmployerAgreementUserInfo(verificationCode).toDomain()

    override suspend fun getWorkshopsWithoutContract(
        page: Int,
    ): PagedListDN<WorkshopWithoutContractDN> =
        remoteDataSource.getEmployerWorkshopsWithoutContract(pageQuery(page))
            .toDomainPage { it.toDomain() }

    override suspend fun getWorkshopContractRows(
        workshopId: String,
        branchCode: String,
        page: Int,
    ): PagedListDN<WorkshopContractRowDN> =
        remoteDataSource
            .getEmployerWorkshopContractList(workshopId, branchCode, pageQuery(page))
            .toDomainPage { it.toDomain() }

    override suspend fun submitEmployerAgreement(request: EmployerAgreementSubmissionDN): String =
        remoteDataSource.submitEmployerAgreement(request.toDto())

    private fun pageQuery(
        page: Int,
        pageSize: Int = WORKSHOP_PAGE_SIZE,
        filters: List<ApiFilterDN> = emptyList(),
    ) = ApiQueryParamDN(
        page = page,
        start = page * pageSize,
        limit = pageSize,
        filters = filters,
    )

    private inline fun buildFilters(block: FilterBuilder.() -> Unit): List<ApiFilterDN> =
        FilterBuilder().apply(block).filters

    /** Collects only the filters that were actually given a value — blanks are never sent. */
    private class FilterBuilder {
        val filters = mutableListOf<ApiFilterDN>()

        fun add(property: FilterProperty, value: String?) {
            if (!value.isNullOrBlank()) {
                filters += ApiFilterDN(property, value, FilterOperator.EQ)
            }
        }
    }

    override fun getLegalRepresentativeWorkshops(): Flow<LegalRepresentativeWorkshopListDN?> = flow {
        val response = remoteDataSource.getLegalRepresentativeWorkshops()
        emit(
            response?.let {
                LegalRepresentativeWorkshopListDN(
                    list = it.list?.map { item -> item.toDomain() } ?: emptyList(),
                    total = it.total ?: 0
                )
            }
        )
    }

    override fun getLegalRepresentatives(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeListDN?> = flow {
        val response = remoteDataSource.getLegalRepresentatives(workshopId, branchCode)
        emit(
            response?.let {
                LegalRepresentativeListDN(
                    list = it.list?.map { item -> item.toDomain() } ?: emptyList(),
                    total = it.total ?: 0
                )
            }
        )
    }

    override fun getLegalRepresentativeWorkshopContracts(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeContractListDN?> = flow {
        val response = remoteDataSource.getLegalRepresentativeWorkshopContracts(workshopId, branchCode)
        emit(
            response?.let {
                LegalRepresentativeContractListDN(
                    list = it.list?.map { item -> item.toDomain() } ?: emptyList(),
                    total = it.total ?: 0
                )
            }
        )
    }

    override suspend fun requestLegalRepresentativeTicket(nationalCode: String?) {
        remoteDataSource.requestLegalRepresentativeTicket(nationalCode)
    }

    override suspend fun verifyLegalRepresentativeTicket(ticket: String) {
        remoteDataSource.verifyLegalRepresentativeTicket(ticket)
    }

    override suspend fun submitLegalRepresentative(ticket: String, request: LegalRepresentativeRequestDN) {
        remoteDataSource.submitLegalRepresentative(ticket, request.toDto(ticket))
    }

    override suspend fun deleteLegalRepresentative(ticket: String, stakeId: Long) {
        remoteDataSource.deleteLegalRepresentative(ticket, stakeId)
    }
}
