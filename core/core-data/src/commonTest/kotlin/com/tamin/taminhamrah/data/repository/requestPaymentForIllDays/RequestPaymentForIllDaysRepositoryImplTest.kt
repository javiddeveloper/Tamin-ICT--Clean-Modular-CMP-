package com.tamin.taminhamrah.data.repository.requestPaymentForIllDays

import com.tamin.taminhamrah.dataSource.requestPaymentForIllDays.RequestPaymentForIllDaysRemoteDataSource
import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultListDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysRequestFileDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysShortTermResultDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessResponseDTO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RequestPaymentForIllDaysRepositoryImplTest {

    private val repository = RequestPaymentForIllDaysRepositoryImpl(
        FakeRequestPaymentForIllDaysRemoteDataSource()
    )

    @Test
    fun getLatestInsuranceInfo_mapsDtoToDomain() = runTest {
        val result = repository.getLatestInsuranceInfo().first()

        assertEquals("1234567890", result?.risuid)
        assertEquals("Ali", result?.firstName)
    }

    @Test
    fun getCovidResult_mapsTimestamps() = runTest {
        val result = repository.getCovidResult().first()

        assertEquals("1700000000", result.startDateTimeStamp)
        assertEquals("1700086400", result.endDateTimeStamp)
    }

    @Test
    fun sendRequestForIllDay_mapsResultMessage() = runTest {
        val result = repository.sendRequestForIllDay(
            SaveShortTermIllnessRequestDN(
                doctorId = null,
                doctorName = null,
                startDateTimeStamp = null,
                endDateTimeStamp = null,
                illnessKind = "2",
                workStatus = "2",
                provinceCode = "",
                cityCode = "",
                branchCode = null,
                branchName = null,
                insuranceFirstName = null,
                insuranceLastName = null,
                mobileNumber = null,
                nationalCode = null,
                risuid = null,
                serviceDateTimeStamp = null,
                requestFileList = emptyList(),
            )
        ).first()

        assertEquals("success", result)
    }

    @Test
    fun getLatestInsuranceInfo_nullResponse_emitsNull() = runTest {
        val repository = RequestPaymentForIllDaysRepositoryImpl(
            FakeRequestPaymentForIllDaysRemoteDataSource(returnNullInsured = true)
        )

        assertNull(repository.getLatestInsuranceInfo().first())
    }
}

private class FakeRequestPaymentForIllDaysRemoteDataSource(
    private val returnNullInsured: Boolean = false,
) : RequestPaymentForIllDaysRemoteDataSource {

    override suspend fun getLatestInsuranceInfo(): IllDaysInsuredMainInfoDTO? {
        if (returnNullInsured) return null
        return IllDaysInsuredMainInfoDTO(
            risuid = "1234567890",
            insuranceFirstName = "Ali",
            insuranceLastName = "Rezaei",
        )
    }

    override suspend fun getCovidResult(): CovidResultListDTO? {
        return CovidResultListDTO(list = listOf("1700000000", "1700086400"))
    }

    override suspend fun sendRequestForIllDay(
        request: SaveShortTermIllnessRequestDTO
    ): SaveShortTermIllnessResponseDTO? {
        return SaveShortTermIllnessResponseDTO(
            shorttermRequest = IllDaysShortTermResultDTO(resultMessage = "success")
        )
    }
}
