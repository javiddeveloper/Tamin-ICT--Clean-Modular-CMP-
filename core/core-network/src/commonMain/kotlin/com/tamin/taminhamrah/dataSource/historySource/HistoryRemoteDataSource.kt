package com.tamin.taminhamrah.dataSource.historySource

import com.tamin.taminhamrah.model.history.DastmozdInfoDTO
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.UserInfoDTO
import com.tamin.taminhamrah.model.history.HistoryJobInfoDTO
import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData

interface HistoryRemoteDataSource {
    suspend fun getTalfighInfos(query: ApiQueryParamDN): TalfighInfoDTO
    suspend fun getDastmozdInfos(query: ApiQueryParamDN): DastmozdInfoDTO
    suspend fun getUserInfos(): UserInfoDTO

    /** The sign-in service's own relation codes, which is what says whether this is a مستمری‌بگیر. */
    suspend fun getLoginInfo(): ListData<String>
    suspend fun sendToInstitution(allHistorySelected: Boolean, historyAndWageSelected: Boolean, combineHistorySelected: Boolean)

    /** Sends this history to the institutions; answers with the server's own confirmation text. */
    suspend fun sendHistoryNotice(): String?

    /** One of the three «سوابق» reports as a PDF, chosen by [type]. */
    suspend fun downloadHistoryReport(type: HistoryCertificateType): PdfDownloadDTO
    suspend fun getHistoryJobInfos(query: ApiQueryParamDN): HistoryJobInfoDTO
}
