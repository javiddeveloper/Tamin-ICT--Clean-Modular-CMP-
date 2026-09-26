package com.tamin.taminhamrah.dataSource.inbox

import com.tamin.taminhamrah.tools.safeCall
import com.tamin.taminhamrah.apiService.inbox.PersonalInboxApiService
import com.tamin.taminhamrah.model.inbox.InboxInquiryRequestDTO
import com.tamin.taminhamrah.model.inbox.InboxPermissionRequestDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxListDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData

class PersonalInboxRemoteDataSourceImpl(
    private val personalInboxApiService: PersonalInboxApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : PersonalInboxRemoteDataSource {

    override suspend fun getInboxItems(query: ApiQueryParamDN): PersonalInboxListDTO {
        return errorParser.safeCall("getInboxItems") {
            val response = personalInboxApiService.getInboxItems(apiQueryBuilder.buildQuery(query))
            response.extractData()
        }
    }

    override suspend fun getInboxSize(): PersonalInboxSizeDTO {
        return errorParser.safeCall("getInboxSize") {
            personalInboxApiService.getInboxSize()
        }
    }

    override suspend fun getMyRequestPDF(requestId: String): PersonalInboxItemDTO {
        return errorParser.safeCall("getMyRequestPDF") {
            val response = personalInboxApiService.getMyRequestPDF(requestId)
            response.extractData()
        }
    }

    override suspend fun deleteMyRequest(requestId: String) {
        errorParser.safeCall("deleteMyRequest") {
            val response = personalInboxApiService.deleteMyRequest(requestId)
            response.extractData()
        }
    }

    override suspend fun inboxInquiryLicense(
        requestId: String,
        operation: String,
        duration: String?
    ) {
        errorParser.safeCall("inboxInquiryLicense") {
            val body = InboxInquiryRequestDTO(
                operation = operation,
                permission = duration?.let {
                    InboxPermissionRequestDTO(operation = it)
                }
            )
            val response = personalInboxApiService.inboxInquiryLicense(requestId, body)
            response.extractData()
        }
    }
}
