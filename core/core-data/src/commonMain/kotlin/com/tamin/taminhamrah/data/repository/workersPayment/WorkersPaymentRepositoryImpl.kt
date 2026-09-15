package com.tamin.taminhamrah.data.repository.workersPayment

import com.tamin.taminhamrah.data.mapper.workersPayment.toDomain
import com.tamin.taminhamrah.data.mapper.workersPayment.toRequestDTO
import com.tamin.taminhamrah.dataSource.workersPayment.WorkersPaymentRemoteDataSource
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitParamsDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitResultDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoListDN
import com.tamin.taminhamrah.repository.workersPayment.WorkersPaymentRepository

class WorkersPaymentRepositoryImpl(
    private val remoteDataSource: WorkersPaymentRemoteDataSource,
) : WorkersPaymentRepository {

    override suspend fun getWorkersPaymentInfo(): WorkersPaymentInfoListDN =
        remoteDataSource.getWorkersPaymentInfo().toDomain()

    override suspend fun payWorkersDebit(params: WorkersPayDebitParamsDN): WorkersPayDebitResultDN =
        remoteDataSource.payWorkersDebit(params.toRequestDTO(), params.redirectUrl).toDomain()

    override suspend fun inspectTicket(ticket: String?, paymentInfo: String?): String =
        remoteDataSource.inspectTicket(ticket, paymentInfo)
}
