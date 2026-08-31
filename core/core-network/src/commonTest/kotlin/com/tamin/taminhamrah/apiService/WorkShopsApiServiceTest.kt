package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDTO
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.WorkshopTestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * What these cover is deserialization, not routing: the workshop payloads nest their most-read
 * fields two levels down (`workshop.branch.code`, `workshop.workshopStatus.workshopStatusCode`)
 * and spell some keys in ways no formatter would choose (`workshoptypeDesc`). A rename anywhere
 * on that path fails here rather than as a blank card at runtime.
 */
class WorkShopsApiServiceTest : BaseApiTest() {

    @Test
    fun `employer agreement carries the nested workshop the card reads`() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(dataJson = WorkshopTestData.employerAgreementSuccess)
        )
        val apiService = ktorfit.createWorkShopsApiService()

        val response = apiService.getAllEmployerAgreementByNationalId(emptyMap())

        assertEquals(200, response.status)
        val listData = assertNotNull(response.data)
        assertEquals(1, listData.total)

        val agreement: EmployerAgreementDTO = listData.list.orEmpty().first()
        assertEquals("13991203", agreement.startDate)

        val workshop = assertNotNull(agreement.workshop)
        // Identity — both halves, and the branch code that is not the branch office code.
        assertEquals("1071410004", workshop.workshopId)
        assertEquals("123", workshop.branchCode)
        // The nested objects the redesigned card is built from.
        assertEquals("0960", workshop.branch?.code)
        assertEquals("شعبه یک تهران", workshop.branch?.organizationName)
        assertEquals("حقیقی", workshop.character?.characterDesc)
        // Lower-case `t` is the server's spelling; correcting it stops this deserializing.
        assertEquals("خدماتی", workshop.workshopType?.workshopTypeDesc)
        assertEquals("01", workshop.workshopStatus?.workshopStatusCode)
        assertEquals("فعال", workshop.workshopStatus?.workshopStatusDesc)
    }

    @Test
    fun `workshop debit list returns the payable debt shape`() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(dataJson = WorkshopTestData.workshopDebitSuccess)
        )
        val apiService = ktorfit.createWorkShopsApiService()

        val response = apiService.getWorkshopDebitList(
            workshopId = "1071410004",
            branchCode = "123",
            queries = emptyMap(),
        )

        val listData = assertNotNull(response.data)
        assertEquals(2, listData.total)

        val debts: List<WorkShopDebtDTO> = listData.list.orEmpty()
        assertEquals(2, debts.size)

        val unobjected = debts.first()
        assertEquals("1070040032789", unobjected.debitNumber)
        assertEquals("14040115", unobjected.orderRecipeDate)
        assertEquals(1348557L, unobjected.debitAmount)
        assertEquals("02100014", unobjected.peymanSequence)
        // No objection filed yet: this is what decides the row offers filing rather than viewing.
        assertNull(unobjected.seqNo)

        val objected = debts[1]
        assertEquals(987L, objected.seqNo)
        assertEquals("12345", objected.primaryVoteNumber)
    }

    @Test
    fun `payment sheets deserialize with epoch dates and status codes`() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(dataJson = WorkshopTestData.paymentSheetsSuccess)
        )
        val apiService = ktorfit.createWorkShopsApiService()

        val response = apiService.getWorkshopPaymentSheets(emptyMap())

        val listData = assertNotNull(response.data)
        val sheets: List<PaymentSheetDTO> = listData.list.orEmpty()
        assertNotNull(sheets.firstOrNull())
    }

    @Test
    fun `debt inquiry keeps its amounts as sent`() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(dataJson = WorkshopTestData.workshopDebtInquirySuccess)
        )
        val apiService = ktorfit.createWorkShopsApiService()

        val response = apiService.getWorkshopDebtInquiry(
            workshopId = "1071410004",
            branchCode = "123",
        )

        val inquiry = assertNotNull(response.data)
        // Strings, not numbers: the service answers some inquiries with words instead of figures.
        assertNotNull(inquiry.definitiveDebt)
    }
}
