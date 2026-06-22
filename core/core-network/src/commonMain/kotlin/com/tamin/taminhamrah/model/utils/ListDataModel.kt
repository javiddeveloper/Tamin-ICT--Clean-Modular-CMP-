package com.tamin.taminhamrah.model.utils

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.tools.BaseDTO
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.utils.EmptyContent.status
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

@Serializable
data class ListDataModel<T>(
    val data: ListData<T>? = null
)

@Serializable
data class ListData<T>(
    val total: Int = 0,
    val list: List<T>? = null
)


