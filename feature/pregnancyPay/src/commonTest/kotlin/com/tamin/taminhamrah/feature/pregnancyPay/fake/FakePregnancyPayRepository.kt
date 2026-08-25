package com.tamin.taminhamrah.feature.pregnancyPay.fake

import com.tamin.taminhamrah.model.pregnancyPay.PregnancyMainInfoDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyOptionDN
import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayRequestDN
import com.tamin.taminhamrah.repository.pregnancyPay.PregnancyPayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePregnancyPayRepository : PregnancyPayRepository {
    var mainInfoResult: PregnancyMainInfoDN? = null
    var pregnancyStatusListResult: List<PregnancyOptionDN> = emptyList()
    var pregnancyTypeListResult: List<PregnancyOptionDN> = emptyList()

    var sendResult: String? = "درخواست شما با موفقیت ثبت شد."
    var shouldThrowOnSend = false
    var sendError: Throwable = RuntimeException("send failed")
    var lastSendRequest: SendPregnancyPayRequestDN? = null

    override fun getMainInfo(): Flow<PregnancyMainInfoDN?> = flow {
        emit(mainInfoResult)
    }

    override fun getPregnancyStatusList(): Flow<List<PregnancyOptionDN>> = flow {
        emit(pregnancyStatusListResult)
    }

    override fun getPregnancyTypeList(): Flow<List<PregnancyOptionDN>> = flow {
        emit(pregnancyTypeListResult)
    }

    override fun sendPregnancyPayRequest(request: SendPregnancyPayRequestDN): Flow<String?> = flow {
        lastSendRequest = request
        if (shouldThrowOnSend) throw sendError
        emit(sendResult)
    }
}
