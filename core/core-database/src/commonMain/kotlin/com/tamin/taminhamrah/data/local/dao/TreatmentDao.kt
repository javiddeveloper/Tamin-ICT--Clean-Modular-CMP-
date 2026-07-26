package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tamin.taminhamrah.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TreatmentDao {

    // --- Deserved Treatment ---
    @Insert
    suspend fun insertDeservedTreatment(items: List<DeservedTreatmentEntity>)

    @Query("DELETE FROM deserved_treatments WHERE nationalCode = :nationalCode")
    suspend fun clearDeservedTreatment(nationalCode: String)

    @Query("SELECT * FROM deserved_treatments WHERE nationalCode = :nationalCode")
    fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentEntity>>

    // --- Electronic Prescription List ---
    @Insert
    suspend fun insertElectronicPrescriptions(items: List<ElectronicPrescriptionEntity>)

    @Query("DELETE FROM electronic_prescriptions WHERE patientNationalCode = :patientNationalCode AND (:prescType = '6' OR prescType = :prescType)")
    suspend fun clearElectronicPrescriptions(patientNationalCode: String, prescType: String)

    @Query("SELECT * FROM electronic_prescriptions WHERE patientNationalCode = :patientNationalCode AND (:prescType = '6' OR prescType = :prescType)")
    fun getElectronicPrescriptions(patientNationalCode: String, prescType: String): Flow<List<ElectronicPrescriptionEntity>>

    // --- Electronic Prescription Detail ---
    @Insert
    suspend fun insertElectronicPrescriptionDetails(items: List<ElectronicPrescriptionDetailEntity>)

    @Query("DELETE FROM electronic_prescription_details WHERE noteHeadId = :noteHeadId")
    suspend fun clearElectronicPrescriptionDetails(noteHeadId: String)

    @Query("SELECT * FROM electronic_prescription_details WHERE noteHeadId = :noteHeadId")
    fun getElectronicPrescriptionDetails(noteHeadId: String): Flow<List<ElectronicPrescriptionDetailEntity>>

    // --- Electronic Prescription Price ---
    @Insert
    suspend fun insertElectronicPrescriptionPrices(items: List<ElectronicPrescriptionPriceEntity>)

    @Query("DELETE FROM electronic_prescription_prices WHERE noteHeadId = :noteHeadId")
    suspend fun clearElectronicPrescriptionPrices(noteHeadId: String)

    @Query("SELECT * FROM electronic_prescription_prices WHERE noteHeadId = :noteHeadId")
    fun getElectronicPrescriptionPrices(noteHeadId: String): Flow<List<ElectronicPrescriptionPriceEntity>>

    // --- Dependant User Under Eighteen ---
    @Insert
    suspend fun insertDependantsUnderEighteen(items: List<DependantUserUnderEighteenEntity>)

    @Query("DELETE FROM dependants_under_eighteen WHERE nationalCode = :nationalCode")
    suspend fun clearDependantsUnderEighteen(nationalCode: String)

    @Query("SELECT * FROM dependants_under_eighteen WHERE nationalCode = :nationalCode")
    fun getDependantsUnderEighteen(nationalCode: String): Flow<List<DependantUserUnderEighteenEntity>>

    // --- Treatment Costs ---
    @Insert
    suspend fun insertTreatmentCosts(items: List<TreatmentCostEntity>)

    @Query("DELETE FROM treatment_costs")
    suspend fun clearTreatmentCosts()

    @Query("SELECT * FROM treatment_costs")
    fun getTreatmentCosts(): Flow<List<TreatmentCostEntity>>

    // --- Medical Confirmations ---
    @Insert
    suspend fun insertMedicalConfirmations(items: List<MedicalAuthoritiesEntity>)

    @Query("DELETE FROM medical_confirmations")
    suspend fun clearMedicalConfirmations()

    @Query("SELECT * FROM medical_confirmations")
    fun getMedicalConfirmations(): Flow<List<MedicalAuthoritiesEntity>>
}
