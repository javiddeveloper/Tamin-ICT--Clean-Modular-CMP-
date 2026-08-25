package com.tamin.taminhamrah.useCases.occurrence

import com.tamin.taminhamrah.model.occurrence.InsuredRelationDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceResultDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceSubmitRequestDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceUploadedDocDN
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDN
import com.tamin.taminhamrah.repository.occurrence.FakeOccurrenceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OccurrenceUseCasesTest : BaseUseCaseTest() {

    private lateinit var repository: FakeOccurrenceRepository
    private lateinit var getPersonalInfoUseCase: GetOccurrencePersonalInfoUseCase
    private lateinit var getAllWorkshopsUseCase: GetAllWorkshopsUseCase
    private lateinit var getWorkshopSpecUseCase: GetWorkshopSpecUseCase
    private lateinit var getInsuredRelationUseCase: GetInsuredRelationUseCase
    private lateinit var getDocTypesUseCase: GetOccurrenceDocTypesUseCase
    private lateinit var submitOccurrenceUseCase: SubmitOccurrenceUseCase

    @BeforeTest
    fun setup() {
        repository = FakeOccurrenceRepository()
        getPersonalInfoUseCase = GetOccurrencePersonalInfoUseCase(repository)
        getAllWorkshopsUseCase = GetAllWorkshopsUseCase(repository)
        getWorkshopSpecUseCase = GetWorkshopSpecUseCase(repository)
        getInsuredRelationUseCase = GetInsuredRelationUseCase(repository)
        getDocTypesUseCase = GetOccurrenceDocTypesUseCase(repository)
        submitOccurrenceUseCase = SubmitOccurrenceUseCase(repository)
    }

    @Test
    fun `GetOccurrencePersonalInfoUseCase should return personal info and forward params`() = runTest {
        val result = getPersonalInfoUseCase(
            nationalCode = "0012345678",
            birthDate = "662688000",
            workshopCode = "1412345",
            branchCode = "014",
        )

        assertEquals(repository.personalInfoResult, result)
        assertEquals(
            listOf("0012345678", "662688000", "1412345", "014"),
            repository.lastPersonalInfoParams,
        )
    }

    @Test
    fun `GetAllWorkshopsUseCase should return workshops for the given national code`() = runTest {
        val expected = listOf(
            WorkshopItemDN(
                id = "1", workshopCode = "1412345", branchCode = "014", name = "کارگاه الف",
                employerName = "", employerPhone = "", address = "", postalCode = "", phone = "",
                nationality = "", nationalityCode = "",
            )
        )
        repository.allWorkshopsResult = expected

        val result = getAllWorkshopsUseCase("0012345678")

        assertEquals(expected, result)
        assertEquals("0012345678", repository.lastAllWorkshopsNationalCode)
    }

    @Test
    fun `GetWorkshopSpecUseCase should return workshop spec and forward params`() = runTest {
        val result = getWorkshopSpecUseCase("1412345", "014")

        assertEquals(repository.workshopSpecResult, result)
        assertEquals("1412345" to "014", repository.lastWorkshopSpecParams)
    }

    @Test
    fun `GetInsuredRelationUseCase should return insured relation for national code`() = runTest {
        val expected = InsuredRelationDN(
            insuranceTypeCode = "02", insuranceType = "فرعی", branchCode = "20", branchName = "شعبه دو",
        )
        repository.insuredRelationResult = expected

        val result = getInsuredRelationUseCase("0012345678")

        assertEquals(expected, result)
        assertEquals("0012345678", repository.lastInsuredRelationNationalCode)
    }

    @Test
    fun `GetOccurrenceDocTypesUseCase should return document types from repository`() = runTest {
        val expected = listOf(OccurrenceDocTypeDN(id = 1, title = "گزارش حادثه"))
        repository.documentTypesResult = expected

        val result = getDocTypesUseCase()

        assertEquals(expected, result)
    }

    @Test
    fun `SubmitOccurrenceUseCase should submit request and return result`() = runTest {
        val request = sampleSubmitRequest()
        repository.submitResult = OccurrenceResultDN(trackingCode = "TRACK-99")

        val result = submitOccurrenceUseCase(request)

        assertEquals("TRACK-99", result.trackingCode)
        assertEquals(request, repository.lastSubmitRequest)
    }

    @Test
    fun `GetOccurrenceDocTypesUseCase should propagate repository error`() = runTest {
        val expectedError = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedError

        val actualError = assertFailsWith<RuntimeException> { getDocTypesUseCase() }

        assertEquals(expectedError.message, actualError.message)
    }

    @Test
    fun `SubmitOccurrenceUseCase should propagate repository error`() = runTest {
        val expectedError = RuntimeException("Submit failed")
        repository.shouldThrowError = true
        repository.error = expectedError

        val actualError = assertFailsWith<RuntimeException> { submitOccurrenceUseCase(sampleSubmitRequest()) }

        assertEquals(expectedError.message, actualError.message)
    }

    private fun sampleSubmitRequest() = OccurrenceSubmitRequestDN(
        nationalCode = "0012345678",
        firstName = "علی",
        lastName = "رضایی",
        gender = 1,
        nationalityCode = 1,
        insuranceType = "اصلی",
        insuranceTypeCode = "01",
        insuranceNumber = "1234567",
        branchCode = "10",
        branchName = "شعبه مرکزی",
        birthDate = 662688000L,
        workshopId = "1412345",
        workshopBranchCode = "014",
        workshopName = "کارگاه الف",
        employerName = "شرکت الف",
        employerPhone = "02112345678",
        workshopAddress = "تهران",
        workshopPostalCode = "1234567890",
        workshopPhone = "02112345678",
        employmentDate = 662688000L,
        maritalStatus = 1,
        jobTitle = "کارگر",
        workLocation = "خط تولید",
        transportation = "شخصی",
        workStartTime = "08:00",
        workEndTime = "16:00",
        homeAddress = "تهران",
        homePhone = "02112345679",
        homePostalCode = "1234567891",
        accidentDate = 662688000L,
        accidentTime = "10:30",
        accidentOutcomeId = 1,
        exactLocation = "طبقه دوم",
        description = "توضیحات حادثه",
        reporterType = "1",
        documents = listOf(
            OccurrenceUploadedDocDN(typeId = 1, typeName = "گزارش", fileName = "a.jpg", guid = "guid-1")
        ),
    )
}
