package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.UserTypeInfoDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow

class CheckUserTypeUseCase(private val repository: CommonRepository) {
    operator fun invoke(): Flow<UserTypeInfoDN> = repository.checkUserType()
}
