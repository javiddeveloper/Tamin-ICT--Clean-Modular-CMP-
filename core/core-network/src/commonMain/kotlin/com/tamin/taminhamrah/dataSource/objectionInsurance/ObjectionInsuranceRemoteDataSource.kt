package com.tamin.taminhamrah.dataSource.objectionInsurance

import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData

interface ObjectionInsuranceRemoteDataSource {
    suspend fun checkStatusConflict(): Boolean
    suspend fun getConflictHistories(query: ApiQueryParamDN): ListData<ObjectionInsuranceHistoryDTO>
    suspend fun saveConflict(items: List<ObjectionInsuranceHistoryDTO>): String?
    suspend fun confirmConflict(description: String?): Boolean
    suspend fun finalConfirmConflict(): String
}
