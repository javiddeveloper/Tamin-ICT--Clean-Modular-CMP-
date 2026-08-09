package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.model.workshop.WorkshopDebitDTO
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDTO
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDTO
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDTO
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.WorkshopTestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class WorkShopsApiServiceTest : BaseApiTest() {

    @Test
    fun `getWorkshopDebit should return workshop debit list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = WorkshopTestData.workshopDebitSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createWorkShopsApiService()

        val response = apiService.getWorkshopDebit(
            workshopId = "1071410004", 
            branchCode = "123", 
            queries = emptyMap()
        )

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val listData = response.data
        assertNotNull(listData)
        assertEquals(2, listData.total)

        val debits: List<WorkshopDebitDTO> = listData.list.orEmpty()
        assertEquals(2, debits.size)

        val firstDebit = debits.first()
        assertEquals("1070040032789", firstDebit.debitNumber)
        assertEquals("05", firstDebit.debitCreateReasonCode)
        assertEquals("محاسبات  رياضي فني", firstDebit.debitCreateReasonDesc)
        assertEquals("14040101", firstDebit.debitStartDate)
        assertEquals(1348557L, firstDebit.debitAmount)
        
        val secondDebit = debits[1]
        assertEquals("1070880064198", secondDebit.debitNumber)
        assertEquals("24", secondDebit.debitCreateReasonCode)
        assertEquals("سند 4% بدهي حق بيمه سخت و زيان آور - بخشنامه 49/5", secondDebit.debitCreateReasonDesc)
        assertEquals(18546691L, secondDebit.debitAmount)
    }

    @Test
    fun `getWorkshopDebtInquiry should return workshop debt inquiry`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = WorkshopTestData.workshopDebtInquirySuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createWorkShopsApiService()

        val response = apiService.getWorkshopDebtInquiry(
            workshopId = "1071410004",
            branchCode = "1070"
        )

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val data = response.data
        assertNotNull(data)

        assertEquals("1071410004", data.workshopId)
        assertEquals("1070", data.branchCode)
        assertEquals("سنگ بري سعيد", data.workshopName)
        assertEquals("کارگاه دارای بدهی قطعی", data.result)
        assertEquals("19895251", data.amount1)
        assertEquals("1405/04/01", data.sDate)
    }

    @Test
    fun `getAllEmployerAgreementByNationalId should return agreement list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(WorkshopTestData.employerAgreementSuccess)
        val apiService = createMockKtorfit(jsonResponse).create<WorkShopsApiService>()
        val response = apiService.getAllEmployerAgreementByNationalId(emptyMap())

        assertEquals(200, response.status)
        val list = response.data?.list.orEmpty()
        assertEquals(1, list.size)
        assertEquals("1071410004", list.first().workshop?.workshopId)
    }

    @Test
    fun `getWorkshopPaymentSheets should return payment sheets`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(WorkshopTestData.paymentSheetsSuccess)
        val apiService = createMockKtorfit(jsonResponse).create<WorkShopsApiService>()
        val response = apiService.getWorkshopPaymentSheets(emptyMap())

        assertEquals(200, response.status)
        val list = response.data?.list.orEmpty()
        assertEquals(1, list.size)
        assertEquals("9900020917749", list.first().orderNo)
    }

    @Test
    fun `getWorkshopObjectionableDebitList should return objectionable debits`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(WorkshopTestData.objectionableDebitSuccess)
        val apiService = createMockKtorfit(jsonResponse).create<WorkShopsApiService>()
        val response = apiService.getWorkshopObjectionableDebitList("1071410004", "123", emptyMap())

        assertEquals(200, response.status)
        val list = response.data?.list.orEmpty()
        assertEquals(1, list.size)
        assertEquals("1070010999716", list.first().debitNumber)
    }

    @Test
    fun `getWorkshopRecentlyAddedMembers should return newly added members`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(WorkshopTestData.recentlyAddedMembersSuccess)
        val apiService = createMockKtorfit(jsonResponse).create<WorkShopsApiService>()
        val response = apiService.getWorkshopRecentlyAddedMembers(emptyMap())

        assertEquals(200, response.status)
        val list = response.data?.list.orEmpty()
        assertEquals(1, list.size)
        assertEquals("12345678", list.first().insuranceId)
    }

    @Test
    fun `getWorkshopsDebtsList should return management debits`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(WorkshopTestData.managementDebitSuccess)
        val apiService = createMockKtorfit(jsonResponse).create<WorkShopsApiService>()
        val response = apiService.getWorkshopsDebtsList("1071410004", "123", emptyMap())

        assertEquals(200, response.status)
        val list = response.data?.list.orEmpty()
        assertEquals(1, list.size)
    }

    @Test
    fun `getWorkshopMembers should return workshop members`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(WorkshopTestData.workshopMembersSuccess)
        val apiService = createMockKtorfit(jsonResponse).create<WorkShopsApiService>()
        val response = apiService.getWorkshopMembers(emptyMap())

        assertEquals(200, response.status)
        val list = response.data?.list.orEmpty()
        assertEquals(1, list.size)
        assertEquals("1400/01/01", list.first().leavingWorkDate)
    }

    @Test
    fun `getWorkshopStackHolders should return stakeholders`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(WorkshopTestData.workshopStackholdersSuccess)
        val apiService = createMockKtorfit(jsonResponse).create<WorkShopsApiService>()
        val response = apiService.getWorkshopStackHolders(emptyMap())

        assertEquals(200, response.status)
        val list = response.data?.list.orEmpty()
        assertEquals(1, list.size)
        assertEquals("0000000000", list.first().nationalId)
    }
}
