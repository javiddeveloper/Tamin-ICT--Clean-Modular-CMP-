package com.tamin.taminhamrah.data.repository.constructionInsurance

import com.tamin.taminhamrah.data.local.dao.ConstructionFileDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.constructionInsurance.ConstructionInsuranceRemoteDataSource
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.BuildingRequestSummaryDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.WorkshopIdInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * TEMPORARY — UI/flow testing scaffold, not a real feature. When `true`, every method below
 * returns in-memory sample data instead of touching Room/the network, so the عملیات screens can
 * be clicked through end to end before a real backend/environment is available. Flip to `false`
 * (or delete the mock branch + [ConstructionInsuranceMockData]) once real API testing is possible
 * — the original cache-then-network / network-only implementations are kept intact below each flag
 * check.
 *
 * `internal var` rather than `private const val` so [ConstructionInsuranceRepositoryImplTest]
 * (same module's commonTest, a Gradle "friend" source set) can flip it to `false` to exercise the
 * real Room/network code paths without changing the app's own default (still `true`).
 */
internal var USE_MOCK_DATA = true

internal class ConstructionInsuranceRepositoryImpl(
    private val remoteDataSource: ConstructionInsuranceRemoteDataSource,
    private val constructionFileDao: ConstructionFileDao,
) : ConstructionInsuranceRepository {

    override fun getConstructionFiles(
        search: ConstructionFileSearchParamsDN?
    ): Flow<List<ConstructionFileDN>> {
        if (USE_MOCK_DATA) {
            return flow { emit(ConstructionInsuranceMockData.constructionFiles(search)) }
        }
        return flow {
            val localFiles = constructionFileDao.getConstructionFiles().first()
            emit(localFiles.map { it.toDomain() })

            try {
                val query = buildQuery(search)
                val response = remoteDataSource.getConstructionFiles(query)
                val remoteFiles = response.list.orEmpty()
                constructionFileDao.replaceAll(remoteFiles.map { it.toEntity() })
            } catch (e: Exception) {
                if (localFiles.isEmpty()) {
                    throw e
                }
            }

            emitAll(
                constructionFileDao.getConstructionFiles().map { entities ->
                    entities.map { it.toDomain() }
                }
            )
        }.distinctUntilChanged()
    }

    // Network-only from here down — see the offline-first rubric in
    // .claude/rules/data-and-caching.md: these are per-action/per-request lookups reached from the
    // عملیات menu, not data worth caching to Room.

    override fun getBeneficiariesWorkshop(
        requestNumber: Long?,
        fileNumber: Long?,
        requestDate: String?,
    ): Flow<List<BeneficiaryConstructionDN>> = flow {
        if (USE_MOCK_DATA) {
            emit(
                ConstructionInsuranceMockData.beneficiaries(
                    requestNumber,
                    fileNumber,
                    requestDate
                )
            )
            return@flow
        }
        val result =
            remoteDataSource.getBeneficiariesWorkshop(requestNumber, fileNumber, requestDate)
        emit(result.list.orEmpty().map { it.toDomain() })
    }

    override fun getPaymentSheetConstructionInfo(
        debitNumber: String
    ): Flow<List<PaymentSheetConstructionFileDN>> = flow {
        if (USE_MOCK_DATA) {
            emit(ConstructionInsuranceMockData.paymentSheets(debitNumber))
            return@flow
        }
        val result = remoteDataSource.getPaymentSheetConstructionInfo(debitNumber)
        emit(result.list.orEmpty().map { it.toDomain() })
    }

    override fun getCertificatePaymentSheetPdf(
        debitNumber: String,
        branchCode: String,
    ): Flow<PdfDownloadDN> = flow {
        if (USE_MOCK_DATA) {
            emit(ConstructionInsuranceMockData.certificatePdf())
            return@flow
        }
        emit(remoteDataSource.getCertificatePaymentSheetPdf(debitNumber, branchCode).toDomain())
    }

    override fun issuancePaymentSheet(debitNumber: String): Flow<String> = flow {
        if (USE_MOCK_DATA) {
            emit(ConstructionInsuranceMockData.issuanceMessage(debitNumber))
            return@flow
        }
        emit(remoteDataSource.issuancePaymentSheet(debitNumber))
    }

    override fun getInstallmentLetterList(
        workshopId: String,
        branchId: String,
    ): Flow<List<InstallmentLetterDN>> = flow {
        if (USE_MOCK_DATA) {
            emit(ConstructionInsuranceMockData.installmentLetters(workshopId, branchId))
            return@flow
        }
        val result = remoteDataSource.getInstallmentLetterList(workshopId, branchId)
        emit(result.list.orEmpty().map { it.toDomain() })
    }

    private fun buildQuery(search: ConstructionFileSearchParamsDN?): ApiQueryParamDN {
        if (search == null) {
            return ApiQueryParamDN()
        }
        val filters = mutableListOf<ApiFilterDN>()
        val fileNo = search.fileNo
        if (!fileNo.isNullOrBlank()) {
            filters.add(
                ApiFilterDN(
                    property = FilterProperty.FILE_NO,
                    value = fileNo,
                    operator = FilterOperator.EQ
                )
            )
        }
        val reqNo = search.reqNo
        if (!reqNo.isNullOrBlank()) {
            filters.add(
                ApiFilterDN(
                    property = FilterProperty.REQ_NO,
                    value = reqNo,
                    operator = FilterOperator.EQ
                )
            )
        }
        val workshopId = search.workshopId
        if (!workshopId.isNullOrBlank()) {
            filters.add(
                ApiFilterDN(
                    property = FilterProperty.WORKSHOP_ID,
                    value = workshopId,
                    operator = FilterOperator.EQ
                )
            )
        }
        val branchCode = search.branchCode
        if (!branchCode.isNullOrBlank()) {
            filters.add(
                ApiFilterDN(
                    property = FilterProperty.WORKSHOP_BRANCH_CODE,
                    value = branchCode,
                    operator = FilterOperator.EQ
                )
            )
        }
        return ApiQueryParamDN(filters = filters)
    }
}


/**
 * TEMPORARY — sample data backing [USE_MOCK_DATA]. Mirrors the shapes the real
 * `bld-request-services/…` endpoints return (see `ConstructionInsuranceRemoteDataSourceImpl`),
 * with enough variety (mixed debitStatusCode, owner types, statuses) to exercise every UI branch
 * — empty vs. populated, cash vs. installment row, owner vs. applicant, etc.
 */

private object ConstructionInsuranceMockData {

    private val workshops = listOf(
        WorkshopIdInfoDN("14020901", "9028222442", "6400"),
        WorkshopIdInfoDN("14020815", "6393610019", "6400"),
        WorkshopIdInfoDN("14020703", "1122334455", "6122"),
        WorkshopIdInfoDN("14021005", "5566778899", "6400"),
        WorkshopIdInfoDN("14020520", "3344556677", "6055"),
        WorkshopIdInfoDN("14030110", "7788990011", "6122"),
    )

    private val addresses = listOf(
        "مشهد - بلوار وکیل آباد - نبش وکیل آباد ۲۵",
        "مشهد - احمدآباد - خیابان دانشگاه",
        "مشهد - قاسم آباد - بلوار فارغ التحصیلان",
        "مشهد - طرقبه - جاده کندوان",
        "مشهد - الهیه - خیابان کوهسنگی",
        "مشهد - سجاد - بلوار سجاد",
    )

    /** debitStatusCode values seen in the old app besides "51" (installment) — cash/paid/pending. */
    private val cashDebitStatusCodes = listOf("10", "20", "30")

    private val allConstructionFiles: List<ConstructionFileDN> = (0 until 14).map { i ->
        val workshop = workshops[i % workshops.size]
        val isInstallment = i % 3 == 0
        // Edge cases so every "-" / fallback branch in the UI actually gets exercised, not just
        // the happy path: no debit number yet (پرداخت pending allocation), no address on file,
        // an unpaid deadline, a zero-fee row, etc. — spread across a few indices, not every row.
        val debitNumberAllocating = i == 4
        val missingAddress = i == 7
        val missingPostalCode = i == 7
        ConstructionFileDN(
            fileNumber = 4_479_890_000L + i,
            requestNumber = 123_456_700L + i,
            requestDate = workshop.workshopRegisterDate,
            workshopInfo = workshop,
            postalCode = if (missingPostalCode) null else "918795551${i % 10}",
            address = if (missingAddress) null else addresses[i % addresses.size],
            mainPlaque = 10 + i,
            subPlaque = (i % 5) + 1,
            block = (i % 8).toLong() + 1,
            propertyConstruction = 1390 + (i % 12),
            apartment = i % 6,
            trade = i % 2,
            partPlaque = (i % 4) + 1,
            sumOfComplications = if (i == 10) 0L else 150_000L * (i + 1),
            debitNumber = if (debitNumberAllocating) null else "12345678901$i",
            totalPayment = 900_000L * (i + 2),
            meterage = 80 + i * 7,
            debitStatusCode = if (isInstallment) "51" else cashDebitStatusCodes[i % cashDebitStatusCodes.size],
            protrusion = 30_000L * (i + 1),
            applicationFees = 120_000L * (i + 1),
            residentialServiceInfrastructureFees = 90_000L * (i + 1),
            excessDensitySurchargeFees = 60_000L * (i + 1),
            increasePropertyValue = 200_000L * (i + 1),
            issuanceFencingWallConstructionFees = 45_000L * (i + 1),
            coveredClause3Fees = 30_000L * (i + 1),
            article100 = 15_000L * (i + 1),
            paymentDeadLine = if (i == 12) null else "1402${10 + (i % 2)}${10 + i}",
        )
    }

    fun constructionFiles(search: ConstructionFileSearchParamsDN?): List<ConstructionFileDN> {
        if (search == null) return allConstructionFiles
        return allConstructionFiles.filter { file ->
            (search.fileNo.isNullOrBlank() || file.fileNumber?.toString() == search.fileNo) &&
                (search.reqNo.isNullOrBlank() || file.requestNumber?.toString() == search.reqNo) &&
                (search.workshopId.isNullOrBlank() || file.workshopInfo?.workshopId == search.workshopId) &&
                (search.branchCode.isNullOrBlank() || file.workshopInfo?.brhCode == search.branchCode)
        }
    }

    private val firstNames = listOf("علی", "زهرا", "حسین", "فاطمه", "محمد", "مریم", "رضا")
    private val lastNames = listOf("توکلی", "احمدی", "رضایی", "کریمی", "موسوی", "نجفی", "قاسمی")

    /**
     * 7 rows spanning: owner ("01"), applicant ("02"), an unrecognized/legacy ownerType code
     * (neither [BeneficiaryConstructionDN.ownerType] value the UI's `isOwner`/`isApplicant` know
     * about — checks the fallback path), a row missing its mobile number, one missing its last
     * name, and one entirely missing its nationalCode (server sometimes omits it pre-verification).
     */
    fun beneficiaries(
        requestNumber: Long?,
        fileNumber: Long?,
        requestDate: String?,
    ): List<BeneficiaryConstructionDN> = (0 until 7).map { i ->
        BeneficiaryConstructionDN(
            nationalCode = if (i == 6) null else "093012345$i",
            ownerType = when {
                i == 5 -> "09" // unknown/legacy code — exercises the isOwner==false && isApplicant==false path
                i % 2 == 0 -> "01"
                else -> "02"
            },
            requestNumber = requestNumber ?: 123_456_789L,
            fileNumber = fileNumber ?: 4_479_890_882L,
            requestDate = requestDate ?: "14020901",
            name = firstNames[i % firstNames.size],
            lastName = if (i == 4) null else lastNames[i % lastNames.size],
            mobile = if (i == 3) null else "0912345${6700 + i}",
        )
    }

    private val paymentStatuses = listOf("پرداخت شده", "در انتظار پرداخت", "منقضی شده")

    /**
     * 6 rows: a null `paymentDate` (still pending), a zero-amount row, and one whose
     * `buildingRequest` is entirely null (server sometimes omits the nested summary) — all real
     * shapes the UI's `?:`/`orEmpty()` fallbacks need to survive, not just fully-populated rows.
     */
    fun paymentSheets(debitNumber: String): List<PaymentSheetConstructionFileDN> =
        (0 until 6).map { i ->
            PaymentSheetConstructionFileDN(
                orderNumber = "${i + 1}",
                paymentCode = "3600${debitNumber.takeLast(6)}0$i",
                paymentSheetAmount = if (i == 5) 0L else 500_000L * (i + 1),
                status = paymentStatuses[i % paymentStatuses.size],
                paymentDate = if (i % 3 == 1) null else "140210${10 + i}",
                buildingRequest = if (i == 5) {
                    null
                } else {
                    BuildingRequestSummaryDN(
                        debitNumber = debitNumber,
                        fileNumber = 4_479_890_882L,
                        requestNumber = 123_456_789L,
                        requestDate = "14020901",
                        totalPayment = 1_850_000L,
                        paymentDeadLine = "14021001",
                        workshopInfo = workshops[i % workshops.size],
                    )
                },
            )
        }

    fun certificatePdf(): PdfDownloadDN =
        PdfDownloadDN(pdf = InputStreamDN(pdf = ByteReadChannel(buildMockPdfBytes())))

    fun issuanceMessage(debitNumber: String): String =
        "برگه پرداخت برای بدهی $debitNumber با موفقیت صادر شد (داده آزمایشی)."

    private val debitSteps = listOf("قسط اول", "قسط دوم", "قسط سوم", "قسط چهارم", "تسویه نهایی")
    private val debitStatuses = listOf("پرداخت شده", "سررسید نشده", "معوق")

    /** 6 rows: one fully paid off (`remainingAmount = 0`) and one missing its end date (open-ended). */
    fun installmentLetters(workshopId: String, branchId: String): List<InstallmentLetterDN> =
        (0 until 6).map { i ->
            InstallmentLetterDN(
                workshopId = workshopId,
                debitNumber = "77${branchId}0000$i",
                debitStepDescription = debitSteps[i % debitSteps.size],
                debitStatusDescription = debitStatuses[i % debitStatuses.size],
                debitStartDate = "1402${10 + (i % 2)}01",
                debitEndDate = if (i == 4) null else "1403${10 + (i % 2)}01",
                remainingAmount = if (i == 5) 0L else 400_000L * (5 - i),
                debitNumberOld = if (i == 0) null else "OLD-77$branchId-000$i",
            )
        }

    /**
     * Builds a small, byte-exact, genuinely renderable one-page PDF (not just a placeholder blob)
     * so [com.tamin.taminhamrah.ui.components.TaminPdfViewer] has something real to show while
     * testing «گواهی پرداخت حق بیمه». Offsets are computed from actual string lengths rather than
     * hardcoded, since PDF's xref table must point at exact byte positions.
     */
    private fun buildMockPdfBytes(): ByteArray {
        val header = "%PDF-1.4\n"
        val objects = listOf(
            "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n",
            "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n",
            "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 320 220] " +
                "/Resources << /Font << /F1 5 0 R >> >> /Contents 4 0 R >>\nendobj\n",
            run {
                val stream = "BT /F1 14 Tf 20 110 Td (Mock Payment Certificate - Test Data) Tj ET"
                "4 0 obj\n<< /Length ${stream.length} >>\nstream\n$stream\nendstream\nendobj\n"
            },
            "5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n",
        )

        val offsets = mutableListOf<Int>()
        var pos = header.length
        for (obj in objects) {
            offsets.add(pos)
            pos += obj.length
        }
        val xrefStart = pos

        val pdf = buildString {
            append(header)
            objects.forEach { append(it) }
            append("xref\n0 ${objects.size + 1}\n")
            append("0000000000 65535 f \n")
            offsets.forEach { offset ->
                append(offset.toString().padStart(10, '0')).append(" 00000 n \n")
            }
            append("trailer\n<< /Size ${objects.size + 1} /Root 1 0 R >>\n")
            append("startxref\n$xrefStart\n%%EOF")
        }
        return pdf.encodeToByteArray()
    }
}
