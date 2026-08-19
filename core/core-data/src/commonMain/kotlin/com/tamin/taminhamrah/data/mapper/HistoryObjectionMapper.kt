package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDN
import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDTO

fun NotExistRequestDTO.toDomain(): NotExistRequestDN = NotExistRequestDN(
    requestNumber = requestNumber,
    requestType = requestType,
    rowIndex = rowIndex,
    insuredId = insuredId,
    workshopId = workshopId,
    workshopName = workshopName,
    workDays = workDays,
    startDate = startDate,
    endDate = endDate,
    provinceCode = provinceCode,
    branchCode = branchCode,
    insuranceType = insuranceType,
    cityCode = cityCode,
    workshopManager = workshopManager,
    workshopAddress = workshopAddress,
    branchName = branchName,
    insuranceTypeDesc = insuranceTypeDesc,
    provinceName = provinceName,
    cityName = cityName,
    confirmed = confirmed ?: false,
    userDesc = userDesc,
)
