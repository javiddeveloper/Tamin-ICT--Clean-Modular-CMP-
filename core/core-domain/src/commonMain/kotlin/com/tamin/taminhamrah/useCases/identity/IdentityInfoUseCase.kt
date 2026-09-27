package com.tamin.taminhamrah.useCases.identity

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.query.city.CityListQuery
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class IdentityInfoUseCase(
    private val userRepository: UserRepository,
    private val cityProvinceRepository: CityProvinceRepository,
) {

    operator fun invoke(): Flow<IdentityInfoDN> = flow {
        userRepository.getIdentityInfo().collect { identity ->
            emit(resolveCityNames(identity))
        }
    }

    // Not migrated to incremental paging (Paginator) — this use case hands back a flat list, so a
    // single generously-sized page stands in for "all results", as the old fixed-limit query did.
    fun getCities(
        cityName: String? = null,
        provinceCode: String? = null,
    ): Flow<List<CityDN>> = cityProvinceRepository.getCitiesPage(
        ApiQueryParamDN(filters = CityListQuery.filters(cityName, provinceCode), limit = ALL_CITIES_PAGE_SIZE),
    ).map { it.items }

    fun getProvinces(): Flow<List<ProvinceDN>> = cityProvinceRepository.getProvincesPage(
        ApiQueryParamDN(limit = ALL_PROVINCES_PAGE_SIZE),
    ).map { it.items }

    /**
     * Names the two cities separately.
     *
     * They are usually the same place, so an equal pair is looked up once and reused — but they
     * genuinely can differ, and resolving only one of them and labeling both with it reports the
     * wrong place of issue for anyone who moved.
     */
    private suspend fun resolveCityNames(identity: IdentityInfoDN): IdentityInfoDN {
        val birthId = identity.cityOfBirthId.orEmpty()
        val issueId = identity.cityOfIssueId.orEmpty()
        val birthName = cityNameOf(birthId)
        val issueName = if (issueId == birthId) birthName else cityNameOf(issueId)

        // Left null when the lookup finds nothing: naming it is the screen's job, so the
        // wording stays in the string resources rather than hard-coded down here.
        return identity.apply {
            cityOfBirthName = birthName ?: cityOfBirthName
            cityOfIssueName = issueName ?: cityOfIssueName
        }
    }

    /** The city's name, or null when it has no code or the lookup fails. */
    private suspend fun cityNameOf(cityId: String): String? {
        if (cityId.isEmpty()) return null
        return try {
            cityProvinceRepository.getCity(cityId).firstOrNull()?.cityName
        } catch (_: Exception) {
            null
        }
    }

    private companion object {
        const val ALL_CITIES_PAGE_SIZE = 500
        const val ALL_PROVINCES_PAGE_SIZE = 100
    }
}
