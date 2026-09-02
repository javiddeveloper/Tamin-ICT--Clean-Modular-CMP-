package com.tamin.taminhamrah.feature.workshops.ui

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.CityListResultDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeContractListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeRequestDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeWorkshopListDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtQuery
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestInfoDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveRequestDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenSaveResultDN
import com.tamin.taminhamrah.model.workshop.ArticleSixteenWorkshopInfoDN
import com.tamin.taminhamrah.model.workshop.DebitObjectionRequestDN
import com.tamin.taminhamrah.model.workshop.DebitObjectionResultDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentPreCheckDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDN
import com.tamin.taminhamrah.model.workshop.DebitReasonDN
import com.tamin.taminhamrah.model.workshop.ContractRowQuery
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetQuery
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDN
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberQuery
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberQuery
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderQuery
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.WorkShopsRepository
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 * Fakes for the cascade tests. Only the members the cascade actually touches return data;
 * the rest fail loudly rather than returning empty, so a future call routed through one of
 * them shows up as a broken test instead of a silently empty picker.
 */

internal class FakeCascadeCityProvinceRepository : CityProvinceRepository {
    var lastRequestedProvinceCode: String? = null

    private val allCities = listOf(
        CityDN(cityCode = "0701", provinceCode = "07", cityName = "تهران"),
        CityDN(cityCode = "0401", provinceCode = "04", cityName = "اصفهان"),
    )

    override fun getProvinces(): Flow<List<ProvinceDN>> = flowOf(
        listOf(
            ProvinceDN("07", "تهران", null, null),
            ProvinceDN("04", "اصفهان", null, null),
        ),
    )

    override fun getCities(cityName: String?, provinceCode: String?): Flow<List<CityDN>> = flow {
        lastRequestedProvinceCode = provinceCode
        emit(allCities.filter { provinceCode == null || it.provinceCode == provinceCode })
    }

    override fun getCity(cityId: String): Flow<CityDN> = flowOf()
    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flowOf()
    override fun getCitiesByProvince(provinceCode: String): Flow<CityListResultDN> = unused()
}

internal class FakeCascadeContractsRepository : ContractsRepository {
    var lastRequestedCityCode: String? = null

    override fun getBranches(cityCode: String): Flow<List<BranchDN>> = flow {
        lastRequestedCityCode = cityCode
        emit(
            listOf(
                BranchDN(
                    code = "123",
                    name = "شعبه ۱ تهران",
                    branchAddress = "خیابان ولیعصر",
                    cityCode = cityCode,
                    minCode = null,
                    maxCode = null,
                ),
            ),
        )
    }

    override fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>> = unused()
    override fun getContractsByPremiumType(premiumTypeCode: String): Flow<List<ContractDN>> = unused()
    override fun getStudentInsuranceContracts(): Flow<List<ContractDN>> = unused()
    override fun getRegistrationInfo(): Flow<RegistrationInfoDN> = unused()
    override fun getSpcPremiumRates(): Flow<List<PremiumRateDN>> = unused()
    override fun getFreeJobWages(): Flow<List<FreeJobDN>> = unused()
    override fun getFreelancePremiumRange(
        params: FreelancePremiumRangeParams
    ): Flow<FreelancePremiumRangeDN> = unused()
    override fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Flow<Long> = unused()
    override fun calculateOptionalSalary(premiumRateCode: String): Flow<Long> = unused()
    override fun makeFreelanceContract(
        params: FreelanceMakeContractParams
    ): Flow<FreelanceContractResultDN> = unused()
    override fun makeContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = unused()
    override fun makeFreelanceContractByGuardian(
        params: FreelanceContractByGuardianParams
    ): Flow<FreelanceContractResultDN> = unused()
    override fun makeOptionalContractByGuardian(
        params: OptionalContractByGuardianParams
    ): Flow<FreelanceContractResultDN> = unused()
    override fun getInsurancePayment(params: InsurancePaymentParamsDN): Flow<InsurancePaymentDN> = unused()
    override fun checkInsurancePaymentStatus(systemType: String): Flow<Any?> = unused()
    override fun uploadImage(request: UploadImageRequestDN): Flow<String> = unused()
    override fun saveContact(request: SaveContactRequestDN): Flow<Any?> = unused()
}

internal class FakeCascadeWorkShopsRepository : WorkShopsRepository {
    override suspend fun getEmployerAgreements(query: WorkshopListQuery): PagedListDN<EmployerAgreementDN> = unusedValue()
    override suspend fun getContractRowsWithAgreement(
        query: ContractRowQuery
    ): PagedListDN<EmployerAgreementDN> = unusedValue()
    override suspend fun getContractRowsWithoutAgreement(
        query: ContractRowQuery
    ): PagedListDN<WorkshopContractDN> = unusedValue()
    override suspend fun getPaymentSheets(query: PaymentSheetQuery): PagedListDN<PaymentSheetDN> = unusedValue()
    override suspend fun getDebitReasons(page: Int): PagedListDN<DebitReasonDN> = unusedValue()
    override suspend fun getWorkshopDebits(
        workshopId: String,
        branchCode: String,
        page: Int
    ): PagedListDN<WorkShopDebtDN> = unusedValue()
    override suspend fun getDemandDocuments(
        debitNumber: String,
        branchCode: String,
        page: Int
    ): PagedListDN<WorkshopDemandDocDN> = unusedValue()
    override suspend fun getDebitTurnoverPdf(debitNumber: String, branchCode: String): PdfDownloadDN = unusedValue()
    override suspend fun checkDebitPayment(debitNumber: String, branchCode: String): DebitPaymentPreCheckDN = unusedValue()
    override suspend fun payWorkshopDebit(request: DebitPaymentRequestDN): DebitPaymentDN = unusedValue()
    override suspend fun getWorkshopDebtInquiry(
        workshopId: String,
        branchCode: String
    ): WorkshopDebtInquiryDN = unusedValue()
    override suspend fun getObjectionableDebits(
        workshopId: String,
        branchCode: String,
        page: Int
    ): PagedListDN<WorkShopDebtDN> = unusedValue()
    override suspend fun getObjectionElapsedDays(orderRecipeDate: String): Int = unusedValue()
    override suspend fun saveDebitObjection(request: DebitObjectionRequestDN): DebitObjectionResultDN = unusedValue()
    override suspend fun getDebitObjectionPdf(seqNo: Long): PdfDownloadDN = unusedValue()
    override suspend fun getRecentlyAddedMembers(
        query: WorkshopNewMemberQuery
    ): PagedListDN<WorkshopNewMemberDN> = unusedValue()
    override suspend fun confirmRecentlyAddedMember(requestId: Long): String = unusedValue()
    override suspend fun deleteRecentlyAddedMember(personalId: Long): Unit = unusedValue()
    override suspend fun checkNewMemberIsNew(nationalId: String): Boolean = unusedValue()
    override suspend fun createNewMemberRegistration(
        request: NewMemberRegistrationDN
    ): NewMemberRegistrationResultDN = unusedValue()
    override suspend fun getArticleSixteenDebts(
        query: ArticleSixteenDebtQuery
    ): PagedListDN<WorkshopsDebtListModelDN> = unusedValue()
    override suspend fun getArticleSixteenWorkshopInfo(
        workshopId: String,
        branchCode: String
    ): ArticleSixteenWorkshopInfoDN = unusedValue()
    override suspend fun getArticleSixteenRequestInfo(objectionNumber: Long): ArticleSixteenRequestInfoDN = unusedValue()
    override suspend fun saveArticleSixteenRequest(request: ArticleSixteenSaveRequestDN): ArticleSixteenSaveResultDN = unusedValue()
    override suspend fun getArticleSixteenReportPdf(seqNo: Long): PdfDownloadDN = unusedValue()
    override suspend fun getWorkshopMembers(query: WorkshopMemberQuery): PagedListDN<WorkshopMemberDN> = unusedValue()
    override suspend fun getWorkshopStackHolders(
        query: WorkshopStackHolderQuery
    ): PagedListDN<WorkshopStackHolderDN> = unusedValue()
    override fun getLegalRepresentativeWorkshops(): Flow<LegalRepresentativeWorkshopListDN?> = unused()
    override fun getLegalRepresentatives(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeListDN?> = unused()
    override fun getLegalRepresentativeWorkshopContracts(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeContractListDN?> = unused()
    override suspend fun requestLegalRepresentativeTicket(nationalCode: String?): Unit = unusedValue()
    override suspend fun verifyLegalRepresentativeTicket(ticket: String): Unit = unusedValue()
    override suspend fun submitLegalRepresentative(ticket: String, request: LegalRepresentativeRequestDN): Unit = unusedValue()
    override suspend fun deleteLegalRepresentative(ticket: String, stakeId: Long): Unit = unusedValue()
}

private fun <T> unused(): Flow<T> =
    flow { error("not part of the cascade under test") }

private fun <T> unusedValue(): T =
    error("not part of the cascade under test")
