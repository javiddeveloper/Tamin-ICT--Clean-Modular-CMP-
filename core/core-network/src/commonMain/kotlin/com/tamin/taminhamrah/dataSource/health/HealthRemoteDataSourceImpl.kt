package com.tamin.taminhamrah.dataSource.health

import com.tamin.taminhamrah.apiService.health.HealthApiService
import com.tamin.taminhamrah.model.health.PatientGeneralDTO
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDTO
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDTO
import com.tamin.taminhamrah.model.health.PatientHospitalizationsDTO
import com.tamin.taminhamrah.model.health.PatientVisitDTO
import com.tamin.taminhamrah.model.health.PatientLabDTO
import com.tamin.taminhamrah.model.health.PatientImagingDTO
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

    override suspend fun getPatientHospitalizations(
        natCode: String,
        patientID: Int
    ): ListData<PatientHospitalizationsDTO>? {
        return try {
            val response = apiService.getPatientHospitalize(natCode, patientID)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getPatientHospitalizations failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getPatientVisits(
        natCode: String,
        patientID: Int
    ): ListData<PatientVisitDTO>? {
        return try {
            val response = apiService.getPatientVisit(natCode, patientID)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getPatientVisits failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getPatientLabs(
        natCode: String,
        patientID: Int
    ): ListData<PatientLabDTO>? {
        return try {
            val response = apiService.getPatientLab(natCode, patientID)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getPatientLabs failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getPatientImaging(
        natCode: String,
        patientID: Int
    ): ListData<PatientImagingDTO>? {
        return try {
            val response = apiService.getPatientImaging(natCode, patientID)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getPatientImaging failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }
}
