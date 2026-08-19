package com.tamin.taminhamrah.data.repository.calculateWagePension

import com.tamin.taminhamrah.dataSource.calculateWagePension.CalculateWagePensionRemoteDataSource
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDTO
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDTO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateWagePensionRepositoryImplTest {

    private val repository = CalculateWagePensionRepositoryImpl(FakeCalculateWagePensionRemoteDataSource())

    @Test
    fun `getPersonalInfo maps dto to domain`() = runTest {
        val result = repository.getPersonalInfo().first()

        assertEquals("12345", result.branchCode)
        assertEquals("9876543210", result.insuranceNumber)
    }

    @Test
    fun `isMultipleWorkshops maps dto to domain`() = runTest {
        val result = repository.isMultipleWorkshops("12345", "9876543210").first()

        assertEquals(1, result.result)
    }

    @Test
    fun `calculateMultipleWorkshops maps dto to domain`() = runTest {
        val result = repository.calculateMultipleWorkshops("12345", "9876543210").first()

        assertEquals(25_000_000, result.result)
    }
}

private class FakeCalculateWagePensionRemoteDataSource : CalculateWagePensionRemoteDataSource {
    override suspend fun getPersonalInfo() = MultipleWorkshopPersonalInfoDTO(
        organizationId = "12345",
        insuranceId = "9876543210",
        branch = "Tehran Main"
    )

    override suspend fun isMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ) = MultipleWorkshopResultDTO(result = 1)

    override suspend fun calculateMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ) = MultipleWorkshopResultDTO(result = 25_000_000)
}
