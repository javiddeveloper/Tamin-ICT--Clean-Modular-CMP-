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
import com.tamin.taminhamrah.model.health.SyncDrugAllergiesRequestDTO
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
import com.tamin.taminhamrah.tools.extractMessage
import co.touchlab.kermit.Logger
import com.tamin.taminhamrah.model.health.ActFrequencyDTO

internal class HealthRemoteDataSourceImpl(
    private val apiService: HealthApiService,
    private val errorParser: ErrorParser
) : HealthRemoteDataSource {

    /**
     * Runs [block], normalizing every failure through [errorParser].
     * Consolidates the try/catch boilerplate that used to be duplicated
     * identically in every method of this class.
     */
    private inline fun <T> safeCall(tag: String, block: () -> T): T {
        return try {
            block()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            Logger.e("HealthDS") { "$tag failed: ${e::class.simpleName} - ${e.message}" }
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getPatientGeneral(natCode: String): PatientGeneralDTO? =
        safeCall("getPatientGeneral") {
            apiService.getPatientGeneral(natCode).extractData()
        }

    override suspend fun getPatientSelfDeclarative(
        natCode: String,
        patientID: Int
    ): PatientSelfDeclarativeDTO? = safeCall("getPatientSelfDeclarative") {
        apiService.getPatientSelfDeclarative(natCode, patientID).extractData()
    }

    override suspend fun getPatientDrugAllergies(
        natCode: String,
        patientID: Int
    ): ListData<DrugItemAllergiesDTO>? = safeCall("getPatientDrugAllergies") {
        apiService.getPatientDrugAllergies(natCode, patientID).extractData()
    }

    override suspend fun getPatientHospitalizations(
        natCode: String,
        patientID: Int
    ): ListData<PatientHospitalizationsDTO>? = safeCall("getPatientHospitalizations") {
        apiService.getPatientHospitalize(
            mapOf(
                "natCode" to natCode,
                "patientID" to patientID.toString()
            )
        ).extractData()
    }

    override suspend fun getPatientVisits(
        natCode: String,
        patientID: Int
    ): ListData<PatientVisitDTO>? = safeCall("getPatientVisits") {
        apiService.getPatientVisit(mapOf("natCode" to natCode, "patientID" to patientID.toString()))
            .extractData()
    }

    override suspend fun getPatientLabs(
        natCode: String,
        patientID: Int
    ): ListData<PatientLabDTO>? = safeCall("getPatientLabs") {
        apiService.getPatientLab(mapOf("natCode" to natCode, "patientID" to patientID.toString()))
            .extractData()
    }

    override suspend fun getPatientImaging(
        natCode: String,
        patientID: Int
    ): ListData<PatientImagingDTO>? = safeCall("getPatientImaging") {
        apiService.getPatientImaging(
            mapOf(
                "natCode" to natCode,
                "patientID" to patientID.toString()
            )
        ).extractData()
    }

    // --- Location ---

    override suspend fun getAllProvinces(): ProvincesDTO? = safeCall("getAllProvinces") {
        apiService.getAllProvinces().extractData()
    }

    override suspend fun getProvinceCities(provinceID: Int): ProvinceCitiesDTO? =
        safeCall("getProvinceCities") {
            apiService.getProvinceCities(provinceID).extractData()
        }

    // --- Lookup ---

    override suspend fun getBloodGroups(): List<BloodGroupDTO>? = safeCall("getBloodGroups") {
        apiService.getBloodGroups()
    }

    override suspend fun getMaritalStatus(): List<MaritalStatusDTO>? =
        safeCall("getMaritalStatus") {
            apiService.getMaritalStatus()
        }

    override suspend fun getSmokingStatus(): List<SmokingStatusDTO>? =
        safeCall("getSmokingStatus") {
            apiService.getSmokingStatus()
        }

    override suspend fun getActFrequencies(): List<ActFrequencyDTO>? =
        safeCall("getActFrequencies") {
            apiService.getActFrequencies()
        }

    // --- Illnesses ---

    override suspend fun getSelfDeclarableIllnesses(): DeclarableIllnessesDTO? =
        safeCall("getSelfDeclarableIllnesses") {
            apiService.getSelfDeclarableIllnesses().extractData()
        }

    override suspend fun getSelfDeclarableIllnessesByGroup(): SelfDeclarableIllnessesByGroupDTO? =
        safeCall("getSelfDeclarableIllnessesByGroup") {
            apiService.getSelfDeclarableIllnessesByGroup().extractData()
        }

    // --- Drug master list ---

    override suspend fun getAllDrugs(): AllergicDrugsDTO? = safeCall("getAllDrugs") {
        apiService.getAllergicDrugs().extractData()
    }

    // --- Mutations (POST) ---

    override suspend fun updatePatient(request: UpdatePatientRequestDTO): UpdatePatientDTO? =
        safeCall("updatePatient") {
            apiService.updatePatient(request).extractData()
        }

    override suspend fun addSelfDeclarative(request: AddSelfDeclarativeRequestDTO): AddSelfDeclarativeDTO? =
        safeCall("addSelfDeclarative") {
            apiService.addSelfDeclarative(request).extractData()
        }

    override suspend fun updateSelfDeclarative(request: UpdateSelfDeclarativeRequestDTO): UpdateSelfDeclarativeDTO? =
        safeCall("updateSelfDeclarative") {
            apiService.updateSelfDeclarative(request).extractData()
        }

    override suspend fun syncIllnessSelfDeclaratives(request: SyncIllnessesSelfDecRequestDTO): String? =
        safeCall("syncIllnessSelfDeclaratives") {
            apiService.syncIllnessSelfDeclaratives(request).extractMessage()
        }

    override suspend fun syncDrugAllergies(request: SyncDrugAllergiesRequestDTO): String? =
        safeCall("syncDrugAllergies") {
            apiService.syncDrugAllergies(request).extractMessage()
        }
}
