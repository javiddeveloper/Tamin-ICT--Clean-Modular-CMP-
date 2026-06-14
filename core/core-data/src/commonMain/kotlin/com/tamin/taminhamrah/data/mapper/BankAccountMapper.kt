package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.bankAccount.AccountTypeDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountResponse
import com.tamin.taminhamrah.model.bankAccount.BankAccountType
import com.tamin.taminhamrah.model.bankAccount.BankInfo
import com.tamin.taminhamrah.model.bankAccount.BankInfoDN

fun BankAccountResponse.toDomain(): BankAccountDN {
    return BankAccountDN(
        dateOfFinish = dateOfFinish,
        creationTime = creationTime,
        lastModificationTime = lastModificationTime,
        lastModifiedBy = lastModifiedBy,
        accounType = accountType?.toDomain(),
        personal = personal,
        accountNumber = accountNumber,
        isValidAccount = isValidAccount,
        confirmed = confirmed,
        organizationId = organizationId,
        bank = bank?.toDomain(),
        createdBy = createdBy,
        dateOfStart = dateOfStart,
        id = id
    )
}

fun BankAccountType.toDomain(): AccountTypeDN = AccountTypeDN(
    accountCode = accountCode,
    accountName = accountName,
    status = status
)

fun BankInfo.toDomain(): BankInfoDN = BankInfoDN(
    statusDate = statusDate,
    bankCode = bankCode,
    bankName = bankName,
    status = status
)
