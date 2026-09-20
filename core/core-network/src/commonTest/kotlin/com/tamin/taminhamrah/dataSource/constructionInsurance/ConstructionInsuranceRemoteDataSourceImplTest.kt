package com.tamin.taminhamrah.dataSource.constructionInsurance

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.apiService.constructionInsurance.ConstructionInsuranceApiService
import com.tamin.taminhamrah.apiService.constructionInsurance.createConstructionInsuranceApiService
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDTO
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDTO
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDTO
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilderImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import io.ktor.client.statement.HttpStatement
import io.ktor.http.ContentType
import io.ktor.utils.io.readRemaining
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.io.readByteArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

class FakeConstructionInsuranceApiService : ConstructionInsuranceApiService {
    var constructionFilesResult: BaseDTO<ListData<ConstructionFileDTO>> =
        BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = ListData(total = 0, list = emptyList()))
    var beneficiariesResult: BaseDTO<ListData<BeneficiaryConstructionDTO>> =
        BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = ListData(total = 0, list = emptyList()))
    var paymentSheetResult: BaseDTO<ListData<PaymentSheetConstructionFileDTO>> =
        BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = ListData(total = 0, list = emptyList()))
    var issuanceResult: BaseDTO<JsonElement?> =
        BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = JsonPrimitive("OK"))
    var installmentLettersResult: BaseDTO<ListData<InstallmentLetterDTO>> =
        BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = ListData(total = 0, list = emptyList()))

    var shouldThrowException: Exception? = null
    var lastConstructionFilesParameters: Map<String, String>? = null
    var lastBeneficiariesParameters: Map<String, String>? = null
    var lastPaymentSheetDebitNumber: String? = null
    var lastIssuanceDebitNumber: String? = null
    var lastInstallmentWorkshopId: String? = null
    var lastInstallmentBranchId: String? = null
    var lastInstallmentParameters: Map<String, String>? = null

    override suspend fun getConstructionFiles(parameters: Map<String, String>): BaseDTO<ListData<ConstructionFileDTO>> {
        shouldThrowException?.let { throw it }
        lastConstructionFilesParameters = parameters
        return constructionFilesResult
    }

    override suspend fun getBeneficiariesWorkshop(
        parameters: Map<String, String>
    ): BaseDTO<ListData<BeneficiaryConstructionDTO>> {
        shouldThrowException?.let { throw it }
        lastBeneficiariesParameters = parameters
        return beneficiariesResult
    }

    override suspend fun getPaymentSheetConstructionInfo(debitNumber: String): BaseDTO<ListData<PaymentSheetConstructionFileDTO>> {
        shouldThrowException?.let { throw it }
        lastPaymentSheetDebitNumber = debitNumber
        return paymentSheetResult
    }

    override suspend fun getCertificatePaymentSheetPdf(debitNumber: String, branchCode: String): HttpStatement {
        shouldThrowException?.let { throw it }
        error("Not stubbed — covered by a live Ktorfit instance instead, see the streaming test below")
    }

    override suspend fun issuancePaymentSheet(debitNumber: String): BaseDTO<JsonElement?> {
        shouldThrowException?.let { throw it }
        lastIssuanceDebitNumber = debitNumber
        return issuanceResult
    }

    override suspend fun getInstallmentLetterList(
        workshopId: String,
        branchId: String,
        parameters: Map<String, String>
    ): BaseDTO<ListData<InstallmentLetterDTO>> {
        shouldThrowException?.let { throw it }
        lastInstallmentWorkshopId = workshopId
        lastInstallmentBranchId = branchId
        lastInstallmentParameters = parameters
        return installmentLettersResult
    }
}

class ConstructionInsuranceRemoteDataSourceImplTest : BaseApiTest() {

    private lateinit var fakeApiService: FakeConstructionInsuranceApiService
    private lateinit var dataSource: ConstructionInsuranceRemoteDataSourceImpl

    @BeforeTest
    fun setup() {
        fakeApiService = FakeConstructionInsuranceApiService()
        dataSource = ConstructionInsuranceRemoteDataSourceImpl(
            apiService = fakeApiService,
            queryBuilder = ApiQueryBuilderImpl(),
            errorParser = ErrorParserImpl(),
        )
    }

    @Test
    fun getConstructionFiles_success_returnsListData() = runTest {
        val expected = listOf(ConstructionFileDTO(fileNumber = 4_479_890_000L, debitStatusCode = "51"))
        fakeApiService.constructionFilesResult =
            BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = ListData(total = 1, list = expected))

        val result = dataSource.getConstructionFiles(ApiQueryParamDN())

        assertEquals(expected, result.list)
    }

    @Test
    fun getConstructionFiles_doesNotSendPositionParam() = runTest {
        // Legacy never actually sent this — it only kept it in a local map. Sending it was a
        // porting mistake (MR !244 review item 10), not a real backend requirement.
        dataSource.getConstructionFiles(ApiQueryParamDN())

        assertEquals(null, fakeApiService.lastConstructionFilesParameters?.get("position"))
    }

    @Test
    fun getBeneficiariesWorkshop_success_forwardsFilters() = runTest {
        val expected = listOf(BeneficiaryConstructionDTO(name = "علی"))
        fakeApiService.beneficiariesResult =
            BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = ListData(total = 1, list = expected))
        val query = ApiQueryParamDN(
            filters = listOf(ApiFilterDN(FilterProperty.REQ_NO, "123", FilterOperator.EQ))
        )

        val result = dataSource.getBeneficiariesWorkshop(query)

        assertEquals(expected, result.list)
        assertNotNull(fakeApiService.lastBeneficiariesParameters)
    }

    @Test
    fun getBeneficiariesWorkshop_doesNotSendPositionParam() = runTest {
        dataSource.getBeneficiariesWorkshop(ApiQueryParamDN())

        assertEquals(null, fakeApiService.lastBeneficiariesParameters?.get("position"))
    }

    @Test
    fun getPaymentSheetConstructionInfo_success_forwardsDebitNumber() = runTest {
        val expected = listOf(PaymentSheetConstructionFileDTO(orderNumber = "1"))
        fakeApiService.paymentSheetResult =
            BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = ListData(total = 1, list = expected))

        val result = dataSource.getPaymentSheetConstructionInfo("123456789010")

        assertEquals(expected, result.list)
        assertEquals("123456789010", fakeApiService.lastPaymentSheetDebitNumber)
    }

    @Test
    fun issuancePaymentSheet_success_returnsMessage() = runTest {
        fakeApiService.issuanceResult =
            BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = JsonPrimitive("صادر شد"))

        val result = dataSource.issuancePaymentSheet("123456789010")

        assertEquals("صادر شد", result)
        assertEquals("123456789010", fakeApiService.lastIssuanceDebitNumber)
    }

    @Test
    fun getInstallmentLetterList_success_forwardsWorkshopAndBranch() = runTest {
        val expected = listOf(InstallmentLetterDTO(debitNumber = "77640000001"))
        fakeApiService.installmentLettersResult =
            BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = ListData(total = 1, list = expected))

        val result = dataSource.getInstallmentLetterList("14020901", "6400", ApiQueryParamDN())

        assertEquals(expected, result.list)
        assertEquals("14020901", fakeApiService.lastInstallmentWorkshopId)
        assertEquals("6400", fakeApiService.lastInstallmentBranchId)
    }

    @Test
    fun getInstallmentLetterList_doesNotSendPositionParam() = runTest {
        dataSource.getInstallmentLetterList("14020901", "6400", ApiQueryParamDN())

        assertEquals(null, fakeApiService.lastInstallmentParameters?.get("position"))
    }

    @Test
    fun getCertificatePaymentSheetPdf_success_readsPdfBytes() = runTest {
        val pdfBytes = "%PDF-1.4 mock-certificate".encodeToByteArray()
        val ktorfit = createMockKtorfit(content = pdfBytes, contentType = ContentType.Application.Pdf)
        val liveDataSource = ConstructionInsuranceRemoteDataSourceImpl(
            apiService = ktorfit.createConstructionInsuranceApiService(),
            queryBuilder = ApiQueryBuilderImpl(),
            errorParser = ErrorParserImpl(),
        )

        val result = liveDataSource.getCertificatePaymentSheetPdf("123456789010", "6400")

        val channel = assertNotNull(result.pdf?.pdf)
        val bytes = channel.readRemaining().readByteArray()
        assertEquals(pdfBytes.toList(), bytes.toList())
    }

    @Test
    fun getConstructionFiles_onNetworkError_throwsParsedTaminApiException() = runTest {
        fakeApiService.shouldThrowException = TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)

        val exception = assertFailsWith<TaminApiException> {
            dataSource.getConstructionFiles(ApiQueryParamDN())
        }

        assertEquals("خطای اتصال", exception.title)
    }

    @Test
    fun issuancePaymentSheet_onNetworkError_throwsParsedTaminApiException() = runTest {
        fakeApiService.shouldThrowException = TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)

        val exception = assertFailsWith<TaminApiException> {
            dataSource.issuancePaymentSheet("123456789010")
        }

        assertEquals("خطای اتصال", exception.title)
    }

    @Test
    fun getInstallmentLetterList_onNetworkError_throwsParsedTaminApiException() = runTest {
        fakeApiService.shouldThrowException = TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)

        val exception = assertFailsWith<TaminApiException> {
            dataSource.getInstallmentLetterList("14020901", "6400", ApiQueryParamDN())
        }

        assertEquals("خطای اتصال", exception.title)
    }

    @Test
    fun getConstructionFiles_onCancellation_propagatesCancellationRatherThanWrappingIt() = runTest {
        fakeApiService.shouldThrowException = CancellationException("left the screen")

        assertFailsWith<CancellationException> {
            dataSource.getConstructionFiles(ApiQueryParamDN())
        }
    }
}
