package com.tamin.taminhamrah.useCases.identity

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class IdentityInfoUseCase(
    private val userRepository: UserRepository,
    private val cityProvinceRepository: CityProvinceRepository,
) {

    operator fun invoke(): Flow<IdentityInfoDN> = flow {
        userRepository.getIdentityInfo().collect { identity ->
            emit(resolveCityNames(identity))
        }
    }

    fun getCities(
        cityName: String? = null,
        provinceCode: String? = null,
    ): Flow<List<CityDN>> = cityProvinceRepository.getCities(cityName, provinceCode)

    fun getProvinces(): Flow<List<ProvinceDN>> = cityProvinceRepository.getProvinces()

    private suspend fun resolveCityNames(identity: IdentityInfoDN): IdentityInfoDN {
        val cityId = identity.cityOfBirthId ?: identity.cityOfIssueId ?: ""
        if (cityId.isEmpty()) {
            return identity.apply {
                cityOfBirthName = cityOfBirthName ?: UNKNOWN_CITY
                cityOfIssueName = cityOfIssueName ?: UNKNOWN_CITY
            }
        }

        return try {
            val city = cityProvinceRepository.getCity(cityId).firstOrNull()

            identity.apply {
                cityOfBirthName = city?.cityName ?: cityOfBirthName ?: UNKNOWN_CITY
                cityOfIssueName = city?.cityName ?: cityOfIssueName ?: UNKNOWN_CITY
            }

        } catch (_: Exception) {
            identity.apply {
                cityOfBirthName = cityOfBirthName ?: UNKNOWN_CITY
                cityOfIssueName = cityOfIssueName ?: UNKNOWN_CITY
            }
        }
    }

    private companion object {
        const val UNKNOWN_CITY = "نامشخص"
    }
}
