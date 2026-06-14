package com.tamin.taminhamrah.useCases.file

import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetElectronicFileUseCase(
    private val userRepository: UserRepository
)  {
     suspend operator fun invoke(
        page: String ,
        start: String,
        limit: String,
        filter: String,
        sort: String ,
    ): Flow<List<ElectronicFileDN>> {
        return userRepository.getElectronicFile(page, start, limit, filter, sort)
    }
}
