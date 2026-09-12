package com.tamin.taminhamrah.repository.fractionContract

import com.tamin.taminhamrah.model.fractionContract.FractionContractResultDN
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityDN
import kotlinx.coroutines.flow.Flow

interface FractionContractRepository {
    fun checkAgeAndHistory(): Flow<FractionEligibilityDN?>
    fun makeFractionContract(premium: String = "this.premium"): Flow<FractionContractResultDN>
}
