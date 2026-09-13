package com.tamin.taminhamrah.data.repository.weddingPresent

import com.tamin.taminhamrah.dataSource.weddingPresent.WeddingPresentRemoteDataSource
import com.tamin.taminhamrah.model.weddingPresent.ShortTermMarriageRequestDTO
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDN
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDTO
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentSubmitRequestDN
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FakeWeddingPresentRemoteDataSource : WeddingPresentRemoteDataSource {
    var getInfoResult: WeddingPresentInfoDTO? = WeddingPresentInfoDTO()
    var lastSubmitRequest: ShortTermMarriageRequestDTO? = null
    var calculateResult: List<String>? = listOf("1000", "2000")
    var lastCalculateTimeStamp: String? = null

    override suspend fun getWeddingPresentInfo(): WeddingPresentInfoDTO? = getInfoResult

    override suspend fun submitWeddingPresent(request: ShortTermMarriageRequestDTO) {
        lastSubmitRequest = request
    }

    override suspend fun calculateMarriageAllowance(timeStamp: String): List<String>? {
        lastCalculateTimeStamp = timeStamp
        return calculateResult
    }
}

class WeddingPresentRepositoryImplTest {

    @Test
    fun getWeddingPresentInfo_mapsDtoToDomain() = runTest {
        val fake = FakeWeddingPresentRemoteDataSource().apply {
            getInfoResult = WeddingPresentInfoDTO(
                risuid = "123",
                insuranceFirstName = "علی",
                insuranceLastName = "رضایی",
            )
        }
        val repository = WeddingPresentRepositoryImpl(fake)

        val result = repository.getWeddingPresentInfo().first()

        assertEquals("123", result.risuid)
        assertEquals("علی", result.insuranceFirstName)
        assertEquals("رضایی", result.insuranceLastName)
    }

    @Test
    fun getWeddingPresentInfo_nullResponse_emitsEmpty() = runTest {
        val fake = FakeWeddingPresentRemoteDataSource().apply {
            getInfoResult = null
        }
        val repository = WeddingPresentRepositoryImpl(fake)

        val result = repository.getWeddingPresentInfo().first()

        assertEquals(WeddingPresentInfoDN(), result)
    }

    @Test
    fun submitWeddingPresent_mapsAndCallsRemote() = runTest {
        val fake = FakeWeddingPresentRemoteDataSource()
        val repository = WeddingPresentRepositoryImpl(fake)

        repository.submitWeddingPresent(
            WeddingPresentSubmitRequestDN(
                partnerNationalId = "0098765432",
                weddingDateTimeStamp = 42L,
                info = WeddingPresentInfoDN(risuid = "123"),
            )
        ).first()

        assertEquals("0098765432", fake.lastSubmitRequest?.partnerNationalId)
        assertEquals(42L, fake.lastSubmitRequest?.weddingDateTimeStamp)
        assertEquals("123", fake.lastSubmitRequest?.shortTermRequest?.risuid)
    }

    @Test
    fun calculateMarriageAllowance_emitsRemoteList() = runTest {
        val fake = FakeWeddingPresentRemoteDataSource().apply {
            calculateResult = listOf("130300000", "130300000")
        }
        val repository = WeddingPresentRepositoryImpl(fake)

        val result = repository.calculateMarriageAllowance("42").first()

        assertEquals(listOf("130300000", "130300000"), result)
        assertEquals("42", fake.lastCalculateTimeStamp)
    }

    @Test
    fun calculateMarriageAllowance_nullResponse_emitsEmpty() = runTest {
        val fake = FakeWeddingPresentRemoteDataSource().apply {
            calculateResult = null
        }
        val repository = WeddingPresentRepositoryImpl(fake)

        val result = repository.calculateMarriageAllowance("1").first()

        assertEquals(emptyList(), result)
    }
}
