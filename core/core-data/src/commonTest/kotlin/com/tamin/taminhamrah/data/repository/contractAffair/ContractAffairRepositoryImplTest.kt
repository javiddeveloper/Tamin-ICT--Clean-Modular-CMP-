package com.tamin.taminhamrah.data.repository.contractAffair

import com.tamin.taminhamrah.dataSource.contractAffair.ContractAffairRemoteDataSource
import com.tamin.taminhamrah.model.contractAffair.CancelContractParamsDN
import com.tamin.taminhamrah.model.contractAffair.CancelContractRequestDTO
import com.tamin.taminhamrah.model.contractAffair.ContractDTO
import com.tamin.taminhamrah.model.contractAffair.ContractDebitDTO
import com.tamin.taminhamrah.model.contractAffair.ContractLastPaymentDTO
import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemDTO
import com.tamin.taminhamrah.model.contractAffair.ContractPremiumType
import com.tamin.taminhamrah.model.contractAffair.ContractStateDTO
import com.tamin.taminhamrah.model.contractAffair.PaymentCalculationRowDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * [ContractAffairRepositoryImpl] is network-only (no Room). These tests pin the DTO→DN mapping,
 * the fixed «all reasons in one page» query for علت خاتمه قرارداد, and the غیرفعال کردن قرارداد
 * request shape (`contractStatus = 99`).
 */
class ContractAffairRepositoryImplTest {

    private fun repository(remote: FakeRemote) = ContractAffairRepositoryImpl(remote)

    @Test
    fun `getContractsPage maps the list envelope into a page and forwards the query`() = runTest {
        val remote = FakeRemote(
            contracts = ListData(
                total = 57,
                list = listOf(contractDto(contractNumber = 11), contractDto(contractNumber = 12)),
            ),
        )
        val query = ApiQueryParamDN(limit = 10)

        val page = repository(remote).getContractsPage(query).first()

        assertEquals(57, page.total)
        assertEquals(listOf(11, 12), page.items.map { it.contractNumber })
        assertEquals(query, remote.lastContractsQuery)
    }

    @Test
    fun `getContractsPage tolerates a null list`() = runTest {
        val remote = FakeRemote(contracts = ListData(total = 0, list = null))

        val page = repository(remote).getContractsPage(ApiQueryParamDN()).first()

        assertTrue(page.items.isEmpty())
        assertEquals(0, page.total)
    }

    @Test
    fun `getContractStates asks for every reason in one page and maps the rows`() = runTest {
        val remote = FakeRemote(
            states = ListData(
                total = 2,
                list = listOf(
                    ContractStateDTO(selfIsuContStatDesc = "ابطال به درخواست", selfIsuContStatCode = 5),
                    ContractStateDTO(selfIsuContStatDesc = "عدم پرداخت", selfIsuContStatCode = 7),
                ),
            ),
        )

        val states = repository(remote).getContractStates().first()

        assertEquals(listOf(5, 7), states.map { it.code })
        assertEquals("ابطال به درخواست", states.first().description)
        val q = remote.lastStatesQuery
        assertEquals(0, q?.page)
        assertEquals(0, q?.start)
        assertEquals(100, q?.limit)
    }

    @Test
    fun `cancelContract forwards the endpoint variant and sends contractStatus 99`() = runTest {
        val remote = FakeRemote()
        val params = CancelContractParamsDN(
            premiumType = ContractPremiumType.FREELANCE,
            stateCode = 5,
            description = "توضیحات",
        )

        repository(remote).cancelContract(params).toList()

        assertEquals(ContractPremiumType.FREELANCE, remote.lastCancelPremiumType)
        assertEquals(5, remote.lastCancelStateCode)
        assertEquals("توضیحات", remote.lastCancelRequest?.canceldesc)
        assertEquals(99, remote.lastCancelRequest?.contractStatus)
    }

    @Test
    fun `getContractPaymentHistory maps the rows and forwards the contract number`() = runTest {
        val remote = FakeRemote(
            paymentHistory = listOf(
                ContractPaymentHistoryItemDTO(
                    debtNumber = "9001",
                    amountPayment = 53_866_782.0,
                    datePayment = "14050610",
                    statusContract = "پرداخت شده",
                ),
            ),
        )

        val rows = repository(remote).getContractPaymentHistory("9001").first()

        assertEquals("9001", rows.single().debtNumber)
        assertEquals(53_866_782.0, rows.single().amountPayment)
        assertEquals("9001", remote.lastPaymentHistoryContractNumber)
    }

    @Test
    fun `downloadContractReport forwards the premium type`() = runTest {
        val remote = FakeRemote()

        val pdf = repository(remote).downloadContractReport(ContractPremiumType.FRACTION).first()

        assertNull(pdf.pdf)
        assertEquals(ContractPremiumType.FRACTION, remote.lastReportPremiumType)
    }

    @Test
    fun `getContractDebit maps the debit envelope`() = runTest {
        val remote = FakeRemote(
            debit = ContractDebitDTO(
                total = 53_866_782L,
                insurancePremiums = 50_000_000L,
                previousDebit = 1_250_000L,
                startDate = 1L,
                endDate = 2L,
                payPremiumDate = "14051001",
                messageInformation = "پیام اطلاع‌رسانی",
            ),
        )

        val debit = repository(remote).getContractDebit(ContractPremiumType.OPTIONAL, month = 3).first()

        assertEquals(53_866_782L, debit.total)
        assertEquals(1_250_000L, debit.previousDebit)
        assertEquals("14051001", debit.payPremiumDate)
        assertEquals("پیام اطلاع‌رسانی", debit.infoMessage)
        assertEquals(ContractPremiumType.OPTIONAL, remote.lastDebitPremiumType)
        assertEquals(3, remote.lastDebitMonth)
    }

    @Test
    fun `getContractLastPayment maps a real timestamp and treats zero as no history`() = runTest {
        val withHistory = repository(
            FakeRemote(lastPayment = ContractLastPaymentDTO(lastPaymentTimestamp = "1700000000000")),
        ).getContractLastPayment(ContractPremiumType.FREELANCE).first()
        val withoutHistory = repository(
            FakeRemote(lastPayment = ContractLastPaymentDTO(lastPaymentTimestamp = "0")),
        ).getContractLastPayment(ContractPremiumType.OPTIONAL).first()

        assertEquals(1_700_000_000_000L, withHistory.lastPaymentTimestamp)
        assertNull(withoutHistory.lastPaymentTimestamp)
    }

    @Test
    fun `getPaymentCalculationDetails maps every row and forwards the period`() = runTest {
        val remote = FakeRemote(
            calcDetails = listOf(
                PaymentCalculationRowDTO("1405", "06", "23", "حق بیمه", 6_650_220.0, 45_886_518.0),
                PaymentCalculationRowDTO("1405", "06", "23", "کمک دولت", 6_650_220.0, -4_588_652.0),
            ),
        )

        val rows = repository(remote)
            .getPaymentCalculationDetails(ContractPremiumType.FREELANCE, startDate = 10L, endDate = 20L)
            .first()

        assertEquals(2, rows.size)
        assertEquals("کمک دولت", rows[1].description)
        assertEquals(-4_588_652.0, rows[1].amount)
        assertEquals(Triple(ContractPremiumType.FREELANCE, 10L, 20L), remote.lastCalcArgs)
    }

    @Test
    fun `a remote failure propagates through the flow`() = runTest {
        val remote = FakeRemote(error = IllegalStateException("boom"))

        assertFailsWith<IllegalStateException> {
            repository(remote).getContractsPage(ApiQueryParamDN()).first()
        }
    }
}

private class FakeRemote(
    private val contracts: ListData<ContractDTO> = ListData(total = 0, list = emptyList()),
    private val states: ListData<ContractStateDTO> = ListData(total = 0, list = emptyList()),
    private val paymentHistory: List<ContractPaymentHistoryItemDTO> = emptyList(),
    private val debit: ContractDebitDTO = ContractDebitDTO(),
    private val lastPayment: ContractLastPaymentDTO = ContractLastPaymentDTO(),
    private val calcDetails: List<PaymentCalculationRowDTO> = emptyList(),
    private val report: PdfDownloadDTO = PdfDownloadDTO(pdf = null),
    private val error: Exception? = null,
) : ContractAffairRemoteDataSource {

    var lastContractsQuery: ApiQueryParamDN? = null
    var lastStatesQuery: ApiQueryParamDN? = null
    var lastCancelPremiumType: ContractPremiumType? = null
    var lastCancelStateCode: Int? = null
    var lastCancelRequest: CancelContractRequestDTO? = null
    var lastPaymentHistoryContractNumber: String? = null
    var lastReportPremiumType: ContractPremiumType? = null
    var lastDebitPremiumType: ContractPremiumType? = null
    var lastDebitMonth: Int? = null
    var lastCalcArgs: Triple<ContractPremiumType, Long, Long>? = null

    override suspend fun getContracts(query: ApiQueryParamDN): ListData<ContractDTO> {
        lastContractsQuery = query
        error?.let { throw it }
        return contracts
    }

    override suspend fun getContractStates(query: ApiQueryParamDN): ListData<ContractStateDTO> {
        lastStatesQuery = query
        error?.let { throw it }
        return states
    }

    override suspend fun cancelContract(
        premiumType: ContractPremiumType,
        stateCode: Int,
        request: CancelContractRequestDTO,
    ) {
        lastCancelPremiumType = premiumType
        lastCancelStateCode = stateCode
        lastCancelRequest = request
        error?.let { throw it }
    }

    override suspend fun getContractPaymentHistory(
        contractNumber: String,
    ): List<ContractPaymentHistoryItemDTO> {
        lastPaymentHistoryContractNumber = contractNumber
        error?.let { throw it }
        return paymentHistory
    }

    override suspend fun downloadContractReport(premiumType: ContractPremiumType): PdfDownloadDTO {
        lastReportPremiumType = premiumType
        error?.let { throw it }
        return report
    }

    override suspend fun getContractDebit(
        premiumType: ContractPremiumType,
        month: Int,
    ): ContractDebitDTO {
        lastDebitPremiumType = premiumType
        lastDebitMonth = month
        error?.let { throw it }
        return debit
    }

    override suspend fun getContractLastPayment(
        premiumType: ContractPremiumType,
    ): ContractLastPaymentDTO {
        error?.let { throw it }
        return lastPayment
    }

    override suspend fun getPaymentCalculationDetails(
        premiumType: ContractPremiumType,
        startDate: Long,
        endDate: Long,
    ): List<PaymentCalculationRowDTO> {
        lastCalcArgs = Triple(premiumType, startDate, endDate)
        error?.let { throw it }
        return calcDetails
    }
}

private fun contractDto(contractNumber: Int): ContractDTO = ContractDTO(
    adultLetterDate = null,
    adultLetterNumber = null,
    age = null,
    branchCode = null,
    brchCodeNew = null,
    cancelDate = null,
    cancelUID = null,
    canceldesc = null,
    cityCode = null,
    cntDrmn = null,
    cntFreeJobCode = null,
    cntIncPayDate3t4 = null,
    cntMedicalFlag = null,
    comment = null,
    commissionStatus = null,
    confirmDate = null,
    confirmUID = null,
    contractDate = null,
    contractNumber = contractNumber,
    contractStatus = null,
    contractStatusObject = null,
    creatDate = null,
    createDate = null,
    createUID = null,
    eligibilityStatus = null,
    freeJob = null,
    guid = null,
    guidName = null,
    history = null,
    insuranceId = null,
    isStudent = null,
    medicalExemptionStatus = null,
    militaryServiceLicense = null,
    mobileNumber = null,
    natinoalCode = null,
    physicalStatus = null,
    premiumRate = null,
    premiumRateCode = null,
    premiumType = null,
    premiumTypeCode = null,
    provinceCode = null,
    provinceName = null,
    refCode = null,
    salary = null,
    startDate = null,
    statusDate = null,
    wage = null,
)
