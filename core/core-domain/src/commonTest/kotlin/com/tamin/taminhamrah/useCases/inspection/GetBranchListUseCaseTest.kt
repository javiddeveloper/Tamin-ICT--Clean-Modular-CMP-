package com.tamin.taminhamrah.useCases.inspection

import com.tamin.taminhamrah.model.inspection.BranchDN
import com.tamin.taminhamrah.model.inspection.BranchListDN
import com.tamin.taminhamrah.repository.inspection.FakeInspectionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetBranchListUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeInspectionRepository
    private lateinit var useCase: GetBranchListUseCase

    @BeforeTest
    fun setup() {
        repository = FakeInspectionRepository()
        useCase = GetBranchListUseCase(repository)
    }

    @Test
    fun invoke_returnsListFromRepository() = runTest {
        val expected = BranchListDN(
            total = 1,
            list = listOf(
                BranchDN(
                    operation = "", code = "0010", name = "یک تهران",
                    minCode = "", maxCode = "", type = "", branchAddress = "",
                    cityCode = "", status = ""
                )
            )
        )
        repository.branchesResult = expected

        val result = useCase(emptyList())

        assertEquals(expected, result)
        assertEquals(emptyList(), repository.lastBranchesFilters)
    }

    @Test
    fun invoke_onFailure_propagatesException() = runTest {
        repository.shouldThrowError = true

        assertFailsWith<RuntimeException> {
            useCase(emptyList())
        }
    }
}
