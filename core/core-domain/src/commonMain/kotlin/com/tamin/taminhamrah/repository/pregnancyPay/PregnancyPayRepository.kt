package com.tamin.taminhamrah.repository.pregnancyPay

import com.tamin.taminhamrah.model.pregnancyPay.PregnancyMainInfoDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyOptionDN
import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayRequestDN
import kotlinx.coroutines.flow.Flow

interface PregnancyPayRepository {
    fun getMainInfo(): Flow<PregnancyMainInfoDN?>
    fun getPregnancyStatusList(): Flow<List<PregnancyOptionDN>>
    fun getPregnancyTypeList(): Flow<List<PregnancyOptionDN>>
    fun sendPregnancyPayRequest(request: SendPregnancyPayRequestDN): Flow<String?>
}
