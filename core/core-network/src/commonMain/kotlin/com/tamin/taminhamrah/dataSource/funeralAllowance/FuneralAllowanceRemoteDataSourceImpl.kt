package com.tamin.taminhamrah.dataSource.funeralAllowance

import com.tamin.taminhamrah.apiService.funeralAllowance.FuneralAllowanceApiService
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDTO
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceRequestDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage

internal class FuneralAllowanceRemoteDataSourceImpl(
    private val apiService: FuneralAllowanceApiService,
    private val errorParser: ErrorParser,
) : FuneralAllowanceRemoteDataSource {

    override suspend fun getFuneralAllowanceInfo(): FuneralAllowanceInfoDTO {
        return try {
            apiService.getFuneralAllowanceInfo().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun validateDeceased(nationalCode: String): List<String?> {
        return try {
            apiService.validateDeceased(nationalCode).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun submitFuneralAllowanceRequest(request: FuneralAllowanceRequestDTO): String {
        return try {
            apiService.submitFuneralAllowanceRequest(request).extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun confirmAccountCorrection(requestId: String): String {
        return try {
            apiService.confirmAccountCorrection(requestId).extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
