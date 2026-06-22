package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper

import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.CityOptionPR
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.contracts.BranchDN

fun ProvinceDN.toProvinceOption(): CityOptionPR = CityOptionPR(
    code = provinceCode,
    name = provinceName.orEmpty(),
)

fun List<ProvinceDN>.toProvinceOptions(): List<CityOptionPR> = map { it.toProvinceOption() }

fun BranchDN.toBranchOption(): CityOptionPR = CityOptionPR(
    code = code?:"",
    name = displayName,
)

fun List<BranchDN>.toBranchOptions(): List<CityOptionPR> = map { it.toBranchOption() }
