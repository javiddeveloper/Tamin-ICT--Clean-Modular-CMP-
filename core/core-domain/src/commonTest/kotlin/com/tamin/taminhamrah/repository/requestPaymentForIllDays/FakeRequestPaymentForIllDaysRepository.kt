package com.tamin.taminhamrah.repository.requestPaymentForIllDays

import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRequestPaymentForIllDaysRepository : RequestPaymentForIllDaysRepository {
    var shouldThrowError: Boolean = false
    var error: Throwable = RuntimeException("failed")
    var lastRequest: SaveShortTermIllnessRequestDN? = null

    override fun getLatestInsuranceInfo(): Flow<IllDaysInsuredMainInfoDN?> = flow {
        if (shouldThrowError) throw error
        emit(
            IllDaysInsuredMainInfoDN(
                risuid = "1234567890",
                nationalCode = "0012345678",
                firstName = "Ali",
                lastName = "Rezaei",
                mobileNumber = "0912",
                genderCode = "1",
                branchCode = "0100",
                branchName = "Branch",
                bankAccount = "123",
                bankName = "Bank",
                insuranceTypeDesc = "Type",
                insuranceStatusDesc = "Active",
                serviceDateTimeStamp = 100L,
                branchWorkshops = emptyList(),
            )
        )
    }

    override fun getCovidResult(): Flow<CovidResultDN> = flow {
        if (shouldThrowError) throw error
        emit(
            CovidResultDN(
                startDateTimeStamp = "1700000000",
                endDateTimeStamp = "1700086400",
                timestamps = listOf("1700000000", "1700086400"),
            )
        )
    }

    override fun calcIllness(
        startDateTimeStamp: String,
        endDateTimeStamp: String,
        maritalStatus: String,
    ): Flow<List<String>> = flow {
        if (shouldThrowError) throw error
        emit(listOf("1000000"))
    }

    override fun sendRequestForIllDay(
        request: SaveShortTermIllnessRequestDN
    ): Flow<String?> = flow {
        lastRequest = request
        if (shouldThrowError) throw error
        emit("success")
    }
}
