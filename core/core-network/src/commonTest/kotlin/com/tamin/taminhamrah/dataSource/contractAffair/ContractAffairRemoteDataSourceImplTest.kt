package com.tamin.taminhamrah.dataSource.contractAffair

import com.tamin.taminhamrah.tools.FakeIOException
import com.tamin.taminhamrah.apiService.contractAffair.ContractAffairApiService
import com.tamin.taminhamrah.model.contractAffair.CancelContractRequestDTO
import com.tamin.taminhamrah.model.contractAffair.ContractDTO
import com.tamin.taminhamrah.model.contractAffair.ContractDebitDTO
import com.tamin.taminhamrah.model.contractAffair.ContractPremiumType
import com.tamin.taminhamrah.model.contractAffair.ContractStateDTO
import com.tamin.taminhamrah.model.contractAffair.FreelanceLastPaymentDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ContractAffairRemoteDataSourceImplTest {

    private fun dataSource(api: FakeApi) = ContractAffairRemoteDataSourceImpl(
        contractAffairApiService = api,
        apiQueryBuilder = FakeApiQueryBuilder(),
        errorParser = FakeErrorParser(),
    )

    @Test
    fun `getContracts returns the extracted list envelope`() = runTest {
        val api = FakeApi(contracts = ListData(total = 3, list = emptyList()))

        val result = dataSource(api).getContracts(ApiQueryParamDN())

        assertEquals(3, result.total)
    }

    @Test
    fun `getContracts maps any failure to NO_CONNECTION_ERROR`() = runTest {
        val api = FakeApi(shouldThrow = true)

        val error = assertFailsWith<TaminApiException> {
            dataSource(api).getContracts(ApiQueryParamDN())
        }
        assertEquals("NO_CONNECTION_ERROR", error.title)
    }

    @Test
    fun `getContractStates returns the extracted reasons`() = runTest {
        val api = FakeApi(
            states = ListData(
                total = 1,
                list = listOf(
                    ContractStateDTO(selfIsuContStatDesc = "ابطال به درخواست", selfIsuContStatCode = 5),
                ),
            ),
        )

        val result = dataSource(api).getContractStates(ApiQueryParamDN(limit = 100))

        assertEquals(5, result.list?.single()?.selfIsuContStatCode)
    }

    @Test
    fun `cancelContract hits the optional endpoint for optional insurance`() = runTest {
        val api = FakeApi()
        val request = CancelContractRequestDTO(canceldesc = "توضیحات", contractStatus = 99)

        dataSource(api).cancelContract(ContractPremiumType.OPTIONAL, stateCode = 5, request = request)

        assertEquals("optional", api.cancelVariant)
        assertEquals(5, api.cancelStateCode)
        assertEquals(request, api.cancelRequest)
    }

    @Test
    fun `cancelContract hits the freelance endpoint for freelance`() = runTest {
        val api = FakeApi()
        val request = CancelContractRequestDTO(canceldesc = null, contractStatus = 99)

        dataSource(api).cancelContract(ContractPremiumType.FREELANCE, stateCode = 7, request = request)

        assertEquals("freelance", api.cancelVariant)
        assertEquals(7, api.cancelStateCode)
    }

    @Test
    fun `cancelContract maps a network failure to NO_CONNECTION_ERROR`() = runTest {
        val api = FakeApi(shouldThrow = true)

        val error = assertFailsWith<TaminApiException> {
            dataSource(api).cancelContract(
                ContractPremiumType.OPTIONAL,
                stateCode = 5,
                request = CancelContractRequestDTO(),
            )
        }
        assertEquals("NO_CONNECTION_ERROR", error.title)
    }

    @Test
    fun `getContractPaymentHistory lifts fields out of the positional array`() = runTest {
        val row = buildJsonArray {
            add("row-type")       // [0]
            add("0012345678")     // [1] nationalId
            add("77")             // [2] insuranceId
            add("9001")           // [3] debtNumber
            add("unused")         // [4]
            add("140501")         // [5] startTermPayment
            add("140506")         // [6] endTermPayment
            add(53_866_782.0)     // [7] totalDebt
            add("14051015")       // [8] paymentDeadline
            add(53_866_782.0)     // [9] amountPayment
            add("14050610")       // [10] datePayment
            add("پرداخت شده")     // [11] statusContract
            add("وصول شده")       // [12] statusRecipient
        }
        val api = FakeApi(paymentHistory = ListData(total = 1, list = listOf(row)))

        val item = dataSource(api).getContractPaymentHistory("9001").single()

        assertEquals("0012345678", item.nationalId)
        assertEquals("9001", item.debtNumber)
        assertEquals(53_866_782.0, item.totalDebt)
        assertEquals(53_866_782.0, item.amountPayment)
        assertEquals("14050610", item.datePayment)
        assertEquals("پرداخت شده", item.statusContract)
        assertEquals("وصول شده", item.statusRecipient)
    }

    @Test
    fun `getContractDebit branches on premium type`() = runTest {
        val api = FakeApi(debit = ContractDebitDTO(total = 53_866_782L))

        val optional = dataSource(api).getContractDebit(ContractPremiumType.OPTIONAL, month = 3)
        assertEquals("optional", api.debitVariant)
        assertEquals(3, api.debitMonth)
        assertEquals(53_866_782L, optional.total)

        dataSource(api).getContractDebit(ContractPremiumType.FREELANCE, month = 6)
        assertEquals("freelance", api.debitVariant)
        assertEquals(6, api.debitMonth)
    }

    @Test
    fun `getContractLastPayment unwraps the bare optional timestamp`() = runTest {
        val api = FakeApi(optionalLastPayment = 1_700_000_000_000L)

        val result = dataSource(api).getContractLastPayment(ContractPremiumType.OPTIONAL)

        assertEquals("1700000000000", result.lastPaymentTimestamp)
        assertNull(result.chekReloLap)
    }

    @Test
    fun `getContractLastPayment maps the freelance object`() = runTest {
        val api = FakeApi(
            freelanceLastPayment = FreelanceLastPaymentDTO(
                chekReloLap = "قرارداد نیاز به بازبینی دارد",
                lastPaymentDate = "1700000000000",
                medicalRsltResend = null,
            ),
        )

        val result = dataSource(api).getContractLastPayment(ContractPremiumType.FREELANCE)

        assertEquals("1700000000000", result.lastPaymentTimestamp)
        assertEquals("قرارداد نیاز به بازبینی دارد", result.chekReloLap)
    }

    @Test
    fun `getPaymentCalculationDetails parses the positional row and branches on premium type`() = runTest {
        val row = buildJsonArray {
            add("1405")          // [0] year
            add("06")            // [1] month
            add("23")            // [2] day
            add("حق بیمه")       // [3] description
            add(6_650_220.0)     // [4] wage
            add(45_886_518.0)    // [5] amount
        }
        val api = FakeApi(paymentDetails = ListData(total = 1, list = listOf(row)))

        val optionalRows = dataSource(api)
            .getPaymentCalculationDetails(ContractPremiumType.OPTIONAL, startDate = 10L, endDate = 20L)
        assertEquals("optional", api.paymentDetailsVariant)
        assertEquals("10", api.paymentDetailsStart)
        assertEquals("20", api.paymentDetailsEnd)
        val parsed = optionalRows.single()
        assertEquals("1405", parsed.year)
        assertEquals("حق بیمه", parsed.description)
        assertEquals(6_650_220.0, parsed.wage)
        assertEquals(45_886_518.0, parsed.amount)

        dataSource(api).getPaymentCalculationDetails(ContractPremiumType.FREELANCE, 1L, 2L)
        assertEquals("freelance", api.paymentDetailsVariant)
    }

    @Test
    fun `downloadContractReport maps a network failure to NO_CONNECTION_ERROR`() = runTest {
        val api = FakeApi(shouldThrow = true)

        val error = assertFailsWith<TaminApiException> {
            dataSource(api).downloadContractReport(ContractPremiumType.OPTIONAL)
        }
        assertEquals("NO_CONNECTION_ERROR", error.title)
    }
}

private class FakeApiQueryBuilder : ApiQueryBuilder {
    override fun defaultQuery(): ApiQueryParamDN = ApiQueryParamDN()
    override fun buildQuery(query: ApiQueryParamDN): Map<String, String> = emptyMap()
    override fun buildFilterJson(filters: List<ApiFilterDN>): String = "[]"
}

private class FakeErrorParser : ErrorParser {
    override fun parseGeneralError(exception: TaminErrorUriException): TaminApiException =
        TaminApiException(title = exception.uri.name, cause = exception)
}

private class FakeApi(
    private val contracts: ListData<ContractDTO> = ListData(total = 0, list = emptyList()),
    private val states: ListData<ContractStateDTO> = ListData(total = 0, list = emptyList()),
    private val paymentHistory: ListData<JsonArray> = ListData(total = 0, list = emptyList()),
    private val paymentDetails: ListData<JsonArray> = ListData(total = 0, list = emptyList()),
    private val debit: ContractDebitDTO = ContractDebitDTO(),
    private val optionalLastPayment: Long? = null,
    private val freelanceLastPayment: FreelanceLastPaymentDTO = FreelanceLastPaymentDTO(),
    private val shouldThrow: Boolean = false,
) : ContractAffairApiService {

    var cancelVariant: String? = null
    var cancelStateCode: Int? = null
    var cancelRequest: CancelContractRequestDTO? = null
    var debitVariant: String? = null
    var debitMonth: Int? = null
    var paymentDetailsVariant: String? = null
    var paymentDetailsStart: String? = null
    var paymentDetailsEnd: String? = null

    private fun <T> ok(data: T): BaseDTO<T> =
        BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = data)

    override suspend fun getContractList(
        parameters: Map<String, String>,
    ): BaseDTO<ListData<ContractDTO>> {
        if (shouldThrow) throw FakeIOException()
        return ok(contracts)
    }

    override suspend fun getContractStates(
        parameters: Map<String, String>,
    ): BaseDTO<ListData<ContractStateDTO>> {
        if (shouldThrow) throw FakeIOException()
        return ok(states)
    }

    override suspend fun cancelOptionalContract(
        stateCode: Int,
        request: CancelContractRequestDTO,
    ): BaseDTO<JsonElement> {
        if (shouldThrow) throw FakeIOException()
        cancelVariant = "optional"
        cancelStateCode = stateCode
        cancelRequest = request
        return BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK")
    }

    override suspend fun cancelFreelanceContract(
        stateCode: Int,
        request: CancelContractRequestDTO,
    ): BaseDTO<JsonElement> {
        if (shouldThrow) throw FakeIOException()
        cancelVariant = "freelance"
        cancelStateCode = stateCode
        cancelRequest = request
        return BaseDTO(status = 200, family = "SUCCESSFUL", reason = "OK")
    }

    override suspend fun getContractPaymentHistory(
        contractNumber: String,
    ): BaseDTO<ListData<JsonArray>> {
        if (shouldThrow) throw FakeIOException()
        return ok(paymentHistory)
    }

    override suspend fun getOptionalContractReport(timestamp: Long): HttpStatement {
        if (shouldThrow) throw FakeIOException()
        error("HttpStatement is not built in this test")
    }

    override suspend fun getFreelanceContractReport(timestamp: Long): HttpStatement {
        if (shouldThrow) throw FakeIOException()
        error("HttpStatement is not built in this test")
    }

    override suspend fun getFractionContractReport(timestamp: Long): HttpStatement {
        if (shouldThrow) throw FakeIOException()
        error("HttpStatement is not built in this test")
    }

    override suspend fun getFreelanceContractDebit(month: Int): BaseDTO<ContractDebitDTO> {
        if (shouldThrow) throw FakeIOException()
        debitVariant = "freelance"
        debitMonth = month
        return ok(debit)
    }

    override suspend fun getOptionalContractDebit(month: Int): BaseDTO<ContractDebitDTO> {
        if (shouldThrow) throw FakeIOException()
        debitVariant = "optional"
        debitMonth = month
        return ok(debit)
    }

    override suspend fun getFreelanceLastPayment(): BaseDTO<FreelanceLastPaymentDTO> {
        if (shouldThrow) throw FakeIOException()
        return ok(freelanceLastPayment)
    }

    override suspend fun getOptionalLastPayment(): BaseDTO<Long> {
        if (shouldThrow) throw FakeIOException()
        return ok(optionalLastPayment ?: 0L)
    }

    override suspend fun getFreelancePaymentDetails(
        startDate: String,
        endDate: String,
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String,
    ): BaseDTO<ListData<JsonArray>> {
        if (shouldThrow) throw FakeIOException()
        paymentDetailsVariant = "freelance"
        paymentDetailsStart = startDate
        paymentDetailsEnd = endDate
        return ok(paymentDetails)
    }

    override suspend fun getOptionalPaymentDetails(
        startDate: String,
        endDate: String,
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String,
    ): BaseDTO<ListData<JsonArray>> {
        if (shouldThrow) throw FakeIOException()
        paymentDetailsVariant = "optional"
        paymentDetailsStart = startDate
        paymentDetailsEnd = endDate
        return ok(paymentDetails)
    }
}
