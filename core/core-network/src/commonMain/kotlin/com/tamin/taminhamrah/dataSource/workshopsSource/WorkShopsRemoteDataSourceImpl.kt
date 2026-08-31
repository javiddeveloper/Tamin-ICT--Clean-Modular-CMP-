package com.tamin.taminhamrah.dataSource.workshopsSource

import com.tamin.taminhamrah.apiService.WorkShopsApiService
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeContractDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeRequestDTO
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebitDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDTO
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDTO
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage

internal class WorkShopsRemoteDataSourceImpl(
    private val apiService: WorkShopsApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : WorkShopsRemoteDataSource {
    override suspend fun getAllEmployerAgreementByNationalId(query: ApiQueryParamDN): ListData<EmployerAgreementDTO> {
        val queries = queryBuilder.buildQuery(query)
        return try {
            val response = apiService.getAllEmployerAgreementByNationalId(queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getWorkshopPaymentSheets(query: ApiQueryParamDN): ListData<PaymentSheetDTO> {
        val queries = queryBuilder.buildQuery(query)

        return try {
            val response = apiService.getWorkshopPaymentSheets(queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getWorkshopDebit(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): ListData<WorkshopDebitDTO>? {
        val queries = queryBuilder.buildQuery(query)

        return try {
            val response = apiService.getWorkshopDebit(workshopId, branchCode, queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getWorkshopDebtInquiry(
        workshopId: String,
        branchCode: String
    ): WorkshopDebtInquiryDTO? {
        return try {
            val response = apiService.getWorkshopDebtInquiry(workshopId, branchCode)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getWorkshopObjectionableDebitList(
        workshopNumber: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): ListData<WorkShopDebtDTO>? {
        val queries = queryBuilder.buildQuery(query)
        return try {
            val response = apiService.getWorkshopObjectionableDebitList(workshopNumber, branchCode, queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getWorkshopRecentlyAddedMembers(
        query: ApiQueryParamDN
    ): ListData<WorkshopNewMemberDTO>? {
        val queries = queryBuilder.buildQuery(query)
        return try {
            val response = apiService.getWorkshopRecentlyAddedMembers(queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getWorkshopsDebtsList(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN
    ): ListData<WorkshopsDebtListModelDTO>? {
        val queries = queryBuilder.buildQuery(query)
        return try {
            val response = apiService.getWorkshopsDebtsList(workshopId, branchId, queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getWorkshopMembers(
        query: ApiQueryParamDN
    ): ListData<WorkshopMemberDTO>? {
        val queries = queryBuilder.buildQuery(query)
        return try {
            val response = apiService.getWorkshopMembers(queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getWorkshopStackHolders(
        query: ApiQueryParamDN
    ): ListData<WorkshopStackHolderDTO>? {
        val queries = queryBuilder.buildQuery(query)
        return try {
            val response = apiService.getWorkshopStackHolders(queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getLegalRepresentativeWorkshops(): ListData<LegalRepresentativeWorkshopDTO>? {
        return try {
            val response = apiService.getLegalRepresentativeWorkshops()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getLegalRepresentatives(
        workshopId: String,
        branchCode: String
    ): ListData<LegalRepresentativeDTO>? {
        return try {
            val response = apiService.getLegalRepresentatives(workshopId = workshopId, branchCode = branchCode)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getLegalRepresentativeWorkshopContracts(
        workshopId: String,
        branchCode: String
    ): ListData<LegalRepresentativeContractDTO>? {
        return try {
            val response = apiService.getLegalRepresentativeWorkshopContracts(workshopId = workshopId, branchCode = branchCode)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun requestLegalRepresentativeTicket(nationalCode: String?) {
        try {
            val response = if (nationalCode.isNullOrEmpty()) {
                apiService.requestLegalTicket()
            } else {
                apiService.requestLegalTicketWithNationalCode(nationalCode)
            }
            response.extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun verifyLegalRepresentativeTicket(ticket: String) {
        try {
            apiService.validateLegalTicket(ticket).extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun submitLegalRepresentative(ticket: String, request: LegalRepresentativeRequestDTO) {
        try {
            apiService.submitLegalRepresentative(ticket, request).extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun deleteLegalRepresentative(ticket: String, stackId: Long) {
        try {
            apiService.deleteLegalRepresentative(ticket, stackId).extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }
}
