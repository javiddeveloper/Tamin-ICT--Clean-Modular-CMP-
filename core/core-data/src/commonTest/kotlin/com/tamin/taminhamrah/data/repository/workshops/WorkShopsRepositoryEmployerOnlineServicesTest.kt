package com.tamin.taminhamrah.data.repository.workshops

import com.tamin.taminhamrah.data.repository.WorkShopsRepositoryImpl
import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSource
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.*
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * [WorkShopsRepositoryImpl] for the خدمات غیرحضوری کارفرما calls.
 *
 * The repository's job on these is small but real: turn a bare page index into the ExtJS-style
 * `{page, start, limit}` the services take (`start = page * WORKSHOP_PAGE_SIZE`), forward the
 * workshop/branch identity as path arguments, fold `ListData` into `PagedListDN`, and run the
 * DTO⇄domain mappers. Those are what is pinned here.
 */
class WorkShopsRepositoryEmployerOnlineServicesTest {

    private lateinit var remote: FakeRemote
    private lateinit var repository: WorkShopsRepositoryImpl

    @BeforeTest
    fun setup() {
        remote = FakeRemote()
        repository = WorkShopsRepositoryImpl(remote)
    }

    @Test
    fun `workshops-without-contract folds ListData into a page and asks for a real page window`() = runTest {
        remote.workshopsWithoutContract = ListData(
            total = 42,
            list = listOf(
                WorkshopWithoutContractDTO(workshopId = "1071410004", branchCode = "123", workshopName = "کارگاه الف"),
            ),
        )

        val page = repository.getWorkshopsWithoutContract(page = 0)

        assertEquals(42, page.total)
        assertEquals(1, page.items.size)
        assertEquals("1071410004", page.items.first().workshopId)
        assertEquals(0, remote.lastWorkshopsQuery?.page)
        assertEquals(0, remote.lastWorkshopsQuery?.start)
        assertEquals(WORKSHOP_PAGE_SIZE, remote.lastWorkshopsQuery?.limit)
    }

    @Test
    fun `contract rows forward workshop and branch as path args and map the list`() = runTest {
        remote.contractRows = ListData(
            total = 1,
            list = listOf(WorkshopContractRowDTO(contractRow = "02100014", firstName = "علی", lastName = "پیمانکار")),
        )

        val page = repository.getWorkshopContractRows(workshopId = "0968210170", branchCode = "0960", page = 0)

        assertEquals("علی پیمانکار", page.items.first().fullName)
        assertEquals("0968210170" to "0960", remote.lastContractRowsPath)
    }

    @Test
    fun `contact info forwards the verification code and maps the identity block`() = runTest {
        remote.commitmentInfo = EmployerCommitmentInfoDTO(
            firstName = "رضا",
            lastName = "کارفرما",
            nationalCode = "0012345678",
            mobile = "09120000000",
        )

        val dn = repository.getEmployerAgreementContactInfo("123456")

        assertEquals("رضا کارفرما", dn.fullName)
        assertEquals("09120000000", dn.currentMobile)
        assertEquals("123456", remote.lastVerificationCode)
    }

    @Test
    fun `request ticket passes mobile and email straight through`() = runTest {
        remote.ticketMessage = "کد ارسال شد"

        val message = repository.requestEmployerAgreementTicket(mobile = "09121234567", email = "boss@example.com")

        assertEquals("کد ارسال شد", message)
        assertEquals("09121234567" to "boss@example.com", remote.lastTicketArgs)
    }

    @Test
    fun `submit maps the domain submission to the request body`() = runTest {
        remote.submitMessage = "ثبت شد"

        val message = repository.submitEmployerAgreement(
            EmployerAgreementSubmissionDN(mobile = "09121234567", email = "boss@example.com", ticketCode = "654321"),
        )

        assertEquals("ثبت شد", message)
        assertEquals("09121234567", remote.lastSubmitBody?.mobile)
        assertEquals("boss@example.com", remote.lastSubmitBody?.email)
        assertEquals("654321", remote.lastSubmitBody?.ticketCode)
    }

    @Test
    fun `a remote failure propagates unchanged`() = runTest {
        remote.failure = IllegalStateException("no connection")

        val error = assertFailsWith<IllegalStateException> {
            repository.getWorkshopsWithoutContract(page = 0)
        }
        assertEquals("no connection", error.message)
    }

    /**
     * A [WorkShopsRemoteDataSource] scoped to the خدمات غیرحضوری کارفرما calls the tests above
     * touch. Everything else is inherited from [NotUsedWorkShopsRemoteDataSource] and throws, so an
     * unintended call surfaces instead of returning a silent default.
     */
    private class FakeRemote : NotUsedWorkShopsRemoteDataSource() {
        var workshopsWithoutContract: ListData<WorkshopWithoutContractDTO> = ListData()
        var contractRows: ListData<WorkshopContractRowDTO> = ListData()
        var agreementsByWorkshop: ListData<EmployerAgreementByWorkshopDTO> = ListData()
        var commitmentInfo: EmployerCommitmentInfoDTO = EmployerCommitmentInfoDTO()
        var ticketMessage: String = ""
        var submitMessage: String = ""
        var failure: Throwable? = null

        var lastWorkshopsQuery: ApiQueryParamDN? = null
        var lastContractRowsPath: Pair<String, String>? = null
        var lastAgreementsByWorkshopPath: Pair<String, String>? = null
        var lastVerificationCode: String? = null
        var lastTicketArgs: Pair<String, String>? = null
        var lastSubmitBody: EmployerAgreementSubmitRequestDTO? = null

        override suspend fun getEmployerWorkshopsWithoutContract(
            query: ApiQueryParamDN,
        ): ListData<WorkshopWithoutContractDTO> {
            failure?.let { throw it }
            lastWorkshopsQuery = query
            return workshopsWithoutContract
        }

        override suspend fun getEmployerWorkshopContractList(
            workshopId: String,
            branchCode: String,
            query: ApiQueryParamDN,
        ): ListData<WorkshopContractRowDTO> {
            failure?.let { throw it }
            lastContractRowsPath = workshopId to branchCode
            return contractRows
        }

        override suspend fun getEmployerAgreementUserInfo(verificationCode: String): EmployerCommitmentInfoDTO {
            failure?.let { throw it }
            lastVerificationCode = verificationCode
            return commitmentInfo
        }

        override suspend fun requestEmployerAgreementTicket(mobileNumber: String, email: String): String {
            failure?.let { throw it }
            lastTicketArgs = mobileNumber to email
            return ticketMessage
        }

        override suspend fun submitEmployerAgreement(request: EmployerAgreementSubmitRequestDTO): String {
            failure?.let { throw it }
            lastSubmitBody = request
            return submitMessage
        }
    }
}
