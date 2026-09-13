package com.tamin.taminhamrah.useCases.weddingPresent

import app.cash.turbine.test
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDN
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentSubmitRequestDN
import com.tamin.taminhamrah.repository.weddingPresent.WeddingPresentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FakeWeddingPresentRepository : WeddingPresentRepository {
    var infoResult: WeddingPresentInfoDN = WeddingPresentInfoDN()
    var lastSubmitRequest: WeddingPresentSubmitRequestDN? = null
    var calculateResult: List<String> = listOf("1000", "2000")
    var lastCalculateTimeStamp: String? = null

    override fun getWeddingPresentInfo(): Flow<WeddingPresentInfoDN> = flowOf(infoResult)

    override fun submitWeddingPresent(request: WeddingPresentSubmitRequestDN): Flow<Unit> {
        lastSubmitRequest = request
        return flowOf(Unit)
    }

    override fun calculateMarriageAllowance(timeStamp: String): Flow<List<String>> {
        lastCalculateTimeStamp = timeStamp
        return flowOf(calculateResult)
    }
}

class WeddingPresentUseCasesTest {

    @Test
    fun getWeddingPresentInfoUseCase_returnsInfoFromRepository() = runTest {
        val repository = FakeWeddingPresentRepository().apply {
            infoResult = WeddingPresentInfoDN(
                risuid = "123",
                insuranceFirstName = "علی",
            )
        }
        val useCase = GetWeddingPresentInfoUseCase(repository)

        useCase().test {
            val item = awaitItem()
            assertEquals("123", item.risuid)
            assertEquals("علی", item.insuranceFirstName)
            awaitComplete()
        }
    }

    @Test
    fun submitWeddingPresentUseCase_forwardsRequest() = runTest {
        val repository = FakeWeddingPresentRepository()
        val useCase = SubmitWeddingPresentUseCase(repository)
        val request = WeddingPresentSubmitRequestDN(
            partnerNationalId = "0098765432",
            weddingDateTimeStamp = 1L,
            info = WeddingPresentInfoDN(risuid = "123"),
        )

        useCase(request).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(request, repository.lastSubmitRequest)
    }

    @Test
    fun calculateMarriageAllowanceUseCase_forwardsTimeStamp() = runTest {
        val repository = FakeWeddingPresentRepository().apply {
            calculateResult = listOf("130300000", "130300000")
        }
        val useCase = CalculateMarriageAllowanceUseCase(repository)

        useCase("1700000000000").test {
            assertEquals(listOf("130300000", "130300000"), awaitItem())
            awaitComplete()
        }

        assertEquals("1700000000000", repository.lastCalculateTimeStamp)
    }
}
