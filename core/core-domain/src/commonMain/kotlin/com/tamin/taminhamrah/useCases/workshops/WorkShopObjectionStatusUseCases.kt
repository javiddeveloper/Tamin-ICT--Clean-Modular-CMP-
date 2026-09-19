package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.SmsMessageDN
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionDN
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionQuery
import com.tamin.taminhamrah.repository.WorkShopsRepository

/** پیگیری وضعیت اعتراض — every objection the employer has filed, across all of their workshops. */
class GetWorkShopObjectionsUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: WorkShopObjectionQuery): PagedListDN<WorkShopObjectionDN> =
        repository.getWorkShopObjections(query)
}

/** The پیامک thread of one filed objection. */
class GetWorkShopObjectionSmsUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(seqNo: Long, page: Int = 0): PagedListDN<SmsMessageDN> =
        repository.getWorkShopObjectionSms(seqNo, page)
}
