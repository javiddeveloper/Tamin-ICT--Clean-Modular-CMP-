package com.tamin.taminhamrah.feature.orotezprotez.fake

import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonDN
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
import com.tamin.taminhamrah.model.orotezProtez.SaveShortTermOrthosisRequestDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.orotezProtez.OrotezProtezRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeOrotezProtezRepository : OrotezProtezRepository {
    var mainInfoResult: RequestInsuredMainInfoDN? = null
    var insuredPersonsResult: List<InsuredPersonDN> = emptyList()

    var saveResult: String? = "درخواست شما با موفقیت ثبت شد."
    var shouldThrowOnSave = false
    var saveError: Throwable = RuntimeException("save failed")
    var lastSaveRequest: SaveShortTermOrthosisRequestDN? = null

    override fun getRequestInsuredMainInfo(): Flow<RequestInsuredMainInfoDN?> = flow {
        emit(mainInfoResult)
    }

    override fun getInsuredPersons(query: ApiQueryParamDN?): Flow<List<InsuredPersonDN>> = flow {
        emit(insuredPersonsResult)
    }

    override fun saveShortTermOrthosis(request: SaveShortTermOrthosisRequestDN): Flow<String?> = flow {
        lastSaveRequest = request
        if (shouldThrowOnSave) throw saveError
        emit(saveResult)
    }
}
