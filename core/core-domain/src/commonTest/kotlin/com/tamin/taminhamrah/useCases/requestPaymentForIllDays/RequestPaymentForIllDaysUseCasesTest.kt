package com.tamin.taminhamrah.useCases.requestPaymentForIllDays

import app.cash.turbine.test
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysRequestFileDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDN
import com.tamin.taminhamrah.repository.requestPaymentForIllDays.FakeRequestPaymentForIllDaysRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestPaymentForIllDaysUseCasesTest : BaseUseCaseTest() {

    private lateinit var repository: FakeRequestPaymentForIllDaysRepository

    @BeforeTest
    fun setup() {
        repository = FakeRequestPaymentForIllDaysRepository()
    }

    @Test
    fun getIllDaysInsuredMainInfo_returnsRepositoryData() = runTest {
        val useCase = GetIllDaysInsuredMainInfoUseCase(repository)

        useCase().test {
            val result = awaitItem()
            assertEquals("1234567890", result?.risuid)
            assertEquals("Ali", result?.firstName)
            awaitComplete()
        }
    }

    @Test
    fun getCovidResult_returnsTimestamps() = runTest {
        val useCase = GetCovidResultUseCase(repository)

        useCase().test {
            val result = awaitItem()
            assertEquals("1700000000", result.startDateTimeStamp)
            assertEquals("1700086400", result.endDateTimeStamp)
            awaitComplete()
        }
    }

    @Test
    fun sendRequestForIllDay_forwardsRequestAndMessage() = runTest {
        val useCase = SendRequestForIllDayUseCase(repository)
        val request = SaveShortTermIllnessRequestDN(
            doctorId = "d1",
            doctorName = "Dr",
            startDateTimeStamp = 1L,
            endDateTimeStamp = 2L,
            illnessKind = "2",
            workStatus = "2",
            provinceCode = "08",
            cityCode = "01",
            branchCode = "0100",
            branchName = "Branch",
            insuranceFirstName = "Ali",
            insuranceLastName = "Rezaei",
            mobileNumber = "0912",
            nationalCode = "001",
            risuid = "1",
            serviceDateTimeStamp = 10L,
            requestFileList = listOf(
                IllDaysRequestFileDN(documentFile = "guid", documentType = "01")
            ),
        )

        useCase(request).test {
            assertEquals("success", awaitItem())
            awaitComplete()
        }
        assertEquals(request, repository.lastRequest)
    }
}
