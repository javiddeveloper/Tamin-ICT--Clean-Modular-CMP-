package com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.pensionSurvivor.fake.FakeContractsRepository
import com.tamin.taminhamrah.feature.pensionSurvivor.fake.FakePensionSurvivorPersonalRepository
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.SurvivorContactDraft
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.SurvivorDependencyType
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoEvent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoIntent
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.personal.SaveSurvivorInfoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SurvivorInfoViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FakePensionSurvivorPersonalRepository
    private lateinit var contractsRepository: FakeContractsRepository
    private lateinit var viewModel: SurvivorInfoViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakePensionSurvivorPersonalRepository()
        contractsRepository = FakeContractsRepository()
        viewModel = SurvivorInfoViewModel(
            saveSurvivorInfoUseCase = SaveSurvivorInfoUseCase(repository),
            uploadImageUseCase = UploadImageUseCase(contractsRepository),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_populatesEditableFieldsFromDraftAndClassifiesPartner() = runTest(testDispatcher) {
        viewModel.sendIntent(
            SurvivorInfoIntent.Init(
                survivor = samplePartnerSurvivor(),
                deceasedNationalId = "1234567890",
                branchCode = "1001",
                deceasedInsuranceId = "INS-1",
                address = "Tehran",
                phoneNumber = "02122223333",
                mobileNumber = "09121234567",
            ),
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Tehran", state.address)
        assertEquals("02122223333", state.phoneNumber)
        assertEquals("09121234567", state.mobileNumber)
        assertEquals(SurvivorDependencyType.Spouse, state.dependencyType)
        assertEquals(5, state.requiredDocuments.size)
        assertEquals("1001", state.branchCode)
        assertEquals("INS-1", state.deceasedInsuranceId)
    }

    @Test
    fun save_withoutDocuments_emitsValidationError() = runTest(testDispatcher) {
        viewModel.sendIntent(
            SurvivorInfoIntent.Init(
                survivor = samplePartnerSurvivor(),
                deceasedNationalId = "1234567890",
                address = "Tehran",
                phoneNumber = "02122223333",
                mobileNumber = "09121234567",
            ),
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(SurvivorInfoIntent.Save)
            testDispatcher.scheduler.advanceUntilIdle()
            assertIs<SurvivorInfoEvent.ShowError>(awaitItem())
        }
    }

    @Test
    fun save_withAllPartnerDocs_sendsRemappedDependencyAndDocList() = runTest(testDispatcher) {
        val draft = SurvivorContactDraft(
            address = "Tehran",
            phoneNumber = "02122223333",
            mobileNumber = "09121234567",
        )
        viewModel.sendIntent(
            SurvivorInfoIntent.Init(
                survivor = samplePartnerSurvivor(),
                deceasedNationalId = "1234567890",
                branchCode = "1001",
                deceasedInsuranceId = "DEC-INS",
                address = draft.address,
                phoneNumber = draft.phoneNumber,
                mobileNumber = draft.mobileNumber,
            ),
        )
        testDispatcher.scheduler.advanceUntilIdle()
        uploadAllRequiredDocuments()

        viewModel.events.test {
            viewModel.sendIntent(SurvivorInfoIntent.Save)
            testDispatcher.scheduler.advanceUntilIdle()

            val saved = assertIs<SurvivorInfoEvent.Saved>(awaitItem())
            assertEquals("0098765432", saved.nationalId)
            assertEquals(draft, saved.draft)
            assertIs<SurvivorInfoEvent.ShowSuccess>(awaitItem())
            assertIs<SurvivorInfoEvent.NavigateBack>(awaitItem())
        }

        val savedBody = requireNotNull(repository.lastSavedSurvivorInfoBody)
        assertEquals(draft.address, savedBody.address)
        assertEquals(draft.phoneNumber, savedBody.phoneNumber)
        assertEquals(draft.mobileNumber, savedBody.mobileNumber)
        assertEquals("02", savedBody.dependencyType?.code)
        assertEquals("1", savedBody.deathType)
        assertEquals("1001", savedBody.branchCode)
        assertEquals("DEC-INS", savedBody.insuranceNumber)
        val docs = assertNotNull(savedBody.pensionRequestDocList)
        assertEquals(5, docs.size)
        assertTrue(docs.all { !it.guid.isNullOrBlank() && !it.documentType.isNullOrBlank() })
    }

    private fun uploadAllRequiredDocuments() {
        viewModel.uiState.value.requiredDocuments.forEach { type ->
            viewModel.sendIntent(
                SurvivorInfoIntent.DocumentImagePicked(
                    type = type,
                    fileName = "${type.code}.jpg",
                    bytes = byteArrayOf(1, 2, 3),
                ),
            )
            testDispatcher.scheduler.advanceUntilIdle()
        }
    }

    private companion object {
        fun samplePartnerSurvivor() = SurvivorDependentPR(
            firstName = "Maryam",
            lastName = "Sadeghi",
            nationalId = "0098765432",
            fatherName = "Hossein",
            idCardNumber = "12345",
            cityOfIssue = "Tehran",
            genderCode = "02",
            genderDesc = "زن",
            dateOfBirth = "0",
            insuranceId = "998877",
            tendencyCode = "100",
        )
    }
}
