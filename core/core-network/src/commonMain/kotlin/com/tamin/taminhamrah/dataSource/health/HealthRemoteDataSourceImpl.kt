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
import com.tamin.taminhamrah.tools.ApiOutcome
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractDataOrProblems
import com.tamin.taminhamrah.tools.extractMessageOrProblems
import com.tamin.taminhamrah.tools.safeCall
import com.tamin.taminhamrah.model.health.ActFrequencyDTO

internal class HealthRemoteDataSourceImpl(
    private val apiService: HealthApiService,
    private val errorParser: ErrorParser
) : HealthRemoteDataSource {

    override suspend fun getPatientGeneral(natCode: String): PatientGeneralDTO? =
        errorParser.safeCall("getPatientGeneral") {
            apiService.getPatientGeneral(natCode).extractData()
        }

    override suspend fun getPatientSelfDeclarative(
        natCode: String,
        patientID: Int
    ): PatientSelfDeclarativeDTO? = errorParser.safeCall("getPatientSelfDeclarative") {
        apiService.getPatientSelfDeclarative(natCode, patientID).extractData()
    }

    override suspend fun getPatientDrugAllergies(
        natCode: String,
        patientID: Int
    ): ListData<DrugItemAllergiesDTO>? = errorParser.safeCall("getPatientDrugAllergies") {
        apiService.getPatientDrugAllergies(natCode, patientID).extractData()
    }

    override suspend fun getPatientHospitalizations(
        natCode: String,
        patientID: Int
    ): ListData<PatientHospitalizationsDTO>? = errorParser.safeCall("getPatientHospitalizations") {
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
    ): ListData<PatientVisitDTO>? = errorParser.safeCall("getPatientVisits") {
        apiService.getPatientVisit(mapOf("natCode" to natCode, "patientID" to patientID.toString()))
            .extractData()
    }

    override suspend fun getPatientLabs(
        natCode: String,
        patientID: Int
    ): ListData<PatientLabDTO>? = errorParser.safeCall("getPatientLabs") {
        apiService.getPatientLab(mapOf("natCode" to natCode, "patientID" to patientID.toString()))
            .extractData()
    }

    override suspend fun getPatientImaging(
        natCode: String,
        patientID: Int
    ): ListData<PatientImagingDTO>? = errorParser.safeCall("getPatientImaging") {
        apiService.getPatientImaging(
            mapOf(
                "natCode" to natCode,
                "patientID" to patientID.toString()
            )
        ).extractData()
    }

    // --- Location ---

    override suspend fun getAllProvinces(): ProvincesDTO? = errorParser.safeCall("getAllProvinces") {
        apiService.getAllProvinces().extractData()
    }

    override suspend fun getProvinceCities(provinceID: Int): ProvinceCitiesDTO? =
        errorParser.safeCall("getProvinceCities") {
            apiService.getProvinceCities(provinceID).extractData()
        }

    // --- Lookup ---

    override suspend fun getBloodGroups(): List<BloodGroupDTO>? = errorParser.safeCall("getBloodGroups") {
        apiService.getBloodGroups()
    }

    override suspend fun getMaritalStatus(): List<MaritalStatusDTO>? =
        errorParser.safeCall("getMaritalStatus") {
            apiService.getMaritalStatus()
        }

    override suspend fun getSmokingStatus(): List<SmokingStatusDTO>? =
        errorParser.safeCall("getSmokingStatus") {
            apiService.getSmokingStatus()
        }

    override suspend fun getActFrequencies(): List<ActFrequencyDTO>? =
        errorParser.safeCall("getActFrequencies") {
            apiService.getActFrequencies()
        }

    // --- Illnesses ---

    override suspend fun getSelfDeclarableIllnesses(): DeclarableIllnessesDTO? =
        errorParser.safeCall("getSelfDeclarableIllnesses") {
            apiService.getSelfDeclarableIllnesses().extractData()
        }

    override suspend fun getSelfDeclarableIllnessesByGroup(): SelfDeclarableIllnessesByGroupDTO? =
        errorParser.safeCall("getSelfDeclarableIllnessesByGroup") {
            apiService.getSelfDeclarableIllnessesByGroup().extractData()
        }

    // --- Drug master list ---

    override suspend fun getAllDrugs(): AllergicDrugsDTO? = errorParser.safeCall("getAllDrugs") {
        apiService.getAllergicDrugs().extractData()
    }

    // --- Mutations (POST) ---

    override suspend fun updatePatient(request: UpdatePatientRequestDTO): ApiOutcome<UpdatePatientDTO> = errorParser.safeCall("updatePatient") {
        apiService.updatePatient(request).extractDataOrProblems()
    }

    override suspend fun addSelfDeclarative(request: AddSelfDeclarativeRequestDTO): ApiOutcome<AddSelfDeclarativeDTO> = errorParser.safeCall("addSelfDeclarative") {
        apiService.addSelfDeclarative(request).extractDataOrProblems()
    }

    override suspend fun updateSelfDeclarative(request: UpdateSelfDeclarativeRequestDTO): ApiOutcome<UpdateSelfDeclarativeDTO> = errorParser.safeCall("updateSelfDeclarative") {
        apiService.updateSelfDeclarative(request).extractDataOrProblems()
    }

    override suspend fun syncIllnessSelfDeclaratives(request: SyncIllnessesSelfDecRequestDTO): ApiOutcome<String> = errorParser.safeCall("syncIllnessSelfDeclaratives") {
        apiService.syncIllnessSelfDeclaratives(request).extractMessageOrProblems()
    }

    override suspend fun syncDrugAllergies(request: SyncDrugAllergiesRequestDTO): ApiOutcome<String> = errorParser.safeCall("syncDrugAllergies") {
        apiService.syncDrugAllergies(request).extractMessageOrProblems()
    }
}
