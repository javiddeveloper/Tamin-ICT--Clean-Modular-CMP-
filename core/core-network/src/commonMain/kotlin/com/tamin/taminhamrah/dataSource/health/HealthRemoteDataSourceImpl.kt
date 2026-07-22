package com.tamin.taminhamrah.dataSource.health

import com.tamin.taminhamrah.apiService.health.HealthApiService
import com.tamin.taminhamrah.model.health.AddSelfDeclarativeDTO
import com.tamin.taminhamrah.model.health.AddSelfDeclarativeRequestDTO
import com.tamin.taminhamrah.model.health.AllergicDrugsDTO
import com.tamin.taminhamrah.model.health.BloodGroupDTO
import com.tamin.taminhamrah.model.health.DeclarableIllnessesDTO
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDTO
import com.tamin.taminhamrah.model.health.MaritalStatusDTO
import com.tamin.taminhamrah.model.health.PatientGeneralDTO
import com.tamin.taminhamrah.model.health.PatientHospitalizationsDTO
import com.tamin.taminhamrah.model.health.PatientImagingDTO
import com.tamin.taminhamrah.model.health.PatientLabDTO
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDTO
import com.tamin.taminhamrah.model.health.PatientVisitDTO
import com.tamin.taminhamrah.model.health.ProvinceCitiesDTO
import com.tamin.taminhamrah.model.health.ProvincesDTO
import com.tamin.taminhamrah.model.health.SelfDeclarableIllnessesByGroupDTO
import com.tamin.taminhamrah.model.health.SmokingStatusDTO
import com.tamin.taminhamrah.model.health.SyncDrugAllergiesDTO
import com.tamin.taminhamrah.model.health.SyncDrugAllergiesRequestDTO
import com.tamin.taminhamrah.model.health.SyncIllnessSelfDeclarativesDTO
import com.tamin.taminhamrah.model.health.SyncIllnessesSelfDecRequestDTO
import com.tamin.taminhamrah.model.health.UpdatePatientDTO
import com.tamin.taminhamrah.model.health.UpdatePatientRequestDTO
import com.tamin.taminhamrah.model.health.UpdateSelfDeclarativeDTO
import com.tamin.taminhamrah.model.health.UpdateSelfDeclarativeRequestDTO
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
            val response = apiService.getPatientHospitalize(mapOf("natCode" to natCode, "patientID" to patientID.toString()))
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
            val response = apiService.getPatientVisit(mapOf("natCode" to natCode, "patientID" to patientID.toString()))
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
            val response = apiService.getPatientLab(mapOf("natCode" to natCode, "patientID" to patientID.toString()))
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
            val response = apiService.getPatientImaging(mapOf("natCode" to natCode, "patientID" to patientID.toString()))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getPatientImaging failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    // --- Location ---

    override suspend fun getAllProvinces(): ProvincesDTO? {
        return try {
            apiService.getAllProvinces().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getAllProvinces failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getProvinceCities(provinceID: Int): ProvinceCitiesDTO? {
        return try {
            apiService.getProvinceCities(provinceID).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getProvinceCities failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    // --- Lookup ---

    override suspend fun getBloodGroups(): List<BloodGroupDTO>? {
        return try {
            apiService.getBloodGroups().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getBloodGroups failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getMaritalStatus(): List<MaritalStatusDTO>? {
        return try {
            apiService.getMaritalStatus().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getMaritalStatus failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getSmokingStatus(): List<SmokingStatusDTO>? {
        return try {
            apiService.getSmokingStatus().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getSmokingStatus failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    // --- Illnesses ---

    override suspend fun getSelfDeclarableIllnesses(): DeclarableIllnessesDTO? {
        return try {
            apiService.getSelfDeclarableIllnesses().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getSelfDeclarableIllnesses failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getSelfDeclarableIllnessesByGroup(): SelfDeclarableIllnessesByGroupDTO? {
        return try {
            apiService.getSelfDeclarableIllnessesByGroup().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getSelfDeclarableIllnessesByGroup failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    // --- Drug master list ---

    override suspend fun getAllDrugs(): AllergicDrugsDTO? {
        return try {
            apiService.getAllergicDrugs().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "getAllDrugs failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    // --- Mutations (POST) ---

    override suspend fun updatePatient(request: UpdatePatientRequestDTO): UpdatePatientDTO? {
        return try {
            apiService.updatePatient(request).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "updatePatient failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun addSelfDeclarative(request: AddSelfDeclarativeRequestDTO): AddSelfDeclarativeDTO? {
        return try {
            apiService.addSelfDeclarative(request).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "addSelfDeclarative failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun updateSelfDeclarative(request: UpdateSelfDeclarativeRequestDTO): UpdateSelfDeclarativeDTO? {
        return try {
            apiService.updateSelfDeclarative(request).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "updateSelfDeclarative failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun syncIllnessSelfDeclaratives(request: SyncIllnessesSelfDecRequestDTO): SyncIllnessSelfDeclarativesDTO? {
        return try {
            apiService.syncIllnessSelfDeclaratives(request).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "syncIllnessSelfDeclaratives failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun syncDrugAllergies(request: SyncDrugAllergiesRequestDTO): SyncDrugAllergiesDTO? {
        return try {
            apiService.syncDrugAllergies(request).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "syncDrugAllergies failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }
}

