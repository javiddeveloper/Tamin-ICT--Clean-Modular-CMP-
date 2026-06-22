package com.tamin.taminhamrah.dataSource.workshopsSource

import com.tamin.taminhamrah.apiService.WorkShopsApiService
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebitDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

internal class WorkShopsRemoteDataSourceImpl(
    private val apiService: WorkShopsApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : WorkShopsRemoteDataSource {
    override suspend fun getAllEmployerAgreementByNationalId(query: ApiQueryParamDN): ListData<EmployerAgreementDTO> {
        val queries = queryBuilder.buildQuery(query)
        return try {
            val response = apiService.getAllEmployerAgreementByNationalId(queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getWorkshopPaymentSheets(query: ApiQueryParamDN): ListData<PaymentSheetDTO> {
        val queries = queryBuilder.buildQuery(query)

        return try {
            val response = apiService.getWorkshopPaymentSheets(queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getWorkshopDebit(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): ListData<WorkshopDebitDTO>? {
        val queries = queryBuilder.buildQuery(query)

        return try {
            val response = apiService.getWorkshopDebit(workshopId, branchCode, queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
