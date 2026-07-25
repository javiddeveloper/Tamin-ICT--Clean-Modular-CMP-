package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.RoleDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow

class GetRolesUseCase(private val commonRepository: CommonRepository) {
    operator fun invoke(): Flow<List<RoleDN>> {
        return commonRepository.getRoles()
    }
}
