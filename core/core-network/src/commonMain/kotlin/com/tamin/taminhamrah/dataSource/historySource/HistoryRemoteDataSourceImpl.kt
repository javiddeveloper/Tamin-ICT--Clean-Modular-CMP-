package com.tamin.taminhamrah.dataSource.historySource

import com.tamin.taminhamrah.apiService.HistoryApiServices
import com.tamin.taminhamrah.model.history.DastmozdInfoDTO
import com.tamin.taminhamrah.model.history.HistoryJobInfoDTO
import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.safeApiCall

/**
 * The three «سوابق» endpoints.
 *
 * Every call goes through [safeApiCall] rather than a private try/catch: these three answered a
 * plain-text 500 and a torn-down sibling request as "خطای اتصال، لطفا اتصال اینترنت خود را بررسی
 * کنید", which sends the user to check a connection that was never the problem.
 */
internal class HistoryRemoteDataSourceImpl(
    private val apiServices: HistoryApiServices,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : HistoryRemoteDataSource {

    override suspend fun getTalfighInfos(query: ApiQueryParamDN): TalfighInfoDTO =
        errorParser.safeApiCall(TAG_TALFIGH) {
            apiServices.getTalfighInfos(queryBuilder.buildQuery(query)).extractData()
        }

    override suspend fun getDastmozdInfos(query: ApiQueryParamDN): DastmozdInfoDTO =
        errorParser.safeApiCall(TAG_DASTMOZD) {
            apiServices.getDastmozdInfos(queryBuilder.buildQuery(query)).extractData()
        }

    override suspend fun getHistoryJobInfos(query: ApiQueryParamDN): HistoryJobInfoDTO =
        errorParser.safeApiCall(TAG_JOB_INFO) {
            apiServices.getHistoryJobInfos(queryBuilder.buildQuery(query)).extractData()
        }

    private companion object {
        const val TAG_TALFIGH = "getTalfighInfos"
        const val TAG_DASTMOZD = "getDastmozdInfos"
        const val TAG_JOB_INFO = "getHistoryJobInfos"
    }
}
