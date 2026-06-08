package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.relation.TaminRelationDN
import kotlinx.coroutines.flow.Flow

interface TaminRelationUseCase {
    suspend operator fun invoke(): Flow<TaminRelationDN>
}
