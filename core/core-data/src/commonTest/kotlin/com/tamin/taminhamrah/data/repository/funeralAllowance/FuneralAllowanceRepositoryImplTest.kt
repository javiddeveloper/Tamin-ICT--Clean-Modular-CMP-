package com.tamin.taminhamrah.data.repository.funeralAllowance

import com.tamin.taminhamrah.dataSource.funeralAllowance.FuneralAllowanceRemoteDataSource
import com.tamin.taminhamrah.model.funeralAllowance.DeceasedValidationDTO
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDTO
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceRequestDTO
import com.tamin.taminhamrah.model.funeralAllowance.SubmitFuneralAllowanceParamsDN
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class FakeFuneralAllowanceRemoteDataSource : FuneralAllowanceRemoteDataSource {
    var infoResult: FuneralAllowanceInfoDTO = FuneralAllowanceInfoDTO()
    var validateResult: DeceasedValidationDTO = DeceasedValidationDTO()
    var submitResult: String = "درخواست شما ثبت شد"
    var confirmResult: String = "درخواست شما ثبت شد"

    var shouldThrowError: Exception? = null
    var lastValidateNationalCode: String? = null
    var lastSubmitRequest: FuneralAllowanceRequestDTO? = null
    var lastConfirmRequestId: String? = null

    override suspend fun getFuneralAllowanceInfo(): FuneralAllowanceInfoDTO {
        shouldThrowError?.let { throw it }
        return infoResult
    }

    override suspend fun validateDeceased(nationalCode: String): DeceasedValidationDTO {
        shouldThrowError?.let { throw it }
        lastValidateNationalCode = nationalCode
        return validateResult
    }

    override suspend fun submitFuneralAllowanceRequest(request: FuneralAllowanceRequestDTO): String {
        shouldThrowError?.let { throw it }
        lastSubmitRequest = request
        return submitResult
    }

    override suspend fun confirmAccountCorrection(requestId: String): String {
        shouldThrowError?.let { throw it }
        lastConfirmRequestId = requestId
        return confirmResult
    }
}

class FuneralAllowanceRepositoryImplTest {

    private lateinit var remoteDataSource: FakeFuneralAllowanceRemoteDataSource
    private lateinit var repository: FuneralAllowanceRepositoryImpl

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeFuneralAllowanceRemoteDataSource()
        repository = FuneralAllowanceRepositoryImpl(remoteDataSource)
    }

    @Test
    fun getFuneralAllowanceInfo_returnsMappedDomainModel() = runTest {
        remoteDataSource.infoResult = FuneralAllowanceInfoDTO(
            insuranceFirstName = "علی",
            insuranceLastName = "رضایی",
            risuid = "1234567",
            partnerNationalId = "0055667788",
            nationalCode = "0012345678",
            flag = false,
        )

        val result = repository.getFuneralAllowanceInfo()

        assertEquals("علی", result.firstName)
        assertEquals("علی رضایی", result.fullName)
        assertEquals("1234567", result.insuranceNumber)
        assertEquals("0055667788", result.deceasedNationalId)
        assertEquals(false, result.hasBankAccountIssue)
    }

    @Test
    fun validateDeceased_mapsDtoToDomain() = runTest {
        remoteDataSource.validateResult = DeceasedValidationDTO(
            fullName = "زهرا رضایی",
            relationship = "همسر",
            isEligible = true,
            message = "دارای شرایط",
        )

        val result = repository.validateDeceased("0055667788")

        assertEquals("0055667788", remoteDataSource.lastValidateNationalCode)
        assertTrue(result.isEligible)
        assertEquals("زهرا رضایی", result.deceasedFullName)
        assertEquals("همسر", result.relationship)
    }

    @Test
    fun validateDeceased_passesThroughIneligible() = runTest {
        remoteDataSource.validateResult = DeceasedValidationDTO(isEligible = false)

        val result = repository.validateDeceased("0055667788")

        assertTrue(!result.isEligible)
    }

    @Test
    fun submitFuneralAllowanceRequest_mapsParamsToDtoAndReturnsRemoteMessage() = runTest {
        remoteDataSource.submitResult = "درخواست با موفقیت ثبت شد"

        val result = repository.submitFuneralAllowanceRequest(
            SubmitFuneralAllowanceParamsDN(
                deceasedNationalId = "0055667788",
                branchCode = "10",
                branchName = "شعبه مرکزی",
                insuranceFirstName = "علی",
                insuranceLastName = "رضایی",
                mobileNumber = "09121234567",
                nationalCode = "0012345678",
                insuranceNumber = "1234567",
            )
        )

        assertEquals("درخواست با موفقیت ثبت شد", result)
        assertEquals("0055667788", remoteDataSource.lastSubmitRequest?.deadNationalId)
        assertEquals("10", remoteDataSource.lastSubmitRequest?.shorttermRequest?.branchCode)
        assertEquals("1234567", remoteDataSource.lastSubmitRequest?.shorttermRequest?.risuid)
    }

    @Test
    fun confirmAccountCorrection_forwardsRequestIdAndReturnsRemoteMessage() = runTest {
        remoteDataSource.confirmResult = "درخواست مجدداً ثبت شد"

        val result = repository.confirmAccountCorrection("998877")

        assertEquals("درخواست مجدداً ثبت شد", result)
        assertEquals("998877", remoteDataSource.lastConfirmRequestId)
    }

    @Test
    fun getFuneralAllowanceInfo_onError_propagatesException() = runTest {
        remoteDataSource.shouldThrowError = RuntimeException("Network failure")

        val error = assertFailsWith<RuntimeException> { repository.getFuneralAllowanceInfo() }

        assertEquals("Network failure", error.message)
    }
}
