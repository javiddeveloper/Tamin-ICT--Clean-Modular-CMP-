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
import com.tamin.taminhamrah.model.health.RelationTypeDTO
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
import com.tamin.taminhamrah.model.health.ActFrequencyDTO
import kotlinx.coroutines.CancellationException
import io.ktor.serialization.JsonConvertException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException

internal class HealthRemoteDataSourceImpl(
    private val apiService: HealthApiService,
    private val errorParser: ErrorParser
) : HealthRemoteDataSource {

    override suspend fun getPatientGeneral(natCode: String): PatientGeneralDTO? {
        return try {
            apiService.getPatientGeneral(natCode).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getPatientSelfDeclarative(
        natCode: String,
        patientID: Int
    ): PatientSelfDeclarativeDTO? {
        return try {
            apiService.getPatientSelfDeclarative(natCode, patientID).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getPatientDrugAllergies(
        natCode: String,
        patientID: Int
    ): ListData<DrugItemAllergiesDTO>? {
        return try {
            apiService.getPatientDrugAllergies(natCode, patientID).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getPatientHospitalizations(
        natCode: String,
        patientID: Int
    ): ListData<PatientHospitalizationsDTO>? {
        return try {
            apiService.getPatientHospitalize(
                mapOf(
                    "natCode" to natCode,
                    "patientID" to patientID.toString()
                )
            ).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getPatientVisits(
        natCode: String,
        patientID: Int
    ): ListData<PatientVisitDTO>? {
        return try {
            apiService.getPatientVisit(mapOf("natCode" to natCode, "patientID" to patientID.toString()))
                .extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getPatientLabs(
        natCode: String,
        patientID: Int
    ): ListData<PatientLabDTO>? {
        return try {
            apiService.getPatientLab(mapOf("natCode" to natCode, "patientID" to patientID.toString()))
                .extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getPatientImaging(
        natCode: String,
        patientID: Int
    ): ListData<PatientImagingDTO>? {
        return try {
            apiService.getPatientImaging(
                mapOf(
                    "natCode" to natCode,
                    "patientID" to patientID.toString()
                )
            ).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    // --- Location ---

    override suspend fun getAllProvinces(): ProvincesDTO? {
        return try {
            apiService.getAllProvinces().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getProvinceCities(provinceID: Int): ProvinceCitiesDTO? {
        return try {
            apiService.getProvinceCities(provinceID).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    // --- Lookup ---

    override suspend fun getBloodGroups(): List<BloodGroupDTO>? {
        return try {
            apiService.getBloodGroups()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getMaritalStatus(): List<MaritalStatusDTO>? {
        return try {
            apiService.getMaritalStatus()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getRelationTypes(): List<RelationTypeDTO>? {
        return try {
            apiService.getRelationTypes()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }


    override suspend fun getSmokingStatus(): List<SmokingStatusDTO>? {
        return try {
            apiService.getSmokingStatus()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getActFrequencies(): List<ActFrequencyDTO>? {
        return try {
            apiService.getActFrequencies()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    // --- Illnesses ---

    override suspend fun getSelfDeclarableIllnesses(): DeclarableIllnessesDTO? {
        return try {
            apiService.getSelfDeclarableIllnesses().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getSelfDeclarableIllnessesByGroup(): SelfDeclarableIllnessesByGroupDTO? {
        return try {
            apiService.getSelfDeclarableIllnessesByGroup().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    // --- Drug master list ---

    override suspend fun getAllDrugs(): AllergicDrugsDTO? {
        return try {
            apiService.getAllergicDrugs().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    // --- Mutations (POST) ---

    override suspend fun updatePatient(request: UpdatePatientRequestDTO): ApiOutcome<UpdatePatientDTO> {
        return try {
            apiService.updatePatient(request).extractDataOrProblems()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun addSelfDeclarative(request: AddSelfDeclarativeRequestDTO): ApiOutcome<AddSelfDeclarativeDTO> {
        return try {
            apiService.addSelfDeclarative(request).extractDataOrProblems()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun updateSelfDeclarative(request: UpdateSelfDeclarativeRequestDTO): ApiOutcome<UpdateSelfDeclarativeDTO> {
        return try {
            apiService.updateSelfDeclarative(request).extractDataOrProblems()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun syncIllnessSelfDeclaratives(request: SyncIllnessesSelfDecRequestDTO): ApiOutcome<String> {
        return try {
            apiService.syncIllnessSelfDeclaratives(request).extractMessageOrProblems()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun syncDrugAllergies(request: SyncDrugAllergiesRequestDTO): ApiOutcome<String> {
        return try {
            apiService.syncDrugAllergies(request).extractMessageOrProblems()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
