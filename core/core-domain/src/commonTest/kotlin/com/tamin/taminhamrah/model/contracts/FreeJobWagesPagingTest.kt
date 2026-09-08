package com.tamin.taminhamrah.model.contracts

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FreeJobWagesPagingTest {

    @Test
    fun `nextPage is 1-indexed from received count`() {
        assertEquals(1, FreeJobWagesPaging.nextPage(0))
        assertEquals(2, FreeJobWagesPaging.nextPage(10))
        assertEquals(3, FreeJobWagesPaging.nextPage(20))
    }

    @Test
    fun `hasMore requires both remaining total and a full page`() {
        assertTrue(FreeJobWagesPaging.hasMore(receivedCount = 10, total = 25, pageItemCount = 10))
        assertFalse(FreeJobWagesPaging.hasMore(receivedCount = 25, total = 25, pageItemCount = 5))
        assertFalse(FreeJobWagesPaging.hasMore(receivedCount = 10, total = 25, pageItemCount = 5))
    }

    @Test
    fun `dedupeKey keeps distinct null job codes via id`() {
        val a = job(id = 1, jobCode = null)
        val b = job(id = 2, jobCode = null)
        assertEquals("id:1", FreeJobWagesPaging.dedupeKey(a))
        assertEquals("id:2", FreeJobWagesPaging.dedupeKey(b))
    }

    @Test
    fun `mergePage first page replaces and reports hasMore`() {
        val page = (1..10).map { job(id = it, jobCode = "c$it") }
        val merged = FreeJobWagesPaging.mergePage(
            existing = emptyList(),
            priorReceivedCount = 0,
            pageItems = page,
            append = false,
            total = 25,
        )
        assertEquals(10, merged.items.size)
        assertEquals(10, merged.receivedCount)
        assertTrue(merged.hasMore)
    }

    @Test
    fun `mergePage append keeps null-code jobs distinct by id`() {
        val first = listOf(job(id = 1, jobCode = null), job(id = 2, jobCode = "a"))
        val second = listOf(job(id = 3, jobCode = null), job(id = 2, jobCode = "a"))
        val merged = FreeJobWagesPaging.mergePage(
            existing = first,
            priorReceivedCount = 2,
            pageItems = second,
            append = true,
            total = 4,
        )
        assertEquals(3, merged.items.size)
        assertEquals(4, merged.receivedCount)
        assertFalse(merged.hasMore)
    }

    private fun job(id: Int, jobCode: String?) = FreeJobDN(
        discrioption = "job-$id",
        endDate = null,
        fixRank = null,
        id = id,
        iscoCode = null,
        jobCode = jobCode,
        startDate = null,
        status = null,
    )
}
