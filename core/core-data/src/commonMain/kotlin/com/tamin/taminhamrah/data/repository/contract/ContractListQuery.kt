package com.tamin.taminhamrah.data.repository.contract

import com.tamin.taminhamrah.model.contracts.ContractPremiumTypeCode
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty

internal object ContractListQuery {
    fun build(): ApiQueryParamDN = ApiQueryParamDN(
        page = 1,
        start = 0,
        limit = 100,
        filters = listOf(
            ApiFilterDN(
                property = FilterProperty.PREMIUM_TYPE_CODE,
                operator = FilterOperator.EQ,
                value = ContractPremiumTypeCode.FREELANCE,
            ),
        ),
    )
}
