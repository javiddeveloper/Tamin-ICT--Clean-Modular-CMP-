package com.tamin.taminhamrah.useCases.home

import com.tamin.taminhamrah.model.home.HomeContentDN
import com.tamin.taminhamrah.repository.home.HomeRepository
import kotlinx.coroutines.flow.Flow

class GetHomeContentUseCase(
    private val homeRepository: HomeRepository
) {
    operator fun invoke(): Flow<HomeContentDN?> = homeRepository.getHomeContent()
}
