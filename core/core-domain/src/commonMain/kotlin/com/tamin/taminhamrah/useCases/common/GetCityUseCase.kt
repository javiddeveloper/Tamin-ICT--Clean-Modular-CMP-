package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow

/**
 * One city by the code the service files it under.
 *
 * For screens that hold a code and need the name — a saved draft carries `cityOfBirthId`, not
 * «تهران», and showing the raw code to the user is worse than showing nothing.
 */
class GetCityUseCase(private val repository: CityProvinceRepository) {
    operator fun invoke(cityId: String): Flow<CityDN> = repository.getCity(cityId)
}
