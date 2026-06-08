package com.tamin.taminhamrah.useCases.identity

import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import kotlinx.coroutines.flow.Flow

interface IdentityInfoUseCase {
    operator fun invoke(): Flow<IdentityInfoDN>
}
