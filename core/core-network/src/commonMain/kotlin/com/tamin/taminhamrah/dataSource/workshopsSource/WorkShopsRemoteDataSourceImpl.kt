package com.tamin.taminhamrah.dataSource.workshopsSource

import com.tamin.taminhamrah.apiService.WorkShopsApiService
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.tools.extractData

internal class WorkShopsRemoteDataSourceImpl(
    private val apiService: WorkShopsApiService
) : WorkShopsRemoteDataSource {
    override suspend fun getAllEmployerAgreementByNationalId(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): ListData<EmployerAgreementDTO> {
        val queries = mapOf(
            "page" to page,
            "start" to start,
            "limit" to limit,
            "filter" to filter,
            "sort" to sort
        )
        return apiService.getAllEmployerAgreementByNationalId(queries).extractData()
    }
}
