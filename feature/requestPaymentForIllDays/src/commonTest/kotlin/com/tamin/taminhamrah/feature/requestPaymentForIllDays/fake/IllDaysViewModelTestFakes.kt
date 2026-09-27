package com.tamin.taminhamrah.feature.requestPaymentForIllDays.fake

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.ContractDN
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
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysBranchWorkshopDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import com.tamin.taminhamrah.repository.requestPaymentForIllDays.RequestPaymentForIllDaysRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeIllDaysRepository : RequestPaymentForIllDaysRepository {
    var shouldThrowError: Boolean = false
    var error: Throwable = RuntimeException("failed")
    var lastRequest: SaveShortTermIllnessRequestDN? = null
    var branchWorkshops: List<IllDaysBranchWorkshopDN> = listOf(
        IllDaysBranchWorkshopDN("0100", "Branch A", "W1", "Workshop 1"),
    )
    var calcResult: List<String> = listOf("1000000", "500000")
    var covidResult: CovidResultDN = CovidResultDN(
        startDateTimeStamp = "1700000000",
        endDateTimeStamp = "1700086400",
        timestamps = listOf("1700000000", "1700086400"),
    )

    var insuredInfo: IllDaysInsuredMainInfoDN? = IllDaysInsuredMainInfoDN(
        risuid = "1234567890",
        nationalCode = "0012345678",
        firstName = "Ali",
        lastName = "Rezaei",
        mobileNumber = "0912",
        genderCode = "1",
        branchCode = "0100",
        branchName = "Branch",
        bankAccount = "123",
        bankName = "Bank",
        insuranceTypeDesc = "Type",
        insuranceStatusDesc = "Active",
        serviceDateTimeStamp = 100L,
        branchWorkshops = branchWorkshops,
    )

    override fun getLatestInsuranceInfo(): Flow<IllDaysInsuredMainInfoDN?> = flow {
        if (shouldThrowError) throw error
        emit(insuredInfo?.copy(branchWorkshops = branchWorkshops))
    }

    override fun getCovidResult(): Flow<CovidResultDN> = flow {
        if (shouldThrowError) throw error
        emit(covidResult)
    }

    override fun calcIllness(
        startDateTimeStamp: String,
        endDateTimeStamp: String,
        maritalStatus: String,
    ): Flow<List<String>> = flow {
        if (shouldThrowError) throw error
        emit(calcResult)
    }

    override fun sendRequestForIllDay(request: SaveShortTermIllnessRequestDN): Flow<String?> = flow {
        lastRequest = request
        if (shouldThrowError) throw error
        emit("success")
    }
}

class FakeIllDaysCityProvinceRepository : CityProvinceRepository {
    var cities: List<CityDN> = listOf(
        CityDN(cityCode = "1158", cityName = "Tehran", provinceCode = "08"),
    )
    var lastQuery: ApiQueryParamDN? = null
    var citiesError: Throwable? = null

    override fun getCity(cityId: String): Flow<CityDN> = flowOf()
    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flowOf()
    override fun getProvincesPage(query: ApiQueryParamDN): Flow<PageDN<ProvinceDN>> = flowOf(PageDN(emptyList()))
    override fun getCitiesPage(query: ApiQueryParamDN): Flow<PageDN<CityDN>> = flow {
        lastQuery = query
        citiesError?.let { throw it }
        val term = query.filters.firstOrNull { it.property == FilterProperty.CITY_NAME }?.value?.trim('*')
        val matching = cities.filter { term.isNullOrBlank() || it.cityName?.contains(term, ignoreCase = true) == true }
        emit(PageDN(matching.drop(query.start).take(query.limit), total = matching.size))
    }
    override fun getCitiesByProvincePage(provinceCode: String, query: ApiQueryParamDN): Flow<PageDN<CityDN>> =
        flowOf(PageDN(emptyList()))
}

class FakeIllDaysContractsRepository : ContractsRepository {
    var uploadGuid: String = "upload-guid-1"
    var shouldThrowOnUpload: Boolean = false

    override fun uploadImage(request: UploadImageRequestDN): Flow<String> = flow {
        if (shouldThrowOnUpload) throw RuntimeException("upload failed")
        emit(uploadGuid)
    }

    override fun getBranches(cityCode: String, page: Int): Flow<PagedListDN<BranchDN>> = flowOf(PagedListDN())
    override fun getContracts(page: Int): Flow<PagedListDN<ContractDN>> = flowOf(PagedListDN())
    override fun getContractsByPremiumType(premiumTypeCode: String, page: Int): Flow<PagedListDN<ContractDN>> =
        flowOf(PagedListDN())
    override fun getStudentInsuranceContracts(page: Int): Flow<PagedListDN<ContractDN>> = flowOf(PagedListDN())
    override fun getRegistrationInfo(): Flow<RegistrationInfoDN> = flowOf()
    override fun getSpcPremiumRates(): Flow<List<PremiumRateDN>> = flowOf(emptyList())
    override fun getFreeJobWages(
        page: Int,
        searchQuery: String?
    ): Flow<PagedListDN<FreeJobDN>>  = flowOf()

    override fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN> = flowOf()
    override fun getOptionalPremiumRange(): Flow<FreelancePremiumRangeDN> = flowOf()

    override fun checkRedCrossStatus(): Flow<String> = flowOf()

    override fun checkMedicalStudent(): Flow<String> = flowOf()

    override fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Flow<Long> = flowOf(0L)
    override fun calculateOptionalSalary(premiumRateCode: String): Flow<Long> = flowOf(0L)
    override fun makeFreelanceContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = flowOf()
    override fun makeContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> = flowOf()
    override fun makeFreelanceContractByGuardian(params: FreelanceContractByGuardianParams): Flow<FreelanceContractResultDN> = flowOf()
    override fun makeOptionalContractByGuardian(params: OptionalContractByGuardianParams): Flow<FreelanceContractResultDN> = flowOf()
    override fun updateFreelanceContract(params: FreelanceMakeContractParams): Flow<Unit> = flowOf(Unit)
    override fun updateOptionalContract(premium: Long): Flow<Unit> = flowOf(Unit)
    override fun updateFreelanceContractByGuardian(params: FreelanceContractByGuardianParams): Flow<Unit> = flowOf(Unit)
    override fun updateOptionalContractByGuardian(params: OptionalContractByGuardianParams): Flow<Unit> = flowOf(Unit)
    override fun getInsurancePayment(params: InsurancePaymentParamsDN): Flow<InsurancePaymentDN> = flowOf()
    override fun checkInsurancePaymentStatus(systemType: String): Flow<Any?> = flowOf(null)
    override fun saveContact(request: SaveContactRequestDN): Flow<Any?> = flowOf(null)
}
