package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import kotlinx.coroutines.flow.Flow

interface ChangeMobileUseCase {
    suspend operator fun invoke(filter: List<ApiFilterDN>): Flow<EditMobileResponseDN>
}
