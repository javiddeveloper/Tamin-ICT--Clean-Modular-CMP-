package com.tamin.taminhamrah.useCases.home

import com.tamin.taminhamrah.repository.home.HomeRepository

class SyncHomeContentUseCase(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke() = homeRepository.syncHomeContent()
}
