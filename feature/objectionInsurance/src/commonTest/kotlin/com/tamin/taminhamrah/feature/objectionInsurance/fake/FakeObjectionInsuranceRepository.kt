package com.tamin.taminhamrah.feature.objectionInsurance.fake

import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDN
import com.tamin.taminhamrah.repository.objectionInsurance.ObjectionInsuranceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeObjectionInsuranceRepository : ObjectionInsuranceRepository {
    var hasActiveRequest = false
    var histories: List<ObjectionInsuranceHistoryDN> = emptyList()
    var saveResult: String? = "ok"
    var confirmResult = true
    var finalConfirmResult = "123456"
    var lastSavedItems: List<ObjectionInsuranceHistoryDN>? = null
    var lastConfirmDescription: String? = null
    var shouldThrowOnLoad = false

    override fun checkStatusConflict(): Flow<Boolean> = flow { emit(hasActiveRequest) }

    override fun getConflictHistories(): Flow<List<ObjectionInsuranceHistoryDN>> = flow {
        if (shouldThrowOnLoad) throw RuntimeException("load error")
        emit(histories)
    }

    override fun saveConflict(items: List<ObjectionInsuranceHistoryDN>): Flow<String?> = flow {
        lastSavedItems = items
        emit(saveResult)
    }

    override fun confirmConflict(description: String?): Flow<Boolean> = flow {
        lastConfirmDescription = description
        emit(confirmResult)
    }

    override fun finalConfirmConflict(): Flow<String> = flow { emit(finalConfirmResult) }
}
