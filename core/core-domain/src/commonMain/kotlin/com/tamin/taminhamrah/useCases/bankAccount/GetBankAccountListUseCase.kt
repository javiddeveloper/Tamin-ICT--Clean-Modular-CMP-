package com.tamin.taminhamrah.useCases.bankAccount

import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetBankAccountListUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<BankAccountDN>> {
        return userRepository.getBankAccountList(page, start, limit, filter, sort)
    }
}
