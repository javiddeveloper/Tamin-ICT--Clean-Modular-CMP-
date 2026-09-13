package com.tamin.taminhamrah.feature.taminServices.occurrence

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.ErrorSource
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceEvent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceIntent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.WorkshopItemPR
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.occurrence.InsuredRelationDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDN
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceResultDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceSubmitRequestDN
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.repository.occurrence.OccurrenceRepository
import com.tamin.taminhamrah.useCases.history.GetUserInfosUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetAllWorkshopsUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetInsuredRelationUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetOccurrenceDocTypesUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetOccurrencePersonalInfoUseCase
import com.tamin.taminhamrah.useCases.occurrence.GetWorkshopSpecUseCase
import com.tamin.taminhamrah.useCases.occurrence.SubmitOccurrenceUseCase
import com.tamin.taminhamrah.useCases.occurrence.UploadOccurrenceImageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Covers loadInitialData's gated fetch sequence, the workshop-select prefill flow, submit request
 * mapping, and the document upload/removal flow. Occurrence's [OccurrenceIntent.UploadDocument]
 * takes already-read [fileName]/[fileBytes] directly (the JPEG/size/duplicate validation that
 * orotez-protez does in its ViewModel happens in [com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps.Step6DocumentSubmitStep]
 * before the intent is sent), so — unlike orotez-protez's document-upload test — this needs no
 * [io.github.vinceglb.filekit.PlatformFile]/Robolectric setup; it exercises the same
 * upload-success / upload-failure / remove-document contract directly through the fake repository.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OccurrenceViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var occurrenceRepository: FakeOccurrenceRepository
    private lateinit var historyRepository: FakeHistoryRepository
    private lateinit var viewModel: OccurrenceViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        occurrenceRepository = FakeOccurrenceRepository()
        historyRepository = FakeHistoryRepository().apply { userInfoResult = defaultUserInfo() }
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = OccurrenceViewModel(
        getPersonalInfoUseCase = GetOccurrencePersonalInfoUseCase(occurrenceRepository),
        getUserInfosUseCase = GetUserInfosUseCase(historyRepository),
        getAllWorkshopsUseCase = GetAllWorkshopsUseCase(occurrenceRepository),
        getWorkshopSpecUseCase = GetWorkshopSpecUseCase(occurrenceRepository),
        getInsuredRelationUseCase = GetInsuredRelationUseCase(occurrenceRepository),
        getDocTypesUseCase = GetOccurrenceDocTypesUseCase(occurrenceRepository),
        uploadImageUseCase = UploadOccurrenceImageUseCase(occurrenceRepository),
        submitOccurrenceUseCase = SubmitOccurrenceUseCase(occurrenceRepository),
    )

    // ── LoadInitialData (fired from init) ────────────────────────────────────

    @Test
    fun `LoadInitialData loads user info, workshops, doc types and insured relation`() = runTest(testDispatcher) {
        occurrenceRepository.allWorkshopsResult = listOf(sampleWorkshopDN())
        occurrenceRepository.documentTypesResult = listOf(OccurrenceDocTypeDN(id = 1, title = "گزارش حادثه"))
        occurrenceRepository.insuredRelationResult = InsuredRelationDN(
            insuranceTypeCode = "01", insuranceType = "اصلی", branchCode = "10", branchName = "شعبه مرکزی",
        )

        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.personInfo.userInfo)
        assertEquals(1, state.workshop.workshops.size)
        assertEquals(1, state.documentSubmit.docTypes.size)
        assertEquals("01", state.jobDetails.insuranceTypeCode)
        assertEquals("شعبه مرکزی", state.jobDetails.branchName)
        assertFalse(state.isLoading)
        assertTrue(state.errors.isEmpty())
    }

    @Test
    fun `LoadInitialData when user info fails records only a USER_INFO error`() = runTest(testDispatcher) {
        historyRepository.shouldThrowError = true
        historyRepository.error = RuntimeException("user info failed")

        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.errors.containsKey(ErrorSource.USER_INFO))
        assertNull(state.personInfo.userInfo)
        assertTrue(state.workshop.workshops.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `RetrySource WORKSHOPS re-fetches only the workshop list`() = runTest(testDispatcher) {
        occurrenceRepository.allWorkshopsResult = emptyList()
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.workshop.workshops.isEmpty())

        occurrenceRepository.allWorkshopsResult = listOf(sampleWorkshopDN())
        viewModel.sendIntent(OccurrenceIntent.RetrySource(ErrorSource.WORKSHOPS))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.workshop.workshops.size)
    }

    // ── Step navigation ───────────────────────────────────────────────────────

    @Test
    fun `GoToNextStep advances to the next step`() = runTest(testDispatcher) {
        viewModel.sendIntent(OccurrenceIntent.GoToNextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(OccurrenceStep.WORKSHOP_INFO, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `GoToPreviousStep on the first step sends NavigateBack event`() = runTest(testDispatcher) {
        viewModel.events.test {
            viewModel.sendIntent(OccurrenceIntent.GoToPreviousStep)
            assertEquals(OccurrenceEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GoToPreviousStep on a later step moves back one step`() = runTest(testDispatcher) {
        viewModel.sendIntent(OccurrenceIntent.GoToNextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(OccurrenceIntent.GoToPreviousStep)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(OccurrenceStep.PERSON_INFO, viewModel.uiState.value.currentStep)
    }

    // ── SelectWorkshop ────────────────────────────────────────────────────────

    @Test
    fun `SelectWorkshop success prefills workshop and job details from spec and personal info`() = runTest(testDispatcher) {
        occurrenceRepository.workshopSpecResult = sampleWorkshopDN()
        occurrenceRepository.personalInfoResult = OccurrencePersonalInfoDN(
            nationalCode = "0012345678", firstName = "علی", lastName = "رضایی", fatherName = "",
            gender = "01", birthDate = "", insuranceNumber = "1234567", branchCode = "10",
            nationality = "ایرانی", insuranceType = "اصلی",
        )

        viewModel.sendIntent(OccurrenceIntent.SelectWorkshop(sampleWorkshopPR()))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("شرکت الف", state.workshop.employerName)
        assertFalse(state.workshop.isWorkshopSpecLoading)
        assertEquals("علی رضایی", state.jobDetails.fullName)
        assertEquals("01", state.jobDetails.gender)
        assertNotNull(state.personInfo.personalInfo)
    }

    @Test
    fun `SelectWorkshop failure resets loading flag and shows a toast`() = runTest(testDispatcher) {
        occurrenceRepository.shouldThrowError = true
        occurrenceRepository.error = RuntimeException("workshop spec failed")

        viewModel.events.test {
            viewModel.sendIntent(OccurrenceIntent.SelectWorkshop(sampleWorkshopPR()))
            val event = assertIs<OccurrenceEvent.ShowToast>(awaitItem())
            assertTrue(event.message.isNotBlank())
            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.uiState.value.workshop.isWorkshopSpecLoading)
    }

    // ── UploadDocument / RemoveDocument ──────────────────────────────────────

    @Test
    fun `UploadDocument success stores the uploaded document with the returned guid`() = runTest(testDispatcher) {
        occurrenceRepository.uploadImageResult = "uploaded-guid-1"
        val fileBytes = byteArrayOf(1, 2, 3)

        viewModel.sendIntent(
            OccurrenceIntent.UploadDocument(typeId = 1, typeName = "گزارش حادثه", fileName = "photo.jpg", fileBytes = fileBytes)
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.documentSubmit.isUploadingDoc)
        assertEquals(1, state.documentSubmit.uploadedDocuments.size)
        val doc = state.documentSubmit.uploadedDocuments.first()
        assertEquals("uploaded-guid-1", doc.guid)
        assertEquals("گزارش حادثه", doc.typeName)
        assertEquals("photo.jpg", doc.fileName)

        val (lastFileName, lastFileBytes) = requireNotNull(occurrenceRepository.lastUploadImageParams)
        assertEquals("photo.jpg", lastFileName)
        assertEquals(fileBytes.toList(), lastFileBytes.toList())
    }

    @Test
    fun `UploadDocument failure shows a toast and leaves the document list untouched`() = runTest(testDispatcher) {
        occurrenceRepository.shouldThrowError = true
        occurrenceRepository.error = RuntimeException("upload failed")

        viewModel.events.test {
            viewModel.sendIntent(
                OccurrenceIntent.UploadDocument(typeId = 1, typeName = "گزارش حادثه", fileName = "photo.jpg", fileBytes = byteArrayOf(1, 2, 3))
            )
            val event = assertIs<OccurrenceEvent.ShowToast>(awaitItem())
            assertTrue(event.message.isNotBlank())
            cancelAndIgnoreRemainingEvents()
        }

        val state = viewModel.uiState.value
        assertFalse(state.documentSubmit.isUploadingDoc)
        assertTrue(state.documentSubmit.uploadedDocuments.isEmpty())
    }

    @Test
    fun `RemoveDocument removes the previously uploaded document by guid`() = runTest(testDispatcher) {
        occurrenceRepository.uploadImageResult = "guid-a"
        viewModel.sendIntent(
            OccurrenceIntent.UploadDocument(typeId = 1, typeName = "گزارش حادثه", fileName = "a.jpg", fileBytes = byteArrayOf(1))
        )
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.documentSubmit.uploadedDocuments.size)

        viewModel.sendIntent(OccurrenceIntent.RemoveDocument("guid-a"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.documentSubmit.uploadedDocuments.isEmpty())
    }

    // ── SubmitOccurrence ──────────────────────────────────────────────────────

    @Test
    fun `SubmitOccurrence success maps ui state into the request and stores the tracking code`() = runTest(testDispatcher) {
        occurrenceRepository.workshopSpecResult = sampleWorkshopDN()
        occurrenceRepository.personalInfoResult = OccurrencePersonalInfoDN(
            nationalCode = "0012345678", firstName = "علی", lastName = "رضایی", fatherName = "",
            gender = "01", birthDate = "", insuranceNumber = "1234567", branchCode = "10",
            nationality = "ایرانی", insuranceType = "اصلی",
        )
        occurrenceRepository.submitResult = OccurrenceResultDN(trackingCode = "TRACK-9")
        viewModel.sendIntent(OccurrenceIntent.SelectWorkshop(sampleWorkshopPR()))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(OccurrenceIntent.SubmitOccurrence)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSubmitting)
        assertEquals("TRACK-9", state.dialogs.successTrackingCode)

        val request: OccurrenceSubmitRequestDN = requireNotNull(occurrenceRepository.lastSubmitRequest)
        assertEquals("0012345678", request.nationalCode)
        assertEquals(1, request.gender) // GenderPR.MALE.legacyCode
        // workshopId is the selected WorkshopItemPR's workshopCode, not its (list-selection-only,
        // possibly composite) id — see OccurrenceViewModel.submitOccurrence()'s
        // `workshopId = selectedWorkshop?.workshopCode`.
        assertEquals("1412345", request.workshopId)
        assertEquals("014", request.workshopBranchCode)
        // Iranian nationality (nationCode "01" from the workshop spec) -> reporterType "1".
        assertEquals("1", request.reporterType)
    }

    @Test
    fun `SubmitOccurrence maps a non-Iranian nationality to reporterType 2`() = runTest(testDispatcher) {
        occurrenceRepository.workshopSpecResult = sampleWorkshopDN().copy(nationalityCode = "02")
        occurrenceRepository.personalInfoResult = OccurrencePersonalInfoDN(
            nationalCode = "0012345678", firstName = "علی", lastName = "رضایی", fatherName = "",
            gender = "01", birthDate = "", insuranceNumber = "1234567", branchCode = "10",
            nationality = "غیر ایرانی", insuranceType = "اصلی",
        )
        occurrenceRepository.submitResult = OccurrenceResultDN(trackingCode = "TRACK-9")
        viewModel.sendIntent(OccurrenceIntent.SelectWorkshop(sampleWorkshopPR()))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(OccurrenceIntent.SubmitOccurrence)
        testDispatcher.scheduler.advanceUntilIdle()

        val request: OccurrenceSubmitRequestDN = requireNotNull(occurrenceRepository.lastSubmitRequest)
        assertEquals("2", request.reporterType)
    }

    @Test
    fun `SubmitOccurrence failure shows a toast and resets the submitting flag`() = runTest(testDispatcher) {
        occurrenceRepository.shouldThrowError = true
        occurrenceRepository.error = RuntimeException("submit failed")

        viewModel.events.test {
            viewModel.sendIntent(OccurrenceIntent.SubmitOccurrence)
            val event = assertIs<OccurrenceEvent.ShowToast>(awaitItem())
            assertTrue(event.message.isNotBlank())
            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private fun sampleWorkshopDN() = WorkshopItemDN(
        id = "1", workshopCode = "1412345", branchCode = "014", name = "کارگاه تولیدی الف",
        employerName = "شرکت الف", employerPhone = "02112345678", address = "تهران",
        postalCode = "1234567890", phone = "02112345678", nationality = "ایرانی", nationalityCode = "01",
    )

    private fun sampleWorkshopPR() = WorkshopItemPR(id = "1", workshopCode = "1412345", branchCode = "014")

    // ── Test doubles ──────────────────────────────────────────────────────────

    private class FakeOccurrenceRepository : OccurrenceRepository {
        var shouldThrowError = false
        var error: Throwable = RuntimeException("Fake Occurrence Repository Error")

        var personalInfoResult = OccurrencePersonalInfoDN(
            nationalCode = "", firstName = "", lastName = "", fatherName = "", gender = "",
            birthDate = "", insuranceNumber = "", branchCode = "", nationality = "", insuranceType = "",
        )
        var allWorkshopsResult: List<WorkshopItemDN> = emptyList()
        var workshopSpecResult = WorkshopItemDN(
            id = "", workshopCode = "", branchCode = "", name = "", employerName = "", employerPhone = "",
            address = "", postalCode = "", phone = "", nationality = "", nationalityCode = "",
        )
        var insuredRelationResult = InsuredRelationDN(
            insuranceTypeCode = "", insuranceType = "", branchCode = "", branchName = "",
        )
        var documentTypesResult: List<OccurrenceDocTypeDN> = emptyList()
        var uploadImageResult: String = "guid-1"
        var submitResult = OccurrenceResultDN(trackingCode = "")

        var lastUploadImageParams: Pair<String, ByteArray>? = null
        var lastSubmitRequest: OccurrenceSubmitRequestDN? = null

        override suspend fun getPersonalInfo(
            nationalCode: String,
            birthDate: String,
            workshopCode: String,
            branchCode: String,
        ): OccurrencePersonalInfoDN {
            if (shouldThrowError) throw error
            return personalInfoResult
        }

        override suspend fun getAllWorkshops(nationalCode: String): List<WorkshopItemDN> {
            if (shouldThrowError) throw error
            return allWorkshopsResult
        }

        override suspend fun getWorkshopSpec(workshopCode: String, branchCode: String): WorkshopItemDN {
            if (shouldThrowError) throw error
            return workshopSpecResult
        }

        override suspend fun getInsuredRelation(nationalCode: String): InsuredRelationDN {
            if (shouldThrowError) throw error
            return insuredRelationResult
        }

        override suspend fun getDocumentTypes(): List<OccurrenceDocTypeDN> {
            if (shouldThrowError) throw error
            return documentTypesResult
        }

        override suspend fun uploadImage(fileName: String, fileBytes: ByteArray): String {
            lastUploadImageParams = fileName to fileBytes
            if (shouldThrowError) throw error
            return uploadImageResult
        }

        override suspend fun submitOccurrence(request: OccurrenceSubmitRequestDN): OccurrenceResultDN {
            lastSubmitRequest = request
            if (shouldThrowError) throw error
            return submitResult
        }
    }

    private class FakeHistoryRepository : HistoryRepository {
        var shouldThrowError = false
        var error: Throwable = RuntimeException("Fake History Repository Error")
        var userInfoResult: UserInfoDN = defaultUserInfo()

        override suspend fun getTalfighInfos(filters: List<ApiFilterDN>) = TalfighInfoDN(list = emptyList(), total = 0)
        override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>) = DastmozdInfoDN(list = emptyList(), total = 0)

        override suspend fun getUserInfos(): UserInfoDN {
            if (shouldThrowError) throw error
            return userInfoResult
        }

        override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) {
            if (shouldThrowError) throw error
        }

        override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> = flow {
            emit(HistoryJobInfoDN(list = emptyList(), total = 0))
        }

        // Added to HistoryRepository by the «کلیه سوابق» work; nothing here exercises them.
        override suspend fun getUserRole(): UserRoleDN = error("not used in OccurrenceViewModelTest")
        override suspend fun sendHistoryNotice(): String? = error("not used in OccurrenceViewModelTest")
        override fun downloadHistoryReport(
            type: HistoryCertificateType,
        ): Flow<PdfDownloadDN> = error("not used in OccurrenceViewModelTest")
    }
}

private fun defaultUserInfo(
    nationalID: String? = "0012345678",
    firstName: String? = "علی",
    lastName: String? = "رضایی",
    insuranceNumber: String? = "1234567",
) = UserInfoDN(
    serial1 = null, militaryServiceCode = null, fatherName = null,
    lastName = lastName, serial2 = null, creationTime = null,
    lastModificationTime = null, cityCode = null, socialSecurityNumber = null,
    lastModifiedBy = null, issueplaceName = null, birthDate = null,
    firstName = firstName, insuranceNumber = insuranceNumber,
    genderCode = null, nationalID = nationalID, marriageCode = null,
    createdBy = null, identityNumber = null, countryCode = null,
    id = null, birthDateTimestamp = null, issueplace = null, nationCode = null,
)
