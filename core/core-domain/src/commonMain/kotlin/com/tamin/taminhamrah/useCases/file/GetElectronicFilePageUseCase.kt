package com.tamin.taminhamrah.useCases.file

import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.UserRepository

class GetElectronicFilePageUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        page: Int,
        limit: Int,
        filters: List<ApiFilterDN> = emptyList(),
    ): List<ElectronicFileDN> = userRepository.getElectronicFilePage(page, limit, filters)
}
