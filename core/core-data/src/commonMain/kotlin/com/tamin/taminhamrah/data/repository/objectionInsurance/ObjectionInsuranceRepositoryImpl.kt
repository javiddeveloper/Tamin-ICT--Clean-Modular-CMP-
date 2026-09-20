package com.tamin.taminhamrah.data.repository.objectionInsurance

import com.tamin.taminhamrah.data.mapper.toDTO
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.objectionInsurance.ObjectionInsuranceRemoteDataSource
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.objectionInsurance.ObjectionInsuranceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val CONFLICT_HISTORIES_PAGE_LIMIT = 60

/**
 * Safety ceiling while paging `conflicthistories`. A career of year×workshop rows cannot
 * plausibly exceed this; above it we stop rather than loop forever on a bad `total`.
 */
private const val CONFLICT_HISTORIES_MAX_ROWS = 500

internal class ObjectionInsuranceRepositoryImpl(
    private val objectionInsuranceRemoteDataSource: ObjectionInsuranceRemoteDataSource,
) : ObjectionInsuranceRepository {

    override fun checkStatusConflict(): Flow<Boolean> = flow {
        emit(objectionInsuranceRemoteDataSource.checkStatusConflict())
    }

    /**
     * All conflict-history rows, page by page — same as legacy's `GeneralPagingSource` which
     * kept requesting until `total` was covered (limit 60 per page).
     */
    override fun getConflictHistories(): Flow<List<ObjectionInsuranceHistoryDN>> = flow {
        val all = mutableListOf<ObjectionInsuranceHistoryDN>()
        var page = 0
        while (all.size < CONFLICT_HISTORIES_MAX_ROWS) {
            val response = objectionInsuranceRemoteDataSource.getConflictHistories(
                ApiQueryParamDN(
                    page = page,
                    start = page * CONFLICT_HISTORIES_PAGE_LIMIT,
                    limit = CONFLICT_HISTORIES_PAGE_LIMIT,
                )
            )
            val pageItems = response.list.orEmpty()
            if (pageItems.isEmpty()) break
            all += pageItems.map { it.toDomain() }
            if (all.size >= response.total) break
            page++
        }
        emit(all)
    }

    override fun saveConflict(items: List<ObjectionInsuranceHistoryDN>): Flow<String?> = flow {
        emit(objectionInsuranceRemoteDataSource.saveConflict(items.map { it.toDTO() }))
    }

    override fun confirmConflict(description: String?): Flow<Boolean> = flow {
        emit(objectionInsuranceRemoteDataSource.confirmConflict(description))
    }

    override fun finalConfirmConflict(): Flow<String> = flow {
        emit(objectionInsuranceRemoteDataSource.finalConfirmConflict())
    }
}
