package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDN
import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDTO
import com.tamin.taminhamrah.model.historyObjection.SaveNotExistRequestDN
import com.tamin.taminhamrah.model.historyObjection.SaveNotExistRequestDTO

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

fun SaveNotExistRequestDN.toDTO(): SaveNotExistRequestDTO = SaveNotExistRequestDTO(
    branchCode = branchCode,
    branchName = branchName,
    cityCode = cityCode,
    cityName = cityName,
    endDate = endDate.toString(),
    insuranceType = insuranceType,
    provinceCode = provinceCode,
    provinceName = provinceName,
    rwshAddress = workshopAddress,
    rwshManager = workshopManager,
    rwshid = workshopId,
    rwshname = workshopName,
    startDate = startDate.toString(),
    workDays = workDays,
)
