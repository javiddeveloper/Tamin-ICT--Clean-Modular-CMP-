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

    override suspend fun getWeddingPresentInfo(): WeddingPresentInfoDTO? = getInfoResult

    override suspend fun submitWeddingPresent(request: ShortTermMarriageRequestDTO) {
        lastSubmitRequest = request
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
}
