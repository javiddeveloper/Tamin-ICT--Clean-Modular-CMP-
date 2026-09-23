package com.tamin.taminhamrah.repository.objectionInsurance

import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDN
import kotlinx.coroutines.flow.Flow

interface ObjectionInsuranceRepository {
    fun checkStatusConflict(): Flow<Boolean>
    fun getConflictHistories(): Flow<List<ObjectionInsuranceHistoryDN>>
    fun saveConflict(items: List<ObjectionInsuranceHistoryDN>): Flow<String?>
    fun confirmConflict(description: String?): Flow<Boolean>
    fun finalConfirmConflict(): Flow<String>
}
