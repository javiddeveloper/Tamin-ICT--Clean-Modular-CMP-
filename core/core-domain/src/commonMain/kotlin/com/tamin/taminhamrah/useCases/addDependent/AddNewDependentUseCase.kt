package com.tamin.taminhamrah.useCases.addDependent

import com.tamin.taminhamrah.model.addDependent.GeneralResultDN
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.repository.addDependent.AddDependentRepository
import kotlinx.coroutines.flow.Flow

class AddNewDependentUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(
        request: RequestAddDependentDN
    ): Flow<GeneralResultDN> = repository.addNewDependent(request)
}
