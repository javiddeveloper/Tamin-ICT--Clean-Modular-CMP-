package com.tamin.taminhamrah.repository.common

import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.flow.Flow

interface CommonRepository {
    fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>>
    fun getMainMenu(versionCode: String, forceUpdate: Boolean): Flow<List<MainServiceDN>>
}
