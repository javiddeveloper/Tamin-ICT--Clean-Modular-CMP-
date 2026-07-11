package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.repository.contracts.FakeContractsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SaveContactUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: SaveContactUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = SaveContactUseCase(repository)
    }

    @Test
    fun `invoke should save contact through repository`() = runTest {
        val request = SaveContactRequestDN(
            address = "تهران",
            mobile = "",
            ssn = "2487741923",
            phoneNumber = "02126555891",
            zipCode = "4915784967",
        )
        repository.saveContactResult = null

        useCase(request).test {
            assertNull(awaitItem())
            awaitComplete()
        }

        assertEquals(request, repository.lastSaveContactRequest)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(
            SaveContactRequestDN(
                address = "تهران",
                mobile = "",
                ssn = "2487741923",
                phoneNumber = "02126555891",
                zipCode = "4915784967",
            ),
        ).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
