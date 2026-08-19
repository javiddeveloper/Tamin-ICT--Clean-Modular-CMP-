package com.tamin.taminhamrah.dataSource.historyObjection

import com.tamin.taminhamrah.apiService.historyObjection.HistoryObjectionApiService
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

class HistoryObjectionRemoteDataSourceImpl(
    private val historyObjectionApiService: HistoryObjectionApiService,
    private val errorParser: ErrorParser,
) : HistoryObjectionRemoteDataSource {

    override suspend fun checkStatusNotExist(): Boolean {
        return fetchData { historyObjectionApiService.checkStatusNotExist() }
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
