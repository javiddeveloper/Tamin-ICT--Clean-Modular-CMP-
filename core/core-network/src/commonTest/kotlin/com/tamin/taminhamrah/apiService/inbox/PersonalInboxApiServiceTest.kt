package com.tamin.taminhamrah.apiService.inbox

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDTO
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.PersonalInboxTestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PersonalInboxApiServiceTest : BaseApiTest() {

    @Test
    fun `getInboxItems should return personal inbox list`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PersonalInboxTestData.inboxItemsSuccess,
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPersonalInboxApiService()

        val response = apiService.getInboxItems(emptyMap())

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)

        val listData = response.data
        assertNotNull(listData)
        assertEquals("1", listData.total)

        val items: List<PersonalInboxItemDTO> = listData.list.orEmpty()
        assertEquals(1, items.size)

        val firstItem = items.first()
        assertEquals(12345L, firstItem.id)
        assertEquals("0946168113", firstItem.nationalCode)
        assertEquals("09123456789", firstItem.mobileNumber)
        assertEquals(false, firstItem.seen)
        assertEquals("سیستم تامین", firstItem.type?.typeDesc)
        assertEquals("اعلامیه", firstItem.subType?.typeDesc)
        assertEquals(1234L, firstItem.permission?.password)
    }

    @Test
    fun `getInboxSize should return personal inbox size`() = runTest {
        val ktorfit = createMockKtorfit(PersonalInboxTestData.inboxSizeSuccess)
        val apiService = ktorfit.createPersonalInboxApiService()

        val response = apiService.getInboxSize()

        assertEquals("0.53", response.usage)
        assertEquals("10", response.total)
    }
}
