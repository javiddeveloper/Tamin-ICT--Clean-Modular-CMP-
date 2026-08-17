package com.tamin.taminhamrah.useCases.bankAccount

import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

/**
 * Registers one account.
 *
 * Takes codes rather than a form model so the domain layer stays unaware of what the screen calls
 * its fields; the bank and the account type are the service's two-digit codes.
 */
class RegisterBankAccountUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        accountNumber: String,
        bankCode: String,
        accountTypeCode: String,
        startDateMillis: Long,
    ): Flow<String?> = userRepository.registerBankAccount(
        accountNumber = accountNumber,
        bankCode = bankCode,
        accountTypeCode = accountTypeCode,
        startDateMillis = startDateMillis,
    )
}
