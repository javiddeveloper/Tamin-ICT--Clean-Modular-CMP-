package com.tamin.taminhamrah.useCases.common

import app.cash.turbine.test
import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.common.FakeCommonRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetBeneficiaryUseCaseTest : BaseUseCaseTest() {

    private lateinit var commonRepository: FakeCommonRepository
    private lateinit var useCase: GetBeneficiaryUseCase

    @BeforeTest
    fun setup() {
        commonRepository = FakeCommonRepository()
        useCase = GetBeneficiaryUseCase(commonRepository)
    }

    @Test
    fun `invoke should return beneficiary list from repository`() = runTest {
        val expectedList = listOf(
            BeneficiaryDN(bankCode = "1", bankName = "Melli"),
            BeneficiaryDN(bankCode = "2", bankName = "Saderat")
        )
        commonRepository.beneficiaryResult = expectedList

        useCase.invoke().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Melli", result[0].bankName)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Network Error")
        commonRepository.shouldThrowError = true
        commonRepository.getBeneficiaryError = expectedException

        useCase.invoke().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
