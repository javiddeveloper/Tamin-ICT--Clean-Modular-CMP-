package com.tamin.taminhamrah.data.repository.fractionContract

import com.tamin.taminhamrah.dataSource.fractionContract.FractionContractRemoteDataSource
import com.tamin.taminhamrah.model.fractionContract.FractionContractResultDTO
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityContractDTO
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityDTO
import com.tamin.taminhamrah.model.fractionContract.FractionPremiumTypeDTO
import com.tamin.taminhamrah.model.fractionContract.MakeFractionContractRequestDTO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FakeFractionContractRemoteDataSource : FractionContractRemoteDataSource {
    var checkAgeAndHistoryResult: FractionEligibilityDTO? = null
    var makeFractionContractResult: FractionContractResultDTO =
        FractionContractResultDTO(contractNumber = 1L, contractDate = 2L)
    var lastRequest: MakeFractionContractRequestDTO? = null

    override suspend fun checkAgeAndHistory(): FractionEligibilityDTO? = checkAgeAndHistoryResult

    override suspend fun makeFractionContract(
        request: MakeFractionContractRequestDTO,
    ): FractionContractResultDTO {
        lastRequest = request
        return makeFractionContractResult
    }
}

class FractionContractRepositoryImplTest {

    @Test
    fun checkAgeAndHistory_mapsDtoToDomain() = runTest {
        val fake = FakeFractionContractRemoteDataSource().apply {
            checkAgeAndHistoryResult = FractionEligibilityDTO(
                isInsurance = true,
                checkFractionMonthStatus = "1",
                eligibilityStatus = 2,
                newAge = "250101",
                history = 120,
                city = "تهران",
                provinceName = "تهران",
                provinceCode = "01",
                organizationAddress = "شعبه ۱",
                contract = FractionEligibilityContractDTO(
                    insuranceId = "123",
                    branchCode = "0101",
                    premiumTypeCode = "38",
                    premiumType = FractionPremiumTypeDTO(insuranceTypeCode = "01"),
                    contractNumber = "555",
                ),
            )
        }
        val repository = FractionContractRepositoryImpl(fake)

        val result = repository.checkAgeAndHistory().first()

        assertEquals(true, result?.isInsurance)
        assertEquals("1", result?.checkFractionMonthStatus)
        assertEquals(2, result?.eligibilityStatus)
        assertEquals("123", result?.insuranceId)
        assertEquals("01", result?.insuranceTypeCode)
        assertEquals("555", result?.contractNumber)
    }

    @Test
    fun checkAgeAndHistory_nullResponse_emitsNull() = runTest {
        val fake = FakeFractionContractRemoteDataSource().apply {
            checkAgeAndHistoryResult = null
        }
        val repository = FractionContractRepositoryImpl(fake)

        val result = repository.checkAgeAndHistory().first()

        assertNull(result)
    }

    @Test
    fun makeFractionContract_mapsResult() = runTest {
        val fake = FakeFractionContractRemoteDataSource().apply {
            makeFractionContractResult = FractionContractResultDTO(
                contractNumber = 987L,
                contractDate = 1710000000000L,
            )
        }
        val repository = FractionContractRepositoryImpl(fake)

        val result = repository.makeFractionContract().first()

        assertEquals(987L, result.contractNumber)
        assertEquals(1710000000000L, result.contractDate)
        assertEquals("this.premium", fake.lastRequest?.premium)
    }
}
