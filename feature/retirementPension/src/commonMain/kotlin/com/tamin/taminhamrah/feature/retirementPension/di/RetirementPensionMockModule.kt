package com.tamin.taminhamrah.feature.retirementPension.di

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.history.WageDetailDN
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.EdictPensionerInboxDN
import com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.PayRollInboxDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDN
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.pension.retirement.JobDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestCreatedDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestFormDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.model.pension.retirement.WorkDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.model.personal.PersonalDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.pension.AuthenticationAndGetPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.pension.CheckRetirementStatusUseCase
import com.tamin.taminhamrah.useCases.pension.CreateRetirementRequestUseCase
import com.tamin.taminhamrah.useCases.pension.GetAuthenticationCodeUseCase
import com.tamin.taminhamrah.useCases.pension.GetRetirementRequestInfoUseCase
import com.tamin.taminhamrah.useCases.pension.GetUserAgeUseCase
import com.tamin.taminhamrah.useCases.pension.SendRetirementDocumentUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.dsl.module
import kotlin.time.Duration.Companion.milliseconds

/**
 * In-memory stand-ins for the retirement service, so the whole eight-step flow can be walked on an
 * emulator without an account the service actually has a pension case for.
 *
 * Loaded only when [RETIREMENT_PENSION_USE_MOCK_DATA] is on. It replaces the **pension and history**
 * use cases only — identity, branch and image upload stay on the real services, so what you see for
 * your own name and branch is still real.
 *
 * Delete this file, and the flag beside it, once there is a test account.
 */
val retirementPensionMockModule = module {
    val pension: PensionRepository = MockRetirementPensionRepository()
    val history: HistoryRepository = MockRetirementHistoryRepository()

    factory { GetUserAgeUseCase(pension) }
    factory { CheckRetirementStatusUseCase(pension) }
    factory { GetAuthenticationCodeUseCase(pension) }
    factory { AuthenticationAndGetPersonalInfoUseCase(pension) }
    factory { GetRetirementRequestInfoUseCase(pension) }
    factory { CreateRetirementRequestUseCase(pension) }
    factory { SendRetirementDocumentUseCase(pension) }
    factory { GetTalfighInfosUseCase(history) }
    factory { GetDastmozdInfosUseCase(history) }
}

/** The code that makes step 2 report «کد وارد شده اشتباه است.», for exercising the failure path. */
const val MOCK_REJECTED_OTP: Long = 111_111L

private const val NETWORK_DELAY_MILLIS = 600L

/**
 * Answers the retirement calls from memory, and remembers what the flow has already done: creating
 * the request and uploading each set of documents moves the status on, so the tracking screen shows
 * real progress rather than a fixed stage.
 */
private class MockRetirementPensionRepository : PensionRepository {

    private var requestId: String? = null
    private var statusCode: String? = null

    override suspend fun getUserAge(filters: List<ApiFilterDN>): Flow<AgeDN> = respond {
        // Comfortably over the gate; swap the years to something under 50 to see the age dialog.
        AgeDN(age = "62,3,12", birthDate = "1343/02/18")
    }

    override suspend fun checkRetirementStatus(): Flow<RetirementStatusDN> = respond {
        RetirementStatusDN(requestId = requestId, requestStatusCode = statusCode)
    }

    override suspend fun getAuthenticationCode(): Flow<AuthenticationTicketDN> = respond {
        AuthenticationTicketDN(mobileNumber = "09126226066")
    }

    override suspend fun authenticationAndGetPersonalInfo(
        authenticationsCode: Long
    ): Flow<RetirementPersonalDN> = respond {
        RetirementPersonalDN(
            branch = "1101",
            branchName = "یک تهران",
            insuranceId = "0010692308",
            mobileNumber = "09126226066",
            organizationId = "1",
            personal = PersonalDN(
                firstName = "رضا",
                lastName = "دریکوند",
                fatherName = "محمود",
                nationalId = "4060434061",
                ssn = "0010692308",
                genderDesc = "مرد",
                genderCode = "01",
                dateOfBirth = BIRTH_DATE_EPOCH_MILLIS,
                idCardNumber = "1147",
                contactAddress = "تهران، خیابان ولیعصر، کوچهٔ بهار، پلاک ۱۲، واحد ۳",
                contactPhoneNumber = "02188776655",
            ),
            provinceName = "خرم‌آباد",
            work = WorkDN(
                job = JobDN(
                    jobCode = "1",
                    jobDescription = "تولیدی صنعتی",
                    status = null,
                    statusDate = null,
                ),
                workshopId = "1024300719",
            ),
            strAge = "62,3,12",
            // Any code but MOCK_REJECTED_OTP verifies, so the happy path needs no memorizing.
            verificationResult = "ticketNotFound".takeIf { authenticationsCode == MOCK_REJECTED_OTP },
        )
    }

    override suspend fun getRetirementRequestInfo(
        filters: List<ApiFilterDN>
    ): Flow<List<RetirementRequestDN>> = respond {
        listOf(
            RetirementRequestDN(
                activityType = "تولیدی",
                address = "تهران، خیابان ولیعصر، کوچهٔ بهار، پلاک ۱۲، واحد ۳",
                age = "62,3,12",
                birthDate = BIRTH_DATE_EPOCH_MILLIS,
                branchCode = "1101",
                fatherName = "محمود",
                firstName = "رضا",
                gender = "01",
                insuranceNumber = "0010692308",
                issuePlace = "خرم‌آباد",
                idNumber = "1147",
                lastName = "دریکوند",
                mobileNumber = "09126226066",
                nationalCode = "4060434061",
                phoneNumber = "02188776655",
                workshopAddress = "تهران، کیلومتر ۸ جادهٔ مخصوص، خیابان دوم، پلاک ۴",
                workshopCode = "1024300719",
                workshopName = "شرکت ارد پارس اسپادانا",
                managerName = "مهدی کریمی",
            ),
        )
    }

    override suspend fun createRetirementRequest(
        authenticationsCode: Long,
        form: RetirementRequestFormDN
    ): Flow<RetirementRequestCreatedDN> = respond {
        requestId = MOCK_REQUEST_ID.toString()
        statusCode = STATUS_UPLOAD_IDENTITY_DOCUMENTS
        RetirementRequestCreatedDN(requestId = MOCK_REQUEST_ID)
    }

    override suspend fun sendRetirementDocument(
        requestId: String,
        request: RetirementSaveDocumentDN
    ): Flow<String?> = respond {
        val sentQuitLetter = request.pensionRequestDocList
            ?.any { it.documentType == QUIT_LETTER_DOCUMENT_TYPE } == true
        statusCode = if (sentQuitLetter) STATUS_BRANCH_REVIEW else STATUS_UPLOAD_QUIT_LETTER
        null
    }

    /** Emits after a short pause, so loading and shimmer states are actually visible. */
    private fun <T> respond(value: suspend () -> T): Flow<T> = flow {
        delay(NETWORK_DELAY_MILLIS.milliseconds)
        emit(value())
    }

    // --- the rest of the interface: other pension screens keep their own real services ---

    override suspend fun getPensionInquiry(filters: List<ApiFilterDN>): Flow<List<PensionInquiryDN>> =
        notMocked()

    override suspend fun getPensionerId(): Flow<List<PensionIdDN>> = notMocked()

    override suspend fun getEdictPensioner(query: ApiQueryParamDN): Flow<EdictPensionerDN?> =
        notMocked()

    override suspend fun sendRequestDeferredInstallmentCertificate(
        request: DeferredInstallmentRequestDN
    ): Flow<DeferredInstallmentCertificateDN> = notMocked()

    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>): Flow<List<PayRollDN>> =
        notMocked()

    override suspend fun getDisabilityPersonalInfo(): Flow<DisabilityPersonalInfoDN> = notMocked()

    override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> =
        notMocked()

    override suspend fun getEdictReportPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> =
        notMocked()

    override suspend fun sendEdictPensionerToMyInbox(
        filters: List<ApiFilterDN>
    ): Flow<EdictPensionerInboxDN> = notMocked()

    override suspend fun sendPayRollToInbox(filters: List<ApiFilterDN>): Flow<PayRollInboxDN> =
        notMocked()

    override suspend fun sendRequestInquirePensionCertificate(
        filters: List<ApiFilterDN>
    ): Flow<InquirePensionCertificateDN> = notMocked()

    private fun <T> notMocked(): Flow<T> =
        error("Mock retirement data is on; this call belongs to another service")

    private companion object {
        const val MOCK_REQUEST_ID = 2_982_515_668L
        const val BIRTH_DATE_EPOCH_MILLIS = -179_884_800_000L

        /** Codes from `RetirementStage`, so the tracking pipeline lands on a real rung. */
        const val STATUS_UPLOAD_IDENTITY_DOCUMENTS = "0015"
        const val STATUS_UPLOAD_QUIT_LETTER = "0046"
        const val STATUS_BRANCH_REVIEW = "0048"

        const val QUIT_LETTER_DOCUMENT_TYPE = "18"
    }
}

/** Thirty-one years of history at a flat wage, which is what step 5 reports on. */
private class MockRetirementHistoryRepository : HistoryRepository {

    override suspend fun getTalfighInfos(filters: List<ApiFilterDN>): TalfighInfoDN {
        delay(NETWORK_DELAY_MILLIS.milliseconds)
        return TalfighInfoDN(
            list = listOf(
                TalfighInfoItemDN(
                    months = emptyList(),
                    risuid = "0010692308",
                    historyYears = 31,
                    historyMonths = 4,
                    sumYear = 31,
                    historyDays = 12,
                    sumHistoryYears = 11_447,
                    id = 1,
                    hisYear = "1404",
                ),
            ),
            total = 1,
        )
    }

    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>): DastmozdInfoDN {
        delay(NETWORK_DELAY_MILLIS.milliseconds)
        // Two full years at the design's 85,400,000 monthly wage, so the average comes back to it.
        val year = DastmozdInfoItemDN(
            wageDetails = List(MONTHS_IN_YEAR) { WageDetailDN(month = "30", wage = "85400000") },
            hisyear = "1404",
            id = 1,
            risufname = null,
            risubirthdate = null,
            risuidserial2 = null,
            risuidserial1 = null,
            rwshname = "شرکت ارد پارس اسپادانا",
            expcitycode = null,
            brhcode = "1101",
            risuidno = null,
            risudname = null,
            risuid = "0010692308",
            risulname = null,
            risunatcode = null,
            brhname = "یک تهران",
            historytypedesc = null,
            rwshid = "1024300719",
        )
        return DastmozdInfoDN(list = listOf(year.copy(hisyear = "1403"), year), total = 2)
    }

    override suspend fun getUserInfos(): UserInfoDN =
        error("Mock retirement data is on; this call belongs to another service")

    override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) =
        error("Mock retirement data is on; this call belongs to another service")

    override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> =
        error("Mock retirement data is on; this call belongs to another service")

    private companion object {
        const val MONTHS_IN_YEAR = 12
    }
}
