package com.tamin.taminhamrah.repository.workshops

import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeRequestDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeWorkshopListDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetListDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebitListDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberListDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberListDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderListDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWorkShopsRepository : WorkShopsRepository {
    var result: EmployerAgreementListDN? = null
    var shouldThrowError: Boolean = false
    var error: Exception = RuntimeException("Fake Network Error")

    var paymentSheetsResult: PaymentSheetListDN? = null
    var workshopDebitResult: WorkshopDebitListDN? = null

    override suspend fun getAllEmployerAgreementByNationalId(filters: List<ApiFilterDN>): EmployerAgreementListDN? {
        if (shouldThrowError) {
            throw error
        }
        return result
    }

    override suspend fun getPaymentSheets(filters: List<ApiFilterDN>): com.tamin.taminhamrah.model.workshop.PaymentSheetListDN? {
        if (shouldThrowError) {
            throw error
        }
        return paymentSheetsResult
    }

    override suspend fun getWorkshopDebit(
        workshopId: String,
        branchCode: String
    ): WorkshopDebitListDN? {
        if (shouldThrowError) {
            throw error
        }
        return workshopDebitResult
    }

    var workshopDebtInquiryResult: com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN? = null

    override suspend fun getWorkshopDebtInquiry(
        workshopId: String,
        branchCode: String
    ): com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN? {
        if (shouldThrowError) {
            throw error
        }
        return workshopDebtInquiryResult
    }

    var workshopObjectionableDebitListResult: WorkShopDebtListDN? = null
    override fun getWorkshopObjectionableDebitList(
        workshopNumber: String,
        branchCode: String,
        filters: List<ApiFilterDN>
    ): Flow<WorkShopDebtListDN?> = flow {
        if (shouldThrowError) throw error
        emit(workshopObjectionableDebitListResult)
    }

    var workshopRecentlyAddedMembersResult: WorkshopNewMemberListDN? = null
    override fun getWorkshopRecentlyAddedMembers(
        filters: List<ApiFilterDN>
    ): Flow<WorkshopNewMemberListDN?> = flow {
        if (shouldThrowError) throw error
        emit(workshopRecentlyAddedMembersResult)
    }

    var workshopsDebtsListResult: WorkshopsDebtListDN? = null
    override fun getWorkshopsDebtsList(
        workshopId: String,
        branchId: String,
        filters: List<ApiFilterDN>
    ): Flow<WorkshopsDebtListDN?> = flow {
        if (shouldThrowError) throw error
        emit(workshopsDebtsListResult)
    }

    var workshopMembersResult: WorkshopMemberListDN? = null
    override fun getWorkshopMembers(
        filters: List<ApiFilterDN>
    ): Flow<WorkshopMemberListDN?> = flow {
        if (shouldThrowError) throw error
        emit(workshopMembersResult)
    }

    var workshopStackHoldersResult: WorkshopStackHolderListDN? = null
    override fun getWorkshopStackHolders(
        filters: List<ApiFilterDN>
    ): Flow<WorkshopStackHolderListDN?> = flow {
        if (shouldThrowError) throw error
        emit(workshopStackHoldersResult)
    }

    var legalRepresentativeWorkshopsResult: LegalRepresentativeWorkshopListDN? = null
    override fun getLegalRepresentativeWorkshops(): Flow<LegalRepresentativeWorkshopListDN?> = flow {
        if (shouldThrowError) throw error
        emit(legalRepresentativeWorkshopsResult)
    }

    var legalRepresentativesResult: LegalRepresentativeListDN? = null
    override fun getLegalRepresentatives(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeListDN?> = flow {
        if (shouldThrowError) throw error
        emit(legalRepresentativesResult)
    }

    override suspend fun requestLegalRepresentativeTicket(nationalCode: String?) {
        if (shouldThrowError) throw error
    }

    override suspend fun verifyLegalRepresentativeTicket(ticket: String) {
        if (shouldThrowError) throw error
    }

    override suspend fun submitLegalRepresentative(ticket: String, request: LegalRepresentativeRequestDN) {
        if (shouldThrowError) throw error
    }

    override suspend fun deleteLegalRepresentative(ticket: String, stakeId: Long) {
        if (shouldThrowError) throw error
    }
}
