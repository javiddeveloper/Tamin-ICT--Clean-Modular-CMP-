package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tamin.taminhamrah.data.local.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for local treatment feature caching.
 *
 * All [replace] methods are annotated with [@Transaction] to execute clear-and-insert
 * operations atomically in a single SQLite transaction. This guarantees that Room [Flow]
 * observers receive only one atomic update with the final refreshed dataset, avoiding
 * intermediate empty list emissions during background network synchronization.
 */
@Dao
interface TreatmentDao {

    // =================================================================================
    // DESERVED TREATMENT
    // =================================================================================

    /** Inserts or updates deserved treatment entitlement records in local cache. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeservedTreatment(items: List<DeservedTreatmentEntity>)

    /** Clears local deserved treatment records for the specified [nationalCode]. */
    @Query("DELETE FROM deserved_treatments WHERE nationalCode = :nationalCode")
    suspend fun clearDeservedTreatment(nationalCode: String)

    /** Atomically replaces local deserved treatment cache in a single transaction. */
    @Transaction
    suspend fun replaceDeservedTreatment(nationalCode: String, items: List<DeservedTreatmentEntity>) {
        clearDeservedTreatment(nationalCode)
        insertDeservedTreatment(items)
    }

    /** Observes local deserved treatment records for the given [nationalCode]. */
    @Query("SELECT * FROM deserved_treatments WHERE nationalCode = :nationalCode")
    fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentEntity>>

    // =================================================================================
    // ELECTRONIC PRESCRIPTION LIST
    // =================================================================================

    /** Inserts or updates prescription list records in local cache. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElectronicPrescriptions(items: List<ElectronicPrescriptionEntity>)

    /** Clears cached prescriptions for the specified patient and prescription type. */
    @Query("DELETE FROM electronic_prescriptions WHERE patientNationalCode = :patientNationalCode AND (:prescType = '6' OR prescType = :prescType)")
    suspend fun clearElectronicPrescriptions(patientNationalCode: String, prescType: String)

    /** Atomically replaces cached prescriptions in a single transaction (prevents intermediate empty list emissions). */
    @Transaction
    suspend fun replaceElectronicPrescriptions(patientNationalCode: String, prescType: String, items: List<ElectronicPrescriptionEntity>) {
        clearElectronicPrescriptions(patientNationalCode, prescType)
        insertElectronicPrescriptions(items)
    }

    /** Observes cached prescriptions for the given patient and prescription type. */
    @Query("SELECT * FROM electronic_prescriptions WHERE patientNationalCode = :patientNationalCode AND (:prescType = '6' OR prescType = :prescType)")
    fun getElectronicPrescriptions(patientNationalCode: String, prescType: String): Flow<List<ElectronicPrescriptionEntity>>

    // =================================================================================
    // ELECTRONIC PRESCRIPTION DETAILS
    // =================================================================================

    /** Inserts or updates prescription item detail records in local cache. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElectronicPrescriptionDetails(items: List<ElectronicPrescriptionDetailEntity>)

    /** Clears cached prescription details for the specified [noteHeadId]. */
    @Query("DELETE FROM electronic_prescription_details WHERE noteHeadId = :noteHeadId")
    suspend fun clearElectronicPrescriptionDetails(noteHeadId: String)

    /** Atomically replaces prescription details for the given [noteHeadId] in a single transaction. */
    @Transaction
    suspend fun replaceElectronicPrescriptionDetails(noteHeadId: String, items: List<ElectronicPrescriptionDetailEntity>) {
        clearElectronicPrescriptionDetails(noteHeadId)
        insertElectronicPrescriptionDetails(items)
    }

    /** Observes cached prescription detail items for the given [noteHeadId]. */
    @Query("SELECT * FROM electronic_prescription_details WHERE noteHeadId = :noteHeadId")
    fun getElectronicPrescriptionDetails(noteHeadId: String): Flow<List<ElectronicPrescriptionDetailEntity>>

    // =================================================================================
    // ELECTRONIC PRESCRIPTION PRICES
    // =================================================================================

    /** Inserts or updates prescription cost breakdown records in local cache. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElectronicPrescriptionPrices(items: List<ElectronicPrescriptionPriceEntity>)

    /** Clears cached prescription cost breakdown records for the specified [noteHeadId]. */
    @Query("DELETE FROM electronic_prescription_prices WHERE noteHeadId = :noteHeadId")
    suspend fun clearElectronicPrescriptionPrices(noteHeadId: String)

    /** Atomically replaces cached prescription cost breakdown for [noteHeadId] in a single transaction. */
    @Transaction
    suspend fun replaceElectronicPrescriptionPrices(noteHeadId: String, items: List<ElectronicPrescriptionPriceEntity>) {
        clearElectronicPrescriptionPrices(noteHeadId)
        insertElectronicPrescriptionPrices(items)
    }

    /** Observes cached prescription cost breakdown for the given [noteHeadId]. */
    @Query("SELECT * FROM electronic_prescription_prices WHERE noteHeadId = :noteHeadId")
    fun getElectronicPrescriptionPrices(noteHeadId: String): Flow<List<ElectronicPrescriptionPriceEntity>>

    // =================================================================================
    // DEPENDANT USERS UNDER EIGHTEEN
    // =================================================================================

    /** Inserts or updates under-18 dependant records in local cache. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDependantsUnderEighteen(items: List<DependantUserUnderEighteenEntity>)

    /** Clears cached under-18 dependant records for the main user's [nationalCode]. */
    @Query("DELETE FROM dependants_under_eighteen WHERE nationalCode = :nationalCode")
    suspend fun clearDependantsUnderEighteen(nationalCode: String)

    /** Atomically replaces under-18 dependant records for [nationalCode] in a single transaction. */
    @Transaction
    suspend fun replaceDependantsUnderEighteen(nationalCode: String, items: List<DependantUserUnderEighteenEntity>) {
        clearDependantsUnderEighteen(nationalCode)
        insertDependantsUnderEighteen(items)
    }

    /** Observes cached under-18 dependant records for the given [nationalCode]. */
    @Query("SELECT * FROM dependants_under_eighteen WHERE nationalCode = :nationalCode")
    fun getDependantsUnderEighteen(nationalCode: String): Flow<List<DependantUserUnderEighteenEntity>>

    // =================================================================================
    // TREATMENT COSTS
    // =================================================================================

    /** Inserts or updates treatment cost reimbursement records in local cache. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTreatmentCosts(items: List<TreatmentCostEntity>)

    /** Clears all cached treatment cost records. */
    @Query("DELETE FROM treatment_costs")
    suspend fun clearTreatmentCosts()

    /** Atomically replaces cached treatment cost records in a single transaction. */
    @Transaction
    suspend fun replaceTreatmentCosts(items: List<TreatmentCostEntity>) {
        clearTreatmentCosts()
        insertTreatmentCosts(items)
    }

    /** Observes all cached treatment cost reimbursement records. */
    @Query("SELECT * FROM treatment_costs")
    fun getTreatmentCosts(): Flow<List<TreatmentCostEntity>>

    // =================================================================================
    // MEDICAL CONFIRMATIONS / AUTHORITIES
    // =================================================================================

    /** Inserts or updates medical confirmation records in local cache. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalConfirmations(items: List<MedicalAuthoritiesEntity>)

    /** Clears all cached medical confirmation records. */
    @Query("DELETE FROM medical_confirmations")
    suspend fun clearMedicalConfirmations()

    /** Atomically replaces cached medical confirmation records in a single transaction. */
    @Transaction
    suspend fun replaceMedicalConfirmations(items: List<MedicalAuthoritiesEntity>) {
        clearMedicalConfirmations()
        insertMedicalConfirmations(items)
    }

    /** Observes all cached medical confirmation records. */
    @Query("SELECT * FROM medical_confirmations")
    fun getMedicalConfirmations(): Flow<List<MedicalAuthoritiesEntity>>
}
