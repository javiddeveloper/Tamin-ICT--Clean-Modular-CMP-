package com.tamin.taminhamrah.data.mapper

import com.tamin.core.network.model.common.CityDto
import com.tamin.core.network.model.common.ProvinceDto
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.data.local.entity.CityEntity
import com.tamin.taminhamrah.data.local.entity.ProvinceEntity

internal fun CityDto.toEntity(): CityEntity = CityEntity(
    cityCode = cityCode,
    provinceCode = provinceCode,
    cityName = cityName,
)

internal fun ProvinceDto.toEntity(): ProvinceEntity = ProvinceEntity(
    provinceCode = provinceCode,
    provinceName = provinceName,
    status = status,
    statusStartDate = statusStartDate,
)

internal fun CityEntity.toDomain(): CityDN = CityDN(
    cityCode = cityCode,
    provinceCode = provinceCode,
    cityName = cityName,
)

internal fun ProvinceEntity.toDomain(): ProvinceDN = ProvinceDN(
    provinceCode = provinceCode,
    provinceName = provinceName,
    status = status,
    statusStartDate = statusStartDate,
)
