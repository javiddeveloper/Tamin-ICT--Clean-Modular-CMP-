package com.tamin.taminhamrah.apiService.funeralAllowance

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceRequestDTO
import com.tamin.taminhamrah.model.funeralAllowance.FuneralShorttermRequestDTO
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.FuneralAllowanceTestData
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FuneralAllowanceApiServiceTest : BaseApiTest() {

    @Test
    fun getFuneralAllowanceInfo_returnsInsuredAndBranchInfo() = runTest {
        val json = ApiTestUtils.createJsonResponse(dataJson = FuneralAllowanceTestData.infoSuccess)

        val apiService = createMockKtorfit(json).createFuneralAllowanceApiService()
        val response = apiService.getFuneralAllowanceInfo()

        assertEquals(200, response.status)
        val data = assertNotNull(response.data)
        assertEquals("علی", data.insuranceFirstName)
        assertEquals("1234567", data.risuid)
        assertEquals("0055667788", data.partnerNationalId)
        assertEquals("09121234567", data.mobileNumber)
        assertEquals(false, data.flag)
    }

    @Test
    fun getFuneralAllowanceInfo_withBankAccountIssue_parsesRegisteredRequest() = runTest {
        val json = ApiTestUtils.createJsonResponse(dataJson = FuneralAllowanceTestData.infoBankAccountIssueSuccess)

        val apiService = createMockKtorfit(json).createFuneralAllowanceApiService()
        val response = apiService.getFuneralAllowanceInfo()

        assertEquals(200, response.status)
        val data = assertNotNull(response.data)
        assertTrue(data.flag)
        assertEquals(998877L, data.request?.id)
        assertEquals("در انتظار اصلاح حساب", data.request?.statusName)
        assertEquals(1712000000000L, data.deathTimestamp)
    }

    @Test
    fun validateDeceased_returnsRawPositionalStringArray() = runTest {
        val json = ApiTestUtils.createJsonResponse(dataJson = FuneralAllowanceTestData.validateDeceasedSuccess)

        val apiService = createMockKtorfit(json).createFuneralAllowanceApiService()
        val response = apiService.validateDeceased("0055667788")

        assertEquals(200, response.status)
        val data = assertNotNull(response.data)
        assertEquals(14, data.size)
        assertEquals("زهرا رضایی", data[4])
        assertEquals("1", data[6])
        assertEquals("14050110", data[13])
    }

    @Test
    fun submitFuneralAllowanceRequest_returnsSuccessMessageString() = runTest {
        val json = ApiTestUtils.createJsonResponse(dataJson = FuneralAllowanceTestData.submitSuccess)

        val apiService = createMockKtorfit(json).createFuneralAllowanceApiService()
        val response = apiService.submitFuneralAllowanceRequest(sampleRequestDTO())

        assertEquals(200, response.status)
        assertEquals("درخواست شما ثبت شد", response.data?.jsonPrimitive?.content)
    }

    @Test
    fun confirmAccountCorrection_returnsSuccessMessageString() = runTest {
        val json = ApiTestUtils.createJsonResponse(dataJson = FuneralAllowanceTestData.confirmAccountCorrectionSuccess)

        val apiService = createMockKtorfit(json).createFuneralAllowanceApiService()
        val response = apiService.confirmAccountCorrection("998877")

        assertEquals(200, response.status)
        assertEquals("درخواست مجدداً ثبت شد", response.data?.jsonPrimitive?.content)
    }

    private fun sampleRequestDTO() = FuneralAllowanceRequestDTO(
        deadNationalId = "0055667788",
        shorttermRequest = FuneralShorttermRequestDTO(
            branchCode = "10",
            branchName = "شعبه مرکزی",
            insuranceFirstName = "علی",
            insuranceLastName = "رضایی",
            mobileNumber = "09121234567",
            nationalCode = "0012345678",
            requestHelpType = "07",
            risuid = "1234567",
        ),
    )
}
