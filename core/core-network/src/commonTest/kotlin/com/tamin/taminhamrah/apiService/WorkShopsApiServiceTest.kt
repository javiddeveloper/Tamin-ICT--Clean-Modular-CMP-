package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.model.workshop.WorkshopDebitDTO
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
        val apiService = ktorfit.create<WorkShopsApiService>()

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
}
