package com.tamin.taminhamrah.dataSource.upload

import com.tamin.taminhamrah.apiService.contract.ContractsApiService
import com.tamin.taminhamrah.model.upload.UploadImageRequestDN
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UploadImageRemoteDataSourceImpl(
    private val contractsApiService: ContractsApiService,
    private val errorParser: ErrorParser,
) : UploadImageRemoteDataSource {
    override suspend fun uploadImage(request: UploadImageRequestDN): String? {
        return try {
            val content = MultiPartFormDataContent(
                formData {
                    append(
                        key = "file",
                        value = request.bytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=\"${request.fileName}\"",
                            )
                        },
                    )
                },
            )
            contractsApiService.uploadImage(content).guid
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR),
            )
        }
    }
}
