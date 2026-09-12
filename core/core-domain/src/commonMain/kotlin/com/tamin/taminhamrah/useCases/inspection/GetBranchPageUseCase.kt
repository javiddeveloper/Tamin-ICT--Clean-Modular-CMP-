package com.tamin.taminhamrah.useCases.inspection

import com.tamin.taminhamrah.model.inspection.BranchDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.inspection.InspectionRepository
import kotlinx.coroutines.flow.Flow

class GetBranchPageUseCase(
    private val repository: InspectionRepository
) {
    operator fun invoke(
        query: ApiQueryParamDN
    ): Flow<PageDN<BranchDN>> = repository.getBranchesPage(query)
}
