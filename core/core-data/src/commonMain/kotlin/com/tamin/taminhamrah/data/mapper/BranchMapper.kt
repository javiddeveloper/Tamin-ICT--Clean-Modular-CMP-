package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.BranchEntity
import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.BranchDTO

internal fun BranchDTO.toDomain(): BranchDN = BranchDN(
    code = code,
    name = name?:"",
    branchAddress = branchAddress?:"",
    cityCode = cityCode?:"",
    minCode = minCode,
    maxCode = maxCode,
)

internal fun List<BranchDTO>.toDomain(): List<BranchDN> = map { it.toDomain() }

internal fun BranchDTO.toEntity(): BranchEntity = BranchEntity(
    code = code?:"",
    name = name?:"",
    branchAddress = branchAddress?:"",
    cityCode = cityCode?:"",
    minCode = minCode,
    maxCode = maxCode,
)

internal fun BranchEntity.toDomain(): BranchDN = BranchDN(
    code = code,
    name = name,
    branchAddress = branchAddress,
    cityCode = cityCode,
    minCode = minCode,
    maxCode = maxCode,
)
