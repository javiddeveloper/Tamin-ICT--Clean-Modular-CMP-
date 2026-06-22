package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.BranchDTO

internal fun BranchDTO.toDomain(): BranchDN = BranchDN(
    code = code,
    name = name.orEmpty().trim(),
    branchAddress = branchAddress.orEmpty().trim(),
    cityCode = cityCode.orEmpty(),
    minCode = minCode,
    maxCode = maxCode,
)

internal fun List<BranchDTO>.toDomain(): List<BranchDN> = map { it.toDomain() }
