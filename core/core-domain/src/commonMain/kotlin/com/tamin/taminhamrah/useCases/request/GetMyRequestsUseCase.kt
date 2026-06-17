package com.tamin.taminhamrah.useCases.request

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.ApiSortDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.request.RequestDN
import com.tamin.taminhamrah.model.request.SortDirection
import com.tamin.taminhamrah.repository.request.RequestRepository
import kotlinx.coroutines.flow.Flow

class GetMyRequestsUseCase(
    private val requestRepository: RequestRepository
) {
    operator fun invoke(
        query: ApiQueryParamDN = defaultQuery()
    ): Flow<List<RequestDN>> {
        return requestRepository.getMyRequests(query)
    }

    companion object {
        fun defaultQuery(): ApiQueryParamDN = ApiQueryParamDN(
            page = 0,
            start = 0,
            limit = 10,
            filters = listOf(
                ApiFilterDN(
                    property = FilterProperty.OPERATION,
                    value = "03",
                    operator = FilterOperator.EQUAL
                )
            ),
            sorts = listOf(
                ApiSortDN(
                    property = "refCode",
                    direction = SortDirection.DESC
                )
            )
        )
    }
}
