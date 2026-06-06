package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import kotlinx.coroutines.flow.Flow

interface GetRelationTaminAllUseCase {
    suspend operator fun invoke(
        page: String = "1",
        start: String = "0",
        limit: String = "10",
        filter: String = "[]",
        sort: String = "[]"
    ): Flow<List<ActiveRelationDN>>
}
