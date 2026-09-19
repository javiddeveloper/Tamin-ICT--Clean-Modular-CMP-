package com.tamin.taminhamrah.data.repository.employerInfo

import app.cash.turbine.test
import com.tamin.taminhamrah.dataSource.employerInfo.EmployerInfoRemoteDataSource
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDTO
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDTO
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDTO
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDN
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeEmployerInfoRemoteDataSource : EmployerInfoRemoteDataSource {
    var legalWorkshopResult = LegalWorkshopDTO(name = "شرکت تست", nationalCode = "10100000000")
    var legalWorkshopCeoResult = LegalWorkshopCeoDTO(firstName = "علی", lastName = "محمدی")
    var legalTicketResult = "12345"
    var saveStackHoldersResult = "OK"
    var realTicketResult = "54321"
    var saveRealPersonResult = "OK"

    var shouldThrowError: Exception? = null

    var lastLegalWorkshopId: String? = null
    var lastCeoNationalCode: String? = null
    var lastCeoBirthDate: String? = null
    var lastLegalTicketFilters: List<ApiFilterDN>? = null
    var lastSaveStackHoldersRequest: LegalWorkshopInfoRequestDTO? = null
    var lastRealTicketFilters: List<ApiFilterDN>? = null
    var lastSaveRealPersonRequest: RealWorkshopInfoRequestDTO? = null

    override suspend fun getLegalWorkshop(legalWorkshopId: String): LegalWorkshopDTO {
        shouldThrowError?.let { throw it }
        lastLegalWorkshopId = legalWorkshopId
        return legalWorkshopResult
    }

    override suspend fun getLegalWorkshopCeo(nationalCode: String, birthDate: String): LegalWorkshopCeoDTO {
        shouldThrowError?.let { throw it }
        lastCeoNationalCode = nationalCode
        lastCeoBirthDate = birthDate
        return legalWorkshopCeoResult
    }

    override suspend fun requestLegalTicket(filters: List<ApiFilterDN>): String {
        shouldThrowError?.let { throw it }
        lastLegalTicketFilters = filters
        return legalTicketResult
    }

    override suspend fun submitLegalWorkshopInfo(body: LegalWorkshopInfoRequestDTO): String {
        shouldThrowError?.let { throw it }
        lastSaveStackHoldersRequest = body
        return saveStackHoldersResult
    }

    override suspend fun requestRealTicket(filters: List<ApiFilterDN>): String {
        shouldThrowError?.let { throw it }
        lastRealTicketFilters = filters
        return realTicketResult
    }

    override suspend fun submitRealWorkshopInfo(body: RealWorkshopInfoRequestDTO): String {
        shouldThrowError?.let { throw it }
        lastSaveRealPersonRequest = body
        return saveRealPersonResult
    }
}

class EmployerInfoRepositoryImplTest {

    private lateinit var remoteDataSource: FakeEmployerInfoRemoteDataSource
    private lateinit var repository: EmployerInfoRepositoryImpl

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeEmployerInfoRemoteDataSource()
        repository = EmployerInfoRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getLegalWorkshop delegates to remoteDataSource and maps correctly`() = runTest {
        remoteDataSource.legalWorkshopResult = LegalWorkshopDTO(name = "شرکت پتروشیمی", nationalCode = "10101234567")

        repository.getLegalWorkshop("10101234567").test {
            val result = awaitItem()
            assertEquals("شرکت پتروشیمی", result.name)
            assertEquals("10101234567", result.nationalCode)
            awaitComplete()
        }
        assertEquals("10101234567", remoteDataSource.lastLegalWorkshopId)
    }

    @Test
    fun `getLegalWorkshopCeo delegates to remoteDataSource with formatted date`() = runTest {
        remoteDataSource.legalWorkshopCeoResult = LegalWorkshopCeoDTO(firstName = "حسن", lastName = "رضایی")

        repository.getLegalWorkshopCeo("0012345678", 540864000000L).test {
            val result = awaitItem()
            assertEquals("حسن", result.firstName)
            assertEquals("رضایی", result.lastName)
            assertEquals("حسن رضایی", result.fullName)
            awaitComplete()
        }
        assertEquals("0012345678", remoteDataSource.lastCeoNationalCode)
    }

    @Test
    fun `requestLegalTicket delegates to remoteDataSource with filters`() = runTest {
        repository.requestLegalTicket("09123456789", "test@domain.com", "0012345678").test {
            assertEquals("12345", awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `submitLegalWorkshopInfo delegates to remoteDataSource with DTO`() = runTest {
        val request = LegalWorkshopInfoRequestDN(
            workshopId = "ws1",
            branchCode = "01",
            workshopNationalCode = "10101234567",
            legalWorkshopTypeCode = "03",
            ceoNationalId = "0012345678",
            ceoBirthDateMillis = 540864000000L,
            telephone = "02188888888",
            mobile = "09123456789",
            email = "info@company.ir",
            ticketCode = "12345",
        )
        repository.submitLegalWorkshopInfo(request).test {
            assertTrue(awaitItem().isNotBlank())
            awaitComplete()
        }
        assertEquals("ws1", remoteDataSource.lastSaveStackHoldersRequest?.workshopId)
        assertEquals("10101234567", remoteDataSource.lastSaveStackHoldersRequest?.workshopNationalCode)
    }

    @Test
    fun `requestRealTicket delegates to remoteDataSource with filters`() = runTest {
        repository.requestRealTicket("09123456789", "test@domain.com").test {
            assertEquals("54321", awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `submitRealWorkshopInfo delegates to remoteDataSource with DTO`() = runTest {
        val request = RealWorkshopInfoRequestDN(
            branchCode = "01",
            workshopCode = "0012345678",
            ticketCode = "54321",
        )
        repository.submitRealWorkshopInfo(request).test {
            assertTrue(awaitItem().isNotBlank())
            awaitComplete()
        }
        assertEquals("01", remoteDataSource.lastSaveRealPersonRequest?.brchcode)
        assertEquals("0012345678", remoteDataSource.lastSaveRealPersonRequest?.rwshid)
    }
}
