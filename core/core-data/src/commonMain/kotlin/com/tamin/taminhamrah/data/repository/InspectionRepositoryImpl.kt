package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.data.local.dao.InspectionDao
import com.tamin.taminhamrah.data.mapper.inspection.toDN
import com.tamin.taminhamrah.data.mapper.inspection.toPageEntity
import com.tamin.taminhamrah.data.mapper.inspection.toDTO
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.repository.paging.pageCacheKey
import com.tamin.taminhamrah.dataSource.inspection.InspectionRemoteDataSource
import com.tamin.taminhamrah.model.inspection.BranchDN
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDN
import com.tamin.taminhamrah.model.inspection.JobDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestResultDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.inspection.InspectionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InspectionRepositoryImpl(
    private val remoteDataSource: InspectionRemoteDataSource,
    private val inspectionDao: InspectionDao,
) : InspectionRepository {

    override fun getInsurancePage(
        query: ApiQueryParamDN
    ): Flow<PageDN<InspectionPerformedDN>> {
        val listKey = query.pageCacheKey(SCOPE_INSURANCE)
        return offlineFirstPage(
            query = query,
            readCached = { inspectionDao.getInspectionsSlice(listKey, query.limit, query.start).map { it.toDN() } },
            fetch = {
                val data = remoteDataSource.getAllInsurance(query)
                PageDN(items = data.list.orEmpty().map { it.toDN() }, total = data.total)
            },
            write = { items, replace ->
                val rows = items.mapIndexed { i, item -> item.toPageEntity(listKey, query.start + i) }
                if (replace) inspectionDao.replaceInspections(listKey, rows) else inspectionDao.upsertInspections(rows)
            },
        )
    }

    override fun getWorkshopInspectionsPage(
        query: ApiQueryParamDN
    ): Flow<PageDN<InspectionPerformedDN>> {
        val listKey = query.pageCacheKey(SCOPE_MANAGER)
        return offlineFirstPage(
            query = query,
            readCached = { inspectionDao.getInspectionsSlice(listKey, query.limit, query.start).map { it.toDN() } },
            fetch = {
                val data = remoteDataSource.getAllManager(query)
                PageDN(items = data.list.orEmpty().map { it.toDN() }, total = data.total)
            },
            write = { items, replace ->
                val rows = items.mapIndexed { i, item -> item.toPageEntity(listKey, query.start + i) }
                if (replace) inspectionDao.replaceInspections(listKey, rows) else inspectionDao.upsertInspections(rows)
            },
        )
    }

    override fun getBranchesPage(
        query: ApiQueryParamDN
    ): Flow<PageDN<BranchDN>> {
        val listKey = query.pageCacheKey()
        return offlineFirstPage(
            query = query,
            readCached = { inspectionDao.getBranchesSlice(listKey, query.limit, query.start).map { it.toDN() } },
            fetch = {
                val data = remoteDataSource.getBranches(query)
                PageDN(items = data.list.orEmpty().map { it.toDN() }, total = data.total)
            },
            write = { items, replace ->
                val rows = items.mapIndexed { i, item -> item.toPageEntity(listKey, query.start + i) }
                if (replace) inspectionDao.replaceBranches(listKey, rows) else inspectionDao.upsertBranches(rows)
            },
        )
    }

    override fun getJobsPage(
        query: ApiQueryParamDN
    ): Flow<PageDN<JobDN>> {
        val listKey = query.pageCacheKey()
        return offlineFirstPage(
            query = query,
            readCached = { inspectionDao.getJobsSlice(listKey, query.limit, query.start).map { it.toDN() } },
            fetch = {
                val data = remoteDataSource.getJobs(query)
                PageDN(items = data.list.orEmpty().map { it.toDN() }, total = data.total)
            },
            write = { items, replace ->
                val rows = items.mapIndexed { i, item -> item.toPageEntity(listKey, query.start + i) }
                if (replace) inspectionDao.replaceJobs(listKey, rows) else inspectionDao.upsertJobs(rows)
            },
        )
    }

    /**
     * The offline-first page shape shared by the four lists: cached slice (if any), then the
     * network page, written back (first page replaces the list, later pages append). Offline with
     * a cached slice the flow just completes; with nothing cached the error is rethrown.
     */
    private fun <T> offlineFirstPage(
        query: ApiQueryParamDN,
        readCached: suspend () -> List<T>,
        fetch: suspend () -> PageDN<T>,
        write: suspend (items: List<T>, replace: Boolean) -> Unit,
    ): Flow<PageDN<T>> = flow {
        val cached = readCached()
        if (cached.isNotEmpty()) emit(PageDN(items = cached, isFromCache = true))

        val page = try {
            fetch()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (cached.isEmpty()) throw e
            return@flow
        }
        write(page.items, query.start == 0)
        emit(page)
    }

    private companion object {
        const val SCOPE_INSURANCE = "insurance"
        const val SCOPE_MANAGER = "manager"
    }

    override suspend fun submitInspectionRequest(
        request: SubmitInspectionRequestDN
    ): SubmitInspectionRequestResultDN {
        val result = remoteDataSource.submitInspectionRequest(request.toDTO())
        return SubmitInspectionRequestResultDN(
            id = result.request?.id
        )
    }

    override suspend fun getInspectionReportPDF(inspectionNo: String): PdfDownloadDN {
        return remoteDataSource.getInspectionReportPDF(inspectionNo).toDomain()
    }
}
