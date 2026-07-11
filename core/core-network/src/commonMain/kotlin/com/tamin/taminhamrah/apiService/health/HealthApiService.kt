package com.tamin.taminhamrah.apiService.health

import com.tamin.taminhamrah.model.health.*
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface HealthApiService {

    @GET("patient/PatientGeneral/v1/GetPatient")
    suspend fun getPatientGeneral(
        @Query("natCode") natCode: String
    ): BaseDTO<PatientGeneralDTO>

    @GET("patient/PatientSelfDeclarative/v1/GetSelfDeclarative")
    suspend fun getPatientSelfDeclarative(
        @Query("natCode") natCode: String,
        @Query("patientID") patientID: Int
    ): BaseDTO<PatientSelfDeclarativeDTO>

    @GET("patient/PatientSelfDeclarative/v1/GetDrugAllergies")
    suspend fun getPatientDrugAllergies(
        @Query("natCode") natCode: String,
        @Query("patientID") patientID: Int
    ): BaseDTO<ListData<DrugItemAllergiesDTO>>

    @GET("patient/PatientSelfDeclarative/v1/GetSelfDeclarativeIllness")
    suspend fun getSelfDeclarativeIllness(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<SelfDeclarativeIllnessDTO>>

    @GET("patient/Individual/v1/GetPatientHealthData")
    suspend fun getPatientHealthData(
        @Query("natCode") natCode: String
    ): BaseDTO<PatientHealthDataDTO>

    @GET("patient/PatientDrug/v1/GetPatientDrug")
    suspend fun getPatientDrug(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<PatientDrugDTO>>

    @GET("patient/PatientDrug/v1/GetDrugDelivery")
    suspend fun getDrugDelivery(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<DrugDeliveryDTO>>

    @GET("patient/PatientCommission/v1/GetPatientCommission")
    suspend fun getPatientCommission(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<PatientCommissionDTO>>

    @GET("patient/PatientHospitalize/v1/GetPatientHospitalizations")
    suspend fun getPatientHospitalize(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<PatientHospitalizationsDTO>>

    @GET("patient/PatientVisit/v1/GetPatientVisit")
    suspend fun getPatientVisit(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<PatientVisitDTO>>

    @GET("patient/PatientLab/v1/GetPatientLab")
    suspend fun getPatientLab(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<PatientLabDTO>>

    @GET("patient/PatientLab/v1/GetLabDelivery")
    suspend fun getLabDelivery(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<LabDeliveryDTO>>

    @GET("patient/PatientImaging/v1/GetPatientImaging")
    suspend fun getPatientImaging(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<PatientImagingDTO>>

    @GET("patient/PatientImaging/v1/GetImagingDelivery")
    suspend fun getImagingDelivery(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<ImagingDeliveryDTO>>

    @GET("patient/PatientSurgery/v1/GetPatientSurgeries")
    suspend fun getPatientSurgeries(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<PatientSurgeriesDTO>>

    @GET("patient/PatientPhysio/v1/GetPatientPhysio")
    suspend fun getPatientPhysio(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<PatientPhysioDTO>>

    @GET("patient/PatientPhysio/v1/GetPhysioDelivery")
    suspend fun getPhysioDelivery(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<PhysioDeliveryDTO>>

    // --- SUBMISSION & SYNC ENDPOINTS (POST) ---

    @POST("patient/PatientGeneral/v1/UpdatePatient")
    suspend fun updatePatient(
        @Body updatePatientRequest: UpdatePatientRequestDTO
    ): BaseDTO<UpdatePatientDTO>

    @POST("patient/PatientSelfDeclarative/v1/SyncIllnessSelfDeclaratives")
    suspend fun syncIllnessSelfDeclaratives(
        @Body syncIllnessesRequest: SyncIllnessesSelfDecRequestDTO
    ): BaseDTO<SyncIllnessSelfDeclarativesDTO>

    @POST("patient/PatientSelfDeclarative/v1/SyncDrugAllergies")
    suspend fun syncDrugAllergies(
        @Body syncDrugAllergiesRequest: SyncDrugAllergiesRequestDTO
    ): BaseDTO<SyncDrugAllergiesDTO>

    @POST("patient/PatientSelfDeclarative/v1/AddSelfDeclarative")
    suspend fun addSelfDeclarative(
        @Body addSelfDeclarativeRequest: AddSelfDeclarativeRequestDTO
    ): BaseDTO<AddSelfDeclarativeDTO>

    @POST("patient/PatientSelfDeclarative/v1/UpdateSelfDeclarative")
    suspend fun updateSelfDeclarative(
        @Body updateSelfDeclarativeRequest: UpdateSelfDeclarativeRequestDTO
    ): BaseDTO<UpdateSelfDeclarativeDTO>

    // --- BASE INFO & LOOKUP ENDPOINTS ---

    @GET("UIServices/BaseInfo/V1/GetSelfDeclarableIllnesses")
    suspend fun getSelfDeclarableIllnesses(): BaseDTO<DeclarableIllnessesDTO>

    @GET("UIServices/BaseInfo/V1/GetSelfDeclarableIllnessesByGroup")
    suspend fun getSelfDeclarableIllnessesByGroup(): BaseDTO<SelfDeclarableIllnessesByGroupDTO>

    @GET("Location/GetAllProvinces")
    suspend fun getAllProvinces(): BaseDTO<ProvincesDTO>

    @GET("Location/GetProvinceCities")
    suspend fun getProvinceCities(
        @Query("provinceID") provinceID: Int
    ): BaseDTO<ProvinceCitiesDTO>

    @GET("UIServices/BaseInfo/V1/GetAllergicDrugs")
    suspend fun getAllergicDrugs(): BaseDTO<AllergicDrugsDTO>

    @GET("UIServices/Lookup/GetGenderTypes")
    suspend fun getGenderTypes(): BaseDTO<List<GenderTypeDTO>>

    @GET("UIServices/Lookup/GetMaritalStatus")
    suspend fun getMaritalStatus(): BaseDTO<List<MaritalStatusDTO>>

    @GET("UIServices/Lookup/GetRelationTypes")
    suspend fun getRelationTypes(): BaseDTO<List<RelationTypeDTO>>

    @GET("UIServices/Lookup/GetIllnessGroups")
    suspend fun getIllnessGroups(): BaseDTO<List<IllnessGroupDTO>>

    @GET("UIServices/Lookup/GetActFrequencies")
    suspend fun getActFrequencies(): BaseDTO<List<ActFrequencyDTO>>

    @GET("UIServices/Lookup/GetSmokingStatus")
    suspend fun getSmokingStatus(): BaseDTO<List<SmokingStatusDTO>>

    @GET("UIServices/Lookup/GetBloodGroups")
    suspend fun getBloodGroups(): BaseDTO<List<BloodGroupDTO>>
}
