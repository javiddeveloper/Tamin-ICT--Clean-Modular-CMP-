package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSource
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.repository.WorkShopsRepository

class WorkShopsRepositoryImpl(
    private val remoteDataSource: WorkShopsRemoteDataSource
) : WorkShopsRepository {

    override suspend fun getAllEmployerAgreementByNationalId(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): ListData<EmployerAgreementDTO>? {
        return remoteDataSource.getAllEmployerAgreementByNationalId(
            page = page,
            start = start,
            limit = limit,
            filter = filter,
            sort = sort
        )
    }
}
