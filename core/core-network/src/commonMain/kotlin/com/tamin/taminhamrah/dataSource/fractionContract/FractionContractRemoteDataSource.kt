package com.tamin.taminhamrah.dataSource.fractionContract

import com.tamin.taminhamrah.model.fractionContract.FractionContractResultDTO
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityDTO
import com.tamin.taminhamrah.model.fractionContract.MakeFractionContractRequestDTO

interface FractionContractRemoteDataSource {
    suspend fun checkAgeAndHistory(): FractionEligibilityDTO?
    suspend fun makeFractionContract(
        request: MakeFractionContractRequestDTO = MakeFractionContractRequestDTO(),
    ): FractionContractResultDTO
}
