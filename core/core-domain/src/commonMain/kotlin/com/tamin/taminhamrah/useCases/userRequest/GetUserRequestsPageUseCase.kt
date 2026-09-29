package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import kotlinx.coroutines.flow.Flow

/** Offline-first: cached slice, then the network page — collect the whole flow. */
class GetUserRequestsPageUseCase(
    private val repository: UserRequestRepository
) {
    operator fun invoke(
        search: UserRequestSearchParams,
        page: ApiQueryParamDN,
    ): Flow<PageDN<UserRequestDN>> = repository.getUserRequestsPage(search, page)
}
