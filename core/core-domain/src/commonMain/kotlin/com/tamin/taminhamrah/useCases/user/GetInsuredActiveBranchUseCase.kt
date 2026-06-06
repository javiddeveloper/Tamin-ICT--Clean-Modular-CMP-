package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import kotlinx.coroutines.flow.Flow

interface GetInsuredActiveBranchUseCase {
    suspend operator fun invoke(): Flow<List<InsuredActiveBranchDN>>
}
