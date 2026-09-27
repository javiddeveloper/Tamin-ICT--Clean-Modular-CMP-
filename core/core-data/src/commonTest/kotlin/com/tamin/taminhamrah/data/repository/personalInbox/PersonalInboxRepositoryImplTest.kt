package com.tamin.taminhamrah.data.repository.personalInbox

import app.cash.turbine.test
import com.tamin.taminhamrah.data.local.dao.PersonalInboxDao
import com.tamin.taminhamrah.data.local.entity.PersonalInboxItemEntity
import com.tamin.taminhamrah.data.local.entity.PersonalInboxSizeEntity
import com.tamin.taminhamrah.data.repository.personalInbox.PersonalInboxRepositoryImpl
import com.tamin.taminhamrah.dataSource.inbox.PersonalInboxRemoteDataSource
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxListDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.personalInbox.PersonalInboxRepository
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PersonalInboxRepositoryImplTest {

    private lateinit var remoteDataSource: FakeRemoteDataSource
    private lateinit var dao: FakeDao
    private lateinit var apiQueryBuilder: FakeApiQueryBuilder
    private lateinit var repository: PersonalInboxRepository

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeRemoteDataSource()
        dao = FakeDao()
        apiQueryBuilder = FakeApiQueryBuilder()
        repository = PersonalInboxRepositoryImpl(remoteDataSource, dao, apiQueryBuilder)
    }

    @Test
    fun `getInboxItems should first emit local data then remote data`() = runTest {
        val localItem = createEntity(id = 1L)
        val remoteItem = createDTO(id = 2L)

        dao.itemsFlow.value = listOf(localItem)
        remoteDataSource.getInboxItemsResult = PersonalInboxListDTO(list = listOf(remoteItem), total = "1")

        repository.getInboxItems(null).test {
            // First emission: Local data
            val firstEmission = awaitItem()
            assertEquals(1, firstEmission.size)
            assertEquals(1L, firstEmission[0].id)

            // Second emission: Remote data (after DAO update)
            val secondEmission = awaitItem()
            assertEquals(1, secondEmission.size)
            assertEquals(2L, secondEmission[0].id)

            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(1, dao.replaceAllCalledCount)
        assertEquals(listOf(2L), dao.itemsFlow.value.map { it.id })
    }

    @Test
    fun `getInboxItems should not throw error if local data exists and remote fails`() = runTest {
        val localItem = createEntity(id = 1L)
        dao.itemsFlow.value = listOf(localItem)
        remoteDataSource.shouldThrowError = true

        repository.getInboxItems(null).test {
            val firstEmission = awaitItem()
            assertEquals(1, firstEmission.size)
            expectNoEvents()
        }
    }

    @Test
    fun `getInboxItems should throw error if local data is empty and remote fails`() = runTest {
        dao.itemsFlow.value = emptyList()
        remoteDataSource.shouldThrowError = true

        repository.getInboxItems(null).test {
            awaitItem() // First emission: empty list
            awaitError()
        }
    }

    @Test
    fun `getInboxSize should emit local then remote`() = runTest {
        val localSize = PersonalInboxSizeEntity(usage = "10", total = "100")
        val remoteSize = PersonalInboxSizeDTO(usage = "20", total = "100")

        dao.sizeFlow.value = localSize
        remoteDataSource.getInboxSizeResult = remoteSize

        repository.getInboxSize().test {
            // First emission
            val first = awaitItem()
            assertEquals("10", first.usage)

            // Second emission
            val second = awaitItem()
            assertEquals("20", second.usage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getInboxItemsPage should emit remote items with the backend total`() = runTest {
        remoteDataSource.getInboxItemsResult =
            PersonalInboxListDTO(list = listOf(createDTO(id = 2L)), total = "37")

        repository.getInboxItemsPage(ApiQueryParamDN(page = 1, start = 0, limit = 10)).test {
            val page = awaitItem()
            assertEquals(listOf(2L), page.items.map { it.id })
            assertEquals(37, page.total)
            awaitComplete()
        }
    }

    @Test
    fun `getInboxItemsPage should cache the first page`() = runTest {
        remoteDataSource.getInboxItemsResult =
            PersonalInboxListDTO(list = listOf(createDTO(id = 2L)), total = "37")

        repository.getInboxItemsPage(ApiQueryParamDN(page = 1, start = 0, limit = 10)).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(1, dao.replaceAllCalledCount)
        assertEquals(listOf(2L), dao.itemsFlow.value.map { it.id })
    }

    @Test
    fun `getInboxItemsPage should emit the cached page then the network page`() = runTest {
        dao.itemsFlow.value = listOf(createEntity(id = 1L))
        remoteDataSource.getInboxItemsResult =
            PersonalInboxListDTO(list = listOf(createDTO(id = 2L)), total = "37")

        repository.getInboxItemsPage(ApiQueryParamDN(page = 0, start = 0, limit = 10)).test {
            val cached = awaitItem()
            assertEquals(listOf(1L), cached.items.map { it.id })
            assertTrue(cached.isFromCache)

            val network = awaitItem()
            assertEquals(listOf(2L), network.items.map { it.id })
            assertEquals(37, network.total)
            assertFalse(network.isFromCache)
            awaitComplete()
        }
    }

    @Test
    fun `getInboxItemsPage first page from network should replace the whole cache`() = runTest {
        // Stale items from an earlier session, more than one page's worth.
        dao.itemsFlow.value = (1L..15L).map { createEntity(id = it, sentDate = it) }
        remoteDataSource.getInboxItemsResult =
            PersonalInboxListDTO(list = listOf(createDTO(id = 100L)), total = "1")

        repository.getInboxItemsPage(ApiQueryParamDN(page = 0, start = 0, limit = 10)).test {
            awaitItem() // cache
            awaitItem() // network
            awaitComplete()
        }

        assertEquals(1, dao.replaceAllCalledCount)
        assertEquals(listOf(100L), dao.itemsFlow.value.map { it.id })
    }

    @Test
    fun `getInboxItemsPage should append a later page to the cache`() = runTest {
        dao.itemsFlow.value = listOf(createEntity(id = 1L))
        remoteDataSource.getInboxItemsResult =
            PersonalInboxListDTO(list = listOf(createDTO(id = 2L)), total = "37")

        repository.getInboxItemsPage(ApiQueryParamDN(page = 1, start = 10, limit = 10)).test {
            val page = awaitItem() // nothing cached at offset 10, so only the network page
            assertEquals(listOf(2L), page.items.map { it.id })
            awaitComplete()
        }

        assertEquals(0, dao.replaceAllCalledCount)
        assertEquals(setOf(1L, 2L), dao.itemsFlow.value.map { it.id }.toSet())
    }

    @Test
    fun `getInboxItemsPage should serve the cache when the first page fails`() = runTest {
        dao.itemsFlow.value = listOf(createEntity(id = 1L))
        remoteDataSource.shouldThrowError = true

        repository.getInboxItemsPage(ApiQueryParamDN(page = 1, start = 0, limit = 10)).test {
            val page = awaitItem()
            assertEquals(listOf(1L), page.items.map { it.id })
            assertNull(page.total)
            assertTrue(page.isFromCache)
            awaitComplete()
        }
    }

    @Test
    fun `getInboxItemsPage should serve a later page's slice from the cache offline`() = runTest {
        // 15 cached items, newest first: the page at start 10 is the 5 oldest.
        dao.itemsFlow.value = (1L..15L).map { createEntity(id = it, sentDate = it) }
        remoteDataSource.shouldThrowError = true

        repository.getInboxItemsPage(ApiQueryParamDN(page = 1, start = 10, limit = 10)).test {
            val page = awaitItem()
            assertEquals(listOf(5L, 4L, 3L, 2L, 1L), page.items.map { it.id })
            assertTrue(page.isFromCache)
            awaitComplete()
        }
    }

    @Test
    fun `getInboxItemsPage should throw when the first page fails and nothing is cached`() = runTest {
        dao.itemsFlow.value = emptyList()
        remoteDataSource.shouldThrowError = true

        repository.getInboxItemsPage(ApiQueryParamDN(page = 1, start = 0, limit = 10)).test {
            awaitError()
        }
    }

    @Test
    fun `getInboxItemsPage should throw when a later page fails and its slice is not cached`() = runTest {
        dao.itemsFlow.value = listOf(createEntity(id = 1L))
        remoteDataSource.shouldThrowError = true

        repository.getInboxItemsPage(ApiQueryParamDN(page = 2, start = 10, limit = 10)).test {
            awaitError()
        }
    }

    @Test
    fun `getInboxItemsPage should forward the page query untouched`() = runTest {
        val query = ApiQueryParamDN(page = 3, start = 20, limit = 10)

        repository.getInboxItemsPage(query).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(query, remoteDataSource.lastQuery)
    }

    @Test
    fun `getInboxItemsPage should survive a first() collector aborting after one emission`() = runTest {
        remoteDataSource.getInboxItemsResult =
            PersonalInboxListDTO(list = listOf(createDTO(id = 2L)), total = "73")

        val page = repository.getInboxItemsPage(ApiQueryParamDN(page = 0, start = 0, limit = 10)).first()

        assertEquals(listOf(2L), page.items.map { it.id })
        assertEquals(73, page.total)
    }

    @Test
    fun `getInboxItemsPage should still fail through first() when remote fails and cache is empty`() = runTest {
        dao.itemsFlow.value = emptyList()
        remoteDataSource.shouldThrowError = true

        assertFailsWith<RuntimeException> {
            repository.getInboxItemsPage(ApiQueryParamDN(page = 0, start = 0, limit = 10)).first()
        }
    }

    private fun createEntity(id: Long, sentDate: Long = 1000L) = PersonalInboxItemEntity(
        id = id,
        nationalCode = "123",
        mobileNumber = "0912",
        email = null,
        read = null,
        data = null,
        sentDate = sentDate,
        receiveDate = null,
        seenDate = null,
        seen = null,
        hasImage = null,
        hasText = null,
        hasPdf = null,
        updateable = null,
        status = null,
        referenceId = null,
        pdf = null,
        typeDesc = null,
        typeCode = null,
        subTypeDesc = null,
        subTypeCode = null,
        permissionPassword = null,
        permissionDateFrom = null,
        permissionDateTo = null
    )

    private fun createDTO(id: Long) = PersonalInboxItemDTO(
        id = id,
        nationalCode = "456",
        mobileNumber = "0913",
        email = null,
        read = null,
        data = null,
        sentDate = 2000L,
        receiveDate = null,
        seenDate = null,
        seen = null,
        hasImage = null,
        hasText = null,
        hasPdf = null,
        updateable = null,
        status = null,
        referenceId = null,
        pdf = null,
        type = null,
        subType = null,
        permission = null
    )

    // Fakes
    private class FakeRemoteDataSource : PersonalInboxRemoteDataSource {
        var getInboxItemsResult = PersonalInboxListDTO(list = emptyList(), total = "0")
        var getInboxSizeResult = PersonalInboxSizeDTO(usage = "0", total = "0")
        var shouldThrowError = false
        var lastQuery: ApiQueryParamDN? = null

        override suspend fun getInboxItems(query: ApiQueryParamDN): PersonalInboxListDTO {
            lastQuery = query
            if (shouldThrowError) throw RuntimeException("Remote failure")
            return getInboxItemsResult
        }

        override suspend fun getInboxSize(): PersonalInboxSizeDTO {
            if (shouldThrowError) throw RuntimeException("Remote failure")
            return getInboxSizeResult
        }

        override suspend fun getMyRequestPDF(requestId: String): PersonalInboxItemDTO = PersonalInboxItemDTO(
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null
        )
        override suspend fun deleteMyRequest(requestId: String) {}
        override suspend fun inboxInquiryLicense(requestId: String, operation: String, duration: String?) {}
    }

    private class FakeDao : PersonalInboxDao {
        val itemsFlow = MutableStateFlow<List<PersonalInboxItemEntity>>(emptyList())
        val sizeFlow = MutableStateFlow<PersonalInboxSizeEntity?>(null)
        var replaceAllCalledCount = 0

        // Like the Room query: newest first.
        override fun getInboxItems(): Flow<List<PersonalInboxItemEntity>> =
            itemsFlow.map { items -> items.sortedByDescending { it.sentDate } }

        // Like @Upsert: replaces rows with the same id, keeps the rest.
        override suspend fun upsertInboxItems(items: List<PersonalInboxItemEntity>) {
            val incomingIds = items.map { it.id }.toSet()
            itemsFlow.value = itemsFlow.value.filterNot { it.id in incomingIds } + items
        }

        override suspend fun clearInboxItems() {
            itemsFlow.value = emptyList()
        }

        override suspend fun replaceAllInboxItems(items: List<PersonalInboxItemEntity>) {
            replaceAllCalledCount++
            itemsFlow.value = items
        }

        override fun getInboxSize(id: Int): Flow<PersonalInboxSizeEntity?> = sizeFlow

        override suspend fun upsertInboxSize(size: PersonalInboxSizeEntity) {
            sizeFlow.value = size
        }
    }

    private class FakeApiQueryBuilder : ApiQueryBuilder {
        override fun defaultQuery(): ApiQueryParamDN = ApiQueryParamDN()
        override fun buildQuery(query: ApiQueryParamDN): Map<String, String> = emptyMap()
        override fun buildFilterJson(filters: List<ApiFilterDN>): String = ""
    }
}
