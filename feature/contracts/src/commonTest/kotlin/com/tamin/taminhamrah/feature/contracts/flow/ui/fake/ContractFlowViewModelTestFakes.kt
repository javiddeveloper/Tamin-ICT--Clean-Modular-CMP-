package com.tamin.taminhamrah.feature.contracts.flow.ui.fake

import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.ContractStatusCode
import com.tamin.taminhamrah.model.contracts.ContractStatusObjectDN
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.RegistrationContactDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.RegistrationPersonalInfoDN
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.CurrentUserDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

internal object ContractFlowViewModelTestData {
    /** 1980-01-01 UTC — old enough that the under-18 preflight does not fire. */
    const val ADULT_BIRTH_EPOCH = 315_532_800_000L

    val registrationInfo = RegistrationInfoDN(
        personalInfo = RegistrationPersonalInfoDN(
            firstName = "رضا",
            lastName = "تست",
            nationalId = "4060434061",
            dateOfBirth = ADULT_BIRTH_EPOCH,
            genderCode = "01",
            genderDesc = "مرد",
            ssn = "4060434061",
        ),
        insuranceIdValidity = true,
        mobileNumber = "09143018372",
        insuranceId = "0081631829",
        lastContact = RegistrationContactDN(
            address = "تهران",
            zipCode = "1414657771",
            mobile = "09143018372",
            phoneNumber = "02188974532",
        ),
    )

    fun contract(statusCode: Int): ContractDN = ContractDN(
        adultLetterDate = null,
        adultLetterNumber = null,
        age = null,
        branchCode = null,
        brchCodeNew = null,
        cancelDate = null,
        cancelUID = null,
        canceldesc = null,
        cityCode = null,
        cntDrmn = null,
        cntFreeJobCode = null,
        cntIncPayDate3t4 = null,
        cntMedicalFlag = null,
        comment = null,
        commissionStatus = null,
        confirmDate = null,
        confirmUID = null,
        contractDate = null,
        contractNumber = 1,
        contractStatus = null,
        contractStatusObject = ContractStatusObjectDN(
            selfIsuContStatDesc = null,
            selfIsuContStatCode = statusCode,
        ),
        creatDate = null,
        createDate = null,
        createUID = null,
        eligibilityStatus = "1",
        freeJob = null,
        guid = null,
        guidName = null,
        history = null,
        insuranceId = null,
        isStudent = null,
        medicalExemptionStatus = null,
        militaryServiceLicense = null,
        mobileNumber = null,
        natinoalCode = null,
        physicalStatus = null,
        premiumRate = null,
        premiumRateCode = null,
        premiumType = null,
        premiumTypeCode = null,
        provinceCode = null,
        provinceName = null,
        refCode = null,
        salary = null,
        startDate = null,
        statusDate = null,
        wage = null,
    )

    /** Eligible, and not a preflight block (cancelled is status 2). */
    fun eligibleContract(): ContractDN = contract(ContractStatusCode.CANCELLED)

    fun activeContract(): ContractDN = contract(ContractStatusCode.ACTIVE)
}

/**
 * Cache emission, then a pause, then a second emission that stands in for the network refresh.
 * [holdRefresh] keeps [finally] from running until [releaseTypedRefresh] / [releaseAllRefresh].
 */
internal class FakeContractFlowRepository : ContractsRepository {
    var holdRefresh: Boolean = false
    var failAllContracts: Boolean = false
    val releaseTypedRefresh = CompletableDeferred<Unit>()
    val releaseAllRefresh = CompletableDeferred<Unit>()
    var typedContracts: List<ContractDN> = listOf(ContractFlowViewModelTestData.eligibleContract())
    var allContracts: List<ContractDN> = emptyList()
    var registrationInfo: RegistrationInfoDN = ContractFlowViewModelTestData.registrationInfo

    override fun getContracts(page: Int): Flow<PagedListDN<ContractDN>> = flow {
        if (failAllContracts) error("network")
        emit(pageOf(allContracts))
        if (holdRefresh) releaseAllRefresh.await()
        emit(pageOf(allContracts))
    }

    override fun getContractsByPremiumType(
        premiumTypeCode: String,
        page: Int,
    ): Flow<PagedListDN<ContractDN>> = flow {
        emit(pageOf(typedContracts))
        if (holdRefresh) releaseTypedRefresh.await()
        emit(pageOf(typedContracts))
    }

    override fun getRegistrationInfo(): Flow<RegistrationInfoDN> = flowOf(registrationInfo)

    override fun getStudentInsuranceContracts(page: Int): Flow<PagedListDN<ContractDN>> = flowOf(PagedListDN())
    override fun getBranches(cityCode: String, page: Int): Flow<PagedListDN<BranchDN>> = flowOf(PagedListDN())
    override fun getSpcPremiumRates(): Flow<List<PremiumRateDN>> = flowOf(emptyList())
    override fun getFreeJobWages(page: Int, searchQuery: String?): Flow<PagedListDN<FreeJobDN>> =
        flowOf(PagedListDN())
    override fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN> =
        flowOf(FreelancePremiumRangeDN(paymentTabayi = 0L, lowPremium = 0L, history = 0, highPremium = 0L))
    override fun getOptionalPremiumRange(): Flow<FreelancePremiumRangeDN> =
        flowOf(FreelancePremiumRangeDN(paymentTabayi = 0L, lowPremium = 0L, history = 0, highPremium = 0L))
    override fun checkRedCrossStatus(): Flow<String> = flowOf("")
    override fun checkMedicalStudent(): Flow<String> = flowOf("")
    override fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Flow<Long> = flowOf(0L)
    override fun calculateOptionalSalary(premiumRateCode: String): Flow<Long> = flowOf(0L)
    override fun makeFreelanceContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> =
        flowOf(FreelanceContractResultDN(contractNumber = null, contractDate = null))
    override fun makeContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> =
        flowOf(FreelanceContractResultDN(contractNumber = null, contractDate = null))
    override fun makeFreelanceContractByGuardian(
        params: FreelanceContractByGuardianParams,
    ): Flow<FreelanceContractResultDN> =
        flowOf(FreelanceContractResultDN(contractNumber = null, contractDate = null))
    override fun makeOptionalContractByGuardian(
        params: OptionalContractByGuardianParams,
    ): Flow<FreelanceContractResultDN> =
        flowOf(FreelanceContractResultDN(contractNumber = null, contractDate = null))
    override fun updateFreelanceContract(params: FreelanceMakeContractParams): Flow<Unit> = flowOf(Unit)
    override fun updateOptionalContract(premium: Long): Flow<Unit> = flowOf(Unit)
    override fun updateFreelanceContractByGuardian(params: FreelanceContractByGuardianParams): Flow<Unit> =
        flowOf(Unit)
    override fun updateOptionalContractByGuardian(params: OptionalContractByGuardianParams): Flow<Unit> =
        flowOf(Unit)
    override fun getInsurancePayment(params: InsurancePaymentParamsDN): Flow<InsurancePaymentDN> =
        flowOf(InsurancePaymentDN(paymentTicket = null, paymentUrl = null, responseMessage = null, succeed = null))
    override fun checkInsurancePaymentStatus(systemType: String): Flow<Any?> = flowOf(null)
    override fun uploadImage(request: UploadImageRequestDN): Flow<String> = flowOf("guid")
    override fun saveContact(request: SaveContactRequestDN): Flow<Any?> = flowOf(null)

    private fun pageOf(items: List<ContractDN>) = PagedListDN(items = items, total = items.size)
}

internal class FakeContractFlowUserRepository : UserRepository {
    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flowOf(
        IdentityInfoDN(
            cityOfBirthId = null,
            cityOfIssueId = null,
            countryId = null,
            dateOfBirth = null,
            fatherName = null,
            firstName = null,
            gender = null,
            id = null,
            idCardNumber = null,
            idCardSerial1 = null,
            idCardSerial2 = null,
            lastName = null,
            nationalId = null,
            ssn = null,
        ),
    )
    override suspend fun getUserProfileImage(): Flow<String> = flowOf("")
    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = flowOf(TaminRelationDN())
    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> = flowOf("")
    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> =
        flowOf(EditMobileResponseDN())
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> =
        flowOf("")
    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>): Flow<SubdominantDN> =
        flowOf(SubdominantDN())
    override suspend fun getBankAccountList(filters: List<ApiFilterDN>): Flow<List<BankAccountDN>> =
        flowOf(emptyList())
    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> = flowOf(emptyList())
    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>): Flow<List<ActiveRelationDN>> =
        flowOf(emptyList())
    override fun getElectronicFile(filters: List<ApiFilterDN>): Flow<List<ElectronicFileDN>> = flowOf(emptyList())
    override suspend fun downloadDocument(url: String): PdfDownloadDN = PdfDownloadDN()
    override suspend fun getUserProfile(): Flow<UserProfileDN> = flowOf(
        UserProfileDN(
            entityId = null,
            login = null,
            firstName = null,
            lastName = null,
            email = null,
            nationalCode = null,
            mobile = null,
        ),
    )
    override suspend fun getCurrentUser(): Flow<CurrentUserDN> = flowOf(CurrentUserDN())
    override fun checkUserIsNew(nationalId: String): Flow<Boolean> = flowOf(false)
    override suspend fun registerBankAccount(
        accountNumber: String,
        bankCode: String,
        accountTypeCode: String,
        startDateMillis: Long,
    ): Flow<String?> = flowOf(null)
    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flowOf("")
    override suspend fun getWageCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flowOf("")
    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = flowOf(emptyList())
}

internal class FakeContractFlowCityProvinceRepository : CityProvinceRepository {
    override fun getCity(cityId: String): Flow<CityDN> =
        flowOf(CityDN(cityCode = cityId, provinceCode = null, cityName = null))
    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flowOf(
        ProvinceDN(provinceCode = provinceId, provinceName = null, status = null, statusStartDate = null),
    )
    override fun getProvincesPage(query: ApiQueryParamDN): Flow<PageDN<ProvinceDN>> = flowOf(PageDN(emptyList()))
    override fun getCitiesPage(query: ApiQueryParamDN): Flow<PageDN<CityDN>> = flowOf(PageDN(emptyList()))
    override fun getCitiesByProvincePage(provinceCode: String, query: ApiQueryParamDN): Flow<PageDN<CityDN>> =
        flowOf(PageDN(emptyList()))
}
