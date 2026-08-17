package com.tamin.taminhamrah.dataSource.historySource

import com.tamin.taminhamrah.apiService.HistoryApiServices
import com.tamin.taminhamrah.model.history.DastmozdInfoDTO
import com.tamin.taminhamrah.model.history.HistoryJobInfoDTO
import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.safeCall

/**
 * The three «سوابق» endpoints.
 *
 * Each goes through the shared [safeCall], the same wrapper the other data sources use: the
 * envelope's own status is already classified by `BaseDTO.extractData` through
 * `HttpStatusErrorMapper`, and anything that never reached that point is a failed call.
 */
internal class HistoryRemoteDataSourceImpl(
    private val apiServices: HistoryApiServices,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : HistoryRemoteDataSource {

    override suspend fun getTalfighInfos(query: ApiQueryParamDN): TalfighInfoDTO =
        errorParser.safeCall(TAG_TALFIGH) {
            apiServices.getTalfighInfos(queryBuilder.buildQuery(query)).extractData()
        }

    override suspend fun getDastmozdInfos(query: ApiQueryParamDN): DastmozdInfoDTO =
        errorParser.safeCall(TAG_DASTMOZD) {
            apiServices.getDastmozdInfos(queryBuilder.buildQuery(query)).extractData()
        }

    override suspend fun getHistoryJobInfos(query: ApiQueryParamDN): HistoryJobInfoDTO =
        errorParser.safeCall(TAG_JOB_INFO) {
            apiServices.getHistoryJobInfos(queryBuilder.buildQuery(query)).extractData()
        }

    private companion object {
        const val TAG_TALFIGH = "getTalfighInfos"
        const val TAG_DASTMOZD = "getDastmozdInfos"
        const val TAG_JOB_INFO = "getHistoryJobInfos"
    }
}
