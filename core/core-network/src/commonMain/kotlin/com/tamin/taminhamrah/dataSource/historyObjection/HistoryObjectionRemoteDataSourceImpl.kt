package com.tamin.taminhamrah.dataSource.historyObjection

import com.tamin.taminhamrah.apiService.historyObjection.HistoryObjectionApiService
import com.tamin.taminhamrah.model.historyObjection.ConfirmNotExistItemDTO
import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDTO
import com.tamin.taminhamrah.model.historyObjection.SaveNotExistRequestDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.safeCall

class HistoryObjectionRemoteDataSourceImpl(
    private val historyObjectionApiService: HistoryObjectionApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : HistoryObjectionRemoteDataSource {

    override suspend fun checkStatusNotExist(): Boolean =
        errorParser.safeCall(TAG_CHECK_STATUS) {
            historyObjectionApiService.checkStatusNotExist().extractData()
        }

    override suspend fun getNotExistRequests(query: ApiQueryParamDN): ListData<NotExistRequestDTO> =
        errorParser.safeCall(TAG_GET_REQUESTS) {
            historyObjectionApiService.getNotExistRequests(apiQueryBuilder.buildQuery(query)).extractData()
        }

    override suspend fun saveNotExist(request: SaveNotExistRequestDTO): Boolean =
        errorParser.safeCall(TAG_SAVE) {
            historyObjectionApiService.saveNotExist(request).extractData()
        }

    override suspend fun deleteNotExist(requestNumber: String, rowIndex: String): Boolean =
        errorParser.safeCall(TAG_DELETE) {
            historyObjectionApiService.deleteNotExist(requestNumber, rowIndex).extractData()
        }

    override suspend fun confirmNotExist(description: String?): Boolean =
        errorParser.safeCall(TAG_CONFIRM) {
            historyObjectionApiService.confirmNotExist(listOf(ConfirmNotExistItemDTO(userDesc = description))).extractData()
        }

    override suspend fun finalConfirmNotExist(): String =
        errorParser.safeCall(TAG_FINAL_CONFIRM) {
            historyObjectionApiService.finalConfirmNotExist().extractData()
        }

    private companion object {
        const val TAG_CHECK_STATUS = "checkStatusNotExist"
        const val TAG_GET_REQUESTS = "getNotExistRequests"
        const val TAG_SAVE = "saveNotExist"
        const val TAG_DELETE = "deleteNotExist"
        const val TAG_CONFIRM = "confirmNotExist"
        const val TAG_FINAL_CONFIRM = "finalConfirmNotExist"
    }
}
