package com.tamin.taminhamrah.useCases.fractionContract

import app.cash.turbine.test
import com.tamin.taminhamrah.model.fractionContract.FractionContractResultDN
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityDN
import com.tamin.taminhamrah.repository.fractionContract.FractionContractRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FakeFractionContractRepository : FractionContractRepository {
    var eligibilityResult: FractionEligibilityDN? = null
    var makeResult: FractionContractResultDN = FractionContractResultDN()
    var lastPremium: String? = null

    override fun checkAgeAndHistory(): Flow<FractionEligibilityDN?> = flowOf(eligibilityResult)

    override fun makeFractionContract(premium: String): Flow<FractionContractResultDN> {
        lastPremium = premium
        return flowOf(makeResult)
    }
}

class FractionContractUseCasesTest {

    @Test
    fun checkFractionAgeAndHistoryUseCase_returnsEligibilityFromRepository() = runTest {
        val repository = FakeFractionContractRepository().apply {
            eligibilityResult = FractionEligibilityDN(
                isInsurance = true,
                checkFractionMonthStatus = "1",
                eligibilityStatus = 2,
            )
        }
        val useCase = CheckFractionAgeAndHistoryUseCase(repository)

        useCase().test {
            val item = awaitItem()
            assertEquals(true, item?.isInsurance)
            assertEquals("1", item?.checkFractionMonthStatus)
            awaitComplete()
        }
    }

    @Test
    fun checkFractionAgeAndHistoryUseCase_preservesNull() = runTest {
        val repository = FakeFractionContractRepository().apply {
            eligibilityResult = null
        }
        val useCase = CheckFractionAgeAndHistoryUseCase(repository)

        useCase().test {
            assertNull(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun makeFractionContractUseCase_returnsResultFromRepository() = runTest {
        val repository = FakeFractionContractRepository().apply {
            makeResult = FractionContractResultDN(
                contractNumber = 987L,
                contractDate = 1710000000000L,
            )
        }
        val useCase = MakeFractionContractUseCase(repository)

        useCase().test {
            val item = awaitItem()
            assertEquals(987L, item.contractNumber)
            assertEquals(1710000000000L, item.contractDate)
            awaitComplete()
        }

        assertEquals("this.premium", repository.lastPremium)
    }
}
