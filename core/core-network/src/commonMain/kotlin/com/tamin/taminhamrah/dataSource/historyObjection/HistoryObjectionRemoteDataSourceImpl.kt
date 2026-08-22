package com.tamin.taminhamrah.dataSource.historyObjection

import com.tamin.taminhamrah.apiService.historyObjection.HistoryObjectionApiService
import com.tamin.taminhamrah.model.historyObjection.ConfirmNotExistItemDTO
import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDTO
import com.tamin.taminhamrah.model.historyObjection.SaveNotExistRequestDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

class HistoryObjectionRemoteDataSourceImpl(
    private val historyObjectionApiService: HistoryObjectionApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : HistoryObjectionRemoteDataSource {

    override suspend fun checkStatusNotExist(): Boolean {
        return fetchData { historyObjectionApiService.checkStatusNotExist() }
    }

    override suspend fun getNotExistRequests(query: ApiQueryParamDN): ListData<NotExistRequestDTO> {
        return fetchData { historyObjectionApiService.getNotExistRequests(apiQueryBuilder.buildQuery(query)) }
    }

    override suspend fun saveNotExist(request: SaveNotExistRequestDTO): Boolean {
        return fetchData { historyObjectionApiService.saveNotExist(request) }
    }

    override suspend fun deleteNotExist(requestNumber: String, rowIndex: String): Boolean {
        return fetchData { historyObjectionApiService.deleteNotExist(requestNumber, rowIndex) }
    }

    override suspend fun confirmNotExist(description: String?): Boolean {
        return fetchData { historyObjectionApiService.confirmNotExist(listOf(ConfirmNotExistItemDTO(userDesc = description))) }
    }

    override suspend fun finalConfirmNotExist(): String {
        return fetchData { historyObjectionApiService.finalConfirmNotExist() }
    }

    private suspend fun <T> fetchData(call: suspend () -> BaseDTO<T>): T {
        return try {
            call().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
