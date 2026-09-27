package com.tamin.taminhamrah.data.repository.workshops

import com.tamin.taminhamrah.data.repository.WorkShopsRepositoryImpl
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.AssignerContractDTO
import com.tamin.taminhamrah.model.workshop.AssignerContractQuery
import com.tamin.taminhamrah.model.workshop.AssignerPartyDTO
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDTO
import com.tamin.taminhamrah.model.workshop.ComputationalBaseQuery
import com.tamin.taminhamrah.model.workshop.SettlementCertificateDN
import com.tamin.taminhamrah.model.workshop.SettlementCertificateDTO
import com.tamin.taminhamrah.model.workshop.SettlementCertificateDetailDTO
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * [WorkShopsRepositoryImpl] for واگذارندگان.
 *
 * This is the layer that knows the service spells one idea several ways, and two of its decisions
 * are the ones that break silently rather than loudly:
 *
 * - the list's identity travels as **filter clauses** (`workshop.workshopId`, `workshop.branchCode`,
 *   `contractRow`), and a blank one has to be *omitted* — sending it empty narrows the search to
 *   nothing rather than widening it;
 * - the bases call takes four positional query parameters, and the branch goes to `brchCode`. Swap
 *   two of them and the request still succeeds — with somebody else's records in it.
 */
class WorkShopsRepositoryAssignerContractsTest {

    private lateinit var remote: FakeRemote
    private lateinit var repository: WorkShopsRepositoryImpl

    @BeforeTest
    fun setup() {
        remote = FakeRemote()
        repository = WorkShopsRepositoryImpl(remote, FakeEmployerServicesPageDao())
    }

    @Test
    fun `assigner contracts send all three codes as the services own filter property names`() = runTest {
        repository.getAssignerContracts(
            AssignerContractQuery(workshopId = "9028212822", branchCode = "0210", contractRow = "1")
        )

        val filters = remote.lastContractsQuery?.filters.orEmpty()
        assertEquals(3, filters.size)
        assertEquals(FilterProperty.WORKSHOP_ID, filters[0].property)
        assertEquals("9028212822", filters[0].value)
        assertEquals(FilterProperty.WORKSHOP_BRANCH_CODE, filters[1].property)
        assertEquals("0210", filters[1].value)
        assertEquals(FilterProperty.CONTRACT_ROW, filters[2].property)
        assertEquals("1", filters[2].value)
    }

    /**
     * The optional halves are dropped, not sent blank.
     *
     * This is the opposite of ردیف‌های پیمان, where the same two codes are path segments and a blank
     * one addresses a route that 404s. Here a blank clause would narrow the result to nothing.
     */
    @Test
    fun `assigner contracts omit the optional codes when they are blank`() = runTest {
        repository.getAssignerContracts(AssignerContractQuery(workshopId = "9028212822"))

        val filters = remote.lastContractsQuery?.filters.orEmpty()
        assertEquals(1, filters.size)
        assertEquals(FilterProperty.WORKSHOP_ID, filters.single().property)
    }

    /** With no code at all the filter array is empty — which is how the screen asks for every پیمان. */
    @Test
    fun `assigner contracts with no codes send no filter at all`() = runTest {
        repository.getAssignerContracts(AssignerContractQuery(workshopId = ""))

        assertEquals(emptyList(), remote.lastContractsQuery?.filters)
    }

    @Test
    fun `assigner contracts page from the index and fold the envelope`() = runTest {
        remote.contracts = ListData(
            total = 17,
            list = listOf(
                AssignerContractDTO(
                    contractRow = "1",
                    contractSequence = "3",
                    employer = AssignerPartyDTO(workshopId = "9028212822", workshopName = "کارن"),
                ),
            ),
        )

        val page = repository.getAssignerContracts(
            AssignerContractQuery(workshopId = "9028212822", page = 2)
        )

        assertEquals(17, page.total)
        assertEquals("9028212822", page.items.single().employer.workshopId)
        assertEquals(2, remote.lastContractsQuery?.page)
        assertEquals(2 * WORKSHOP_PAGE_SIZE, remote.lastContractsQuery?.start)
        assertEquals(WORKSHOP_PAGE_SIZE, remote.lastContractsQuery?.limit)
    }

    /**
     * The branch reaches `brchCode`, and none of the four keys is transposed.
     *
     * All four are strings in the same call, so a swap compiles and the service answers 200 with
     * the wrong contract's bases — which is why they are pinned by value rather than by shape.
     */
    @Test
    fun `computational bases put each key in its own query parameter`() = runTest {
        repository.getComputationalBases(
            ComputationalBaseQuery(
                workshopId = "9028212822",
                branchCode = "0210",
                contractRow = "1",
                contractSequence = "3",
            )
        )

        assertEquals("9028212822", remote.lastBasesArgs?.workshopId)
        assertEquals("1", remote.lastBasesArgs?.contractRow)
        assertEquals("0210", remote.lastBasesArgs?.brchCode)
        assertEquals("3", remote.lastBasesArgs?.contractSequence)
    }

    @Test
    fun `computational bases carry no filter array of their own`() = runTest {
        remote.bases = ListData(total = 1, list = listOf(ComputationalBaseDTO(letterNumber = "12044")))

        val page = repository.getComputationalBases(
            ComputationalBaseQuery("9028212822", "0210", "1", "3")
        )

        assertEquals("12044", page.items.single().letterNumber)
        assertEquals(emptyList(), remote.lastBasesArgs?.query?.filters)
    }

    /**
     * One ردیف can hold several پیمان, so the contract number picks the certificate — and the detail
     * is asked for by that row's serial, not by whichever row was listed first.
     */
    @Test
    fun `a settlement certificate is picked by contract number and read by its serial`() = runTest {
        remote.certificates = ListData(
            total = 2,
            list = listOf(
                SettlementCertificateDTO(clearanceSerial = "111", contractNumber = "45200", contractRow = "1"),
                SettlementCertificateDTO(clearanceSerial = "222", contractNumber = "44122", contractRow = "1"),
            ),
        )
        remote.certificateDetail = ListData(
            total = 1,
            list = listOf(
                SettlementCertificateDetailDTO(
                    clearanceSerial = "222",
                    clearanceNumber = "38-7712405",
                    clearanceDate = "14021103",
                ),
            ),
        )

        val certificate = repository.getSettlementCertificate("9028212822", "0310", "1", "44122")

        assertEquals(listOf("9028212822", "0310", "1"), remote.lastCertificateArgs)
        assertEquals("222", remote.lastDetailSerial)
        assertEquals(SettlementCertificateDN(serial = "222", number = "38-7712405", date = "14021103"), certificate)
    }

    @Test
    fun `no certificate for the contract answers null without asking for a detail`() = runTest {
        remote.certificates = ListData(
            total = 1,
            list = listOf(SettlementCertificateDTO(clearanceSerial = "111", contractNumber = "45200")),
        )

        assertNull(repository.getSettlementCertificate("9028212822", "0310", "1", "44122"))
        assertNull(remote.lastDetailSerial)
    }

    /** What the bases endpoint was called with, as one value so a transposition is visible. */
    private data class BasesArgs(
        val workshopId: String,
        val contractRow: String,
        val brchCode: String,
        val contractSequence: String,
        val query: ApiQueryParamDN,
    )

    private class FakeRemote : NotUsedWorkShopsRemoteDataSource() {
        var contracts: ListData<AssignerContractDTO> = ListData()
        var bases: ListData<ComputationalBaseDTO> = ListData()

        var lastContractsQuery: ApiQueryParamDN? = null
        var lastBasesArgs: BasesArgs? = null

        override suspend fun getAssignerContracts(
            query: ApiQueryParamDN,
        ): ListData<AssignerContractDTO> {
            lastContractsQuery = query
            return contracts
        }

        override suspend fun getComputationalBases(
            workshopId: String,
            contractRow: String,
            brchCode: String,
            contractSequence: String,
            query: ApiQueryParamDN,
        ): ListData<ComputationalBaseDTO> {
            lastBasesArgs = BasesArgs(workshopId, contractRow, brchCode, contractSequence, query)
            return bases
        }

        var certificates: ListData<SettlementCertificateDTO> = ListData()
        var certificateDetail: ListData<SettlementCertificateDetailDTO> = ListData()
        var lastCertificateArgs: List<String>? = null
        var lastDetailSerial: String? = null

        override suspend fun getSettlementCertificates(
            workshopId: String,
            branchCode: String,
            contractRow: String,
            query: ApiQueryParamDN,
        ): ListData<SettlementCertificateDTO> {
            lastCertificateArgs = listOf(workshopId, branchCode, contractRow)
            return certificates
        }

        override suspend fun getSettlementCertificateDetail(
            workshopId: String,
            branchCode: String,
            contractRow: String,
            serial: String,
            query: ApiQueryParamDN,
        ): ListData<SettlementCertificateDetailDTO> {
            lastDetailSerial = serial
            return certificateDetail
        }
    }
}
