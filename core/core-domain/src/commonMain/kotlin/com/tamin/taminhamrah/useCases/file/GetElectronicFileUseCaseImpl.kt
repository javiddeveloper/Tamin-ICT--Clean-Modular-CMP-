package com.tamin.taminhamrah.useCases.file

import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetElectronicFileUseCase(
    private val userRepository: UserRepository
)  {
     suspend operator fun invoke(
        filters: List<ApiFilterDN> = emptyList(),
    ): Flow<List<ElectronicFileDN>> {
        return userRepository.getElectronicFile(filters)
    }
}
