package com.tamin.taminhamrah.dataSource.inbox

import com.tamin.taminhamrah.apiService.inbox.PersonalInboxApiService
import com.tamin.taminhamrah.model.inbox.InboxInquiryRequestDTO
import com.tamin.taminhamrah.model.inbox.InboxPermissionRequestDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxListDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

class PersonalInboxRemoteDataSourceImpl(
    private val personalInboxApiService: PersonalInboxApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : PersonalInboxRemoteDataSource {

    override suspend fun getInboxItems(query: ApiQueryParamDN): PersonalInboxListDTO {
        return try {
            val response = personalInboxApiService.getInboxItems(apiQueryBuilder.buildQuery(query))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getInboxSize(): PersonalInboxSizeDTO {
        return try {
            personalInboxApiService.getInboxSize()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getMyRequestPDF(requestId: String): PersonalInboxItemDTO {
        return try {
            val response = personalInboxApiService.getMyRequestPDF(requestId)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun deleteMyRequest(requestId: String) {
        try {
            val response = personalInboxApiService.deleteMyRequest(requestId)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun inboxInquiryLicense(
        requestId: String,
        operation: String,
        duration: String?
    ) {
        try {
            val body = InboxInquiryRequestDTO(
                operation = operation,
                permission = duration?.let {
                    InboxPermissionRequestDTO(operation = it)
                }
            )
            val response = personalInboxApiService.inboxInquiryLicense(requestId, body)
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
