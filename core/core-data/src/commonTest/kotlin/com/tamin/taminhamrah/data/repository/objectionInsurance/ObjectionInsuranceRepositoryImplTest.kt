package com.tamin.taminhamrah.data.repository.objectionInsurance

import com.tamin.taminhamrah.dataSource.objectionInsurance.ObjectionInsuranceRemoteDataSource
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDN
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeObjectionInsuranceRemoteDataSource : ObjectionInsuranceRemoteDataSource {
    var checkStatusConflictResult: Boolean = false
    var getConflictHistoriesResult: ListData<ObjectionInsuranceHistoryDTO> = ListData()
    /** When set, each call to [getConflictHistories] returns the next entry (for paging tests). */
    var getConflictHistoriesPages: List<ListData<ObjectionInsuranceHistoryDTO>>? = null
    var saveConflictResult: String? = null
    var confirmConflictResult: Boolean = true
    var finalConfirmConflictResult: String = "TRK"
    var lastQuery: ApiQueryParamDN? = null
    val queries: MutableList<ApiQueryParamDN> = mutableListOf()
    var lastSaveItems: List<ObjectionInsuranceHistoryDTO>? = null
    var lastConfirmDescription: String? = null
    private var pageCallIndex = 0

    override suspend fun checkStatusConflict(): Boolean = checkStatusConflictResult

    override suspend fun getConflictHistories(query: ApiQueryParamDN): ListData<ObjectionInsuranceHistoryDTO> {
        lastQuery = query
        queries += query
        val pages = getConflictHistoriesPages
        return if (pages != null) {
            pages.getOrElse(pageCallIndex++) { ListData(total = 0, list = emptyList()) }
        } else {
            getConflictHistoriesResult
        }
    }

    override suspend fun saveConflict(items: List<ObjectionInsuranceHistoryDTO>): String? {
        lastSaveItems = items
        return saveConflictResult
    }

    override suspend fun confirmConflict(description: String?): Boolean {
        lastConfirmDescription = description
        return confirmConflictResult
    }

    override suspend fun finalConfirmConflict(): String = finalConfirmConflictResult
}

class ObjectionInsuranceRepositoryImplTest {

    @Test
    fun checkStatusConflict_emitsRemoteValue() = runTest {
        val fake = FakeObjectionInsuranceRemoteDataSource().apply {
            checkStatusConflictResult = true
        }
        val repository = ObjectionInsuranceRepositoryImpl(fake)

        assertTrue(repository.checkStatusConflict().first())
    }

    @Test
    fun getConflictHistories_usesLimit60_andMapsToDomain() = runTest {
        val fake = FakeObjectionInsuranceRemoteDataSource().apply {
            getConflictHistoriesResult = ListData(
                total = 1,
                list = listOf(
                    ObjectionInsuranceHistoryDTO(
                        year = "1402",
                        branchCode = "0950",
                        workshopName = "کارگاه",
                    )
                ),
            )
        }
        val repository = ObjectionInsuranceRepositoryImpl(fake)

        val result = repository.getConflictHistories().first()

        assertEquals(60, fake.lastQuery?.limit)
        assertEquals(0, fake.lastQuery?.page)
        assertEquals(0, fake.lastQuery?.start)
        assertEquals(1, result.size)
        assertEquals("1402", result.first().year)
        assertEquals("0950", result.first().branchCode)
        assertEquals("کارگاه", result.first().workshopName)
    }

    @Test
    fun getConflictHistories_pagesUntilTotalReached() = runTest {
        val page0 = List(60) { i -> ObjectionInsuranceHistoryDTO(year = "14${i.toString().padStart(2, '0')}") }
        val page1 = List(15) { i -> ObjectionInsuranceHistoryDTO(year = "15${i.toString().padStart(2, '0')}") }
        val fake = FakeObjectionInsuranceRemoteDataSource().apply {
            getConflictHistoriesPages = listOf(
                ListData(total = 75, list = page0),
                ListData(total = 75, list = page1),
            )
        }
        val repository = ObjectionInsuranceRepositoryImpl(fake)

        val result = repository.getConflictHistories().first()

        assertEquals(75, result.size)
        assertEquals(2, fake.queries.size)
        assertEquals(0, fake.queries[0].start)
        assertEquals(60, fake.queries[1].start)
        assertEquals(1, fake.queries[1].page)
    }

    @Test
    fun getConflictHistories_nullList_emitsEmpty() = runTest {
        val fake = FakeObjectionInsuranceRemoteDataSource().apply {
            getConflictHistoriesResult = ListData(total = 0, list = null)
        }
        val repository = ObjectionInsuranceRepositoryImpl(fake)

        assertEquals(emptyList(), repository.getConflictHistories().first())
    }

    @Test
    fun saveConflict_mapsDnToDto() = runTest {
        val fake = FakeObjectionInsuranceRemoteDataSource().apply {
            saveConflictResult = null
        }
        val repository = ObjectionInsuranceRepositoryImpl(fake)

        val result = repository.saveConflict(
            listOf(ObjectionInsuranceHistoryDN(year = "1402", newMonth1 = "10"))
        ).first()

        assertNull(result)
        assertEquals("1402", fake.lastSaveItems?.first()?.year)
        assertEquals("10", fake.lastSaveItems?.first()?.newMonth1)
    }

    @Test
    fun confirmConflict_forwardsDescription() = runTest {
        val fake = FakeObjectionInsuranceRemoteDataSource()
        val repository = ObjectionInsuranceRepositoryImpl(fake)

        assertTrue(repository.confirmConflict("desc").first())
        assertEquals("desc", fake.lastConfirmDescription)
    }

    @Test
    fun finalConfirmConflict_emitsTrackingNumber() = runTest {
        val fake = FakeObjectionInsuranceRemoteDataSource().apply {
            finalConfirmConflictResult = "TRK-42"
        }
        val repository = ObjectionInsuranceRepositoryImpl(fake)

        assertEquals("TRK-42", repository.finalConfirmConflict().first())
    }
}
