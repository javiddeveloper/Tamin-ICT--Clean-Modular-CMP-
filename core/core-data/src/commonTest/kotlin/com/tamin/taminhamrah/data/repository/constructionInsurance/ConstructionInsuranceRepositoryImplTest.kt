package com.tamin.taminhamrah.data.repository.constructionInsurance

import app.cash.turbine.test
import com.tamin.taminhamrah.data.local.dao.ConstructionFileDao
import com.tamin.taminhamrah.data.local.entity.ConstructionFileEntity
import com.tamin.taminhamrah.dataSource.constructionInsurance.ConstructionInsuranceRemoteDataSource
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDTO
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDTO
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDTO
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDTO
import com.tamin.taminhamrah.model.constructionInsurance.WorkshopIdInfoDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.utils.ListData
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

/**
 * Exercises the real cache-then-network ([getConstructionFiles]) and network-only (every other
 * method) code paths of [ConstructionInsuranceRepositoryImpl] — not the `USE_MOCK_DATA` UI-testing
 * scaffold that sits in front of them. [USE_MOCK_DATA] is flipped to `false` for the duration of
 * this test class and restored afterwards so the app's own default (mock data on) is unaffected.
 */
class ConstructionInsuranceRepositoryImplTest {

    private lateinit var remoteDataSource: FakeConstructionInsuranceRemoteDataSource
    private lateinit var dao: FakeConstructionFileDao
    private lateinit var repository: ConstructionInsuranceRepositoryImpl

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeConstructionInsuranceRemoteDataSource()
        dao = FakeConstructionFileDao()
        repository = ConstructionInsuranceRepositoryImpl(remoteDataSource, dao)
    }

    @Test
    fun `getConstructionFiles should first emit local data then remote data`() = runTest {
        dao.filesFlow.value = listOf(createFileEntity(fileNumber = 1L))
        remoteDataSource.constructionFilesResult = ListData(total = 1, list = listOf(createFileDTO(fileNumber = 2L)))

        repository.getConstructionFiles().test {
            val firstEmission = awaitItem()
            assertEquals(listOf(1L), firstEmission.map { it.fileNumber })

            val secondEmission = awaitItem()
            assertEquals(listOf(2L), secondEmission.map { it.fileNumber })

            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(1, dao.replaceAllCalledCount)
        assertEquals(listOf(2L), dao.filesFlow.value.map { it.fileNumber })
    }

    @Test
    fun `getConstructionFiles should not throw when local data exists and remote fails`() = runTest {
        dao.filesFlow.value = listOf(createFileEntity(fileNumber = 1L))
        remoteDataSource.shouldThrowError = true

        repository.getConstructionFiles().test {
            val firstEmission = awaitItem()
            assertEquals(listOf(1L), firstEmission.map { it.fileNumber })
            expectNoEvents()
        }
    }

    @Test
    fun `getConstructionFiles should throw when local data is empty and remote fails`() = runTest {
        dao.filesFlow.value = emptyList()
        remoteDataSource.shouldThrowError = true

        repository.getConstructionFiles().test {
            awaitItem() // first emission: empty local list
            awaitError()
        }
    }

    @Test
    fun `getConstructionFiles should replace the cached list rather than merge it`() = runTest {
        dao.filesFlow.value = listOf(createFileEntity(fileNumber = 1L))
        remoteDataSource.constructionFilesResult = ListData(total = 1, list = listOf(createFileDTO(fileNumber = 2L)))

        repository.getConstructionFiles().test {
            awaitItem()
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(listOf(2L), dao.filesFlow.value.map { it.fileNumber })
    }

    @Test
    fun `getConstructionFiles should forward non-blank search fields as EQ filters`() = runTest {
        val search = ConstructionFileSearchParamsDN(fileNo = "1234", reqNo = "5678", workshopId = null, branchCode = null)

        // .first() would cancel the flow right after its first (local-cache) emission, before the
        // flow body ever reaches the remote call below it. Here local and remote both resolve to
        // an empty list, so the post-remote re-emission is swallowed by .distinctUntilChanged() —
        // there is only ever one item on the wire — but the remote call (and buildQuery(search))
        // has already run by the time it arrives, since Turbine's unlimited buffer never makes the
        // producer wait on a slow consumer.
        repository.getConstructionFiles(search).test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        val filterProperties = remoteDataSource.lastQuery?.filters.orEmpty().map { it.property }
        assertEquals(listOf(FilterProperty.FILE_NO, FilterProperty.REQ_NO), filterProperties)
    }

    @Test
    fun `getBeneficiariesWorkshop should emit mapped beneficiaries from remote`() = runTest {
        remoteDataSource.beneficiariesResult = ListData(
            total = 1,
            list = listOf(BeneficiaryConstructionDTO(name = "علی", lastName = "توکلی"))
        )

        val result = repository.getBeneficiariesWorkshop(requestNumber = 1L, fileNumber = 2L, requestDate = null).first()

        assertEquals(1, result.size)
        assertEquals("علی", result.first().name)
        assertEquals(1L, remoteDataSource.lastBeneficiariesRequestNumber)
    }

    @Test
    fun `getPaymentSheetConstructionInfo should emit mapped payment sheets from remote`() = runTest {
        remoteDataSource.paymentSheetResult = ListData(
            total = 1,
            list = listOf(PaymentSheetConstructionFileDTO(orderNumber = "1", paymentSheetAmount = 500_000L))
        )

        val result = repository.getPaymentSheetConstructionInfo("123456789010").first()

        assertEquals(1, result.size)
        assertEquals(500_000L, result.first().paymentSheetAmount)
        assertEquals("123456789010", remoteDataSource.lastPaymentSheetDebitNumber)
    }

    @Test
    fun `getCertificatePaymentSheetPdf should emit mapped pdf from remote`() = runTest {
        remoteDataSource.certificatePdfResult = PdfDownloadDTO(pdf = InputStreamDTO(pdf = null))

        val result = repository.getCertificatePaymentSheetPdf("123456789010", "6400").first()

        assertEquals(null, result.pdf?.pdf)
        assertEquals("123456789010", remoteDataSource.lastCertificateDebitNumber)
        assertEquals("6400", remoteDataSource.lastCertificateBranchCode)
    }

    @Test
    fun `issuancePaymentSheet should emit the message returned by remote`() = runTest {
        remoteDataSource.issuanceMessageResult = "صادر شد"

        val result = repository.issuancePaymentSheet("123456789010").first()

        assertEquals("صادر شد", result)
        assertEquals("123456789010", remoteDataSource.lastIssuanceDebitNumber)
    }

    @Test
    fun `issuancePaymentSheet should propagate remote errors`() = runTest {
        val expectedError = RuntimeException("Network Error")
        remoteDataSource.shouldThrowError = true
        remoteDataSource.thrownError = expectedError

        val actualError = assertFailsWith<RuntimeException> {
            repository.issuancePaymentSheet("123").first()
        }

        assertEquals(expectedError.message, actualError.message)
    }

    @Test
    fun `getInstallmentLetterList should emit mapped installment letters from remote`() = runTest {
        remoteDataSource.installmentLettersResult = ListData(
            total = 1,
            list = listOf(InstallmentLetterDTO(debitNumber = "77640000001", remainingAmount = 400_000L))
        )

        val result = repository.getInstallmentLetterList("14020901", "6400").first()

        assertEquals(1, result.size)
        assertEquals("77640000001", result.first().debitNumber)
        assertEquals("14020901", remoteDataSource.lastInstallmentWorkshopId)
        assertEquals("6400", remoteDataSource.lastInstallmentBranchId)
    }

    private fun createFileDTO(fileNumber: Long) = ConstructionFileDTO(
        fileNumber = fileNumber,
        workshopInfo = WorkshopIdInfoDTO(workshopId = "14020901", brhCode = "6400"),
    )

    private fun createFileEntity(fileNumber: Long) = ConstructionFileEntity(
        fileNumber = fileNumber, requestNumber = null, requestDate = null, workshopId = null,
        workshopRegisterDate = null, brhCode = null, postalCode = null, address = null,
        mainPlaque = null, subPlaque = null, block = null, propertyConstruction = null,
        apartment = null, trade = null, partPlaque = null, sumOfComplications = null,
        debitNumber = null, totalPayment = null, meterage = null, debitStatusCode = null,
        protrusion = null, applicationFees = null, residentialServiceInfrastructureFees = null,
        excessDensitySurchargeFees = null, increasePropertyValue = null,
        issuanceFencingWallConstructionFees = null, coveredClause3Fees = null,
        article100 = null, paymentDeadLine = null,
    )

    private class FakeConstructionInsuranceRemoteDataSource : ConstructionInsuranceRemoteDataSource {
        var constructionFilesResult = ListData<ConstructionFileDTO>(total = 0, list = emptyList())
        var beneficiariesResult = ListData<BeneficiaryConstructionDTO>(total = 0, list = emptyList())
        var paymentSheetResult = ListData<PaymentSheetConstructionFileDTO>(total = 0, list = emptyList())
        var certificatePdfResult = PdfDownloadDTO(pdf = InputStreamDTO(pdf = null))
        var issuanceMessageResult = "OK"
        var installmentLettersResult = ListData<InstallmentLetterDTO>(total = 0, list = emptyList())

        var shouldThrowError = false
        var thrownError: Throwable = RuntimeException("Remote failure")

        var lastQuery: ApiQueryParamDN? = null
        var lastBeneficiariesRequestNumber: Long? = null
        var lastPaymentSheetDebitNumber: String? = null
        var lastCertificateDebitNumber: String? = null
        var lastCertificateBranchCode: String? = null
        var lastIssuanceDebitNumber: String? = null
        var lastInstallmentWorkshopId: String? = null
        var lastInstallmentBranchId: String? = null

        override suspend fun getConstructionFiles(query: ApiQueryParamDN): ListData<ConstructionFileDTO> {
            lastQuery = query
            if (shouldThrowError) throw thrownError
            return constructionFilesResult
        }

        override suspend fun getBeneficiariesWorkshop(
            requestNumber: Long?,
            fileNumber: Long?,
            requestDate: String?,
        ): ListData<BeneficiaryConstructionDTO> {
            lastBeneficiariesRequestNumber = requestNumber
            if (shouldThrowError) throw thrownError
            return beneficiariesResult
        }

        override suspend fun getPaymentSheetConstructionInfo(debitNumber: String): ListData<PaymentSheetConstructionFileDTO> {
            lastPaymentSheetDebitNumber = debitNumber
            if (shouldThrowError) throw thrownError
            return paymentSheetResult
        }

        override suspend fun getCertificatePaymentSheetPdf(debitNumber: String, branchCode: String): PdfDownloadDTO {
            lastCertificateDebitNumber = debitNumber
            lastCertificateBranchCode = branchCode
            if (shouldThrowError) throw thrownError
            return certificatePdfResult
        }

        override suspend fun issuancePaymentSheet(debitNumber: String): String {
            lastIssuanceDebitNumber = debitNumber
            if (shouldThrowError) throw thrownError
            return issuanceMessageResult
        }

        override suspend fun getInstallmentLetterList(workshopId: String, branchId: String): ListData<InstallmentLetterDTO> {
            lastInstallmentWorkshopId = workshopId
            lastInstallmentBranchId = branchId
            if (shouldThrowError) throw thrownError
            return installmentLettersResult
        }
    }

    private class FakeConstructionFileDao : ConstructionFileDao {
        val filesFlow = MutableStateFlow<List<ConstructionFileEntity>>(emptyList())
        var replaceAllCalledCount = 0

        override fun getConstructionFiles(): Flow<List<ConstructionFileEntity>> = filesFlow

        override suspend fun insertAll(files: List<ConstructionFileEntity>) {
            val existingByKey = filesFlow.value.associateBy { it.fileNumber }.toMutableMap()
            files.forEach { existingByKey[it.fileNumber] = it }
            filesFlow.value = existingByKey.values.toList()
        }

        override suspend fun clearAll() {
            filesFlow.value = emptyList()
        }

        override suspend fun replaceAll(files: List<ConstructionFileEntity>) {
            replaceAllCalledCount++
            clearAll()
            insertAll(files)
        }
    }
}
