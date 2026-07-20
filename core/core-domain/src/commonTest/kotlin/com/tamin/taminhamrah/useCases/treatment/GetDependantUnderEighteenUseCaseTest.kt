package com.tamin.taminhamrah.useCases.treatment

import app.cash.turbine.test
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.repository.FakeTreatmentRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetDependantUnderEighteenUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var useCase: GetDependantUnderEighteenUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTreatmentRepository()
        useCase = GetDependantUnderEighteenUseCase(repository)
    }

    @Test
    fun `invoke should return dependant list`() = runTest {
        val expected = listOf(
            DependantUserUnderEighteenDN(
                firstName = "ChildFirstName",
                lastName = "ChildLastName", nationalId = "childNationalId", id = 1L
            )
        )
        repository.getDependantUnderEighteenResult = expected

        useCase("nationalCode").test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase("nationalCode").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    @Test
    fun `invoke should emit empty list when there are no dependants`() = runTest {
        repository.getDependantUnderEighteenResult = emptyList()

        useCase("nationalCode").test {
            assertEquals(emptyList<DependantUserUnderEighteenDN>(), awaitItem())
            awaitComplete()
        }
    }
}
