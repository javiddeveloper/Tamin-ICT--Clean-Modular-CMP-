package com.tamin.taminhamrah.data.repository.workshops

import com.tamin.taminhamrah.data.repository.WorkShopsRepositoryImpl
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDTO
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderQuery
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * [WorkShopsRepositoryImpl] for ذینفعان.
 *
 * The stakeholder endpoint is the one workshop list whose identity sits under `workshopId.*`, and it
 * is tempting to put the person filters there too. They do not belong there: `workshopId` is the
 * workshop entity, and filtering on `workshopId.nationalId` answers 500 — so the wire names are
 * pinned here by their literal keys, not just by enum entry.
 */
class WorkShopsRepositoryStakeHoldersTest {

    private lateinit var remote: FakeRemote
    private lateinit var repository: WorkShopsRepositoryImpl

    @BeforeTest
    fun setup() {
        remote = FakeRemote()
        repository = WorkShopsRepositoryImpl(remote, FakeEmployerServicesPageDao())
    }

    @Test
    fun `a national code search filters on the stakeholder row's own nationalId`() = runTest {
        repository.getWorkshopStackHolders(
            WorkshopStackHolderQuery(workshopId = "6318210573", branchCode = "6310", nationalId = "0024567891"),
        )

        val filters = remote.lastQuery?.filters.orEmpty()
        assertEquals(
            listOf("workshopId.workshopId", "workshopId.branchCode", "nationalId"),
            filters.map { it.property.key },
        )
        assertEquals(listOf("6318210573", "6310", "0024567891"), filters.map { it.value })
    }

    /** No search, no person clause: a blank one would narrow the list to nobody. */
    @Test
    fun `without a search only the workshop's two keys are sent`() = runTest {
        repository.getWorkshopStackHolders(
            WorkshopStackHolderQuery(workshopId = "6318210573", branchCode = "6310"),
        )

        val filters = remote.lastQuery?.filters.orEmpty()
        assertEquals(
            listOf(FilterProperty.WORKSHOPID_ID, FilterProperty.WORKSHOPID_BRANCH_CODE),
            filters.map { it.property },
        )
    }

    private class FakeRemote : NotUsedWorkShopsRemoteDataSource() {
        var lastQuery: ApiQueryParamDN? = null

        override suspend fun getWorkshopStackHolders(query: ApiQueryParamDN): ListData<WorkshopStackHolderDTO> {
            lastQuery = query
            return ListData()
        }
    }
}
