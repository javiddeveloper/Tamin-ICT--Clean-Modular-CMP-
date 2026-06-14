package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetInsuredActiveBranchUseCase(private val userRepository: UserRepository,)   {
     suspend fun invoke(): Flow<List<InsuredActiveBranchDN>> {
        return userRepository.getInsuredActiveBranch()
    }
}
