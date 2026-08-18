package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.repository.HistoryRepository

class GetUserInfosUseCase(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(): UserInfoDN {
        return repository.getUserInfos()
    }
}
