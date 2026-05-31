package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.core.model.common.IdentityInfoDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import kotlinx.coroutines.flow.Flow

interface TaminRelationUseCase {
    suspend operator fun invoke(): Flow<TaminRelationDN>
}
