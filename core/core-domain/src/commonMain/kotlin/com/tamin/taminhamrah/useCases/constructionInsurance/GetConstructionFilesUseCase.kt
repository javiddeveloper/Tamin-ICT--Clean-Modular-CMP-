package com.tamin.taminhamrah.useCases.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class GetConstructionFilesUseCase(
    private val repository: ConstructionInsuranceRepository
) {
    operator fun invoke(
        search: ConstructionFileSearchParamsDN? = null
    ): Flow<List<ConstructionFileDN>> {
        return repository.getConstructionFiles(search)
    }
}
