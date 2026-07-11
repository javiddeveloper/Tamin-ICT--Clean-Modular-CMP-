package com.tamin.taminhamrah.dataSource.health

import com.tamin.taminhamrah.apiService.health.HealthApiService
import com.tamin.taminhamrah.model.health.PatientGeneralDTO
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDTO
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import co.touchlab.kermit.Logger

internal class HealthRemoteDataSourceImpl(
    private val apiService: HealthApiService,
    private val errorParser: ErrorParser
) : HealthRemoteDataSource {

    override suspend fun getPatientGeneral(natCode: String): PatientGeneralDTO? {
        return try {
            val response = apiService.getPatientGeneral(natCode)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getPatientGeneral failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getPatientSelfDeclarative(
        natCode: String,
        patientID: Int
    ): PatientSelfDeclarativeDTO? {
        return try {
            val response = apiService.getPatientSelfDeclarative(natCode, patientID)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getPatientSelfDeclarative failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getPatientDrugAllergies(
        natCode: String,
        patientID: Int
    ): ListData<DrugItemAllergiesDTO>? {
        return try {
            val response = apiService.getPatientDrugAllergies(natCode, patientID)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getPatientDrugAllergies failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }
}
