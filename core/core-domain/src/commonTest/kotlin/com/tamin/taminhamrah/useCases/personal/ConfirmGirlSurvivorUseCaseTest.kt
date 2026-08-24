package com.tamin.taminhamrah.useCases.personal

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.DependencyTypeDN
import com.tamin.taminhamrah.repository.FakePersonalRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfirmGirlSurvivorUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakePersonalRepository
    private lateinit var useCase: ConfirmGirlSurvivorUseCase

    @BeforeTest
    fun setup() {
        repository = FakePersonalRepository()
        useCase = ConfirmGirlSurvivorUseCase(repository)
    }

    @Test
    fun `invoke should return confirm message from repository`() = runTest {
        repository.confirmGirlSurvivorResult = "درخواست با موفقیت ثبت شد"

        useCase(sampleBody()).test {
            assertEquals("درخواست با موفقیت ثبت شد", awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("confirm failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(sampleBody(pensionId = "1234567890")).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    private fun sampleBody(
        nationalCode: String? = "9988776655",
        pensionId: String? = null,
    ) = ConfirmGirlSurvivorDN(
        address = "تهران، خیابان آزادی، پلاک ۱۲",
        age = "33",
        birthDate = 631152000000,
        childInsuranceId = "0071234567",
        childNationalId = "0012345678",
        dependencyType = DependencyTypeDN(code = "04"),
        firstName = "زهرا",
        gender = "02",
        idNumber = "456789",
        insuranceNumber = "0071234567",
        lastName = "محمدی",
        mobileNumber = "09121234567",
        nationalCode = nationalCode,
        pensionId = pensionId,
        phoneNumber = "02166778899",
        status = "0",
    )
}
