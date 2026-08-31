package com.tamin.taminhamrah.data.repository.funeralAllowance

import com.tamin.taminhamrah.data.mapper.funeralAllowance.toDomain
import com.tamin.taminhamrah.data.mapper.funeralAllowance.toRequestDTO
import com.tamin.taminhamrah.dataSource.funeralAllowance.FuneralAllowanceRemoteDataSource
import com.tamin.taminhamrah.model.funeralAllowance.DeceasedValidationDN
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDN
import com.tamin.taminhamrah.model.funeralAllowance.SubmitFuneralAllowanceParamsDN
import com.tamin.taminhamrah.repository.funeralAllowance.FuneralAllowanceRepository

class FuneralAllowanceRepositoryImpl(
    private val remoteDataSource: FuneralAllowanceRemoteDataSource,
) : FuneralAllowanceRepository {

    override suspend fun getFuneralAllowanceInfo(): FuneralAllowanceInfoDN =
        remoteDataSource.getFuneralAllowanceInfo().toDomain()

    override suspend fun validateDeceased(nationalCode: String): DeceasedValidationDN =
        DeceasedValidationDN.fromRawList(remoteDataSource.validateDeceased(nationalCode))

    override suspend fun submitFuneralAllowanceRequest(params: SubmitFuneralAllowanceParamsDN): String =
        remoteDataSource.submitFuneralAllowanceRequest(params.toRequestDTO())

    override suspend fun confirmAccountCorrection(requestId: String): String =
        remoteDataSource.confirmAccountCorrection(requestId)
}
