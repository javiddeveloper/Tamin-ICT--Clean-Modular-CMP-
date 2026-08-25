package com.tamin.taminhamrah.feature.pensionSurvivor.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.pensionSurvivor.fake.FakeContractsRepository
import com.tamin.taminhamrah.feature.pensionSurvivor.fake.FakePensionSurvivorPersonalRepository
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.DeceasedDocumentChecklist
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorEvent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.SurvivorContactDraft
import com.tamin.taminhamrah.model.personal.PersonalDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedPersonalDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentDN
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.personal.survivorList.RequestModelDN
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.personal.GetAgeUseCase
import com.tamin.taminhamrah.useCases.personal.GetConfirmSurvivorsListUseCase
import com.tamin.taminhamrah.useCases.personal.GetDeceasedInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetFinalSurvivorPensionPDFUseCase
import com.tamin.taminhamrah.useCases.personal.GetPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetSurvivorListUseCase
import com.tamin.taminhamrah.useCases.personal.SubmitFinalSurvivorPensionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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

@OptIn(ExperimentalCoroutinesApi::class)
class PensionSurvivorViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FakePensionSurvivorPersonalRepository
    private lateinit var contractsRepository: FakeContractsRepository
    private lateinit var uploadImageUseCase: UploadImageUseCase
    private lateinit var viewModel: PensionSurvivorViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakePensionSurvivorPersonalRepository().apply {
            personalInfoResult = PersonalInfoDN(
                insuranceId = null,
                branch = null,
                mobileNumber = null,
                provinceName = null,
                personal = PersonalDN(
                    firstName = "Ali",
                    lastName = "Ahmadi",
                    fatherName = null,
                    nationalId = "0012345678",
                    ssn = null,
                    genderDesc = null,
                    dateOfBirth = null,
                ),
            )
            deceasedInfoResult = sampleDeceasedInfo()
        }
        contractsRepository = FakeContractsRepository()
        uploadImageUseCase = UploadImageUseCase(contractsRepository)
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun refreshSurvivors_invalidatesFinalPdfAndBumpsRevision() = runTest(testDispatcher) {
        repository.survivorListResult = listOf(sampleSurvivorDn())
        repository.confirmSurvivorsListResult = listOf(ConfirmSurvivorDN(RequestModelDN(42)))
        repository.pdfDownloadResult = PdfDownloadDN()

        moveToFinalStep()
        viewModel.sendIntent(PensionSurvivorIntent.DownloadFinalPdf)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(PensionSurvivorIntent.PdfConfirmedChanged(true))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(PensionSurvivorIntent.SubmitFinal)
        testDispatcher.scheduler.advanceUntilIdle()

        val beforeRefresh = viewModel.uiState.value
        assertEquals(42, beforeRefresh.requestId)
        assertNotNull(beforeRefresh.viewerPdf)
        assertEquals(true, beforeRefresh.isPdfConfirmed)
        assertEquals(true, beforeRefresh.showSuccessDialog)

        viewModel.sendIntent(PensionSurvivorIntent.RefreshSurvivors)
        testDispatcher.scheduler.advanceUntilIdle()

        val refreshed = viewModel.uiState.value
        assertNull(refreshed.requestId)
        assertNull(refreshed.viewerPdf)
        assertFalse(refreshed.viewerDownloadFailed)
        assertFalse(refreshed.isPdfConfirmed)
        assertFalse(refreshed.showSuccessDialog)
        assertEquals(beforeRefresh.finalPdfRevision + 1, refreshed.finalPdfRevision)
    }

    @Test
    fun openSurvivor_emitsSavedDraftForThatNationalId() = runTest(testDispatcher) {
        val draft = SurvivorContactDraft(
            address = "Tehran",
            phoneNumber = "02122223333",
            mobileNumber = "09121234567",
        )

        viewModel.sendIntent(PensionSurvivorIntent.DeceasedNationalIdChanged("1234567890"))
        viewModel.sendIntent(PensionSurvivorIntent.SurvivorContactSaved("0098765432", draft))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(PensionSurvivorIntent.OpenSurvivor(sampleSurvivor()))

            val event = assertIs<PensionSurvivorEvent.NavigateToSurvivorInfo>(awaitItem())
            assertEquals("1234567890", event.deceasedNationalId)
            assertEquals(draft, event.draft)
        }
    }

    @Test
    fun nextStepFromSurvivors_picksHighestRequestIdAndWarnsWhenMultipleExist() = runTest(testDispatcher) {
        repository.confirmSurvivorsListResult = listOf(
            ConfirmSurvivorDN(RequestModelDN(3)),
            ConfirmSurvivorDN(RequestModelDN(9)),
            ConfirmSurvivorDN(RequestModelDN(5)),
        )

        viewModel.sendIntent(PensionSurvivorIntent.CommitmentChanged(true))
        viewModel.sendIntent(PensionSurvivorIntent.NextStep)
        viewModel.sendIntent(PensionSurvivorIntent.DeceasedNationalIdChanged("1234567890"))
        viewModel.sendIntent(PensionSurvivorIntent.SearchDeceased)
        testDispatcher.scheduler.advanceUntilIdle()
        uploadAllDeceasedDocuments()

        viewModel.sendIntent(PensionSurvivorIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(PensionSurvivorIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(PensionSurvivorStep.Final, viewModel.uiState.value.currentStep)
        assertEquals(9, viewModel.uiState.value.requestId)
        assertEquals(emptyList(), repository.lastConfirmSurvivorsFilters)
    }

    private fun moveToFinalStep() {
        moveToDeceasedStepWithDocuments()
        viewModel.sendIntent(PensionSurvivorIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(PensionSurvivorIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun moveToDeceasedStepWithDocuments() {
        viewModel.sendIntent(PensionSurvivorIntent.CommitmentChanged(true))
        viewModel.sendIntent(PensionSurvivorIntent.NextStep)
        viewModel.sendIntent(PensionSurvivorIntent.DeceasedNationalIdChanged("1234567890"))
        viewModel.sendIntent(PensionSurvivorIntent.SearchDeceased)
        testDispatcher.scheduler.advanceUntilIdle()
        uploadAllDeceasedDocuments()
        viewModel.sendIntent(PensionSurvivorIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun uploadAllDeceasedDocuments() {
        DeceasedDocumentChecklist.forEach { type ->
            viewModel.sendIntent(
                PensionSurvivorIntent.DeceasedDocumentImagePicked(
                    type = type,
                    fileName = "${type.name}.jpg",
                    bytes = byteArrayOf(type.ordinal.toByte()),
                ),
            )
        }
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun buildViewModel(): PensionSurvivorViewModel {
        return PensionSurvivorViewModel(
            getPersonalInfoUseCase = GetPersonalInfoUseCase(repository),
            getDeceasedInfoUseCase = GetDeceasedInfoUseCase(repository),
            getAgeUseCase = GetAgeUseCase(repository),
            getSurvivorListUseCase = GetSurvivorListUseCase(repository),
            getConfirmSurvivorsListUseCase = GetConfirmSurvivorsListUseCase(repository),
            getFinalSurvivorPensionPDFUseCase = GetFinalSurvivorPensionPDFUseCase(repository),
            submitFinalSurvivorPensionUseCase = SubmitFinalSurvivorPensionUseCase(repository),
            uploadImageUseCase = uploadImageUseCase,
        )
    }

    private companion object {
        fun sampleSurvivor() = SurvivorDependentPR(
            firstName = "Sara",
            lastName = "Ahmadi",
            nationalId = "0098765432",
            fatherName = "Hossein",
            idCardNumber = "12345",
            cityOfIssue = "Tehran",
            genderCode = "2",
            genderDesc = "زن",
            dateOfBirth = "631152000000",
            insuranceId = "998877",
            tendencyCode = "1",
        )

        fun sampleSurvivorDn() = SurvivorDependentDN(
            firstName = "Sara",
            lastName = "Ahmadi",
            nationalId = "0098765432",
            fatherName = "Hossein",
            idCardNumber = "12345",
            cityOfIssue = "Tehran",
            genderCode = "2",
            genderDesc = "زن",
            dateOfBirth = 631152000000,
            insuranceId = "998877",
            tendencyCode = "1",
        )

        fun sampleDeceasedInfo() = DeceasedInfoDN(
            branchCode = null,
            branchName = null,
            deadDate = null,
            insuranceId = null,
            pensionerId = null,
            personal = DeceasedPersonalDN(
                cityOfIssueDesc = null,
                dateOfBirth = null,
                fatherName = null,
                firstName = "Hossein",
                lastName = "Ahmadi",
                nationalId = "1234567890",
                gender = null,
                idCardNumber = null,
            ),
            yearsAge = null,
            monthsAge = null,
            daysAge = null,
            related = null,
        )
    }
}
