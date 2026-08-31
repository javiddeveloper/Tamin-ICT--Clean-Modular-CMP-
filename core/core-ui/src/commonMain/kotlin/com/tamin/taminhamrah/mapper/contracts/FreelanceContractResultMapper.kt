package com.tamin.taminhamrah.mapper.contracts

import com.tamin.taminhamrah.model.contractFlow.FreelanceContractResultPR
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.util.PersianDateFormatter

fun FreelanceContractResultDN.toPresentation(): FreelanceContractResultPR = FreelanceContractResultPR(
    contractNumber = contractNumber?.toString() ?: "",
    contractDate = PersianDateFormatter.formatTimestamp(contractDate),
)
