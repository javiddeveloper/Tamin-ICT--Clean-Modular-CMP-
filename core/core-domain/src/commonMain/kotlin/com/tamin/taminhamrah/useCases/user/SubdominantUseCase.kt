package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import kotlinx.coroutines.flow.Flow

interface SubdominantUseCase {
    suspend operator fun invoke(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<SubdominantDN>
}
