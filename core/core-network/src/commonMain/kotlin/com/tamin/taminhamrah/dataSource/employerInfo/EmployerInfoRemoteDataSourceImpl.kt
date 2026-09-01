package com.tamin.taminhamrah.dataSource.employerInfo

import com.tamin.taminhamrah.apiService.employerInfo.EmployerInfoApiService
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDTO
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDTO
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDTO
import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage

internal class EmployerInfoRemoteDataSourceImpl(
    private val apiService: EmployerInfoApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : EmployerInfoRemoteDataSource {

    override suspend fun getLegalWorkshop(legalWorkshopId: String): LegalWorkshopDTO = call {
        apiService.getLegalWorkshop(legalWorkshopId).extractData()
    }

    override suspend fun getLegalWorkshopCeo(
        nationalCode: String,
        birthDate: String,
    ): LegalWorkshopCeoDTO = call {
        apiService.getLegalWorkshopCeo(nationalCode, birthDate).extractData()
    }

    override suspend fun requestLegalTicket(filters: List<ApiFilterDN>): String = call {
        apiService.requestLegalTicket(queryBuilder.buildFilterJson(filters)).extractMessage()
    }

    override suspend fun submitLegalWorkshopInfo(body: LegalWorkshopInfoRequestDTO): String = call {
        apiService.submitLegalWorkshopInfo(body).extractMessage()
    }

    override suspend fun requestRealTicket(filters: List<ApiFilterDN>): String = call {
        apiService.requestRealTicket(queryBuilder.buildFilterJson(filters)).extractMessage()
    }

    override suspend fun submitRealWorkshopInfo(body: RealWorkshopInfoRequestDTO): String = call {
        apiService.submitRealWorkshopInfo(body).extractMessage()
    }

    /** Every call here reports failure the same way, so the try/catch lives once. */
    private inline fun <T> call(block: () -> T): T = try {
        block()
    } catch (e: TaminErrorUriException) {
        throw errorParser.parseGeneralError(e)
    } catch (_: Exception) {
        throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
    }
}
