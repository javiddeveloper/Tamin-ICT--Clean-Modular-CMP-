package com.tamin.taminhamrah.model.contracts

import com.tamin.taminhamrah.util.CommonRequestConstants

/**
 * Shared paging rules for contracts list and branch list endpoints (1-based page, size 10).
 */
object ContractsPaging {
    const val PAGE_SIZE = CommonRequestConstants.LIMIT
}
