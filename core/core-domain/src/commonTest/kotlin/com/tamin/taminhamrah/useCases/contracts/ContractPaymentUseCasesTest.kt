package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contracts.FreelanceContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.ContractInsuranceSystemTypeCode
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDN
import com.tamin.taminhamrah.model.contracts.GuardianShipDetailDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.model.contracts.OptionalMakeContractRequestDN
import com.tamin.taminhamrah.repository.contracts.FakeContractsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MakeFreelanceContractByGuardianUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: MakeFreelanceContractByGuardianUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = MakeFreelanceContractByGuardianUseCase(repository)
    }

    @Test
    fun `invoke delegates freelance guardian params to repository`() = runTest {
        val params = freelanceGuardianParams()
        val expectedResult = FreelanceContractResultDN(contractNumber = 123L, contractDate = 456L)
        repository.makeContractResult = expectedResult

        useCase(params).test {
            assertEquals(expectedResult, awaitItem())
            awaitComplete()
        }

        assertEquals(params, repository.lastMakeFreelanceContractByGuardianParams)
        assertTrue(repository.makeFreelanceContractByGuardianCalled)
        assertFalse(repository.makeOptionalContractByGuardianCalled)
    }

    private fun freelanceGuardianParams() = FreelanceContractByGuardianParams(
        selectedSalary = 25_989_368L,
        contract = FreelanceMakeContractRequestDN(
            brchCodeNew = "0360",
            cityCode = "2442",
            cntDrmn = "1",
            cntFreeJobCode = "099796",
            guid = "00",
            guidName = "00",
            premiumRateCode = "01",
            provinceCode = "33",
        ),
        protector = sampleProtector(),
    )

    private fun sampleProtector() = GuardianShipDetailDN(
        proCode = "3860387200",
        guid = "6f1c66e4-1ecf-441a-8868-96e1b2f29157",
        guidName = "تصویر قیم نامه",
        nid = "0083834001",
        fullName = "رضا نادری",
        protectorLetterNo = "222222222222",
        protectorLetterDate = "2022-09-18T19:30:00.000Z",
    )
}

class MakeOptionalContractByGuardianUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: MakeOptionalContractByGuardianUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = MakeOptionalContractByGuardianUseCase(repository)
    }

    @Test
    fun `invoke delegates optional guardian params to repository`() = runTest {
        val params = optionalGuardianParams()
        val expectedResult = FreelanceContractResultDN(contractNumber = 789L, contractDate = 101L)
        repository.makeContractResult = expectedResult

        useCase(params).test {
            assertEquals(expectedResult, awaitItem())
            awaitComplete()
        }

        assertEquals(params, repository.lastMakeOptionalContractByGuardianParams)
        assertTrue(repository.makeOptionalContractByGuardianCalled)
        assertFalse(repository.makeFreelanceContractByGuardianCalled)
    }

    private fun optionalGuardianParams() = OptionalContractByGuardianParams(
        selectedSalary = 362_592_593L,
        contract = OptionalMakeContractRequestDN(
            brchCodeNew = "0360",
            cityCode = "2442",
            cntDrmn = "1",
            premiumRateCode = "01",
            provinceCode = "33",
        ),
        protector = sampleProtector(),
    )

    private fun sampleProtector() = GuardianShipDetailDN(
        proCode = "3860387200",
        guid = "6f1c66e4-1ecf-441a-8868-96e1b2f29157",
        guidName = "تصویر قیم نامه",
        nid = "0083834001",
        fullName = "رضا نادری",
        protectorLetterNo = "222222222222",
        protectorLetterDate = "2022-09-18T19:30:00.000Z",
    )
}

class GetInsurancePaymentUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: GetInsurancePaymentUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = GetInsurancePaymentUseCase(repository)
    }

    @Test
    fun `invoke returns payment info from repository`() = runTest {
        val params = InsurancePaymentParamsDN(
            systemType = ContractInsuranceSystemTypeCode.SPECIAL_INSURED,
            redirectUrl = "https://hamrah.tamin.ir/payment/callback",
            startDate = 0L,
            endDate = 0L,
            amount = 0L,
            redirectUri = "",
            paramPage = "",
            month = 0,
        )
        val expected = InsurancePaymentDN(
            paymentTicket = "ticket-123",
            paymentUrl = "https://sep.shaparak.ir/payment/ticket-123",
            responseMessage = "SUCCESS",
            succeed = true,
        )
        repository.insurancePaymentResult = expected

        useCase(params).test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }

        assertEquals(params, repository.lastInsurancePaymentParams)
    }
}

class CheckInsurancePaymentStatusUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: CheckInsurancePaymentStatusUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = CheckInsurancePaymentStatusUseCase(repository)
    }

    @Test
    fun `invoke returns payment status from repository`() = runTest {
        val expected = JsonPrimitive(true)
        repository.paymentStatusResult = expected

        useCase(ContractInsuranceSystemTypeCode.OPTIONAL).test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }

        assertEquals(ContractInsuranceSystemTypeCode.OPTIONAL, repository.lastPaymentStatusSystemType)
    }
}
