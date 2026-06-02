package com.tamin.taminhamrah.useCases.bankAccount

import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import kotlinx.coroutines.flow.Flow

interface GetBankAccountListUseCase {
    suspend operator fun invoke(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<BankAccountDN>>
}
