package com.tamin.taminhamrah.data.repository.workshops

import com.tamin.taminhamrah.data.repository.WorkShopsRepositoryImpl
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDTO
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDTO
import com.tamin.taminhamrah.model.workshop.SmsMessageDTO
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionDTO
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionQuery
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Only what the objection lookups and registration create-or-update need; everything else throws via the base. */
private class FakeWorkShopsRemoteDataSource : NotUsedWorkShopsRemoteDataSource() {
    var lastObjectionsQuery: ApiQueryParamDN? = null
        private set
    var createdRegistration: NewMemberRegistrationDTO? = null
        private set
    var updatedPersonalId: Long? = null
        private set
    var lastSmsSeqNo: Long? = null
        private set

    override suspend fun getWorkShopObjections(query: ApiQueryParamDN): ListData<WorkShopObjectionDTO> {
        lastObjectionsQuery = query
        return ListData(list = emptyList(), total = 0)
    }

    override suspend fun getWorkShopObjectionSms(
        objectionCode: Long,
        query: ApiQueryParamDN,
    ): ListData<SmsMessageDTO> {
        lastSmsSeqNo = objectionCode
        return ListData(list = emptyList(), total = 0)
    }

    override suspend fun createNewMemberRegistration(
        request: NewMemberRegistrationDTO,
    ): NewMemberRegistrationResultDTO {
        createdRegistration = request
        return NewMemberRegistrationResultDTO(id = CREATED_PERSONAL_ID)
    }

    override suspend fun updateNewMemberRegistration(
        personalId: Long,
        request: NewMemberRegistrationDTO,
    ): NewMemberRegistrationResultDTO {
        updatedPersonalId = personalId
        return NewMemberRegistrationResultDTO(id = personalId)
    }
}

class WorkShopsRepositoryImplTest {

    private val remote = FakeWorkShopsRemoteDataSource()
    private val repository = WorkShopsRepositoryImpl(remote, FakeEmployerServicesPageDao())

    /**
     * The legacy client wrote "شماره اعتراض" into a filter key (`branchCode`) the backend's own
     * filter builder never read there — a silent no-op. This is the deliberate fix: the objection
     * number must filter on [FilterProperty.SEQ_NO], not any branch-code-shaped property, or the
     * search field regresses back to doing nothing against the real backend.
     */
    @Test
    fun getWorkShopObjections_filtersObjectionNumberOnSeqNo() = runTest {
        repository.getWorkShopObjections(WorkShopObjectionQuery(objectionNumber = "1403008720"))

        val filters = remote.lastObjectionsQuery?.filters.orEmpty()
        assertEquals(
            listOf(ApiFilterDN(FilterProperty.SEQ_NO, "1403008720", FilterOperator.EQ)),
            filters,
        )
    }

    @Test
    fun getWorkShopObjections_mapsWorkshopIdAndDebitNumberToTheReusedFilterProperties() = runTest {
        repository.getWorkShopObjections(
            WorkShopObjectionQuery(workshopId = "2361847", debitNumber = "140244190")
        )

        val filters = remote.lastObjectionsQuery?.filters.orEmpty()
        assertEquals(
            setOf(
                ApiFilterDN(FilterProperty.PAYMENT_WORKSHOP_ID, "2361847", FilterOperator.EQ),
                ApiFilterDN(FilterProperty.DEBIT_NUMBER, "140244190", FilterOperator.EQ),
            ),
            filters.toSet(),
        )
    }

    @Test
    fun getWorkShopObjections_blankFiltersAreOmittedEntirely() = runTest {
        repository.getWorkShopObjections(WorkShopObjectionQuery())

        assertEquals(emptyList(), remote.lastObjectionsQuery?.filters)
    }

    @Test
    fun getWorkShopObjectionSms_passesTheSeqNoThrough() = runTest {
        repository.getWorkShopObjectionSms(seqNo = 1403008720L, page = 0)

        assertEquals(1403008720L, remote.lastSmsSeqNo)
    }

    @Test
    fun createNewMemberRegistration_createsAPersonNotYetOnFile() = runTest {
        val result = repository.createNewMemberRegistration(registration(personalId = null))

        assertNotNull(remote.createdRegistration)
        assertNull(remote.updatedPersonalId)
        assertEquals(CREATED_PERSONAL_ID, result.personalId)
    }

    /**
     * A person already on file is updated under their own id. Creating them again adds a second
     * `employers` record for the same person — the duplicate a re-opened draft used to leave.
     */
    @Test
    fun createNewMemberRegistration_updatesAPersonAlreadyOnFile() = runTest {
        val result = repository.createNewMemberRegistration(registration(personalId = 42L))

        assertEquals(42L, remote.updatedPersonalId)
        assertNull(remote.createdRegistration)
        assertEquals(42L, result.personalId)
    }

    private fun registration(personalId: Long?) = NewMemberRegistrationDN(
        firstName = "احمد",
        lastName = "احمدی",
        nationalId = "1234567891",
        dateOfBirth = "1370/01/01",
        cityOfBirthId = "0701",
        cityOfIssueId = "0701",
        jobCode = "7",
        startDate = "1405/01/01",
        workshopId = "9028218513",
        branchCode = "14",
        personalId = personalId,
    )
}

private const val CREATED_PERSONAL_ID = 7L
