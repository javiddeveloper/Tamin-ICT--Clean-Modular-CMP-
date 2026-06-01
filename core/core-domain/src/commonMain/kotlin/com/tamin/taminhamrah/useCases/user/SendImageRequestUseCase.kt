package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.core.model.common.IdentityInfoDN
import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import kotlinx.coroutines.flow.Flow

interface SendImageRequestUseCase {
    suspend operator fun invoke(branchCode: String, filter: List<ApiFilterDN>): Flow<String>
}
