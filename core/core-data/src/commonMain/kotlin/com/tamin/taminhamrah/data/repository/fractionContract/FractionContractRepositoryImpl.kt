package com.tamin.taminhamrah.data.repository.fractionContract

import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.fractionContract.FractionContractRemoteDataSource
import com.tamin.taminhamrah.model.fractionContract.FractionContractResultDN
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityDN
import com.tamin.taminhamrah.model.fractionContract.MakeFractionContractRequestDTO
import com.tamin.taminhamrah.repository.fractionContract.FractionContractRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FractionContractRepositoryImpl(
    private val fractionContractRemoteDataSource: FractionContractRemoteDataSource,
) : FractionContractRepository {

    override fun checkAgeAndHistory(): Flow<FractionEligibilityDN?> = flow {
        val response = fractionContractRemoteDataSource.checkAgeAndHistory()
        emit(response?.toDomain())
    }

    override fun makeFractionContract(premium: String): Flow<FractionContractResultDN> = flow {
        val result = fractionContractRemoteDataSource.makeFractionContract(
            MakeFractionContractRequestDTO(premium = premium),
        )
        emit(result.toDomain())
    }
}
