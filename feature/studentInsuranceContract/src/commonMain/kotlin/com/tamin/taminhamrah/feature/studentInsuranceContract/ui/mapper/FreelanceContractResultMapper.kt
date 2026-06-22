package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper

import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.FreelanceContractResultPR
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.util.PersianDateFormatter

fun FreelanceContractResultDN.toPresentation(): FreelanceContractResultPR = FreelanceContractResultPR(
    contractNumber = contractNumber?.toString().orEmpty(),
    contractDate = PersianDateFormatter.formatTimestamp(contractDate),
)
