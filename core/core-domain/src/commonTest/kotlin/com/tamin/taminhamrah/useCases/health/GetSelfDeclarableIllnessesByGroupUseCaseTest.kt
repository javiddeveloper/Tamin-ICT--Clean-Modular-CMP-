package com.tamin.taminhamrah.useCases.health

import app.cash.turbine.test
import com.tamin.taminhamrah.model.health.IllnessItemDN
import com.tamin.taminhamrah.model.health.SelfDeclarableIllnessGroupDN
import com.tamin.taminhamrah.repository.FakeHealthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetSelfDeclarableIllnessesByGroupUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeHealthRepository
    private lateinit var useCase: GetSelfDeclarableIllnessesByGroupUseCase

    @BeforeTest
    fun setup() {
        repository = FakeHealthRepository()
        useCase = GetSelfDeclarableIllnessesByGroupUseCase(repository)
    }

    @Test
    fun `invoke should return grouped illnesses`() = runTest {
        val expected = listOf(
            SelfDeclarableIllnessGroupDN(groupId = 1, groupTitle = "Cancer", forFamily = false, illnessList = listOf(IllnessItemDN(1, "Breast Cancer"))),
            SelfDeclarableIllnessGroupDN(groupId = 2, groupTitle = "Mental Health", forFamily = true, illnessList = emptyList())
        )
        repository.getSelfDeclarableIllnessesByGroupResult = expected

        useCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty list when no groups`() = runTest {
        repository.getSelfDeclarableIllnessesByGroupResult = emptyList()

        useCase().test {
            assertEquals(emptyList(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Network error")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
