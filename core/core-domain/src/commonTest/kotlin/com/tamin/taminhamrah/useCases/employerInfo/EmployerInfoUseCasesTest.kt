package com.tamin.taminhamrah.useCases.employerInfo

import app.cash.turbine.test
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDN
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDN
import com.tamin.taminhamrah.repository.employerInfo.FakeEmployerInfoRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EmployerInfoUseCasesTest : BaseUseCaseTest() {

    private lateinit var repository: FakeEmployerInfoRepository
    private lateinit var getLegalWorkshopUseCase: GetLegalWorkshopUseCase
    private lateinit var getLegalWorkshopCeoUseCase: GetLegalWorkshopCeoUseCase
    private lateinit var requestLegalTicketUseCase: RequestLegalTicketUseCase
    private lateinit var submitLegalWorkshopInfoUseCase: SubmitLegalWorkshopInfoUseCase
    private lateinit var requestRealTicketUseCase: RequestRealTicketUseCase
    private lateinit var submitRealWorkshopInfoUseCase: SubmitRealWorkshopInfoUseCase

    @BeforeTest
    fun setup() {
        repository = FakeEmployerInfoRepository()
        getLegalWorkshopUseCase = GetLegalWorkshopUseCase(repository)
        getLegalWorkshopCeoUseCase = GetLegalWorkshopCeoUseCase(repository)
        requestLegalTicketUseCase = RequestLegalTicketUseCase(repository)
        submitLegalWorkshopInfoUseCase = SubmitLegalWorkshopInfoUseCase(repository)
        requestRealTicketUseCase = RequestRealTicketUseCase(repository)
        submitRealWorkshopInfoUseCase = SubmitRealWorkshopInfoUseCase(repository)
    }

    @Test
    fun `GetLegalWorkshopUseCase returns data from repository`() = runTest {
        val expected = LegalWorkshopDN(name = "شرکت تست البرز", nationalCode = "10101234567")
        repository.legalWorkshopResult = expected

        getLegalWorkshopUseCase("10101234567").test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
        assertEquals("10101234567", repository.lastLegalNationalCode)
    }

    @Test
    fun `GetLegalWorkshopCeoUseCase returns data from repository`() = runTest {
        val expected = LegalWorkshopCeoDN(firstName = "رضا", lastName = "احمدی")
        repository.legalWorkshopCeoResult = expected

        getLegalWorkshopCeoUseCase("0012345678", 540864000000L).test {
            val result = awaitItem()
            assertEquals(expected, result)
            assertEquals("رضا احمدی", result.fullName)
            awaitComplete()
        }
        assertEquals("0012345678", repository.lastCeoNationalCode)
        assertEquals(540864000000L, repository.lastCeoBirthDateMillis)
    }

    @Test
    fun `RequestLegalTicketUseCase passes parameters correctly`() = runTest {
        repository.requestLegalTicketResult = "12345"
        requestLegalTicketUseCase("09123456789", "test@domain.com", "0012345678").test {
            assertEquals("12345", awaitItem())
            awaitComplete()
        }
        assertEquals("09123456789", repository.lastLegalTicketMobile)
        assertEquals("test@domain.com", repository.lastLegalTicketEmail)
        assertEquals("0012345678", repository.lastLegalTicketCeoNationalCode)
    }

    @Test
    fun `SubmitLegalWorkshopInfoUseCase passes payload correctly`() = runTest {
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
        repository.submitLegalWorkshopInfoResult = "OK"
        submitLegalWorkshopInfoUseCase(request).test {
            assertEquals("OK", awaitItem())
            awaitComplete()
        }
        assertEquals(request, repository.lastLegalWorkshopInfoRequest)
    }

    @Test
    fun `RequestRealTicketUseCase passes parameters correctly`() = runTest {
        repository.requestRealTicketResult = "54321"
        requestRealTicketUseCase("09123456789", "real@domain.com").test {
            assertEquals("54321", awaitItem())
            awaitComplete()
        }
        assertEquals("09123456789", repository.lastRealTicketMobile)
        assertEquals("real@domain.com", repository.lastRealTicketEmail)
    }

    @Test
    fun `SubmitRealWorkshopInfoUseCase passes payload correctly`() = runTest {
        val request = RealWorkshopInfoRequestDN(
            branchCode = "01",
            workshopCode = "0012345678",
            ticketCode = "54321",
        )
        repository.submitRealWorkshopInfoResult = "OK"
        submitRealWorkshopInfoUseCase(request).test {
            assertEquals("OK", awaitItem())
            awaitComplete()
        }
        assertEquals(request, repository.lastRealWorkshopInfoRequest)
    }
}
