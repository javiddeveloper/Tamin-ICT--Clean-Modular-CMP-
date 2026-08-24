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

/**
 * [requestedCityCode] is the code the branches were asked for, and it — not the one the server
 * echoes — becomes the row's key.
 *
 * The API is inconsistent about zero-padding a city code (`CityProvinceRepositoryImpl` already
 * compares them with `trimStart('0')` for the same reason) and sometimes omits it entirely. Filing
 * a row under the echoed value meant the read-back, which queries by the requested value, found
 * nothing and the branch picker opened empty.
 */
internal fun BranchDTO.toEntity(requestedCityCode: String): BranchEntity = BranchEntity(
    code = code.orEmpty(),
    name = name.orEmpty(),
    branchAddress = branchAddress.orEmpty(),
    cityCode = requestedCityCode,
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
