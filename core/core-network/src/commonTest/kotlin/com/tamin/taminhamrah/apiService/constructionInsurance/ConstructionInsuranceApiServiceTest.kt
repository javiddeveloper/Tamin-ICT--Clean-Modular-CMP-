package com.tamin.taminhamrah.apiService.constructionInsurance

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDTO
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDTO
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDTO
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDTO
import com.tamin.taminhamrah.tools.extractMessage
import com.tamin.taminhamrah.tools.readPdfChannel
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.ConstructionInsuranceTestData
import io.ktor.http.ContentType
import io.ktor.utils.io.readRemaining
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest
import kotlinx.io.readByteArray

/**
 * Deserialization coverage for the بیمه ساختمانی endpoints — the payloads use odd/legacy server
 * key names (`mainPelak`, `metrage`, `postulate`, `shenase`) that don't match the Kotlin property
 * names at all, and nest the workshop identity two levels down in a couple of responses. A rename
 * anywhere on that mapping fails here rather than as a blank card at runtime.
 */
class ConstructionInsuranceApiServiceTest : BaseApiTest() {

    @Test
    fun `getConstructionFiles deserializes odd server keys and the nested workshopId block`() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(dataJson = ConstructionInsuranceTestData.constructionFilesSuccess)
        )
        val apiService = ktorfit.createConstructionInsuranceApiService()

        val response = apiService.getConstructionFiles(emptyMap())

        assertEquals(200, response.status)
        val listData = assertNotNull(response.data)
        assertEquals(1, listData.total)

        val file: ConstructionFileDTO = listData.list.orEmpty().first()
        assertEquals(4_479_890_000L, file.fileNumber)
        assertEquals(10, file.mainPlaque)
        assertEquals(80, file.meterage)
        assertEquals(120_000L, file.applicationFees)
        assertEquals("51", file.debitStatusCode)
        assertEquals("123456789010", file.debitNumber)

        val workshop = assertNotNull(file.workshopInfo)
        assertEquals("14020901", workshop.workshopId)
        assertEquals("6400", workshop.brhCode)
    }

    @Test
    fun `getBeneficiariesWorkshop returns owner and applicant rows`() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(dataJson = ConstructionInsuranceTestData.beneficiariesSuccess)
        )
        val apiService = ktorfit.createConstructionInsuranceApiService()

        val response = apiService.getBeneficiariesWorkshop(emptyMap())

        val listData = assertNotNull(response.data)
        assertEquals(1, listData.total)

        val beneficiary: BeneficiaryConstructionDTO = listData.list.orEmpty().first()
        assertEquals("01", beneficiary.ownerType)
        assertEquals("علی", beneficiary.name)
        assertEquals("توکلی", beneficiary.lastName)
        assertEquals("09123456700", beneficiary.mobile)
    }

    @Test
    fun `getPaymentSheetConstructionInfo returns the nested buildingRequest summary`() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(dataJson = ConstructionInsuranceTestData.paymentSheetsSuccess)
        )
        val apiService = ktorfit.createConstructionInsuranceApiService()

        val response = apiService.getPaymentSheetConstructionInfo("123456789010")

        val listData = assertNotNull(response.data)
        val sheet: PaymentSheetConstructionFileDTO = listData.list.orEmpty().first()
        assertEquals("36001234560", sheet.paymentCode)
        assertEquals(500_000L, sheet.paymentSheetAmount)

        val buildingRequest = assertNotNull(sheet.buildingRequest)
        assertEquals(1_850_000L, buildingRequest.totalPayment)
        assertEquals("6400", buildingRequest.workshopInfo?.brhCode)
    }

    @Test
    fun `getCertificatePaymentSheetPdf streams pdf bytes`() = runTest {
        val pdfBytes = "%PDF-1.4 mock-payment-certificate".encodeToByteArray()
        val ktorfit = createMockKtorfit(content = pdfBytes, contentType = ContentType.Application.Pdf)
        val apiService = ktorfit.createConstructionInsuranceApiService()

        val statement = apiService.getCertificatePaymentSheetPdf("123456789010", "6400")
        val bytes = statement.readPdfChannel().readRemaining().readByteArray()

        assertEquals(pdfBytes.toList(), bytes.toList())
    }

    @Test
    fun `issuancePaymentSheet returns the backend confirmation message`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(dataJson = "\"برگه پرداخت با موفقیت صادر شد.\"")
        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createConstructionInsuranceApiService()

        val response = apiService.issuancePaymentSheet("123456789010")

        assertEquals("برگه پرداخت با موفقیت صادر شد.", response.extractMessage())
    }

    @Test
    fun `getInstallmentLetterList returns debit letters`() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(dataJson = ConstructionInsuranceTestData.installmentLettersSuccess)
        )
        val apiService = ktorfit.createConstructionInsuranceApiService()

        val response = apiService.getInstallmentLetterList("14020901", "6400", emptyMap())

        val listData = assertNotNull(response.data)
        val letter: InstallmentLetterDTO = listData.list.orEmpty().first()
        assertEquals("77640000001", letter.debitNumber)
        assertEquals("قسط اول", letter.debitStepDescription)
        assertEquals(400_000L, letter.remainingAmount)
    }
}
