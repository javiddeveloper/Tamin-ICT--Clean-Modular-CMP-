package com.tamin.taminhamrah.useCases.identity

import com.tamin.taminhamrah.core.model.common.IdentityInfoDN
import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.core.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.core.model.request.FilterOperator
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

internal class IdentityInfoUseCaseImpl(
    private val userRepository: UserRepository,
    private val cityProvinceRepository: CityProvinceRepository,
) : IdentityInfoUseCase {

    override fun invoke(): Flow<IdentityInfoDN> = flow {
        userRepository.getIdentityInfo().collect { identity ->
            emit(resolveCityNames(identity))
        }
    }

    private suspend fun resolveCityNames(identity: IdentityInfoDN): IdentityInfoDN {
        val cityId = identity.cityOfBirthId ?: identity.cityOfIssueId ?: ""
        if (cityId.isEmpty()) {
            return identity.apply {
                cityOfBirthName = cityOfBirthName ?: UNKNOWN_CITY
                cityOfIssueName = cityOfIssueName ?: UNKNOWN_CITY
            }
        }

        return try {
            val city = cityProvinceRepository.getCity(
                ApiQueryParamDN(
                    filters = listOf(
                        ApiFilterDN(
                            property = "cityCode",
                            operator = FilterOperator.EQUAL,
                            value = cityId,
                        ),
                    ),
                ),
            ).firstOrNull()

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
