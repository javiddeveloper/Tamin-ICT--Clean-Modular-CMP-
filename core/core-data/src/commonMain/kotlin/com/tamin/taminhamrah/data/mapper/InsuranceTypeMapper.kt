package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.model.common.InsuranceTypeDTO

internal fun InsuranceTypeDTO.toDomain(): InsuranceTypeDN = InsuranceTypeDN(
    insuranceTypeCode = insuranceTypeCode,
    insuranceTypeDesc = insuranceTypeDesc,
    status = status,
)
