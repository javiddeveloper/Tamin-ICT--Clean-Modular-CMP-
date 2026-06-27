package com.tamin.taminhamrah.repository.workshops

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebitListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

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

    override suspend fun getWorkshopObjectionableDebitList(
        workshopNumber: String,
        branchCode: String,
        filters: List<ApiFilterDN>
    ): com.tamin.taminhamrah.model.workshop.WorkShopDebtListDN? = null

    override suspend fun getWorkshopRecentlyAddedMembers(
        filters: List<ApiFilterDN>
    ): com.tamin.taminhamrah.model.workshop.WorkshopNewMemberListDN? = null

    override suspend fun getWorkshopsDebtsList(
        workshopId: String,
        branchId: String,
        filters: List<ApiFilterDN>
    ): com.tamin.taminhamrah.model.workshop.WorkshopsDebtListDN? = null

    override suspend fun getWorkshopMembers(
        filters: List<ApiFilterDN>
    ): com.tamin.taminhamrah.model.workshop.WorkshopMemberListDN? = null

    override suspend fun getWorkshopStackHolders(
        filters: List<ApiFilterDN>
    ): com.tamin.taminhamrah.model.workshop.WorkshopStackHolderListDN? = null
}
