package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.repository.HistoryRepository

class GetUserRoleUseCase(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(): UserRoleDN {
        return repository.getUserRole()
    }
}
