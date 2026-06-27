package com.tamin.taminhamrah.apiService.contract

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDTO
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDTO
import com.tamin.taminhamrah.model.contracts.FreeJobDTO
import com.tamin.taminhamrah.model.contracts.PremiumRateDTO
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDTO
import com.tamin.taminhamrah.model.contracts.SaveContactPersonalDTO
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDTO
import com.tamin.taminhamrah.model.contracts.BranchDTO
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.ContractsTestData
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ContractsApiServiceTest : BaseApiTest() {

    @Test
    fun `getContractList should return contract list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.contractsListSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.getContractList(emptyMap())

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val listData = response.data
        assertNotNull(listData)
        assertEquals(1, listData.total)

        val contracts: List<ContractDTO> = listData.list.orEmpty()
        assertEquals(1, contracts.size)

        val firstContract = contracts.first()
        assertEquals(478176974, firstContract.contractNumber)
        assertEquals(1780398668987L, firstContract.contractDate)
        assertEquals(362592593L, firstContract.salary)
        assertEquals("1", firstContract.cntDrmn)

        assertNotNull(firstContract.contractStatusObject)
        assertEquals(1, firstContract.contractStatusObject?.selfIsuContStatCode)

        assertNotNull(firstContract.premiumType)
        assertEquals("02", firstContract.premiumType?.insuranceTypeCode)

        assertNotNull(firstContract.premiumRate)
        assertEquals("27", firstContract.premiumRate?.insurDpercent)

        assertNotNull(firstContract.freeJob)
    }

    @Test
    fun `getSpcPremiumRates should return premium rate list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.spcPremiumRateSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.getSpcPremiumRates()

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val listData = response.data
        assertNotNull(listData)
        assertEquals(2, listData.total)

        val rates: List<PremiumRateDTO> = listData.list.orEmpty()
        assertEquals(2, rates.size)

        val firstRate = rates.first()
        assertEquals("01", firstRate.spcrateCode)
        assertEquals("12", firstRate.insurDpercent)

        val secondRate = rates[1]
        assertEquals("02", secondRate.spcrateCode)
        assertEquals("14", secondRate.insurDpercent)
    }

    @Test
    fun `getFreelancePremiumRange should return low and high premium bounds`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.freelancePremiumRangeSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.getFreelancePremiumRange(
            treatmentSupportCode = "1",
            spcRateCode = "01",
            freeJobCode = "099796",
        )

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val data: FreelancePremiumRangeDTO = response.data!!
        assertEquals(0L, data.paymentTabayi)
        assertEquals(25_989_368L, data.lowPremium)
        assertEquals(538, data.history)
        assertEquals(139_654_620L, data.highPremium)
    }

    @Test
    fun `calculateFreelanceSalary should return calculated monthly salary`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.freelanceCalculateSalarySuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.calculateFreelanceSalary(
            monthlyPremium = 60_300_000L,
            treatmentSupportCode = "1",
            spcRateCode = "01",
        )

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertEquals(502_500_000L, response.data)
    }

    @Test
    fun `saveContact should return successful response with null data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(dataJson = "null")

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.saveContact(
            SaveContactRequestDTO(
                address = "اينجا56564544545",
                mobile = "",
                personal = SaveContactPersonalDTO(ssn = "2487741923"),
                phoneNumber = "02126555891",
                zipCode = "4915784967",
            ),
        )

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
    }

    @Test
    fun `getRegistrationInfo should return registration info`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.registrationInfoSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.getRegistrationInfo()

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val data: RegistrationInfoDTO = response.data!!
        assertEquals(true, data.insuranceIdValidity)
        assertEquals("12345678901", data.insuranceId)
        assertEquals("09121234567", data.mobileNumber)
        assertNotNull(data.personalInfo)
        assertEquals("علی", data.personalInfo?.firstName)
        assertEquals("2487741923", data.personalInfo?.ssn)
        assertNotNull(data.lastContact)
        assertEquals("تهران", data.lastContact?.address)
    }

    @Test
    fun `getBranches should return branch list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.branchesListSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.getBranches(emptyMap())

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val listData = response.data
        assertNotNull(listData)
        assertEquals(1, listData.total)

        val branches: List<BranchDTO> = listData.list.orEmpty()
        assertEquals(1, branches.size)
        assertEquals("001", branches.first().code)
        assertEquals("شعبه مرکزی", branches.first().name)
        assertEquals("0101", branches.first().cityCode)
    }

    @Test
    fun `calculateOptionalSalary should return calculated monthly salary`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.optionalCalculateSalarySuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.calculateOptionalSalary(premiumRate = "25989368")

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        assertEquals(362_592_593L, response.data)
    }

    @Test
    fun `getFreeJobWages should return free job list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.freeJobWagesSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.getFreeJobWages(emptyMap())

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val listData = response.data
        assertNotNull(listData)
        assertEquals(1, listData.total)

        val freeJobs: List<FreeJobDTO> = listData.list.orEmpty()
        assertEquals(1, freeJobs.size)
        assertEquals("099796", freeJobs.first().jobCode)
        assertEquals("تاسیساتی", freeJobs.first().discrioption)
    }

    @Test
    fun `makeFreelanceContract should return contract number and date`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.freelanceMakeContractSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.makeFreelanceContract(
            monthlyPremium = 25_989_368L,
            request = FreelanceMakeContractRequestDTO(
                brchCodeNew = "0360",
                cityCode = "2442",
                cntDrmn = "1",
                cntFreeJobCode = "099796",
                guid = "00",
                guidName = "00",
                premiumRateCode = "01",
                provinceCode = "33",
            ),
        )

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val data: FreelanceContractResultDTO = response.data!!
        assertEquals(478_176_975L, data.contractNumber)
        assertEquals(1_782_132_474_000L, data.contractDate)
    }

    @Test
    fun `makeContract should return contract number and date`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = ContractsTestData.freelanceMakeContractSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createContractsApiService()

        val response = apiService.makeContract(
            selectedSalary = 362_592_593L,
            request = FreelanceMakeContractRequestDTO(
                brchCodeNew = "0360",
                cityCode = "2442",
                cntDrmn = "1",
                cntFreeJobCode = "",
                guid = "00",
                guidName = "00",
                premiumRateCode = "01",
                provinceCode = "33",
            ),
        )

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val data: FreelanceContractResultDTO = response.data!!
        assertEquals(478_176_975L, data.contractNumber)
        assertEquals(1_782_132_474_000L, data.contractDate)
    }

    @Test
    fun `uploadImage should return image guid`() = runTest {
        val ktorfit = createMockKtorfit(ContractsTestData.uploadImageSuccess)
        val apiService = ktorfit.createContractsApiService()

        val content = MultiPartFormDataContent(
            formData {
                append(
                    key = "file",
                    value = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xD9.toByte()),
                    headers = Headers.build {
                        append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
                        append(HttpHeaders.ContentDisposition, "filename=\"test.jpg\"")
                    },
                )
            },
        )

        val response = apiService.uploadImage(content)

        assertEquals("a4769aa8-b9af-4183-83b9-367dc9f52511", response.guid)
    }

}
