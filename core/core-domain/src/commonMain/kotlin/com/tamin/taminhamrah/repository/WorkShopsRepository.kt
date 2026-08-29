package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeRequestDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeWorkshopListDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebitListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtListDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberListDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberListDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderListDN
import kotlinx.coroutines.flow.Flow

interface WorkShopsRepository {
    suspend fun getAllEmployerAgreementByNationalId(
        filters: List<ApiFilterDN> = emptyList()
    ): EmployerAgreementListDN?

    suspend fun getPaymentSheets(
        filters: List<ApiFilterDN> = emptyList()
    ): PaymentSheetListDN?

    suspend fun getWorkshopDebit(
        workshopId: String,
        branchCode: String
    ): WorkshopDebitListDN?

    suspend fun getWorkshopDebtInquiry(
        workshopId: String,
        branchCode: String
    ): WorkshopDebtInquiryDN?

    fun getWorkshopObjectionableDebitList(
        workshopNumber: String,
        branchCode: String,
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<WorkShopDebtListDN?>

    fun getWorkshopRecentlyAddedMembers(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<WorkshopNewMemberListDN?>

    fun getWorkshopsDebtsList(
        workshopId: String,
        branchId: String,
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<WorkshopsDebtListDN?>

    fun getWorkshopMembers(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<WorkshopMemberListDN?>

    fun getWorkshopStackHolders(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<WorkshopStackHolderListDN?>

    /** Workshops (کارگاه‌های حقوقی) the current user has legal-representative rights on. */
    fun getLegalRepresentativeWorkshops(): Flow<LegalRepresentativeWorkshopListDN?>

    /** Representatives already registered for one workshop. */
    fun getLegalRepresentatives(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeListDN?>

    /** Requests a one-time verification code, optionally scoped to a specific national code. */
    suspend fun requestLegalRepresentativeTicket(nationalCode: String? = null)

    /** Verifies a one-time code. The code itself becomes the "ticket" used by later calls. */
    suspend fun verifyLegalRepresentativeTicket(ticket: String)

    suspend fun submitLegalRepresentative(ticket: String, request: LegalRepresentativeRequestDN)

    suspend fun deleteLegalRepresentative(ticket: String, stakeId: Long)
}
