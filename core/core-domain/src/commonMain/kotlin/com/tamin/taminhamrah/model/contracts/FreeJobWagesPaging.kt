package com.tamin.taminhamrah.model.contracts

/**
 * Shared paging rules for `GET baseinfo/free-job-wage` (legacy page size 10).
 */
object FreeJobWagesPaging {
    const val PAGE_SIZE = 10

    fun nextPage(receivedCount: Int): Int = (receivedCount / PAGE_SIZE) + 1

    fun hasMore(receivedCount: Int, total: Int, pageItemCount: Int): Boolean =
        receivedCount < total && pageItemCount == PAGE_SIZE

    fun dedupeKey(job: FreeJobDN): String =
        job.jobCode?.takeIf { it.isNotBlank() } ?: "id:${job.id}"

    fun mergePage(
        existing: List<FreeJobDN>,
        priorReceivedCount: Int,
        pageItems: List<FreeJobDN>,
        append: Boolean,
        total: Int,
    ): MergedFreeJobs {
        val merged = if (append) existing + pageItems else pageItems
        val received = if (append) priorReceivedCount + pageItems.size else pageItems.size
        return MergedFreeJobs(
            items = merged.distinctBy(::dedupeKey),
            receivedCount = received,
            hasMore = hasMore(received, total, pageItems.size),
        )
    }

    data class MergedFreeJobs(
        val items: List<FreeJobDN>,
        val receivedCount: Int,
        val hasMore: Boolean,
    )
}
